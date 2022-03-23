package com.yee.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yee.gmall.model.activity.SeckillGoods;
import com.yee.mapper.SeckillGoodsMapper;
import com.yee.util.DateUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * ClassName: SeckillGoodsFromDbToRedisTask
 * Description:
 * date: 2022/3/4 17:00
 * 秒杀商品从数据库存入缓冲redis定时任务
 * 一天24小时,分为12个时间段,2个小时一个段
 * 第一步
 * @author Yee
 * @since JDK 1.8
 */
@Component
@Log4j2
public class SeckillGoodsFromDbToRedisTask {

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    /**
     * 定时任务
     * *表示任意时间,问号表示忽略这个参数
     * 逗号表示间隔时间,减号表示区间,斜杠表示步长
     * 每隔20秒执行一次,实际开发是10-20-30分钟
     */
    @Scheduled(cron = "1/20 * * * * *")
    public void seckillGoodsFromDbToRedis(){
    //获取系统时间段
        List<Date> dateMenus = DateUtil.getDateMenus();
        //遍历查询每个时间段
        dateMenus.stream().forEach(date -> {
            //获得时间段开始
            String startTime = DateUtil.data2str(date, DateUtil.PATTERN_YYYY_MM_DDHHMM);
            //结束时间,时间段加两小时
            Date endData = DateUtil.addDateHour(date, 2);
            //获得时间段结束,
            String endTime = DateUtil.data2str(endData,
                    DateUtil.PATTERN_YYYY_MM_DDHHMM);
            //计算剩余存活时间
            long timeToLive = endData.getTime() - System.currentTimeMillis();
            //当前时间段的key
            String key = DateUtil.data2str(date, DateUtil.PATTERN_YYYYMMDDHH);
            //条件构造器
            LambdaQueryWrapper<SeckillGoods> wrapper = new LambdaQueryWrapper<>();
            //时间段匹配,大于开始时间,小于结束时间
            wrapper.ge(SeckillGoods::getStartTime,startTime);
            wrapper.le(SeckillGoods::getEndTime,endTime);
            //审核过的商品,1审核通过
            wrapper.eq(SeckillGoods::getStatus,"1");
            //库存大于0
            wrapper.gt(SeckillGoods::getStockCount,0);
            //判断是否已经存在该商品,怎么存的就怎么取
            //根据时间段的key获得每一个时间段下的商品key和value
            Set seckillInRedis = redisTemplate.opsForHash().keys(key);
            if (!seckillInRedis.isEmpty() && seckillInRedis.size() > 0 ){
                //如果已经存在,则查询未存在的商品
                    wrapper.notIn(SeckillGoods::getId,seckillInRedis);
            }
            //执行查询,由于页面只展示5个时间段,所以需要循环查询每个时间段的商品数据
            List<SeckillGoods> seckillGoods = seckillGoodsMapper.selectList(wrapper);
            //写入商品
          seckillGoods.stream().forEach(seckill -> {
              //获得商品id
              Long seckillId = seckill.getId();
              //存到redis,根据时间段去存
              //展示商品的列表和查询商品的详情
              redisTemplate.opsForHash().put(key,seckillId+"",seckill);
              //商品构建队列,队列大小为商品剩余库存
              Integer stockCount = seckill.getStockCount();
              //商品库存数,例如:库存为100个,那么数组的长度为100,每个元素的值都是商品的id
              String[] ids = getIds(stockCount, seckillId + "");
              //由左边开始添加到队列,用户从右边取的时候,不为null就说明有库存可以下单
              redisTemplate.opsForList()
                      .leftPushAll("seckill_goods_stock_queue_"
                              +seckillId,ids);
              //设置过期时间,活动一到下个时间段,当前商品队列过期
                redisTemplate.expire("seckill_goods_stock_queue_"+seckillId,
                timeToLive,TimeUnit.MILLISECONDS);
              //商品库存展示用,也用来同步数据库
              redisTemplate.opsForHash().increment("seckill_goods_stock_count_"+key,
                      seckillId+"",stockCount);
          });
          //设置商品过期时间
            setSeckillGoodsTimeOutInRedis(key,timeToLive);
        });

    }


    /**
     * 构建一个库存长度的数组
     * @param seckillId
     * @param stockCount
     * @return
     */
    private String[] getIds(Integer stockCount, String seckillId) {
        //剩余多少库存,数组就多长
        String[] ids = new String[stockCount];
        //每个数组的元素进行赋值
        for (Integer i = 0; i < stockCount; i++) {
            ids[i] = seckillId;
        }
        return ids;
    }

    /**
     * 时间段过期时间,商品从redis消失
     * @param key
     * @param timeToLive key剩余有效时间
     */
    private void setSeckillGoodsTimeOutInRedis(String key,Long timeToLive) {
        //只设置一次过期时间
        Long count = redisTemplate.opsForHash()
                .increment("seckill_goods_expire", key, 1);
        //判断是否过期
        if (count > 1){
            return;
        }
        //库存同步,发送延迟信息,一天只发12次,12个时间段
        rabbitTemplate.convertAndSend("seckill_goods_exchange",
                "seckill.goods.dead",key,message -> {
            //获得信息属性
                    MessageProperties messageProperties = message.getMessageProperties();
                    //设置过期时间,同步时间比回滚库存多存活10分钟,防止主动取消和超时取消冲突
                    messageProperties.setExpiration((timeToLive + 600000)+"");
                    return message;
                });
        //设置过期时间
        redisTemplate.expire(key,timeToLive, TimeUnit.MILLISECONDS);
    }

}
