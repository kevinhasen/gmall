package com.yee.service;

import com.yee.pojo.UserRecode;
import io.swagger.models.auth.In;

import java.util.Map;

/**
 * ClassName: SeckillOrderService
 * Description:
 * date: 2022/3/4 23:30
 * 秒杀商品下单接口类
 * @author Yee
 * @since JDK 1.8
 */
public interface SeckillOrderService {

    /**
     * 同步排队,异步下单
     * @param time
     * @param goodsId
     * @param num
     * @return
     */
    public UserRecode addSeckillOrder(String time, String goodsId, Integer num);

    /**
     * 查询用户排队状态
     * @return
     */
     public UserRecode getUserRecode();

    /**
     * 根据用户名取消订单
     * @param username
     */
     public void cancelSeckillOrder(String username,String meg);

    /**
     * 修改秒杀订单的支付结果
     * @param map
     * @param payway
     */
    void updateSeckillOrderPayStauts(Map<String, String> map, Integer payway);
}
