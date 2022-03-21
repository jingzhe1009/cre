package com.bonc.framework.rule.executor.worker;

import com.bonc.framework.rule.resources.flow.FlowNode;

import java.util.List;

/**
 * 遍历FlowNode策略接口
 *
 * @author qxl
 * @version 1.0
 * @date 2018年4月11日 下午4:37:36
 */
public interface ITraversalStrategy {

    /**
     * 获取节点
     *
     * @param iTraversal
     * @return
     * @throws Exception
     */
    FlowNode getNode(ITraversal iTraversal) throws Exception;

    List<FlowNode> getNodes(ITraversal iTraversal) throws Exception;

}
