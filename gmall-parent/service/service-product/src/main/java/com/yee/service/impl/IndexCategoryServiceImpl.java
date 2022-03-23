package com.yee.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yee.gmall.model.product.BaseCategoryView;
import com.yee.mapper.BaseCategoryViewMapper;
import com.yee.service.IndexCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ClassName: IndexCategoryServiceImpl
 * Description:
 * date: 2022/2/20 20:05
 *
 * @author Yee
 * @since JDK 1.8
 */
@Service
public class IndexCategoryServiceImpl implements IndexCategoryService {

    @Autowired
    private BaseCategoryViewMapper baseCategoryViewMapper;
    /**
     * 查询首页分类信息
     */
    @Override
    public List<JSONObject>  getIndexCategory() {
        //获得分类视图,一二级分类需要去重
        List<BaseCategoryView> baseCategoryViews1 = baseCategoryViewMapper.selectList(null);
        //一级分类id去重进行分桶
        Map<Long, List<BaseCategoryView>> category1Map = baseCategoryViews1.stream().collect(
                Collectors.groupingBy(BaseCategoryView::getCategory1Id));
        List<JSONObject> category1JsonList = new ArrayList<>();
        category1Map.entrySet().stream().forEach(categor1 -> {
            JSONObject category1Json = new JSONObject();
            //获得一级分类ID
            Long category1Id = categor1.getKey();
            category1Json.put("categoryId",category1Id);
            //获得所有二级三级分类信息
            List<BaseCategoryView> baseCategoryViews2 = categor1.getValue();
            //二级分类id去重进行分桶
            Map<Long, List<BaseCategoryView>> category2Map = baseCategoryViews2.stream().collect(
                    Collectors.groupingBy(BaseCategoryView::getCategory2Id));
            List<JSONObject> category2JsonList = new ArrayList<>();
            category2Map.entrySet().stream().forEach(categor2 -> {
                JSONObject category2Json = new JSONObject();
                //获得一级分类ID
                Long category2Id = categor2.getKey();
                category2Json.put("categoryId", category2Id);
                //获得所有二级三级分类信息
                List<BaseCategoryView> baseCategoryViews3 = categor2.getValue();
                //页面需要的数据三级分类id和分类名
                List<JSONObject> category3JsonList = baseCategoryViews3.stream().map(baseCategoryView -> {
                    JSONObject category3JS = new JSONObject();
                    //获得三级分类id
                    Long category3Id = baseCategoryView.getCategory3Id();
                    //获得三级分类名
                    String category3Name = baseCategoryView.getCategory3Name();
                    //保存数据
                    category3JS.put("categoryId", category3Id);
                    category3JS.put("categoryName", category3Name);
                    //返回结果
                    return category3JS;
                }).collect(Collectors.toList());
                //保存二级分类列表
                category2Json.put("childCategory",category3JsonList);
                //保存二级分类名字
                String category2Name = baseCategoryViews3.get(0).getCategory2Name();
                category2Json.put("categoryName",category2Name);
                //保存二级分类
                category2JsonList.add(category2Json);
            });
            //保存一级分类列表
            category1Json.put("childCategory",category2JsonList);
            //保存一级分类名字
            String category1Name = baseCategoryViews2.get(0).getCategory1Name();
            category1Json.put("categoryName", category1Name);
            //保存一级分类
            category1JsonList.add(category1Json);
        });
        return category1JsonList;
    }
}
