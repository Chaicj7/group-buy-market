package com.chaicj.domain.trade.service.settlement.filter;

import com.chaicj.domain.trade.adapter.repository.ITradeOrderRepository;
import com.chaicj.domain.trade.model.entity.*;
import com.chaicj.types.design.framework.link.model2.handler.ILogicHandler;
import com.chaicj.types.enums.ResponseCode;
import com.chaicj.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Slf4j
@Service
public class SettableRuleFilter implements ILogicHandler<TradeSettlementRuleCommandEntity, TradeSettlementRuleDynamicContext, TradeSettlementRuleFilterBackEntity> {

    @Resource
    private ITradeOrderRepository tradeOrderRepository;

    @Override
    public TradeSettlementRuleFilterBackEntity apply(TradeSettlementRuleCommandEntity requestParameter, TradeSettlementRuleDynamicContext dynamicContext) throws Exception {
        log.info("结算规则过滤-有效时间校验{} outTradeNo:{}", requestParameter.getUserId(), requestParameter.getOutTradeNo());
        MarketPayOrderEntity marketPayOrderEntity = dynamicContext.getMarketPayOrderEntity();
        // 查询拼团对象
        GroupBuyTeamEntity groupBuyTeamEntity = tradeOrderRepository.queryGroupBuyTeamByTeamId(marketPayOrderEntity.getTeamId());
        // 外部交易时间
        Date outTradeTime = requestParameter.getOutTradeTime();
        if (!outTradeTime.before(groupBuyTeamEntity.getValidEndTime())) {
            log.error("订单交易时间不在拼团有效时间范围内");
            throw new AppException(ResponseCode.E0106);
        }
        dynamicContext.setGroupBuyTeamEntity(groupBuyTeamEntity);
        return next(requestParameter, dynamicContext);
    }
}
