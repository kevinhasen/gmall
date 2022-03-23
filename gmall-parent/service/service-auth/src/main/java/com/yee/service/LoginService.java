package com.yee.service;

import com.yee.util.AuthToken;

import java.util.Map;

/**
 * ClassName: LoginService
 * Description:
 * date: 2022/2/25 20:14
 * 自定义登录服务接口
 * @author Yee
 * @since JDK 1.8
 */

public interface LoginService {

    /**
     * 自定义登录
     * @param username
     * @param password
     * @return
     */
    public AuthToken login(String username, String password);
}
