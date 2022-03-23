package com.yee.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yee.gmall.model.product.BaseAttrValue;
import org.apache.ibatis.annotations.Mapper;

/**
 * ClassName: BaseAttrValueMapper
 * Description:
 * date: 2022/2/14 20:18
 * 平台属性的值表
 * @author Yee
 * @since JDK 1.8
 */
@Mapper
public interface BaseAttrValueMapper extends BaseMapper<BaseAttrValue> {
}
