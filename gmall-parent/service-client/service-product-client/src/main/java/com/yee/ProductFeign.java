package com.yee;

import com.yee.gmall.model.product.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * ClassName: com.yee.ProductFeign
 * Description:
 * date: 2022/2/17 19:01
 * 商品管理微服务的feign接口
 * @author Yee
 * @since JDK 1.8
 */
@FeignClient(value = "service-product",path = "/api/product")
public interface ProductFeign {

    /**
     * 根据skuId查询商品详情
     * @param skuId
     * @return
     */
    @GetMapping("/getSkuInfo/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable("skuId") Long skuId);


    /**
     * 根据三级分类ID查找二级一级分类
     * @param category3Id
     * @return
     */
    @GetMapping("/getCategory/{category3Id}")
    public BaseCategoryView getCategory(@PathVariable("category3Id") Long category3Id);


    /**
     * 根据skuId查询图片
     *
     * @param skuId
     * @return
     */
    @GetMapping("/getSkuImage/{skuId}")
    public List<SkuImage> getSkuImageList(@PathVariable("skuId") Long skuId);


    /**
     * 根据skuId查询价格
     *
     * @param skuId
     * @return
     */
    @GetMapping("/getPrice/{skuId}")
    public BigDecimal getPrice(@PathVariable("skuId") Long skuId);

    /**
     * 根据skuId和SPuID获得销售属性
     * @param skuId
     * @param spuId
     * @return
     */
    @GetMapping("/getSpuSaleAttr/{skuId}/{spuId}")
    public List<SpuSaleAttr> getSpuSaleAttr(@PathVariable("skuId") Long skuId,
                                            @PathVariable("spuId") Long spuId);


    /**
     * 根据spu获得spu下的所有skuId和属性值键值对
     * @param spuId
     * @return
     */
    @GetMapping("/getSkuIdAndSaleAttrValues/{spuId}")
    public Map getSkuIdAndSaleAttrValues(@PathVariable("spuId") Long spuId);

    /**
     * 查询品牌详情
     * @param id
     * @return
     */
    @GetMapping("/getBaseTrademark/{id}")
    public BaseTrademark getBaseTrademark(@PathVariable("id") Long id);


    /**
     *  根据skuId查询平台属性列表
     * @param skuId
     * @return
     */
    @GetMapping("/getBaseAttrInfo/{skuId}")
    public List<BaseAttrInfo> getBaseAttrInfo(@PathVariable("skuId") Long skuId);


    /**
     * 扣减库存
     * @param decountMap
     * @return
     */
    @GetMapping("/decountStock")
    public Boolean decountStock(@RequestParam Map<String,Object> decountMap);

    /**
     * 回滚库存
     * @param rollbackMap
     * @return
     */
    @GetMapping("/rollbackStock")
    public Boolean rollbackStock(@RequestParam Map<String, Object> rollbackMap);
}
