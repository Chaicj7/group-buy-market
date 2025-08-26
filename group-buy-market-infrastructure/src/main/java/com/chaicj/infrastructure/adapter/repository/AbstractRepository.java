package com.chaicj.infrastructure.adapter.repository;

import com.chaicj.infrastructure.dcc.DCCService;
import com.chaicj.infrastructure.redis.IRedisService;
import org.redisson.api.RLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.function.Supplier;

public abstract class AbstractRepository {

    private final Logger logger = LoggerFactory.getLogger(AbstractRepository.class);

    @Resource
    private IRedisService redisService;
    @Resource
    private DCCService dccService;

    public <T> T getFromRedisOrDB(String cacheKey, Supplier<T> dbSupplier) {
        return getFromRedisOrDB(cacheKey, 0, dbSupplier);
    }

    public <T> T getFromRedisOrDB(String cacheKey, long time, Supplier<T> dbSupplier) {
        // 检查supplier参数是否为null，如果为null则抛出NullPointerException
        Objects.requireNonNull(dbSupplier);
        // 检查缓存开关是否开启
        if (dccService.isCacheOpenSwitch()) {
            T cacheResult = redisService.getValue(cacheKey);
            if (cacheResult != null) {
                return cacheResult;
            }
            RLock lock = redisService.getLock(cacheKey + ":lock");
            try {
                // 获取锁
                lock.lock();
                cacheResult = redisService.getValue(cacheKey);
                if (cacheResult != null) {
                    return cacheResult;
                }
                cacheResult = dbSupplier.get();
                if (cacheResult != null) {
                    if (time > 0) {
                        redisService.setValue(cacheKey, cacheResult, time);
                    } else {
                        redisService.setValue(cacheKey, cacheResult);
                    }
                }
                return cacheResult;
            } finally {
                // 释放锁
                if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        } else {
            // 缓存未开启，直接从数据库获取
            logger.warn("缓存降级 {}", cacheKey);
            return dbSupplier.get();
        }
    }
}
