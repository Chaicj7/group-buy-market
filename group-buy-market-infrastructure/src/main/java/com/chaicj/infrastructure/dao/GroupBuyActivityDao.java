package com.chaicj.infrastructure.dao;

import com.chaicj.infrastructure.dao.po.GroupBuyActivity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GroupBuyActivityDao {

    List<GroupBuyActivity> queryAll();

    GroupBuyActivity queryValidGroupBuyActivity(GroupBuyActivity groupBuyActivityReq);

}
