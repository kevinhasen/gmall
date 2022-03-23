package com.yee.filter;

import com.yee.util.OrderThreadLocalUtil;
import com.yee.util.TokenUtil;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

/**
 * ClassName: CartFilter
 * Description:
 * date: 2022/2/27 13:58
 * 购物车微服务过滤器
 * @author Yee
 * @since JDK 1.8
 */
@WebFilter(filterName = "orderFilter",urlPatterns = "/*")
//过滤器执行顺序
@Order(1)
public class OrderFilter extends GenericFilter {

    /**
     * 自定义过滤器逻辑
     * @param servletRequest
     * @param servletResponse
     * @param filterChain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        //获取请求头中的令牌数据
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        //获取头部信息
        String authorization = request.getHeader("Authorization");
        //获得token
        authorization = authorization.replace("bearer ","");
        //解析令牌,从载荷中获取用户名:jwt令牌头+.+载荷+.+签名
        Map<String, String> map = TokenUtil.dcodeToken(authorization);
        if (!map.isEmpty()){
            String username = map.get("username");
            if (!StringUtils.isEmpty(username)){
                //将用户名存储在ThreadLocal中
                OrderThreadLocalUtil.set(username);
            }
        }
        //放行
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
