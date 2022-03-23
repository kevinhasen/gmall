package com.yee.gmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yee.gmall.model.product.BaseAttrInfo;
import com.yee.gmall.product.mapper.BaseAttrInfoMapper;
import com.yee.gmall.product.service.BaseAttrInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.zip.ZipEntry;

/**
 * ClassName: BaseAttrInfoServiceImpl
 * Description:
 * date: 2022/2/24 21:49
 * BaseAttrInfo 逻辑层实现
 * @author Yee
 * @since JDK 1.8
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BaseAttrInfoServiceImpl implements BaseAttrInfoService {

    @Resource
    private BaseAttrInfoMapper baseAttrInfoMapper;
    /**
     * 新增数据
     *
     * @param baseAttrInfo
     */
    @Override
    public void add(BaseAttrInfo baseAttrInfo) {
        int insert = baseAttrInfoMapper.insert(baseAttrInfo);
        if (insert <= 0 ){
            throw  new RuntimeException("新增失败");
        }
    }

    /**
     * 根据id删除数据
     *
     * @param id
     */
    @Override
    public void removeById(Long id) {
        int delete = baseAttrInfoMapper.deleteById(id);
        if (delete <= 0 ){
            throw  new RuntimeException("删除失败");
        }
    }

    /**
     * 根据id修改数据
     *
     * @param baseAttrInfo
     */
    @Override
    public void update(BaseAttrInfo baseAttrInfo) {
        int update = baseAttrInfoMapper.updateById(baseAttrInfo);
        if (update <= 0 ){
            throw  new RuntimeException("删除失败");
        }
    }

    /**
     * 根据id查询数据
     *
     * @param id
     * @return
     */
    @Override
    public BaseAttrInfo getById(Long id) {
        BaseAttrInfo baseAttrInfo = baseAttrInfoMapper.selectById(id);
        return baseAttrInfo;
    }

    /**
     * 查询所有数据
     *
     * @return
     */
    @Override
    public List<BaseAttrInfo> getAll() {
        List<BaseAttrInfo> list = baseAttrInfoMapper.selectList(null);
        return list;
    }

    /**
     * 条件查询
     *
     * @param baseAttrInfo
     * @return
     */
    @Override
    public List<BaseAttrInfo> search(BaseAttrInfo baseAttrInfo) {
        LambdaQueryWrapper<BaseAttrInfo> wrapper = getWrapper(baseAttrInfo);
        List<BaseAttrInfo> list = baseAttrInfoMapper.selectList(wrapper);
        return list;
    }

    /**
     * 分页查询
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public IPage page(Integer page, Integer size) {
        IPage<BaseAttrInfo> iPage = baseAttrInfoMapper.selectPage(
                new Page<BaseAttrInfo>(page, size), null);
        return iPage;
    }

    /**
     * 条件分页查询
     *
     * @param page
     * @param size
     * @param baseAttrInfo
     * @return
     */
    @Override
    public IPage search(Integer page, Integer size, BaseAttrInfo baseAttrInfo) {
        LambdaQueryWrapper<BaseAttrInfo> wrapper = getWrapper(baseAttrInfo);
        IPage<BaseAttrInfo> iPage = baseAttrInfoMapper.selectPage(
                new Page<BaseAttrInfo>(page, size), wrapper);
        return iPage;
    }

    /**
     * 获得查询条件
     * @param baseAttrInfo
     * @return
     */
    private  LambdaQueryWrapper<BaseAttrInfo> getWrapper(BaseAttrInfo baseAttrInfo){
        LambdaQueryWrapper<BaseAttrInfo> wrapper = new LambdaQueryWrapper<>();
        Long id = baseAttrInfo.getId();
        String attrName = baseAttrInfo.getAttrName();
        Long categoryId = baseAttrInfo.getCategoryId();
        Integer categoryLevel = baseAttrInfo.getCategoryLevel();
        if (id != null){
            wrapper.eq(BaseAttrInfo::getId,id);
        }
        if (!StringUtils.isEmpty(attrName)){
            wrapper.eq(BaseAttrInfo::getAttrName,attrName);
        }
        if (categoryId != null){
            wrapper.eq(BaseAttrInfo::getCategoryId,categoryId);
        }
        if (categoryLevel != null){
            wrapper.eq(BaseAttrInfo::getCategoryLevel,categoryLevel);
        }
        return wrapper;
    }
}
