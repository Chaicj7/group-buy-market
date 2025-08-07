package com.chaicj.types.design.framework.link.model1;

public interface ILogicLink<T, D, R> {

    R apply(T request, D dynamicContext) throws Exception;

    ILogicLink<T, D, R> next();

    ILogicLink<T, D, R> appendNext(ILogicLink<T, D, R> next);
}
