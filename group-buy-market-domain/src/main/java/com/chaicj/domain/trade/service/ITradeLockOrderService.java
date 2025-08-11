package com.chaicj.domain.trade.service;

import com.chaicj.domain.trade.model.entity.MarketPayOrderEntity;
import com.chaicj.domain.trade.model.entity.PayActivityEntity;
import com.chaicj.domain.trade.model.entity.PayDiscountEntity;
import com.chaicj.domain.trade.model.entity.UserEntity;
import com.chaicj.domain.trade.model.valobj.GroupBuyProgressVO;

public interface ITradeLockOrderService {

    /**
     *  查询，未被支付消费完成的营销优惠订单
     * @param userId 用户ID
     * @param outOrderNo 外部订单号
     * @return
     */
    MarketPayOrderEntity queryNoPayMarketPayOrderByOutTradeNo(String userId, String outOrderNo);

    GroupBuyProgressVO queryGroupBuyProgress(String teamId);

    MarketPayOrderEntity lockMarketPayOrder(UserEntity userEntity, PayActivityEntity activityEntity, PayDiscountEntity discountEntity) throws Exception;
}
