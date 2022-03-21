package com.bonc.framework.entity.format;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @Author: wangzhengbao
 * @DATE: 2019/11/29 18:47
 */
public class VariableFormat {
    static Log log = LogFactory.getLog(VariableFormat.class);

    /**
     * 只对 数组类型,数字类型 进行了转换处理  其他的都当做字符串处理
     * 数组类型,如果为空,返回一个空的String类型的数组 new String[]{};
     * 数字类型,数值型默认转化成{@code Double}
     * 可能会返回{@code null} 需要由调用方法处理空指针
     *
     * @param typeId        类型id
     * @param variableCode
     * @param variableValue
     * @return
     */
    public static Object convertValue(String typeId, String variableCode, Object variableValue) {
        if (variableCode == null) {
            throw new IllegalArgumentException("参数值转化失败，参数编码不能为空");
        }

        if ("5".equals(typeId)) {//数组类型
            if (variableValue == null || "".equals(variableValue.toString().trim())) {
                return new String[]{};
            }
            try {
                String[] valueArray = ((String) variableValue).split(",");
                return valueArray;
            } catch (Exception e) {
                log.warn("数组类型变量[" + variableCode + "]转换失败！值[" + variableValue + "] ，赋值默认值\"[]\"", e);
                throw new IllegalArgumentException("数组类型变量[" + variableCode + "]转换失败！值[" + variableValue + "]", e);
            }
        } else if ("2".equals(typeId) || "4".equals(typeId)) {//如果是数字类型的变量则统一转换成double   update by jxw
            if (variableValue == null || "".equals(variableValue.toString().trim())) {
                return null;
            } else {
                if ("2".equals(typeId)) {
                    // 整型
                    try {
                        int value = Integer.parseInt(variableValue.toString());
                        return value;
                    } catch (NumberFormatException e) {
                        final String msg = String.format("数值型参数输入格式不合法，" +
                                "[变量编码: %s, 变量输入值: %s]", variableCode, variableValue);
                        log.warn(msg, e);
                        return null;
                    }
                } else {
                    try {
                        double value = Double.parseDouble(variableValue.toString());
                        return value;
                    } catch (NumberFormatException e) {
                        log.warn("变量【" + variableCode + "】转换失败！值【" + variableValue + "】 ，赋值默认值【空】", e);
                        return null;
                    }
                }
            }
        } else { // 其他的都如果为空当做字符串进行处理
            if (variableValue == null) {
                return "";
            } else {
                return variableValue;
            }
        }
    }
}
