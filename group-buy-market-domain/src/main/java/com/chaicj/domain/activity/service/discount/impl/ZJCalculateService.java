package com.chaicj.domain.activity.service.discount.impl;

import com.chaicj.domain.activity.model.valobj.GroupBuyDiscountVO;
import com.chaicj.domain.activity.service.discount.AbstractDiscountCalculateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service("ZJ")
public class ZJCalculateService extends AbstractDiscountCalculateService {

    @Override
    protected BigDecimal doCalculate(BigDecimal originalPrice, GroupBuyDiscountVO groupBuyDiscount) {
        // 折扣表达式 - 直减为扣减金额
        String marketExpr = groupBuyDiscount.getMarketExpr();
        return originalPrice.subtract(new BigDecimal(marketExpr));
    }
}
