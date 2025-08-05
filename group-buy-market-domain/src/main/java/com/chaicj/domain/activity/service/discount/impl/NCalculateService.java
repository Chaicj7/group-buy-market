package com.chaicj.domain.activity.service.discount.impl;

import com.chaicj.domain.activity.model.valobj.GroupBuyDiscountVO;
import com.chaicj.domain.activity.service.discount.AbstractDiscountCalculateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service("N")
public class NCalculateService extends AbstractDiscountCalculateService {


    @Override
    protected BigDecimal doCalculate(BigDecimal originalPrice, GroupBuyDiscountVO groupBuyDiscount) {
        // 折扣表达式 - 直接为优惠后的金额
        String marketExpr = groupBuyDiscount.getMarketExpr();
        // n元购
        return new BigDecimal(marketExpr);
    }
}
