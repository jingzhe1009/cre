package com.bonc.frame.service.modelCompare;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bonc.frame.entity.modelCompare.entity.ConditionListCompareResult;
import com.bonc.frame.util.CollectionUtil;
import com.bonc.frame.util.ModelComparUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.*;


/**
 * @Author: wangzhengbao
 * @DATE: 2019/12/20 9:55
 */
public class ConditionListCompareImp {
    private String oldConditionListJSONString;
    private String newConditionListJSONString;
    private String oldConditionTxtJSONString;
    private String newConditionTxtJSONString;
    private JSONArray oldConditionsTxt;
    private JSONArray newConditionsTxt;

    private String type;
    private String txtType;

    private ConditionListCompareResult conditionListCompareResult; //类型 , RuleComparResult中的几个常量

    private ConditionListCompareResult conditionTxtListCompareResult;

    public Map<String, Object> getConditionListCompareResult() {
        return conditionListCompareResult.getResult();
    }

    public Map<String, Object> getConditionTxtListCompareResult() {
        return conditionTxtListCompareResult.getResult();
    }

    public ConditionListCompareImp(String oldConditionListJSONString, String newConditionListJSONString, String type, String oldConditionTxtJSONString, String newConditionTxtJSONString, String txtType) {
        this.oldConditionListJSONString = oldConditionListJSONString;
        this.newConditionListJSONString = newConditionListJSONString;
        this.type = type;
        conditionListCompareResult = new ConditionListCompareResult();
        this.oldConditionTxtJSONString = oldConditionTxtJSONString;
        this.newConditionTxtJSONString = newConditionTxtJSONString;
        this.oldConditionsTxt = JSONArray.parseArray(oldConditionTxtJSONString);
        this.newConditionsTxt = JSONArray.parseArray(newConditionTxtJSONString);
        this.txtType = txtType;
        conditionTxtListCompareResult = new ConditionListCompareResult();
        comparConditionList();
    }

    private void insertTxtDifferentToRestlt(Integer oldIndex, Integer newIndex, String even) {
        if (CollectionUtil.isEmpty(oldConditionsTxt) && CollectionUtil.isEmpty(newConditionsTxt)) {
            return;
        }
        if (even == null) {
            return;
        }
        if ("add".equals(even)) {
            if (newIndex == null) {
                //TODO : ERR
            }
            conditionTxtListCompareResult.addOrDeleteOneInfoToList(newConditionsTxt.get(newIndex), txtType, true);
        } else if ("delete".equals(even)) {
            if (oldIndex == null) {
                //TODO : ERR
            }
            conditionTxtListCompareResult.addOrDeleteOneInfoToList(oldConditionsTxt.get(oldIndex), txtType, false);
        } else if ("update".equals(even)) {
            if (oldIndex == null || newIndex == null) {
                //TODO : ERR
            }
            conditionTxtListCompareResult.addUpdateNodeToList(newConditionsTxt.get(newIndex), "new_" + txtType);
            conditionTxtListCompareResult.addUpdateNodeToList(oldConditionsTxt.get(oldIndex), txtType);
        }
    }

    /**
     * 比较,规则中的判断条件的List,或ResultList,或路径中的判断List
     * ---- 先比较 id
     * ---- 再比较每个id的个数的不同
     * ---- 再比较每个id中的判断条件或结果条件
     * type        类型 , RuleComparResult中的几个常量
     */
    public void comparConditionList() {

        //转化为Map ----- 先转化为List 然后遍历List ,放到 Map中, key为uid ,value为 List中的值
        Map<String, List<JSONObject>> oldConditions = paseConditionsMapByKey(oldConditionListJSONString, "conditionId");
        Map<String, List<JSONObject>> newConditions = paseConditionsMapByKey(newConditionListJSONString, "conditionId");


        // 比较OldMap和newMap中的key
        if (ModelComparUtil.isEmpty(oldConditions) && ModelComparUtil.isEmpty(newConditions)) {
            return;
        }
        if (ModelComparUtil.isEmpty(oldConditions)) {
            //如果旧的模型中的规则为空，那么新的模型中的所有的规则都是  新加的  create
//            conditionListCompareResult.addOrDeleteAllListMapToList(oldConditions, newConditions, newConditions.keySet(), type, true);
            for (String key : newConditions.keySet()) {
                List<JSONObject> jsonObjects = newConditions.get(key);
                for (JSONObject jsonObject : jsonObjects) {
                    conditionListCompareResult.addOrDeleteOneInfoToList(jsonObject, type, true);
                    insertTxtDifferentToRestlt(null, jsonObject.getInteger("index"), "add");
                }
            }
        } else if (ModelComparUtil.isEmpty(newConditions)) {
            // 如果新的模型中的规则为空,那么旧的模型中的所有的规则都是被  删除的  delete
//            conditionListCompareResult.addOrDeleteAllListMapToList(oldConditions, newConditions, oldConditions.keySet(), type, false);
            for (String key : oldConditions.keySet()) {
                List<JSONObject> jsonObjects = oldConditions.get(key);
                for (JSONObject jsonObject : jsonObjects) {
                    conditionListCompareResult.addOrDeleteOneInfoToList(jsonObject, type, false);
                    insertTxtDifferentToRestlt(jsonObject.getInteger("index"), null, "delete");
                }
            }

        } else {
            // 两个都不为空 则比较
            //      -----  比较,先通过 id,来判断,有没有增加或删除的id(id为kpiId或varId)
            Set<String> oldConditionsKeySet = oldConditions.keySet();
            Set<String> newConditionsKeySet = newConditions.keySet();

            Set<String> addKeySet = ModelComparUtil.differenceSet(newConditionsKeySet, oldConditionsKeySet); // 新的减去旧的 --- 都是add
            Set<String> deleteKeySet = ModelComparUtil.differenceSet(oldConditionsKeySet, newConditionsKeySet); // 旧的减去新的 --- 都是delete

//            conditionListCompareResult.addOrDeleteAllListMapToList(oldConditions, newConditions, addKeySet, type, true);  // add的规则路径永远来自于新的模型
            for (String key : addKeySet) {
                List<JSONObject> jsonObjects = newConditions.get(key);
                for (JSONObject jsonObject : jsonObjects) {
                    conditionListCompareResult.addOrDeleteOneInfoToList(jsonObject, type, true);
                    insertTxtDifferentToRestlt(null, jsonObject.getInteger("index"), "add");
                }
            }
//            conditionListCompareResult.addOrDeleteAllListMapToList(oldConditions, newConditions, deleteKeySet, type, false); // delete的规则路径永远来自于旧的模型
            for (String key : deleteKeySet) {
                List<JSONObject> jsonObjects = oldConditions.get(key);
                for (JSONObject jsonObject : jsonObjects) {
                    conditionListCompareResult.addOrDeleteOneInfoToList(jsonObject, type, false);
                    insertTxtDifferentToRestlt(jsonObject.getInteger("index"), null, "delete");
                }
            }

            //      -----   比较id一样的
            Set<String> updateKeySet = ModelComparUtil.intersectionSet(newConditionsKeySet, oldConditionsKeySet); // 交集 新旧模型里面都有,则判断有没有更新

            // 处理 更新的规则
            for (String key : updateKeySet) {
                List<JSONObject> oldConditionList = oldConditions.get(key);
                List<JSONObject> newConditionList = newConditions.get(key);

                comparConditionList(key, oldConditionList, newConditionList);

            }
        }
    }

    private void comparConditionList(String key, List<JSONObject> oldConditionList, List<JSONObject> newConditionList) {
        if (oldConditionList == null && newConditionList == null) {
            return;
        }
        if (oldConditionList == null || oldConditionList.isEmpty()) {
            //都为add
//            Map<String, List<JSONObject>> tmpMap = new HashMap<>();
//            tmpMap.add(key, newConditionList);
//            conditionListCompareResult.addOrDeleteAllListMapToList(null, tmpMap, tmpMap.keySet(), type, true);  // add的规则路径永远来自于新的模型
            for (JSONObject jsonObject : newConditionList) {
                conditionListCompareResult.addOrDeleteOneInfoToList(jsonObject, type, true);
                insertTxtDifferentToRestlt(null, jsonObject.getInteger("index"), "add");
            }
        } else if (newConditionList == null || newConditionList.isEmpty()) {
//            Map<String, List<JSONObject>> tmpMap = new HashMap<>();
//            tmpMap.add(key, oldConditionList);
//            conditionListCompareResult.addOrDeleteAllListMapToList(tmpMap, null, tmpMap.keySet(), type, false);  // add的规则路径永远来自于新的模型
            for (JSONObject jsonObject : oldConditionList) {
                conditionListCompareResult.addOrDeleteOneInfoToList(jsonObject, type, false);
                insertTxtDifferentToRestlt(jsonObject.getInteger("index"), null, "delete");
            }
        } else {
            int oldConditionSize = oldConditionList.size();
            int newConditionSize = newConditionList.size();
            int size = oldConditionSize < newConditionSize ? oldConditionSize : newConditionSize;
            for (int i = 0; i < size; i++) { // 比较可以比较的
                //TODo : 比较
                comparCondition(key, oldConditionList.get(i), newConditionList.get(i));
            }

            if (newConditionSize > size) {
                for (int i = size - 1; i < size; i++) {
                    JSONObject jsonObject = newConditionList.get(i);
                    conditionListCompareResult.addOrDeleteOneInfoToList(jsonObject, type, true);
                    insertTxtDifferentToRestlt(null, jsonObject.getInteger("index"), "add");
                }
            }

            if (oldConditionSize > size) {
                for (int i = size - 1; i < size; i++) {
                    JSONObject jsonObject = oldConditionList.get(i);
                    conditionListCompareResult.addOrDeleteOneInfoToList(jsonObject, type, false);
                    insertTxtDifferentToRestlt(jsonObject.getInteger("index"), null, "delete");

                }
            }

        }
    }

    private void comparCondition(String key, JSONObject oldCondition, JSONObject newCondition) {

        JSONObject oldConditionDifferent = new JSONObject();
        ModelComparUtil.comparJsonValueDifferent(oldCondition, newCondition, "kpiId", oldConditionDifferent);
        ModelComparUtil.comparJsonValueDifferent(oldCondition, newCondition, "varId", oldConditionDifferent);
        ModelComparUtil.comparJsonValueDifferent(oldCondition, newCondition, "opt", oldConditionDifferent);
        ModelComparUtil.comparJsonValueDifferent(oldCondition, newCondition, "valueType", oldConditionDifferent);
        ModelComparUtil.comparJsonValueDifferent(oldCondition, newCondition, "value", oldConditionDifferent);
        ModelComparUtil.comparJsonValueDifferent(oldCondition, newCondition, "references", oldConditionDifferent);
        if (!oldConditionDifferent.isEmpty()) {
            insertTxtDifferentToRestlt(oldCondition.getInteger("index"), newCondition.getInteger("index"), "update");
            oldConditionDifferent.put("conditionId", key);
            conditionListCompareResult.addUpdateNodeToList(oldConditionDifferent, type);
        }
    }


    /**
     * 将List<Map<String,Object>> 转化为 Map<String,List<Map<String,Object>>>
     * results 的 key为 List里的Map中的 keyProperty 字段的值
     *
     * @param conditionsJSONArrayString List<Map<String,Object>> 的JSON格式
     */
    private Map<String, List<JSONObject>> paseConditionsMapByKey(String conditionsJSONArrayString, String keyProperty) {
        JSONArray conditionsAllList = JSONArray.parseArray(conditionsJSONArrayString);
        if (conditionsAllList == null || conditionsAllList.isEmpty()) {
            return new HashMap<>(1);
        }
        Map<String, List<JSONObject>> result = new HashMap<>();
        for (int i = 0; i < conditionsAllList.size(); i++) {
            Object conditionObject = conditionsAllList.get(i);
            JSONObject condition = paseCondition(ModelComparUtil.paseObjectToString(conditionObject));
            if (condition != null && !condition.isEmpty()) {
                condition.put("index", i);
                String id = condition.getString(keyProperty);
                if (!StringUtils.isBlank(id)) {
                    List<JSONObject> conditions = result.get(id);
                    if (conditions == null) {
                        conditions = new ArrayList<>();
                    }
                    conditions.add(condition);
                    result.put(id, conditions);
                }
            }
        }
        return result;
    }

    public static JSONObject paseCondition(String conditionJsonString) {
        //TODO : 加 下标 , 在原数组中的下标,
        if (StringUtils.isBlank(conditionJsonString)) {
            return new JSONObject(1);
        }
//        conditionJsonString = conditionJsonString.replaceAll("\\\\", "");
        JSONObject condition = JSONObject.parseObject(conditionJsonString);
        if (condition == null || condition.isEmpty()) {
            return new JSONObject(1);
        }
        String kpiId = condition.getString("kpiId");
        String varId = condition.getString("varId");
        String id = null;
        if (!StringUtils.isBlank(kpiId)) {
            id = kpiId;
        } else if (!StringUtils.isBlank(varId)) {
            id = varId;
        }
        if (StringUtils.isBlank(id)) {
            return new JSONObject(1);
        }
        condition.put("conditionId", id);
        return condition;
    }
}
