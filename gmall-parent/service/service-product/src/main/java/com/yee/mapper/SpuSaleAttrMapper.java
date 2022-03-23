package com.yee.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import com.yee.gmall.model.product.SpuSaleAttr;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * ClassName: spuInfoMapper
 * Description:
 * date: 2022/2/16 19:21
 * SpuSaleAttr表
 * @author Yee
 * @since JDK 1.8
 */
@Mapper
public interface SpuSaleAttrMapper extends BaseMapper<SpuSaleAttr> {

    /**
     *   根据spuId查找属性和属性值
     * @param spuId
     * @return
     */
    public List<SpuSaleAttr> selectSpuSaleAttrBySpuId(@Param("spuId") Long spuId);

    /**
     *   根据spuId和skuID查找属性和属性值,并且指定选中的值
     * @param spuId
     * @return
     */
    public List<SpuSaleAttr> selectSpuSaleAttrBySpuIdAndSkuId(@Param("spuId") Long spuId,
                                                              @Param("skuId") Long skuId);

}
