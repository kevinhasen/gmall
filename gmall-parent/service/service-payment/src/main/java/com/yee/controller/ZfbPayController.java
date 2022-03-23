package com.yee.controller;

import com.alibaba.fastjson.JSONObject;
import com.yee.gmall.common.result.Result;
import com.yee.service.ZfbService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * ClassName: ZfbPayController
 * Description:
 * date: 2022/3/3 21:47
 * 支付宝控制层
 * @author Yee
 * @since JDK 1.8
 */
@RestController
@RequestMapping(value = "/api/pay/zfb")
public class ZfbPayController {

    @Autowired
    private ZfbService zfbService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    /**
     * 支付宝统一下单
     * @param money
     * @param orderId
     * @param desc
     * @return
     */
    @GetMapping(value = "/getPayUrl")
    public String getPayUrl(String money, String orderId, String desc){
        return zfbService.getPayUrl(money, orderId, desc);
    }
    /**
     * 主动查询支付的结果
     * @param orderId
     * @return
     */
    @GetMapping(value = "/getPayResult")
    public Result getPayResult(String orderId){
        return Result.ok(zfbService.getPayResult(orderId));
    }

    /**
     * 同步回调
     * @return
     */
    @RequestMapping(value = "/callback/return")
    public String returnCallback(@RequestParam Map<String, String> retrunData){
        System.out.println("同步回调成功,返回的参数为:" + retrunData);
        return "跳转到商城";
    }


    /**
     * 异步通知: 不及时告知支付宝收到了结果,反复的调用
     * @param retrunData
     * @return
     */
    @RequestMapping(value = "/callback/notify")
    public String notifyCallback(@RequestParam Map<String, String> retrunData){
        System.out.println("异步通知成功,通知的参数为:" + retrunData);
        String s = JSONObject.toJSONString(retrunData);
        //发支付结果的消息
        rabbitTemplate.convertAndSend("order_pay_exchange", "pay.order.zfb", s);
        return "success";
    }
}
