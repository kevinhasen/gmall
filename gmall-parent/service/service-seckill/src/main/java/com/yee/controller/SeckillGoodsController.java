package com.yee.controller;

import com.yee.gmall.common.result.Result;
import com.yee.gmall.model.activity.SeckillGoods;
import com.yee.service.SeckillGoodsService;
import com.yee.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * ClassName: SeckillGoodsController
 * Description:
 * date: 2022/3/4 22:57
 * 秒杀商品控制层
 * @author Yee
 * @since JDK 1.8
 */
@RestController
@RequestMapping("/api/seckill/goods")
public class SeckillGoodsController {

    @Autowired
    private SeckillGoodsService seckillGoodsService;


    /**
     * 根据时间段获得商品数据
     * @param time
     * @return
     */
    @GetMapping("/getSeckillGoods")
    public Result getSeckillGoods(String time){
        List<SeckillGoods> seckillGoods = seckillGoodsService.getSeckillGoods(time);
        return Result.ok(seckillGoods);
    }

    /**
     * 获取时间段信息
     * @return
     */
    @GetMapping("/getTimeList")
    public Result getTimeList(){
        List<Date> dateMenus = DateUtil.getDateMenus();
        return Result.ok(dateMenus);
    }


    /**
     * 获取指定时间段的商品
     * @param time
     * @param goodsId
     * @return
     */
    @GetMapping("/getSeckillGoodsDetail")
    public Result getSeckillGoodsDetail(String time, String goodsId){
        SeckillGoods goodsDetail = seckillGoodsService.getSeckillGoodsDetail(time, goodsId);
        return Result.ok(goodsDetail);
    }

}
