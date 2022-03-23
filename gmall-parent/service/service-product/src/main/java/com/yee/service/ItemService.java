package com.yee.service;


import com.yee.gmall.model.product.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * ClassName: ItemService
 * Description:
 * date: 2022/2/17 18:34
 * 商品详情页微服务内部调用接口
 * @author Yee
 * @since JDK 1.8
 */
public interface ItemService {

    /**
     * 获得sku信息
     * @param skuId
     * @return
     */
    public SkuInfo getSkuInfo(Long skuId);

    /**
     * 根据三级分类id查找二级和一级分类
     * @param category3Id
     * @return
     */
    public BaseCategoryView getCategory(Long category3Id);

    /**
     * 根据skuId查询图片
     * @param skuId
     * @return
     */
    public List<SkuImage> getSkuImageList(Long skuId);

    /**
     * 根据skuId查询价格
     * @param skuId
     * @return
     */
    public BigDecimal getPrice(Long skuId);

    /**
     * 根据spuId和skuID查找属性和属性值,并且指定选中的值
     * @param skuId
     * @param spuId
     * @return
     */
    public List<SpuSaleAttr> getSpuSaleAttr(Long skuId,
                                            Long spuId);


    /**
     * 根据spu获得spu下的所有skuId和属性值键值对
     * @param spuId
     * @return
     */
    public Map getSkuIdAndSaleAttrValues(Long spuId);


    /**
     * 查询品牌详情
     * @param skuId
     * @return
     */
    public BaseTrademark getBaseTrademark(Long skuId);


    /**
     * 根据skuId查询平台属性列表
     * @param skuId
     * @return
     */
    public List<BaseAttrInfo> getBaseAttrInfo(Long skuId);

    /**
     * 扣减库存
     * @param decountMap
     * @return
     */
    public Boolean decountStock( Map<String, Object> decountMap);

    /**
     * 回滚库存
     * @param rollbackMap
     * @return
     */
    public Boolean rollbackStock( Map<String, Object> rollbackMap);
}
