package com.yee.controller;

import com.yee.gmall.common.result.Result;
import com.yee.pojo.UserRecode;
import com.yee.service.SeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName: SeckillOrderController
 * Description:
 * date: 2022/3/4 23:46
 * 秒杀订单控制层
 * @author Yee
 * @since JDK 1.8
 */
@RestController
@RequestMapping("/api/seckill/order")
public class SeckillOrderController {

    @Autowired
    private SeckillOrderService seckillOrderService;

    /**
     * 同步排队,异步下单
     * @param time
     * @param goodsId
     * @param num
     * @return
     */
    @GetMapping("/addSeckillOrder")
    public Result addSeckillOrder(String time, String goodsId, Integer num){
        UserRecode userRecode = seckillOrderService.
                addSeckillOrder(time, goodsId, num);
        return Result.ok(userRecode);
    }

    /**
     * 查询用户排队状态
     * @return
     */
    @GetMapping("/getUserRecode")
    public Result getUserRecode(){
        UserRecode userRecode = seckillOrderService.getUserRecode();
        return Result.ok(userRecode);
    }

    /**
     * 取消秒杀订单
     * @return
     */
    @GetMapping("/cancelSeckillOrder")
    public Result cancelSeckillOrder(){
        String username = "yee";
        seckillOrderService.cancelSeckillOrder(username,"主动取消订单");
        return Result.ok();
    }
}
