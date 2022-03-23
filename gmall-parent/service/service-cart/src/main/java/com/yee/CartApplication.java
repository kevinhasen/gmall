package com.yee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * ClassName: CartApplication
 * Description:
 * date: 2022/2/26 19:07
 * 购物车微服务启动类
 * @author Yee
 * @since JDK 1.8
 */
@SpringBootApplication
//注册中心注解
@EnableDiscoveryClient
//feign注解
@EnableFeignClients
//过滤器注解
@ServletComponentScan("com.yee.filter")
public class CartApplication {
    public static void main(String[] args) {
        SpringApplication.run(CartApplication.class,args);
    }
}
