package com.yee.util;

/**
 * ClassName: CartThreadLocalUtil
 * Description:
 * date: 2022/2/27 13:45
 * 购物车微服务本地线程对象工具类
 * @author Yee
 * @since JDK 1.8
 */
public class OrderThreadLocalUtil {
    /**
     * 购物车微服务使用的本地线程对象
     */
    private final static ThreadLocal<String> threadLocal = new ThreadLocal<>();


    /**
     * 保存用户名:
     * 1.数据直接保存在各自的线程中
     * 2.取非常快
     * 3.安全-->每个线程是隔离的独立的
     * 4.不要放太大的对象
     * @param username
     */
   public static void set(String username){
    threadLocal.set(username);
   }


    /**
     * 获取用户名
     * @return
     */
    public static String get(){
        String username = threadLocal.get();
        return username;
    }
}
