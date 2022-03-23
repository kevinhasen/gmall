package com.yee.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yee.gmall.model.product.BaseAttrInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * ClassName: BaseAttrInfoMapper
 * Description:
 * date: 2022/2/14 19:56
 * 平台属性名称表
 * @author Yee
 * @since JDK 1.8
 */
@Mapper
public interface BaseAttrInfoMapper extends BaseMapper<BaseAttrInfo> {

    /**
     * 根据分类的id(一级二级三级)查询平台属性的信息
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    public List<BaseAttrInfo> selectBaseAttrInfoByCategoryId(@Param("category1Id") Long category1Id,
                                                             @Param("category2Id") Long category2Id,
                                                             @Param("category3Id") Long category3Id);

    /**
     * 根据sku的id查询平台属性的列表
     * @param skuId
     * @return
     */
    public List<BaseAttrInfo> selectBaseAttrInfoBySkuId(@Param("skuId") Long skuId);
}
