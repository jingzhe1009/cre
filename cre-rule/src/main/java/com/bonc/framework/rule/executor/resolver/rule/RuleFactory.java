package com.bonc.framework.rule.executor.resolver.rule;

public class RuleFactory {
    private static IRule rule;

    public synchronized static IRule getRule() {
        if (rule == null) {
            rule = new QLExpressRule();
        }
        return rule;
    }

}
