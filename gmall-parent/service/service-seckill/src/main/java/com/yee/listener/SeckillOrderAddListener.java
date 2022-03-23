package com.yee.listener;

import com.alibaba.fastjson.JSONObject;

import com.rabbitmq.client.Channel;
import com.yee.gmall.model.activity.SeckillGoods;
import com.yee.pojo.SeckillOrder;
import com.yee.pojo.UserRecode;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

/**
 * ClassName: SeckillOrderAddListener
 * Description:
 * date: 2022/3/5 20:46
 * 秒杀订单消费者
 * 用户下单失败,付款,取消,超时取消的时候需要删除标识位
 * user_record_count_,防止重复下单的
 * @author Yee
 * @since JDK 1.8
 */
@Component
@Log4j2
public class SeckillOrderAddListener {


    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "seckill_order_queue")
    public void seckillOrderAdd(Channel channel, Message message){
        //获得消息
        byte[] body = message.getBody();
        String s = new String(body);
        //获得排队状态
        UserRecode userRecode = JSONObject.parseObject(s, UserRecode.class);
        //获得消息属性
        MessageProperties properties = message.getMessageProperties();
        long deliveryTag = properties.getDeliveryTag();
        try {

            //秒杀下单
            realSeckillOrderAdd(userRecode);
            //确认消息
            channel.basicAck(deliveryTag,false);
        }catch (Exception e){
            e.printStackTrace();
            try {
                if (properties.getRedelivered()){
                    //下单失败
                    updateUserRecord(userRecode);
                    //消费了两次
                    channel.basicReject(deliveryTag,false);
                }else {
                    //消费了一次
                    channel.basicReject(deliveryTag,true);
                }
            }catch (Exception ex){
                ex.printStackTrace();
                log.error("秒杀下单失败:"+ex.getMessage()+"用户信息:"+s);
            }
        }
    }

    /**
     * 修改用户的秒杀排队的状态:秒杀失败
     * @param userRecode
     */
    private void updateUserRecord(UserRecode userRecode) {
        //设置状态
        userRecode.setStatus(3);
        //设置失败信息
        userRecode.setMsg("商品估清,秒杀下单失败!");
        //修改redis中的信息
        redisTemplate.opsForHash().put("user_record",
                userRecode.getUsername(), userRecode);
        //删除标识位
        redisTemplate.delete("user_record_count_"+userRecode.getUsername());
    }

    /**
     * 秒杀下单
     */
    private void realSeckillOrderAdd(UserRecode userRecode) {
        //获取用户
        String username = userRecode.getUsername();
        //获得时间段
        String time = userRecode.getTime();
        //获得商品id
        String goodsId = userRecode.getGoodsId();
        //获得购买数量
        Integer num = userRecode.getNum();
        //能到这边说明都是已经支付的订单
        //判断是否存在未支付订单,有标识位之后,无需判断
//        Object o = redisTemplate.opsForHash().get("user_seckill_order",
//                username);
//        if (o != null){
//            //设置状态
//            userRecode.setStatus(3);
//            //设置失败信息
//            userRecode.setMsg("存在未支付订单,秒杀下单失败!");
//            //修改redis中的信息
//            redisTemplate.opsForHash().put("user_record",
//                    userRecode.getUsername(), userRecode);
//            //删除标识位
//            redisTemplate.delete("user_record_count_"+userRecode.getUsername());
//            return;
//        }
        //判断活动时间是否过期
        SeckillGoods seckillGoods = (SeckillGoods)redisTemplate.opsForHash().get(time, goodsId + "");
        if (seckillGoods != null){
            //判断库存是否足够
//            Integer stockNum = seckillGoods.getStockCount() - num ;
            for (Integer i = 0; i < num; i++) {
                //买多少个就拿多少个
                //商品库存队列右边拿,不为空则下单
                Object o = redisTemplate.opsForList().rightPop("seckill_goods_stock_queue_"
                        + goodsId);
                //获取长度在比较会有时间差
//                Long size = redisTemplate.opsForList()
//                        .size("seckill_goods_stock_queue_" + goodsId);
//                if (num>size){
//                    //库存不足
//                }
                if (o == null){

                    //设置状态
                    userRecode.setStatus(3);
                    //设置失败信息
                    userRecode.setMsg("商品库存不足,秒杀失败!");
                    //修改redis中的信息
                    redisTemplate.opsForHash().put("user_record",
                            userRecode.getUsername(), userRecode);
                    //删除标识位
                    redisTemplate.delete("user_record_count_"+userRecode.getUsername());
                    //没取到元素说明卖完了
                    String[] ids = getIds(i, goodsId);
                    redisTemplate.opsForList()
                            .leftPushAll("seckill_goods_stock_queue_"
                                    +goodsId,ids);
                    return;
                }
            }

//            if (stockNum > 0){
                //生成秒杀订单
                SeckillOrder seckillOrder = new SeckillOrder();
                seckillOrder.setId(UUID.randomUUID()
                        .toString().replace("-",""));
                seckillOrder.setGoodsId(goodsId);
                seckillOrder.setNum(num);
                seckillOrder.setMoney(num * seckillGoods.getCostPrice().doubleValue()+"");
                seckillOrder.setUserId(username);
                seckillOrder.setCreateTime(new Date());
                seckillOrder.setStatus("0");
                //保存订单信息,用户付款取消超时三种情况,订单才会写入数据库
                redisTemplate.opsForHash().put("user_seckill_order",
                        username, seckillOrder);
                //修改状态为秒杀成功,等待付款
                userRecode.setStatus(2);
                userRecode.setMsg("秒杀成功,等待付款");
                //补充订单号
                userRecode.setOrderId(seckillOrder.getId());
                //补充金额
                userRecode.setMoney(seckillOrder.getMoney());
                //修改redis中的信息
                redisTemplate.opsForHash().put("user_record",
                        username, userRecode);
                //商品如果卖完了,需要估清处理,否则更新库存
                updateSeckillGoodsStockRedis(num,seckillGoods,time);
            //发送延迟消息: 防止用户一致不付钱,到了规则的时间后,将用户的定单取消掉
            rabbitTemplate.convertAndSend("seckill_order_timeout_exchange",
                    "seckill.order.dead",username,message -> {
                        //设置过期时间 , 案例是5分钟=300秒=300000毫秒
                    message.getMessageProperties().setExpiration(300000+"");
                    return message;
                    });
                return;
//            }
        }else {
            //设置状态
            userRecode.setStatus(3);
            //设置失败信息
            userRecode.setMsg("商品活动结束,秒杀失败!");
            //修改redis中的信息
            redisTemplate.opsForHash().put("user_record",
                    userRecode.getUsername(), userRecode);
            //删除标识位
            redisTemplate.delete("user_record_count_"+userRecode.getUsername());
        }
        //商品估清
//       updateUserRecord(userRecode);
    }

    /**
     * 每次下单完成,更新redis剩余库存
     * @param num  本次购买的商品数量
     * @param seckillGoods 本次购买的商品
     * @param time 商品时间段
     */
    private void updateSeckillGoodsStockRedis(Integer num,
                                              SeckillGoods seckillGoods,
                                              String time) {
        //买多少个商品就减去多少个
        Long increment = redisTemplate.opsForHash()
                .increment("seckill_goods_stock_count_" + time,
                seckillGoods.getId()+"", -num);
        //设置剩余库存
        seckillGoods.setStockCount(increment.intValue());
        //更新redis商品数据
        redisTemplate.opsForHash().put(time,seckillGoods.getId()+"",seckillGoods);
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
}
