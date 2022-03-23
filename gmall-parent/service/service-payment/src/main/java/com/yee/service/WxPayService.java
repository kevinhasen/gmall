package com.yee.service;

import java.util.Map;

/**
 * ClassName: WxPayService
 * Description:
 * date: 2022/3/3 18:36
 * 微信支付接口类
 * @author Yee
 * @since JDK 1.8
 */
public interface WxPayService {

    /**
     * 调用微信支付生成二维码
     * @return
     */
    public Map<String, String> getPayUrl(Map<String,String> paramMap);

    /**
     * 查询订单支付结果
     * @param orderId
     * @return
     */
    public Map<String, String> getPayResult(String orderId);

    /**
     * 关闭交易
     * @return
     */
    public Map<String, String> closePay(String orderId);
}
