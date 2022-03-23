package com.yee;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * ClassName: ItemFeign
 * Description:
 * date: 2022/2/19 23:31
 * 商品详情微服务feign接口
 * @author Yee
 * @since JDK 1.8
 */
@FeignClient(value = "service-item",path = "/admin/item")
public interface ItemFeign {

    /**
     * 查询商品详情全部信息
     * @param skuId
     * @return
     */
    @GetMapping("/getItemInfo/{skuId}")
    public Map<String, Object> getItemInfo(@PathVariable("skuId") Long skuId);
}
