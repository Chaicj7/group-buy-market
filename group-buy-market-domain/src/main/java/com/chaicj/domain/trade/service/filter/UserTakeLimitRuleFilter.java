package com.chaicj.domain.trade.service.filter;

import com.chaicj.domain.trade.adapter.repository.ITradeOrderRepository;
import com.chaicj.domain.trade.model.entity.GroupBuyActivityEntity;
import com.chaicj.domain.trade.model.entity.TradeRuleCommandEntity;
import com.chaicj.domain.trade.model.entity.TradeRuleDynamicContext;
import com.chaicj.domain.trade.model.entity.TradeRuleFilterBackEntity;
import com.chaicj.types.design.framework.link.model2.handler.ILogicHandler;
import com.chaicj.types.enums.ResponseCode;
import com.chaicj.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class UserTakeLimitRuleFilter implements ILogicHandler<TradeRuleCommandEntity, TradeRuleDynamicContext, TradeRuleFilterBackEntity> {

    @Resource
    private ITradeOrderRepository repository;

    @Override
    public TradeRuleFilterBackEntity apply(TradeRuleCommandEntity requestParameter, TradeRuleDynamicContext dynamicContext) throws Exception {
        log.info("交易规则过滤-用户参与次数校验{} activityId:{}", requestParameter.getUserId(), requestParameter.getActivityId());
        GroupBuyActivityEntity groupBuyActivity = dynamicContext.getGroupBuyActivity();
        // 查询用户在一个拼团活动上参与的次数
        Integer count = repository.queryOrderCountByActivityId(requestParameter.getActivityId(), requestParameter.getUserId());
        if (groupBuyActivity.getTakeLimitCount() != null && count >= groupBuyActivity.getTakeLimitCount()) {
            log.info("用户参与次数校验，已达可参与上限 activityId:{}", requestParameter.getActivityId());
            throw new AppException(ResponseCode.E0103);
        }
        return TradeRuleFilterBackEntity.builder()
                .userTakeOrderCount(count)
                .build();
    }
}
