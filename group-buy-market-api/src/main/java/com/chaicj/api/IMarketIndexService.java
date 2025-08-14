package com.chaicj.api;

import com.chaicj.api.dto.GoodsMarketRequestDTO;
import com.chaicj.api.dto.GoodsMarketResponseDTO;
import com.chaicj.api.response.Response;

/**
 * 营销首页服务接口
 */
public interface IMarketIndexService {


    Response<GoodsMarketResponseDTO> queryGoodsBuyMarketConfig(GoodsMarketRequestDTO requestDTO);

}
