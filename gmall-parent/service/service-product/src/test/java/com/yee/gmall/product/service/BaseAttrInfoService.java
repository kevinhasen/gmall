package com.yee.gmall.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yee.gmall.model.product.BaseAttrInfo;

import java.util.List;

/**
 * ClassName: BaseAttrInfoService
 * Description:
 * date: 2022/2/24 21:48
 * BaseAttrInfo 逻辑层
 * @author Yee
 * @since JDK 1.8
 */
public interface BaseAttrInfoService {

    /**
     * 新增数据
     * @param baseAttrInfo
     */
    public void add(BaseAttrInfo baseAttrInfo);

    /**
     * 根据id删除数据
     * @param id
     */
    public void removeById(Long id);

    /**
     * 修改数据
     * @param baseAttrInfo
     */
    public void update(BaseAttrInfo baseAttrInfo);

    /**
     * 根据id查询数据
     * @param id
     * @return
     */
    public BaseAttrInfo getById(Long id);

    /**
     * 查询所有数据
     * @return
     */
    public List<BaseAttrInfo> getAll();


    /**
     * 条件查询
     * @return
     */
    public List<BaseAttrInfo> search(BaseAttrInfo baseAttrInfo);


    /**
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    public IPage page(Integer page,Integer size);

    /**
     * 条件分页查询
     * @param page
     * @param size
     * @return
     */
    public IPage search(Integer page,
                      Integer size,
                      BaseAttrInfo baseAttrInfo);
}
