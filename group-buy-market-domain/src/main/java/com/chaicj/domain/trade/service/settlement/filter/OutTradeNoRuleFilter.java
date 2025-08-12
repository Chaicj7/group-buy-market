package com.chaicj.domain.trade.service.settlement.filter;

import com.chaicj.domain.trade.adapter.repository.ITradeOrderRepository;
import com.chaicj.domain.trade.model.entity.MarketPayOrderEntity;
import com.chaicj.domain.trade.model.entity.TradeSettlementRuleCommandEntity;
import com.chaicj.domain.trade.model.entity.TradeSettlementRuleDynamicContext;
import com.chaicj.domain.trade.model.entity.TradeSettlementRuleFilterBackEntity;
import com.chaicj.types.design.framework.link.model2.handler.ILogicHandler;
import com.chaicj.types.enums.ResponseCode;
import com.chaicj.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 *  外部交易单号过滤；外部交易单号是否为退单
 */

@Slf4j
@Service
public class OutTradeNoRuleFilter implements ILogicHandler<TradeSettlementRuleCommandEntity, TradeSettlementRuleDynamicContext, TradeSettlementRuleFilterBackEntity> {

    @Resource
    private ITradeOrderRepository repository;

    @Override
    public TradeSettlementRuleFilterBackEntity apply(TradeSettlementRuleCommandEntity requestParameter, TradeSettlementRuleDynamicContext dynamicContext) throws Exception {
        log.info("结算规则过滤-外部单号校验{} outTradeNo:{}", requestParameter.getUserId(), requestParameter.getOutTradeNo());
        // 查询拼团信息
        MarketPayOrderEntity marketPayOrderEntity = repository.queryNoPayMarketPayOrderByOutTradeNo(requestParameter.getUserId(), requestParameter.getOutTradeNo());
        if (marketPayOrderEntity == null) {
            log.info("不存在的外部交易单号或用户已退单，不需要做支付订单结算:{} outTradeNo:{}", requestParameter.getUserId(), requestParameter.getOutTradeNo());
            throw new AppException(ResponseCode.E0104);
        }
        dynamicContext.setMarketPayOrderEntity(marketPayOrderEntity);
        return next(requestParameter, dynamicContext);
    }
}
