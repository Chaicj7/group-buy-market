package com.chaicj.trigger.job;

import com.chaicj.domain.trade.adapter.repository.ITradeOrderRepository;
import com.chaicj.domain.trade.model.entity.NotifyTaskEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class GroupBuySuccessNotifyTask {

    @Resource
    private ITradeOrderRepository tradeOrderRepository;

    @Scheduled(cron = "0 * * * * ?")
    public void exec() {
        log.info("成团定时任务----");
        List<NotifyTaskEntity> notifyTaskEntity = tradeOrderRepository.queryGroupBuySuccessNotifyList();
        if (CollectionUtils.isEmpty(notifyTaskEntity)) return;

        for (NotifyTaskEntity taskEntity : notifyTaskEntity) {
            // TODO 处理成团

            // 修改task
            tradeOrderRepository.updateNotifyTaskStatus(taskEntity.getId());
        }
    }
}
