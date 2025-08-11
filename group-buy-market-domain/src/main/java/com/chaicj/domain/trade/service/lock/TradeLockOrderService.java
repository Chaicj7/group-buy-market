package com.chaicj.domain.trade.service.lock;

import com.chaicj.domain.trade.adapter.repository.ITradeOrderRepository;
import com.chaicj.domain.trade.model.aggregate.GroupBuyOrderAggregate;
import com.chaicj.domain.trade.model.entity.*;
import com.chaicj.domain.trade.model.valobj.GroupBuyProgressVO;
import com.chaicj.domain.trade.service.ITradeLockOrderService;
import com.chaicj.types.design.framework.link.model2.chain.BusinessLinkedList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class TradeLockOrderService implements ITradeLockOrderService {

    @Resource
    private ITradeOrderRepository tradeOrderRepository;
    @Resource
    private BusinessLinkedList<TradeRuleCommandEntity, TradeRuleDynamicContext, TradeRuleFilterBackEntity> tradeRuleFilter;


    @Override
    public MarketPayOrderEntity queryNoPayMarketPayOrderByOutTradeNo(String userId, String outOrderNo) {
        return tradeOrderRepository.queryNoPayMarketPayOrderByOutTradeNo(userId, outOrderNo);
    }

    @Override
    public GroupBuyProgressVO queryGroupBuyProgress(String teamId) {
        return tradeOrderRepository.queryGroupBuyProgress(teamId);
    }

    @Override
    public MarketPayOrderEntity lockMarketPayOrder(UserEntity userEntity, PayActivityEntity activityEntity, PayDiscountEntity discountEntity) throws Exception {
        log.info("拼团交易-锁定营销优惠支付订单:{} activityId:{} goodsId:{}", userEntity.getUserId(), activityEntity.getActivityId(), discountEntity.getGoodsId());
        // 交易规则过滤
        TradeRuleFilterBackEntity tradeRuleFilterBack = tradeRuleFilter.apply(TradeRuleCommandEntity.builder()
                        .activityId(activityEntity.getActivityId())
                        .userId(userEntity.getUserId())
                        .build(),
                TradeRuleDynamicContext.builder().build());
        // 已参与拼团量 - 用于构建数据库唯一索引使用，确保用户只能在一个活动上参与固定的次数
        Integer userTakeOrderCount = tradeRuleFilterBack.getUserTakeOrderCount();

        GroupBuyOrderAggregate groupBuyOrderAggregate = GroupBuyOrderAggregate.builder()
                .activityEntity(activityEntity)
                .discountEntity(discountEntity)
                .userEntity(userEntity)
                .userTakeOrderCount(userTakeOrderCount)
                .build();
        // 锁定聚合订单 - 这会用户只是下单还没有支付。后续会有2个流程；支付成功、超时未支付（回退）
        return tradeOrderRepository.lockMarketPayOrder(groupBuyOrderAggregate);
    }
}
