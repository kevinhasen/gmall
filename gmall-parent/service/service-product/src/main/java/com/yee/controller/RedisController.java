package com.yee.controller;

import com.yee.gmall.common.result.Result;
import com.yee.service.RedisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName: RedisController
 * Description:
 * date: 2022/2/18 18:00
 * 测试redis控制
 * @author Yee
 * @since JDK 1.8
 */
@RestController
@RequestMapping("/api/product")
public class RedisController {
    @Autowired
    private RedisTest redisTest;

    /**
     * redis测试
     * @return
     */
    @GetMapping("/test")
    public Result test(){
        redisTest.setRedisByRedission();
        return Result.ok();
    }
}
