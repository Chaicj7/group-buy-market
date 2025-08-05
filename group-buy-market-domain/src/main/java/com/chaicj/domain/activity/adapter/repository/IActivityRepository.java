package com.chaicj.domain.activity.adapter.repository;

import com.chaicj.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import com.chaicj.domain.activity.model.valobj.SCSkuActivityVO;
import com.chaicj.domain.activity.model.valobj.SkuVO;

public interface IActivityRepository {

    SkuVO querySkuByGoodsId(String goodsId);

    GroupBuyActivityDiscountVO queryGroupBuyActivityDiscountVO(Long activityId);

    SCSkuActivityVO querySCSkuActivityBySCGoodsIs(String source, String channel, String goodsId);
}
