package com.bonc.framework.rule.resources.flow.util;

import com.bonc.framework.rule.resources.flow.FlowNode;
import com.bonc.framework.rule.resources.flow.basicflow.CompoundFlowNode;
import com.bonc.framework.rule.resources.flow.basicflow.impl.ConvergeFlowNode;
import com.bonc.framework.rule.resources.flow.basicflow.impl.DivergeFlowNode;
import com.google.common.collect.ImmutableList;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author qxl
 * @version 1.0
 * @date 2018年1月22日 下午4:55:24
 */
public class FlowNodeUtil {

    public static void setNodeDeep(FlowNode flowNode) {
        int maxNodeDeep = 1;
        if (flowNode == null) {
            return;
        }
        flowNode.setMaxNodeDeep(maxNodeDeep);
        FlowNode currectFlowNode = null;
        Queue<FlowNode> queue = new LinkedList<FlowNode>();//节点对应的队列
        queue.offer(flowNode);//将开始节点入队
        Map<String, FlowNode> nodeMap = new HashMap<String, FlowNode>();//处理过的节点
        while ((currectFlowNode = queue.poll()) != null) {
            if (nodeMap.containsKey(currectFlowNode.getNodeId())) {
                //已处理过此节点
                continue;
            }
            //获取当前节点的所有孩子节点
            List<FlowNode> childNodesList = currectFlowNode.getChildFlowNodes();
            if (childNodesList != null && childNodesList.size() > 0) {
                for (FlowNode childNode : childNodesList) {
                    if (childNode.getMaxNodeDeep() < currectFlowNode.getMaxNodeDeep() + 1) {
                        childNode.setMaxNodeDeep(currectFlowNode.getMaxNodeDeep() + 1);
                    }
                    queue.offer(childNode);//将孩子节点入队，等待处理
                }
            }
            //将处理过的节点存到map中，防止重复处理
            if (!(currectFlowNode instanceof ConvergeFlowNode)) {
                nodeMap.put(currectFlowNode.getNodeId(), currectFlowNode);
            }
        }
    }

    // bfs遍历更新节点入度数
    public static void initNode(FlowNode flowNode) {
        if (flowNode == null) {
            return;
        }
        FlowNode currentFlowNode = null;
        Queue<FlowNode> queue = new LinkedList<FlowNode>();//节点对应的队列
        queue.offer(flowNode);//将开始节点入队
        Map<String, FlowNode> nodeMap = new HashMap<String, FlowNode>();//处理过的节点
        int zeroInDegreeNum = 0;    // 记录入度为0的节点个数
        while ((currentFlowNode = queue.poll()) != null) {
            if (nodeMap.containsKey(currentFlowNode.getNodeId())) {
                //已处理过此节点
                continue;
            }

            // 设置入度数
            final List<FlowNode> parentFlowNodes = currentFlowNode.getParentFlowNodes();
            if (parentFlowNodes == null) {
                currentFlowNode.setInDegree(0);
                zeroInDegreeNum++;
            } else {
                currentFlowNode.setInDegree(parentFlowNodes.size());
            }

            // 合并分支节点后的第一个非路径节点后到分支或聚合节点前的路径节点
            List<FlowNode> childFlowNodes = currentFlowNode.getChildFlowNodes();
            if (currentFlowNode instanceof DivergeFlowNode &&
                    !CollectionUtils.isEmpty(childFlowNodes)) {
                // 路径节点
                for (FlowNode childNode : childFlowNodes) {
                    // 从下一个非路径节点开始
                    FlowNode grandSon = childNode.getChildFlowNodes().get(0);
                    if (grandSon == null) {
                        // not reach
                        throw new UnsupportedOperationException("路径的节点不能为空");
                    }
                    // 复合节点
                    CompoundFlowNode compoundFlowNode = new CompoundFlowNode();
                    compoundFlowNode.setHead(grandSon);
                    while (true) {
                        List<FlowNode> grandSonChildFlowNodes = grandSon.getChildFlowNodes();
                        if (CollectionUtils.isEmpty(grandSonChildFlowNodes)) {
                            break;
                        }
                        // 如果下一个节点是分支或聚合节点
                        FlowNode nextNode = grandSonChildFlowNodes.get(0);
                        if (nextNode instanceof ConvergeFlowNode ||
                                nextNode instanceof DivergeFlowNode) {
                            break;
                        }
                        grandSon = nextNode;
                    }
                    compoundFlowNode.setTail(grandSon);
                    if (compoundFlowNode.isEmpty()) {
                        // not reach
                        throw new UnsupportedOperationException("分支后的节点不能为空");
                    }
                    childNode.setChildFlowNodes(ImmutableList.<FlowNode>of(compoundFlowNode));
                } // end for
            }

            List<FlowNode> childNodesList = childFlowNodes;
            if (childNodesList != null && childNodesList.size() > 0) {
                for (FlowNode childNode : childNodesList) {
                    queue.offer(childNode);//将孩子节点入队，等待处理
                }
            }
            //将处理过的节点存到map中，防止重复处理
            if (!(currentFlowNode instanceof ConvergeFlowNode)) {
                nodeMap.put(currentFlowNode.getNodeId(), currentFlowNode);
            }
        }

        // 如果不存在入度为0的节点
        if (zeroInDegreeNum == 0) {
            throw new UnsupportedOperationException("Unsupported operations, there are rings in the rule graph.");
        }

    }

}
