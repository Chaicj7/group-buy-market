package com.chaicj.api;

import com.chaicj.api.dto.SkuRequestDTO;
import com.chaicj.api.dto.SkuResponseDTO;
import com.chaicj.api.response.Response;

public interface ISkuService {

    Response<SkuResponseDTO> querySkuByGoodsId(SkuRequestDTO requestDTO);

}
