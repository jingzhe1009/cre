package com.bonc.framework.rule.resources.flow.basicflow.impl;

import com.bonc.framework.rule.resources.flow.FlowNodeFactory;
import com.bonc.framework.rule.resources.flow.annotation.FlowNodeAnno;
import com.bonc.framework.rule.resources.flow.basicflow.SimpleFlowNode;
import com.bonc.framework.util.FrameLogUtil;
import org.dom4j.Element;

import java.util.Map;

/**
 * 结束节点
 *
 * @author qxl
 * @version 1.0.0
 * @date 2016年11月14日 下午3:33:57
 */
@FlowNodeAnno(type = "end", tagName = "endEvent", executeClass = "com.bonc.framework.rule.executor.actor.impl.EndExecutor")
public class EndFlowNode extends SimpleFlowNode {
    private static final long serialVersionUID = 7377274332282309050L;

    @Override
    public Element parseToXml(Element ele, Map<String, String> data) {
        try {
            Element e = ele.addElement(FlowNodeFactory.getFlowTag(this.getNodeType()));
            e.addAttribute("id", this.getNodeId());
            e.addAttribute("name", this.getNodeType());
            e.addElement("terminateEventDefinition");
        } catch (Exception e) {
            FrameLogUtil.error(getClass(), e.getMessage());
        }
        return ele;
    }

}
