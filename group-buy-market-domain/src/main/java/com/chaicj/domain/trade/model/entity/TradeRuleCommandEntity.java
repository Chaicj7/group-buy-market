package com.chaicj.domain.trade.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeRuleCommandEntity {

    /** 用户ID */
    private String userId;
    /** 活动ID */
    private Long activityId;
}
