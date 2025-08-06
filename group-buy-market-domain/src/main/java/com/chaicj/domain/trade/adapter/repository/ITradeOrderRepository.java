package com.chaicj.domain.trade.adapter.repository;

import com.chaicj.domain.activity.model.valobj.GroupBuyDiscountVO;
import com.chaicj.domain.trade.model.aggregate.GroupBuyOrderAggregate;
import com.chaicj.domain.trade.model.entity.MarketPayOrderEntity;
import com.chaicj.domain.trade.model.valobj.GroupBuyProgressVO;

public interface ITradeOrderRepository {

    /**
     *  查询，未被支付消费完成的营销优惠订单
     * @param userId 用户ID
     * @param outOrderNo 外部订单号
     * @return
     */
    MarketPayOrderEntity queryNoPayMarketPayOrderByOutTradeNo(String userId, String outOrderNo);

    GroupBuyProgressVO queryGroupBuyProgress(String teamId);

    MarketPayOrderEntity lockMarketPayOrder(GroupBuyOrderAggregate groupBuyOrderAggregate);
}
