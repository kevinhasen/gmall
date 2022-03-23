package com.yee.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yee.gmall.model.product.*;
import com.yee.mapper.*;
import com.yee.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ClassName: ItemServiceImpl
 * Description:
 * date: 2022/2/17 18:35
 * 商品微服务内部调用接口
 * @author Yee
 * @since JDK 1.8
 */
@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private SkuInfoMapper skuInfoMapper;
    @Autowired
    private BaseCategoryViewMapper baseCategoryViewMapper;
    @Autowired
    private SkuImageMapper skuImageMapper;
    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;
    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;
    @Autowired
    private BaseTrademarkMapper baseTrademarkMapper;
    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;


    /**
     * 获得sku信息
     *
     * @param skuId
     * @return
     */
    @Override
    public SkuInfo getSkuInfo(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        return skuInfo;
    }

    /**
     * 根据三级分类id查找二级和一级分类
     *
     * @param category3Id
     * @return
     */
    @Override
    public BaseCategoryView getCategory(Long category3Id) {
        BaseCategoryView baseCategoryView = baseCategoryViewMapper.selectById(category3Id);
        return baseCategoryView;
    }

    /**
     * 根据skuId查询图片
     *
     * @param skuId
     * @return
     */
    @Override
    public List<SkuImage> getSkuImageList(Long skuId) {
        List<SkuImage> list = skuImageMapper.selectList(new LambdaQueryWrapper<SkuImage>()
                .eq(SkuImage::getSkuId, skuId));
        return list;
    }

    /**
     * 根据skuId查询价格
     *
     * @param skuId
     * @return
     */
    @Override
    public BigDecimal getPrice(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        return skuInfo.getPrice();
    }

    /**
     * 根据spuId和skuID查找属性和属性值,并且指定选中的值
     *
     * @param skuId
     * @param spuId
     * @return
     */
    @Override
    public List<SpuSaleAttr> getSpuSaleAttr(Long skuId, Long spuId) {
        List<SpuSaleAttr> list = spuSaleAttrMapper.selectSpuSaleAttrBySpuIdAndSkuId(spuId, skuId);
        return list;
    }

    /**
     * 根据spu获得spu下的所有skuId和属性值键值对
     *
     * @param spuId
     * @return
     */
    @Override
    public Map getSkuIdAndSaleAttrValues(Long spuId) {
        //使用线程安全的map
        Map result = new ConcurrentHashMap();
        List<Map> maps = skuSaleAttrValueMapper.selectSkuIdAndSaleAttrValues(spuId);
        maps.stream().forEach(map -> {
            Object skuId = map.get("sku_id");
            Object valuesId = map.get("values_id");
            result.put(valuesId,skuId);
        });
        return result;
    }

    /**
     * 查询品牌详情
     *
     * @param skuId
     * @return
     */
    @Override
    public BaseTrademark getBaseTrademark(Long skuId) {
        BaseTrademark baseTrademark = baseTrademarkMapper.selectById(skuId);
        return baseTrademark;
    }

    /**
     * 根据skuId查询平台属性列表
     *
     * @param skuId
     * @return
     */
    @Override
    public List<BaseAttrInfo> getBaseAttrInfo(Long skuId) {
        List<BaseAttrInfo> baseAttrInfos = baseAttrInfoMapper.selectBaseAttrInfoBySkuId(skuId);
        return baseAttrInfos;
    }

    /**
     * 扣减库存
     *
     * @param decountMap
     * @return
     */
    @Override
    public Boolean decountStock(Map<String, Object> decountMap) {
        decountMap.entrySet().stream().forEach(entry ->{
            //获取商品id
            String key = entry.getKey();
            Long skuId = Long.parseLong(key);
            Object value = entry.getValue();
            Integer skuNum = Integer.parseInt(value.toString());
            int stock = skuInfoMapper.decountStock(skuId, skuNum);
            if (stock <= 0){
                throw new RuntimeException("扣减库存失败");
            }
            //以下逻辑在并发的模式下会出现超卖问题
//            SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
//            if (skuInfo == null || skuInfo.getId() == null){
//                throw new RuntimeException("扣减库存失败");
//            }
//            //减去库存之前判断是否大于0
//            skuNum = skuInfo.getStock() - skuNum;
//            if (skuNum >= 0){
//                skuInfo.setStock(skuNum);
//                int update = skuInfoMapper.updateById(skuInfo);
//                if (update <= 0){
//                    throw new RuntimeException("扣减库存失败");
//                }
//            }else {
//                throw new RuntimeException("扣减库存失败");
//            }

        });
        return true;
    }

    /**
     * 回滚库存
     *
     * @param rollbackMap
     * @return
     */
    @Override
    public Boolean rollbackStock(Map<String, Object> rollbackMap) {
        //遍历map
        rollbackMap.entrySet().stream().forEach(entry -> {
            //获取商品的id
            String key = entry.getKey();
            Long skuId = Long.parseLong(key);
            //获取回滚的数量
            Object value = entry.getValue();
            Integer num = Integer.parseInt(value.toString());
            //回滚库存
            int i = skuInfoMapper.rollbackStock(skuId, num);
            if(i <= 0){
                throw new RuntimeException("扣减库存失败");
            }
        });
        return true;
    }


}
