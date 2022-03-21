package com.bonc.framework.rule.resources.flow.basicflow;

import com.bonc.framework.rule.resources.flow.FlowNode;
import com.google.common.base.Preconditions;

import java.util.List;

/**
 * 基于链表实现的复合节点
 * <p>
 * 将分支节点后聚合节点前的节点（或路径）组合起来
 *
 * @author yedunyao
 * @since 2019/12/25 19:33
 */
public class CompoundFlowNode extends AbstractFlowNode {

    private static final long serialVersionUID = 4926113456850033764L;
    private FlowNode head;

    private FlowNode tail;

    public CompoundFlowNode() {

    }

    public FlowNode getHead() {
        return head;
    }

    public void setHead(FlowNode head) {
        Preconditions.checkNotNull(head);
        this.head = head;
        this.nodeType = "CompoundFlowNode";
        this.nodeId = head.getNodeId();
    }

    public FlowNode getTail() {
        return tail;
    }

    public void setTail(FlowNode tail) {
        Preconditions.checkNotNull(tail);
        this.tail = tail;
    }

    public boolean isEmpty() {
        return head == null;
    }

    /**
     * 非复合节点，空或是只有一个节点
     *
     * @return
     */
    public boolean nonCompound() {
        return head == tail;
    }

    @Override
    public List<FlowNode> getParentFlowNodes() {
        return getHead().getParentFlowNodes();
    }

    @Override
    public FlowNode getParentNode() {
        return getParentFlowNodes().get(0);
    }

    @Override
    public List<FlowNode> getChildFlowNodes() {
        return getTail().getChildFlowNodes();
    }

    @Override
    public boolean isSlowNode() {
        FlowNode current = head;
        while (current != null &&
                current != tail) {
            if (current.isSlowNode()) {
                return true;
            }
            FlowNode next = current.getChildFlowNodes().get(0);
            current = next;
        }
        if (current != null &&
                current.isSlowNode()) {
            return true;
        }
        return false;
    }

}
