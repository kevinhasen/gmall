package com.yee.service;

import java.util.Map;

/**
 * ClassName: SearchService
 * Description:
 * date: 2022/2/22 19:20
 * 搜索接口类
 * @author Yee
 * @since JDK 1.8
 */
public interface SearchService {

    /**
     * 商品搜索
     * @param searchData
     * @return
     */
    public Map<String, Object> search( Map<String,String> searchData);
}
