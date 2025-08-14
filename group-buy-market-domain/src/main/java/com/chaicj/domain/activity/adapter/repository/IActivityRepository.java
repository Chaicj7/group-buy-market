package com.chaicj.domain.activity.adapter.repository;

import com.chaicj.domain.activity.model.entity.UserGroupBuyOrderDetailEntity;
import com.chaicj.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import com.chaicj.domain.activity.model.valobj.SCSkuActivityVO;
import com.chaicj.domain.activity.model.valobj.SkuVO;
import com.chaicj.domain.activity.model.valobj.TeamStatisticVO;

import java.util.List;

public interface IActivityRepository {

    SkuVO querySkuByGoodsId(String goodsId);

    GroupBuyActivityDiscountVO queryGroupBuyActivityDiscountVO(Long activityId);

    SCSkuActivityVO querySCSkuActivityBySCGoodsIs(String source, String channel, String goodsId);

    boolean isTagCrowdRange(String tagId, String userId);

    boolean downgradeSwitch();

    boolean cutRange(String userId);

    TeamStatisticVO queryTeamStatisticByActivityId(Long activityId);

    List<UserGroupBuyOrderDetailEntity> queryInProgressUserGroupBuyOrderDetailListByOwner(Long activityId, String userId, int ownerCount);

    List<UserGroupBuyOrderDetailEntity> queryInProgressUserGroupBuyOrderDetailListByRandom(Long activityId, String userId, int randomCount);
}
