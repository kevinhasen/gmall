package com.yee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * ClassName: ListApplication
 * Description:
 * date: 2022/2/22 15:16
 *  搜索微服务启动类
 * @author Yee
 * @since JDK 1.8
 */
//不需要使用数据源自动配置
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableDiscoveryClient
@EnableFeignClients
public class ListApplication {
    public static void main(String[] args) {
        SpringApplication.run(ListApplication.class,args);
    }
}
