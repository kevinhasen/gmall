package com.yee.listener;

import com.rabbitmq.client.Channel;
import com.yee.gmall.common.constant.ProductConst;
import com.yee.service.GoodsService;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * ClassName: GoodsListener
 * Description:
 * date: 2022/3/3 10:36
 * 商品数据同步消费者
 * @author Yee
 * @since JDK 1.8
 */
@Component
@Log4j2
public class GoodsListener {

    @Autowired
    private GoodsService goodsService;

    /**
     * 监听消息同步:写到es
     *
     */
    @RabbitListener(queues = "upper_queue")
    public void goodsMessage(Channel channel, Message message){
        messageManual(channel,message, ProductConst.SKU_ON_SALE);
    }


    /**
     * 监听消息同步:移除es
     *
     */
    @RabbitListener(queues = "down_queue")
    public void goodsMessageRemoveFroEs(Channel channel, Message message){
        messageManual(channel,message,ProductConst.SKU_CANCLE_SALE);
    }

    /**
     * 处理消息
     * @param channel
     * @param message
     * @param status 0是下架,1是上架
     */
    private void messageManual(Channel channel, Message message,Short status){
        //获得消息体
        byte[] body = message.getBody();
        //获得id
        long skuId = Long.parseLong( new String(body));
        //获得信息属性
        MessageProperties properties = message.getMessageProperties();
        //获得标签
        long deliveryTag = properties.getDeliveryTag();
        try {
            //判断是否上下架
            if (ProductConst.SKU_ON_SALE.equals(status)){
                //执行上架操作
                goodsService.addGoodsIntoEs(skuId);
            }else {
                //执行下架操作
                goodsService.removeGoodsFromEs(skuId);
            }
            //手动签收信息
            channel.basicAck(deliveryTag,false);
        }catch (Exception e){
            try {
                //判断是否第一次消费信息
                if (properties.getRedelivered()){
                    //第二次消费,拒绝,不放回队列
                    channel.basicReject(deliveryTag,false);
                    log.error("两次同步失败,商品id:"+skuId);
                }else {
                    //第一次消费,返回队列
                    channel.basicReject(deliveryTag,true);
                }
            } catch (Exception ex) {
                log.error("第一次同步失败,商品id:"+skuId+",错误内容:"+ex.getMessage());
            }
        }
    }
}
