package com.bonc.frame.service.modelCompare;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bonc.frame.entity.modelCompare.entity.ModelCompareConstant;
import com.bonc.frame.entity.modelCompare.entity.ModelCompareResult;
import com.bonc.frame.entity.modelCompare.entity.ModelCompareType;
import com.bonc.frame.entity.modelCompare.entity.ConditionListCompareResult;
import com.bonc.frame.util.ModelComparUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.bonc.frame.util.ModelComparUtil.differenceSet;
import static com.bonc.frame.util.ModelComparUtil.intersectionSet;
import static com.bonc.frame.util.ModelComparUtil.paseObjectToString;

@Service
public abstract class AbstractModelCompare implements ModelCompare {

    Log log = LogFactory.getLog(AbstractModelCompare.class);

    public boolean isSupport(String type) {
        return getSupport().getValue().equals(type);
    }

    @Override
    public ModelCompareType getSupport() {
        return ModelCompareType.ABS;
    }

    /**
     * 比较两个节点,将不同点放到result中
     * rect走这个方法 -- 只比较props
     * path重写这个方法,所以path走它的实现类,比较整个路径信息
     */
    @Override
    public void comparNodePropsJson(ModelCompareResult modelComparResult, JSONObject oldNodeJson, JSONObject newNodeJson, String nodeKey) {
        // 节点类型一定一样,并且两个都不可能为空
        JSONObject rectNodePropsDifferentJson = new JSONObject();
        String oldNodePropsString = ModelComparUtil.getMapValue(oldNodeJson, "props");
        String newNodePropsString = ModelComparUtil.getMapValue(newNodeJson, "props");


        if (!ModelComparUtil.isEqualsString(oldNodePropsString, newNodePropsString)) {

            JSONObject oldNodeProps = JSONObject.parseObject(oldNodePropsString);
            JSONObject newNodeProps = JSONObject.parseObject(newNodePropsString);

            ModelComparUtil.comparJsonValueDifferent(oldNodeProps, newNodeProps, "text", rectNodePropsDifferentJson);
            //比较其他属性;
            comparNodePropsOthers(oldNodeProps, newNodeProps, rectNodePropsDifferentJson);

            //比较test属性
            comparNodePropsTest(oldNodeJson, newNodeJson, rectNodePropsDifferentJson);
        }


        if (rectNodePropsDifferentJson != null && !rectNodePropsDifferentJson.isEmpty()) {
            rectNodePropsDifferentJson.put("rectId", nodeKey);

            String nodeType = ModelComparUtil.getMapValue(oldNodeJson, "type");
            if (nodeType == null) {
                nodeType = ModelComparUtil.getMapValue(newNodeJson, "type");
            }
            rectNodePropsDifferentJson.put("type", nodeType);

            modelComparResult.addUpdateNodeToList(rectNodePropsDifferentJson, ModelCompareResult.RECT_Type);
        }
    }


    public void comparNodePropsTest(JSONObject oldNodeJson, JSONObject newNodeJson, JSONObject rectNodePropsDifferentJson) {
        String textValue = null;
        String newTextValue = null;
        if (newNodeJson != null) {
            newTextValue = ModelComparUtil.getJsonStringValueString(newNodeJson.getString("props"), "text");
        }
        if (oldNodeJson != null) {
            textValue = ModelComparUtil.getJsonStringValueString(oldNodeJson.getString("props"), "text");
        }

        if (rectNodePropsDifferentJson != null && !rectNodePropsDifferentJson.isEmpty()) {
            if (!StringUtils.isBlank(textValue) || !StringUtils.isBlank(newTextValue)) {
                if (StringUtils.isBlank(textValue)) {
                    //操作之前的名称为null -- 一般不会存在
                    rectNodePropsDifferentJson.put("text", JSONObject.parse(newTextValue));
                }
                if (StringUtils.isBlank(newTextValue)) {
                    //操作之后的名称为null -- 一般不会存在
                    rectNodePropsDifferentJson.put("text", JSONObject.parse(textValue));
                }
                if (!StringUtils.isBlank(textValue) && !StringUtils.isBlank(newTextValue)) {
                    if (!ModelComparUtil.isEqualsString(textValue, newTextValue)) {
                        // 两个都不为null, 并且两个不相等 --- 也就是说,改名了
                        rectNodePropsDifferentJson.put("text", JSONObject.parse(textValue));
                        rectNodePropsDifferentJson.put("new_text", JSONObject.parse(newTextValue));
                    } else {
                        // 两个都不为null ,并且两个相等--- 也就是说,没改名
                        rectNodePropsDifferentJson.put("text", JSONObject.parse(textValue));
                    }
                }
            }
        } else {
            if (!StringUtils.isBlank(textValue) && !StringUtils.isBlank(newTextValue) && !ModelComparUtil.isEqualsString(textValue, newTextValue)) {
                // 两个都不为null, 并且两个不相等 --- 也就是说,改名了
                if (rectNodePropsDifferentJson == null) {
                    rectNodePropsDifferentJson = new JSONObject();
                }
                rectNodePropsDifferentJson.put("text", JSONObject.parse(textValue));
                rectNodePropsDifferentJson.put("new_text", JSONObject.parse(newTextValue));
            }

        }
    }

    public void comparNodePropsOthers(JSONObject oldNodeProps, JSONObject newNodeProps, JSONObject oldPropsDifferentJson) {
        // 没有props,不比较
    }


    // --------------- -------------- ---------- ------------- --------- ------- ------ ---------- -------------
    // --------------- 比较,规则中的判断条件的List,或ResultList,或路径中的判断List ------ ---------- -------------
    // --------------- -------------- ---------- ------------- --------- ------- ------ ---------- -------------

    /**
     * 比较,规则中的判断条件的List,或ResultList,或路径中的判断List
     * ---- 先比较 id
     * ---- 再比较每个id的个数的不同
     * ---- 再比较每个id中的判断条件或结果条件
     *
     * @param oldConditionListJSONString
     * @param newConditionListJSONString
     * @param type                       类型 , RuleComparResult中的几个常量
     */
    public void comparConditionList(ConditionListCompareResult ruleComparResult, String oldConditionListJSONString, String newConditionListJSONString, String type) {

        //转化为Map ----- 先转化为List 然后遍历List ,放到 Map中, key为uid ,value为 List中的值
        Map<String, List<JSONObject>> oldConditions = paseConditionsMapByKey(oldConditionListJSONString, "conditionId");
        Map<String, List<JSONObject>> newConditions = paseConditionsMapByKey(newConditionListJSONString, "conditionId");

        // 比较OldMap和newMap中的key
        if (ModelComparUtil.isEmpty(oldConditions) && ModelComparUtil.isEmpty(newConditions)) {
            return;
        }
        if (ModelComparUtil.isEmpty(oldConditions)) {
            //如果旧的模型中的规则为空，那么新的模型中的所有的规则都是  新加的  create
            ruleComparResult.addOrDeleteAllListMapToList(oldConditions, newConditions, newConditions.keySet(), type, true);
        } else if (ModelComparUtil.isEmpty(newConditions)) {
            // 如果新的模型中的规则为空,那么旧的模型中的所有的规则都是被  删除的  delete
            ruleComparResult.addOrDeleteAllListMapToList(oldConditions, newConditions, oldConditions.keySet(), type, false);

        } else {
            // 两个都不为空 则比较
            //      -----  比较,先通过 id,来判断,有没有增加或删除的id(id为kpiId或varId)
            Set<String> oldConditionsKeySet = oldConditions.keySet();
            Set<String> newConditionsKeySet = newConditions.keySet();

            Set<String> addKeySet = differenceSet(newConditionsKeySet, oldConditionsKeySet); // 新的减去旧的 --- 都是add
            Set<String> deleteKeySet = differenceSet(oldConditionsKeySet, newConditionsKeySet); // 旧的减去新的 --- 都是delete

            ruleComparResult.addOrDeleteAllListMapToList(oldConditions, newConditions, addKeySet, type, true);  // add的规则路径永远来自于新的模型
            ruleComparResult.addOrDeleteAllListMapToList(oldConditions, newConditions, deleteKeySet, type, false); // delete的规则路径永远来自于旧的模型

            //      -----   比较id一样的
            Set<String> updateKeySet = intersectionSet(newConditionsKeySet, oldConditionsKeySet); // 交集 新旧模型里面都有,则判断有没有更新

            // 处理 更新的规则
            for (String key : updateKeySet) {
                List<JSONObject> oldConditionList = oldConditions.get(key);
                List<JSONObject> newConditionList = newConditions.get(key);

                comparConditionList(ruleComparResult, type, key, oldConditionList, newConditionList);

            }
        }

        return;
    }

    private void comparConditionList(ConditionListCompareResult ruleComparResult, String type, String key, List<JSONObject> oldConditionList, List<JSONObject> newConditionList) {
        if (oldConditionList == null && newConditionList == null) {
            return;
        }
        if (oldConditionList == null || oldConditionList.isEmpty()) {
            //都为add
            Map<String, List<JSONObject>> tmpMap = new HashMap<>();
            tmpMap.put(key, newConditionList);
            ruleComparResult.addOrDeleteAllListMapToList(null, tmpMap, tmpMap.keySet(), type, true);  // add的规则路径永远来自于新的模型
        } else if (newConditionList == null || newConditionList.isEmpty()) {
            Map<String, List<JSONObject>> tmpMap = new HashMap<>();
            tmpMap.put(key, oldConditionList);
            ruleComparResult.addOrDeleteAllListMapToList(tmpMap, null, tmpMap.keySet(), type, false);  // add的规则路径永远来自于新的模型
        } else {
            int oldConditionSize = oldConditionList.size();
            int newConditionSize = newConditionList.size();
            int size = oldConditionSize < newConditionSize ? oldConditionSize : newConditionSize;
            for (int i = 0; i < size; i++) { // 比较可以比较的
                //TODo : 比较
                comparCondition(ruleComparResult, type, key, oldConditionList.get(i), newConditionList.get(i));
            }

            if (oldConditionSize > size) {
                for (int i = size - 1; i < size; i++) {
                    ruleComparResult.addOrDeleteOneInfoToList(oldConditionList.get(i), type, false);
                }
            }
            if (newConditionSize > size) {
                for (int i = size - 1; i < size; i++) {
                    ruleComparResult.addOrDeleteOneInfoToList(newConditionList.get(i), type, true);
                }
            }

        }
    }

    private void comparCondition(ConditionListCompareResult ruleComparResult, String type, String key, JSONObject oldCondition, JSONObject newCondition) {
        JSONObject oldConditionDifferent = new JSONObject();
        ModelComparUtil.comparJsonValueDifferent(oldCondition, newCondition, "kpiId", oldConditionDifferent);
        ModelComparUtil.comparJsonValueDifferent(oldCondition, newCondition, "varId", oldConditionDifferent);
        ModelComparUtil.comparJsonValueDifferent(oldCondition, newCondition, "opt", oldConditionDifferent);
        ModelComparUtil.comparJsonValueDifferent(oldCondition, newCondition, "valueType", oldConditionDifferent);
        ModelComparUtil.comparJsonValueDifferent(oldCondition, newCondition, "value", oldConditionDifferent);
        if (!oldConditionDifferent.isEmpty()) {
            oldConditionDifferent.put("conditionId", key);
        }
        ruleComparResult.addUpdateNodeToList(oldConditionDifferent, type);
    }


    /**
     * 将List<Map<String,Object>> 转化为 Map<String,List<Map<String,Object>>>
     * results 的 key为 List里的Map中的 keyProperty 字段的值
     *
     * @param conditionsJSONArrayString List<Map<String,Object>> 的JSON格式
     */
    public static Map<String, List<JSONObject>> paseConditionsMapByKey(String conditionsJSONArrayString, String keyProperty) {
        JSONArray conditionsAllList = JSONArray.parseArray(conditionsJSONArrayString);
        if (conditionsAllList == null || conditionsAllList.isEmpty()) {
            return new HashMap<>(1);
        }
        Map<String, List<JSONObject>> result = new HashMap<>();
        for (int i = 0; i < conditionsAllList.size(); i++) {
            Object conditionObject = conditionsAllList.get(i);
            JSONObject condition = ConditionListCompareResult.paseCondition(paseObjectToString(conditionObject));
            if (condition != null && !condition.isEmpty()) {
//                condition.add("index", i);
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



    public void putCompareResultToDifferent(Map<String, Object> differentJSONObject, Map<String, Object> compareResults) {
        Map<String, Object> addMapValueMap = getMapValueMap(compareResults, ModelCompareConstant.ADD);
        if (addMapValueMap != null && !addMapValueMap.isEmpty()) {
            getMapValueMap(differentJSONObject, ModelCompareConstant.ADD).putAll(addMapValueMap);
        }
        Map<String, Object> updateMapValueMap = getMapValueMap(compareResults, ModelCompareConstant.UPDATE);
        if (updateMapValueMap != null && !updateMapValueMap.isEmpty()) {
            getMapValueMap(differentJSONObject, ModelCompareConstant.UPDATE).putAll(updateMapValueMap);
        }
        Map<String, Object> deleteMapValueMap = getMapValueMap(compareResults, ModelCompareConstant.DELETE);
        if (deleteMapValueMap != null && !deleteMapValueMap.isEmpty()) {
            getMapValueMap(differentJSONObject, ModelCompareConstant.DELETE).putAll(deleteMapValueMap);
        }
//        getMapValueMap(differentJSONObject, ModelCompareConstant.UPDATE).putAll(getMapValueMap(compareResults, ModelCompareConstant.UPDATE));
//        getMapValueMap(differentJSONObject, ModelCompareConstant.DELETE).putAll(getMapValueMap(compareResults, ModelCompareConstant.DELETE));

    }

    private Map<String, Object> getMapValueMap(Map<String, Object> differentJSONObject, String type) {
        Object add = differentJSONObject.get(type);
        if (add == null) {
            add = new HashMap<>();
            differentJSONObject.put(type, add);

        } else {
            if (!(add instanceof Map)) {
                add = new HashMap<>();
                differentJSONObject.put(type, add);
            }
        }
        return (Map) add;
    }


}
