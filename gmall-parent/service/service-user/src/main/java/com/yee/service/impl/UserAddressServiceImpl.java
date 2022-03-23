package com.yee.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yee.gmall.model.user.UserAddress;
import com.yee.mapper.UserAddressMapper;
import com.yee.service.UserAddressService;
import com.yee.util.UserThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ClassName: UserAddressServiceImpl
 * Description:
 * date: 2022/2/25 21:52
 *
 * @author Yee
 * @since JDK 1.8
 */
@Service
public class UserAddressServiceImpl implements UserAddressService {
    @Autowired
    private UserAddressMapper userAddressMapper;
    /**
     * 根据用户名获取收货地址
     * @return
     */
    @Override
    public List<UserAddress> getUserAddress() {
        String username = UserThreadLocalUtil.get();
        List<UserAddress> list = userAddressMapper.selectList(
                new LambdaQueryWrapper<UserAddress>()
                        .eq(UserAddress::getUserId, username));
        return list;
    }
}
