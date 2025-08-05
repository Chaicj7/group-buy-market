package com.chaicj.domain.activity.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SCSkuActivityVO {

    private String source;

    private String channel;

    private Long activityId;

    private String goodsId;
}
