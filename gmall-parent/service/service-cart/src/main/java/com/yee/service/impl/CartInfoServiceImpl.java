package com.yee.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.util.concurrent.AtomicDouble;
import com.yee.ProductFeign;
import com.yee.gmall.common.constant.CartConst;
import com.yee.gmall.model.cart.CartInfo;
import com.yee.gmall.model.product.SkuInfo;
import com.yee.mapper.CartInfoMapper;
import com.yee.service.CartInfoService;
import com.yee.util.CartThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * ClassName: CartInfoServiceImpl
 * Description:
 * date: 2022/2/26 19:40
 * 购物车管理实现类
 * @author Yee
 * @since JDK 1.8
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CartInfoServiceImpl implements CartInfoService {

    @Autowired
    private CartInfoMapper cartInfoMapper;
    @Autowired
    private ProductFeign productFeign;

    /**
     * @param username
     * @param skuId
     * @param skuNum
     */
    @Override
    public void addCartInfo(Long skuId, Integer skuNum) {
        //动态获取用户名
        String username = CartThreadLocalUtil.get();
        //校验参数
        if (skuId == null || skuNum == null){
            throw new RuntimeException("添加购物车失败,参数错误");
        }

        SkuInfo skuInfo = productFeign.getSkuInfo(skuId);
        if (skuInfo == null || skuInfo.getId() == null){
            throw new RuntimeException("添加购物车失败,商品不存在");
        }
        //判断购物车是否包含该商品
        CartInfo cartInfo = cartInfoMapper.selectOne(new LambdaQueryWrapper<CartInfo>()
                .eq(CartInfo::getUserId, username).eq(CartInfo::getSkuId, skuId));
        if (cartInfo == null || cartInfo.getId() == null){
            //判断数量是否合法
            if (skuNum <= 0){
                throw new RuntimeException("新增数量不能为负数");
            }
            //商品新增
            cartInfo = new CartInfo();
            //商品图片
            cartInfo.setImgUrl(skuInfo.getSkuDefaultImg());
            //商品名字
            cartInfo.setSkuName(skuInfo.getSkuName());
            //设置用户名id
            cartInfo.setUserId(username);
            //设置商品id
            cartInfo.setSkuId(skuId);
            //购物车的价格
            BigDecimal price = productFeign.getPrice(skuId);
            cartInfo.setCartPrice(price);
            //商品数量
            cartInfo.setSkuNum(skuNum);
            //添加购物车
            int insert = cartInfoMapper.insert(cartInfo);
            if (insert <= 0 ){
                throw new RuntimeException("添加购物车失败");
            }
        }else {
            skuNum = cartInfo.getSkuNum() + skuNum;
            //负数表示删除
            if (skuNum > 0){
                //商品数量加
                cartInfo.setSkuNum(skuNum);
                //更新数据
                int update = cartInfoMapper.updateById(cartInfo);
                if (update < 0 ){
                    throw new RuntimeException("数量更新失败");
                }
            }else {
               cartInfoMapper.deleteById(cartInfo.getId());
            }

        }

    }

    /**
     * 根据用户名查询购物车数据
     *
     * @param username
     * @return
     */
    @Override
    public List<CartInfo> getCartInfo() {
        //动态获取用户名
        String username = CartThreadLocalUtil.get();
        List<CartInfo> list = cartInfoMapper.selectList(new LambdaQueryWrapper<CartInfo>()
                .eq(CartInfo::getUserId, username));
        return list;
    }

    /**
     * 修改选中状态
     *
     * @param id
     * @param status   1是选中,0是取消选中
     * @param username
     */
    @Override
    public void updateCartInfo(Long id, Short status) {
        //动态获取用户名
        String username = CartThreadLocalUtil.get();
        int update = 0;
        //id为空表示全选或者取消全选
        if (id == null){
            update =  cartInfoMapper.updateCartInfo(status, username);
        }else {
            //选中或取消某一个
            update =  cartInfoMapper.updateCartInfoOne(status, id);
        }
        if (update < 0){
            throw new RuntimeException("修改购物车状态失败");
        }
    }

    /**
     * 根据id删除购物车
     *
     * @param id
     */
    @Override
    public void delCartInfo(Long id) {
        int delete = cartInfoMapper.deleteById(id);
    }

    /**
     * 合并购物车
     *
     * @param username
     * @param cartInfos
     */
    @Override
    public void mergeCartInfo(List<CartInfo> cartInfos) {
        //批量添加购物车
        cartInfos.stream().forEach(cartInfo -> {
            addCartInfo(cartInfo.getSkuId(),cartInfo.getSkuNum());
        });
    }

    /**
     * 查询订单确认页面的信息
     * @return
     */
    @Override
    public Map<String, Object> getOrderConfirm() {
        //获得当前用户名
        String username = CartThreadLocalUtil.get();
        //根据用户名和选中状态获得购物车数据
        List<CartInfo> cartInfos = cartInfoMapper.selectList(
                new LambdaQueryWrapper<CartInfo>()
                        .eq(CartInfo::getUserId, username)
                        .eq(CartInfo::getIsChecked, CartConst.CART_Checked));
        //没有购物车数据
        if (cartInfos.isEmpty()){
            return null;
        }
        //总数量
        AtomicInteger totalNum = new AtomicInteger(0);
        //总价格
        AtomicDouble totalMoney = new AtomicDouble(0);
        List<CartInfo> cartInfoList = cartInfos.stream().map(cartInfo -> {
            //获得商品ID
            Long skuId = cartInfo.getSkuId();
            //获得商品实际价格
            BigDecimal price = productFeign.getPrice(skuId);
            //存放实时价格
            cartInfo.setSkuPrice(price);
            //查询总数量
            Integer skuNum = cartInfo.getSkuNum();
            totalNum.getAndAdd(skuNum);
            //查询总价格,商品x数量等于当前商品价格
           Double total = price.doubleValue() * skuNum;
            totalMoney.getAndAdd(total);
            return cartInfo;
        }).collect(Collectors.toList());
        Map<String,Object> result = new HashMap<>();
        result.put("totalNum",totalNum);
        result.put("totalMoney",totalMoney);
        result.put("cartInfoList",cartInfoList);
        return result;
    }

    /**
     * 清空购物车
     *
     * @return
     */
    @Override
    public Boolean removeCart() {
        String username = CartThreadLocalUtil.get();
        int delete = cartInfoMapper.delete(new LambdaQueryWrapper<CartInfo>()
                .eq(CartInfo::getUserId, username).eq(CartInfo::getIsChecked, CartConst.CART_Checked));
        return delete > 0;
    }
}
