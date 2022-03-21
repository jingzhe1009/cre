package com.bonc.framework.rule.executor.worker.impl;

import com.bonc.framework.rule.executor.worker.ITraversal;
import com.bonc.framework.rule.resources.flow.FlowNode;
import com.bonc.framework.rule.resources.flow.FlowNodeFactory;
import com.bonc.framework.rule.resources.flow.FlowNodeStateEnum;
import com.bonc.framework.rule.resources.flow.basicflow.AbstractFlowNode;
import com.bonc.framework.rule.resources.flow.basicflow.PathFlowNode;
import com.bonc.framework.rule.resources.flow.basicflow.impl.ConvergeFlowNode;
import com.bonc.framework.rule.resources.flow.basicflow.impl.InterfaceFlowNode;
import com.bonc.framework.rule.resources.flow.util.FlowNodeUtil;
import com.google.common.collect.ImmutableList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * 将分支节点与聚合节点间的分支合并在一起，使用深度优先的方式遍历
 * <p>
 * 广度优先遍历，并根据入度对队列进行排序
 * 入度为0且是{@link InterfaceFlowNode}实例的节点可同时出队并行执行
 *
 * @author yedunyao
 * @version 1.0
 */
public class MixedTopoTraversal implements ITraversal {
    private Log log = LogFactory.getLog(getClass());

    private FlowNode preNode;//上一个节点

    private FlowNode curNode;//当前节点

    private FlowNode headNode;//头节点

    private Queue<FlowNode> queue;//待遍历节点的队列

    private Comparator<FlowNode> comparator;

    private Deque<FlowNode> traversalLog;//遍历的历史记录

    // 当isSkipElse为true时，可以跳过else分支
    private boolean isSkipElse = false;

    /**
     * 记录上一次出队行为是否是批量出队，
     * 如果是批量出队，则对出队的节点进行相应的处理
     */
    private boolean isBatchOutQueue = false;

    /**
     * 记录批量出队的节点
     */
    private List<FlowNode> batchOutQueue;

    public MixedTopoTraversal() {
    }

    public MixedTopoTraversal(FlowNode node) {
        this.headNode = node;
        init();
    }

    private void init() {
        queue = new LinkedList<>();
        this.comparator = new Comparator<FlowNode>() {
            @Override
            public int compare(FlowNode o1, FlowNode o2) {
                // 先按照入度数进行排序
                if (o1.inDegree() < o2.inDegree()) {
                    return -1;
                } else if (o1.inDegree() > o2.inDegree()) {
                    return 1;
                } else {
                    /**
                     * 按照节点类型排序, 将{@link InterfaceFlowNode}放在最后面，
                     * 使其他节点优先串行执行
                     */
                    FlowNode n1 = null;
                    FlowNode n2 = null;
                    if (o1 instanceof InterfaceFlowNode) {
                        n1 = o1;
                    }
                    if (o2 instanceof InterfaceFlowNode) {
                        n2 = o2;
                    }
                    if (n1 == null && n2 != null) {
                        return -1;
                    } else if (n1 != null && n2 == null) {
                        return 1;
                    } else if (n1 != null && n2 != null) {
                        return 0;
                    } else {
                        // 按照是否为分支排序, 将分支放在最后
                        PathFlowNode pfn1 = null;
                        PathFlowNode pfn2 = null;
                        if (o1 instanceof PathFlowNode) {
                            pfn1 = (PathFlowNode) o1;
                        }
                        if (o2 instanceof PathFlowNode) {
                            pfn2 = (PathFlowNode) o2;
                        }
                        if (pfn1 == null || pfn2 == null) {
                            return 0;
                        }
                        if (!pfn1.isElse() && pfn2.isElse()) {
                            return -1;
                        }
                        return 1;
                    }
                }
            }
        };

        traversalLog = new LinkedList<>();
        batchOutQueue = new ArrayList<>();
        //将开始节点入队
        queue.offer(headNode);
        // 广度优先更新每个节点最大高度、入度数
        FlowNodeUtil.initNode(headNode);
    }

    @Override
    public void setHeadNode(FlowNode flowNode) {
        this.headNode = flowNode;
        init();
    }

    @Override
    public FlowNode preNode() throws Exception {
        return preNode;
    }

    @Override
    public FlowNode fromNode() throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public FlowNode curNode() throws Exception {
        return curNode;
    }

    private void minusInDegree(FlowNode node) {
        int inDegree = node.inDegree();
//        inDegree  = inDegree > 0 ? inDegree - 1 : inDegree;
        inDegree = inDegree - 1;
        node.setInDegree(inDegree);
    }

    // 当执行成功时减少子节点的入度
    private void decreaseChildInDegree(FlowNode curNode) {
        if (curNode != null) {
            final List<FlowNode> childFlowNodes = curNode.getChildFlowNodes();
            if (childFlowNodes != null) {
                for (FlowNode childFlowNode : childFlowNodes) {
                    minusInDegree(childFlowNode);
                }
            }
        }
    }

    // 当执行失败时需要删除当前节点后的所有路径
    private void deepDecreaseChildInDegree(FlowNode curNode) {
        if (curNode != null) {
            final List<FlowNode> childFlowNodes = curNode.getChildFlowNodes();
            if (childFlowNodes != null) {
                for (FlowNode childFlowNode : childFlowNodes) {
                    minusInDegree(childFlowNode);
                    // 仅对非聚合节点进行递归的删除路径
                    if (!(childFlowNode instanceof ConvergeFlowNode)) {
                        deepDecreaseChildInDegree(childFlowNode);
                    }
                    /*if (childFlowNode.inDegree() == 0) {
                        deepDecreaseChildInDegree(childFlowNode);
                    }*/
                }
            }
        }
    }

    /**
     * 调用此方法时说明curNode的执行后状态为true
     * <p>
     * 1.加入距离1度的子节点
     * 2.删除子节点入度
     * 3.重排队列，获取下一个需要执行的节点
     *
     * @return
     * @throws Exception
     */
    @Override
    public FlowNode nextNode() throws Exception {
        if (isBatchOutQueue) {
            return processBatchNode();
        }
        offerChildNode(curNode);
        if (isPathNode(curNode)) {
            PathFlowNode pfn = (PathFlowNode) curNode;
            boolean isElse = pfn.isElse();
            if (!isElse) {
                // 如果当前节点为PathFlowNode，且不是else分支
                this.isSkipElse = true;
            }
        }
        decreaseChildInDegree(curNode);
        return nextBro();
    }

    // 处理并行前的一个Node
    private void beforeParaller() {
        if (curNode == null) {
            return;
        }
        if (curNode.getNodeState().equals(FlowNodeStateEnum.TRUE)) {
            offerChildNode(curNode);
            if (isPathNode(curNode)) {
                PathFlowNode pfn = (PathFlowNode) curNode;
                boolean isElse = pfn.isElse();
                if (!isElse) {
                    // 如果当前节点为PathFlowNode，且不是else分支
                    this.isSkipElse = true;
                }
            }
            decreaseChildInDegree(curNode);

        } else if (curNode.getNodeState().equals(FlowNodeStateEnum.FALSE)) {
            deepDecreaseChildInDegree(curNode);
        }
    }

    /**
     * 将子节点入队
     */
    private void offerChildNode(FlowNode curNode) {
        if (curNode == null) {//当前节点为空，则无子节点
            return;
        }
        List<FlowNode> childNodes = curNode.getChildFlowNodes();
        if (childNodes == null || childNodes.size() == 0) {
            return;
        }
        for (FlowNode childNode : childNodes) {
            push(childNode);//放入队列
        }
    }

    private FlowNode nextBro() throws Exception {
        preNode = curNode;//将当前节点赋值给前一节点
        curNode = poll();//从队列中取出下一个节点

        traversalLog.addLast(curNode);//记录遍历日志
        log.debug("traversal history :" + traversalLog.getLast());
        // 新的节点在未执行前，判断是否跳过else分支
        if (isPathNode(curNode)) {
            PathFlowNode pfn = (PathFlowNode) curNode;
            boolean isElse = pfn.isElse();
            if (isSkipElse && isElse) {
                // 跳过else分支
                this.isSkipElse = false;
                return nextBroNode();
            }
        } else {
            this.isSkipElse = false;
        }
        return curNode;
    }

    /**
     * 批量处理可并行的节点（执行后的状态{@link FlowNode#getNodeState()}）都为{@code true}
     *
     * @return
     * @throws Exception
     */
    private FlowNode processBatchNode() throws Exception {
        for (FlowNode node : batchOutQueue) {
            curNode = node;
            offerChildNode(curNode);
            decreaseChildInDegree(curNode);
            traversalLog.addLast(curNode);//记录遍历日志
            log.debug("traversal history :" + traversalLog.getLast());
        }
        isBatchOutQueue = false;
        batchOutQueue.clear();
        return nextBro();
    }

    @Override
    public FlowNode nextBroNode() throws Exception {
        if (isBatchOutQueue) {
            return processBatchNode();
        }
        deepDecreaseChildInDegree(curNode);
        return nextBro();
    }

    private boolean isPathNode(FlowNode curNode) {
        if (curNode != null) {
            String nodeType = ((AbstractFlowNode) curNode).getNodeType();
            if (FlowNodeFactory.TYPE_PATH.equals(nodeType)) {
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean hasNext() {
        throw new UnsupportedOperationException();
    }

    //存入待遍历的节点
    private void push(FlowNode node) {
        if (!queue.contains(node)) {
            queue.offer(node);
        }
    }

    private void topoSorted() {
        if (queue == null || queue.size() <= 1) {
            if (log.isDebugEnabled()) {
                log.debug("After topoSorted queue: " + queue);
            }
            return;
        }
        final FlowNode[] nodes = queue.toArray(new FlowNode[0]);
        Arrays.sort(nodes, comparator);
        Queue<FlowNode> newQueue = new LinkedList<>();
        newQueue.addAll(Arrays.asList(nodes));
        queue = newQueue;
        if (log.isDebugEnabled()) {
            log.debug("After topoSorted queue: " + queue);
        }
    }

    //从待遍历节点列表中取出一个
    private FlowNode poll() {
        topoSorted();
        checkHead();
        FlowNode node = queue.poll();
        return node;
    }

    public List<FlowNode> pollNodes() {
        beforeParaller();
//        topoSorted();
        FlowNode head;
        while ((head = nextParallelNode()) != null) {
            batchOutQueue.add(head);
        }
        isBatchOutQueue = true;
        return ImmutableList.copyOf(batchOutQueue);
    }

    /**
     * 出队前检查队顶元素入度是否为0，
     * 不为0则遍历算法有错误
     */
    private void checkHead() {
        FlowNode head = queue.peek();
        if (head != null) {
            if (head.inDegree() != 0) {
                log.warn("The in-degree of head element in queue is not zero.");
                log.warn("Current queue: " + queue + ". TraversalLog: " + traversalLog);
                throw new IllegalStateException("The in-degree of head element in queue is not zero, " +
                        "the traversal algorithm must be error.");
            }
        }
    }

    /**
     * 可以并行操作的判断条件为
     * 入度为0且非{@link PathFlowNode}实例的节点
     *
     * @return
     */
    @Override
    public boolean canParallel() {
        FlowNode head = queue.peek();
        if (head == null) {
            return false;
        }
        if (head.inDegree() == 0 &&
                (head instanceof InterfaceFlowNode)) {
            return true;
        }
        return false;
    }

    private FlowNode nextParallelNode() {
        if (canParallel()) {
            return queue.poll();
        }
        return null;
    }

    @Override
    public List<FlowNode> getList() throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public void end() {
        if (traversalLog != null) {
            log.debug("Finally traversal history :" + traversalLog);
            traversalLog = null;
        }
        queue = null;
    }

}
