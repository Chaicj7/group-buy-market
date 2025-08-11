package com.chaicj.infrastructure.dao;

import com.chaicj.infrastructure.dao.po.GroupBuyOrderList;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GroupBuyOrderListDao {

    GroupBuyOrderList queryGroupBuyOrderRecordByOutTradeNo(GroupBuyOrderList groupBuyOrderListReq);

    void insert(GroupBuyOrderList groupBuyOrderList);

    Integer queryOrderCountByActivityId(GroupBuyOrderList groupBuyOrderListReq);

    int updateOrderStatus2COMPLETE(GroupBuyOrderList groupBuyOrderListReq);

    List<String> queryGroupBuyCompleteOrderOutTradeNoListByTeamId(String teamId);
}
