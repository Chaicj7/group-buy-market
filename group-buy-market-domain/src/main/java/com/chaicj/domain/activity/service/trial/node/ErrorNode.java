package com.chaicj.domain.activity.service.trial.node;

import com.alibaba.fastjson.JSON;
import com.chaicj.domain.activity.model.entity.MarketDynamicContext;
import com.chaicj.domain.activity.model.entity.MarketProductEntity;
import com.chaicj.domain.activity.model.entity.TrialBalanceEntity;
import com.chaicj.domain.activity.service.trial.AbstractGroupBuyMarketSupport;
import com.chaicj.types.design.framework.tree.StrategyHandler;
import com.chaicj.types.enums.ResponseCode;
import com.chaicj.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ErrorNode extends AbstractGroupBuyMarketSupport<MarketProductEntity, MarketDynamicContext, TrialBalanceEntity> {

    @Override
    protected TrialBalanceEntity doApply(MarketProductEntity requestParameter, MarketDynamicContext dynamicContext) throws Exception {
        log.info("拼团商品查询试算服务-NoMarketNode userId:{} requestParameter:{}", requestParameter.getUserId(), JSON.toJSONString(requestParameter));
        if (dynamicContext.getSkuVO() == null || dynamicContext.getGroupBuyActivityDiscountVO() == null) {
            log.info("商品无拼团营销配置 {}", requestParameter.getGoodsId());
            throw new AppException(ResponseCode.E0002.getCode(), ResponseCode.E0002.getInfo());
        }
        return TrialBalanceEntity.builder().build();
    }

    @Override
    public StrategyHandler<MarketProductEntity, MarketDynamicContext, TrialBalanceEntity> get(MarketProductEntity requestParameter, MarketDynamicContext dynamicContext) throws Exception {
        return defaultStrategyHandler;
    }
}
