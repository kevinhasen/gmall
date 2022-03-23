package com.yee;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * ClassName: ListFeign
 * Description:
 * date: 2022/2/23 16:24
 * 搜索微服务feign接口
 * @author Yee
 * @since JDK 1.8
 */
@FeignClient(value = "service-list",path = "/api/list")
public interface ListFeign {

    /**
     * 商品搜索
     * @param searchData
     * @return
     */
    @GetMapping(value = "/search")
    public Map<String, Object> search(@RequestParam Map<String,String> searchData);
}
