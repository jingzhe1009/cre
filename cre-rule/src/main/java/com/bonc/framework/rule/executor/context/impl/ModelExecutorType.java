package com.bonc.framework.rule.executor.context.impl;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/9/17 16:14
 */
public enum ModelExecutorType {
    TASK("1", "离线任务"),
    TRIAL("2", "试算"),
    TEST("3", "测试"),
    ABTEST("4", "A/B测试"),
    WS_INTERFACE("5", "外部接口调用");

    private String code;
    private String name;

    ModelExecutorType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }


}
