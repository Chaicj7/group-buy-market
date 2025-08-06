package com.chaicj.infrastructure.adapter.repository;

import com.chaicj.domain.activity.model.valobj.GroupBuyDiscountVO;
import com.chaicj.domain.trade.adapter.repository.ITradeOrderRepository;
import com.chaicj.domain.trade.model.aggregate.GroupBuyOrderAggregate;
import com.chaicj.domain.trade.model.entity.MarketPayOrderEntity;
import com.chaicj.domain.trade.model.entity.PayActivityEntity;
import com.chaicj.domain.trade.model.entity.PayDiscountEntity;
import com.chaicj.domain.trade.model.entity.UserEntity;
import com.chaicj.domain.trade.model.valobj.GroupBuyProgressVO;
import com.chaicj.domain.trade.model.valobj.TradeOrderStatusEnumVO;
import com.chaicj.infrastructure.dao.GroupBuyOrderDao;
import com.chaicj.infrastructure.dao.GroupBuyOrderListDao;
import com.chaicj.infrastructure.dao.po.GroupBuyOrder;
import com.chaicj.infrastructure.dao.po.GroupBuyOrderList;
import com.chaicj.types.enums.ResponseCode;
import com.chaicj.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Slf4j
@Component
public class TradeOrderRepository implements ITradeOrderRepository {

    @Resource
    private GroupBuyOrderListDao groupBuyOrderListDao;
    @Resource
    private GroupBuyOrderDao groupBuyOrderDao;

    @Override
    public MarketPayOrderEntity queryNoPayMarketPayOrderByOutTradeNo(String userId, String outOrderNo) {
        GroupBuyOrderList groupBuyOrderListReq = new GroupBuyOrderList();
        groupBuyOrderListReq.setUserId(userId);
        groupBuyOrderListReq.setOutTradeNo(outOrderNo);
        GroupBuyOrderList orderList = groupBuyOrderListDao.queryGroupBuyOrderRecordByOutTradeNo(groupBuyOrderListReq);
        if (orderList == null) return null;
        return MarketPayOrderEntity.builder()
                .orderId(orderList.getOrderId())
                .deductionPrice(orderList.getDeductionPrice())
                .tradeOrderStatus(TradeOrderStatusEnumVO.valueOf(orderList.getStatus()))
                .build();
    }

    @Override
    public GroupBuyProgressVO queryGroupBuyProgress(String teamId) {
        GroupBuyOrder groupBuyOrder = groupBuyOrderDao.queryGroupBuyProgress(teamId);
        if (null == groupBuyOrder) return null;
        return GroupBuyProgressVO.builder()
                .targetCount(groupBuyOrder.getTargetCount())
                .completeCount(groupBuyOrder.getCompleteCount())
                .lockCount(groupBuyOrder.getLockCount())
                .build();
    }

    @Transactional(timeout = 500)
    @Override
    public MarketPayOrderEntity lockMarketPayOrder(GroupBuyOrderAggregate groupBuyOrderAggregate) {
        UserEntity userEntity = groupBuyOrderAggregate.getUserEntity();
        PayActivityEntity activityEntity = groupBuyOrderAggregate.getActivityEntity();
        PayDiscountEntity discountEntity = groupBuyOrderAggregate.getDiscountEntity();

        String teamId = activityEntity.getTeamId();
        // teamId为空
        if (StringUtils.isBlank(teamId)) {
            teamId = RandomStringUtils.randomNumeric(8);

            GroupBuyOrder groupBuyOrder = GroupBuyOrder.builder()
                    .teamId(teamId)
                    .activityId(activityEntity.getActivityId())
                    .source(discountEntity.getSource())
                    .channel(discountEntity.getChannel())
                    .originalPrice(discountEntity.getOriginalPrice())
                    .deductionPrice(discountEntity.getDeductionPrice())
                    .payPrice(discountEntity.getDeductionPrice())
                    .targetCount(activityEntity.getTargetCount())
                    .completeCount(0)
                    .lockCount(1)
                    .status(TradeOrderStatusEnumVO.CREATE.getCode())
                    .build();
            groupBuyOrderDao.insert(groupBuyOrder);
        } else {
            // 更新一下 groupBuyOrder
            int updateCount = groupBuyOrderDao.updateAddLockCount(teamId);
            if (updateCount != 1) {
                throw new AppException(ResponseCode.E0006);
            }
        }
        // 使用 RandomStringUtils.randomNumeric 替代公司里使用的雪花算法UUID
        String orderId = RandomStringUtils.randomNumeric(12);
        GroupBuyOrderList groupBuyOrderList = GroupBuyOrderList.builder()
                .userId(userEntity.getUserId())
                .teamId(teamId)
                .orderId(orderId)
                .activityId(activityEntity.getActivityId())
                .startTime(activityEntity.getStartTime())
                .endTime(activityEntity.getEndTime())
                .goodsId(discountEntity.getGoodsId())
                .source(discountEntity.getSource())
                .channel(discountEntity.getChannel())
                .originalPrice(discountEntity.getOriginalPrice())
                .deductionPrice(discountEntity.getDeductionPrice())
                .status(TradeOrderStatusEnumVO.CREATE.getCode())
                .outTradeNo(discountEntity.getOutTradeNo())
                .build();
        try {
            // 写入拼团记录
            groupBuyOrderListDao.insert(groupBuyOrderList);
        } catch (DuplicateKeyException e) {
            throw new AppException(ResponseCode.INDEX_EXCEPTION);
        }

        return MarketPayOrderEntity.builder()
                .orderId(orderId)
                .deductionPrice(discountEntity.getDeductionPrice())
                .tradeOrderStatus(TradeOrderStatusEnumVO.CREATE)
                .build();
    }
}
