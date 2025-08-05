package com.chaicj.domain.activity.model.entity;

import com.chaicj.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import com.chaicj.domain.activity.model.valobj.SkuVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DynamicContext {

    private SkuVO skuVO;

    private GroupBuyActivityDiscountVO groupBuyActivityDiscountVO;
}
