package com.bonc.framework.rule.executor.resolver.rule;

import com.bonc.framework.rule.executor.context.IQLExpressContext;

import java.util.Map;

public interface IRule {

    public Object execute(String express, IQLExpressContext<?, ?> context) throws Exception;


    public IQLExpressContext<?, ?> createContext(Map<String, Object> map);

}
