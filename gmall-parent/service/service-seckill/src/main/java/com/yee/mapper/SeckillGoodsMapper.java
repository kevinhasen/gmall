package com.yee.mapper;

import com.yee.gmall.model.activity.SeckillGoods;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * ClassName: SeckillGoodsMapper
 * Description:
 * date: 2022/3/4 22:07
 * 秒杀商品映射
 * @author Yee
 * @since JDK 1.8
 */
@Mapper
public interface SeckillGoodsMapper extends BaseMapper<SeckillGoods> {

    @Update("update seckill_goods set stock_count = #{num} where id = #{id}")
    public int updateSeckillGoodsStock(@Param("id") Long id,@Param("num") Integer num);
}
