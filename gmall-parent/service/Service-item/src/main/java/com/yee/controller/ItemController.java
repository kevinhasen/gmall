package com.yee.controller;

import com.yee.gmall.common.result.Result;
import com.yee.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * ClassName: ItemController
 * Description:
 * date: 2022/2/17 19:30
 *  商品微服务处理类内部调用
 * @author Yee
 * @since JDK 1.8
 */
@RestController
@RequestMapping("/admin/item")
public class ItemController {

    @Autowired
    private ItemService itemService;


    /**
     * 查询商品详情全部信息
     * @param skuId
     * @return
     */
    @GetMapping("/getItemInfo/{skuId}")
    public  Map<String, Object> getItemInfo(@PathVariable("skuId") Long skuId){
        Map<String, Object> map = itemService.getSkuItem(skuId);
        return map;
    }


}
