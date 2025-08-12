package com.chaicj.domain.trade.adapter.repository;

import com.chaicj.domain.activity.model.valobj.GroupBuyDiscountVO;
import com.chaicj.domain.trade.model.aggregate.GroupBuyOrderAggregate;
import com.chaicj.domain.trade.model.aggregate.GroupBuyTeamSettlementAggregate;
import com.chaicj.domain.trade.model.entity.GroupBuyActivityEntity;
import com.chaicj.domain.trade.model.entity.GroupBuyTeamEntity;
import com.chaicj.domain.trade.model.entity.MarketPayOrderEntity;
import com.chaicj.domain.trade.model.entity.NotifyTaskEntity;
import com.chaicj.domain.trade.model.valobj.GroupBuyProgressVO;

import java.util.List;

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

    GroupBuyActivityEntity queryGroupBuyActivityEntityByActivityId(Long activityId);

    Integer queryOrderCountByActivityId(Long activityId, String userId);

    GroupBuyTeamEntity queryGroupBuyTeamByTeamId(String teamId);

    boolean settlementMarketPayOrder(GroupBuyTeamSettlementAggregate settlementAggregate);

    List<NotifyTaskEntity> queryGroupBuySuccessNotifyList();

    Boolean isSCBlackIntercept(String source, String channel);

    int updateNotifyTaskStatusSuccess(String teamId);

    int updateNotifyTaskStatusRetry(String teamId);

    int updateNotifyTaskStatusError(String teamId);

    List<NotifyTaskEntity> queryUnExecutedNotifyTaskList(String teamId);
}
