package com.chaicj.domain.trade.model.entity;

import com.chaicj.domain.trade.model.valobj.TradeOrderStatusEnumVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketPayOrderEntity {

    /** 拼单组队ID */
    private String teamId;
    /** 预购订单ID */
    private String orderId;
    /** 原始金额 */
    private BigDecimal originalPrice;
    /** 折扣金额 */
    private BigDecimal deductionPrice;
    /** 实际支付金额 */
    private BigDecimal payPrice;
    /** 交易订单状态 */
    private TradeOrderStatusEnumVO tradeOrderStatus;
}
