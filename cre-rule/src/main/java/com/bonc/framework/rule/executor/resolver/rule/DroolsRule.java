package com.bonc.framework.rule.executor.resolver.rule;

import com.bonc.framework.rule.executor.context.IQLExpressContext;

import java.util.Map;

public class DroolsRule extends AbstractRule {
    private static DroolsRule instance;

    public static DroolsRule getInstance() {
        if (instance == null) {
            instance = new DroolsRule();
        }
        return instance;
    }

    @Override
    public Object execute(String express, IQLExpressContext context) throws Exception {
        return null;
    }

    @Override
    public IQLExpressContext<?, ?> createContext(Map<String, Object> map) {
        return null;
    }

}
