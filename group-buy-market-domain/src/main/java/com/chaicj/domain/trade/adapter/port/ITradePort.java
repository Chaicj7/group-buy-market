package com.chaicj.domain.trade.adapter.port;

import com.chaicj.domain.trade.model.entity.NotifyTaskEntity;

public interface ITradePort {

    String GroupBuyNotify(NotifyTaskEntity notifyTaskEntity) throws Exception;

}
