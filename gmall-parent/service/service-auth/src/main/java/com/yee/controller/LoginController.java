package com.yee.controller;

import com.yee.gmall.common.result.Result;
import com.yee.gmall.common.util.IpUtil;
import com.yee.service.LoginService;
import com.yee.util.AuthToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * ClassName: LoginController
 * Description:
 * date: 2022/2/25 20:12
 * 自定义登录控制层
 * @author Yee
 * @since JDK 1.8
 */
@RestController
@RequestMapping("/user/login")
public class LoginController {

    @Autowired
    private LoginService loginService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private HttpServletRequest request;
    @GetMapping
    public Result login(String username,String password){
        //登录获得令牌
        AuthToken authToken = loginService.login(username, password);
        //获取用户ip地址
        String ipAddress = IpUtil.getIpAddress(request);
        //存储到redis中
        stringRedisTemplate.opsForValue().set(ipAddress,authToken.getAccessToken());
        return Result.ok(authToken);
    }
}
