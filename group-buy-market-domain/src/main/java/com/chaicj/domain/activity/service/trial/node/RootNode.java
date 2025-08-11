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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RootNode extends AbstractGroupBuyMarketSupport<MarketProductEntity, MarketDynamicContext, TrialBalanceEntity> {

    @Autowired
    private SwitchRoot switchRoot;

    @Override
    protected TrialBalanceEntity doApply(MarketProductEntity requestParameter, MarketDynamicContext dynamicContext) throws Exception {
        log.info("拼图商品查询试算服务-RootNode userId:{} requestParameter:{}", requestParameter.getUserId(), JSON.toJSONString(requestParameter));
        // 参数判断
        if (StringUtils.isBlank(requestParameter.getUserId()) || StringUtils.isBlank(requestParameter.getGoodsId())
                || StringUtils.isBlank(requestParameter.getChannel()) || StringUtils.isBlank(requestParameter.getSource())) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
        }
        return routor(requestParameter, dynamicContext);
    }

    @Override
    public StrategyHandler<MarketProductEntity, MarketDynamicContext, TrialBalanceEntity> get(MarketProductEntity requestParameter, MarketDynamicContext dynamicContext) throws Exception {
        return switchRoot;
    }
}
