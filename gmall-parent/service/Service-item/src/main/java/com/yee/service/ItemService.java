package com.yee.service;

import java.util.Map;

/**
 * ClassName: ItemService
 * Description:
 * date: 2022/2/17 17:55
 * 商品详情微服务数据整合接口
 * @author Yee
 * @since JDK 1.8
 */
public interface ItemService {

    /**
     * 获得商品详情页的所有数据
     * @param skuId
     * @return
     */
    public Map<String, Object> getSkuItem(Long skuId);


}
