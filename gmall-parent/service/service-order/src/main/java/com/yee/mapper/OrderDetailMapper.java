package com.yee.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yee.gmall.model.order.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

/**
 * ClassName: OrderDetailMapper
 * Description:
 * date: 2022/2/28 19:43
 * 订单详情表
 * @author Yee
 * @since JDK 1.8
 */
@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {
}
