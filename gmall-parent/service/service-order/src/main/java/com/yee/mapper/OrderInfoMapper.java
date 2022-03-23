package com.yee.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yee.gmall.model.order.OrderInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * ClassName: OrderInfoMapper
 * Description:
 * date: 2022/2/28 19:43
 * 订单表
 * @author Yee
 * @since JDK 1.8
 */
@Mapper
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {
}
