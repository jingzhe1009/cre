package com.bonc.frame.entity.modelCompare.entity;

import com.alibaba.fastjson.JSONObject;

import java.util.*;

import static com.bonc.frame.util.ModelComparUtil.getMapValue;

/**
 * 规则集规则的比较结果
 */
public class ActionCompareResult extends BaseCompareResult{

    public static final String RULE_Type = "value";

    private Map<String, Object> result;

    /**
     * map<event,Map<type>>
     */
    private Map<String, Map<String, List<Object>>> different = new HashMap<>(8);

    private final String addRule = "add";
    private final String updateRule = "update";
    private final String deleteRule = "delete";
//    private final String updateNewRule = "updateNewRule";

    public Map<String, Object> getResult() {
        result = new HashMap<>();
        Map<String, Object> add = paseEVEN(addRule);
        if (add != null && !add.isEmpty()) {
            result.put("ADD", add);
        }
        Map<String, Object> update = paseEVEN(updateRule);
        if (update != null && !update.isEmpty()) {
            result.put("UPDATE", update);
        }
        Map<String, Object> delete = paseEVEN(deleteRule);
        if (delete != null && !delete.isEmpty()) {
            result.put("DELETE", delete);
        }
        return result;
    }

    private Map<String, Object> paseEVEN(String even) {
        Map<String, Object> map = new HashMap<>();
        List<Object> ruleList = getListFromDifferentMap(RULE_Type, even);
        if (ruleList != null && !ruleList.isEmpty()) {
            map.put(RULE_Type, ruleList);
        }
        return map;
    }


    public void addOrDeleteAllInfoToList(JSONObject datas, Set<String> addOrDeleteSet, String type, boolean isAdd) {
        if (datas != null && !datas.isEmpty() && addOrDeleteSet != null && !addOrDeleteSet.isEmpty()) {
            for (String key : addOrDeleteSet) {
                String value = getMapValue(datas, key);
                if (value != null) {
                    JSONObject data = JSONObject.parseObject(value);
                    addOrDeleteOneInfoToList(data, type, isAdd);
                }
            }
        }
    }

    public void addOrDeleteOneInfoToList(JSONObject data, String type, boolean isAdd) {
        if (data != null && !data.isEmpty()) {
            if (RULE_Type.equals(type)) {
                if (isAdd) {
                    getListFromDifferentMap(type, addRule).add(data);
                } else {
                    getListFromDifferentMap(type, deleteRule).add(data);
                }
            }
        }
    }


    public void addUpdateNodeToList(JSONObject oldPropsDifferentJson, String type) {
        if (oldPropsDifferentJson != null && !oldPropsDifferentJson.isEmpty()) {
            getListFromDifferentMap(type, updateRule).add(oldPropsDifferentJson);
        }
    }

    public List<Object> getListFromDifferentMap(String type, String event) {

        if (different == null) {
            different = new HashMap<>();
        }
        Map<String, List<Object>> stringListMap = different.get(event);
        if (stringListMap == null) {
            stringListMap = new HashMap<>();
            different.put(event, stringListMap);
        }
        List<Object> list = stringListMap.get(type);
        if (list == null) {
            list = new ArrayList<>();
            stringListMap.put(type, list);
        }
        return list;
    }

    public boolean isEmptyDifferent() {
        boolean fl = true;
        if (different == null || different.isEmpty()) {
            return fl;
        }
        for (String type : different.keySet()) {
            Map<String, List<Object>> typeDifferent = different.get(type);
            if (typeDifferent != null && !typeDifferent.isEmpty()) {
                for (String key : typeDifferent.keySet()) {
                    List<Object> value = typeDifferent.get(key);
                    if (value != null && !value.isEmpty()) {
                        fl = false;
                    }
                }
            }
        }
        return fl;
    }
}