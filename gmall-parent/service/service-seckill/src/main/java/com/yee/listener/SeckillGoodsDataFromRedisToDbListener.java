package com.yee.listener;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import com.yee.mapper.SeckillGoodsMapper;
import com.yee.pojo.UserRecode;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * ClassName: SeckillGoodsDataFromRedisToDbListener
 * Description:
 * date: 2022/3/8 15:45
 * 监听活动结束的时间段的消息,将这个时间段的商品的剩余库存同步到数据库中去
 * @author Yee
 * @since JDK 1.8
 */
@Component
@Log4j2
public class SeckillGoodsDataFromRedisToDbListener {


    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;
    /**
     * 在活动结束的时候同步redis和数据库的商品库存数据
     * @param channel
     * @param message
     */
    @RabbitListener(queues = "seckill_goods_queue")
    public void seckillGoodsAdd(Channel channel, Message message){
        //获得消息
        byte[] body = message.getBody();
        //获得过期时间段
        String time = new String(body);
        //获得消息属性
        MessageProperties properties = message.getMessageProperties();
        long deliveryTag = properties.getDeliveryTag();
        try {

            //从redis中获取这个时间段的所有的数据
            Set keys = redisTemplate.opsForHash().keys("seckill_goods_stock_count_" + time);
            if ( !keys.isEmpty() && keys.size() > 0){
                //获得商品id
                keys.stream().forEach(goodsId -> {
                    //同步商品数据: 通过商品的id获取商品的剩余库存
                    Integer stockNum =
                            (Integer)redisTemplate.opsForHash()
                                    .get("seckill_goods_stock_count_" + time,goodsId);
                    //写数据到数据库
                    seckillGoodsMapper.updateSeckillGoodsStock(Long.parseLong(goodsId.toString()),stockNum);
                });
                //同步库存之后清除这个时间段的redis中的数据
                redisTemplate.delete("seckill_goods_stock_count_"+time);
            }
            //确认消息
            channel.basicAck(deliveryTag,false);
        }catch (Exception e){
            e.printStackTrace();
            try {
                if (properties.getRedelivered()){
                    //下单失败
                    log.error("");
                    //记录同步2次失败
                    log.error("在活动结束的时候同步redis和数据库的商品库存数据,同步失败,时间段为:" + time);
                }else {
                    //消费了一次
                    channel.basicReject(deliveryTag,true);
                }
            }catch (Exception ex){
                ex.printStackTrace();
                log.error("活动结束同步热diss和数据库的商品库存数据:"+ex.getMessage());
            }
        }
    }

}
