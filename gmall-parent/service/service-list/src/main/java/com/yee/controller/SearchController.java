package com.yee.controller;

import com.yee.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.util.Map;

/**
 * ClassName: SearchController
 * Description:
 * date: 2022/2/22 21:09
 * 商品搜索内部调用接口
 * @author Yee
 * @since JDK 1.8
 */

@RestController
@RequestMapping(value = "/api/list")
public class SearchController {

    @Autowired
    private SearchService searchService;

    /**
     * 商品搜索
     * @param searchData
     * @return
     */
    @GetMapping(value = "/search")
    public Map<String, Object> search(@RequestParam  Map<String,String> searchData){
        Map<String, Object> map = searchService.search(searchData);
        return map;
    }
}
