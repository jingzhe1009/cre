package com.bonc.frame.service.modelCompare;

import com.alibaba.fastjson.JSONObject;
import com.bonc.frame.entity.modelCompare.entity.ModelCompareResult;
import com.bonc.frame.entity.rule.RuleDetailWithBLOBs;
import com.bonc.frame.util.ModelComparUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.bonc.frame.util.ModelComparUtil.differenceSet;

@Service
public class ModelComparOperator {
    Log log = LogFactory.getLog(ModelComparOperator.class);


    private Map<String, ModelCompare> modelComparMap;

    @Autowired
    public ModelComparOperator(List<ModelCompare> modelCompars) {
        modelComparMap = new HashMap<>(16);
        for (ModelCompare modelCompar : modelCompars) {
            modelComparMap.put(modelCompar.getSupport().getValue(), modelCompar);
        }
    }

    /**
     * 模型比较的入口
     *
     * @param oldModelDetailWithBLOBs
     * @param newModelDetailWithBLOBs
     * @param fromRuleDetail
     * @return
     */
    public Map<String, Object> startModelCompar(RuleDetailWithBLOBs oldModelDetailWithBLOBs, RuleDetailWithBLOBs newModelDetailWithBLOBs, RuleDetailWithBLOBs fromRuleDetail) {
        String oldRuleName = null;
        String newRuleName = null;
        String oldRuleId = null;
        String newRuleId = null;
        String oldVersion = null;
        String newVersion = null;

        JSONObject oldModel = null;
        JSONObject newModel = null;

        if (oldModelDetailWithBLOBs != null) {
            oldRuleName = oldModelDetailWithBLOBs.getRuleName();
            oldRuleId = oldModelDetailWithBLOBs.getRuleId();
            oldVersion = oldModelDetailWithBLOBs.getVersion();

            String oldModelJson = oldModelDetailWithBLOBs.getRuleContent();
            oldModel = JSONObject.parseObject(oldModelJson);
        }
        if (newModelDetailWithBLOBs != null) {
            newRuleName = newModelDetailWithBLOBs.getRuleName();
            newRuleId = newModelDetailWithBLOBs.getRuleId();
            newVersion = newModelDetailWithBLOBs.getVersion();

            String newModelJson = newModelDetailWithBLOBs.getRuleContent();
            newModel = JSONObject.parseObject(newModelJson);
        }
//        if (oldModel == null || oldModel.isEmpty() || newModel == null || newModel.isEmpty()) {
//            log.error("转换JSONObject为空,oldModel:" + oldModelJson + "\nnewModel:" + newModelJson);
//            return null;
//        }
        if (oldModel == null) {
            oldModel = new JSONObject();
        }
        if (newModel == null) {
            newModel = new JSONObject();
        }

        ModelCompareResult modelCompareResult = new ModelCompareResult();
        modelCompareResult.setNewRuleDetailWithBLOBs(newModelDetailWithBLOBs);
        modelCompareResult.setOldRuleDetailWithBLOBs(oldModelDetailWithBLOBs);
        modelCompareResult.setFromRule(fromRuleDetail);

        comparModelContent(modelCompareResult, oldModel, newModel, ModelCompareResult.RECT_Type);
        comparModelContent(modelCompareResult, oldModel, newModel, ModelCompareResult.PATH_Type);

        Map<String, Object> result = modelCompareResult.getModelContentCompareResult();
//        log.trace("比较结果 result : " + JSONObject.toJSONString(result));

        return result;
    }


    // FIXME : 如果节点为空,路径一定为空  -- 这个还用不用处理了?
    public void comparModelContent(ModelCompareResult result, JSONObject oldModel, JSONObject newModel, String type) {


        String oldModelJsonString;
        String newModelJsonString;
        if (ModelCompareResult.RECT_Type.equals(type)) {
            oldModelJsonString = getJsonValue(oldModel, "states");
            newModelJsonString = getJsonValue(newModel, "states");
        } else {
            oldModelJsonString = getJsonValue(oldModel, "paths");
            newModelJsonString = getJsonValue(newModel, "paths");
        }

        JSONObject oldStatesOrPaths = JSONObject.parseObject(oldModelJsonString);
        JSONObject newStatesOrPaths = JSONObject.parseObject(newModelJsonString);

        if (!ModelComparUtil.isEmpty(oldStatesOrPaths) || !ModelComparUtil.isEmpty(newStatesOrPaths)) {
            if (ModelComparUtil.isEmpty(oldStatesOrPaths)) {
                //如果旧的模型中的节点为空，那么新的模型中的所有的节点都是  新加的  create
                result.addOrDeleteAllInfoToList(newStatesOrPaths, newStatesOrPaths.keySet(), type, true);
            } else if (ModelComparUtil.isEmpty(newStatesOrPaths)) {
                // 如果新的模型中的节点为空,那么旧的模型中的所有的节点都是被  删除的  delete
                result.addOrDeleteAllInfoToList(oldStatesOrPaths, oldStatesOrPaths.keySet(), type, false);

            } else {
                // 两个都不为空 则比较

                Set<String> oldStatesOrPathsKeySet = oldStatesOrPaths.keySet();
                Set<String> newStatesOrPathsKeySet = newStatesOrPaths.keySet();

                Set<String> addKeySet = differenceSet(newStatesOrPathsKeySet, oldStatesOrPathsKeySet); // 新的减去旧的
                Set<String> deleteKeySet = differenceSet(oldStatesOrPathsKeySet, newStatesOrPathsKeySet); // 旧的减去新的
                Set<String> updateKeySet = ModelComparUtil.intersectionSet(newStatesOrPathsKeySet, oldStatesOrPathsKeySet); // 交集 新旧模型里面都有,则判断有没有更新
//                log.debug("增加的节点id:[" + addKeySet + "]  更新的节点id:[" + updateKeySet + "]  删除的节点id:[" + deleteKeySet + "]");

                // 处理 添加/删除的节点
                result.addOrDeleteAllInfoToList(newStatesOrPaths, addKeySet, type, true);  // add的节点路径永远来自于新的模型
                result.addOrDeleteAllInfoToList(oldStatesOrPaths, deleteKeySet, type, false); // delete的节点路径永远来自于旧的模型

                // 处理 更新的节点
                for (String key : updateKeySet) {
                    JSONObject oldStateOrPathNode = JSONObject.parseObject(getJsonValue(oldStatesOrPaths, key));
                    JSONObject newStateOrPathNode = JSONObject.parseObject(getJsonValue(newStatesOrPaths, key));
                    if (ModelCompareResult.RECT_Type.equals(type)) {
                        comparRectNodeJson(result, key, oldStateOrPathNode, newStateOrPathNode);
                    } else {
                        comparPathNodeJson(result, key, oldStateOrPathNode, newStateOrPathNode);
                    }
                }
            }
        }

    }


    private void comparRectNodeJson(ModelCompareResult result, String NodeId, JSONObject oldNode, JSONObject newNode) {
        if (oldNode != null || newNode != null) {
            if (oldNode == null) {
                result.addOrDeleteOneInfoToList(oldNode, ModelCompareResult.RECT_Type, true);
            } else if (newNode == null) {
                result.addOrDeleteOneInfoToList(oldNode, ModelCompareResult.RECT_Type, false);
            } else {
                //判断节点类型,获取比较器
                String oldNodeType = getJsonValue(oldNode, "type");
                String newNodeType = getJsonValue(newNode, "type");
                if (!StringUtils.isBlank(oldNodeType) && !StringUtils.isBlank(newNodeType)) {
                    if (oldNodeType.equals(newNodeType)) {

                        ModelCompare modelCompar = modelComparMap.get(oldNodeType);
                        if (modelCompar == null) {
                            modelCompar = modelComparMap.get("abstract");
                        }
                        modelCompar.comparNodePropsJson(result, oldNode, newNode, NodeId);

                    } else {
                        //FIXME:  节点类型不一样 -- 一般不会发生,同一个模型进行比较的话,一定不会发生  -- 应该怎么处理 ?
                    }
                }
            }
        }


        return;
    }

    private void comparPathNodeJson(ModelCompareResult result, String nodeId, JSONObject oldNode, JSONObject newNode) {
        if (oldNode != null || newNode != null) {
            if (oldNode == null) {
                newNode.put("pathId", nodeId);
                result.addOrDeleteOneInfoToList(oldNode, ModelCompareResult.PATH_Type, true);
            } else if (newNode == null) {
                oldNode.put("pathId", nodeId);
                result.addOrDeleteOneInfoToList(oldNode, ModelCompareResult.PATH_Type, false);
            } else {
                newNode.put("pathId", nodeId);
                oldNode.put("pathId", nodeId);
                modelComparMap.get("path").comparNodePropsJson(result, oldNode, newNode, nodeId);
            }
        }
    }


    public static boolean isBlank(Object object) {
        if (object != null && !StringUtils.isBlank(object + "")) {
            return false;
        }
        return true;
    }

    public static String getJsonValue(JSONObject jsonObject, String key) {
        if (StringUtils.isBlank(jsonObject.getString(key))) {
            return null;
        }
        return jsonObject.get(key) + "";
    }


}
