package com.bonc.framework.rule.kpi;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/1/15 9:42
 */
public class KpiConstant {
    /**
     * 加载指标的策略
     */
    // 编译时加载(默认) 模型执行前就加载指标
    public static final String LOADER_KPI_STRATEGY_TYPE_COMPILE = "COMPILE";
    // 运行时加载指标--只加载一次, 第一次遇见该指标时,加载该指标,之后遇见该指标则不进行加载
    public static final String LOADER_KPI_STRATEGY_TYPE_RUNTIME_FIRST = "ONCE";
    // 运行时加载指标--每次都加载, 每次遇见该指标都重新加载一次
    public static final String LOADER_KPI_STRATEGY_TYPE_RUNTIME_EVERY = "FOREVER";
}
