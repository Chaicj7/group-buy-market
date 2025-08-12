package com.chaicj.domain.trade.service.settlement;

import com.alibaba.fastjson.JSON;
import com.chaicj.domain.trade.adapter.port.ITradePort;
import com.chaicj.domain.trade.adapter.repository.ITradeOrderRepository;
import com.chaicj.domain.trade.model.aggregate.GroupBuyTeamSettlementAggregate;
import com.chaicj.domain.trade.model.entity.*;
import com.chaicj.domain.trade.service.ITradeSettlementOrderService;
import com.chaicj.types.design.framework.link.model2.chain.BusinessLinkedList;
import com.chaicj.types.enums.NotifyTaskHTTPEnumVO;
import com.chaicj.types.enums.ResponseCode;
import com.chaicj.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class TradeSettlementOrderService implements ITradeSettlementOrderService {

    @Resource
    private ITradeOrderRepository repository;
    @Resource
    private BusinessLinkedList<TradeSettlementRuleCommandEntity, TradeSettlementRuleDynamicContext, TradeSettlementRuleFilterBackEntity> tradeSettlementRuleFilter;
    @Resource
    private ITradePort tradePort;

    @Override
    public TradePaySettlementEntity settlementMarketPayOrder(TradePaySuccessEntity tradePaySuccessEntity) throws Exception {
        log.info("拼团交易-支付订单结算:{} outTradeNo:{}", tradePaySuccessEntity.getUserId(), tradePaySuccessEntity.getOutTradeNo());
        TradeSettlementRuleFilterBackEntity tradeSettlementRuleFilterBackEntity = tradeSettlementRuleFilter.apply(TradeSettlementRuleCommandEntity.builder()
                .userId(tradePaySuccessEntity.getUserId())
                .source(tradePaySuccessEntity.getSource())
                .channel(tradePaySuccessEntity.getChannel())
                .outTradeNo(tradePaySuccessEntity.getOutTradeNo())
                .outTradeTime(tradePaySuccessEntity.getOutTradeTime())
                .build(), TradeSettlementRuleDynamicContext.builder().build());

        // 查询组团信息
        GroupBuyTeamEntity groupBuyTeamEntity = GroupBuyTeamEntity.builder()
                .teamId(tradeSettlementRuleFilterBackEntity.getTeamId())
                .activityId(tradeSettlementRuleFilterBackEntity.getActivityId())
                .targetCount(tradeSettlementRuleFilterBackEntity.getTargetCount())
                .completeCount(tradeSettlementRuleFilterBackEntity.getCompleteCount())
                .lockCount(tradeSettlementRuleFilterBackEntity.getLockCount())
                .status(tradeSettlementRuleFilterBackEntity.getStatus())
                .validStartTime(tradeSettlementRuleFilterBackEntity.getValidStartTime())
                .validEndTime(tradeSettlementRuleFilterBackEntity.getValidEndTime())
                .notifyUrl(tradeSettlementRuleFilterBackEntity.getNotifyUrl())
                .build();

        // 构建聚合对象
        GroupBuyTeamSettlementAggregate settlementAggregate = GroupBuyTeamSettlementAggregate.builder()
                .userEntity(UserEntity.builder().userId(tradePaySuccessEntity.getUserId()).build())
                .groupBuyTeamEntity(groupBuyTeamEntity)
                .tradePaySuccessEntity(tradePaySuccessEntity)
                .build();

        // 拼团交易结算
        boolean isNotify = repository.settlementMarketPayOrder(settlementAggregate);
        if (isNotify) {
            Map<String, Integer> notifyResultMap = execSettlementNotifyJob(tradeSettlementRuleFilterBackEntity.getTeamId());
            log.info("回调通知拼团完结 result:{}", JSON.toJSONString(notifyResultMap));
        }

        // 返回结算信息 - 公司中开发这样的流程时候，会根据外部需要进行值的设置
        return TradePaySettlementEntity.builder()
                .source(tradePaySuccessEntity.getSource())
                .channel(tradePaySuccessEntity.getChannel())
                .userId(tradePaySuccessEntity.getUserId())
                .teamId(tradeSettlementRuleFilterBackEntity.getTeamId())
                .activityId(groupBuyTeamEntity.getActivityId())
                .outTradeNo(tradePaySuccessEntity.getOutTradeNo())
                .build();
    }

    @Override
    public Map<String, Integer> execSettlementNotifyJob() throws Exception {
        List<NotifyTaskEntity> notifyTaskEntities = repository.queryGroupBuySuccessNotifyList();
        return execSettlementNotifyJob(notifyTaskEntities);
    }

    @Override
    public Map<String, Integer> execSettlementNotifyJob(String teamId) throws Exception {
        log.info("拼团交易-执行结算通知回调，指定 teamId:{}", teamId);
        List<NotifyTaskEntity> notifyTaskEntityList = repository.queryUnExecutedNotifyTaskList(teamId);
        return execSettlementNotifyJob(notifyTaskEntityList);
    }

    private Map<String, Integer> execSettlementNotifyJob(List<NotifyTaskEntity> notifyTaskEntityList) throws Exception {
        int successCount = 0, errorCount = 0, retryCount = 0;
        for (NotifyTaskEntity taskEntity : notifyTaskEntityList) {
            // 回调处理 success 成功，error 失败
            String response = tradePort.GroupBuyNotify(taskEntity);
            if (NotifyTaskHTTPEnumVO.SUCCESS.getCode().equals(response)) {
                int updateCount = repository.updateNotifyTaskStatusSuccess(taskEntity.getTeamId());
                if (updateCount == 1) {
                    successCount += 1;
                }
            } else if (NotifyTaskHTTPEnumVO.ERROR.getCode().equals(response)) {
                if (taskEntity.getNotifyCount() < 5) {
                    int updateCount = repository.updateNotifyTaskStatusError(taskEntity.getTeamId());
                    if (updateCount == 1) {
                        errorCount += 1;
                    }
                } else {
                    int updateCount = repository.updateNotifyTaskStatusRetry(taskEntity.getTeamId());
                    if (updateCount == 1) {
                        retryCount += 1;
                    }
                }
            }
        }
        Map<String, Integer> resultMap = new HashMap<>();
        resultMap.put("waitCount", notifyTaskEntityList.size());
        resultMap.put("successCount", successCount);
        resultMap.put("errorCount", errorCount);
        resultMap.put("retryCount", retryCount);

        return resultMap;
    }
}
