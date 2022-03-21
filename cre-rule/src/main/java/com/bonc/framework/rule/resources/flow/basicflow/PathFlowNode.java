package com.bonc.framework.rule.resources.flow.basicflow;

import com.bonc.framework.rule.resources.flow.INodeParse;
import com.bonc.framework.rule.resources.flow.annotation.FlowNodeAnno;
import com.bonc.framework.util.JsonUtils;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 代表路径
 *
 * @author qxl
 * @version 1.0.0
 * @date 2016年11月16日 下午3:56:27
 */
@FlowNodeAnno(type = "path")
public class PathFlowNode extends AbstractFlowNode implements INodeParse {
    private static final long serialVersionUID = -8722471119355964154L;

    private boolean hasCondition = false;

    private String condition;

    private boolean isElse = false;

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        if (condition != null && !condition.isEmpty()) {
            this.setHasCondition(true);
        }
        this.condition = this.parse(condition);
    }

    public boolean isHasCondition() {
        return hasCondition;
    }

    public void setHasCondition(boolean hasCondition) {
        this.hasCondition = hasCondition;
    }

    public boolean isElse() {
        return isElse;
    }

    public void setElse(boolean isElse) {
        this.isElse = isElse;
    }

    @SuppressWarnings("unchecked")
    @Override
    public String parse(String content) {
        if (content == null || content.isEmpty() || "true".equals(content)) {
            return content;
        }
		/*
		 {
			"condition": ["[week] = 2","[week] = 3"],
			"union": "or"
		 }
		 */
        StringBuffer sb = new StringBuffer();
        Map<String, Object> map = JsonUtils.stringToCollect(content);
        Boolean isElse = (Boolean) map.get("isElse");
        if (isElse == null) {
            isElse = false;
        }
        this.setElse(isElse);
        List<String> cdtList = (List<String>) map.get("condition");
        if (cdtList == null || cdtList.size() == 0) {
            return null;
        }
        String union = (String) map.get("union");
        if (cdtList.size() > 1 && (union == null || union.isEmpty())) {
            return null;
        }
        sb.append("if ");
        boolean unionFlag = false;
        //  遍历规则条件集合
        for (int i = 0; i < cdtList.size(); i++) {
            String cdt = cdtList.get(i);
            if (cdt == null || cdt.isEmpty()) {
                continue;
            }

            cdt = parseExpression(cdt);

            if (unionFlag) {
                if ("OR".equals(union.trim().toUpperCase(Locale.ENGLISH))) {
                    sb.append(" || ");
                } else if ("AND".equals(union.trim().toUpperCase(Locale.ENGLISH))) {
                    sb.append(" && ");
                }
            }
            unionFlag = true;
            sb.append(cdt);
        }
        sb.append(" then true else false ");
        return sb.toString();
    }

    @Override
    public String toString() {
        return "PathFlowNode{" +
                "hasCondition=" + hasCondition +
                ", condition='" + condition + '\'' +
                ", isElse=" + isElse +
                ", nodeId='" + nodeId + '\'' +
                ", nodeType='" + nodeType + '\'' +
                ", nodeName='" + nodeName + '\'' +
                ", nodeState=" + nodeState +
                ", inDegree=" + inDegree +
                '}';
    }
}
