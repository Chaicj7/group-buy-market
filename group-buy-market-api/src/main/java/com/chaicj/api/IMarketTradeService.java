package com.chaicj.api;

import com.chaicj.api.dto.LockMarketPayOrderRequestDTO;
import com.chaicj.api.dto.LockMarketPayOrderResponseDTO;
import com.chaicj.api.response.Response;

public interface IMarketTradeService {

    Response<LockMarketPayOrderResponseDTO> lockMarketPayOrder(LockMarketPayOrderRequestDTO requestDTO) throws Exception;

}
