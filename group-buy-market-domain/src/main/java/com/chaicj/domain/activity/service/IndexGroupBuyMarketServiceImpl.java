package com.chaicj.domain.activity.service;

import com.chaicj.domain.activity.adapter.repository.IActivityRepository;
import com.chaicj.domain.activity.model.entity.MarketDynamicContext;
import com.chaicj.domain.activity.model.entity.MarketProductEntity;
import com.chaicj.domain.activity.model.entity.TrialBalanceEntity;
import com.chaicj.domain.activity.model.entity.UserGroupBuyOrderDetailEntity;
import com.chaicj.domain.activity.model.valobj.TeamStatisticVO;
import com.chaicj.domain.activity.service.trial.factory.DefaultActivityStrategyFactory;
import com.chaicj.types.design.framework.tree.StrategyHandler;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class IndexGroupBuyMarketServiceImpl implements  IIndexGroupBuyMarketService {

    @Resource
    private DefaultActivityStrategyFactory defaultActivityStrategyFactory;
    @Resource
    private IActivityRepository activityRepository;

    @Override
    public TrialBalanceEntity indexMarketTrial(MarketProductEntity marketProductEntity) throws Exception {

        StrategyHandler<MarketProductEntity, MarketDynamicContext, TrialBalanceEntity> strategyHandler = defaultActivityStrategyFactory.strategyHandler();

        TrialBalanceEntity trialBalanceEntity = strategyHandler.apply(marketProductEntity, new MarketDynamicContext());

        return trialBalanceEntity;
    }

    @Override
    public TeamStatisticVO queryTeamStatisticByActivityId(Long activityId) {
        return activityRepository.queryTeamStatisticByActivityId(activityId);
    }

    @Override
    public List<UserGroupBuyOrderDetailEntity> queryInProgressUserGroupBuyOrderDetailList(Long activityId, String userId, int ownerCount, int randomCount) {
        List<UserGroupBuyOrderDetailEntity> unionAllList = new ArrayList<>();
        // 查询个人拼团数据
        if (ownerCount != 0) {
            List<UserGroupBuyOrderDetailEntity> ownerList = activityRepository.queryInProgressUserGroupBuyOrderDetailListByOwner(activityId, userId, ownerCount);
            if (!CollectionUtils.isEmpty(ownerList)) {
                unionAllList.addAll(ownerList);
            }
        }
        // 查询其他非个人拼团
        if (randomCount != 0) {
            List<UserGroupBuyOrderDetailEntity> randomList = activityRepository.queryInProgressUserGroupBuyOrderDetailListByRandom(activityId, userId, randomCount);
            if (!CollectionUtils.isEmpty(randomList)) {
                unionAllList.addAll(randomList);
            }
        }
        return unionAllList;
    }
}
