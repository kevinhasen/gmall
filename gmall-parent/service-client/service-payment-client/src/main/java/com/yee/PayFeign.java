package com.yee;

import com.yee.gmall.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * ClassName: PayFeign
 * Description:
 * date: 2022/3/3 21:20
 *
 * @author Yee
 * @since JDK 1.8
 */
@FeignClient( value= "service-payment", path = "/api/pay/wx")
public interface PayFeign {

    /**
     * 关闭交易
     * @param orderId
     * @return
     */
    @GetMapping(value = "/closePay")
    public Result closePay(String orderId);
}
