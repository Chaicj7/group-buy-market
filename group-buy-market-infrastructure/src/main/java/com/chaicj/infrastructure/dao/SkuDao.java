package com.chaicj.infrastructure.dao;

import com.chaicj.infrastructure.dao.po.Sku;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SkuDao {

    Sku queryByGoodsId(String goodsId);
}
