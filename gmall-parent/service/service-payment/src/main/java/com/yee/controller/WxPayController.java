package com.yee.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.wxpay.sdk.WXPayUtil;
import com.yee.gmall.common.result.Result;
import com.yee.service.WxPayService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * ClassName: WxPayController
 * Description:
 * date: 2022/3/3 19:04
 * 微信控制层
 * @author Yee
 * @since JDK 1.8
 */
@RestController
@RequestMapping("/api/pay/wx")
public class WxPayController {

    @Autowired
    private WxPayService wxPayService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    /**
     * 获取微信二维码
     * @return
     */
    @GetMapping("/getPayUrl")
    public Result getPayUrl(@RequestParam Map<String,String> paramMap){
        Map<String, String> map = wxPayService.getPayUrl(paramMap);
        return Result.ok(map);
    }


    /**
     * 主动查询支付结果
     * @param orderId
     * @return
     */
    @GetMapping("/getPayResult")
    public Result getPayResult(String orderId){
        Map<String, String> map = wxPayService.getPayResult(orderId);
        return Result.ok(map);
    }

    /**
     * 微信回调方法
     * @param request
     * @return
     */
    @RequestMapping("/callback/notify")
    public String callbackNotify(HttpServletRequest request) throws Exception{
            //获取通知数据流
//        ServletInputStream inputStream = request.getInputStream();
        //读取数据流
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        //定义缓冲区
//        byte[] buffer = new byte[1024];
//        int len = 0;
//        if((len = inputStream.read(buffer)) != -1){
//            outputStream.write(buffer,0,len);
//        }
        //将数据流转换为字符串xml
//        byte[] bytes = outputStream.toByteArray();
        //将字符串转换为map
//        Map<String, String> resultMap = WXPayUtil.xmlToMap(new String(bytes));
        //获得附加参数
//        String attachString = resultMap.get("attach");
        //反序列化附加参数
//        Map<String, String> attach = JSONObject.parseObject(attachString, Map.class);
//        String s = JSONObject.toJSONString(resultMap);
        //返回微信收到结果
        //测试代码
        String s =
               "{\"transaction_id\":\"4200001349202203080253133454\",\"nonce_str\":\"71080101e19c4c6b99fec4d053459061\",\"bank_type\":\"OTHERS\",\"openid\":\"oHwsHuAIAcBQlVXL8S2OJceTarOU\",\"sign\":\"6E0743E6E06E01B04587067C33B84113\",\"fee_type\":\"CNY\",\"mch_id\":\"1558950191\",\"cash_fee\":\"1\",\"out_trade_no\":\"php123456\",\"appid\":\"wx74862e0dfcf69954\",\"total_fee\":\"1\",\"trade_type\":\"NATIVE\",\"result_code\":\"SUCCESS\",\"attach\":\"{\\\"exchange\\\":\\\"order_pay_exchange\\\",\\\"routingKey\\\":\\\"pay.seckill.order.wx\\\"}\",\"time_end\":\"20220308230318\",\"is_subscribe\":\"N\",\"return_code\":\"SUCCESS\"}";
        //反序列化附加参数
        Map<String, String> attach = JSONObject.parseObject(s, Map.class);

        //动态获得路由器和交换机,区分秒杀订单和普通订单
        rabbitTemplate.convertAndSend(attach.get("exchange"), attach.get("routingKey"), s);
        Map<String, String> result = new HashMap<>();
        result.put("return_code", "SUCCESS");
        result.put("return_msg", "OK");
        return WXPayUtil.mapToXml(result);
    }

    /**
     * 关闭交易
     * @param orderId
     * @return
     */
    @GetMapping(value = "/closePay")
    public Result closePay(String orderId){
        return Result.ok(wxPayService.closePay(orderId));
    }
}
