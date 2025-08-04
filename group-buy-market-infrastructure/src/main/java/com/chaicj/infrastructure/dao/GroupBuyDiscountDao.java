package com.chaicj.infrastructure.dao;

import com.chaicj.infrastructure.dao.po.GroupBuyDiscount;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GroupBuyDiscountDao {

    List<GroupBuyDiscount> queryAll();

}
