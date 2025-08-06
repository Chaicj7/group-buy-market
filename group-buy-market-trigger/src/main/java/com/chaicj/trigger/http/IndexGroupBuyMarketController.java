package com.chaicj.trigger.http;

import com.chaicj.api.response.Response;
import com.chaicj.domain.activity.model.entity.MarketProductEntity;
import com.chaicj.domain.activity.model.entity.TrialBalanceEntity;
import com.chaicj.domain.activity.service.IIndexGroupBuyMarketService;
import com.chaicj.types.enums.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/index/")
public class IndexGroupBuyMarketController {

    @Resource
    private IIndexGroupBuyMarketService indexGroupBuyMarketService;


    @PostMapping("/indexMarketTrial")
    public Response<TrialBalanceEntity> indexMarketTrial(@RequestBody MarketProductEntity marketProductEntity) {
        try {
            log.info("indexMarketTrial: {}", marketProductEntity);
            TrialBalanceEntity trialBalanceEntity = indexGroupBuyMarketService.indexMarketTrial(marketProductEntity);
            return Response.<TrialBalanceEntity>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(trialBalanceEntity)
                    .build();
        } catch (Exception e) {
            log.info("indexMarketTrial error: {}", e);
            return Response.<TrialBalanceEntity>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(e.getMessage())
                    .build();
        }
    }
}
