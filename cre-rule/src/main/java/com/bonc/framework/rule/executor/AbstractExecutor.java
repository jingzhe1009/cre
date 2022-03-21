package com.bonc.framework.rule.executor;

import com.bonc.framework.rule.executor.builder.AbstractExecutorBuilder;

/**
 * @author qxl
 * @version 1.0
 * @date 2018年1月8日 下午3:47:40
 */
public abstract class AbstractExecutor implements IExecutor {

    protected AbstractExecutorBuilder builder;

    public AbstractExecutor(AbstractExecutorBuilder builder) {
        if (builder == null) {
            throw new NullPointerException("The builder is null.");
        }
        this.builder = builder;
    }

    /**
     * 获取builder
     *
     * @return
     */
    public AbstractExecutorBuilder getBuilder() {
        return builder;
    }

}
