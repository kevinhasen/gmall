package com.yee.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yee.gmall.model.product.SpuInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * ClassName: spuInfoMapper
 * Description:
 * date: 2022/2/16 19:21
 * spuInfo表
 * @author Yee
 * @since JDK 1.8
 */
@Mapper
public interface SpuInfoMapper extends BaseMapper<SpuInfo> {
}
