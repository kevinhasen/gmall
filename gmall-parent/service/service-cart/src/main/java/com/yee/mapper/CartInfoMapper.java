package com.yee.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yee.gmall.model.cart.CartInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
 * ClassName: CartInfoMapper
 * Description:
 * date: 2022/2/26 19:38
 *
 * @author Yee
 * @since JDK 1.8
 */
@Mapper
public interface CartInfoMapper extends BaseMapper<CartInfo> {

    /**
     * 修改购物车状态全选或全不选
     * @param status
     * @param username
     * @return
     */
    @Update("UPDATE cart_info set is_checked = #{status}  where user_id = #{username}")
    public int updateCartInfo(Short status,String username);

    /**
     * 修改购物车状态单选或取消单选
     * @param status
     * @param id
     * @return
     */
    @Update("UPDATE cart_info set is_checked = #{status}  where id = #{id}")
    public int updateCartInfoOne(Short status,Long id);


}
