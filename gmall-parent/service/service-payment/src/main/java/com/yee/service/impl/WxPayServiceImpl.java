package com.yee.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.wxpay.sdk.WXPayUtil;
import com.yee.service.WxPayService;
import com.yee.util.HttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * ClassName: WxPayServiceImpl
 * Description:
 * date: 2022/3/3 18:37
 *
 * @author Yee
 * @since JDK 1.8
 */
@Service
public class WxPayServiceImpl implements WxPayService {


    /**
     * 公众号id
     */
    @Value("${weixin.pay.appid}")
    private String appId;

    /**
     * 商户号
     */
    @Value("${weixin.pay.partner}")
    private String partner;

    /**
     * 商户的秘钥
     */
    @Value("${weixin.pay.partnerkey}")
    private String partnerkey;

    /**
     * 回调地址
     */
    @Value("${weixin.pay.notifyUrl}")
    private String notifyUrl;


    /**
     * 调用微信支付生成二维码
     * @return
     */
    @Override
    public Map<String, String> getPayUrl(Map<String,String> paramQueryMap) {
        //获得统一下单url
        String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
        //包装参数
        Map<String,String> paramMap = new HashMap<>();
        //商户id
        paramMap.put("appid", appId);
        //商户号
        paramMap.put("mch_id", partner);
        //随机字符串
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
        //商品描述
        paramMap.put("body", paramQueryMap.get("desc"));
        //订单号
        paramMap.put("out_trade_no", paramQueryMap.get("orderId"));
        //金额
        paramMap.put("total_fee", paramQueryMap.get("money"));
        //终端ip,随意
        paramMap.put("spbill_create_ip", "192.168.200.1");
        //回调地址
        paramMap.put("notify_url", notifyUrl);
        //支付方式
        paramMap.put("trade_type", "NATIVE");
        //附加参数处理--防止太长
        paramQueryMap.remove("desc");
        paramQueryMap.remove("orderId");
        paramQueryMap.remove("money");
        paramMap.put("attach", JSONObject.toJSONString(paramQueryMap));
        try {
            //转为xml格式同时生成密钥
            String paramXml = WXPayUtil.generateSignedXml(paramMap, partnerkey);
            //发起post请求
            HttpClient httpClient = new HttpClient(url);
            //设置参数
            httpClient.setXmlParam(paramXml);
            //是否https请求
            httpClient.setHttps(true);
            //发送post
            httpClient.post();
            //获得xml数据
            String contentXml =httpClient.getContent();
            //解析xml格式
            Map<String, String> resultMap = WXPayUtil.xmlToMap(contentXml);
            if(resultMap.get("return_code").equals("SUCCESS") &&
                    resultMap.get("result_code").equals("SUCCESS")){
                return resultMap;
            }else{
                return resultMap;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //获取二维码地址
        //返回
        return null;
    }

    /**
     * 查询订单支付结果
     *
     * @param orderId
     * @return
     */
    @Override
    public Map<String, String> getPayResult(String orderId) {
        //查询订单支付结果的url
        String url = "https://api.mch.weixin.qq.com/pay/orderquery";
        //包装参数
        Map<String,String> paramMap = new HashMap<>();
        //商户id
        paramMap.put("appid", appId);
        //商户号
        paramMap.put("mch_id", partner);
        //随机字符串
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
        //订单号
        paramMap.put("out_trade_no", orderId);

        try {
            //转为xml格式同时生成密钥
            String paramXml = WXPayUtil.generateSignedXml(paramMap, partnerkey);
            //发起post请求
            HttpClient httpClient = new HttpClient(url);
            //设置参数
            httpClient.setXmlParam(paramXml);
            //是否https请求
            httpClient.setHttps(true);
            //发送post
            httpClient.post();
            //获得xml数据
            String contentXml =httpClient.getContent();
            //解析xml格式
            Map<String, String> resultMap = WXPayUtil.xmlToMap(contentXml);
            if(resultMap.get("return_code").equals("SUCCESS") &&
                    resultMap.get("result_code").equals("SUCCESS")){
                return resultMap;
            }else{
                return resultMap;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //返回
        return null;
    }

    /**
     * 关闭交易
     * @return
     */
    @Override
    public Map<String, String> closePay(String orderId) {
        //获取请求的查询订单支付结果的url
        String url = "https://api.mch.weixin.qq.com/pay/closeorder";
        //包装参数
        Map<String,String> paramMap = new HashMap<>();
        //将参数转换为xml格式的数据
        paramMap.put("appid", appId);
        paramMap.put("mch_id", partner);
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
        paramMap.put("out_trade_no", orderId);
        try {
            //将参数转换为xml格式,同时生成签名
            String paramXml = WXPayUtil.generateSignedXml(paramMap, partnerkey);
            //发起post请求
            HttpClient httpClient = new HttpClient(url);
            httpClient.setXmlParam(paramXml);
            httpClient.setHttps(true);
            httpClient.post();
            //获取结果:xml格式的数据
            String contentXml = httpClient.getContent();
            //解析xml格式数据
            Map<String, String> resultMap = WXPayUtil.xmlToMap(contentXml);
            //判断通讯是否成功
            if(resultMap.get("return_code").equals("SUCCESS") &&
                    resultMap.get("result_code").equals("SUCCESS")){
                return resultMap;
            }else{
                return resultMap;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
