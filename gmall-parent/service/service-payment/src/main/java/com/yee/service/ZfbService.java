package com.yee.service;

/**
 * ClassName: ZfbService
 * Description:
 * date: 2022/3/3 21:42
 *
 * @author Yee
 * @since JDK 1.8
 */
public interface ZfbService {

    /**
     * 获取支付宝下单页面
     * @param money
     * @param orderId
     * @param desc
     * @return
     */
    public String getPayUrl(String money, String orderId, String desc);

    /**
     * 查询订单的支付结果
     * @param orderId
     * @return
     */
    public String getPayResult(String orderId);

    /**
     * 关闭交易
     * @param orderId
     * @return
     */
    public void closePay(String orderId);
}
