package com.bonc.framework.rule.resources.flow.basicflow.impl;

import com.bonc.framework.rule.resources.flow.FlowNodeFactory;
import com.bonc.framework.rule.resources.flow.annotation.FlowNodeAnno;
import com.bonc.framework.rule.resources.flow.basicflow.GatewayFlowNode;
import com.bonc.framework.util.FrameLogUtil;
import org.dom4j.Element;

import java.util.Map;

/**
 * 聚合节点
 *
 * @author qxl
 * @version 1.0.0
 * @date 2016年11月14日 下午3:38:24
 */
@FlowNodeAnno(type = "join", tagName = "exclusiveGateway",
        executeClass = "com.bonc.framework.rule.executor.actor.impl.ConvergeExecutor")
public class ConvergeFlowNode extends GatewayFlowNode {

    private static final long serialVersionUID = -2138591471344458591L;

    @Override
    public Element parseToXml(Element ele, Map<String, String> data) {
        try {
            Element e = ele.addElement(FlowNodeFactory.getFlowTag(this.getNodeType()));
            e.addAttribute("id", this.getNodeId());
//			e.addAttribute("name", this.getNodeType());
            e.addAttribute("gatewayDirection", "Converging");
        } catch (Exception e) {
            FrameLogUtil.error(getClass(), e.getMessage());
        }
        return ele;
    }

    @Override
    public void serialize(Map<String, String> data) {
        //XOR-exclusiveGateway：有一个符合的即往下执行
        //AND-parallelGateway：所有分支必须执行
        this.setOptType(XOR);
    }

}
