package com.yee.service;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * ClassName: IndexCategoryService
 * Description:
 * date: 2022/2/20 15:59
 * 首页分类信息查询接口类
 * @author Yee
 * @since JDK 1.8
 */

public interface IndexCategoryService {


    /**
     * 查询首页分类信息
     */
    public List<JSONObject> getIndexCategory();
}
