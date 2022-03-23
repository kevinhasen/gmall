package com.yee.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yee.gmall.model.product.BaseCategory1;

import java.util.List;

/**
 * ClassName: BaseCategory1Service
 * Description:
 * date: 2022/2/12 17:47
 * 一级分类接口类
 * @author Yee
 * @since JDK 1.8
 */
public interface BaseCategory1Service extends IService<BaseCategory1> {

    /**
     * 条件查询
     * @param baseCategory1 查询条件
     * @return
     */
    public List<BaseCategory1> search(BaseCategory1 baseCategory1);
    /**
     * 条件分页查询
     * @param page 当前页
     * @param size 每页数量
     * @param baseCategory1 查询条件
     * @return
     */
    public IPage search(Integer page,
                        Integer size,
                        BaseCategory1 baseCategory1);


}
