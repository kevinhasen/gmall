package com.yee.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yee.gmall.model.product.BaseCategory1;
import com.yee.mapper.BaseCategory1Mapper;
import com.yee.service.BaseCategory1Service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * ClassName: BaseCategory1ServiceImpl
 * Description:
 * date: 2022/2/12 17:48
 * 一级分类接口类的实现类
 * @author Yee
 * @since JDK 1.8
 */
@Service
//出现异常回滚
@Transactional(rollbackFor = Exception.class)
public class BaseCategory1ServiceImpl extends ServiceImpl<BaseCategory1Mapper, BaseCategory1>
        implements BaseCategory1Service {


    /**
     * 条件查询
     *
     * @param baseCategory1 查询条件
     * @return
     */
    @Override
    public List<BaseCategory1> search(BaseCategory1 baseCategory1) {
        LambdaQueryWrapper wrapper = getWrapper(baseCategory1);
        List list = baseMapper.selectList(wrapper);
        return list;
    }

    /**
     * 条件分页查询
     *
     * @param page          当前页
     * @param size          每页数量
     * @param baseCategory1 查询条件
     * @return
     */
    @Override
    public IPage search(Integer page, Integer size, BaseCategory1 baseCategory1) {
        LambdaQueryWrapper wrapper = getWrapper(baseCategory1);
        IPage iPage = baseMapper.selectPage(new Page<BaseCategory1>(page, size), wrapper);
        return iPage;
    }

    /**
     * 查询条件
     * @param baseCategory1
     * @return
     */
    private LambdaQueryWrapper getWrapper(BaseCategory1 baseCategory1){
        //使用lambda表达式
        LambdaQueryWrapper<BaseCategory1> wrapper = new LambdaQueryWrapper();
        //条件不为空的时候才查询
        if (baseCategory1.getId() != null){
            wrapper.eq(BaseCategory1::getId,baseCategory1.getId());
        }
        if (!StringUtils.isEmpty(baseCategory1.getName())){
            wrapper.like(BaseCategory1::getName,baseCategory1.getName());
        }
        return wrapper;
    }
}
