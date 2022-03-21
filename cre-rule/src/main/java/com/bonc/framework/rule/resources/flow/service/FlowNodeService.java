package com.bonc.framework.rule.resources.flow.service;

import com.bonc.framework.rule.resources.flow.FlowNode;

/**
 * @author qxl
 * @version 1.0.0
 * @date 2016年11月14日 上午10:16:52
 */
public interface FlowNodeService {
    /**
     * 将指定格式的json串转成Node节点
     *
     * @param json
     * @return
     * @throws Exception
     */
    public FlowNode parseToNode(String json) throws Exception;

    public FlowNode parseToNode(String json, String folderId, String ruleId) throws Exception;
}
