package com.yee.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 订单延迟消息的配置类
 */
@Configuration
public class OrderDelayRabbitConfig {
    /**
     * 流程: 消息--->正常交换机--->死信队列--->死信交换机--->正常队列
     */

    /**
     * 创建正常的交换机
     */
    @Bean("orderExchange")
    public Exchange orderExchange(){
        return ExchangeBuilder.directExchange("order_exchange").build();
    }


    /**
     * 创建死信队列
     */
    @Bean("orderDeadQueue")
    public Queue orderDeadQueue(){
        return QueueBuilder
                .durable("order_dead_queue")
                .withArgument("x-dead-letter-exchange", "order_dead_exchange")
                .withArgument("x-dead-letter-routing-key", "order.timeout")
                .build();
    }

    /**
     * 正常交换机和死信队列绑定
     * @param orderExchange
     * @param orderDeadQueue
     * @return
     */
    @Bean
    public Binding OrderDeadBinding(@Qualifier("orderExchange") Exchange orderExchange,
                                    @Qualifier("orderDeadQueue") Queue orderDeadQueue){
        return BindingBuilder.bind(orderDeadQueue).to(orderExchange).with("order.dead").noargs();
    }

    /**
     * 创建死信交换机
     */
    @Bean("orderDeadExchange")
    public Exchange orderDeadExchange(){
        return ExchangeBuilder.directExchange("order_dead_exchange").build();
    }

    /**
     * 创建正常队列
     */
    @Bean("orderQueue")
    public Queue orderQueue(){
        return QueueBuilder.durable("order_queue").build();
    }

    /**
     * 死信交换机和正常队列绑定
     * @param orderDeadExchange
     * @param orderQueue
     * @return
     */
    @Bean
    public Binding OrderBinding(@Qualifier("orderDeadExchange") Exchange orderDeadExchange,
                                @Qualifier("orderQueue") Queue orderQueue){
        return BindingBuilder.bind(orderQueue).to(orderDeadExchange).with("order.timeout").noargs();
    }
}
