package com.yee.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yee.gmall.model.product.BaseCategoryView;
import org.apache.ibatis.annotations.Mapper;

/**
 * ClassName: BaseCategoryViewMapper
 * Description:
 * date: 2022/2/17 20:01
 * 一二三级分类视图mapper
 * @author Yee
 * @since JDK 1.8
 */
@Mapper
public interface BaseCategoryViewMapper extends BaseMapper<BaseCategoryView> {
}
