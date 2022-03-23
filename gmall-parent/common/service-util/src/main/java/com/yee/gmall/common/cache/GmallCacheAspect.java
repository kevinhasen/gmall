package com.yee.gmall.common.cache;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;


import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * 商城项目缓存aop
 */
@Component
@Aspect
public class GmallCacheAspect {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;


    @Around("@annotation(com.yee.gmall.common.cache.GmallCache)")
    public Object cacheAroundAdvice(ProceedingJoinPoint point){

       //返回结果初始化
        Object result = null;
        try {
            //获取方法参数
            Object[] args = point.getArgs();
            //获得方法签名
            MethodSignature signature = (MethodSignature) point.getSignature();
            //获得方法注解
            GmallCache gmallCache = signature.getMethod().getAnnotation(GmallCache.class);
            // 前缀
            String prefix = gmallCache.prefix();
            // 拼接前缀
            String key = prefix+Arrays.asList(args).toString();

            // 获取缓存数据
            result = cacheHit(signature, key);
            if (result!=null){
                // 缓存有数据
                return result;
            }
            // 获得分布式锁
            String lockKey = key + ":lock";
            RLock lock = redissonClient.getLock(lockKey);
            boolean flag = lock.tryLock(100, 100, TimeUnit.SECONDS);
            if (flag){
               try {
                   try {
                       //执行方法,查询数据库数据
                       result = point.proceed(point.getArgs());
                       // 如果没有数据
                       if (null==result){
                           // 初始化对象放入缓存,5分钟过期
                           Object o = new Object();
                           this.redisTemplate.opsForValue().set(key, JSONObject.toJSONString(o),300,TimeUnit.SECONDS);
                           return null;
                       }else {
                           //如果有数据也设置过期时间
                           this.redisTemplate.opsForValue().set(key, JSONObject.toJSONString(result),24*60*60,TimeUnit.SECONDS);
                       }
                   } catch (Throwable throwable) {
                       throwable.printStackTrace();
                   }
                   // 并把结果放入缓存
                   this.redisTemplate.opsForValue().set(key, JSONObject.toJSONString(result));
                   return result;
               }catch (Exception e){
                   e.printStackTrace();
               }finally {
                   // 释放锁
                   lock.unlock();
               }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        //boolean flag = lock.tryLock(10L, 10L, TimeUnit.SECONDS);
        return result;
    }
    // 获取缓存数据
    private Object cacheHit(MethodSignature signature, String key) {
        // 1. 查询缓存
        String cache = (String)redisTemplate.opsForValue().get(key);
        if (StringUtils.isNotBlank(cache)) {
            // 有，则反序列化，直接返回
            Class returnType = signature.getReturnType(); // 获取方法返回类型
            // 不能使用parseArray<cache, T>，因为不知道List<T>中的泛型
            return JSONObject.parseObject(cache, returnType);
        }
        return null;
    }

}
