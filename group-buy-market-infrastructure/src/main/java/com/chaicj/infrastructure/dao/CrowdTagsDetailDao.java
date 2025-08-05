package com.chaicj.infrastructure.dao;

import com.chaicj.infrastructure.dao.po.CrowdTagsDetail;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CrowdTagsDetailDao {

    void addCrowdTagsUserId(CrowdTagsDetail crowdTagsDetail);

    CrowdTagsDetail queryCrowdTagsDetail(CrowdTagsDetail crowdTagsDetailReq);
}
