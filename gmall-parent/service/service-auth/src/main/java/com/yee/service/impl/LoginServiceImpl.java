package com.yee.service.impl;

import com.yee.service.LoginService;
import com.yee.util.AuthToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Map;

/**
 * ClassName: LoginServiceImpl
 * Description:
 * date: 2022/2/25 20:15
 * 自定义登录服务层实现类
 * @author Yee
 * @since JDK 1.8
 */
@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private RestTemplate restTemplate;
    /**
     * 负载均衡客服端
     */
    @Autowired
    private LoadBalancerClient loadBalancerClient;
    @Value("${auth.clientId}")
    private String clientId;
    @Value("${auth.clientSecret}")
    private String clientSecret;
    /**
     * 自定义登录
     *  @param username
     * @param password
     * @return
     */
    @Override
    public AuthToken login(String username, String password) {
        //参数校验
        if (StringUtils.isEmpty(username)
                || StringUtils.isEmpty(password)){
            throw new RuntimeException("参数异常");
        }
        //包装body三个参数,用户名,密码,模式
        MultiValueMap<String, String> body = new HttpHeaders();
        body.add("grant_type", "password");
        body.add("username", username);
        body.add("password", password);
        //包装客服端id和客服端密钥
        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add("Authorization", getHead());
        //数据转发
//        String url = "http://localhost:9001/oauth/token";
        //通过配置中心动态获取url,
        ServiceInstance choose = loadBalancerClient.choose("service-oauth");
        String url = choose.getUri().toString() + "/oauth/token";
        ResponseEntity<Map> exchange =
                restTemplate.exchange(url, HttpMethod.POST, new HttpEntity(body, headers), Map.class);
        //解析结果
        Map<String,String> result = exchange.getBody();
        //返回结果
        AuthToken authToken = new AuthToken();
        authToken.setJti(result.get("jti"));
        authToken.setAccessToken(result.get("access_token"));
        authToken.setRefreshToken(result.get("refresh_token"));
        return authToken;
    }


    public String getHead(){
        //id:密钥
        String head = clientId + ":" + clientSecret;
        //base64加密
        byte[] encode = Base64.getEncoder().encode(head.getBytes());
        //返回加密结果
        return "Basic " + new String(encode);
    }
}
