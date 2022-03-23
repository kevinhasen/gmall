package com.yee.intercept;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * ClassName: OrderIntercept
 * Description:
 * date: 2022/2/28 23:39
 * 订单微服务拦截器,feign发起调用前,先进这里
 * @author Yee
 * @since JDK 1.8
 */
@Component
public class OrderIntercept implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        //获取原request对象的信息
        ServletRequestAttributes servletRequestAttributes =
                (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        if(servletRequestAttributes != null){
            //获取原reques对象
            HttpServletRequest request = servletRequestAttributes.getRequest();
            //获取原request对象中的数据
            Enumeration<String> headerNames = request.getHeaderNames();
            //遍历获取请求头的每个参数
            while (headerNames.hasMoreElements()){
                //获取每一个请求头的参数名字
                String name = headerNames.nextElement();
                //获取名字对应的值
                String value = request.getHeader(name);
                //放到当前feign调用的对象的请求头中去
                requestTemplate.header(name, value);
            }
        }
    }
}
