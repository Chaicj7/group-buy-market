package com.chaicj.types.design.framework.tree;

/**
 * 策略映射器
 * @param <T> 入参
 * @param <D> 上下文
 * @param <R> 返回结果
 */
public interface StrategyMapper<T, D, R> {

    /**
     * 获取待执行策略
     * @param requestParameter
     * @param dynamicContext
     * @return
     * @throws Exception
     */
    StrategyHandler<T, D, R> get(T requestParameter, D dynamicContext) throws Exception;

}
