package com.bonc.framework.rule.parse;

import com.bonc.framework.rule.exception.RuleParseException;
import com.bonc.framework.rule.resources.RuleResource;
import com.bonc.framework.rule.resources.flow.FlowNode;
import com.bonc.framework.rule.resources.flow.exception.ParseFlowNodeException;
import com.bonc.framework.rule.resources.flow.service.FlowNodeService;
import com.bonc.framework.rule.resources.flow.service.impl.FlowNodeServiceImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 决策树规则解析转换实现类
 *
 * @author qxl
 * @version 1.0
 * @date 2018年3月2日 下午4:59:04
 */
public class FlowRuleParse extends AbstractRuleParse {
    private Log log = LogFactory.getLog(getClass());

    private FlowNodeService flowNodeService = new FlowNodeServiceImpl();


    @Override
    RuleResource parseRule(String ruleContent) throws RuleParseException {
        if (ruleContent == null || ruleContent.isEmpty()) {
            throw new RuleParseException("模型内容为空");
        }
        try {
            FlowNode flowNode = flowNodeService.parseToNode(ruleContent,
                    ruleResource.getFolderId(), ruleResource.getRuleId());
            ruleResource.setFlowNode(flowNode);
        } catch (ParseFlowNodeException e) {
            throw new RuleParseException(e.getMessage());
        } catch (Exception e) {
            throw new RuleParseException(e);
        }
        return ruleResource;
    }


}
