package com.yee.controller;

import com.yee.gmall.common.result.Result;
import com.yee.service.IndexCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName: IndexCategoryController
 * Description:
 * date: 2022/2/20 20:34
 *
 * @author Yee
 * @since JDK 1.8
 */
@RestController
@RequestMapping("/api/product/category")
public class IndexCategoryController {


    @Autowired
    private IndexCategoryService indexCategoryService;
    /**
     * 获取首页分类信息
     * @return
     */
    @GetMapping("/getIndexCategory")
    public Result getIndexCategory(){
        indexCategoryService.getIndexCategory();
        return Result.ok();
    }
}
