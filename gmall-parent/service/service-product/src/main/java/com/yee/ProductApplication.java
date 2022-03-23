package com.yee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * ClassName: ProductApplication
 * Description:
 * date: 2022/2/12 17:54
 * 商品管理微服务启动类
 * @author Yee
 * @since JDK 1.8
 */
@SpringBootApplication
//开启注册中心发现
@EnableDiscoveryClient
public class ProductApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class,args);
    }
}
