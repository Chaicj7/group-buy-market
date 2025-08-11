package com.chaicj.domain.tag.service;

import com.chaicj.domain.tag.adapter.repository.ITagRepository;
import com.chaicj.domain.tag.model.entity.CrowdTagsJobEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class TagService implements ITagService {

    @Resource
    private ITagRepository tagRepository;

    @Override
    public void execTagBatchJob(String tagId, String batchId) {
        log.info("人群标签批次任务 tagId:{} batchId:{}", tagId, batchId);
        // 1. 查询批次任务
        CrowdTagsJobEntity crowdTagsJobEntity = tagRepository.queryCrowdTagsJobEntity(tagId, batchId);
        // 2. 采集用户数据 - 这部分需要采集用户的消费类数据，后续有用户发起拼单后再处理。

        // 3. 数据写入记录
        List<String> userIds = Arrays.asList("chaicj", "zhangsan", "lisi", "wangwu");
        for (String userId : userIds) {
            tagRepository.addCrowdTagsUserId(tagId, userId);
        }

        // 5. 更新人群标签统计量
        tagRepository.updateCrowdTagsStatistics(tagId, userIds.size());
    }
}
