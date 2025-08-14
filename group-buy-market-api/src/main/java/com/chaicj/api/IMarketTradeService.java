package com.chaicj.api;

import com.chaicj.api.dto.LockMarketPayOrderRequestDTO;
import com.chaicj.api.dto.LockMarketPayOrderResponseDTO;
import com.chaicj.api.dto.SettlementMarketPayOrderRequestDTO;
import com.chaicj.api.dto.SettlementMarketPayOrderResponseDTO;
import com.chaicj.api.response.Response;

public interface IMarketTradeService {

    /**
     * 营销锁单
     *
     * @param requestDTO 锁单商品信息
     * @return 锁单结果信息
     */
    Response<LockMarketPayOrderResponseDTO> lockMarketPayOrder(LockMarketPayOrderRequestDTO requestDTO) throws Exception;

    /**
     * 营销结算
     *
     * @param requestDTO 结算商品信息
     * @return 结算结果信息
     */
    Response<SettlementMarketPayOrderResponseDTO> settlementMarketPayOrder(SettlementMarketPayOrderRequestDTO requestDTO);

}
