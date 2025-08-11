package com.chaicj.domain.trade.service.filter;

import com.chaicj.domain.trade.adapter.repository.ITradeOrderRepository;
import com.chaicj.domain.trade.model.entity.GroupBuyActivityEntity;
import com.chaicj.domain.trade.model.entity.TradeRuleCommandEntity;
import com.chaicj.domain.trade.model.entity.TradeRuleDynamicContext;
import com.chaicj.domain.trade.model.entity.TradeRuleFilterBackEntity;
import com.chaicj.types.design.framework.link.model2.handler.ILogicHandler;
import com.chaicj.types.enums.ActivityStatusEnumVO;
import com.chaicj.types.enums.ResponseCode;
import com.chaicj.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 活动的可用性，规则过滤【状态、有效期】
 */

@Slf4j
@Component
public class ActivityUsabilityRuleFilter implements ILogicHandler<TradeRuleCommandEntity, TradeRuleDynamicContext, TradeRuleFilterBackEntity> {

    @Resource
    private ITradeOrderRepository repository;


    @Override
    public TradeRuleFilterBackEntity apply(TradeRuleCommandEntity requestParameter, TradeRuleDynamicContext dynamicContext) throws Exception {
        log.info("交易规则过滤-活动的可用性校验{} activityId:{}", requestParameter.getUserId(), requestParameter.getActivityId());
        // 查询拼团活动
        GroupBuyActivityEntity groupBuyActivityEntity = repository.queryGroupBuyActivityEntityByActivityId(requestParameter.getActivityId());
        // 校验；活动状态 - 可以抛业务异常code，或者把code写入到动态上下文dynamicContext中，最后获取。
        if (!ActivityStatusEnumVO.EFFECTIVE.equals(groupBuyActivityEntity.getStatus())) {
            log.info("活动的可用性校验，非生效状态 activityId:{}", requestParameter.getActivityId());
            throw new AppException(ResponseCode.E0101);
        }
        // 校验；活动时间
        Date currentDate = new Date();
        if (currentDate.before(groupBuyActivityEntity.getStartTime()) || currentDate.after(groupBuyActivityEntity.getEndTime())) {
            log.info("活动的可用性校验，非可参与时间范围 activityId:{}", requestParameter.getActivityId());
            throw new AppException(ResponseCode.E0102);
        }
        // 写入动态上下文
        dynamicContext.setGroupBuyActivity(groupBuyActivityEntity);
        // 走到下一个责任链节点
        return next(requestParameter, dynamicContext);
    }
}
