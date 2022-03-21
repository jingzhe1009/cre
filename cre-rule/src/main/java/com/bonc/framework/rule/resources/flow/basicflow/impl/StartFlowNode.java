package com.bonc.framework.rule.resources.flow.basicflow.impl;

import com.bonc.framework.rule.resources.flow.FlowNodeFactory;
import com.bonc.framework.rule.resources.flow.annotation.FlowNodeAnno;
import com.bonc.framework.rule.resources.flow.basicflow.SimpleFlowNode;
import com.bonc.framework.util.FrameLogUtil;
import org.dom4j.Element;

import java.util.Map;

/**
 * 开始节点
 *
 * @author qxl
 * @version 1.0.0
 * @date 2016年11月14日 下午3:29:05
 */
@FlowNodeAnno(type = "start", tagName = "startEvent", executeClass = "com.bonc.framework.rule.executor.actor.impl.StartExecutor")
public class StartFlowNode extends SimpleFlowNode {
    private static final long serialVersionUID = 2509253681077059942L;

    @Override
    public Element parseToXml(Element ele, Map<String, String> data) {
        try {
            Element e = ele.addElement(FlowNodeFactory.getFlowTag(this.getNodeType()));
            e.addAttribute("id", this.getNodeId());
            e.addAttribute("isInterrupting", "true");
        } catch (Exception e) {
            FrameLogUtil.error(getClass(), e.getMessage());
        }
        return ele;
    }

}
