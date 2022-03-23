package com.yee.listener;

import com.alibaba.fastjson.JSONObject;
import com.yee.service.SeckillOrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;


/**
 * ClassName: SeckillOrderPayListener
 * Description:
 * date: 2022/3/8 23:07
 * 秒杀订单的支付结果的监听类
 * @author Yee
 * @since JDK 1.8
 */
@Component
@Log4j2
public class SeckillOrderPayListener {
    @Autowired
    private SeckillOrderService seckillOrderService;

    /**
     * 秒杀订单的支付结果的监听类同步结果到数据库
     * @param channel
     * @param message
     */
    @RabbitListener(queues = "wx_pay_seckill_order_queue")
    public void seckillOrderAdd(Channel channel, Message message){
        //获取消息:时间段
        byte[] body = message.getBody();
        String s = new String(body);
        //获取消息的属性
        MessageProperties messageProperties = message.getMessageProperties();
        long deliveryTag = messageProperties.getDeliveryTag();
        try {
            //获取秒杀订单的支付结果
            Map<String, String> map = JSONObject.parseObject(s, Map.class);
            //同步订单的数据
            seckillOrderService.updateSeckillOrderPayStauts(map, 0);
            //确认消息
            channel.basicAck(deliveryTag, false);
        }catch (Exception e){
            e.printStackTrace();
            try {
                //判断消息是否被消费过
                if(messageProperties.getRedelivered()){
                    //记录同步2次失败
                    log.error("在活动结束的时候同步redis和数据库的商品库存数据,同步失败,时间段为:" + s);
                    //拒绝消费,从队列移除消息
                    channel.basicReject(deliveryTag, false);
                }else{
                    //消费了一次:再试一次
                    channel.basicReject(deliveryTag, true);
                }
            }catch (Exception e1){
                e1.printStackTrace();
                log.error("在活动结束的时候同步redis和数据库的商品库存数据拒绝消息失败, 原因为: " + e1.getMessage() + ",用户信息为:" + s);
            }
        }
    }
}
