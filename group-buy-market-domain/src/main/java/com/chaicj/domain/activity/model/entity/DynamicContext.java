package com.chaicj.domain.activity.model.entity;

import com.chaicj.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import com.chaicj.domain.activity.model.valobj.SkuVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DynamicContext {

    private SkuVO skuVO;

    private GroupBuyActivityDiscountVO groupBuyActivityDiscountVO;

    // 折扣价格
    private BigDecimal deductionPrice;

    // 活动可见性限制
    private boolean visible;
    // 活动
    private boolean enable;
}
