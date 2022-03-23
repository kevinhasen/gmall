package com.yee.service;

import com.yee.gmall.model.cart.CartInfo;

import java.util.List;
import java.util.Map;

/**
 * ClassName: CartInfoService
 * Description:
 * date: 2022/2/26 19:39
 * 购物车管理接口
 * @author Yee
 * @since JDK 1.8
 */
public interface CartInfoService {

    /**
     * 新增购物车
     * @param skuId  商品
     * @param skuNum 商品数量
     */
    public void addCartInfo(Long skuId,Integer skuNum);

    /**
     * 根据用户名查询购物车数据
     * @return
     */
    public List<CartInfo> getCartInfo();

    /**
     * 修改选中状态
     * @param id
     * @param status 1是选中,0是取消选中
     */
    public void updateCartInfo(Long id,Short status);

    /**
     * 根据id删除购物车
     * @param id
     */
    public void delCartInfo(Long id);

    /**
     * 合并购物车
     * @param cartInfos
     */
    public void mergeCartInfo(List<CartInfo> cartInfos);


    /**
     * 查询订单确认页面的信息
     * @return
     */
    public Map<String, Object> getOrderConfirm();

    /**
     * 清空购物车
     * @return
     */
    public Boolean removeCart();
}
