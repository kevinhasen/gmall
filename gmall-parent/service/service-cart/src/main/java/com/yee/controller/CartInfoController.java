package com.yee.controller;

import com.yee.gmall.common.constant.CartConst;
import com.yee.gmall.common.result.Result;
import com.yee.gmall.model.cart.CartInfo;
import com.yee.service.CartInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * ClassName: CartInfoController
 * Description:
 * date: 2022/2/26 19:50
 * 新增购物车控制层
 * @author Yee
 * @since JDK 1.8
 */
@RestController
@RequestMapping("/api/cart")
public class CartInfoController {

    @Autowired
    private CartInfoService cartInfoService;


    /**
     * 新增购物车
     * @param skuId
     * @param skuNum
     * @return
     */
    @GetMapping("/addCart")
    public Result addCart(Long skuId, Integer skuNum){

        cartInfoService.addCartInfo(skuId,skuNum);
        return Result.ok();
    }

    /**
     * 根据用户名获得购物车数据
     * @return
     */
    @GetMapping("/getCartInfo")
    public Result getCartInfo(){
        List<CartInfo> cartInfo = cartInfoService.getCartInfo();
        return Result.ok(cartInfo);
    }

    /**
     * 选中:全选或只选择一个
     * @param id  有id就是选择单个,没有id则是全选
     * @return
     */
    @GetMapping("/checked")
    public Result checked(Long id){
        cartInfoService.updateCartInfo(id, CartConst.CART_Checked);
        return Result.ok();
    }

    /**
     * 选中:取消全选或只取消一个
     * @param id  有id就是取消单个,没有id则是取消全选
     * @return
     */
    @GetMapping("/cancleChecked")
    public Result cancleChecked(Long id){
        cartInfoService.updateCartInfo(id,CartConst.CART_CANCLE_Checked);
        return Result.ok();
    }

    /**
     * 删除购物车数据
     * @param id
     * @return
     */
    @GetMapping("/delCart")
    public Result delCart(Long id){
        cartInfoService.delCartInfo(id);
        return Result.ok();
    }

    /**
     * 合并购物车
     * @param cartInfos
     * @return
     */
    @PostMapping("/mergeCart")
    public Result mergeCart(@RequestBody List<CartInfo> cartInfos){
        cartInfoService.mergeCartInfo(cartInfos);
        return Result.ok();
    }

    /**
     * 查询订单确认页面的信息
     * @return
     */
    @GetMapping("/getOrderConfirm")
    public Result getOrderConfirm(){
        Map<String, Object> cartInfoMap = cartInfoService.getOrderConfirm();
        return Result.ok(cartInfoMap);
    }


    /**
     * 查询订单确认页面的信息内部调用
     * @return
     */
    @GetMapping("/getOrderAddInfo")
    public Map<String, Object> getOrderAddInfo(){
        Map<String, Object> cartInfoMap = cartInfoService.getOrderConfirm();
        return cartInfoMap;
    }

    /**
     * 生成订单后移除购物车数据
     * @return
     */
    @GetMapping("/removeCart")
    public Boolean removeCart(){
        Boolean flag = cartInfoService.removeCart();
        return flag;
    }
}
