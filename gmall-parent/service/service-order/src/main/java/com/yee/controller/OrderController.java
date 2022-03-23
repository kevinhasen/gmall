package com.yee.controller;

import com.yee.gmall.common.result.Result;
import com.yee.gmall.model.enums.OrderStatus;
import com.yee.gmall.model.order.OrderInfo;
import com.yee.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * ClassName: OrderController
 * Description:
 * date: 2022/2/28 21:18
 * 订单微服务
 * @author Yee
 * @since JDK 1.8
 */
@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 添加订单
     * @param orderInfo
     * @return
     */
    @PostMapping("/addOrder")
    public Result addOrder(@RequestBody OrderInfo orderInfo){
        orderService.addOrder(orderInfo);
        return Result.ok();
    }

    /**
     * 主动取消订单
     * @param orderId
     * @return
     */
    @GetMapping(value = "/cancelOrder/{orderId}")
    public Result cancelOrder(@PathVariable(value = "orderId") Long orderId){
        //使用redis'进行自增
        Long increment = redisTemplate.opsForValue().increment(orderId + "_cancel_order_increment", 1);
        if(increment > 1){
            return Result.fail("正在取消这个订单,请不要重复取消!!");
        }
        //主动取消订单
        orderService.cancelOrder(orderId, OrderStatus.CANCEL.getComment());
        //删除标识位--可以不删除
        redisTemplate.delete(orderId + "_cancel_order_increment");
        //返回结果
        return Result.ok();
    }
}
