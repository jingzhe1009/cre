package com.bonc.framework.rule.constant;

/**
 * 规则核心引擎常用常量类
 *
 * @author qxl
 * @version 1.0
 * @date 2018年3月2日 下午4:31:34
 */
public class RuleEngineConstant {

    /**
     * 引擎配置文件路径
     */
    public static final String PROPERTIESPATH = "ruleEngine.properties";

    /**
     * 引擎配置文件中 规则解析器类路径的前缀
     */
    public static final String PREFIX_RULEPARSECLASS = "ruleParseClass.ruleType";

    /**
     * 引擎配置文件中 规则执行器类路径的前缀
     */
    public static final String PREFIX_RULEEXECUTORCLASS = "ruleExecutorClass.ruleType";

    /**
     * 遍历器
     */
    public static final String PREFIX_RULETRAVERSALCLASS = "ruleTraversalClass.ruleType";

    public static final String PREFIX_THREADPOOL_THREADNUM = "executor.threadpool.threadNum";

    /**
     * 执行模型时,加载指标的策略类型
     */
    public static final String EXECUTOR_LOADER_KPI_STRATEGY_TYPE = "executor.loader.kpi.strategy.type";

}
