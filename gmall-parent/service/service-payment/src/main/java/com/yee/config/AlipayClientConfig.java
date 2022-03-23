package com.yee.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 初始化阿里支付的对象
 */
@Configuration
public class AlipayClientConfig {

    @Value("${alipay_url}")
    private String url;

    @Value("${app_id}")
    private String appId;

    @Value("${app_private_key}")
    private String appPrivateKey;

    @Value("${alipay_public_key}")
    private String alipayPublicKey;

    /**
     * 客户端初始化
     * @return
     */
    @Bean("alipayClient")
    public AlipayClient AlipayClient(){
        return new DefaultAlipayClient(url,
                        appId,
                        appPrivateKey,
                        "json",
                        "UTF-8",
                        alipayPublicKey,
                        "RSA2");
    }
}
