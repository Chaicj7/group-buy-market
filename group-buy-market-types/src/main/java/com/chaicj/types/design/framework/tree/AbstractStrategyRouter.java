package com.chaicj.types.design.framework.tree;

/**
 * 策略路由抽象类
 * @param <T> 入参
 * @param <D> 上下文
 * @param <R> 返回结果
 */
public abstract class AbstractStrategyRouter<T, D, R> implements StrategyHandler<T, D, R>, StrategyMapper<T, D, R> {

    protected StrategyHandler<T, D, R> defaultStrategyHandler = StrategyHandler.DEFAULT;

    public R routor(T requestParameter, D dynamicContext) throws Exception {
        StrategyHandler<T, D, R> strategyHandler = get(requestParameter, dynamicContext);
        if (strategyHandler == null) return defaultStrategyHandler.apply(requestParameter, dynamicContext);
        return strategyHandler.apply(requestParameter, dynamicContext);
    }

}
