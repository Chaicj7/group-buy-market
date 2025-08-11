package com.chaicj.domain.activity.service.trial.factory;

import com.chaicj.domain.activity.model.entity.MarketDynamicContext;
import com.chaicj.domain.activity.model.entity.MarketProductEntity;
import com.chaicj.domain.activity.model.entity.TrialBalanceEntity;
import com.chaicj.domain.activity.service.trial.node.RootNode;
import com.chaicj.types.design.framework.tree.StrategyHandler;
import org.springframework.stereotype.Service;

@Service
public class DefaultActivityStrategyFactory {

    private final RootNode rootNode;

    public DefaultActivityStrategyFactory(RootNode rootNode) {
        this.rootNode = rootNode;
    }

    public StrategyHandler<MarketProductEntity, MarketDynamicContext, TrialBalanceEntity> strategyHandler() {
        return rootNode;
    }

}
