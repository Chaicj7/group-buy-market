package com.chaicj.domain.trade.service.lock.factory;

import com.chaicj.domain.trade.model.entity.TradeRuleCommandEntity;
import com.chaicj.domain.trade.model.entity.TradeRuleDynamicContext;
import com.chaicj.domain.trade.model.entity.TradeRuleFilterBackEntity;
import com.chaicj.domain.trade.service.lock.filter.ActivityUsabilityRuleFilter;
import com.chaicj.domain.trade.service.lock.filter.UserTakeLimitRuleFilter;
import com.chaicj.types.design.framework.link.model2.LinkArmory;
import com.chaicj.types.design.framework.link.model2.chain.BusinessLinkedList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TradeRuleFilterFactory {

    @Bean(name = "tradeRuleFilter")
    public BusinessLinkedList<TradeRuleCommandEntity, TradeRuleDynamicContext, TradeRuleFilterBackEntity> tradeRuleFilter(ActivityUsabilityRuleFilter activityUsabilityRuleFilter, UserTakeLimitRuleFilter userTakeLimitRuleFilter) {
        // 组装链
        LinkArmory<TradeRuleCommandEntity, TradeRuleDynamicContext, TradeRuleFilterBackEntity> linkArmory = new LinkArmory<>("tradeRuleFilter", activityUsabilityRuleFilter, userTakeLimitRuleFilter);
        // 链对象
        return linkArmory.getLogicLink();
    }

}
