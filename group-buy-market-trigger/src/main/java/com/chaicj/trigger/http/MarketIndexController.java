package com.chaicj.trigger.http;

import com.alibaba.fastjson.JSON;
import com.chaicj.api.IMarketIndexService;
import com.chaicj.api.dto.GoodsMarketRequestDTO;
import com.chaicj.api.dto.GoodsMarketResponseDTO;
import com.chaicj.api.response.Response;
import com.chaicj.domain.activity.model.entity.MarketProductEntity;
import com.chaicj.domain.activity.model.entity.TrialBalanceEntity;
import com.chaicj.domain.activity.model.entity.UserGroupBuyOrderDetailEntity;
import com.chaicj.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import com.chaicj.domain.activity.model.valobj.TeamStatisticVO;
import com.chaicj.domain.activity.service.IIndexGroupBuyMarketService;
import com.chaicj.types.enums.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/index/")
public class MarketIndexController implements IMarketIndexService {

    @Resource
    private IIndexGroupBuyMarketService indexGroupBuyMarketService;

    @PostMapping("/query_group_buy_market_config")
    @Override
    public Response<GoodsMarketResponseDTO> queryGoodsBuyMarketConfig(@RequestBody GoodsMarketRequestDTO requestDTO) {
        try {
            log.info("查询拼团营销配置开始:{} goodsId:{}", requestDTO.getUserId(), requestDTO.getGoodsId());
            if (StringUtils.isBlank(requestDTO.getUserId()) || StringUtils.isBlank(requestDTO.getSource()) || StringUtils.isBlank(requestDTO.getChannel()) || StringUtils.isBlank(requestDTO.getGoodsId())) {
                return Response.<GoodsMarketResponseDTO>builder()
                        .code(ResponseCode.ILLEGAL_PARAMETER.getCode())
                        .info(ResponseCode.ILLEGAL_PARAMETER.getInfo())
                        .build();
            }
            // 1. 营销优惠试算
            TrialBalanceEntity trialBalanceEntity = indexGroupBuyMarketService.indexMarketTrial(MarketProductEntity.builder()
                    .userId(requestDTO.getUserId())
                    .channel(requestDTO.getChannel())
                    .source(requestDTO.getSource())
                    .goodsId(requestDTO.getGoodsId())
                    .build());
            GroupBuyActivityDiscountVO groupBuyActivityDiscountVO = trialBalanceEntity.getGroupBuyActivityDiscountVO();
            Long activityId = groupBuyActivityDiscountVO.getActivityId();

            // 2. 查询拼团组队
            List<UserGroupBuyOrderDetailEntity> userGroupBuyOrderDetailEntities = indexGroupBuyMarketService.queryInProgressUserGroupBuyOrderDetailList(activityId, requestDTO.getUserId(), 1, 2);

            // 3. 统计拼团数据
            TeamStatisticVO teamStatistic = indexGroupBuyMarketService.queryTeamStatisticByActivityId(activityId);

            GoodsMarketResponseDTO.Goods goods = new GoodsMarketResponseDTO.Goods().builder()
                    .goodsId(trialBalanceEntity.getGoodsId())
                    .originalPrice(trialBalanceEntity.getOriginalPrice())
                    .deductionPrice(trialBalanceEntity.getDeductionPrice())
                    .payPrice(trialBalanceEntity.getPayPrice())
                    .build();
            List<GoodsMarketResponseDTO.Team> teams = new ArrayList<>();
            if (!CollectionUtils.isEmpty(userGroupBuyOrderDetailEntities)) {
                for (UserGroupBuyOrderDetailEntity userGroupBuyOrderDetailEntity : userGroupBuyOrderDetailEntities) {
                    GoodsMarketResponseDTO.Team team = new GoodsMarketResponseDTO.Team().builder()
                            .userId(userGroupBuyOrderDetailEntity.getUserId())
                            .teamId(userGroupBuyOrderDetailEntity.getTeamId())
                            .activityId(userGroupBuyOrderDetailEntity.getActivityId())
                            .targetCount(userGroupBuyOrderDetailEntity.getTargetCount())
                            .completeCount(userGroupBuyOrderDetailEntity.getCompleteCount())
                            .lockCount(userGroupBuyOrderDetailEntity.getLockCount())
                            .validStartTime(userGroupBuyOrderDetailEntity.getValidStartTime())
                            .validEndTime(userGroupBuyOrderDetailEntity.getValidEndTime())
                            .validTimeCountdown(GoodsMarketResponseDTO.Team.differenceDateTime2Str(new Date(), userGroupBuyOrderDetailEntity.getValidEndTime()))
                            .outTradeNo(userGroupBuyOrderDetailEntity.getOutTradeNo())
                            .build();
                    teams.add(team);
                }
            }
            GoodsMarketResponseDTO.TeamStatistic statistic = GoodsMarketResponseDTO.TeamStatistic.builder()
                    .allTeamCount(teamStatistic.getAllTeamCount())
                    .allTeamCompleteCount(teamStatistic.getAllTeamCompleteCount())
                    .allTeamUserCount(teamStatistic.getAllTeamUserCount())
                    .build();
            Response<GoodsMarketResponseDTO> response = Response.<GoodsMarketResponseDTO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(GoodsMarketResponseDTO.builder()
                            .goods(goods)
                            .teamList(teams)
                            .teamStatistic(statistic)
                            .build())
                    .build();
            log.info("查询拼团营销配置完成:{} goodsId:{} response:{}", requestDTO.getUserId(), requestDTO.getGoodsId(), JSON.toJSONString(response));
            return response;
        } catch (Exception e) {
            log.error("查询拼团营销配置失败:{} goodsId:{}", requestDTO.getUserId(), requestDTO.getGoodsId(), e);
            return Response.<GoodsMarketResponseDTO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }
}
