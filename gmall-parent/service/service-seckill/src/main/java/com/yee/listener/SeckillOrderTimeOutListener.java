package com.yee.listener;

import com.rabbitmq.client.Channel;
import com.yee.mapper.SeckillGoodsMapper;
import com.yee.service.SeckillOrderService;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * ClassName: SeckillOrderTimeOutListener
 * Description:
 * date: 2022/3/8 19:39
 * 秒杀订单超时监听类
 * @author Yee
 * @since JDK 1.8
 */
@Component
@Log4j2
public class SeckillOrderTimeOutListener {


    @Autowired
    private SeckillOrderService seckillOrderService;
    /**
     *监听延迟消息,取消超时订单
     * @param channel
     * @param message
     */
    @RabbitListener(queues = "seckill_order_timeout_queue")
    public void seckillOrderTimeOutAdd(Channel channel, Message message){
        //获得消息
        byte[] body = message.getBody();
        //获得过期时间段
        String username = new String(body);
        //获得消息属性
        MessageProperties properties = message.getMessageProperties();
        long deliveryTag = properties.getDeliveryTag();
        try {
            //redis订单数据同步到数据库
            seckillOrderService.cancelSeckillOrder(username,"超时取消秒杀订单");
            //确认消息
            channel.basicAck(deliveryTag,false);
        }catch (Exception e){
            e.printStackTrace();
            try {
                if (properties.getRedelivered()){
                    //记录同步2次失败
                    log.error("秒杀订单超时取消时拒绝消息失败,用户为:"+username);
                    //拒绝消费,从队列移除消息
                    channel.basicReject(deliveryTag, false);
                }else {
                    //消费了一次
                    channel.basicReject(deliveryTag,true);
                }
            }catch (Exception ex){
                ex.printStackTrace();
                log.error("秒杀订单超时取消时拒绝消息失败:"+ex.getMessage());
            }
        }
    }

}
