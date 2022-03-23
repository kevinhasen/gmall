package com.yee.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yee.gmall.model.product.*;

import java.util.List;

/**
 * ClassName: ManageService
 * Description:
 * date: 2022/2/14 17:54
 * 管理控制台的Service接口
 * @author Yee
 * @since JDK 1.8
 */
public interface ManageService {


    /**
     * 查询所有的一级分类
     * @return
     */
    public List<BaseCategory1> getCategory1();

    /**
     * 查询所有的二级分类
     * @return
     */
    public List<BaseCategory2> getCategory2(Long categoryId2);

    /**
     * 查询所有的三级分类
     * @return
     */
    public List<BaseCategory3> getCategory3(Long categoryId3);


    /**
     * 保存平台属性
     * @param baseAttrInfo
     */
    public void saveBaseAttrInfo(BaseAttrInfo baseAttrInfo);

    /**
     * 根据分类自动查找平台属性
     * @param category1Id 一级分类
     * @param category2Id 二级分类
     * @param category3Id 三级分类
     * @return
     */
    public List<BaseAttrInfo> attrInfoList(Long category1Id,
                                           Long category2Id,
                                           Long category3Id);

    /**
     * 根据分类id回显属性
     * @param id
     * @return
     */
    public List<BaseAttrValue> getAttrValueList(Long id);


    /**
     * 获得所有品牌列表
     * @return
     */
    public List<BaseTrademark> getTrademarkList();

    /**
     * 获得所有销售属性
     * @return
     */
    public List<BaseSaleAttr> getSaleAttrList();


    /**
     * 保存spu的信息
     * @param spuInfo
     */
    public void saveSpuInfo(SpuInfo spuInfo);

    /**
     * 根据三级分类查询spu分页
     * @param page
     * @param size
     * @param category3Id
     * @return
     */
    public IPage<SpuInfo> getSpuInfoList(Integer page,
                                         Integer size,
                                         long category3Id);

    /**
     *   根据spuId查找属性和属性值
     * @param spuId
     * @return
     */
    public List<SpuSaleAttr> getSpuSaleAttr(Long spuId);

    /**
     *   根据spuId查找图片
     * @param spuId
     * @return
     */
    public List<SpuImage> getspuImageList(Long spuId);

    /**
     * 保存sku信息
     * @param skuInfo
     */
    public void saveSkuInfo(SkuInfo skuInfo);

    /**
     * skuInfo获得分页
     * @param page
     * @param size
     * @return
     */
    public IPage<SkuInfo> getSkuInfoList(Integer page,Integer size);


    /**
     * 商品上架或下架
     * @param skuId
     * @param status 1上架,0下架
     */
    public void upOrDown(Long skuId,Short status);

    /**
     * 品牌列表获得分页
     * @param page
     * @param size
     * @return
     */
    public IPage<BaseTrademark> baseTrademark(Integer page,Integer size);
}
