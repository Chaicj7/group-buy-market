package com.chaicj.domain.activity.service.trial.node;

import com.alibaba.fastjson.JSON;
import com.chaicj.domain.activity.model.entity.MarketDynamicContext;
import com.chaicj.domain.activity.model.entity.MarketProductEntity;
import com.chaicj.domain.activity.model.entity.TrialBalanceEntity;
import com.chaicj.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import com.chaicj.domain.activity.model.valobj.SkuVO;
import com.chaicj.domain.activity.service.trial.AbstractGroupBuyMarketSupport;
import com.chaicj.types.design.framework.tree.StrategyHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EndNode extends AbstractGroupBuyMarketSupport<MarketProductEntity, MarketDynamicContext, TrialBalanceEntity> {

    @Override
    public TrialBalanceEntity doApply(MarketProductEntity requestParameter, MarketDynamicContext dynamicContext) throws Exception {
        log.info("拼团商品查询试算服务-EndNode userId:{} requestParameter:{}", requestParameter.getUserId(), JSON.toJSONString(requestParameter));
        SkuVO skuVO = dynamicContext.getSkuVO();
        GroupBuyActivityDiscountVO groupBuyActivityDiscountVO = dynamicContext.getGroupBuyActivityDiscountVO();
        TrialBalanceEntity trialBalance = TrialBalanceEntity.builder()
                .goodsId(skuVO.getGoodsId())
                .goodsName(skuVO.getGoodsName())
                .originalPrice(skuVO.getOriginalPrice())
                .deductionPrice(dynamicContext.getDeductionPrice())
                .payPrice(dynamicContext.getPayPrice())
                .targetCount(groupBuyActivityDiscountVO.getTarget())
                .startTime(groupBuyActivityDiscountVO.getStartTime())
                .endTime(groupBuyActivityDiscountVO.getEndTime())
                .isVisible(dynamicContext.isVisible())
                .isEnable(dynamicContext.isEnable())
                .groupBuyActivityDiscountVO(groupBuyActivityDiscountVO)
                .build();
        return trialBalance;
    }

    @Override
    public StrategyHandler<MarketProductEntity, MarketDynamicContext, TrialBalanceEntity> get(MarketProductEntity requestParameter, MarketDynamicContext dynamicContext) throws Exception {
        return defaultStrategyHandler;
    }
}
