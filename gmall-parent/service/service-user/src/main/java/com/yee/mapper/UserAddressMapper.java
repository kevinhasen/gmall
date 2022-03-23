package com.yee.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yee.gmall.model.user.UserAddress;
import org.apache.ibatis.annotations.Mapper;

/**
 * ClassName: UserAddressMapper
 * Description:
 * date: 2022/2/25 21:49
 *  用户收货地址表的mapper映射
 * @author Yee
 * @since JDK 1.8
 */
@Mapper
public interface UserAddressMapper extends BaseMapper<UserAddress> {
}
