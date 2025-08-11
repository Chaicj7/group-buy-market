package com.chaicj.domain.activity.service.trial.node;

import com.alibaba.fastjson.JSON;
import com.chaicj.domain.activity.model.entity.MarketDynamicContext;
import com.chaicj.domain.activity.model.entity.MarketProductEntity;
import com.chaicj.domain.activity.model.entity.TrialBalanceEntity;
import com.chaicj.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import com.chaicj.domain.activity.model.valobj.GroupBuyDiscountVO;
import com.chaicj.domain.activity.model.valobj.SkuVO;
import com.chaicj.domain.activity.service.discount.IDiscountCalculateService;
import com.chaicj.domain.activity.service.trial.AbstractGroupBuyMarketSupport;
import com.chaicj.domain.activity.service.trial.thread.QueryGroupBuyActivityDiscountVOThreadTask;
import com.chaicj.domain.activity.service.trial.thread.QuerySkuVOThreadTask;
import com.chaicj.types.design.framework.tree.StrategyHandler;
import com.chaicj.types.enums.ResponseCode;
import com.chaicj.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class MarketNode extends AbstractGroupBuyMarketSupport<MarketProductEntity, MarketDynamicContext, TrialBalanceEntity> {

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;
    @Resource
    private TagNode tagNode;
    @Resource
    private ErrorNode errorNode;
    @Resource
    private Map<String, IDiscountCalculateService> discountCalculateServiceMap;

    @Override
    protected void multiThread(MarketProductEntity requestParameter, MarketDynamicContext dynamicContext) throws Exception {
        QueryGroupBuyActivityDiscountVOThreadTask queryGroupBuyActivityDiscountVOThreadTask = new QueryGroupBuyActivityDiscountVOThreadTask(requestParameter.getSource(), requestParameter.getChannel(), requestParameter.getGoodsId(), activityRepository);
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
    public TrialBalanceEntity doApply(MarketProductEntity requestParameter, MarketDynamicContext dynamicContext) throws Exception {
        log.info("拼团商品查询试算服务-MarketNode userId:{} requestParameter:{}", requestParameter.getUserId(), JSON.toJSONString(requestParameter));
        // 拼图试算优惠
        if (dynamicContext.getGroupBuyActivityDiscountVO() == null || dynamicContext.getSkuVO() == null) {
            return routor(requestParameter, dynamicContext);
        }
        GroupBuyActivityDiscountVO groupBuyActivityDiscountVO = dynamicContext.getGroupBuyActivityDiscountVO();
        GroupBuyDiscountVO groupBuyDiscount = groupBuyActivityDiscountVO.getGroupBuyDiscount();
        SkuVO skuVO = dynamicContext.getSkuVO();

        IDiscountCalculateService discountCalculateService = discountCalculateServiceMap.get(groupBuyDiscount.getMarketPlan());
        if (null == discountCalculateService) {
            log.info("不存在{}类型的折扣计算服务，支持类型为:{}", groupBuyDiscount.getMarketPlan(), JSON.toJSONString(discountCalculateServiceMap.keySet()));
            throw new AppException(ResponseCode.E0001.getCode(), ResponseCode.E0001.getInfo());
        }
        // 折扣价格
        BigDecimal calculate = discountCalculateService.calculate(requestParameter.getUserId(), skuVO.getOriginalPrice(), groupBuyDiscount);
        dynamicContext.setDeductionPrice(skuVO.getOriginalPrice().subtract(calculate));
        dynamicContext.setPayPrice(calculate);
        return routor(requestParameter, dynamicContext);
    }

    @Override
    public StrategyHandler<MarketProductEntity, MarketDynamicContext, TrialBalanceEntity> get(MarketProductEntity requestParameter, MarketDynamicContext dynamicContext) throws Exception {
        // 不存在配置的拼团活动，走异常节点
        if (dynamicContext.getPayPrice() == null || dynamicContext.getGroupBuyActivityDiscountVO() == null || dynamicContext.getSkuVO() == null) {
            return errorNode;
        }
        return tagNode;
    }
}
