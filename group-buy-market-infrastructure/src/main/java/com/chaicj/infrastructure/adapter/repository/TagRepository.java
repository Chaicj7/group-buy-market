package com.chaicj.infrastructure.adapter.repository;

import com.chaicj.domain.tag.adapter.repository.ITagRepository;
import com.chaicj.domain.tag.model.entity.CrowdTagsJobEntity;
import com.chaicj.infrastructure.dao.CrowdTagsDao;
import com.chaicj.infrastructure.dao.CrowdTagsDetailDao;
import com.chaicj.infrastructure.dao.CrowdTagsJobDao;
import com.chaicj.infrastructure.dao.po.CrowdTags;
import com.chaicj.infrastructure.dao.po.CrowdTagsDetail;
import com.chaicj.infrastructure.dao.po.CrowdTagsJob;
import com.chaicj.infrastructure.redis.IRedisService;
import org.redisson.api.RBitSet;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class TagRepository implements ITagRepository {

    @Resource
    private CrowdTagsJobDao crowdTagsJobDao;
    @Resource
    private CrowdTagsDetailDao crowdTagsDetailDao;
    @Resource
    private CrowdTagsDao crowdTagsDao;
    @Resource
    private IRedisService redisService;

    @Override
    public CrowdTagsJobEntity queryCrowdTagsJobEntity(String tagId, String batchId) {
        CrowdTagsJob crowdTagsJobReq = new CrowdTagsJob();
        crowdTagsJobReq.setTagId(tagId);
        crowdTagsJobReq.setBatchId(batchId);
        CrowdTagsJob crowdTagsJobRes = crowdTagsJobDao.queryCrowdTagsJob(crowdTagsJobReq);
        if (null == crowdTagsJobRes) return null;
        return CrowdTagsJobEntity.builder()
                .tagType(crowdTagsJobRes.getTagType())
                .tagRule(crowdTagsJobRes.getTagRule())
                .statStartTime(crowdTagsJobRes.getStatStartTime())
                .statEndTime(crowdTagsJobRes.getStatEndTime())
                .build();
    }

    @Override
    public void addCrowdTagsUserId(String tagId, String userId) {
        CrowdTagsDetail crowdTagsDetail = new CrowdTagsDetail();
        crowdTagsDetail.setTagId(tagId);
        crowdTagsDetail.setUserId(userId);
        try {
            crowdTagsDetailDao.addCrowdTagsUserId(crowdTagsDetail);
            // 获取BitSet
            RBitSet bitSet = redisService.getBitSet(tagId);
            bitSet.set(redisService.getIndexFromUserId(userId), true);
        } catch (DuplicateKeyException ignore) {
            // 忽略唯一索引冲突
        }
    }

    @Override
    public void updateCrowdTagsStatistics(String tagId, Integer size) {
        CrowdTags crowdTags = new CrowdTags();
        crowdTags.setTagId(tagId);
        crowdTags.setStatistics(size);
        crowdTagsDao.updateCrowdTagsStatistics(crowdTags);
    }

    @Override
    public boolean queryCrowdTagsDetail(String userId, String tagId) {
        RBitSet bitSet = redisService.getBitSet(tagId);
        boolean flag = bitSet.get(redisService.getIndexFromUserId(userId));
        if (flag) return flag;
        CrowdTagsDetail crowdTagsDetailReq = new CrowdTagsDetail();
        crowdTagsDetailReq.setTagId(tagId);
        crowdTagsDetailReq.setUserId(userId);
        CrowdTagsDetail crowdTagsDetail = crowdTagsDetailDao.queryCrowdTagsDetail(crowdTagsDetailReq);
        return crowdTagsDetail != null;
    }
}
