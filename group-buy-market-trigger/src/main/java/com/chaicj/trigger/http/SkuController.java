package com.chaicj.trigger.http;

import com.chaicj.api.ISkuService;
import com.chaicj.api.dto.SkuRequestDTO;
import com.chaicj.api.dto.SkuResponseDTO;
import com.chaicj.api.response.Response;
import com.chaicj.domain.activity.model.valobj.SkuVO;
import com.chaicj.domain.activity.service.ISkuActivityService;
import com.chaicj.types.enums.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/sku")
public class SkuController implements ISkuService {

    @Resource
    private ISkuActivityService skuActivityService;

    @PostMapping("/query_sku_by_goodsId")
    @Override
    public Response<SkuResponseDTO> querySkuByGoodsId(@RequestBody SkuRequestDTO requestDTO) {
        SkuVO skuVO = skuActivityService.querySkuByGoodsId(requestDTO.getGoodsId());
        if (skuVO != null) {
            return Response.<SkuResponseDTO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(SkuResponseDTO.builder()
                            .goodsId(skuVO.getGoodsId())
                            .goodsName(skuVO.getGoodsName())
                            .price(skuVO.getOriginalPrice())
                            .build())
                    .build();
        }
        return null;
    }
}
