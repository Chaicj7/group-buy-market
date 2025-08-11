package com.chaicj.trigger.http;

import com.alibaba.fastjson.JSON;
import com.chaicj.api.IMarketTradeService;
import com.chaicj.api.dto.LockMarketPayOrderRequestDTO;
import com.chaicj.api.dto.LockMarketPayOrderResponseDTO;
import com.chaicj.api.response.Response;
import com.chaicj.domain.activity.model.entity.MarketProductEntity;
import com.chaicj.domain.activity.model.entity.TrialBalanceEntity;
import com.chaicj.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import com.chaicj.domain.activity.model.valobj.GroupBuyDiscountVO;
import com.chaicj.domain.activity.service.IIndexGroupBuyMarketService;
import com.chaicj.domain.trade.model.entity.MarketPayOrderEntity;
import com.chaicj.domain.trade.model.entity.PayActivityEntity;
import com.chaicj.domain.trade.model.entity.PayDiscountEntity;
import com.chaicj.domain.trade.model.entity.UserEntity;
import com.chaicj.domain.trade.model.valobj.GroupBuyProgressVO;
import com.chaicj.domain.trade.service.ITradeOrderService;
import com.chaicj.types.enums.ResponseCode;
import com.chaicj.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Objects;

@Slf4j
@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/trade")
public class MarketTradeController implements IMarketTradeService {

    @Resource
    private IIndexGroupBuyMarketService indexGroupBuyMarketService;
    @Resource
    private ITradeOrderService tradeOrderService;

    @PostMapping("/lockMarketPayOrder")
    @Override
    public Response<LockMarketPayOrderResponseDTO> lockMarketPayOrder(@RequestBody LockMarketPayOrderRequestDTO requestDTO) throws Exception {
        try {
            String userId = requestDTO.getUserId();
            String teamId = requestDTO.getTeamId();
            Long activityId = requestDTO.getActivityId();
            String goodsId = requestDTO.getGoodsId();
            String source = requestDTO.getSource();
            String channel = requestDTO.getChannel();
            String outTradeNo = requestDTO.getOutTradeNo();

            log.info("营销交易锁单:{} LockMarketPayOrderRequestDTO:{}", userId, JSON.toJSONString(requestDTO));
            if (StringUtils.isBlank(userId) || StringUtils.isBlank(source) || StringUtils.isBlank(channel) || StringUtils.isBlank(goodsId) || StringUtils.isBlank(goodsId) || null == activityId) {
                return Response.<LockMarketPayOrderResponseDTO>builder()
                        .code(ResponseCode.ILLEGAL_PARAMETER.getCode())
                        .info(ResponseCode.ILLEGAL_PARAMETER.getInfo())
                        .build();
            }
            // 查询 outTradeNo 是否已经存在交易记录
            MarketPayOrderEntity marketPayOrderEntity = tradeOrderService.queryNoPayMarketPayOrderByOutTradeNo(userId, outTradeNo);
            if (marketPayOrderEntity != null) {
                LockMarketPayOrderResponseDTO lockMarketPayOrderResponseDTO = LockMarketPayOrderResponseDTO.builder()
                        .orderId(marketPayOrderEntity.getOrderId())
                        .deductionPrice(marketPayOrderEntity.getDeductionPrice())
                        .tradeOrderStatus(marketPayOrderEntity.getTradeOrderStatus().getCode())
                        .build();
                log.info("交易锁单记录(存在):{} marketPayOrderEntity:{}", userId, JSON.toJSONString(marketPayOrderEntity));
                return Response.<LockMarketPayOrderResponseDTO>builder()
                        .code(ResponseCode.SUCCESS.getCode())
                        .info(ResponseCode.SUCCESS.getInfo())
                        .data(lockMarketPayOrderResponseDTO)
                        .build();
            }
            // 判断拼团锁单是否完成了目标
            if (StringUtils.isNotBlank(teamId)) {
                GroupBuyProgressVO groupBuyDiscountVO = tradeOrderService.queryGroupBuyProgress(teamId);
                if (groupBuyDiscountVO != null && Objects.equals(groupBuyDiscountVO.getLockCount(), groupBuyDiscountVO.getTargetCount())) {
                    log.info("交易锁单拦截-拼单目标已达成:{} {}", userId, teamId);
                    return Response.<LockMarketPayOrderResponseDTO>builder()
                            .code(ResponseCode.E0005.getCode())
                            .info(ResponseCode.E0005.getInfo())
                            .build();
                }
            }
            // 优惠试算
            TrialBalanceEntity trialBalanceEntity = indexGroupBuyMarketService.indexMarketTrial(MarketProductEntity.builder()
                    .userId(userId)
                    .goodsId(goodsId)
                    .channel(channel)
                    .source(source)
                    .build());
            // 人群限定
            if (!trialBalanceEntity.getIsVisible() || !trialBalanceEntity.getIsEnable()) {
                return Response.<LockMarketPayOrderResponseDTO>builder()
                        .code(ResponseCode.E0007.getCode())
                        .info(ResponseCode.E0007.getInfo())
                        .build();
            }

            GroupBuyActivityDiscountVO groupBuyActivityDiscountVO = trialBalanceEntity.getGroupBuyActivityDiscountVO();

            // 营销优惠锁单
            marketPayOrderEntity = tradeOrderService.lockMarketPayOrder(
                    UserEntity.builder()
                            .userId(userId)
                            .build(),
                    PayActivityEntity.builder()
                            .teamId(teamId)
                            .activityId(activityId)
                            .activityName(groupBuyActivityDiscountVO.getActivityName())
                            .startTime(groupBuyActivityDiscountVO.getStartTime())
                            .endTime(groupBuyActivityDiscountVO.getEndTime())
                            .targetCount(groupBuyActivityDiscountVO.getTarget())
                            .build(),
                    PayDiscountEntity.builder()
                            .source(source)
                            .channel(channel)
                            .goodsId(goodsId)
                            .goodsName(trialBalanceEntity.getGoodsName())
                            .originalPrice(trialBalanceEntity.getOriginalPrice())
                            .deductionPrice(trialBalanceEntity.getDeductionPrice())
                            .payPrice(trialBalanceEntity.getOriginalPrice())
                            .outTradeNo(outTradeNo)
                            .build());
            log.info("交易锁单记录(新):{} marketPayOrderEntity:{}", userId, JSON.toJSONString(marketPayOrderEntity));
            return Response.<LockMarketPayOrderResponseDTO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(LockMarketPayOrderResponseDTO.builder()
                            .orderId(marketPayOrderEntity.getOrderId())
                            .deductionPrice(marketPayOrderEntity.getDeductionPrice())
                            .tradeOrderStatus(marketPayOrderEntity.getTradeOrderStatus().getCode())
                            .build())
                    .build();
        } catch (AppException e) {
            log.error("营销交易锁单业务异常:{} LockMarketPayOrderRequestDTO:{}", requestDTO.getUserId(), JSON.toJSONString(requestDTO), e);
            return Response.<LockMarketPayOrderResponseDTO>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("营销交易锁单服务失败:{} LockMarketPayOrderRequestDTO:{}", requestDTO.getUserId(), JSON.toJSONString(requestDTO), e);
            return Response.<LockMarketPayOrderResponseDTO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }
}
