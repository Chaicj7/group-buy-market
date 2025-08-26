package com.chaicj.domain.trade.service.lock.factory;

import com.chaicj.domain.trade.model.entity.TradeLockRuleCommandEntity;
import com.chaicj.domain.trade.model.entity.TradeLockRuleDynamicContext;
import com.chaicj.domain.trade.model.entity.TradeLockRuleFilterBackEntity;
import com.chaicj.domain.trade.service.lock.filter.ActivityUsabilityRuleFilter;
import com.chaicj.domain.trade.service.lock.filter.TeamStockOccupyRuleFilter;
import com.chaicj.domain.trade.service.lock.filter.UserTakeLimitRuleFilter;
import com.chaicj.types.design.framework.link.model2.LinkArmory;
import com.chaicj.types.design.framework.link.model2.chain.BusinessLinkedList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TradeLockRuleFilterFactory {

    @Bean(name = "tradeRuleFilter")
    public BusinessLinkedList<TradeLockRuleCommandEntity, TradeLockRuleDynamicContext, TradeLockRuleFilterBackEntity> tradeRuleFilter(ActivityUsabilityRuleFilter activityUsabilityRuleFilter,
                                                                                                                                      UserTakeLimitRuleFilter userTakeLimitRuleFilter,
                                                                                                                                      TeamStockOccupyRuleFilter teamStockOccupyRuleFilter) {
        // 组装链
        LinkArmory<TradeLockRuleCommandEntity, TradeLockRuleDynamicContext, TradeLockRuleFilterBackEntity> linkArmory = new LinkArmory<>("tradeRuleFilter",
                activityUsabilityRuleFilter,
                userTakeLimitRuleFilter,
                teamStockOccupyRuleFilter);
        // 链对象
        return linkArmory.getLogicLink();
    }

}
