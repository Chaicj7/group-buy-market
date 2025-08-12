package com.chaicj.domain.trade.service.settlement.filter;

import com.chaicj.domain.trade.adapter.repository.ITradeOrderRepository;
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
 * SC 渠道来源过滤 - 当某个签约渠道下架后，则不会记账
 */
@Slf4j
@Service
public class SCRuleFilter implements ILogicHandler<TradeSettlementRuleCommandEntity, TradeSettlementRuleDynamicContext, TradeSettlementRuleFilterBackEntity> {

    @Resource
    private ITradeOrderRepository repository;

    @Override
    public TradeSettlementRuleFilterBackEntity apply(TradeSettlementRuleCommandEntity requestParameter, TradeSettlementRuleDynamicContext dynamicContext) throws Exception {
        log.info("结算规则过滤-渠道黑名单校验{} outTradeNo:{}", requestParameter.getUserId(), requestParameter.getOutTradeNo());
        Boolean intercept = repository.isSCBlackIntercept(requestParameter.getSource(), requestParameter.getChannel());
        if (intercept) {
            log.error("{}{} 渠道黑名单拦截", requestParameter.getSource(), requestParameter.getChannel());
            throw new AppException(ResponseCode.E0105);
        }
        return next(requestParameter, dynamicContext);
    }
}
