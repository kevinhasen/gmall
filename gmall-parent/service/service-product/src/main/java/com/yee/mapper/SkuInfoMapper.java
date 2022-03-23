package com.yee.mapper;

import com.yee.gmall.model.product.SkuInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * ClassName: SkuInfoMapper
 * Description:
 * date: 2022/2/17 14:12
 * sku核心表
 * @author Yee
 * @since JDK 1.8
 */
@Mapper
public interface SkuInfoMapper extends BaseMapper<SkuInfo> {

    /**
     * 扣减库存
     * @param skuId
     * @param skuNum
     * @return
     */
    @Update("update sku_info set stock = stock - #{skuNum} where id = #{skuId} and stock >= #{skuNum}")
    public int decountStock(@Param("skuId") Long skuId,
                            @Param("skuNum") Integer skuNum);


    /**
     * 回滚库存
     * @param skuId
     * @param num
     * @return
     */
    @Update("update sku_info set stock=stock+#{num} where id = #{skuId}")
    public int rollbackStock(@Param("skuId") Long skuId,
                             @Param("num") Integer num);
}
