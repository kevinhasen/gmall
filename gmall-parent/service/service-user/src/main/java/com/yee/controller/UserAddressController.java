package com.yee.controller;

import com.yee.gmall.common.result.Result;
import com.yee.gmall.model.user.UserAddress;
import com.yee.service.UserAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * ClassName: UserAddressController
 * Description:
 * date: 2022/2/25 21:55
 * 用户收货地址的控制层
 * @author Yee
 * @since JDK 1.8
 */
@RestController
@RequestMapping("/api/user")
public class UserAddressController {

    @Autowired
    private UserAddressService userAddressService;


    /**
     * 查询用户收货地址信息
     * @return
     */
    @GetMapping("/getUserAddress")
    public Result getUserAddress(){
        List<UserAddress> list = userAddressService.getUserAddress();
        return Result.ok(list);
    }

}
