package com.chaicj.domain.activity.service.trial.node;

import com.alibaba.fastjson.JSON;
import com.chaicj.domain.activity.model.entity.DynamicContext;
import com.chaicj.domain.activity.model.entity.MarketProductEntity;
import com.chaicj.domain.activity.model.entity.TrialBalanceEntity;
import com.chaicj.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import com.chaicj.domain.activity.model.valobj.GroupBuyDiscountVO;
import com.chaicj.domain.activity.model.valobj.SkuVO;
import com.chaicj.domain.activity.service.trial.AbstractGroupBuyMarketSupport;
import com.chaicj.types.design.framework.tree.StrategyHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class EndNode extends AbstractGroupBuyMarketSupport<MarketProductEntity, DynamicContext, TrialBalanceEntity> {

    @Override
    public TrialBalanceEntity doApply(MarketProductEntity requestParameter, DynamicContext dynamicContext) throws Exception {
        log.info("拼团商品查询试算服务-EndNode userId:{} requestParameter:{}", requestParameter.getUserId(), JSON.toJSONString(requestParameter));
        SkuVO skuVO = dynamicContext.getSkuVO();
        GroupBuyActivityDiscountVO groupBuyActivityDiscountVO = dynamicContext.getGroupBuyActivityDiscountVO();
        GroupBuyDiscountVO groupBuyDiscount = groupBuyActivityDiscountVO.getGroupBuyDiscount();
        TrialBalanceEntity trialBalance = TrialBalanceEntity.builder()
                .goodsId(skuVO.getGoodsId())
                .goodsName(skuVO.getGoodsName())
                .originalPrice(skuVO.getOriginalPrice())
                .deductionPrice(dynamicContext.getDeductionPrice())
                .targetCount(groupBuyActivityDiscountVO.getTarget())
                .startTime(groupBuyActivityDiscountVO.getStartTime())
                .endTime(groupBuyActivityDiscountVO.getEndTime())
                .isVisible(false)
                .isEnable(false)
                .build();
        return trialBalance;
    }

    @Override
    public StrategyHandler<MarketProductEntity, DynamicContext, TrialBalanceEntity> get(MarketProductEntity requestParameter, DynamicContext dynamicContext) throws Exception {
        return defaultStrategyHandler;
    }
}
