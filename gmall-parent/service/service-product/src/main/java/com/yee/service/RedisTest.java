package com.yee.service;

/**
 * ClassName: TestService
 * Description:
 * date: 2022/2/18 17:55
 * redis测试接口
 * @author Yee
 * @since JDK 1.8
 */
public interface RedisTest {


    /**
     * 测试redis
     */
    public void setRedis();

    /**
     * 测试分步式锁
     */
    public void setRedisByRedission();
}
