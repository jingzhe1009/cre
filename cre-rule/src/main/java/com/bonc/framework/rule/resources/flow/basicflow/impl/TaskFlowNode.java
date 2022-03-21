package com.bonc.framework.rule.resources.flow.basicflow.impl;

import com.bonc.framework.rule.resources.flow.annotation.FlowNodeAnno;
import com.bonc.framework.rule.resources.flow.basicflow.AbstractFlowNode;
import org.dom4j.Element;

import java.util.Map;

/**
 * @author qxl
 * @version 1.0
 * @date 2017年8月30日 上午10:38:04
 */
@FlowNodeAnno(type = "node", executeClass = "com.bonc.framework.rule.executor.actor.impl.TaskExecutor")
public class TaskFlowNode extends AbstractFlowNode {
    private static final long serialVersionUID = -7915190644372164722L;
    private final String ruleIdKey = "nodeId";
    private final String ruleNameKey = "nodeName";

    private String ruleId;

    private String ruleName;

    private String priority;

    @Override
    public String getRuleId() {
        return ruleId;
    }

    @Override
    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getPriority() {
        if (priority == null || priority.isEmpty()) {
            return "rule_priority";
        }
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    @Override
    public Element parseToXml(Element ele, Map<String, String> data) {
        return null;
    }


    @Override
    public void serialize(Map<String, String> data) {
        if (data.containsKey(ruleIdKey)) {
            this.ruleId = data.get(ruleIdKey);
        }
        if (data.containsKey(ruleNameKey)) {
            this.ruleName = data.get(ruleNameKey);
        }
        if (data.containsKey("priority")) {
            this.priority = data.get("priority");
        }
    }

}
