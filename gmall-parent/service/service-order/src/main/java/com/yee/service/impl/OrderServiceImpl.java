package com.yee.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yee.CartFeign;
import com.yee.PayFeign;
import com.yee.ProductFeign;
import com.yee.gmall.common.result.Result;
import com.yee.gmall.model.cart.CartInfo;
import com.yee.gmall.model.enums.OrderStatus;
import com.yee.gmall.model.enums.ProcessStatus;
import com.yee.gmall.model.order.OrderDetail;
import com.yee.gmall.model.order.OrderInfo;
import com.yee.mapper.OrderDetailMapper;
import com.yee.mapper.OrderInfoMapper;
import com.yee.service.OrderService;
import com.yee.util.OrderThreadLocalUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ClassName: OrderServiceImpl
 * Description:
 * date: 2022/2/28 21:20
 * 微服务订单实现类
 * @author Yee
 * @since JDK 1.8
 */
@Service
@Transactional(rollbackFor = Exception.class)
@Log4j2
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderInfoMapper orderInfoMapper;
    @Autowired
    private CartFeign cartFeign;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private ProductFeign productFeign;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private PayFeign payFeign;
    /**
     * 添加订单信息
     *
     * @param orderInfo
     * @return
     */
    @Override
    public OrderInfo addOrder(OrderInfo orderInfo) {
        //参数校验
         if (orderInfo == null){
            throw new RuntimeException("参数异常");
        }
        //设置用户名
        String username = OrderThreadLocalUtil.get();
         //标识位,表示第一次提交订单
        Long increment = redisTemplate.opsForValue().increment(
                username + "_add_order_increment", 1);
        if (increment > 1){
            throw new RuntimeException("重复提交订单");
        }

        try {
            //获得购物车列表和总数量总金额
            Map<String, Object> result = cartFeign.getOrderAddInfo();
            if(result == null){
                return null;
            }
            Object totalMoney = result.get("totalMoney");

            //总金额
            orderInfo.setTotalAmount(new BigDecimal(totalMoney.toString()));
            //设置订单状态
            orderInfo.setOrderStatus(OrderStatus.UNPAID.getComment());
            //设置用户名
            orderInfo.setUserId(username);
            //设置创建时间
            orderInfo.setCreateTime(new Date());
            //设置失效时间,30分钟
            orderInfo.setExpireTime(new Date(System.currentTimeMillis()+1800000));
            //设置订单进度
            orderInfo.setProcessStatus(ProcessStatus.UNPAID.getComment());
            //保存订单信息
            int insert = orderInfoMapper.insert(orderInfo);
            if (insert <= 0 ){
                throw new RuntimeException("保存订单失败");
            }
            //获得订单号
            Long id = orderInfo.getId();
            List cartInfoList = (List)result.get("cartInfoList");
            //定义库存扣减对象
            Map<String, Object> skuDecountMap = new ConcurrentHashMap<>();
            //保存订单详情
            cartInfoList.stream().forEach(object -> {
                //手动类型转换
                String s = JSONObject.toJSONString(object);
                //反序列化
                CartInfo cartInfo = JSONObject.parseObject(s, CartInfo.class);
                //初始化订单详情的数据
                OrderDetail orderDetail = new OrderDetail();
                //设置订单号
                orderDetail.setOrderId(id);
                //设置商品的id
                orderDetail.setSkuId(cartInfo.getSkuId());
                //商品的名字
                orderDetail.setSkuName(cartInfo.getSkuName());
                //图片
                orderDetail.setImgUrl(cartInfo.getImgUrl());
                //实时价格
                orderDetail.setOrderPrice(cartInfo.getSkuPrice());
                //个数
                orderDetail.setSkuNum(cartInfo.getSkuNum());
                //保存扣减商品数据
                skuDecountMap.put(cartInfo.getSkuId()+"",cartInfo.getSkuNum());
                //保存详情的数据
                int detail = orderDetailMapper.insert(orderDetail);
                if(detail <= 0){
                    throw new RuntimeException("新增订单失败!!!");
                }
            });
            //清空购物车数据
//        cartFeign.removeCart();
            //减库存
            Boolean flage = productFeign.decountStock(skuDecountMap);
            if (!flage){
                throw new RuntimeException("新增订单失败!!!");
            }
            //发送延迟消息,过期时间30分钟,还不支付就取消订单
            rabbitTemplate.convertAndSend("order_exchange",
                    "order.dead",
                    orderInfo.getId() + "",
                    message -> {
                        //获得信息属性
                        MessageProperties messageProperties = message.getMessageProperties();
                        //设置过期时间,20秒是测试用
                        messageProperties.setExpiration("300000");
                        return message;
                    });
        } catch (RuntimeException e) {
            log.error(username+"用户提交订单失败:"+e.getMessage());
            //继续抛,不然不回滚
            throw new RuntimeException("新增订单失败!!!");
        }finally {
            //清除标记位
            redisTemplate.delete(
                    username + "_add_order_increment");
        }

        //不付钱订单取消
        return orderInfo;
    }

    /**
     * 取消订单
     *
     * @param orderId 订单号
     * @param msg     主动取消或者超时取消
     */
    @Override
    public void cancelOrder(Long orderId, String msg) {
        //查询订单的信息
        OrderInfo orderInfo = orderInfoMapper.selectById(orderId);
        //判断订单的状态是否为:未支付
        if(orderInfo != null &&
                orderInfo.getId() != null &&
                orderInfo.getOrderStatus().equals(OrderStatus.UNPAID.getComment())){
            //关闭交易,同步调用,订单创建5分钟不能关闭
            Result result = payFeign.closePay(orderId + "");
            if (!result.isOk()){
                return;
            }
            //将订单的状态修改为取消
            orderInfo.setOrderStatus(msg);
            orderInfo.setProcessStatus(msg);
            int i = orderInfoMapper.updateById(orderInfo);
            if(i <= 0){
                return;
            }
            //库存回滚
            Map<String, Object> rollbackMap = rollback(orderId);
            //
            Boolean stock = productFeign.rollbackStock(rollbackMap);
//            if (stock){
//                System.out.println("回滚成功");
//            }else {
//                System.out.println("回滚失败");
//            }
        }
    }

    /**
     * 修改订单的支付结果
     *
     * @param map
     * @param payway : 0-微信 1-支付宝
     */
    @Override
    public void updateOrderPayStauts(Map<String, String> map, Integer payway) {
        //订单支付成功,获取订单号
        String tradeNo = map.get("out_trade_no");
        //查询订单的信息
        OrderInfo orderInfo = orderInfoMapper.selectById(tradeNo);
        //订单存在且订单的状态为未支付的情况下,修改订单的状态为已支付--幂等性
        if(orderInfo != null &&
                orderInfo.getId() != null &&
                orderInfo.getOrderStatus().equals(OrderStatus.UNPAID.getComment())) {
            if(payway == 0){
                //微信
                updateFromWx(map, orderInfo);
            }else{
                //支付宝
                updateFromZfb(map, orderInfo);
            }
        }
    }

    /**
     * 支付宝结果修改
     * @param map
     * @param orderInfo
     */
    private void updateFromZfb(Map<String, String> map, OrderInfo orderInfo) {
        //获取支付的结果
        if(map.get("trade_status").equals("TRADE_SUCCESS")){
            //获取支付宝的交易号
            String transactionId = map.get("trade_no");
            //第三方交易的流水号
            orderInfo.setOutTradeNo(transactionId);
            //状态
            orderInfo.setOrderStatus(OrderStatus.PAID.getComment());
            orderInfo.setProcessStatus(ProcessStatus.PAID.getComment());
        }else{
            //修改状态
            orderInfo.setOrderStatus(OrderStatus.FAIL.getComment());
            orderInfo.setProcessStatus(OrderStatus.FAIL.getComment());
        }
        //记录第三方交易的报文
        orderInfo.setTradeBody(JSONObject.toJSONString(map));
        //修改数据
        int i = orderInfoMapper.updateById(orderInfo);
        if(i <= 0){
            throw new RuntimeException("修改订单的状态失败");
        }
    }

    /**
     * 微信修改逻辑
     * @param map
     * @param orderInfo
     */
    private void updateFromWx(Map<String, String> map, OrderInfo orderInfo) {
        //获取支付的结果
        if(map.get("result_code").equals("SUCCESS") &&
                map.get("return_code").equals("SUCCESS")){
            //微信的交易号
            String transactionId = map.get("transaction_id");
            //第三方流水号
            orderInfo.setOutTradeNo(transactionId);
            //状态
            orderInfo.setOrderStatus(OrderStatus.PAID.getComment());
            orderInfo.setProcessStatus(ProcessStatus.PAID.getComment());
        }else{
            //修改状态
            orderInfo.setOrderStatus(OrderStatus.FAIL.getComment());
            orderInfo.setProcessStatus(OrderStatus.FAIL.getComment());
        }
        //无论成功失败都要记录第三方交易的报文
        orderInfo.setTradeBody(JSONObject.toJSONString(map));
        //修改数据
        int i = orderInfoMapper.updateById(orderInfo);
        if(i <= 0){
            throw new RuntimeException("修改订单的状态失败");
        }
    }

    /**
     * 获取回滚商品的信息
     * @param orderId
     * @return
     */
    private Map<String, Object> rollback(Long orderId) {
        Map<String, Object> rollbackMap = new ConcurrentHashMap<>();
        //根据订单号查询订单详情
        List<OrderDetail> orderDetails =
                orderDetailMapper.selectList(
                        new LambdaQueryWrapper<OrderDetail>()
                                .eq(OrderDetail::getOrderId, orderId));
        //获取需要的回滚的商品的数据
        orderDetails.stream().forEach(orderDetail -> {
            //获取回滚的商品的id
            Long skuId = orderDetail.getSkuId();
            //获取回滚的数量
            Integer skuNum = orderDetail.getSkuNum();
            //保存
            rollbackMap.put(skuId + "", skuNum);
        });
        //返回
        return rollbackMap;
    }
}
