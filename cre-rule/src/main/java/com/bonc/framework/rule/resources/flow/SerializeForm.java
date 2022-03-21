package com.bonc.framework.rule.resources.flow;

import java.util.Map;

/**
 * 序列化表单接口
 *
 * @author qxl
 * @version 1.0.0
 * @date 2016年11月15日 上午10:02:15
 */
public interface SerializeForm {
    /**
     * 将表单中的属性序列化到FlowNode中
     *
     * @param data
     */
    public void serialize(Map<String, String> data);
}
