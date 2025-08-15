package com.chaicj.infrastructure.dao;

import com.chaicj.infrastructure.dao.po.GroupBuyOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

@Mapper
public interface GroupBuyOrderDao {

    GroupBuyOrder queryGroupBuyProgress(String teamId);

    void insert(GroupBuyOrder groupBuyOrder);

    int updateAddLockCount(String teamId);

    int updateAddCompleteCount(String teamId);

    int updateOrderStatus2COMPLETE(String teamId);

    List<GroupBuyOrder> queryAllTeamCount(@Param("teamIds") List<String> teamIds);

    List<GroupBuyOrder> queryGroupBuyProgressByTeamIds(@Param("teamIds") Set<String> teamIds);
}
