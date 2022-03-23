package com.yee.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ClassName: PayRabbitConfig
 * Description:
 * date: 2022/3/3 19:58
 * 支付相关消息通知
 * @author Yee
 * @since JDK 1.8
 */
@Configuration
public class PayRabbitConfig {


    /**
     * 创建交换机
     */
    @Bean("OrderPayExchange")
    public Exchange OrderPayExchange(){
        return ExchangeBuilder.directExchange("order_pay_exchange").build();
    }

    /**
     * 创建队列:微信普通订单
     */
    @Bean("wxPayOrderQueue")
    public Queue wxPayOrderQueue(){
        return QueueBuilder.durable("wx_pay_order_queue").build();
    }

    /**
     * 创建队列:支付宝普通订单
     */
    @Bean("zfbPayOrderQueue")
    public Queue zfbPayOrderQueue(){
        return QueueBuilder.durable("zfb_pay_order_queue").build();
    }

    /**
     * 创建队列:微信秒杀订单
     */
    @Bean("wxPaySeckillOrderQueue")
    public Queue wxPaySeckillOrderQueue(){
        return QueueBuilder.durable("wx_pay_seckill_order_queue").build();
    }

    /**
     * 创建队列:支付宝秒杀订单
     */
    @Bean("zfbPaySeckillOrderQueue")
    public Queue zfbPaySeckillOrderQueue(){
        return QueueBuilder.durable("zfb_pay_seckill_order_queue").build();

    }

    /**
     * 创建绑定:微信绑定--普通订单
     */
    @Bean
    public Binding wxPayOrderBinding(@Qualifier("OrderPayExchange") Exchange OrderPayExchange,
                                     @Qualifier("wxPayOrderQueue") Queue wxPayOrderQueue){
        return BindingBuilder.bind(wxPayOrderQueue).to(OrderPayExchange).with("pay.order.wx").noargs();
    }

    /**
     * 创建绑定:微信绑定--秒杀订单
     */
    @Bean
    public Binding wxPaySeckillOrderBinding(@Qualifier("OrderPayExchange") Exchange OrderPayExchange,
                                     @Qualifier("wxPaySeckillOrderQueue") Queue wxPaySeckillOrderQueue){
        return BindingBuilder.bind(wxPaySeckillOrderQueue).to(OrderPayExchange).with("pay.seckill.order.wx").noargs();
    }

    /**
     * 创建绑定:支付宝绑定--普通订单
     */
    @Bean
    public Binding zfbPayOrderBinding(@Qualifier("OrderPayExchange") Exchange OrderPayExchange,
                                      @Qualifier("zfbPayOrderQueue") Queue zfbPayOrderQueue){
        return BindingBuilder.bind(zfbPayOrderQueue).to(OrderPayExchange).with("pay.order.zfb").noargs();
    }


    /**
     * 创建绑定:支付宝绑定--秒杀订单
     */
    @Bean
    public Binding zfbPaySeckillOrderBinding(@Qualifier("OrderPayExchange") Exchange OrderPayExchange,
                                             @Qualifier("zfbPaySeckillOrderQueue") Queue zfbPaySeckillOrderQueue){
        return BindingBuilder.bind(zfbPaySeckillOrderQueue).to(OrderPayExchange).with("pay.seckill.order.zfb").noargs();
    }
}
