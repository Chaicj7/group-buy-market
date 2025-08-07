package com.chaicj.types.design.framework.link.model1;

public abstract class AbstractLogicLink<T, D, R> implements ILogicLink<T, D, R> {

    ILogicLink<T, D, R> next;

    public ILogicLink<T, D, R> next() {
        return next;
    }

    @Override
    public ILogicLink<T, D, R> appendNext(ILogicLink<T, D, R> next) {
        this.next = next;
        return next;
    }

    public R nextApply(T request, D dynamicContext, R result) throws Exception {
        if (null != next) {
            return next.apply(request, dynamicContext);
        }
        return result;
    }

}
