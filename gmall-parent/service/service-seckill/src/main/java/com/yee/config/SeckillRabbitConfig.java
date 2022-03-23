package com.yee.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ClassName: SeckillRabbitConfig
 * Description:
 * date: 2022/3/5 19:02
 * 秒杀下单消息队列
 * @author Yee
 * @since JDK 1.8
 */
@Configuration
public class SeckillRabbitConfig {

    /**
     * 订单交换机
     * @return
     */
    @Bean("seckillOrderExchange")
    public Exchange seckillOrderExchange(){
        return ExchangeBuilder
                .directExchange("seckill_order_exchange").build();
    }

    /**
     * 订单队列
     * @return
     */
    @Bean("seckillOrderQueue")
    public Queue seckillOrderQueue(){
        return QueueBuilder.durable("seckill_order_queue").build();
    }

    /**
     * 绑定
     * @param seckillOrderExchange
     * @param seckillOrderQueue
     * @return
     */
    @Bean
    public Binding seckillOrderBinding(@Qualifier("seckillOrderExchange") Exchange seckillOrderExchange,
                                       @Qualifier("seckillOrderQueue") Queue seckillOrderQueue){
        return BindingBuilder
                .bind(seckillOrderQueue).to(seckillOrderExchange)
                .with("seckill.order.add").noargs();
    }




    /**
     * 商品正常交换机
     * @return
     */
    @Bean("seckillGoodsExchange")
    public Exchange seckillGoodsExchange(){
        return ExchangeBuilder
                .directExchange("seckill_goods_exchange").build();
    }
    /**
     * 商品死信交换机
     * @return
     */
    @Bean("seckillGoodsDeadExchange")
    public Exchange seckillGoodsDeadExchange(){
        return ExchangeBuilder
                .directExchange("seckill_goods_dead_exchange").build();
    }

    /**
     * 商品正常队列
     * @return
     */
    @Bean("seckillGoodsQueue")
    public Queue seckillGoodsQueue(){
        return QueueBuilder.durable("seckill_goods_queue").build();
    }

    /**
     * 商品死信队列
     * @return
     */
    @Bean("seckillGoodsDeadQueue")
    public Queue seckillGoodsDeadQueue(){
        return QueueBuilder
                .durable("seckill_goods_dead_queue")
                .withArgument("x-dead-letter-exchange","seckill_goods_dead_exchange")
                .withArgument("x-dead-letter-routing-key","seckill.goods.data")
                .build();
    }

    /**
     * 正常交换机绑定死信队列
     * @param seckillGoodsExchange
     * @param seckillGoodsDeadQueue
     * @return
     */
    @Bean
    public Binding seckillGoodsDeadBinding(@Qualifier("seckillGoodsExchange") Exchange seckillGoodsExchange,
                                       @Qualifier("seckillGoodsDeadQueue") Queue seckillGoodsDeadQueue){
        return BindingBuilder
                .bind(seckillGoodsDeadQueue).to(seckillGoodsExchange)
                .with("seckill.goods.dead").noargs();
    }

    /**
     * 正常交换机绑定死信队列
     * @param seckillGoodsDeadExchange
     * @param seckillGoodsQueue
     * @return
     */
    @Bean
    public Binding seckillGoodsBinding(@Qualifier("seckillGoodsDeadExchange") Exchange seckillGoodsDeadExchange,
                                       @Qualifier("seckillGoodsQueue") Queue seckillGoodsQueue){
        return BindingBuilder
                .bind(seckillGoodsQueue).to(seckillGoodsDeadExchange)
                .with("seckill.goods.data").noargs();
    }
}
