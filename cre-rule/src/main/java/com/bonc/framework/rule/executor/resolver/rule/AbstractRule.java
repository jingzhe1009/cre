package com.bonc.framework.rule.executor.resolver.rule;

import com.bonc.framework.rule.executor.context.IQLExpressContext;

public abstract class AbstractRule implements IRule {

    public abstract Object execute(String express, IQLExpressContext<?, ?> context) throws Exception;

}
