package com.yee.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ClassName: ProductRabbitConfig
 * Description:
 * date: 2022/3/3 9:59
 * es和数据库的rabbit配置
 * @author Yee
 * @since JDK 1.8
 */
@Configuration
public class ProductRabbitConfig {

    /**
     * 数据同步交换机
     * @return
     */
    @Bean("listExchange")
    public Exchange listExchange(){
        return ExchangeBuilder.directExchange("list_exchange").build();
    }

    /**
     * 上架队列
     * @return
     */
    @Bean("upperQueue")
    public Queue upperQueue(){
        return QueueBuilder.durable("upper_queue").build();
    }

    /**
     * 下架队列
     * @return
     */
    @Bean("downQueue")
    public Queue downQueue(){
        return QueueBuilder.durable("down_queue").build();
    }

    /**
     * 上架队列和交换机绑定
     * @param listExchange
     * @param upperQueue
     * @return
     */
    @Bean
    public Binding upperBinding(@Qualifier("listExchange") Exchange listExchange,
                                @Qualifier("upperQueue") Queue upperQueue){
        return BindingBuilder.bind(upperQueue)
                .to(listExchange).with("sku.upper").noargs();
    }

    /**
     * 下架队列和交换机绑定
     * @param listExchange
     * @param downQueue
     * @return
     */
    @Bean
    public Binding downBinding(@Qualifier("listExchange") Exchange listExchange,
                                @Qualifier("downQueue") Queue downQueue){
        return BindingBuilder.bind(downQueue)
                .to(listExchange).with("sku.down").noargs();
    }
}
