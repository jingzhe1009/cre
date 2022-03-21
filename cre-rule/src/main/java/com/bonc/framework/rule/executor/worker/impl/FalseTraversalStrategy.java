package com.bonc.framework.rule.executor.worker.impl;

import com.bonc.framework.rule.executor.worker.ITraversal;
import com.bonc.framework.rule.executor.worker.ITraversalStrategy;
import com.bonc.framework.rule.resources.flow.FlowNode;

import java.util.List;

/**
 * 根据节点状态进行遍历
 * 当节点状态{@link FlowNode#getNodeState()}为{@code false}时，调用遍历器获取下一个兄弟节点
 *
 * @author qxl
 * @version 1.0
 * @date 2018年4月11日 下午4:49:27
 */
public class FalseTraversalStrategy implements ITraversalStrategy {

    @Override
    public FlowNode getNode(ITraversal iTraversal) throws Exception {
        if (iTraversal == null) throw new NullPointerException("The iTraversal is null.");
        return iTraversal.nextBroNode();
    }

    @Override
    public List<FlowNode> getNodes(ITraversal iTraversal) throws Exception {
        return null;
    }
}
