package com.yee.dao;

import com.yee.gmall.model.list.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * ClassName: GoodsDao
 * Description:
 * date: 2022/2/22 16:40
 * es商品持久层
 * @author Yee
 * @since JDK 1.8
 */
@Repository
public interface GoodsDao extends ElasticsearchRepository<Goods,Long> {

}
