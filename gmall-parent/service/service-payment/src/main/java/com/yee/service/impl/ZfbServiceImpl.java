package com.yee.service.impl;

import com.alipay.api.AlipayClient;
import com.yee.service.ZfbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import org.springframework.stereotype.Service;

/**
 * ClassName: ZfbServiceImpl
 * Description:
 * date: 2022/3/3 21:42
 *
 * @author Yee
 * @since JDK 1.8
 */
@Service
public class ZfbServiceImpl implements ZfbService {

    @Value("${return_payment_url}")
    private String returnPaymentUrl;

    @Value("${notify_payment_url}")
    private String notifyPaymentUrl;

    @Autowired
    private AlipayClient alipayClient;
    /**
     * 获取支付宝下单页面
     *
     * @param money
     * @param orderId
     * @param desc
     * @return
     */
    @Override
    public String getPayUrl(String money, String orderId, String desc) {
        //声明请求体
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        //设置通知地址
        request.setNotifyUrl(notifyPaymentUrl);
        //设置回调地址
        request.setReturnUrl(returnPaymentUrl);
        //设置请求参数
        JSONObject bizContent = new JSONObject();
        //订单号
        bizContent.put("out_trade_no", orderId);
        //金额,元单位
        bizContent.put("total_amount", money);
        //标题
        bizContent.put("subject", desc);
        //产品固定值
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
        request.setBizContent(bizContent.toString());
        try {
            //发起请求获取结果
            AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
            if(response.isSuccess()){
                //支付页面
                return response.getBody();
            } else {
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询订单的支付结果
     *
     * @param orderId
     * @return
     */
    @Override
    public String getPayResult(String orderId) {
        //请求体初始化
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        //包装参数
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", orderId);
        request.setBizContent(bizContent.toString());
        try {
            //请求获取结果
            AlipayTradeQueryResponse response = alipayClient.execute(request);
            //返回结果
            return response.getBody();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 关闭交易
     *
     * @param orderId
     * @return
     */
    @Override
    public void closePay(String orderId) {

    }
}
