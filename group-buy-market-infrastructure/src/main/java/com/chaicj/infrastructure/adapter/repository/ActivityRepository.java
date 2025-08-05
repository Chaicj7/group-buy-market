package com.chaicj.infrastructure.adapter.repository;

import com.chaicj.domain.activity.adapter.repository.IActivityRepository;
import com.chaicj.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import com.chaicj.domain.activity.model.valobj.GroupBuyDiscountVO;
import com.chaicj.domain.activity.model.valobj.SkuVO;
import com.chaicj.infrastructure.dao.GroupBuyActivityDao;
import com.chaicj.infrastructure.dao.GroupBuyDiscountDao;
import com.chaicj.infrastructure.dao.SkuDao;
import com.chaicj.infrastructure.dao.po.GroupBuyActivity;
import com.chaicj.infrastructure.dao.po.GroupBuyDiscount;
import com.chaicj.infrastructure.dao.po.Sku;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ActivityRepository implements IActivityRepository {

    @Resource
    private SkuDao skuDao;
    @Resource
    private GroupBuyActivityDao groupBuyActivityDao;
    @Resource
    private GroupBuyDiscountDao groupBuyDiscountDao;

    @Override
    public SkuVO querySkuByGoodsId(String goodsId) {
        Sku sku = skuDao.queryByGoodsId(goodsId);
        return SkuVO.builder()
                .goodsId(sku.getGoodsId())
                .goodsName(sku.getGoodsName())
                .originalPrice(sku.getOriginalPrice())
                .build();
    }

    @Override
    public GroupBuyActivityDiscountVO queryGroupBuyActivityDiscountVO(String source, String channel) {
        GroupBuyActivity groupBuyActivityReq = new GroupBuyActivity();
        groupBuyActivityReq.setSource(source);
        groupBuyActivityReq.setChannel(channel);
        GroupBuyActivity groupBuyActivityRes = groupBuyActivityDao.queryValidGroupBuyActivity(groupBuyActivityReq);

        GroupBuyDiscount discount = groupBuyDiscountDao.queryGroupBuyActivityDiscountByDiscountId(groupBuyActivityRes.getDiscountId());
        GroupBuyDiscountVO groupBuyDiscountVO = GroupBuyDiscountVO.builder()
                .discountName(discount.getDiscountName())
                .discountDesc(discount.getDiscountDesc())
                .discountType(discount.getDiscountType())
                .marketPlan(discount.getMarketPlan())
                .marketExpr(discount.getMarketExpr())
                .tagId(discount.getTagId())
                .build();
        return GroupBuyActivityDiscountVO.builder()
                .activityId(groupBuyActivityRes.getActivityId())
                .activityName(groupBuyActivityRes.getActivityName())
                .source(groupBuyActivityRes.getSource())
                .channel(groupBuyActivityRes.getChannel())
                .goodsId(groupBuyActivityRes.getGoodsId())
                .groupBuyDiscount(groupBuyDiscountVO)
                .groupType(groupBuyActivityRes.getGroupType())
                .takeLimitCount(groupBuyActivityRes.getTakeLimitCount())
                .target(groupBuyActivityRes.getTarget())
                .validTime(groupBuyActivityRes.getValidTime())
                .status(groupBuyActivityRes.getStatus())
                .startTime(groupBuyActivityRes.getStartTime())
                .endTime(groupBuyActivityRes.getEndTime())
                .tagId(groupBuyActivityRes.getTagId())
                .tagScope(groupBuyActivityRes.getTagScope())
                .build();
    }
}
