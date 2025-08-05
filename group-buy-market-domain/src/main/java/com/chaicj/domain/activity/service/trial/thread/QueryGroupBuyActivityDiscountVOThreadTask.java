package com.chaicj.domain.activity.service.trial.thread;

import com.chaicj.domain.activity.adapter.repository.IActivityRepository;
import com.chaicj.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import com.chaicj.domain.activity.model.valobj.SCSkuActivityVO;

import java.util.concurrent.Callable;

public class QueryGroupBuyActivityDiscountVOThreadTask implements Callable<GroupBuyActivityDiscountVO> {

    private final String source;

    private final String channel;

    private final String goodsId;

    private final IActivityRepository activityRepository;

    public QueryGroupBuyActivityDiscountVOThreadTask(String source, String channel, String goodsId, IActivityRepository activityRepository) {
        this.source = source;
        this.channel = channel;
        this.goodsId = goodsId;
        this.activityRepository = activityRepository;
    }

    @Override
    public GroupBuyActivityDiscountVO call() throws Exception {
        // 查询渠道商品活动配置关联配置
        SCSkuActivityVO scSkuActivityVO = activityRepository.querySCSkuActivityBySCGoodsIs(source, channel, goodsId);
        if (null == scSkuActivityVO) return null;
        // 查询活动配置
        GroupBuyActivityDiscountVO groupBuyActivityDiscountVO = activityRepository.queryGroupBuyActivityDiscountVO(scSkuActivityVO.getActivityId());
        if (groupBuyActivityDiscountVO == null) return null;
        groupBuyActivityDiscountVO.setScSkuActivity(scSkuActivityVO);
        return groupBuyActivityDiscountVO;
    }
}
