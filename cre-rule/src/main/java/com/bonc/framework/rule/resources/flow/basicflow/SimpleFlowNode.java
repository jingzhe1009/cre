package com.bonc.framework.rule.resources.flow.basicflow;

import org.dom4j.Element;

import java.util.Map;

/**
 * @author qxl
 * @version 1.0.0
 * @date 2016年11月14日 上午10:09:41
 * @see com.bonc.framework.rule.resources.flow.basicflow.impl.StartFlowNode
 * @see com.bonc.framework.rule.resources.flow.basicflow.impl.EndFlowNode
 * @see com.bonc.framework.rule.resources.flow.basicflow.impl.TrueFlowNode
 * @see com.bonc.framework.rule.resources.flow.basicflow.impl.FalseFlowNode
 */
public class SimpleFlowNode extends AbstractFlowNode {

    private static final long serialVersionUID = -7028311425795384729L;

    @Override
    public Element parseToXml(Element ele, Map<String, String> data) {
        return ele;
    }

}
