package com.yee.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yee.gmall.model.list.Goods;
import com.yee.gmall.model.list.SearchResponseAttrVo;
import com.yee.gmall.model.list.SearchResponseTmVo;
import com.yee.service.SearchService;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * ClassName: SearchServiceImpl
 * Description:
 * date: 2022/2/22 19:21
 *
 * @author Yee
 * @since JDK 1.8
 */
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;
    /**
     * 商品搜索
     *
     * @param searchData
     * @return
     */
    @Override
    public Map<String, Object> search(Map<String,String> searchData) {
        try {
            //拼接条件
            SearchRequest searchRequest = buildQueryParams(searchData);
            //执行查询
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            //解析结果
            Map<String, Object> result = getSearchResult(searchResponse);
            return result;
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 查询条件拼接
     * @param searchData
     * @return
     */
    private SearchRequest buildQueryParams(Map<String,String> searchData) {
        //搜索条件
        SearchRequest searchRequest = new SearchRequest("goods_java0107");
        //条件构造器
        SearchSourceBuilder builder = new SearchSourceBuilder();
        //组合查询条件
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        String keyword = searchData.get("keyword");
        if (!StringUtils.isEmpty(keyword)){
//            builder.query();
            boolQueryBuilder.must(QueryBuilders.matchQuery("title",keyword));
        }
        //品牌查询条件  2:华为
        String tradeMark = searchData.get("tradeMark");
        if (!StringUtils.isEmpty(tradeMark)){
            //获得品牌id
            String[] split = tradeMark.split(":");
            //词条查询
            boolQueryBuilder.must(QueryBuilders.termQuery("tmId",split[0]));
        }
        //平台属性构建
        for (Map.Entry<String, String> entry : searchData.entrySet()) {
            //查询条件的key
            String key = entry.getKey();
            //判断是否以attr_开头
            if (key.startsWith("attr_")){
                String value = entry.getValue();
                String[] split = value.split(":");
                //因为存在两个查询条件,所以使用组合查询
                BoolQueryBuilder nestedBoolQuery = QueryBuilders.boolQuery();
                //平台属性id要一致
                nestedBoolQuery.must(QueryBuilders.termQuery("attrs.attrId", split[0]));
                //平台属性值的值也要一致
                nestedBoolQuery.must(QueryBuilders.termQuery("attrs.attrValue", split[1]));
                //一对多使用nestedQuery
                boolQueryBuilder.must(QueryBuilders.nestedQuery("attrs", nestedBoolQuery, ScoreMode.None));
            }
        }
        //价格查询price=1000元以上 或者500-1000元
        String price = searchData.get("price");
        if(!StringUtils.isEmpty(price)){
            //价格处理--->price=1000 或者500-1000
            price = price.replace("元", "").replace("以上", "");
            //切分
            String[] split = price.split("-");
            //大于第一个值
            boolQueryBuilder.must(QueryBuilders.rangeQuery("price").gte(split[0]));
            //判断是否有第二个值
            if(split.length > 1){
                boolQueryBuilder.must(QueryBuilders.rangeQuery("price").lt(split[1]));
            }
        }
        //设置全部查询条件
        builder.query(boolQueryBuilder);
        //设置排序的信息
        String softRule = searchData.get("softRule");
        String softField = searchData.get("softField");
        if(!StringUtils.isEmpty(softRule) &&
                !StringUtils.isEmpty(softField)){
            //指定排序
            builder.sort(softField, SortOrder.valueOf(softRule));
        }else{
            //默认排序
            builder.sort("id", SortOrder.DESC);
        }
        //设置每页返回数据量,固定返回100条
        builder.size(100);
        //获取页码
        String pageNum = searchData.get("pageNum");
        //判断页码
        Integer page = getPageNum(pageNum);
        //100是每页显示的数量
        builder.from((page - 1)*100);
        //品牌聚合条件
        builder.aggregation(
                AggregationBuilders.terms("aggTmId").field("tmId")
                .subAggregation( AggregationBuilders.terms("aggTmName").field("tmName"))
                .subAggregation( AggregationBuilders.terms("aggTmLogoUrl").field("tmLogoUrl"))
                .size(100)
        );
        //平台属性聚合,设置别名
        builder.aggregation(
                AggregationBuilders.nested("aggAttrs","attrs")
                .subAggregation(
                        AggregationBuilders.terms("aggAttrId").field("attrs.attrId")
                        .subAggregation( AggregationBuilders.terms("aggAttrName").field("attrs.attrName"))
                        .subAggregation( AggregationBuilders.terms("aggAttrValue").field("attrs.attrValue"))
                        .size(100)
                )
        );
        //设置高亮条件
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.preTags("<font style='color:red'>");
        highlightBuilder.postTags("</font>");
        builder.highlighter(highlightBuilder);
        //设置条件
        searchRequest.source(builder);
        return searchRequest;
    }

    /**
     * 判断页码
     * @param pageNum
     */
    private Integer getPageNum(String pageNum) {
        //默认显示第一页
        try {
            int i = Integer.parseInt(pageNum);
            return i>0?i:1;
        } catch (NumberFormatException e) {
            //默认显示第一页
            return 1;
        }

    }

    /**
     * 解析结果
     * @param searchResponse
     * @return
     */
    private Map<String,Object> getSearchResult(SearchResponse searchResponse) {
        //返回结果初始化
        Map<String,Object> result = new HashMap<>();
        //商品初始化
        List<Goods>  goodsList = new ArrayList<>();
        //获得命中数据
        SearchHits hits = searchResponse.getHits();
        //获得总数量
        long totalHits = hits.getTotalHits();
        result.put("totalHits",totalHits);
        //获取迭代器
        Iterator<SearchHit> iterator = hits.iterator();
        //遍历获取
        while (iterator.hasNext()){
            //获得数据
            SearchHit next = iterator.next();
            //取得数据
            String source = next.getSourceAsString();
            //反序列化
            Goods goods = JSONObject.parseObject(source, Goods.class);
            //获取这条数据的高亮数据
            HighlightField highlightField = next.getHighlightFields().get("title");
            if(highlightField != null){
                Text[] fragments = highlightField.getFragments();
                if(fragments != null && fragments.length > 0){
                    //遍历获取全部的高亮内容
                    String title = "";
                    for (Text fragment : fragments) {
                        title += fragment;
                    }
                    //使用高亮的数据替换原始的数据
                    goods.setTitle(title);
                }
            }
            goodsList.add(goods);
        }
        result.put("goodsList",goodsList);
        //获得聚合查询结果
        Aggregations aggregations = searchResponse.getAggregations();
        //品牌聚合结果
        List<SearchResponseTmVo> searchResponseTmVos = getTradeMarkAggResult(aggregations);
        result.put("searchResponseTmVos", searchResponseTmVos);
        //获取平台属性的聚合结果
        List<SearchResponseAttrVo> searchResponseAttrVos = getAttrAggResult(aggregations);
        result.put("searchResponseAttrVos", searchResponseAttrVos);
        return result;
    }

    /**
     * 平台属性聚合
     * @param aggregations
     */
    private List<SearchResponseAttrVo> getAttrAggResult(Aggregations aggregations) {
        //获取nested的attrs属性的聚合结果
        ParsedNested aggAttrs = aggregations.get("aggAttrs");
        //获取子聚合平台属性的id的聚合结果
        ParsedLongTerms aggAttrId = aggAttrs.getAggregations().get("aggAttrId");

        //遍历结果
        List<SearchResponseAttrVo> result = aggAttrId.getBuckets().stream().map(bucket -> {
            SearchResponseAttrVo searchResponseAttrVo = new SearchResponseAttrVo();
            //获得平台属性id
            long attrId = bucket.getKeyAsNumber().longValue();
            searchResponseAttrVo.setAttrId(attrId);
            //子聚合子集
            Aggregations subAggregations = bucket.getAggregations();
            //平台属性名字结果
            ParsedStringTerms aggAttrName = subAggregations.get("aggAttrName");
            if (!aggAttrName.getBuckets().isEmpty()) {
                //取任意一个名字都是一样的
                String attrName = aggAttrName.getBuckets().get(0).getKeyAsString();
                searchResponseAttrVo.setAttrName(attrName);
            }
            //平台属性值
            ParsedStringTerms aggAttrValue = subAggregations.get("aggAttrValue");
            if (!aggAttrValue.getBuckets().isEmpty()) {
                //取任意一个名字都是一样的
               List<String> attrValues = aggAttrValue.getBuckets().stream().map(bucketValue ->{
                   //获取值的名字
                   String attrValue = bucketValue.getKeyAsString();
                   return attrValue;
               }).collect(Collectors.toList());
               searchResponseAttrVo.setAttrValueList(attrValues);
            }
            return searchResponseAttrVo;
        }).collect(Collectors.toList());
        return result;
    }

    /**
     * 品牌聚合结果
     * @param aggregations
     * @return
     */
    private List<SearchResponseTmVo> getTradeMarkAggResult(Aggregations aggregations) {
        //通过别名获取品牌id的聚合结果
        ParsedLongTerms aggTmId = aggregations.get("aggTmId");
        //遍历结果
        List<SearchResponseTmVo> result = aggTmId.getBuckets().stream().map(bucket -> {
            SearchResponseTmVo searchResponseTmVo = new SearchResponseTmVo();
            //获得品牌id
            long tmId = bucket.getKeyAsNumber().longValue();
            searchResponseTmVo.setTmId(tmId);
            //子聚合子集
            Aggregations subAggregations = bucket.getAggregations();
            //品牌名字结果
            ParsedStringTerms aggTmName = subAggregations.get("aggTmName");
            if (!aggTmName.getBuckets().isEmpty()) {
                //取任意一个名字都是一样的
                String tmName = aggTmName.getBuckets().get(0).getKeyAsString();
                searchResponseTmVo.setTmName(tmName);
            }
            //品牌logo结果
            ParsedStringTerms aggTmLogoUrl = subAggregations.get("aggTmLogoUrl");
            if (!aggTmLogoUrl.getBuckets().isEmpty()) {
                //取任意一个都是一样的
                String tmLogoUrl = aggTmLogoUrl.getBuckets().get(0).getKeyAsString();
                searchResponseTmVo.setTmLogoUrl(tmLogoUrl);
            }
            return searchResponseTmVo;
        }).collect(Collectors.toList());
        return result;
    }
}
