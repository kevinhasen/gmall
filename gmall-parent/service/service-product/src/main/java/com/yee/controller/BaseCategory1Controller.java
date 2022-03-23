package com.yee.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yee.gmall.common.result.Result;
import com.yee.gmall.model.product.BaseCategory1;
import com.yee.service.BaseCategory1Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ClassName: BaseCategory1Service
 * Description:
 * date: 2022/2/12 17:50
 * 一级分类控制层
 * @author Yee
 * @since JDK 1.8
 */
//返回json字符串
@RestController
@RequestMapping("/api/category1")
public class BaseCategory1Controller {
    @Autowired
    private BaseCategory1Service baseCategory1Service;

    /**
     * 根据id查询平台属性
     * @param id
     * @return
     */
    @GetMapping("/getById/{id}")
    public Result<BaseCategory1> getById(@PathVariable(value = "id") Long id){
        BaseCategory1 baseCategory1 = baseCategory1Service.getById(id);
        return Result.ok(baseCategory1);
    }

    /**
     * 查询所有数据平台属性
     * @return
     */
    @GetMapping("/getAll")
    public Result getAll(){
        //不追加查询条件
        List<BaseCategory1> list = baseCategory1Service.list(null);
        return Result.ok(list);
    }

    /**
     * 添加数据
     * @return
     */
    @PostMapping
    public Result add(@RequestBody BaseCategory1 baseCategory1){
        boolean save = baseCategory1Service.save(baseCategory1);
        if (!save){
            new RuntimeException("新增失败");
        }
        return Result.ok();
    }

    /**
     * 根据id删除数据
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable("id") Long id){
        boolean remove = baseCategory1Service.removeById(id);
        if (!remove){
            new RuntimeException("删除失败");
        }
        return Result.ok();
    }

    /**
     * 根据id修改数据
     * @param baseCategory1
     * @return
     */
    @PutMapping
    public Result update(@RequestBody BaseCategory1 baseCategory1){
        boolean update = baseCategory1Service.updateById(baseCategory1);
        if (!update){
            new RuntimeException("修改失败");
        }
        return Result.ok();
    }

    /**
     * 条件查询
     * @param baseCategory1
     * @return
     */
    @PostMapping("/search")
    public Result search(@RequestBody BaseCategory1 baseCategory1){
        List<BaseCategory1> list = baseCategory1Service.search(baseCategory1);
        return Result.ok(list);
    }

    /**
     *  分页查询
     * @param page 当前页
     * @param size 每页数量
     * @return
     */
    @GetMapping("/page/{page}/{size}")
    public Result page(@PathVariable("page") Integer page,
                       @PathVariable("size") Integer size){
        IPage<BaseCategory1> page1 = baseCategory1Service.page(new Page<BaseCategory1>(page, size), null);
        return Result.ok(page1);
    }

    /**
     * 条件分页查询
     * @param page
     * @param size
     * @param baseCategory1
     * @return
     */
    @PostMapping("/page/{page}/{size}")
    public Result page(@PathVariable("page") Integer page,
                       @PathVariable("size") Integer size,
                       @RequestBody BaseCategory1 baseCategory1){
        IPage<BaseCategory1> iPage = baseCategory1Service.search(page, size, baseCategory1);
        return Result.ok(iPage);
    }
}
