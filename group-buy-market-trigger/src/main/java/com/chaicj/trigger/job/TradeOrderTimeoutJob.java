package com.chaicj.trigger.job;

import com.chaicj.domain.activity.adapter.repository.IActivityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class TradeOrderTimeoutJob {

    @Resource
    private IActivityRepository activityRepository;


}
