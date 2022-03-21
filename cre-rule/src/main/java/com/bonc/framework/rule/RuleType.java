package com.bonc.framework.rule;

/**
 * 规则类型枚举
 *
 * @author qxl
 * @version 1.0.0
 * @date 2016年12月14日 上午10:18:52
 */
public enum RuleType {

    /**
     * 表达式规则的类型
     */  // 评分模型
    expRule_type("0"),
    /**
     * 决策树规则类型
     */    // 规则模型
    treeRule_type("1");

    private String type;

    private RuleType(String type) {
        this.type = type;
    }

    public String toString() {
        return this.type;
    }


    /**
     * 将给定的规则类型的值转成规则包枚举
     *
     * @param value
     * @return
     * @throws Exception
     */
    public static RuleType parseToEnum(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        RuleType result = null;
        RuleType[] values = RuleType.values();//获取枚举中所有的RuleType
        for (RuleType rpt : values) {
            if (rpt.type.equals(value)) {
                result = rpt;
                break;
            }
        }
        return result;
    }

}
