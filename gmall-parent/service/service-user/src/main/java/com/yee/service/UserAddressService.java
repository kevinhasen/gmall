package com.yee.service;

import com.yee.gmall.model.user.UserAddress;

import java.util.List;

/**
 * ClassName: UserAddressService
 * Description:
 * date: 2022/2/25 21:50
 * 用户收货地址信息管理的接口类
 * @author Yee
 * @since JDK 1.8
 */
public interface UserAddressService {

    /**
     * 根据用户名查询用户的收货地址信息
     * @return
     */
    public List<UserAddress> getUserAddress();
}
