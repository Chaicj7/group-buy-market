package com.chaicj.domain.activity.service;

import com.chaicj.domain.activity.model.valobj.SkuVO;

public interface ISkuActivityService {

    SkuVO querySkuByGoodsId(String goodsId);

}
