package com.bonc.frame.entity.modelCompare.entity;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static com.bonc.frame.util.ModelComparUtil.getMapValue;

public class ConditionListCompareResult {
    public static final String LHS_TYPE = "condition";
    public static final String LHSTxt_TYPE = "LHSTxt";
    public static final String NEW_LHSTxt_TYPE = "new_LHSTxt";
    public static final String RHS_TYPE = "RHS";
    public static final String RHSTxt_TYPE = "RHSTxt";
    public static final String NEW_RHSTxt_TYPE = "new_RHSTxt";
    public static final String PATH_CONDITION_TYPE = "condition";
    public static final String PATH_CONDITION_TXT_TYPE = "pathCdtTxt";
    public static final String NEW_PATH_CONDITION_TXT_TYPE = "new_" + PATH_CONDITION_TXT_TYPE;

    List<String> types = new ArrayList<>();
    private Map<String, Object> result;

    /**
     * map<event,Map<type>>
     */
    private Map<String, Map<String, List<Object>>> different = new HashMap<>(8);

    private static final String add_EVEN = "add";
    private static final String update_EVEN = "update";
    private static final String delete_EVEN = "delete";
//    private final String updateNewCondition = "updateNewCondition";

    public static JSONObject paseCondition(String conditionJsonString) {
        //TODO : 加 下标 , 在原数组中的下标,
        if (StringUtils.isBlank(conditionJsonString)) {
            return new JSONObject();
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


    public Map<String, Object> getResult() {
        result = new HashMap<>();
        Map<String, Object> add = paseEVEN(add_EVEN);
        if (add != null && !add.isEmpty()) {
            result.put("ADD", add);
        }
        Map<String, Object> update = paseEVEN(update_EVEN);
        if (update != null && !update.isEmpty()) {
            result.put("UPDATE", update);
        }
        Map<String, Object> delete = paseEVEN(delete_EVEN);
        if (delete != null && !delete.isEmpty()) {
            result.put("DELETE", delete);
        }
        return result;
    }

    private Map<String, Object> paseEVEN(String even) {
        Map<String, Object> map = new HashMap<>();
        types.add(LHS_TYPE);
        types.add(LHSTxt_TYPE);
        types.add(NEW_LHSTxt_TYPE);
        types.add(RHS_TYPE);
        types.add(RHSTxt_TYPE);
        types.add(NEW_RHSTxt_TYPE);
        types.add(PATH_CONDITION_TYPE);
        types.add(PATH_CONDITION_TXT_TYPE);
        types.add(NEW_PATH_CONDITION_TXT_TYPE);
        for (String type : types) {
            putTypeResultToMap(map, even, type);
        }
        return map;
    }

    private void putTypeResultToMap(Map<String, Object> map, String even, String type) {
        List<Object> rectList = getListFromDifferentMap(type, even);
        if (rectList != null && !rectList.isEmpty()) {
            map.put(type, rectList);
        }
    }


    public void addOrDeleteAllListMapToList(Map<String, List<JSONObject>> dataListMap, Map<String, List<JSONObject>> newDataListMap, Set<String> addOrDeleteSet, String type, boolean isAdd) {
        if (dataListMap == null || dataListMap.isEmpty() || addOrDeleteSet == null || addOrDeleteSet.isEmpty()) {
            return;
        }
        if (isAdd) {
            addOrDeleteAllListMapToList(newDataListMap, addOrDeleteSet, type, isAdd);
        } else {
            addOrDeleteAllListMapToList(dataListMap, addOrDeleteSet, type, isAdd);
        }
    }

    private void addOrDeleteAllListMapToList(Map<String, List<JSONObject>> dataListMap, Set<String> addOrDeleteSet, String type, boolean isAdd) {
        for (String key : addOrDeleteSet) {
            List<JSONObject> dataList = dataListMap.get(key);
            if (dataList != null && !dataList.isEmpty()) {
                for (JSONObject data : dataList) {
                    addOrDeleteOneInfoToList(data, type, isAdd);
                }
            }
        }
    }

    public void addOrDeleteAllInfoToList(Map<String, Object> oldDatas, Map<String, Object> newDatas, Set<String> addOrDeleteSet, String type, boolean isAdd) {
        if (isAdd) {
            addOrDeleteAllInfoToList(newDatas, addOrDeleteSet, type, isAdd);
        } else {
            addOrDeleteAllInfoToList(oldDatas, addOrDeleteSet, type, isAdd);
        }
    }


    private void addOrDeleteAllInfoToList(Map<String, Object> datas, Set<String> addOrDeleteSet, String type, boolean isAdd) {
        if (datas != null && !datas.isEmpty()) {
            for (String key : addOrDeleteSet) {
                String value = getMapValue(datas, key);
                if (value != null) {
                    JSONObject data = JSONObject.parseObject(value);
                    addOrDeleteOneInfoToList(data, type, isAdd);
                }
            }
        }
    }

    public void addOrDeleteOneInfoToList(Object data, String type, boolean isAdd) {
        if (data != null) {
            if (isAdd) {
                getListFromDifferentMap(type, add_EVEN).add(data);
            } else {
                getListFromDifferentMap(type, delete_EVEN).add(data);
            }

        }
    }


    public void addUpdateNodeToList(Object oldPropsDifferentJson, String type) {
        if (oldPropsDifferentJson != null) {
            if (oldPropsDifferentJson instanceof Map) {
                if (!((Map) oldPropsDifferentJson).isEmpty()) {
                    getListFromDifferentMap(type, update_EVEN).add(oldPropsDifferentJson);
                }
            } else {
                getListFromDifferentMap(type, update_EVEN).add(oldPropsDifferentJson);
            }
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

    public boolean isNotEmptyDifferent() {
        boolean fl = false;
        if (different == null || different.isEmpty()) {
            return fl;
        }
        for (String type : different.keySet()) {
            Map<String, List<Object>> typeDifferent = different.get(type);
            if (typeDifferent != null && !typeDifferent.isEmpty()) {
                for (String key : typeDifferent.keySet()) {
                    List<Object> value = typeDifferent.get(key);
                    if (value != null && !value.isEmpty()) {
                        fl = true;
                    }
                }
            }
        }
        return fl;
    }

}
