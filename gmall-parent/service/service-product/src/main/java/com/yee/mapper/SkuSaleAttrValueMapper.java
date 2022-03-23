package com.yee.mapper;

import com.yee.gmall.model.product.SkuSaleAttrValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * ClassName: SkuSaleAttrValueMapper
 * Description:
 * date: 2022/2/17 14:13
 * sku销售属性表
 * @author Yee
 * @since JDK 1.8
 */
@Mapper
public interface SkuSaleAttrValueMapper extends BaseMapper<SkuSaleAttrValue> {
    /**
     * 根据spu获得spu下的所有skuId和属性值键值对
     * @param spuId
     * @return
     */
    @Select("SELECT sku_id,GROUP_CONCAT(DISTINCT sale_attr_value_id " +
            "ORDER BY sale_attr_value_id SEPARATOR '|') as values_id " +
            "FROM sku_sale_attr_value WHERE spu_id = #{spuId} GROUP BY sku_id")
    public List<Map> selectSkuIdAndSaleAttrValues(@Param("spuId") Long spuId);
}
