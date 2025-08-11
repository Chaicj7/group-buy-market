package com.chaicj.infrastructure.dao;

import com.chaicj.infrastructure.dao.po.GroupBuyOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GroupBuyOrderDao {

    GroupBuyOrder queryGroupBuyProgress(String teamId);

    void insert(GroupBuyOrder groupBuyOrder);

    int updateAddLockCount(String teamId);

    int updateAddCompleteCount(String teamId);

    int updateOrderStatus2COMPLETE(String teamId);
}
