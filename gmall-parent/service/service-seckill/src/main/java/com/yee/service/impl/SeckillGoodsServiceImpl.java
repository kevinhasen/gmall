package com.yee.service.impl;

import com.yee.gmall.model.activity.SeckillGoods;
import com.yee.service.SeckillGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ClassName: SeckillGoodsServiceImpl
 * Description:
 * date: 2022/3/4 22:55
 * 秒杀商品实现类
 * @author Yee
 * @since JDK 1.8
 */
@Service
public class SeckillGoodsServiceImpl implements SeckillGoodsService {


    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 根据时间段查询商品列表
     *
     * @param time
     * @return
     */
    @Override
    public List<SeckillGoods> getSeckillGoods(String time) {
        List<SeckillGoods> values = redisTemplate.opsForHash().values(time);
        return values;
    }

    /**
     * 查询指定时间段的某个商品
     *
     * @param time
     * @param goodsId
     * @return
     */
    @Override
    public SeckillGoods getSeckillGoodsDetail(String time, String goodsId) {
        SeckillGoods seckillGoods = (SeckillGoods)redisTemplate.opsForHash().get(time, goodsId);
        return seckillGoods;
    }
}
