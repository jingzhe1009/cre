package com.bonc.framework.rule.resources.flow;

import org.dom4j.Element;

import java.util.List;
import java.util.Map;

/**
 * @author qxl
 * @version 1.0.0
 * @date 2016年11月14日 上午9:44:39
 */
public interface FlowNode {
    public String getNodeId();

    /**
     * 获取孩子节点
     *
     * @return
     */
    public List<FlowNode> getChildFlowNodes();

    /**
     * 设置孩子节点
     */
    public void setChildFlowNodes(List<FlowNode> nodes);

    /**
     * 设置父节点，其中"合并"会有多个父节点，开始没有父节点，其他只有一个父节点
     *
     * @param nodes
     */
    public void setParentFlowNodes(List<FlowNode> nodes);

    /**
     * 获取父节点
     *
     * @return
     */
    public List<FlowNode> getParentFlowNodes();

    /**
     * 将当前节点转成XML 如果需要转成XML，则需要在子类中自己实现
     *
     * @param Element ele 表示新生成的Element存到ele中
     * @return
     */
    Element parseToXml(Element ele, Map<String, String> data);

    /**
     * 将当前节点转成json
     *
     * @return
     */
    public String parseToJson();


    /**
     * 获取节点的最大深度
     *
     * @return
     */
    public int getMaxNodeDeep();

    /**
     * 设置节点的最大深度
     *
     * @param maxNodeDeep
     */
    public void setMaxNodeDeep(int maxNodeDeep);

    /**
     * 获取节点状态
     *
     * @return
     */
    public FlowNodeStateEnum getNodeState();

    /**
     * 设置节点状态
     *
     * @param nodeState
     */
    public void setNodeState(FlowNodeStateEnum nodeState);

    int inDegree();

    void setInDegree(int inDegree);

    boolean isSlowNode();

//    /**
//     * 删除当前节点入度和节点状态的threadLocal
//     */
//    void removeThisThreadLocal();
//
//    /**
//     * 删除当前节点以及子节点的入度和节点状态的threadLocal
//     */
//    void removeAllThreadLocal();

//	Object clone() throws CloneNotSupportedException;

}
