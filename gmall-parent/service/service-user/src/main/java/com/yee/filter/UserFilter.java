package com.yee.filter;

import com.yee.util.UserThreadLocalUtil;
import com.yee.util.TokenUtil;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

/**
 * ClassName: UserFilter
 * Description:
 * date: 2022/2/27 14:29
 * 用户过滤器
 * @author Yee
 * @since JDK 1.8
 */
@WebFilter(filterName = "userFilter",urlPatterns = "/*")
public class UserFilter extends GenericFilter {
    /**
     * 自定义过滤器
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
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        //获得请求头信息
        String authorization = request.getHeader("Authorization");
        //获得token
        authorization = authorization.replace("bearer ","");
        //获得用户名
        Map<String, String> map = TokenUtil.dcodeToken(authorization);
        if (!map.isEmpty()){
            String username = map.get("username");
            if (!StringUtils.isEmpty(username)){
                UserThreadLocalUtil.set(username);
            }
        }
        //放行
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
