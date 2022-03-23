package com.yee.controller;

import com.yee.ListFeign;
import com.yee.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * ClassName: SearchController
 * Description:
 * date: 2022/2/23 15:32
 *
 * @author Yee
 * @since JDK 1.8
 */
@Controller
@RequestMapping("/page/search")
public class SearchController {

    @Autowired
    private ListFeign listFeign;
    @Value("${item.url}")
    private String itemUrl;

    @GetMapping
    public String search(@RequestParam Map<String,String> searchData, Model model){
        Map<String, Object> map = listFeign.search(searchData);
        //查询数据返回前端
        model.addAllAttributes(map);
        //数据回显
        model.addAttribute("searchData",searchData);
        //获得url
        String url = getUrl(searchData);
        model.addAttribute("url",url);
        //获取总数据量
        Object totalHits = map.get("totalHits");
        //每页显示条数
        Integer size = 100;
        //当前第几页
        Integer pageNum = getPageNum(searchData.get("pageNum"));
        //初始化page对象
        Page page = new Page(Long.parseLong(totalHits.toString()), pageNum, size);
        model.addAttribute("page", page);
        //存储商品详情页的前缀地址
        model.addAttribute("itemUrl", itemUrl);
        //打开页面
        return "list";
    }

    /**
     * 拼接url
     * @param searchData
     * @return
     */
    private String getUrl(Map<String,String>  searchData){
        //初始化连接
        String url = "/page/search?";
        //遍历条件
        for (Map.Entry<String, String> entry : searchData.entrySet()) {
            //查询key
            String key = entry.getKey();
            //查询的值
            String value = entry.getValue();
            //不包含排序字段
            if (!key.equals("softRule")
                    && !key.equals("softField")
                    &&  !key.equals("pageNum")){
                //拼接
                url = url + key + "=" + value + "&";
            }

        }
        //url.length() - 1 是为了去掉多余的&符号
        return url.substring(0,url.length() - 1);
    }

    /**
     * 判断页码
     * @param pageNum
     */
    private Integer getPageNum(String pageNum) {
        try {
            int i = Integer.parseInt(pageNum);
            return i>0?i:1;
        }catch (Exception e){
            //默认显示第一页
            return 1;
        }
    }
}
