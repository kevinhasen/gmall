package com.yee.controller;

import com.yee.gmall.common.result.Result;
import com.yee.gmall.model.list.Goods;
import com.yee.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName: GoodsController
 * Description:
 * date: 2022/2/22 16:43
 * es商品管理控制层
 * @author Yee
 * @since JDK 1.8
 */
@RestController
@RequestMapping("/api/goods")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @GetMapping("/create")
    public Result create(){
        //创建索引
        elasticsearchRestTemplate.createIndex(Goods.class);
        //创建映射
        elasticsearchRestTemplate.putMapping(Goods.class);
        return Result.ok();
    }

    /**
     * 新增es数据
     * @param skuId
     * @return
     */
    @GetMapping("/add/{skuId}")
    public Result add(@PathVariable("skuId") Long skuId){
        goodsService.addGoodsIntoEs(skuId);
        return Result.ok();
    }

    @GetMapping("/del/{skuId}")
    public Result del(@PathVariable("skuId") Long skuId){
        goodsService.removeGoodsFromEs(skuId);
        return Result.ok();
    }


}
