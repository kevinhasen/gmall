package com.yee.controller;



import com.yee.gmall.common.cache.GmallCache;
import com.yee.gmall.model.product.*;
import com.yee.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


/**
 * ClassName: ItemController
 * Description:
 * date: 2022/2/17 18:38
 * 商品微服务使用的控制接口
 *  @GmallCache是全局环绕注解
 * @author Yee
 * @since JDK 1.8
 */
@RestController
@RequestMapping("/api/product")
public class ItemController {

    @Autowired
    private ItemService itemService;

    /**
     * 根据skuId查询商品详情
     * @param skuId
     * @return
     */
    @GetMapping("/getSkuInfo/{skuId}")
    @GmallCache(prefix = "getSkuInfo:")
    public SkuInfo getSkuInfo(@PathVariable("skuId") Long skuId){
        SkuInfo skuInfo = itemService.getSkuInfo(skuId);
        return skuInfo;
    }

    /**
     * 根据三级分类ID查找二级一级分类
     * @param category3Id
     * @return
     */
    @GetMapping("/getCategory/{category3Id}")
    @GmallCache(prefix = "getCategory:")
    public BaseCategoryView getCategory(@PathVariable("category3Id") Long category3Id){
        BaseCategoryView category = itemService.getCategory(category3Id);
        return category;
    }

    /**
     * 根据skuId查询图片
     *
     * @param skuId
     * @return
     */
    @GetMapping("/getSkuImage/{skuId}")
    @GmallCache(prefix = "getSkuImageList:")
    public List<SkuImage> getSkuImageList(@PathVariable("skuId") Long skuId){
        List<SkuImage> list = itemService.getSkuImageList(skuId);
        return list;
    }

    /**
     * 根据skuId查询价格
     *
     * @param skuId
     * @return
     */
    @GetMapping("/getPrice/{skuId}")
    @GmallCache(prefix = "getPrice:")
    public BigDecimal getPrice(@PathVariable("skuId") Long skuId){
        BigDecimal price = itemService.getPrice(skuId);
        return price;
    }

    /**
     * 根据spuId和skuID查找属性和属性值,并且指定选中的值
     *
     * @param skuId
     * @param spuId
     * @return
     */
    @GetMapping("/getSpuSaleAttr/{skuId}/{spuId}")
    @GmallCache(prefix = "getSpuSaleAttr:")
    public List<SpuSaleAttr> getSpuSaleAttr(@PathVariable("skuId") Long skuId,
                                            @PathVariable("spuId") Long spuId){
        List<SpuSaleAttr> list = itemService.getSpuSaleAttr(skuId, spuId);
        return list;
    }

    /**
     * 根据spu获得spu下的所有skuId和属性值键值对
     * @param spuId
     * @return
     */
    @GetMapping("/getSkuIdAndSaleAttrValues/{spuId}")
    @GmallCache(prefix = "getSkuIdAndSaleAttrValues:")
    public Map getSkuIdAndSaleAttrValues(@PathVariable("spuId") Long spuId){
        Map map = itemService.getSkuIdAndSaleAttrValues(spuId);
        return map;
    }

    /**
     * 查询品牌详情
     * @param id
     * @return
     */
    @GetMapping("/getBaseTrademark/{id}")
    @GmallCache(prefix = "getBaseTrademark:")
    public BaseTrademark getBaseTrademark(@PathVariable("id") Long id){
        BaseTrademark baseTrademark = itemService.getBaseTrademark(id);
        return baseTrademark;
    }

    /**
     *  根据skuId查询平台属性列表
     * @param skuId
     * @return
     */
    @GetMapping("/getBaseAttrInfo/{skuId}")
    @GmallCache(prefix = "getBaseAttrInfo:")
    public List<BaseAttrInfo> getBaseAttrInfo(@PathVariable("skuId") Long skuId){
        List<BaseAttrInfo> baseAttrInfo = itemService.getBaseAttrInfo(skuId);
        return baseAttrInfo;
    }

    /**
     * 扣减库存
     * @param decountMap
     * @return
     */
    @GetMapping("/decountStock")
    public Boolean decountStock(@RequestParam Map<String,Object> decountMap){
        Boolean flage = itemService.decountStock(decountMap);
        return flage;
    }

    /**
     * 回滚库存
     * @param rollbackMap
     * @return
     */
    @GetMapping("/rollbackStock")
    public Boolean rollbackStock(@RequestParam Map<String, Object> rollbackMap){
        Boolean stock = itemService.rollbackStock(rollbackMap);
        return stock;
    }
}
