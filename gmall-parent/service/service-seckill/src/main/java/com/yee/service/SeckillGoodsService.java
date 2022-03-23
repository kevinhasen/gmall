package com.yee.service;

import com.yee.gmall.model.activity.SeckillGoods;

import java.util.List;

/**
 * ClassName: SeckillGoodsService
 * Description:
 * date: 2022/3/4 22:53
 * 秒杀商品接口类
 * @author Yee
 * @since JDK 1.8
 */
public interface SeckillGoodsService {

    /**
     * 根据时间段查询商品列表
     * @param time
     * @return
     */
    public List<SeckillGoods> getSeckillGoods(String time);


    /**
     * 查询指定时间段的某个商品
     * @param time
     * @param goodsId
     * @return
     */
    public SeckillGoods getSeckillGoodsDetail(String time,
                                              String goodsId);
}
