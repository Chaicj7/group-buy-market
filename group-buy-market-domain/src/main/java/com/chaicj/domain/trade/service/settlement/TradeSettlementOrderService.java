package com.chaicj.domain.trade.service.settlement;

import com.chaicj.domain.trade.adapter.repository.ITradeOrderRepository;
import com.chaicj.domain.trade.model.aggregate.GroupBuyTeamSettlementAggregate;
import com.chaicj.domain.trade.model.entity.*;
import com.chaicj.domain.trade.service.ITradeSettlementOrderService;
import com.chaicj.types.design.framework.link.model2.chain.BusinessLinkedList;
import com.chaicj.types.enums.ResponseCode;
import com.chaicj.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class TradeSettlementOrderService implements ITradeSettlementOrderService {

    @Resource
    private ITradeOrderRepository repository;
    @Resource
    private BusinessLinkedList<TradeSettlementRuleCommandEntity, TradeSettlementRuleDynamicContext, TradeSettlementRuleFilterBackEntity> tradeSettlementRuleFilter;

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
                .build();

        // 构建聚合对象
        GroupBuyTeamSettlementAggregate settlementAggregate = GroupBuyTeamSettlementAggregate.builder()
                .userEntity(UserEntity.builder().userId(tradePaySuccessEntity.getUserId()).build())
                .groupBuyTeamEntity(groupBuyTeamEntity)
                .tradePaySuccessEntity(tradePaySuccessEntity)
                .build();

        // 拼团交易结算
        repository.settlementMarketPayOrder(settlementAggregate);

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
}
