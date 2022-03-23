package com.yee.gmall.product.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yee.gmall.common.result.Result;
import com.yee.gmall.model.product.BaseAttrInfo;
import com.yee.gmall.product.service.BaseAttrInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ClassName: BaseAttrInfoController
 * Description:
 * date: 2022/2/24 21:51
 * BaseAttrInfo控制层
 * @author Yee
 * @since JDK 1.8
 */
@RestController
@RequestMapping("/api/baseAttrInfo")
public class BaseAttrInfoController {


    @Autowired
    private BaseAttrInfoService baseAttrInfoService;

    /**
     * 根据id查询数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable("id") Long id){
        BaseAttrInfo baseAttrInfo = baseAttrInfoService.getById(id);
        return Result.ok(baseAttrInfo);
    }

    /**
     * 查询所有数据
     * @return
     */
    @GetMapping
    public Result getAll(){
        List<BaseAttrInfo> all = baseAttrInfoService.getAll();
        return Result.ok(all);
    }

    /**
     *  新增数据
     * @return
     */
    @PostMapping
    public Result add(@RequestBody BaseAttrInfo baseAttrInfo){
       baseAttrInfoService.add(baseAttrInfo);
        return Result.ok();
    }


    /**
     *   根据id删除数据
     * @return
     */
    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable("id") Long id){
        baseAttrInfoService.removeById(id);
        return Result.ok();
    }

    /**
     * 修改数据
     * @param baseAttrInfo
     */
    @PutMapping
    public Result update(@RequestBody BaseAttrInfo baseAttrInfo){
        baseAttrInfoService.update(baseAttrInfo);
        return Result.ok();
    }

    /**
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/{page}/{size}")
    public Result page(@PathVariable(value = "page") Integer page,
                       @PathVariable(value = "size") Integer size){
        IPage page1 = baseAttrInfoService.page(page, size);
        return Result.ok(page1);
    }

    /**
     * 条件查询
     * @return
     */
    @PostMapping("/search")
    public Result search(@RequestBody BaseAttrInfo baseAttrInfo){
        List<BaseAttrInfo> search = baseAttrInfoService.search(baseAttrInfo);
        return Result.ok(search);
    }

    /**
     * 条件分页查询
     * @param page
     * @param size
     * @return
     */
    @PostMapping("/search/{page}/{size}")
    public Result page(@PathVariable(value = "page") Integer page,
                       @PathVariable(value = "size") Integer size,
                       @RequestBody BaseAttrInfo baseAttrInfo){
        IPage search = baseAttrInfoService.search(page, size, baseAttrInfo);
        return Result.ok(search);
    }
}
