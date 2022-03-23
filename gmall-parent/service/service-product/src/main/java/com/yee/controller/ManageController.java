package com.yee.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yee.gmall.common.constant.ProductConst;
import com.yee.gmall.common.result.Result;
import com.yee.gmall.model.product.*;
import com.yee.service.ManageService;
import com.yee.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * ClassName: ManageServiceController
 * Description:
 * date: 2022/2/14 17:58
 * 管理控制台的控制器
 * @author Yee
 * @since JDK 1.8
 */
@RestController
@RequestMapping("/admin/product")
public class ManageController {

    @Autowired
    private ManageService manageService;
    @Value("${fileServer.url}")
    private String fileUrl;

    //查询所有一级分类
    @GetMapping("/getCategory1")
    public Result getCategory1(){
        List<BaseCategory1> list = manageService.getCategory1();
        return Result.ok(list);
    }

    //查询所有二级分类
    @GetMapping("/getCategory2/{categoryId2}")
    public Result getCategory2(@PathVariable("categoryId2") Long categoryId2){
        List<BaseCategory2> list = manageService.getCategory2(categoryId2);
        return Result.ok(list);
    }

    //查询所有三级分类
    @GetMapping("/getCategory3/{categoryId3}")
    public Result getCategory3(@PathVariable("categoryId3") Long categoryId3){
        List<BaseCategory3> list = manageService.getCategory3(categoryId3);
        return Result.ok(list);
    }

    /**
     * 保存属性
     * @param baseAttrInfo
     * @return
     */
    @PostMapping("/saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo){
        manageService.saveBaseAttrInfo(baseAttrInfo);
        return Result.ok();
    }

    /**
     * 根据分类自动查找平台属性
     *
     * @param category1Id 一级分类
     * @param category2Id 二级分类
     * @param category3Id 三级分类
     * @return
     */
    @GetMapping("/attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    public Result attrInfoList(Long category1Id,
                                           Long category2Id,
                                           Long category3Id) {
        List<BaseAttrInfo> list = manageService.attrInfoList(category1Id, category2Id, category3Id);
        return Result.ok(list);
    }

    /**
     * 根据分类id回显属性
     * @param id
     * @return
     */
    @GetMapping("/getAttrValueList/{id}")
    public Result getAttrValueList(@PathVariable("id") Long id){
        List<BaseAttrValue> list = manageService.getAttrValueList(id);
        return Result.ok(list);
    }

    /**
     * 获得所有品牌列表
     * @return
     */
    @GetMapping("/baseTrademark/getTrademarkList")
    public Result getTrademarkList(){
        List<BaseTrademark> list = manageService.getTrademarkList();
        return Result.ok(list);
    }

    /**
     * 品牌列表分页
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/baseTrademark/{page}/{size}")
    public Result baseTrademark(@PathVariable("page") Integer page,
                                @PathVariable("size") Integer size){
        IPage<BaseTrademark> iPage = manageService.baseTrademark(page, size);
        return Result.ok(iPage);
    }

    /**
     * 获得所有销售属性
     * @return
     */
    @GetMapping("/baseSaleAttrList")
    public Result baseSaleAttrList(){
        List<BaseSaleAttr> list = manageService.getSaleAttrList();
        return Result.ok(list);
    }


    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/fileUpload")
    public Result fileUpload(MultipartFile file) {
        String path = FileUploadUtil.upload(file);
        return Result.ok(fileUrl+path);
    }

    /**
     * 保存spu信息
     * @param spuInfo
     * @return
     */
    @PostMapping("/saveSpuInfo")
    public Result saveSpuInfo(@RequestBody SpuInfo spuInfo){
        manageService.saveSpuInfo(spuInfo);
        return Result.ok();
    }

    /**
     * 根据三级分类查询spu分页
     * @param page
     * @param size
     * @param category3Id
     * @return
     */
    @GetMapping("/{page}/{size}")
    public Result getSpuInfoList(@PathVariable("page") Integer page,
                              @PathVariable("size") Integer size,
                              long category3Id) {
        IPage<SpuInfo> spuInfoList = manageService.getSpuInfoList(page, size, category3Id);
        return Result.ok(spuInfoList);
    }

    /**
     * 根据spuId查找平台属性
     * @param spuId
     * @return
     */
    @GetMapping("/spuSaleAttrList/{spuId}")
    public Result spuSaleAttrList(@PathVariable("spuId") long spuId){
        List<SpuSaleAttr> list = manageService.getSpuSaleAttr(spuId);
        return Result.ok(list);
    }


    /**
     * 根据spuId查找图片
     * @param spuId
     * @return
     */
    @GetMapping("/spuImageList/{spuId}")
    public Result spuImageList(@PathVariable("spuId") long spuId){
        List<SpuImage> list = manageService.getspuImageList(spuId);
        return Result.ok(list);
    }


    /**
     * 保存sku信息
     * @param skuInfo
     * @return
     */
    @PostMapping("/saveSkuInfo")
    public Result saveSkuInfo(@RequestBody SkuInfo skuInfo){
        manageService.saveSkuInfo(skuInfo);
        return Result.ok();
    }

    /**
     * sku信息分页
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/list/{page}/{size}")
    public Result getSkuInfoList(@PathVariable("page") Integer page,
                                 @PathVariable("size") Integer size){
        IPage<SkuInfo> list = manageService.getSkuInfoList(page, size);
        return Result.ok(list);
    }

    /**
     * 商品上架
     * @param skuId
     * @return
     */
    @GetMapping("/onSale/{skuId}")
    public Result onSale(@PathVariable("skuId") long skuId){
        manageService.upOrDown(skuId, ProductConst.SKU_ON_SALE);
        return Result.ok();
    }

    /**
     * 商品下架
     * @param skuId
     * @return
     */
    @GetMapping("/cancelSale/{skuId}")
    public Result cancelSale(@PathVariable("skuId") long skuId){
        manageService.upOrDown(skuId,ProductConst.SKU_CANCLE_SALE);
        return Result.ok();
    }


}
