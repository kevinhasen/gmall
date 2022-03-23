package com.yee.service;

import com.yee.gmall.model.order.OrderInfo;

import java.util.Map;

/**
 * ClassName: OrderService
 * Description:
 * date: 2022/2/28 19:45
 * 订单相关接口类
 * @author Yee
 * @since JDK 1.8
 */
public interface OrderService {

    /**
     * 添加订单信息
     * @param orderInfo
     * @return
     */
    public OrderInfo addOrder(OrderInfo orderInfo);

    /**
     * 取消订单
     * @param orderId 订单号
     * @param msg 主动取消或者超时取消
     */
    public void cancelOrder(Long orderId,String msg);

    /**
     * 修改订单的支付结果
     * @param map
     * @param payway: 0-微信 1-支付宝
     */
    public void updateOrderPayStauts(Map<String, String> map, Integer payway);
}
