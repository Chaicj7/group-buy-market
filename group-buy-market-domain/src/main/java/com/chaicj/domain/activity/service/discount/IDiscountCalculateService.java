package com.chaicj.domain.activity.service.discount;

import com.chaicj.domain.activity.model.valobj.GroupBuyDiscountVO;

import java.math.BigDecimal;

public interface IDiscountCalculateService {

    BigDecimal calculate(String userId, BigDecimal originalPrice, GroupBuyDiscountVO groupBuyDiscount);
}
