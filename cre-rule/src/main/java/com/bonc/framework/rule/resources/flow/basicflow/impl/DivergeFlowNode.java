package com.bonc.framework.rule.resources.flow.basicflow.impl;

import com.bonc.framework.rule.resources.flow.FlowNodeFactory;
import com.bonc.framework.rule.resources.flow.annotation.FlowNodeAnno;
import com.bonc.framework.rule.resources.flow.basicflow.GatewayFlowNode;
import com.bonc.framework.util.FrameLogUtil;
import org.dom4j.Element;

import java.util.Map;

/**
 * 分支节点
 *
 * @author qxl
 * @version 1.0.0
 * @date 2016年11月14日 下午3:38:58
 */
@FlowNodeAnno(type = "fork", tagName = "inclusiveGateway", executeClass = "com.bonc.framework.rule.executor.actor.impl.DivergeExecutor")
public class DivergeFlowNode extends GatewayFlowNode {
    private static final long serialVersionUID = -1624394736183761297L;

    @Override
    public Element parseToXml(Element ele, Map<String, String> data) {
        try {
            Element e = ele.addElement(FlowNodeFactory.getFlowTag(this.getNodeType()));
            e.addAttribute("id", this.getNodeId());
//			e.addAttribute("name", this.getNodeType());
            e.addAttribute("gatewayDirection", "Diverging");
        } catch (Exception e) {
            FrameLogUtil.error(getClass(), e.getMessage());
        }
        return ele;
    }

    @Override
    public void serialize(Map<String, String> data) {
        this.setOptType(OR);
    }
}
