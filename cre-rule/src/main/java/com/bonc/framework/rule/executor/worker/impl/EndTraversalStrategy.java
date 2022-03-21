package com.bonc.framework.rule.executor.worker.impl;

import com.bonc.framework.rule.executor.worker.ITraversal;
import com.bonc.framework.rule.executor.worker.ITraversalStrategy;
import com.bonc.framework.rule.resources.flow.FlowNode;

import java.util.List;

/**
 * 终止流程策略
 *
 * @author qxl
 * @version 1.0
 * @date 2018年4月11日 下午4:49:27
 */
public class EndTraversalStrategy implements ITraversalStrategy {

    @Override
    public FlowNode getNode(ITraversal iTraversal) {
        return null;
    }

    @Override
    public List<FlowNode> getNodes(ITraversal iTraversal) throws Exception {
        return null;
    }
}
