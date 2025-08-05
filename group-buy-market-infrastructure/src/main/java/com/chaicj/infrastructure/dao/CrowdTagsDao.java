package com.chaicj.infrastructure.dao;

import com.chaicj.infrastructure.dao.po.CrowdTags;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CrowdTagsDao {

    void updateCrowdTagsStatistics(CrowdTags crowdTags);
}
