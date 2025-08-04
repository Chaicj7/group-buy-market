package com.chaicj.domain.activity.service;

import com.chaicj.domain.activity.model.entity.MarketProductEntity;
import com.chaicj.domain.activity.model.entity.TrialBalanceEntity;

public interface IIndexGroupBuyMarketService {

    TrialBalanceEntity indexMarketTrial(MarketProductEntity marketProductEntity) throws Exception;
}
