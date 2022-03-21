package com.bonc.frame.security;

/**
 * @author yedunyao
 * @date 2019/7/28 11:23
 */
public enum ResourceType {

    ALL("0"),

    /**
     * 菜单url
     */
    MENU("1"),

    /**
     * 按钮url
     */
    BUTTON("2"),

    /**
     * 数据-模型，记录有权限的模型的各种操作
     */
    DATA_MODEL("3"),

    /**
     * 数据-元数据
     */
    DATA_METADATA("4"),

    /**
     * 数据-公共参数
     */
    DATA_PUB_VARIABLE("5"),

    /**
     * 数据-公共接口
     */
    DATA_PUB_API("6"),

    /**
     * 数据-规则库
     */
    DATA_PUB_RULE_SET("7"),

    /**
     * 数据-模型库
     */
    DATA_PUB_MODEL("8"),

    /**
     * 数据-数据源
     */
    DATA_DATASOURCE("9"),

    /**
     * 数据-离线任务
     */
    DATA_TASK("10"),

    /**
     * 数据-指标
     */
    DATA_KPI("11"),

    /**
     * 数据-场景
     */
    DATA_FOLDER("12");

    private String type;

    ResourceType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

}
