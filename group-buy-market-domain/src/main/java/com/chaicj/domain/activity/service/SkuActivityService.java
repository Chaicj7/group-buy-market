package com.chaicj.domain.activity.service;

import com.chaicj.domain.activity.adapter.repository.IActivityRepository;
import com.chaicj.domain.activity.model.valobj.SkuVO;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class SkuActivityService implements ISkuActivityService {

    @Resource
    private IActivityRepository repository;

    @Override
    public SkuVO querySkuByGoodsId(String goodsId) {
        return repository.querySkuByGoodsId(goodsId);
    }
}
