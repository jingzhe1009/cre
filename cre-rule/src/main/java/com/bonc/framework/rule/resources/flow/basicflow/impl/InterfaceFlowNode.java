package com.bonc.framework.rule.resources.flow.basicflow.impl;

import com.bonc.framework.rule.resources.flow.annotation.FlowNodeAnno;
import com.bonc.framework.rule.resources.flow.basicflow.AbstractFlowNode;

import java.util.Map;

/**
 * 接口节点
 *
 * @author qxl
 * @version 1.0
 * @date 2018年4月3日 上午10:23:03
 */
@FlowNodeAnno(type = "interface")
public class InterfaceFlowNode extends AbstractFlowNode {
    private static final long serialVersionUID = -5713304952944087309L;
    private String apiId;

    public String getApiId() {
        return apiId;
    }

    public void setApiId(String apiId) {
        this.apiId = apiId;
    }

    @Override
    public boolean isSlowNode() {
        return true;
    }

    @Override
    public void serialize(Map<String, String> data) {
        if (data.containsKey("ointerface")) {
            this.setApiId(data.get("ointerface"));
        }

    }

}
