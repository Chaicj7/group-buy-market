package com.chaicj.infrastructure.dao;

import com.chaicj.infrastructure.dao.po.GroupBuyOrderList;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GroupBuyOrderListDao {

    GroupBuyOrderList queryGroupBuyOrderRecordByOutTradeNo(GroupBuyOrderList groupBuyOrderListReq);

    void insert(GroupBuyOrderList groupBuyOrderList);
}
