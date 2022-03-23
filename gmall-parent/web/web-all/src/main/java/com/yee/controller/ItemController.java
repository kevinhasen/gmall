package com.yee.controller;

import com.yee.ItemFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.PrintWriter;
import java.util.Map;

/**
 * ClassName: ItemController
 * Description:
 * date: 2022/2/19 23:20
 *
 * @author Yee
 * @since JDK 1.8
 */
@Controller
@RequestMapping("/page/item")
public class ItemController {

    @Autowired
    private ItemFeign itemFeign;
    @Autowired
    private TemplateEngine templateEngine;

    @GetMapping("/getItemPage/{skuId}")
    public String getItemPage(@PathVariable("skuId") Long skuId, Model model){
        Map<String, Object> map = itemFeign.getItemInfo(skuId);
        //保存数据到model
        model.addAllAttributes(map);
        return "item";
    }

    @GetMapping("/createItemHtml/{skuId}")
    @ResponseBody
    public String createItemHtml(@PathVariable("skuId") Long skuId){
        try {
            //原始数据
            Map<String, Object> map = itemFeign.getItemInfo(skuId);
            //静态页面存储路径
            File file = new File("F:\\temp",skuId+".html");
            //容器对象
            Context context = new Context();
            context.setVariables(map);
            //输出对象
            PrintWriter writer = new PrintWriter(file);
            //模板写出
            templateEngine.process("item",context,writer);
            //关闭资源
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "创建成功";
    }

}
