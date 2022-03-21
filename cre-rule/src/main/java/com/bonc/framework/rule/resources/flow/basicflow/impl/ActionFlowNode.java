package com.bonc.framework.rule.resources.flow.basicflow.impl;

import com.bonc.framework.rule.resources.flow.FlowNodeFactory;
import com.bonc.framework.rule.resources.flow.INodeParse;
import com.bonc.framework.rule.resources.flow.annotation.FlowNodeAnno;
import com.bonc.framework.rule.resources.flow.basicflow.AbstractFlowNode;
import com.bonc.framework.util.FrameLogUtil;
import com.bonc.framework.util.JsonUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;

import java.util.*;

/**
 * 规则集节点
 *
 * @author qxl
 * @version 1.0.0
 * @date 2016年11月14日 上午10:10:43
 */
@FlowNodeAnno(type = "task", tagName = "businessRuleTask")
public class ActionFlowNode extends AbstractFlowNode implements INodeParse {
    private static final long serialVersionUID = 4420478220496422270L;

    private Log log = LogFactory.getLog(getClass());

    private final String actionKey = "action";

    private String action;

    private String priority;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = this.parse(action);
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    @Override
    public void serialize(Map<String, String> data) {
        if (data.containsKey(actionKey)) {
            this.setAction(data.get(actionKey));
        }
        if (data.containsKey("priority")) {
            this.setPriority(data.get("priority"));
        }
    }

    @Override
    public Element parseToXml(Element ele, Map<String, String> data) {
        try {
            Element e = ele.addElement(FlowNodeFactory.getFlowTag(this.getNodeType()));
            e.addAttribute("id", this.getNodeId());
            e.addAttribute("name", this.getNodeType());
            e.addAttribute("g:ruleFlowGroup", FlowNodeFactory.ruleFlowGroupHeader + data.get("ruleId") + "_" + this.getNodeId());
            Element e1 = e.addElement("ioSpecification");
            e1.addElement("inputSet");
            e1.addElement("outputSet");
        } catch (Exception e) {
            FrameLogUtil.error(getClass(), e.getMessage());
        }
        return ele;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public String parse(String content) {
        if (content == null || content.isEmpty()) {
            return content;
        }
		/*
		   [{
				"RHS": ["[result1] = 1"],
				"actRuleName": "rule01",
				"LHS": {
					"condition": ["[color] = red"],
					"union": "or"
				},
				"isEndAction": true,
				"isEndFlow": true
				
			},
			{
				"RHS": ["[result1] = aaa"],
				"actRuleName": "rule02",
				"LHS": {
					"condition": ["[color] = color","[name] = xxx"],
					"union": "and"
				},
				"isEndAction": true,
				"isEndFlow": false
			}]
		 */
        //if color == 'red' then {result1 = 1}; if color == 'color' && name == 'xxx' then {result2 = 'aaa'};
//		String content = "[{\"RHS\":[\"[result1] = 1\"],\"actRuleName\":\"rule01\",\"LHS\":{\"condition\":[\"[color] = red\"],\"union\":\"or\"}},{\"RHS\":[\"[result1] = aaa\"],\"actRuleName\":\"rule02\",\"LHS\":{\"condition\":[\"[color] = color\",\"[name] = xxx\"],\"union\":\"and\"}}]";
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        List<Map> ruleList = JsonUtils.toList(content, Map.class);
        if (ruleList == null || ruleList.size() == 0) {
            return null;
        }
        for (Map map : ruleList) {
            Map<String, Object> resultMap = new HashMap<String, Object>();
            resultMap.put("actRuleName", map.get("actRuleName"));
            resultMap.put("isEndAction", map.get("isEndAction"));
            resultMap.put("isEndFlow", map.get("isEndFlow"));

            StringBuffer sb = new StringBuffer();
            //条件
            Map<String, Object> lhsMap = (Map<String, Object>) map.get("LHS");
            List<String> cdtList = (List<String>) lhsMap.get("condition");
            if (cdtList == null || cdtList.size() == 0) {
                sb.append(" if 1==1 ");
            } else {
                String union = (String) lhsMap.get("union");
                if (cdtList.size() > 1 && (union == null || union.isEmpty())) {
                    continue;
                }
                sb.append(" if ");
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

                    sb.append(cdt);
                    unionFlag = true;
                }
            }
            //结果
            List<String> rhsList = (List<String>) map.get("RHS");
            if (rhsList == null || rhsList.size() == 0) {
                sb.append(" then true;");
                continue;
            }
            sb.append(" then {");
            boolean resultUnionFlag = false;
            for (int i = 0; i < rhsList.size(); i++) {
                String res = rhsList.get(i);
                if (res == null || res.isEmpty()) {
                    continue;
                }
                res = parseExpression(res);

                if (resultUnionFlag) {
                    sb.append(";");
                }

                resultUnionFlag = true;
                sb.append(res);
            }
            sb.append("};");

            if (log.isDebugEnabled()) {
                log.debug("编译后的规则表达式：" + sb.toString());
            }

            resultMap.put("rule", sb.toString());
            result.add(resultMap);
        }
        return JsonUtils.toJSONString(result);
    }

    @Override
    public String toString() {
        return "ActionFlowNode{" +
                "action='" + action + '\'' +
                ", ruleId='" + ruleId + '\'' +
                ", folderId='" + folderId + '\'' +
                ", nodeType='" + nodeType + '\'' +
                ", nodeId='" + nodeId + '\'' +
                ", nodeName='" + nodeName + '\'' +
                '}';
    }
}
