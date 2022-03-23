package com.yee;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

/**
 * ClassName: CartFeign
 * Description:
 * date: 2022/2/28 20:56
 * 购物车微服务feign接口
 * @author Yee
 * @since JDK 1.8
 */
@FeignClient(value = "service-cart",path = "/api/cart")
public interface CartFeign {

    /**
     * 查询订单确认页面的信息内部调用
     * @return
     */
    @GetMapping("/getOrderAddInfo")
    public Map<String, Object> getOrderAddInfo();

    /**
     * 生成订单后移除购物车数据
     * @return
     */
    @GetMapping("/removeCart")
    public Boolean removeCart();
}
