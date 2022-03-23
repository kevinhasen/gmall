package com.yee.service.impl;

import com.yee.service.RedisTest;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * ClassName: TestServiceImpl
 * Description:
 * date: 2022/2/18 17:56
 * redis测试类
 * @author Yee
 * @since JDK 1.8
 */
@Service
public class RedisTestImpl implements RedisTest {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient;
    /**
     * Redis测试案例
     * ab -n 5000 -c 100 http://192.168.200.1:8206/api/redis/test
     */
    @Override
    public void setRedis() {
        //从redis从获取一个key
        Integer i = (Integer)redisTemplate.opsForValue().get("java0823");
        //判断这个key
        if (i != null){
            //自增
            redisTemplate.opsForValue().increment("java0823");
        }

    }

    /**
     * 测试分步式锁
     */
    @Override
    public void setRedisByRedission() {
        //获得锁
        RLock lock = redissonClient.getLock("lock");
        //抢锁
       try {
           if (lock.tryLock(10,10, TimeUnit.SECONDS)){
              try {
                  //加锁成功
                  Integer i = (Integer)redisTemplate.opsForValue().get("java0823");
                  if (i != null){
                      i++;
                      redisTemplate.opsForValue().set("java0823",i);
                  }
              }catch (Exception e){
                  e.printStackTrace();
                  System.out.println("获得锁之后出现异常");
              }finally {
                  //释放锁
                  lock.unlock();
              }
           }
       }catch (Exception e){
           e.printStackTrace();
           //加锁失败
           System.out.println("获得锁之前出现异常");
       }


    }
}
