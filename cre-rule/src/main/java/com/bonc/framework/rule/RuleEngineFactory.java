package com.bonc.framework.rule;

/**
 * 核心引擎工厂
 *
 * @author qxl
 * @version 1.0
 * @date 2018年3月6日 下午6:49:09
 */
public class RuleEngineFactory {

    private volatile static RuleEngineFacade ruleEngineFacade;

    /**
     * 获取引擎
     *
     * @return
     */
    public static RuleEngineFacade getRuleEngine() {
        if (ruleEngineFacade == null) {
            synchronized (RuleEngineFacade.class) {
                if (ruleEngineFacade == null) {//双重检测机制
                    ruleEngineFacade = new RuleEngineFacade();
                }
            }
        }
        return ruleEngineFacade;
    }

}
