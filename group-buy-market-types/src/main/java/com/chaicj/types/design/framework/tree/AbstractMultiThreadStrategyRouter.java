package com.chaicj.types.design.framework.tree;

public abstract class AbstractMultiThreadStrategyRouter<T, D, R> implements StrategyHandler<T, D, R>, StrategyMapper<T, D, R> {

    protected StrategyHandler<T, D, R> defaultStrategyHandler = StrategyHandler.DEFAULT;

    public R routor(T requestParameter, D dynamicContext) throws Exception {
        StrategyHandler<T, D, R> strategyMapper = get(requestParameter, dynamicContext);
        if (null != strategyMapper) return strategyMapper.apply(requestParameter, dynamicContext);
        return defaultStrategyHandler.apply(requestParameter, dynamicContext);
    }

    @Override
    public R apply(T requestParameter, D dynamicContext) throws Exception {
        // 异步加载数据
        multiThread(requestParameter, dynamicContext);
        // 业务流程
        return doApply(requestParameter, dynamicContext);
    }

    // 异步加载数据
    protected abstract void multiThread(T requestParameter, D dynamicContext) throws Exception;

    // 业务流程
    protected abstract R doApply(T requestParameter, D dynamicContext) throws Exception;

}
