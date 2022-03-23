package com.yee.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.yee.gmall.model.activity.SeckillGoods;
import com.yee.mapper.SeckillOrderMapper;
import com.yee.pojo.SeckillOrder;
import com.yee.pojo.UserRecode;
import com.yee.service.SeckillOrderService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * ClassName: SeckillOrderServiceImpl
 * Description:
 * date: 2022/3/4 23:31
 * 秒杀商品下单接口实现类
 * @author Yee
 * @since JDK 1.8
 */
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private SeckillOrderMapper seckillOrderMapper;
    /**
     * 同步排队,异步下单
     * 暂时限制用户每次只能买一个
     * @param time
     * @param goodsId
     * @param num
     * @return
     */
    @Override
    public UserRecode addSeckillOrder(String time, String goodsId, Integer num) {
        //设置用户排队信息
        UserRecode userRecode = new UserRecode();
        //设置时间段
        userRecode.setTime(time);
        //设置商品id
        userRecode.setGoodsId(goodsId);
        //设置购买数量
        userRecode.setNum(num);
        //排队时间
        userRecode.setCreateTime(new Date());
        //设置用户
        String username = "yee";
        userRecode.setUsername(username);
        //防止重复排队
        Long increment = redisTemplate.opsForValue().increment(
                "user_record_count_" + username, 1);
        //大于1说明已经下过单了
        if (increment > 1){
            userRecode.setMsg("重复排队");
            userRecode.setStatus(3);
            return userRecode;
        }
        //设置状态
        userRecode.setMsg("秒杀排队中!");
        userRecode.setStatus(1);
        //排队信息存储到redis
        redisTemplate.opsForHash().put("user_record", username, userRecode);
        //发送秒杀下单信息
        rabbitTemplate.convertAndSend("seckill_order_exchange",
                "seckill.order.add",
                JSONObject.toJSONString(userRecode));
        return userRecode;
    }

    /**
     * 查询用户排队状态
     *
     * @return
     */
    @Override
    public UserRecode getUserRecode() {
        //设置用户
        String username = "yee";
        UserRecode userRecode = (UserRecode)redisTemplate
                .opsForHash().get("user_record", username);
        return userRecode;
    }

    /**
     * 根据用户名取消订单
     *
     * @param username
     */
    @Override
    public void cancelSeckillOrder(String username,String msg) {
        //从redis获取用户订单
        SeckillOrder seckillOrder = (SeckillOrder)redisTemplate.opsForHash()
                .get("user_seckill_order", username);
        //判断订单的前置状态是否为--未支付
        if (seckillOrder != null && seckillOrder.getStatus().equals("0")){
            //将订单的状态修改为--主动取消/超时取消
            seckillOrder.setStatus(msg);
            //将订单的数据写入数据库;
            seckillOrderMapper.insert(seckillOrder);
            //获取用户排队记录
            UserRecode userRecode = (UserRecode)redisTemplate
                    .opsForHash().get("user_record",username);
            //获取用户的买的商品的id
            String goodsId = userRecode.getGoodsId();
            //用户购买的商品的所属的时间戳
            String time = userRecode.getTime();
            //获取用户购买的商品的数量
            Integer num = userRecode.getNum();
            //获取redis中商品的数据
            SeckillGoods seckillGoods =
                    (SeckillGoods)redisTemplate.opsForHash().get(time,goodsId);
            //回滚商品的库存自增值
            Long increment =  redisTemplate.opsForHash()
                    .increment("seckill_goods_stock_count_"+time,
                    goodsId,num);
            //说明活动还没结束
            if (seckillGoods != null){
                //商品活动没有结束,回滚商品下单队列
                String[] ids = getIds(num, goodsId);
                redisTemplate.opsForList()
                        .leftPushAll("seckill_goods_stock_queue_"+goodsId,
                                ids);
                //回滚商品的数据
                seckillGoods.setStockCount(increment.intValue());
                redisTemplate.opsForHash().put(time, goodsId, seckillGoods);
            }
            //清理标识位
            redisTemplate.opsForHash().delete("user_record",username);
            //清理排队计数器
            redisTemplate.delete("user_record_count_"+username);
            //清理秒杀订单数据
            redisTemplate.opsForHash().delete("user_seckill_order",username);
        }

    }

    /**
     * 修改秒杀订单的支付结果
     *
     * @param map
     * @param payway
     */
    @Override
    public void updateSeckillOrderPayStauts(Map<String, String> map, Integer payway) {
        //订单支付成功,获取订单号
        String tradeNo = map.get("out_trade_no");
        //获取附加参数
        String attachString = map.get("attach");
        Map<String,String> attachMap =
                JSONObject.parseObject(attachString, Map.class);
        String username = attachMap.get("username");
        //查询订单的信息--redis
        SeckillOrder seckillOrder =
                (SeckillOrder) redisTemplate
                        .opsForHash().get("user_seckill_order", username);
        //订单存在且订单的状态为未支付的情况下,修改订单的状态为已支付
        if(seckillOrder != null &&
                seckillOrder.getStatus().equals("0")) {
            if(payway == 0){
                //微信
                updateFromWx(map, seckillOrder);
            }else{
                //支付宝
                updateFromZfb(map, seckillOrder);
            }
        }
        //清理标识位
        redisTemplate.opsForHash().delete("user_record", username);
        //清理排队计数器
        redisTemplate.delete("user_record_count_" + username);
        //清理秒杀订单数据
        redisTemplate.opsForHash().delete("user_seckill_order", username);
    }



    /**
     * 微信修改逻辑
     * @param map
     * @param seckillOrder
     */
    private void updateFromWx(Map<String, String> map, SeckillOrder seckillOrder){
        //获取支付的结果
        if(map.get("result_code").equals("SUCCESS") &&
                map.get("return_code").equals("SUCCESS")){
            //获取微信的交易号
            String transactionId = map.get("transaction_id");
            //第三方交易的流水号
            seckillOrder.setOutTradeNo(transactionId);
            //状态
            seckillOrder.setStatus("支付成功");
        }else{
            //修改状态
            seckillOrder.setStatus("支付失败");
        }
        //修改数据
        int i = seckillOrderMapper.insert(seckillOrder);
        if(i <= 0){
            throw new RuntimeException("同步秒杀订单的状态失败");
        }
    }

    /**
     * 支付宝结果修改
     * @param map
     * @param seckillOrder
     */
    private void updateFromZfb(Map<String, String> map, SeckillOrder seckillOrder){
        //获取支付的结果
        if(map.get("trade_status").equals("TRADE_SUCCESS")){
            //获取支付宝的交易号
            String transactionId = map.get("trade_no");
            //第三方交易的流水号
            seckillOrder.setOutTradeNo(transactionId);
            //状态
            seckillOrder.setStatus("支付成功");
        }else{
            //修改状态
            seckillOrder.setStatus("支付失败");
        }
        //修改数据
        int i = seckillOrderMapper.insert(seckillOrder);
        if(i <= 0){
            throw new RuntimeException("同步秒杀订单的状态失败");
        }
    }

    /**
     * 构建一个库存长度的数组
     * @param seckillId
     * @param stockCount
     * @return
     */
    private String[] getIds(Integer stockCount, String seckillId) {
        //剩余多少库存,数组就多长
        String[] ids = new String[stockCount];
        //每个数组的元素进行赋值
        for (Integer i = 0; i < stockCount; i++) {
            ids[i] = seckillId;
        }
        return ids;
    }
}
