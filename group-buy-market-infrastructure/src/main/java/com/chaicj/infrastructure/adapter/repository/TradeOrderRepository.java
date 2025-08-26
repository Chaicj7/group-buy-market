package com.chaicj.infrastructure.adapter.repository;

import com.alibaba.fastjson.JSON;
import com.chaicj.domain.activity.model.valobj.GroupBuyDiscountVO;
import com.chaicj.domain.trade.adapter.repository.ITradeOrderRepository;
import com.chaicj.domain.trade.model.aggregate.GroupBuyOrderAggregate;
import com.chaicj.domain.trade.model.aggregate.GroupBuyTeamSettlementAggregate;
import com.chaicj.domain.trade.model.entity.*;
import com.chaicj.domain.trade.model.valobj.GroupBuyProgressVO;
import com.chaicj.domain.trade.model.valobj.NotifyTypeEnumVO;
import com.chaicj.domain.trade.model.valobj.TradeOrderStatusEnumVO;
import com.chaicj.infrastructure.dao.GroupBuyActivityDao;
import com.chaicj.infrastructure.dao.GroupBuyOrderDao;
import com.chaicj.infrastructure.dao.GroupBuyOrderListDao;
import com.chaicj.infrastructure.dao.NotifyTaskDao;
import com.chaicj.infrastructure.dao.po.GroupBuyActivity;
import com.chaicj.infrastructure.dao.po.GroupBuyOrder;
import com.chaicj.infrastructure.dao.po.GroupBuyOrderList;
import com.chaicj.infrastructure.dao.po.NotifyTask;
import com.chaicj.infrastructure.dcc.DCCService;
import com.chaicj.infrastructure.redis.IRedisService;
import com.chaicj.types.common.Constants;
import com.chaicj.types.enums.ActivityStatusEnumVO;
import com.chaicj.types.enums.GroupBuyOrderEnumVO;
import com.chaicj.types.enums.ResponseCode;
import com.chaicj.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TradeOrderRepository implements ITradeOrderRepository {

    @Resource
    private GroupBuyOrderListDao groupBuyOrderListDao;
    @Resource
    private GroupBuyOrderDao groupBuyOrderDao;
    @Resource
    private GroupBuyActivityDao groupBuyActivityDao;
    @Resource
    private NotifyTaskDao notifyTaskDao;
    @Resource
    private DCCService dccService;

    @Value("${spring.rabbitmq.config.producer.topic_team_success.routing_key}")
    private String topic_team_success;
    @Resource
    private IRedisService redisService;

    @Override
    public MarketPayOrderEntity queryNoPayMarketPayOrderByOutTradeNo(String userId, String outOrderNo) {
        GroupBuyOrderList groupBuyOrderListReq = new GroupBuyOrderList();
        groupBuyOrderListReq.setUserId(userId);
        groupBuyOrderListReq.setOutTradeNo(outOrderNo);
        GroupBuyOrderList orderList = groupBuyOrderListDao.queryGroupBuyOrderRecordByOutTradeNo(groupBuyOrderListReq);
        if (orderList == null) return null;
        return MarketPayOrderEntity.builder()
                .teamId(orderList.getTeamId())
                .orderId(orderList.getOrderId())
                .originalPrice(orderList.getOriginalPrice())
                .deductionPrice(orderList.getDeductionPrice())
                .payPrice(orderList.getPayPrice())
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
        NotifyConfigVO notifyConfigVO = discountEntity.getNotifyConfigVO();
        Integer userTakeOrderCount = groupBuyOrderAggregate.getUserTakeOrderCount();

        String teamId = activityEntity.getTeamId();
        // teamId为空
        if (StringUtils.isBlank(teamId)) {
            teamId = RandomStringUtils.randomNumeric(8);
            Date currentDate = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentDate);
            calendar.add(Calendar.MINUTE, activityEntity.getValidTime());

            GroupBuyOrder groupBuyOrder = GroupBuyOrder.builder()
                    .teamId(teamId)
                    .activityId(activityEntity.getActivityId())
                    .source(discountEntity.getSource())
                    .channel(discountEntity.getChannel())
                    .originalPrice(discountEntity.getOriginalPrice())
                    .deductionPrice(discountEntity.getDeductionPrice())
                    .payPrice(discountEntity.getPayPrice())
                    .targetCount(activityEntity.getTargetCount())
                    .completeCount(0)
                    .lockCount(1)
                    .status(TradeOrderStatusEnumVO.CREATE.getCode())
                    .validStartTime(currentDate)
                    .validEndTime(calendar.getTime())
                    .notifyType(notifyConfigVO.getNotifyType().getCode())
                    .notifyUrl(notifyConfigVO.getNotifyUrl())
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
                .payPrice(discountEntity.getPayPrice())
                .status(TradeOrderStatusEnumVO.CREATE.getCode())
                .outTradeNo(discountEntity.getOutTradeNo())
                .bizId(activityEntity.getActivityId() + Constants.UNDERLINE + userEntity.getUserId() + Constants.UNDERLINE + userTakeOrderCount)
                .build();
        try {
            // 写入拼团记录
            groupBuyOrderListDao.insert(groupBuyOrderList);
        } catch (DuplicateKeyException e) {
            throw new AppException(ResponseCode.INDEX_EXCEPTION);
        }

        return MarketPayOrderEntity.builder()
                .orderId(orderId)
                .originalPrice(discountEntity.getOriginalPrice())
                .deductionPrice(discountEntity.getDeductionPrice())
                .payPrice(discountEntity.getPayPrice())
                .tradeOrderStatus(TradeOrderStatusEnumVO.CREATE)
                .build();
    }

    @Override
    public GroupBuyActivityEntity queryGroupBuyActivityEntityByActivityId(Long activityId) {
        GroupBuyActivity groupBuyActivity = groupBuyActivityDao.queryByActivityId(activityId);
        if (null == groupBuyActivity) return null;
        return GroupBuyActivityEntity.builder()
                .activityId(groupBuyActivity.getActivityId())
                .activityName(groupBuyActivity.getActivityName())
                .discountId(groupBuyActivity.getDiscountId())
                .groupType(groupBuyActivity.getGroupType())
                .takeLimitCount(groupBuyActivity.getTakeLimitCount())
                .target(groupBuyActivity.getTarget())
                .validTime(groupBuyActivity.getValidTime())
                .status(ActivityStatusEnumVO.valueOf(groupBuyActivity.getStatus()))
                .startTime(groupBuyActivity.getStartTime())
                .endTime(groupBuyActivity.getEndTime())
                .tagId(groupBuyActivity.getTagId())
                .tagScope(groupBuyActivity.getTagScope())
                .build();
    }

    @Override
    public Integer queryOrderCountByActivityId(Long activityId, String userId) {
        GroupBuyOrderList groupBuyOrderListReq = new GroupBuyOrderList();
        groupBuyOrderListReq.setActivityId(activityId);
        groupBuyOrderListReq.setUserId(userId);
        return groupBuyOrderListDao.queryOrderCountByActivityId(groupBuyOrderListReq);
    }

    @Override
    public GroupBuyTeamEntity queryGroupBuyTeamByTeamId(String teamId) {
        GroupBuyOrder groupBuyOrder = groupBuyOrderDao.queryGroupBuyProgress(teamId);
        return GroupBuyTeamEntity.builder()
                .teamId(groupBuyOrder.getTeamId())
                .activityId(groupBuyOrder.getActivityId())
                .targetCount(groupBuyOrder.getTargetCount())
                .completeCount(groupBuyOrder.getCompleteCount())
                .lockCount(groupBuyOrder.getLockCount())
                .status(GroupBuyOrderEnumVO.valueOf(groupBuyOrder.getStatus()))
                .validStartTime(groupBuyOrder.getValidStartTime())
                .validEndTime(groupBuyOrder.getValidEndTime())
                .notifyConfigVO(NotifyConfigVO.builder()
                        .notifyType(NotifyTypeEnumVO.valueOf(groupBuyOrder.getNotifyType()))
                        .notifyUrl(groupBuyOrder.getNotifyUrl())
                        .notifyMQ(topic_team_success)
                        .build())
                .build();
    }

//    @Transactional(timeout = 500)
    @Override
    public NotifyTaskEntity settlementMarketPayOrder(GroupBuyTeamSettlementAggregate settlementAggregate) {
        UserEntity userEntity = settlementAggregate.getUserEntity();
        GroupBuyTeamEntity groupBuyTeamEntity = settlementAggregate.getGroupBuyTeamEntity();
        NotifyConfigVO notifyConfigVO = groupBuyTeamEntity.getNotifyConfigVO();
        TradePaySuccessEntity tradePaySuccessEntity = settlementAggregate.getTradePaySuccessEntity();

        // 1. 更新拼团订单明细状态
        GroupBuyOrderList groupBuyOrderListReq = new GroupBuyOrderList();
        groupBuyOrderListReq.setUserId(userEntity.getUserId());
        groupBuyOrderListReq.setOutTradeNo(tradePaySuccessEntity.getOutTradeNo());
        groupBuyOrderListReq.setOutTradeTime(tradePaySuccessEntity.getOutTradeTime());
        int updateOrderListStatusCount = groupBuyOrderListDao.updateOrderStatus2COMPLETE(groupBuyOrderListReq);
        if (updateOrderListStatusCount != 1) {
            throw new AppException(ResponseCode.UPDATE_ZERO);
        }

        // 2. 更新拼团达成数量
        int updateAddCount = groupBuyOrderDao.updateAddCompleteCount(groupBuyTeamEntity.getTeamId());
        if (1 != updateAddCount) {
            throw new AppException(ResponseCode.UPDATE_ZERO);
        }

        // 3. 更新拼团完成状态  最有一个拼团， 拼团完成
        if (groupBuyTeamEntity.getTargetCount() - groupBuyTeamEntity.getCompleteCount() == 1) {
            int updateOrderStatusCount = groupBuyOrderDao.updateOrderStatus2COMPLETE(groupBuyTeamEntity.getTeamId());
            if (1 != updateOrderStatusCount) {
                throw new AppException(ResponseCode.UPDATE_ZERO);
            }

            // 查询拼团交易完成外部单号列表
            List<String> outTradeNoList = groupBuyOrderListDao.queryGroupBuyCompleteOrderOutTradeNoListByTeamId(groupBuyTeamEntity.getTeamId());

            // 拼团完成写入回调任务记录
            NotifyTask notifyTask = NotifyTask.builder()
                    .activityId(groupBuyTeamEntity.getActivityId())
                    .teamId(groupBuyTeamEntity.getTeamId())
                    .notifyType(notifyConfigVO.getNotifyType().getCode())
                    .notifyMQ(notifyConfigVO.getNotifyMQ())
                    .notifyUrl(notifyConfigVO.getNotifyUrl())
                    .notifyCount(0)
                    .notifyStatus(0)
                    .parameterJson(JSON.toJSONString(new HashMap<String, Object>() {{
                        put("teamId", groupBuyTeamEntity.getTeamId());
                        put("outTradeNoList", outTradeNoList);
                    }}))
                    .build();
            notifyTaskDao.insert(notifyTask);
            return NotifyTaskEntity.builder()
                    .teamId(groupBuyTeamEntity.getTeamId())
                    .notifyType(notifyConfigVO.getNotifyType().getCode())
                    .notifyMQ(notifyConfigVO.getNotifyMQ())
                    .notifyUrl(notifyConfigVO.getNotifyUrl())
                    .notifyCount(notifyTask.getNotifyCount())
                    .notifyStatus(notifyTask.getNotifyStatus())
                    .parameterJson(notifyTask.getParameterJson())
                    .build();
        }
        return null;
    }

    @Override
    public List<NotifyTaskEntity> queryGroupBuySuccessNotifyList() {
        List<NotifyTask> notifyTaskList = notifyTaskDao.queryGroupBuySuccessNotifyList();
        if (CollectionUtils.isEmpty(notifyTaskList)) return new ArrayList<>();
        return notifyTaskList.stream().map(notifyTask -> NotifyTaskEntity.builder()
                .teamId(notifyTask.getTeamId())
                .notifyType(notifyTask.getNotifyType())
                .notifyMQ(notifyTask.getNotifyMQ())
                .notifyUrl(notifyTask.getNotifyUrl())
                .notifyCount(notifyTask.getNotifyCount())
                .parameterJson(notifyTask.getParameterJson())
                .build()).collect(Collectors.toList());
    }

    @Override
    public Boolean isSCBlackIntercept(String source, String channel) {
        return dccService.isSCBlackIntercept(source, channel);
    }

    @Override
    public int updateNotifyTaskStatusSuccess(String teamId) {
        return notifyTaskDao.updateNotifyTaskStatusSuccess(teamId);
    }

    @Override
    public int updateNotifyTaskStatusRetry(String teamId) {
        return notifyTaskDao.updateNotifyTaskStatusRetry(teamId);
    }

    @Override
    public int updateNotifyTaskStatusError(String teamId) {
        return notifyTaskDao.updateNotifyTaskStatusError(teamId);
    }

    @Override
    public List<NotifyTaskEntity> queryUnExecutedNotifyTaskList(String teamId) {
        List<NotifyTask> notifyTaskList = notifyTaskDao.queryUnExecutedNotifyTaskList(teamId);
        if (CollectionUtils.isEmpty(notifyTaskList)) return new ArrayList<>();
        return notifyTaskList.stream().map(notifyTask -> NotifyTaskEntity.builder()
                .teamId(notifyTask.getTeamId())
                .notifyType(notifyTask.getNotifyType())
                .notifyMQ(notifyTask.getNotifyMQ())
                .notifyUrl(notifyTask.getNotifyUrl())
                .notifyCount(notifyTask.getNotifyCount())
                .parameterJson(notifyTask.getParameterJson())
                .build()).collect(Collectors.toList());
    }

    @Override
    public boolean occupyTeamStock(String teamStockKey, String recoveryTeamStockKey, Integer target, Integer validTime) {
        Long recoveryCount = redisService.getAtomicLong(recoveryTeamStockKey);
        recoveryCount = null == recoveryCount ? 0L : recoveryCount;
        // 1. incr 得到值，与总量和恢复量做对比。恢复量为系统失败时候记录的量。
        // 2. 从有组队量开始，相当于已经有了一个占用量，所以要 +1
        long occupy = redisService.incr(teamStockKey) + 1;

        if (occupy > target + recoveryCount) {
            return false;
        }
        // 1. 给每个产生的值加锁为兜底设计，虽然incr操作是原子的，基本不会产生一样的值。但在实际生产中，遇到过集群的运维配置问题，以及业务运营配置数据问题，导致incr得到的值相同。
        // 2. validTime + 60分钟，是一个延后时间的设计，让数据保留时间稍微长一些，便于排查问题。
        String lockKey = teamStockKey + Constants.UNDERLINE + occupy;
        Boolean lock = redisService.setNx(lockKey, validTime + 60, TimeUnit.MINUTES);
        if (!lock) {
            log.info("组队库存加锁失败 {}", lockKey);
        }
        return lock;
    }

    @Override
    public void recoveryTeamStock(String recoveryTeamStockKey, Integer validTime) {
        // 首次组队拼团，是没有 teamId 的，所以不需要这个做处理。
        if (StringUtils.isBlank(recoveryTeamStockKey)) return;

        redisService.incr(recoveryTeamStockKey);
    }
}
