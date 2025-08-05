package com.chaicj.domain.activity.service.discount.impl;

import com.chaicj.domain.activity.model.valobj.GroupBuyDiscountVO;
import com.chaicj.domain.activity.service.discount.AbstractDiscountCalculateService;
import com.chaicj.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service("MJ")
public class MJCalculateService extends AbstractDiscountCalculateService {


    @Override
    protected BigDecimal doCalculate(BigDecimal originalPrice, GroupBuyDiscountVO groupBuyDiscount) {
        // 折扣表达式  100,10 满100减10元
        String marketExpr = groupBuyDiscount.getMarketExpr();
        String[] split = marketExpr.split(Constants.SPLIT);
        String x = split[0];
        String y = split[1];
        if (originalPrice.compareTo(new BigDecimal(x)) < 0) return originalPrice;
        return originalPrice.subtract(new BigDecimal(y));
    }
}
