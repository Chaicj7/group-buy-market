package com.chaicj.domain.trade.model.aggregate;

import com.chaicj.domain.trade.model.entity.PayActivityEntity;
import com.chaicj.domain.trade.model.entity.PayDiscountEntity;
import com.chaicj.domain.trade.model.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupBuyOrderAggregate {

    private UserEntity userEntity;

    private PayActivityEntity activityEntity;

    private PayDiscountEntity discountEntity;

}
