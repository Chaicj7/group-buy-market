package com.chaicj.infrastructure.dao;

import com.chaicj.infrastructure.dao.po.CrowdTagsJob;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CrowdTagsJobDao {

    CrowdTagsJob queryCrowdTagsJob(CrowdTagsJob crowdTagsJobReq);
}
