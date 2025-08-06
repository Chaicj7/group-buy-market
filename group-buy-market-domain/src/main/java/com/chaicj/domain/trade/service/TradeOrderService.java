package com.chaicj.domain.trade.service;

import com.chaicj.domain.activity.model.valobj.GroupBuyDiscountVO;
import com.chaicj.domain.trade.adapter.repository.ITradeOrderRepository;
import com.chaicj.domain.trade.model.aggregate.GroupBuyOrderAggregate;
import com.chaicj.domain.trade.model.entity.MarketPayOrderEntity;
import com.chaicj.domain.trade.model.entity.PayActivityEntity;
import com.chaicj.domain.trade.model.entity.PayDiscountEntity;
import com.chaicj.domain.trade.model.entity.UserEntity;
import com.chaicj.domain.trade.model.valobj.GroupBuyProgressVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class TradeOrderService implements ITradeOrderService {

    @Resource
    private ITradeOrderRepository tradeOrderRepository;


    @Override
    public MarketPayOrderEntity queryNoPayMarketPayOrderByOutTradeNo(String userId, String outOrderNo) {
        return tradeOrderRepository.queryNoPayMarketPayOrderByOutTradeNo(userId, outOrderNo);
    }

    @Override
    public GroupBuyProgressVO queryGroupBuyProgress(String teamId) {
        return tradeOrderRepository.queryGroupBuyProgress(teamId);
    }

    @Override
    public MarketPayOrderEntity lockMarketPayOrder(UserEntity userEntity, PayActivityEntity activityEntity, PayDiscountEntity discountEntity) {
        log.info("拼团交易-锁定营销优惠支付订单:{} activityId:{} goodsId:{}", userEntity.getUserId(), activityEntity.getActivityId(), discountEntity.getGoodsId());
        GroupBuyOrderAggregate groupBuyOrderAggregate = GroupBuyOrderAggregate.builder()
                .activityEntity(activityEntity)
                .discountEntity(discountEntity)
                .userEntity(userEntity)
                .build();
        // 锁定聚合订单 - 这会用户只是下单还没有支付。后续会有2个流程；支付成功、超时未支付（回退）
        return tradeOrderRepository.lockMarketPayOrder(groupBuyOrderAggregate);
    }
}
