package com.chaicj.infrastructure.adapter.repository;

import com.chaicj.domain.activity.adapter.repository.IActivityRepository;
import com.chaicj.domain.activity.model.valobj.*;
import com.chaicj.infrastructure.dao.GroupBuyActivityDao;
import com.chaicj.infrastructure.dao.GroupBuyDiscountDao;
import com.chaicj.infrastructure.dao.ScSkuActivityDao;
import com.chaicj.infrastructure.dao.SkuDao;
import com.chaicj.infrastructure.dao.po.GroupBuyActivity;
import com.chaicj.infrastructure.dao.po.GroupBuyDiscount;
import com.chaicj.infrastructure.dao.po.SCSkuActivity;
import com.chaicj.infrastructure.dao.po.Sku;
import com.chaicj.infrastructure.redis.IRedisService;
import org.redisson.api.RBitSet;
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
    @Resource
    private ScSkuActivityDao scSkuActivityDao;
    @Resource
    private IRedisService redisService;

    @Override
    public SkuVO querySkuByGoodsId(String goodsId) {
        Sku sku = skuDao.queryByGoodsId(goodsId);
        if (sku == null) return null;
        return SkuVO.builder()
                .goodsId(sku.getGoodsId())
                .goodsName(sku.getGoodsName())
                .originalPrice(sku.getOriginalPrice())
                .build();
    }

    @Override
    public GroupBuyActivityDiscountVO queryGroupBuyActivityDiscountVO(Long activityId) {

        GroupBuyActivity groupBuyActivityRes = groupBuyActivityDao.queryValidGroupBuyActivityId(activityId);
        if (null == groupBuyActivityRes) return null;

        GroupBuyDiscount discount = groupBuyDiscountDao.queryGroupBuyActivityDiscountByDiscountId(groupBuyActivityRes.getDiscountId());
        if (null == discount) return null;

        GroupBuyDiscountVO groupBuyDiscountVO = GroupBuyDiscountVO.builder()
                .discountName(discount.getDiscountName())
                .discountDesc(discount.getDiscountDesc())
                .discountType(DiscountTypeEnum.get(discount.getDiscountType()))
                .marketPlan(discount.getMarketPlan())
                .marketExpr(discount.getMarketExpr())
                .tagId(discount.getTagId())
                .build();
        return GroupBuyActivityDiscountVO.builder()
                .activityId(groupBuyActivityRes.getActivityId())
                .activityName(groupBuyActivityRes.getActivityName())
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

    @Override
    public SCSkuActivityVO querySCSkuActivityBySCGoodsIs(String source, String channel, String goodsId) {
        SCSkuActivity scSkuActivityReq = new SCSkuActivity();
        scSkuActivityReq.setSource(source);
        scSkuActivityReq.setChannel(channel);
        scSkuActivityReq.setGoodsId(goodsId);
        SCSkuActivity scSkuActivityRes = scSkuActivityDao.querySCSkuActivityBySCGoodsId(scSkuActivityReq);
        if (null == scSkuActivityRes) return null;
        return SCSkuActivityVO.builder()
                .source(scSkuActivityRes.getSource())
                .channel(scSkuActivityRes.getChannel())
                .goodsId(scSkuActivityRes.getGoodsId())
                .activityId(scSkuActivityRes.getActivityId())
                .build();
    }

    @Override
    public boolean isTagCrowdRange(String tagId, String userId) {
        RBitSet bitSet = redisService.getBitSet(tagId);
        if (!bitSet.isExists()) return true;
        // 判断用户是否存在人群中
        return bitSet.get(redisService.getIndexFromUserId(userId));
    }
}
