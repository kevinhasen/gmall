package com.yee.service.impl;

import com.yee.ProductFeign;
import com.yee.dao.GoodsDao;
import com.yee.gmall.model.list.Goods;
import com.yee.gmall.model.list.SearchAttr;
import com.yee.gmall.model.product.BaseAttrInfo;
import com.yee.gmall.model.product.BaseCategoryView;
import com.yee.gmall.model.product.BaseTrademark;
import com.yee.gmall.model.product.SkuInfo;
import com.yee.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ClassName: GoodsServiceImpl
 * Description:
 * date: 2022/2/22 17:09
 * es商品管理实现类
 * @author Yee
 * @since JDK 1.8
 */
@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private ProductFeign productFeign;
    /**
     * 同步商品到es
     *
     * @param skuId
     */
    @Override
    public void addGoodsIntoEs(Long skuId) {
        //参数校验
        if (skuId == null){
            throw  new RuntimeException("参数错误");
        }
        //获得商品详情
        SkuInfo skuInfo = productFeign.getSkuInfo(skuId);
        if (skuInfo == null || skuInfo.getId() == null){
            throw  new RuntimeException("商品不存在");
        }
        //将skuInfo转为Goods对象
        Goods goods = new Goods();
        //商品id
        goods.setId(skuInfo.getId());
        //默认图片
        goods.setDefaultImg(skuInfo.getSkuDefaultImg());
        //商品名字
        goods.setTitle(skuInfo.getSkuName());
        //商品价格
        BigDecimal price = productFeign.getPrice(skuId);
        goods.setPrice(price.doubleValue());
        //创建时间
        goods.setCreateTime(new Date());
        //获得品牌详情
        BaseTrademark baseTrademark = productFeign.getBaseTrademark(skuId);
        //设置品牌id
        goods.setTmId(baseTrademark.getId());
        //设置品牌名字
        goods.setTmName(baseTrademark.getTmName());
        //设置品牌路径
        goods.setTmLogoUrl(baseTrademark.getLogoUrl());
        //查询分类信息
        Long category3Id = skuInfo.getCategory3Id();
        BaseCategoryView category = productFeign.getCategory(category3Id);
        goods.setCategory1Id(category.getCategory1Id());
        goods.setCategory1Name(category.getCategory1Name());
        goods.setCategory2Id(category.getCategory2Id());
        goods.setCategory2Name(category.getCategory2Name());
        goods.setCategory3Id(category.getCategory3Id());
        goods.setCategory3Name(category.getCategory3Name());
        //设置平台属性
        List<BaseAttrInfo> baseAttrInfoList = productFeign.getBaseAttrInfo(skuId);
        List<SearchAttr> searchAttrs = baseAttrInfoList.stream().map(baseAttrInfo -> {
            //初始化
            SearchAttr searchAttr = new SearchAttr();
            searchAttr.setAttrId(baseAttrInfo.getId());
            searchAttr.setAttrName(baseAttrInfo.getAttrName());
            searchAttr.setAttrValue(baseAttrInfo.getAttrValueList().get(0).getValueName());
            return searchAttr;
        }).collect(Collectors.toList());
        goods.setAttrs(searchAttrs);
        //保存es
        goodsDao.save(goods);
    }

    /**
     * es删除商品
     *
     * @param skuId
     */
    @Override
    public void removeGoodsFromEs(Long skuId) {
        goodsDao.deleteById(skuId);
    }
}
