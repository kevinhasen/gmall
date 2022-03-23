package com.yee.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yee.gmall.model.product.BaseTrademark;
import org.apache.ibatis.annotations.Mapper;

/**
 * ClassName: BaseTrademarkMapper
 * Description:
 * date: 2022/2/16 15:53
 * 品牌列表mapper
 * @author Yee
 * @since JDK 1.8
 */
@Mapper
public interface BaseTrademarkMapper extends BaseMapper<BaseTrademark> {
}
