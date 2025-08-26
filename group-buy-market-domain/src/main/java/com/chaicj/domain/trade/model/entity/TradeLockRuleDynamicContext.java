package com.chaicj.domain.trade.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeLockRuleDynamicContext {

    private String teamStockKey = "group_buy_market_team_stock_key_";

    private GroupBuyActivityEntity groupBuyActivity;

    private Integer userTakeOrderCount;

    public String generateTeamStockKey(String teamId) {
        if (StringUtils.isBlank(teamId)) return null;
        return teamStockKey + groupBuyActivity.getActivityId() + "_" + teamId;
    }

    public String generateRecoveryTeamStockKey(String teamId) {
        if (StringUtils.isBlank(teamId)) return null;
        return teamStockKey + groupBuyActivity.getActivityId() + "_" + teamId + "_recovery";
    }
}
