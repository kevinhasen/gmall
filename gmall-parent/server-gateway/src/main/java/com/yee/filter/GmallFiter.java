package com.yee.filter;

import com.yee.util.IpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * ClassName: GMallFiter
 * Description:
 * date: 2022/2/25 22:33
 * 商城项目网关全局过滤器
 * @author Yee
 * @since JDK 1.8
 */
@Component
public class GmallFiter implements GlobalFilter, Ordered {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 过滤器自定义逻辑
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //获取用户请求体,响应体
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        //获取url参数是否有token
        String token = request.getQueryParams().getFirst("token");
        if (StringUtils.isEmpty(token)){
            //url没有则从head中取
            token = request.getHeaders().getFirst("token");
          if (StringUtils.isEmpty(token)){
              //head没有从cookie取
              HttpCookie cookie = request.getCookies().getFirst("token");
              if (cookie != null){
                  String name = cookie.getName();
                 token = cookie.getValue();
              }
          }
        }
        //都没有则拒绝
        if (StringUtils.isEmpty(token)){
            response.setStatusCode(HttpStatus.REQUEST_HEADER_FIELDS_TOO_LARGE);
            response.setComplete();
        }
        //获得ip地址
        String ipAddress = IpUtil.getGatwayIpAddress(request);
        //查询redis存储的令牌
        String redisToken = stringRedisTemplate.opsForValue().get(ipAddress);
        if (StringUtils.isEmpty(redisToken)){
            //用户之前没有登录过
            response.setStatusCode(HttpStatus.REQUEST_HEADER_FIELDS_TOO_LARGE);
            response.setComplete();
        }
        //判断redis的令牌判断现在的令牌是否一致
        if (!redisToken.equals(token)){
            //用户之前登录过,登录令牌和申请令牌不一致
            response.setStatusCode(HttpStatus.REQUEST_HEADER_FIELDS_TOO_LARGE);
            response.setComplete();
        }
        //request请求完善
        request.mutate().header("Authorization","bearer " + token);
        //有则放行
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
