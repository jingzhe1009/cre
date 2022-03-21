package com.bonc.framework.rule.resources;

import com.bonc.framework.rule.resources.flow.FlowNode;

/**
 * 流程类型规则资源类，增加FlowNode熟悉，即规则解析转换成FlowNode
 *
 * @author qxl
 * @version 1.0
 * @date 2018年3月5日 下午5:01:03
 */
public class FlowRuleResource extends RuleResource {

    private FlowNode flowNode;

    public FlowRuleResource() {
        super();
    }

    public FlowNode getFlowNode() {
        return flowNode;
    }

    public void setFlowNode(FlowNode flowNode) {
        this.flowNode = flowNode;
    }


}
