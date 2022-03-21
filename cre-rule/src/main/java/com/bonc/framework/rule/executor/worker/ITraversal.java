package com.bonc.framework.rule.executor.worker;

import com.bonc.framework.rule.resources.flow.FlowNode;

import java.util.List;

/**
 * 遍历FlowNode的接口
 *
 * @author qxl
 * @version 1.0
 * @date 2017年8月30日 上午9:33:32
 */
public interface ITraversal {

    /**
     * 设置头结点
     *
     * @param flowNode
     */
    public void setHeadNode(FlowNode flowNode);

    /**
     * 获取前一个节点
     *
     * @return
     * @throws Exception
     */
    public FlowNode preNode() throws Exception;

    /**
     * 获取当前节点来源的节点，即父节点中的一个
     *
     * @return
     * @throws Exception
     */
    public FlowNode fromNode() throws Exception;

    /**
     * 获取当前节点
     *
     * @return
     * @throws Exception
     */
    public FlowNode curNode() throws Exception;

    /**
     * 获取下一个节点。
     * 若上个节点包含条件判定，条件判定为true，则使用此方法获取下一节点。
     *
     * @return
     * @throws Exception
     */
    public FlowNode nextNode() throws Exception;

    /**
     * 获取下一个兄弟节点。
     * 即如果上一节点包含条件判定，且条件判定false，
     * 此分支不再往下执行，使用此方法来获取下一节点。
     *
     * @return
     * @throws Exception
     */
    public FlowNode nextBroNode() throws Exception;

    /**
     * 判断是否还有下一个节点
     *
     * @return
     */
    public boolean hasNext();

    /**
     * 获取list，用来判断是否为简单流程。
     * 简单流程：无分支，只有一个线性顺序。
     *
     * @return
     * @throws Exception
     */
    public List<FlowNode> getList() throws Exception;

    boolean canParallel();

    List<FlowNode> pollNodes();

    void end();

}
