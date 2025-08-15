package com.chaicj.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SkuResponseDTO {

    private String goodsId;
    private String goodsName;
    private BigDecimal price;

}
