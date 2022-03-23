package com.yee.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 自定义线程池
 */
@Configuration
public class ThreadPoolConfig {

    /**
     * 自定义的线程池对象
     * @return
     */
    @Bean
    public ThreadPoolExecutor threadPoolExecutor(){
        /**
         * 核心线程数
         * 拥有最多线程数
         * 表示空闲线程的存活时间
         * 存活时间单位
         * 用于缓存任务的阻塞队列
         * 省略：
         *  threadFactory：指定创建线程的工厂
         *  handler：表示当workQueue已满，且池中的线程数达到maximumPoolSize时，线程池拒绝添加新任务时采取的策略。
         */
        return new ThreadPoolExecutor(50,
                500,
                30,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10000));
        /**
         * 线程池的初始化流程(以及参数作用):
         * 1.当项目初始化完成的时候,初始化这个自定义的线程池
         * 2.当面指定是核心线程数50个,最大线程数500个--->
         *        这个线程池中核心的线程数50个: 不会被回收的线程
         *        非核心线程: 450个: 会被回收的线程
         * 3.第三和第四个参数是定义了非核心线程回收的时间
         * 4.定义阻塞队列的长度为1万
         * 线程池的工作流程(膨胀流程):
         * 1.当现在有1个任务需要线程的时候,从线程池中获取一个线程,但是初始化完成的线城池中0个核心线程0个非核心线程
         * 2.优先创建核心线程,判断核心线程数是否达到了最大?50--->0,创建一个核心线程去运行这个任务-->核心:1个  非:0个
         * 3.同一个时间又来了49个任务,创建49个核心线程,去执行任务--->50个核心线程 0个非核心线程
         * 4.来了第51个任务,获取线程,线程池判断核心线程数是否达到了最大,发现已经达到了最大,将任务放入阻塞队列
         *    核心线程:50个 非核心线程: 0个 阻塞队列: 1个任务
         * 5.同时随着第51个任务一起进入的有1万个,任务总数就为10050个任务: 核心线程满了 阻塞队列满了
         *   核心线程:50个 非核心线程: 0个 阻塞队列: 10000个任务
         * 6.若依然有任务需要线程,来了450个任务,发现核心线程满了 阻塞队列满了,判断是否达到线程池的最大线程数量,总任务数量:10500个
         *    创建非核心线程: 50个 非核心线程: 450个 阻塞队列: 10000个任务
         * 7.现在的情况是核心线程都在干活满了,非核心线程都在干活满了 阻塞队列也满了,再有任务来,触发拒绝策略
         *
         * 拒绝策略: 4种
         * 1--AbortPolicy(默认的拒绝策略): 抛出异常,不执行任务
         * 2--CallerRunsPolicy: 谁调用的执行的这个任务的方法,谁执行
         * 3--DiscardPolicy: 不抛异常,不执行任务,啥都不做
         * 4--DiscardOldestPolicy:
         *  先从阻塞队列中出列一个等最长时间的任务(先进先出FIFO-->出列的就是等最久的那个任务),将这个任务入列
         */
    }
}