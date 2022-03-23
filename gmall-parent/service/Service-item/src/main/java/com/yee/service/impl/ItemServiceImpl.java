package com.yee.service.impl;

import com.yee.ProductFeign;
import com.yee.config.ThreadPoolConfig;
import com.yee.gmall.model.product.BaseCategoryView;
import com.yee.gmall.model.product.SkuImage;
import com.yee.gmall.model.product.SkuInfo;
import com.yee.gmall.model.product.SpuSaleAttr;
import com.yee.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;


/**
 * ClassName: ItemServiceImpl
 * Description:
 * date: 2022/2/17 18:00
 * 商品详情页数据整合实现类
 * @author Yee
 * @since JDK 1.8
 */
@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ProductFeign productFeign;
    //线程池
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;
    /**
     * 获得商品详情页的所有数据
     *
     * @param skuId
     * @return
     */
    @Override
    public Map<String, Object> getSkuItem(Long skuId) {
        //参数校验
        if (skuId == null){
            throw new RuntimeException("商品不存在");
        }
        //线程安全的map
        Map<String,Object> result = new ConcurrentHashMap<>();
        CompletableFuture<SkuInfo> future1 = CompletableFuture.supplyAsync(() -> {
            //查询sku的信息
            SkuInfo skuInfo = productFeign.getSkuInfo(skuId);
            return skuInfo;
        },threadPoolExecutor);
        try {
            //判断是否存在
            SkuInfo info = future1.get();
            if (info == null || info.getId() == null) {
                throw new RuntimeException("商品不存在");
            }
            result.put("skuInfo", info);
            CompletableFuture<Void> future2 = future1.thenRunAsync(() -> {
                //获得id
                Long category3Id = info.getCategory3Id();
                //如果存在则获取三级分类信息
                BaseCategoryView baseCategoryView = productFeign.getCategory(category3Id);
                result.put("baseCategoryView", baseCategoryView);

            },threadPoolExecutor);
            CompletableFuture<Void> future3 = future1.thenRunAsync(() -> {
                //查询sku图片
                List<SkuImage> imageList = productFeign.getSkuImageList(info.getId());
                result.put("imageList", imageList);

            }, threadPoolExecutor);
            CompletableFuture<Void> future4 = future1.thenRunAsync(() -> {
                //查询sku价格
                BigDecimal price = productFeign.getPrice(info.getId());
                result.put("price", price);

            }, threadPoolExecutor);
            CompletableFuture<Void> future5 = future1.thenRunAsync(() -> {
                //销售属性名称和属性值和选中
                List<SpuSaleAttr> spuSaleAttr = productFeign.getSpuSaleAttr(info.getId(), info.getSpuId());
                result.put("spuSaleAttr", spuSaleAttr);

            },threadPoolExecutor);
            CompletableFuture<Void> future6 = future1.thenRunAsync(() -> {
                //跳转信息
                Map skuAndValuesMap = productFeign.getSkuIdAndSaleAttrValues(info.getSpuId());
                result.put("skuAndValuesMap", skuAndValuesMap);

            },threadPoolExecutor);
            //需要6个任务全部实行完才能返回结果
            CompletableFuture.allOf(future1,
                    future2,future3,future4,future5,future6)
                    .join();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
