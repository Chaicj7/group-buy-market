package com.chaicj.types.design.framework.tree;

/**
 * 受理策略处理
 * @param <T> 入参
 * @param <D> 上下文
 * @param <R> 返回结果
 */
public interface StrategyHandler<T, D, R> {

    StrategyHandler DEFAULT = (T, D) -> null;

    R apply(T requestParameter, D dynamicContext) throws Exception;

}
