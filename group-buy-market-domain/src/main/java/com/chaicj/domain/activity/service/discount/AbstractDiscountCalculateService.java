package com.chaicj.domain.activity.service.discount;

import com.chaicj.domain.activity.model.valobj.DiscountTypeEnum;
import com.chaicj.domain.activity.model.valobj.GroupBuyDiscountVO;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
public abstract class AbstractDiscountCalculateService implements IDiscountCalculateService{


    @Override
    public BigDecimal calculate(String userId, BigDecimal originalPrice, GroupBuyDiscountVO groupBuyDiscount) {
        log.info("优惠策略折扣计算，折扣类型：{} 营销优惠：{}", groupBuyDiscount.getDiscountType().getInfo(), groupBuyDiscount.getMarketPlan());
        // 人群标签过滤
        if (DiscountTypeEnum.TAG.equals(groupBuyDiscount.getDiscountType())) {
            boolean ifCrowdRange = filterTagId(userId, groupBuyDiscount.getTagId());
            if (!ifCrowdRange) return originalPrice;
        }
        // 折扣优惠计算
        BigDecimal bigDecimal = doCalculate(originalPrice, groupBuyDiscount);
        if (bigDecimal.compareTo(BigDecimal.ZERO) <= 0) {
            return new BigDecimal("0.01");
        }
        return bigDecimal;
    }

    private boolean filterTagId(String userId, String tagId) {
        // TODO 人群标签过滤
        return true;
    }

    protected abstract BigDecimal doCalculate(BigDecimal originalPrice, GroupBuyDiscountVO groupBuyDiscount);

}
