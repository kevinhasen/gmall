package com.yee.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ClassName: SeckillOrderRabbitConfig
 * Description:
 * date: 2022/3/8 16:51
 * 秒杀订单的延迟信息发送配置
 * @author Yee
 * @since JDK 1.8
 */
@Configuration
public class SeckillOrderRabbitConfig {

    /**
     * 创建正常的秒杀订单交换机
     */
    @Bean("seckillOrderTimeOutExchange")
    public Exchange seckillOrderTimeOutExchange(){
        return ExchangeBuilder
                .directExchange("seckill_order_timeout_exchange").build();
    }
    /**
     * 商品死信交换机
     * @return
     */
    @Bean("seckillOrderTimeOutDeadExchange")
    public Exchange seckillOrderTimeOutDeadExchange(){
        return ExchangeBuilder
                .directExchange("seckill_order_timeout_dead_exchange").build();
    }

    /**
     * 商品正常队列
     * @return
     */
    @Bean("seckillOrderTimeOutQueue")
    public Queue seckillOrderTimeOutQueue(){
        return QueueBuilder.durable("seckill_order_timeout_queue").build();
    }

    /**
     * 商品死信队列
     * @return
     */
    @Bean("seckillOrderDeadQueue")
    public Queue seckillOrderDeadQueue(){
        return QueueBuilder
                .durable("seckill_order_dead_queue")
                .withArgument("x-dead-letter-exchange","seckill_order_timeout_dead_exchange")
                .withArgument("x-dead-letter-routing-key","seckill.order.cancle")
                .build();
    }

    /**
     * 正常交换机绑定死信队列
     * @param seckillOrderTimeOutExchange
     * @param seckillOrderDeadQueue
     * @return
     */
    @Bean
    public Binding seckillOrderDeadBinding(@Qualifier("seckillOrderTimeOutExchange") Exchange seckillOrderTimeOutExchange,
                                           @Qualifier("seckillOrderDeadQueue") Queue seckillOrderDeadQueue){
        return BindingBuilder
                .bind(seckillOrderDeadQueue).to(seckillOrderTimeOutExchange)
                .with("seckill.order.dead").noargs();
    }

    /**
     * 死信交换机绑定正常队列
     * @param seckillOrderTimeOutDeadExchange
     * @param seckillOrderTimeOutQueue
     * @return
     */
    @Bean
    public Binding seckillOrderNomalBinding(@Qualifier("seckillOrderTimeOutDeadExchange") Exchange seckillOrderTimeOutDeadExchange,
                                       @Qualifier("seckillOrderTimeOutQueue") Queue seckillOrderTimeOutQueue){
        return BindingBuilder
                .bind(seckillOrderTimeOutQueue).to(seckillOrderTimeOutDeadExchange)
                .with("seckill.order.cancle").noargs();
    }
}
