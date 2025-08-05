package com.chaicj.domain.activity.service.trial.node;

import com.alibaba.fastjson.JSON;
import com.chaicj.domain.activity.model.entity.DynamicContext;
import com.chaicj.domain.activity.model.entity.MarketProductEntity;
import com.chaicj.domain.activity.model.entity.TrialBalanceEntity;
import com.chaicj.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import com.chaicj.domain.activity.model.valobj.SkuVO;
import com.chaicj.domain.activity.service.trial.AbstractGroupBuyMarketSupport;
import com.chaicj.domain.activity.service.trial.thread.QueryGroupBuyActivityDiscountVOThreadTask;
import com.chaicj.domain.activity.service.trial.thread.QuerySkuVOThreadTask;
import com.chaicj.types.design.framework.tree.StrategyHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class MarketNode extends AbstractGroupBuyMarketSupport<MarketProductEntity, DynamicContext, TrialBalanceEntity> {

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;
    @Resource
    private EndNode endNode;

    @Override
    protected void multiThread(MarketProductEntity requestParameter, DynamicContext dynamicContext) throws Exception {
        QueryGroupBuyActivityDiscountVOThreadTask queryGroupBuyActivityDiscountVOThreadTask = new QueryGroupBuyActivityDiscountVOThreadTask(requestParameter.getSource() ,requestParameter.getChannel(), activityRepository);
        FutureTask<GroupBuyActivityDiscountVO> groupBuyActivityVOFutureTask = new FutureTask<>(queryGroupBuyActivityDiscountVOThreadTask);
        threadPoolExecutor.execute(groupBuyActivityVOFutureTask);

        QuerySkuVOThreadTask querySkuVOThreadTask = new QuerySkuVOThreadTask(requestParameter.getGoodsId(), activityRepository);
        FutureTask<SkuVO> skuVOFutureTask = new FutureTask<>(querySkuVOThreadTask);
        threadPoolExecutor.execute(skuVOFutureTask);

        dynamicContext.setSkuVO(skuVOFutureTask.get(timeout, TimeUnit.MILLISECONDS));
        dynamicContext.setGroupBuyActivityDiscountVO(groupBuyActivityVOFutureTask.get(timeout, TimeUnit.MILLISECONDS));
        log.info("拼团商品查询试算服务-MarketNode userId:{} 异步线程加载数据「GroupBuyActivityDiscountVO、SkuVO」完成", requestParameter.getUserId());
    }

    @Override
    public TrialBalanceEntity doApply(MarketProductEntity requestParameter, DynamicContext dynamicContext) throws Exception {
        log.info("拼团商品查询试算服务-MarketNode userId:{} requestParameter:{}", requestParameter.getUserId(), JSON.toJSONString(requestParameter));
        // TODO 拼图试算优惠
        return routor(requestParameter, dynamicContext);
    }

    @Override
    public StrategyHandler<MarketProductEntity, DynamicContext, TrialBalanceEntity> get(MarketProductEntity requestParameter, DynamicContext dynamicContext) throws Exception {
        return endNode;
    }
}
