package com.chaicj.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class LockMarketPayOrderRequestDTO {

    // 用户ID
    private String userId;
    // 拼单组队ID - 可为空，为空则创建新组队ID
    private String teamId;
    // 活动ID
    private Long activityId;
    // 商品ID
    private String goodsId;
    // 渠道
    private String source;
    // 来源
    private String channel;
    // 外部交易单号
    private String outTradeNo;
    private String notifyUrl;
    private NotifyConfigVO notifyConfig;

    public void setNotifyMQ() {
        NotifyConfigVO notifyConfigVO = new NotifyConfigVO();
        notifyConfigVO.setNotifyType("MQ");
        this.notifyConfig = notifyConfigVO;
    }

    public void setNotifyUrl(String notifyUrl) {
        NotifyConfigVO notifyConfigVO = new NotifyConfigVO();
        notifyConfigVO.setNotifyType("HTTP");
        notifyConfigVO.setNotifyUrl(notifyUrl);
        this.notifyConfig = notifyConfigVO;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NotifyConfigVO {
        // MQ HTTP
        private String notifyType;

        private String notifyMQ;

        private String notifyUrl;
    }

}
