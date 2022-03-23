package com.yee.service.impl;

import com.yee.gmall.common.constant.ProductConst;
import com.yee.gmall.model.product.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yee.mapper.*;
import com.yee.service.ManageService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.List;

/**
 * ClassName: ManageServiceImpl
 * Description:
 * date: 2022/2/14 17:57
 * 管理控制台接口实现类,控制台使用的接口
 * @author Yee
 * @since JDK 1.8
 */
@Service
//如果有异常则回滚
@Transactional(rollbackFor = Exception.class)
public class ManageServiceImpl implements ManageService {


    // 一级分类
    @Autowired
    private BaseCategory1Mapper baseCategory1Mapper;
    //二级分类
    @Autowired
    private BaseCategory2Mapper baseCategory2Mapper;
    //三级分类
    @Autowired
    private BaseCategory3Mapper baseCategory3Mapper;
    // 平台属性
    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;
    //平台属性值
    @Autowired
    private BaseAttrValueMapper baseAttrValueMapper;
    //品牌列表mapper
    @Autowired
    private BaseTrademarkMapper baseTrademarkMapper;
    // 销售属性mapper
    @Autowired
    private BaseSaleAttrMapper baseSaleAttrMapper;
    //保存spu图片
    @Autowired
    private SpuImageMapper spuImageMapper;
    //保存spu的信息
    @Autowired
    private SpuInfoMapper spuInfoMapper;
    //保存spu销售属性
    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;
    //保存spu销售属性值
    @Autowired
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;
    //sku属性值
    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper;
    //sku图片
    @Autowired
    private SkuImageMapper skuImageMapper;
    //sku信息
    @Autowired
    private SkuInfoMapper skuInfoMapper;
    //sku销售属性值
    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    /**
     * 查询所有的一级分类
     *
     * @return
     */
    @Override
    public List<BaseCategory1> getCategory1() {
        List<BaseCategory1> list = baseCategory1Mapper.selectList(null);
        return list;
    }

    /**
     * 查询所有的二级分类
     *
     * @param categoryId2
     * @return
     */
    @Override
    public List<BaseCategory2> getCategory2(Long categoryId2) {
        LambdaQueryWrapper<BaseCategory2> wrapper = new LambdaQueryWrapper<BaseCategory2>().
                eq(BaseCategory2::getCategory1Id, categoryId2);
        List<BaseCategory2> list = baseCategory2Mapper.selectList(wrapper);
        return list;
    }

    /**
     * 查询所有的三级分类
     *
     * @param categoryId3
     * @return
     */
    @Override
    public List<BaseCategory3> getCategory3(Long categoryId3) {
        LambdaQueryWrapper<BaseCategory3> wrapper = new LambdaQueryWrapper<BaseCategory3>().
                eq(BaseCategory3::getCategory2Id, categoryId3);
        List<BaseCategory3> list = baseCategory3Mapper.selectList(wrapper);
        return list;
    }

    /**
     * 保存平台属性
     *
     * @param baseAttrInfo
     */
    @Override
    public void saveBaseAttrInfo(BaseAttrInfo baseAttrInfo) {
        //参数校验
        if (baseAttrInfo == null){
            throw new RuntimeException("参数错误");
        }
        //判断是新增还是修改
        if (baseAttrInfo.getId() != null){
            int i = baseAttrInfoMapper.updateById(baseAttrInfo);
            if (i <= 0 ){
                throw new RuntimeException("修改失败");
            }
            //删除旧数据
            int delete = baseAttrValueMapper.delete(
                    new LambdaQueryWrapper<BaseAttrValue>()
                            .eq(BaseAttrValue::getAttrId, baseAttrInfo.getId()));
            //小于0是因为会有没数据的情况,所以不能等于0
            if (delete <0 ){
                throw new RuntimeException("删除旧数据失败");
            }
        }else {
            //保存平台属性名称表
            int insert = baseAttrInfoMapper.insert(baseAttrInfo);
            if (insert <=0 ){
                throw new RuntimeException("新增失败");
            }
        }

        //获得id
        Long id = baseAttrInfo.getId();
        //平台信息保存到数据库
        baseAttrInfo.getAttrValueList().stream().forEach(baseAttrValue -> {
            baseAttrValue.setAttrId(id);
            int i = baseAttrValueMapper.insert(baseAttrValue);
            if (i <=0 ){
                throw new RuntimeException("新增失败");
            }
        });
    }

    /**
     * 根据分类自动查找平台属性
     *
     * @param category1Id 一级分类
     * @param category2Id 二级分类
     * @param category3Id 三级分类
     * @return
     */
    @Override
    public List<BaseAttrInfo> attrInfoList(Long category1Id,
                                           Long category2Id,
                                           Long category3Id) {
        List<BaseAttrInfo> list = baseAttrInfoMapper.selectBaseAttrInfoByCategoryId(category1Id, category2Id, category3Id);
        return list;
    }

    /**
     * 根据分类id回显属性
     *
     * @param id
     * @return
     */
    @Override
    public List<BaseAttrValue> getAttrValueList(Long id) {
        List<BaseAttrValue> list = baseAttrValueMapper.selectList(
                new LambdaQueryWrapper<BaseAttrValue>()
                        .eq(BaseAttrValue::getAttrId, id));
        return list;
    }

    /**
     * 获得所有品牌列表
     *
     * @return
     */
    @Override
    public List<BaseTrademark> getTrademarkList() {
        List<BaseTrademark> list = baseTrademarkMapper.selectList(null);
        return list;
    }

    /**
     * 获得所有销售属性
     *
     * @return
     */
    @Override
    public List<BaseSaleAttr> getSaleAttrList() {
        List<BaseSaleAttr> list = baseSaleAttrMapper.selectList(null);
        return list;
    }

    /**
     * 保存或更新spu的信息
     *
     * @param spuInfo
     */
    @Override
    public void saveSpuInfo(SpuInfo spuInfo) {
        //参数校验
        if (spuInfo == null){
            throw new RuntimeException("参数异常");
        }
        //保存info表的信息
        if (spuInfo.getId() == null){
            int insert = spuInfoMapper.insert(spuInfo);
            if (insert <= 0 ){
                throw new RuntimeException("保存失败");
            }
        }else {
            //修改info表的信息
            int update = spuInfoMapper.updateById(spuInfo);
            if (update <= 0){
                throw new RuntimeException("修改失败");
            }
            //删除旧的数据
            spuImageMapper.delete(
                    new LambdaQueryWrapper<SpuImage>()
                            .eq(SpuImage::getSpuId,spuInfo.getId()));
            spuSaleAttrMapper.delete(new LambdaQueryWrapper<SpuSaleAttr>()
            .eq(SpuSaleAttr::getSpuId,spuInfo.getId()));
            spuSaleAttrValueMapper.delete(new LambdaQueryWrapper<SpuSaleAttrValue>()
                    .eq(SpuSaleAttrValue::getSpuId,spuInfo.getId()));
        }
        //保存图片
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        saveSpuImageList(spuInfo.getId(),spuImageList);
        //保存销售属性的信息
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        saveSpuSaleAttrList(spuInfo.getId(),spuSaleAttrList);


    }

    /**
     * 根据三级分类查询spu分页
     *  @param page  当前页
     * @param size  显示页数
     * @param category3Id 要查询的三级分类id
     * @return
     */
    @Override
    public IPage<SpuInfo> getSpuInfoList(Integer page, Integer size, long category3Id) {
        LambdaQueryWrapper<SpuInfo> wrapper = new LambdaQueryWrapper<SpuInfo>().eq(SpuInfo::getCategory3Id, category3Id);
        IPage<SpuInfo> iPage = new Page<>(page,size);
        IPage<SpuInfo> infoIPage = spuInfoMapper.selectPage(iPage, wrapper);
        return infoIPage;
    }

    /**
     * 根据spuId查找属性和属性值
     *
     * @param spuId
     * @return
     */
    @Override
    public List<SpuSaleAttr> getSpuSaleAttr(Long spuId) {
        List<SpuSaleAttr> list = spuSaleAttrMapper.selectSpuSaleAttrBySpuId(spuId);
        return list;
    }

    /**
     * 根据spuId查找图片
     *
     * @param spuId
     * @return
     */
    @Override
    public List<SpuImage> getspuImageList(Long spuId) {
        List<SpuImage> list = spuImageMapper.selectList(
                new LambdaQueryWrapper<SpuImage>()
                        .eq(SpuImage::getSpuId, spuId));
        return list;
    }

    /**
     * 保存sku信息
     *
     * @param skuInfo
     */
    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {
        //参数校验
        if(skuInfo == null){
            throw new RuntimeException("参数错误");
        }
        //判断skuId是否为空
        Long skuId = skuInfo.getId();
        if (skuId != null){
            //不为空则修改
            int update = skuInfoMapper.updateById(skuInfo);
            if (update <= 0){
                throw new RuntimeException("修改sku错误");
            }
            //删除旧数据
            skuImageMapper.delete(
                    new LambdaQueryWrapper<SkuImage>()
                            .eq(SkuImage::getSkuId, skuId));
            skuAttrValueMapper.delete(
                    new LambdaQueryWrapper<SkuAttrValue>()
                            .eq(SkuAttrValue::getSkuId, skuId));
            skuSaleAttrValueMapper.delete(
                    new LambdaQueryWrapper<SkuSaleAttrValue>()
                            .eq(SkuSaleAttrValue::getSkuId, skuId));
        }else {
            //保存sku
            int insert = skuInfoMapper.insert(skuInfo);
            if (insert <= 0){
                throw new RuntimeException("保存sku信息失败");
            }
        }
        //保存sku图片
        saveSkuImageList(skuInfo.getId(),skuInfo.getSkuImageList());
        //保存sku平台属性
        saveAttrValueList(skuInfo.getId(), skuInfo.getSkuAttrValueList());
        //保存sku销售属性
        saveSaleAttrValueList(skuInfo.getId(),skuInfo.getSkuSaleAttrValueList(), skuInfo.getSpuId());
    }

    /**
     * skuInfo获得分页
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public IPage<SkuInfo> getSkuInfoList(Integer page, Integer size) {
        IPage<SkuInfo> iPage = skuInfoMapper.selectPage(new Page<>(page, size), null);
        return iPage;
    }

    /**
     * 商品上架或下架
     *
     * @param skuId
     * @param status 1上架,0下架
     */
    @Override
    public void upOrDown(Long skuId, Short status) {

        //参数校验
        if (skuId == null || status == null){
            throw  new RuntimeException("参数错误");
        }
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        //判断是否有数据
        if (skuInfo == null || skuInfo.getId() == null){
            throw  new RuntimeException("查询数据失败");
        }
        //设置商品上下架状态
        skuInfo.setIsSale(status);
        //修改商品
        skuInfoMapper.updateById(skuInfo);
        //上架商品写入es
        if (status.equals(ProductConst.SKU_ON_SALE)){
            rabbitTemplate.convertAndSend("list_exchange"
                    ,"sku.upper",skuId+"");
        }else {
            //下架商品移除es
            rabbitTemplate.convertAndSend("list_exchange"
                    ,"sku.down",skuId+"");
        }

    }

    /**
     * 品牌列表获得分页
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public IPage<BaseTrademark> baseTrademark(Integer page, Integer size) {
        IPage<BaseTrademark> iPage = baseTrademarkMapper.selectPage(new Page<>(page, size), null);
        return iPage;
    }

    /**
     * 保存sku销售属性
     * @param skuId
     * @param skuSaleAttrValueList
     */
    private void saveSaleAttrValueList(Long skuId, List<SkuSaleAttrValue> skuSaleAttrValueList,long spuId) {
        skuSaleAttrValueList.stream().forEach(skuSaleAttrValue -> {
            //补全sku的id
            skuSaleAttrValue.setSkuId(skuId);
            //补全spu的id
            skuSaleAttrValue.setSpuId(spuId);
            //新增
            int insert = skuSaleAttrValueMapper.insert(skuSaleAttrValue);
            if(insert <= 0){
                throw new RuntimeException("保存sku的销售属性失败");
            }
        });
    }

    /**
     * 保存sku平台属性
     * @param skuId
     * @param skuAttrValueList
     */
    private void saveAttrValueList(Long skuId, List<SkuAttrValue> skuAttrValueList) {
        skuAttrValueList.stream().forEach(skuAttrValue -> {
            //补全sku的id
            skuAttrValue.setSkuId(skuId);
            //保存
            int insert = skuAttrValueMapper.insert(skuAttrValue);
            if(insert <= 0){
                throw new RuntimeException("保存sku的平台属性失败");
            }
        });
    }

    /**
     * 保存sku图片
     * @param skuId
     * @param skuImageList
     */
    private void saveSkuImageList(Long skuId, List<SkuImage> skuImageList) {
        skuImageList.stream().forEach(skuImage -> {
            //补全sku的id
            skuImage.setSkuId(skuId);
            //新增sku的图片
            int insert = skuImageMapper.insert(skuImage);
            if(insert <= 0){
                throw new RuntimeException("保存sku的图片失败");
            }
        });
    }

    //保存销售属性的信息
    private void saveSpuSaleAttrList(Long spuId, List<SpuSaleAttr> spuSaleAttrList) {
        spuSaleAttrList.stream().forEach(spuSaleAttr -> {
            //设置spuId
            spuSaleAttr.setSpuId(spuId);
            //保存属性表
            int insert = spuSaleAttrMapper.insert(spuSaleAttr);
            if (insert <= 0){
                throw  new RuntimeException("保存属性失败");
            }
            //获得属性值
            List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();

            //保存属性值
            saveSpuSaleAttrValueList(spuId,spuSaleAttr.getSaleAttrName(),spuSaleAttrValueList);
        });
    }

    //保存属性值
    private void saveSpuSaleAttrValueList(Long spuId,String saleAttrName, List<SpuSaleAttrValue> spuSaleAttrValueList) {
        spuSaleAttrValueList.stream().forEach(spuSaleAttrValue -> {
            spuSaleAttrValue.setSpuId(spuId);
            spuSaleAttrValue.setSaleAttrName(saleAttrName);
            //保存属性值
            int insert = spuSaleAttrValueMapper.insert(spuSaleAttrValue);
            if (insert <= 0){
                throw  new RuntimeException("保存属性值失败");
            }
        });
    }

    //保存图片
    private void saveSpuImageList(Long spuId, List<SpuImage> spuImageList) {
        spuImageList.stream().forEach(spuImage -> {
            //补全spu的id
            spuImage.setSpuId(spuId);
            //保存图片信息
            int insert = spuImageMapper.insert(spuImage);
        });
    }
}
