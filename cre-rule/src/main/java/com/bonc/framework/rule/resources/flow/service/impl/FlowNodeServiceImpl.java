package com.bonc.framework.rule.resources.flow.service.impl;

import com.bonc.framework.rule.resources.flow.FlowNode;
import com.bonc.framework.rule.resources.flow.FlowNodeFactory;
import com.bonc.framework.rule.resources.flow.FlowNodeStateEnum;
import com.bonc.framework.rule.resources.flow.basicflow.AbstractFlowNode;
import com.bonc.framework.rule.resources.flow.basicflow.PathFlowNode;
import com.bonc.framework.rule.resources.flow.exception.ParseFlowNodeException;
import com.bonc.framework.rule.resources.flow.service.FlowNodeService;
import com.bonc.framework.rule.resources.flow.vo.PathVo;
import com.bonc.framework.rule.resources.flow.vo.RectVo;
import com.bonc.framework.util.JsonUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author qxl
 * @version 1.0.0
 * @date 2016年11月14日 上午10:17:32
 */
public class FlowNodeServiceImpl implements FlowNodeService, Serializable {

    private Log log = LogFactory.getLog(FlowNodeServiceImpl.class);

    /**
     *
     */
    private static final long serialVersionUID = -4541667362387444817L;

    @Override
    public FlowNode parseToNode(String json) throws Exception {
        if (json == null || json.isEmpty()) {
            return null;
        }
        Map<String, RectVo> rectMap = null;
        List<PathVo> pathList = null;

        rectMap = new ConcurrentHashMap<String, RectVo>();
        pathList = new Vector<PathVo>();
        //2.将json转成vo对象
        parseJsonToVo(json, rectMap, pathList);

        //3.遍历，生成FlowNode
        FlowNode node = parseToFlowNode(rectMap, pathList);
        if (node != null) {
            node.setNodeState(FlowNodeStateEnum.TRUE);
        }
        return node;
    }

    @Override
    public FlowNode parseToNode(String json, String folderId, String ruleId) throws Exception {
        if (json == null || json.isEmpty()) {
            return null;
        }
        Map<String, RectVo> rectMap = null;
        List<PathVo> pathList = null;

        rectMap = new ConcurrentHashMap<String, RectVo>();
        pathList = new Vector<PathVo>();
        //2.将json转成vo对象
        parseJsonToVo(json, rectMap, pathList);

        //3.遍历，生成FlowNode
        FlowNode node = parseToFlowNode(rectMap, pathList, folderId, ruleId);
        if (node != null) {
            node.setNodeState(FlowNodeStateEnum.TRUE);
        }
        return node;
    }

    //2.将json串转成对应的vo
    @SuppressWarnings("unchecked")
    private void parseJsonToVo(String json, Map<String, RectVo> rectMap, List<PathVo> pathList) {
        Map<String, Object> map = JsonUtils.stringToCollect(json);
        Map<String, Object> rect_json = JsonUtils.stringToCollect(map.get("states").toString());
        if (rect_json != null && !rect_json.isEmpty()) {
            for (Map.Entry<String, Object> entry : rect_json.entrySet()) {
                RectVo rectVo = new RectVo();
                rectVo.setId(entry.getKey());
                Map<String, Object> rect_attr = JsonUtils.stringToCollect(entry.getValue().toString());
                String type = rect_attr.get("type").toString();
                rectVo.setType(type);
                Map<String, Object> props = JsonUtils.stringToCollect(rect_attr.get("props").toString());
                Map<String, String> data_map = new HashMap<String, String>();
                //遍历所有的属性，存到map中，用于序列化表单
                for (Map.Entry<String, Object> data : props.entrySet()) {
                    String key = data.getKey();
                    String value = data.getValue().toString();
                    Map<String, Object> tempMap = JsonUtils.stringToCollect(value);
                    String data_map_value = tempMap.get("value").toString();
                    data_map.put(key, data_map_value);
                }
                rectVo.setProps(data_map);
                //节点名字
                Map<String, Object> nameMap = JsonUtils.stringToCollect(rect_attr.get("text").toString());
                rectVo.setName((String) nameMap.get("text"));
                //位置信息
                Map<String, Object> posinfo = JsonUtils.stringToCollect(rect_attr.get("attr").toString());
                rectVo.setX(posinfo.get("x").toString());
                rectVo.setY(posinfo.get("y").toString());
                rectVo.setWidth(posinfo.get("width").toString());
                rectVo.setHeight(posinfo.get("height").toString());
                rectMap.put(rectVo.getId(), rectVo);
            }
        }
        Map<String, Object> path_json = JsonUtils.stringToCollect(map.get("paths").toString());
        if (path_json != null && !path_json.isEmpty()) {
            for (Map.Entry<String, Object> entry : path_json.entrySet()) {
                PathVo pathVo = new PathVo();
                pathVo.setId(entry.getKey());
                Map<String, Object> path_attr = JsonUtils.stringToCollect(entry.getValue().toString());
                pathVo.setFrom(path_attr.get("from").toString());
                pathVo.setTo(path_attr.get("to").toString());
                Map<String, Object> props = JsonUtils.stringToCollect(path_attr.get("props").toString());
                Map<String, Object> data_map = JsonUtils.stringToCollect(props.get("pathCdt").toString());
                String data = data_map.get("value").toString();
                pathVo.setData(data);
                Map<String, Object> name_map = JsonUtils.stringToCollect(props.get("text").toString());
                String name = name_map.get("value").toString();
                pathVo.setName(name);
                pathList.add(pathVo);
            }
        }
    }

    /*
     * 3.遍历，生成FlowNode
     * 思路：遍历pathList中的对应关系，找到from节点和 to节点，
     *     然后给from节点设置孩子节点(to节点)，给to结点设置父节点(from)
     */
    private FlowNode parseToFlowNode(Map<String, RectVo> rectMap, List<PathVo> pathList,
                                     String folderId, String ruleId) throws Exception {
        if (rectMap == null || rectMap.isEmpty()) {
            return null;
        }
        boolean startNodeFlag = false;
        FlowNode flowNode = null;
        Map<String, FlowNode> nodeMap = new HashMap<>();//将创建的FlowNode实例存到Map
        for (PathVo pathVo : pathList) {
            //处理线上的来源节点
            RectVo fromRectVo = rectMap.get(pathVo.getFrom());
            FlowNode fromNode = null;
            List<FlowNode> fromNodeChildNodeList = null;
            if (nodeMap.containsKey(fromRectVo.getId())) {
                fromNode = nodeMap.get(fromRectVo.getId());
                fromNodeChildNodeList = fromNode.getChildFlowNodes();
            } else {
                fromNode = createFlowNode(fromRectVo, folderId, ruleId);
                nodeMap.put(fromRectVo.getId(), fromNode);
                fromNodeChildNodeList = new ArrayList<FlowNode>();
            }

            //判断是否是开始节点
            if (!startNodeFlag && FlowNodeFactory.TYPE_START.equals(fromRectVo.getType())) {
                startNodeFlag = true;
                flowNode = fromNode;
            }

            //处理线及条件
            List<FlowNode> pathNodeParentNodeList = new ArrayList<FlowNode>();
            pathNodeParentNodeList.add(fromNode);//线 的父节点
            RectVo pathRectVo = new RectVo();
            pathRectVo.setId(pathVo.getId());
            pathRectVo.setName(pathVo.getName());
            pathRectVo.setType(FlowNodeFactory.TYPE_PATH);
            FlowNode pathFlowNode = createFlowNode(pathRectVo, folderId, ruleId);
            if (pathVo.getData() != null && !pathVo.getData().isEmpty()) {//判断是否配置了条件
                ((PathFlowNode) pathFlowNode).setCondition(pathVo.getData());
            }


            //处理线上的目标节点
            RectVo toRectVo = rectMap.get(pathVo.getTo());
            FlowNode toNode = null;
            List<FlowNode> toNodeParentNodeList = null;
            if (nodeMap.containsKey(toRectVo.getId())) {
                toNode = nodeMap.get(toRectVo.getId());
                toNodeParentNodeList = toNode.getParentFlowNodes();
            } else {
                toNode = createFlowNode(toRectVo, folderId, ruleId);
                nodeMap.put(toRectVo.getId(), toNode);
                toNodeParentNodeList = new ArrayList<>();
            }

            //线 的子节点
            List<FlowNode> pathNodeChildNodeList = new ArrayList<FlowNode>();
            pathNodeChildNodeList.add(toNode);
            //设置关系
            fromNode.setChildFlowNodes(addNode(fromNodeChildNodeList, pathFlowNode));
            pathFlowNode.setParentFlowNodes(pathNodeParentNodeList);
            pathFlowNode.setChildFlowNodes(pathNodeChildNodeList);
            toNode.setParentFlowNodes(addNode(toNodeParentNodeList, pathFlowNode));
        }
        if (!startNodeFlag) {//没有开始节点
            throw new ParseFlowNodeException("模型中没有开始节点");
        }
        return flowNode;
    }

    //添加FlowNode到List中，需要判断是否已存在
    private List<FlowNode> addNode(List<FlowNode> nodesList, FlowNode currctFlowNode) {
        if (nodesList == null) {
            nodesList = new ArrayList<FlowNode>();
        }
        if (nodesList.size() == 0) {
            nodesList.add(currctFlowNode);
        } else {
            boolean flag = true;
            for (FlowNode node : nodesList) {
                if (currctFlowNode == node) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                nodesList.add(currctFlowNode);
            }
        }
        return nodesList;
    }


    //创建节点实例
    private FlowNode createFlowNode(RectVo currectRect, String folderId, String ruleId) throws Exception {
        AbstractFlowNode node = (AbstractFlowNode) FlowNodeFactory.getFlowNode(currectRect.getType());
        if (node != null) {
            node.setNodeId(currectRect.getId());
            node.setNodeType(currectRect.getType());
            node.setNodeName(currectRect.getName());
            node.setX(currectRect.getX());
            node.setY(currectRect.getY());
            node.setWidth(currectRect.getWidth());
            node.setHeight(currectRect.getWidth());
            node.setRuleId(ruleId);
            node.setFolderId(folderId);
        }
        if (currectRect.getProps() != null && !currectRect.getProps().isEmpty()) {
            if (log.isDebugEnabled()) {
                log.debug("开始编译节点" + currectRect.getName());
            }
            node.serialize(currectRect.getProps());
        }
        return node;
    }

    private FlowNode parseToFlowNode(Map<String, RectVo> rectMap, List<PathVo> pathList) throws Exception {
        return this.parseToFlowNode(rectMap, pathList, null, null);
    }
}
