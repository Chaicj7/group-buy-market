package com.chaicj.domain.trade.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeSettlementRuleDynamicContext {

    // 订单营销实体对象
    private MarketPayOrderEntity marketPayOrderEntity;
    // 拼团组队实体对象
    private GroupBuyTeamEntity groupBuyTeamEntity;
}
