package com.bonc.frame.module.lock.distributed.curator;

/**
 * @author yedunyao
 * @date 2019/7/28 11:23
 */
public enum LockDataType {
    /**
     * 模型
     */

    MODEL("MODEL"),


    /**
     * 变量
     */
    VARIABLE("VARIABLE"),
    /**
     * 变量组
     */
    VARIABLE_GROUP("VARIABLE_GROUP"),


    /**
     * 接口
     */
    API("API"),
    /**
     * 接口组
     */
    API_GROUP("API_GROUP"),

    /**
     * 规则库
     */
    RULE_SET("RULE_SET"),
    /**
     * 规则库组
     */
    RULE_SET_GROUP("RULE_SET_GROUP"),
    /**
     * 指标
     */
    KPI("KPI"),
    /**
     * 指标
     */
    KPI_GROUP("KPI_GROUP");

    private String type;

    LockDataType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

}
