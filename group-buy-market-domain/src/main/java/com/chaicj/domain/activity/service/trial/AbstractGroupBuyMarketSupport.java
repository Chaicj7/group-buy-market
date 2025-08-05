package com.chaicj.domain.activity.service.trial;

import com.chaicj.domain.activity.adapter.repository.IActivityRepository;
import com.chaicj.types.design.framework.tree.AbstractMultiThreadStrategyRouter;

import javax.annotation.Resource;

public abstract class AbstractGroupBuyMarketSupport<MarketProductEntity, DynamicContext, TrialBalanceEntity> extends AbstractMultiThreadStrategyRouter<MarketProductEntity, DynamicContext, TrialBalanceEntity> {

    protected long timeout = 500L;

    @Resource
    protected IActivityRepository activityRepository;

    @Override
    protected void multiThread(MarketProductEntity requestParameter, DynamicContext dynamicContext) throws Exception {

    }
}
