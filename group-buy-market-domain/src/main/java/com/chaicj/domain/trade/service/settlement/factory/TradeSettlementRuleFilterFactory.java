package com.chaicj.domain.trade.service.settlement.factory;

import com.chaicj.domain.trade.model.entity.TradeSettlementRuleCommandEntity;
import com.chaicj.domain.trade.model.entity.TradeSettlementRuleDynamicContext;
import com.chaicj.domain.trade.model.entity.TradeSettlementRuleFilterBackEntity;
import com.chaicj.domain.trade.service.settlement.filter.EndRuleFilter;
import com.chaicj.domain.trade.service.settlement.filter.OutTradeNoRuleFilter;
import com.chaicj.domain.trade.service.settlement.filter.SCRuleFilter;
import com.chaicj.domain.trade.service.settlement.filter.SettableRuleFilter;
import com.chaicj.types.design.framework.link.model2.LinkArmory;
import com.chaicj.types.design.framework.link.model2.chain.BusinessLinkedList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TradeSettlementRuleFilterFactory {

    @Bean(name = "tradeSettlementRuleFilter")
    public BusinessLinkedList<TradeSettlementRuleCommandEntity, TradeSettlementRuleDynamicContext, TradeSettlementRuleFilterBackEntity> tradeSettlementRuleFilter(
            OutTradeNoRuleFilter outTradeNoRuleFilter,
            SettableRuleFilter settableRuleFilter,
            SCRuleFilter scRuleFilter,
            EndRuleFilter endRuleFilter
    ) {
        // 组装链
        LinkArmory<TradeSettlementRuleCommandEntity, TradeSettlementRuleDynamicContext, TradeSettlementRuleFilterBackEntity> linkArmory =
                new LinkArmory<>("交易结算规则过滤链", scRuleFilter, outTradeNoRuleFilter, settableRuleFilter, endRuleFilter);
        return linkArmory.getLogicLink();
    }
}
