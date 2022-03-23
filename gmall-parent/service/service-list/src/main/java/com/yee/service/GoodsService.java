package com.yee.service;

/**
 * ClassName: GoodsService
 * Description:
 * date: 2022/2/22 16:45
 * es商品管理服务层
 * @author Yee
 * @since JDK 1.8
 */
public interface GoodsService {
    /**
     * 同步商品到es
     * @param skuId
     */
    public void addGoodsIntoEs(Long skuId);

    /**
     * es删除商品
     * @param skuId
     */
    public void removeGoodsFromEs(Long skuId);
}
