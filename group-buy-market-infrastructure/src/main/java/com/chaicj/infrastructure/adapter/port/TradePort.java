package com.chaicj.infrastructure.adapter.port;

import com.chaicj.domain.trade.adapter.port.ITradePort;
import com.chaicj.domain.trade.model.entity.NotifyTaskEntity;
import com.chaicj.infrastructure.gateway.GroupBuyNotifyService;
import com.chaicj.infrastructure.redis.IRedisService;
import com.chaicj.types.enums.NotifyTaskHTTPEnumVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class TradePort implements ITradePort {

    @Resource
    private IRedisService redisService;
    @Resource
    private GroupBuyNotifyService groupBuyNotifyService;

    @Override
    public String GroupBuyNotify(NotifyTaskEntity notifyTaskEntity) throws Exception {
        RLock lock = redisService.getLock(notifyTaskEntity.lockKey());
        try {
            // group-buy-market 拼团服务端会被部署到多台应用服务器上，那么就会有很多任务一起执行。这个时候要进行抢占，避免被多次执行
            if (lock.tryLock(3, 0 , TimeUnit.SECONDS)) {
                try {
                    // 无效的 notifyUrl 则直接返回成功
                    if (StringUtils.isEmpty(notifyTaskEntity.getNotifyUrl())) {
                        return NotifyTaskHTTPEnumVO.SUCCESS.getCode();
                    }
                    return groupBuyNotifyService.groupBuyNotify(notifyTaskEntity.getNotifyUrl(), notifyTaskEntity.getParameterJson());
                } finally {
                    if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                        lock.unlock();
                    }
                }
            }
            return NotifyTaskHTTPEnumVO.NULL.getCode();
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            return NotifyTaskHTTPEnumVO.NULL.getCode();
        }
    }
}
