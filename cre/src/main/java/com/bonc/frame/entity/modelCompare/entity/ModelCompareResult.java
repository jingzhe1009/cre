package com.bonc.frame.entity.modelCompare.entity;

import com.alibaba.fastjson.JSONObject;
import com.bonc.frame.entity.rule.RuleDetailHeader;
import com.bonc.frame.entity.rule.RuleDetailWithBLOBs;
import com.bonc.frame.util.JsonUtils;
import com.bonc.frame.util.ModelComparUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * 模型内容中节点的的比较结果
 */
public class ModelCompareResult {

    public static final String RECT_Type = "RECT";
    public static final String PATH_Type = "PATH";

    /**
     * 模型操作类型Type
     */
    private String operateType;

    private RuleDetailWithBLOBs oldRuleDetailWithBLOBs;
    private RuleDetailWithBLOBs newRuleDetailWithBLOBs;
    private RuleDetailWithBLOBs fromRule;

    private Map<String, Map<String, Object>> modelContentCompareResult;
    private Map<String, Object> modelCompareResult;

    /**
     * map<event,Map<type,different>> ---- map<add,Map<rect,list[]>>
     */
    private Map<String, Map<String, List<Object>>> different = new HashMap<>(3);
    private Map<String, Map<String, Object>> differentBase = new HashMap<>(3);

    private static final String add_EVEN = "add_EVEN";
    private static final String delete_EVEN = "delete_EVEN";
    private static final String update_EVEN = "update_EVEN";

    public Map<String, Object> getModelContentCompareResult() {

        modelCompareResult = new HashMap<>();
        modelContentCompareResult = new HashMap<>();
        if (oldRuleDetailWithBLOBs == null) {
            modelCompareResult.put("ADD", modelContentCompareResult);
        } else if (newRuleDetailWithBLOBs == null) {
            modelCompareResult.put("DELETE", modelContentCompareResult);
        } else {
            modelCompareResult.put("UPDATE", modelContentCompareResult);
        }
        //比较基本信息
        modelContentCompareResult.putAll(paseBaseAndVersionDifferent());
        Map<String, Object> rectAndPathDifferent = paseCONTENTDifferent();
        if (rectAndPathDifferent != null && !rectAndPathDifferent.isEmpty()) {
            modelContentCompareResult.put("CONTENT", rectAndPathDifferent);
        }

        return modelCompareResult;
    }

    private Map<String, Object> paseEVEN(String even) {
        Map<String, Object> map = new HashMap<>();
        List<Object> rectList = getListFromDifferentMap(RECT_Type, even);
        if (rectList != null && !rectList.isEmpty()) {
            map.put(RECT_Type, rectList);
        }
        List<Object> pathList = getListFromDifferentMap(PATH_Type, even);
        if (pathList != null && !pathList.isEmpty()) {
            map.put(PATH_Type, pathList);
        }
        return map;
    }

    public boolean isEmptyDifferent() {
        boolean fl = true;
        if (different == null || different.isEmpty()) {
            return fl;
        }
        for (String type : different.keySet()) {
            Map<String, List<Object>> typeDifferent = different.get(type);
            if (typeDifferent != null && !typeDifferent.isEmpty()) {
                for (String key : different.keySet()) {
                    List<Object> value = typeDifferent.get(key);
                    if (value != null && !value.isEmpty()) {
                        fl = false;
                    }
                }
            }
        }
        return fl;
    }


    public void addOrDeleteAllInfoToList(JSONObject datas, Set<String> addOrDeleteSet, String type, boolean isAdd) {
        if (datas != null && !datas.isEmpty()) {
            for (String key : addOrDeleteSet) {
                String value = datas.getString(key);
                if (value != null) {
                    JSONObject data = JSONObject.parseObject(value);
                    if (RECT_Type.equals(type)) {
                        data.put("rectId", key);
                    } else {
                        data.put("pathId", key);
                    }
                    addOrDeleteOneInfoToList(data, type, isAdd);
                }
            }
        }
    }

    public void addOrDeleteOneInfoToList(JSONObject data, String type, boolean isAdd) {
        if (data != null && !data.isEmpty()) {
            data.remove("text");
            JSONObject props = data.getJSONObject("props");
            for (String key : props.keySet()) {
                data.put(key, props.get(key));
            }
            data.remove("props");
            if (RECT_Type.equals(type)) {
                data.remove("attr");
                if (isAdd) {
                    getListFromDifferentMap(type, add_EVEN).add(data);
                } else {
                    getListFromDifferentMap(type, delete_EVEN).add(data);
                }
            } else {
                data.remove("dots");
                if (isAdd) {
                    getListFromDifferentMap(type, add_EVEN).add(data);
                } else {
                    getListFromDifferentMap(type, delete_EVEN).add(data);
                }
            }
        }
    }

    public void addUpdateNodeToList(JSONObject oldPropsDifferentJson, String type) {
        if ((oldPropsDifferentJson == null || oldPropsDifferentJson.isEmpty())) {
            return;
        }
        getListFromDifferentMap(type, update_EVEN).add(oldPropsDifferentJson);
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

    public Map<String, Map<String, Object>> paseBaseAndVersionDifferent() {
        Map<String, Object> baseMap = new HashMap<>();
        Map<String, Object> fromRuleMap = new HashMap<>();
        Map<String, Object> versionMap = new HashMap<>();
        String ruleId = null;
        String new_ruleId = null;
        String version = null;
        String new_version = null;
        Map<String, Object> oldRuleHeaderPropertyMap = null;
        Map<String, Object> newRuleHeaderPropertyMap = null;

//String s = com.bonc.frame.util.JsonUtils.toJSONString(ruleDetailWithBLOBs);
//        Map<String,Object> ruleDetailWithBLOBsMap = JSONObject.parseObject(s);

        // null  null
        if (oldRuleDetailWithBLOBs == null && newRuleDetailWithBLOBs == null) {
            return differentBase;
        }

        if (oldRuleDetailWithBLOBs != null) {
            // base 模型基础信息
            RuleDetailHeader oldRuleDetailHeader = new RuleDetailHeader();
            org.springframework.beans.BeanUtils.copyProperties(oldRuleDetailWithBLOBs, oldRuleDetailHeader);
            oldRuleHeaderPropertyMap = JSONObject.parseObject(JsonUtils.toJSONString(oldRuleDetailHeader));

            // version 版本
            String oldRuleId = oldRuleDetailWithBLOBs.getRuleId();
            ruleId = oldRuleId;
            String oldVersion = oldRuleDetailWithBLOBs.getVersion();
            version = oldVersion;
            String oldVersionDesc = oldRuleDetailWithBLOBs.getVersionDesc();
            String oldRuleStatus = oldRuleDetailWithBLOBs.getRuleStatus();


            // 不null   null
            if (newRuleDetailWithBLOBs == null) {
                for (String ruleHeaderProperty : oldRuleHeaderPropertyMap.keySet()) {
                    putToMap(baseMap, ruleHeaderProperty, oldRuleHeaderPropertyMap.get(ruleHeaderProperty));
                }

                putToMap(versionMap, "ruleId", oldRuleId);
                putToMap(versionMap, "version", oldVersion);
                putToMap(versionMap, "versionDesc", oldVersionDesc);
                putToMap(versionMap, "ruleStatus", oldRuleStatus);

            } else {
                // 不null  不null
                RuleDetailHeader newRuleDetailHeader = new RuleDetailHeader();
                org.springframework.beans.BeanUtils.copyProperties(newRuleDetailWithBLOBs, newRuleDetailHeader);
                newRuleHeaderPropertyMap = JSONObject.parseObject(JsonUtils.toJSONString(newRuleDetailHeader));


                String newRuleId = newRuleDetailWithBLOBs.getRuleId();
                new_ruleId = newRuleId;
                String newVersion = newRuleDetailWithBLOBs.getVersion();
                new_version = newVersion;
                String newVersionDesc = newRuleDetailWithBLOBs.getVersionDesc();
                String newRuleStatus = newRuleDetailWithBLOBs.getRuleStatus();

                for (String ruleHeaderProperty : oldRuleHeaderPropertyMap.keySet()) {
                    compareStringToDifferentMap(baseMap, ruleHeaderProperty, newRuleHeaderPropertyMap.get(ruleHeaderProperty), oldRuleHeaderPropertyMap.get(ruleHeaderProperty));
                }


                compareStringToDifferentMap(versionMap, "ruleId", newRuleId, oldRuleId);
                compareStringToDifferentMap(versionMap, "version", newVersion, oldVersion);
                compareStringToDifferentMap(versionMap, "versionDesc", newVersionDesc, oldVersionDesc);
                compareStringToDifferentMap(versionMap, "ruleStatus", newRuleStatus, oldRuleStatus);
            }
        } else {
            if (newRuleDetailWithBLOBs != null) {
                //null 不null
                RuleDetailHeader newRuleDetailHeader = new RuleDetailHeader();
                org.springframework.beans.BeanUtils.copyProperties(newRuleDetailWithBLOBs, newRuleDetailHeader);
                newRuleHeaderPropertyMap = JSONObject.parseObject(JsonUtils.toJSONString(newRuleDetailHeader));


                String newRuleId = newRuleDetailWithBLOBs.getRuleId();
                new_ruleId = newRuleId;
                String newVersion = newRuleDetailWithBLOBs.getVersion();
                new_version = newVersion;
                String newVersionDesc = newRuleDetailWithBLOBs.getVersionDesc();
                String newRuleStatus = newRuleDetailWithBLOBs.getRuleStatus();

                for (String ruleHeaderProperty : newRuleHeaderPropertyMap.keySet()) {
                    putToMap(baseMap, ruleHeaderProperty, newRuleHeaderPropertyMap.get(ruleHeaderProperty));
                }

                putToMap(versionMap, "ruleId", newRuleId);
                putToMap(versionMap, "version", newVersion);
                putToMap(versionMap, "versionDesc", newVersionDesc);
                putToMap(versionMap, "ruleStatus", newRuleStatus);
            }
        }

        if (baseMap != null && !baseMap.isEmpty()) {
            differentBase.put("BASE", baseMap);
        }

        if (!StringUtils.isBlank(ruleId) || !StringUtils.isBlank(new_ruleId)) {
            if (StringUtils.isBlank(ruleId)) {
                //操作之前的Id为null
                versionMap.put("ruleId", new_ruleId);
            }
            if (StringUtils.isBlank(new_ruleId)) {
                //操作之后的Id为null
                versionMap.put("ruleId", ruleId);
            }
            if (!StringUtils.isBlank(ruleId) && !StringUtils.isBlank(new_ruleId)) {
                if (!ModelComparUtil.isEqualsString(ruleId, new_ruleId)) {
                    // 两个都不为null, 并且两个不相等
                    versionMap.put("ruleId", ruleId);
                    versionMap.put("new_ruleId", new_ruleId);
                } else {
                    // 两个都不为null ,并且两个相等
                    versionMap.put("ruleId", ruleId);
                }
            }
        }


        if (versionMap != null && !versionMap.isEmpty()) {
            differentBase.put("VERSION", versionMap);
        }
        return differentBase;
    }

    public Map<String, Object> paseCONTENTDifferent() {
        Map<String, Object> contentCompare = new HashMap<>();
        Map<String, Object> add = paseEVEN(add_EVEN);
        if (add != null && !add.isEmpty()) {
            contentCompare.put("ADD", add);
        }
        Map<String, Object> update = paseEVEN(update_EVEN);
        if (update != null && !update.isEmpty()) {
            contentCompare.put("UPDATE", update);
        }
        Map<String, Object> delete = paseEVEN(delete_EVEN);
        if (delete != null && !delete.isEmpty()) {
            contentCompare.put("DELETE", delete);
        }
        return contentCompare;
    }

    /**
     * key,value
     * 如果value为空,不进行任何操作
     * 如果value不为空,放到map中
     *
     * @param map
     * @param key
     * @param value
     */
    public void putToMap(Map map, String key, Object value) {
        if (!("updateDate".equals(key) || "createDate".equals(key) || "updatePerson".equals(key) || "createPerson".equals(key) ||
                "isHeader".equals(key) || "modelGroupName".equals(key) ||
                "platformCreateUserJobNumber".equals(key) || "platformUpdateUserJobNumber".equals(key))) {

            if (value != null && StringUtils.isNotBlank(value.toString())) {
                map.put(key, value);
            }
        }
        int i = 1;
    }

    /**
     * 比较两个字符串
     * 如果不相等,则放到map中,
     * key,oldvalue
     * "new_"+key,newvalue
     */
    public void compareStringToDifferentMap(Map<String, Object> different, String key, Object newValue, Object oldValue) {
        if (!("updateDate".equals(key) || "createDate".equals(key) || "updatePerson".equals(key) || "createPerson".equals(key) ||
                "isHeader".equals(key) || "modelGroupName".equals(key) ||
                "platformCreateUserJobNumber".equals(key) || "platformUpdateUserJobNumber".equals(key))) {

            if (!ModelComparUtil.isEqualsString(oldValue, newValue)) {
                different.put("new_" + key, newValue);
                different.put(key, oldValue);
            }
        }
    }


    public RuleDetailWithBLOBs getFromRule() {
        return fromRule;
    }

    public void setFromRule(RuleDetailWithBLOBs fromRule) {
        this.fromRule = fromRule;
    }

    public RuleDetailWithBLOBs getOldRuleDetailWithBLOBs() {
        return oldRuleDetailWithBLOBs;
    }

    public void setOldRuleDetailWithBLOBs(RuleDetailWithBLOBs oldRuleDetailWithBLOBs) {
        this.oldRuleDetailWithBLOBs = oldRuleDetailWithBLOBs;
    }

    public RuleDetailWithBLOBs getNewRuleDetailWithBLOBs() {
        return newRuleDetailWithBLOBs;
    }

    public void setNewRuleDetailWithBLOBs(RuleDetailWithBLOBs newRuleDetailWithBLOBs) {
        this.newRuleDetailWithBLOBs = newRuleDetailWithBLOBs;
    }


//    public Map<String, Map<String, Object>> paseBaseAndVersionDifferent() {
//        Map<String, Object> baseMap = new HashMap<>();
//        Map<String, Object> fromRuleMap = new HashMap<>();
//        Map<String, Object> versionMap = new HashMap<>();
//        String ruleId = null;
//        String new_ruleId = null;
//        String version = null;
//        String new_version = null;
//        Map<String, Object> oldRuleHeaderPropertyMap = null;
//        Map<String, Object> newRuleHeaderPropertyMap = null;
//
////String s = com.bonc.frame.util.JsonUtils.toJSONString(ruleDetailWithBLOBs);
////        Map<String,Object> ruleDetailWithBLOBsMap = JSONObject.parseObject(s);
//
//        // null  null
//        if (oldRuleDetailWithBLOBs == null && newRuleDetailWithBLOBs == null) {
//            return differentBase;
//        }
//
//        if (oldRuleDetailWithBLOBs != null) {
//            // base 模型基础信息
//            String oldRuleName = oldRuleDetailWithBLOBs.getRuleName();
//            String oldModelName = oldRuleDetailWithBLOBs.getModuleName();
//            String oldRuleDesc = oldRuleDetailWithBLOBs.getRuleDesc();
//            String oldRuleType = oldRuleDetailWithBLOBs.getRuleType();
//            String oldFolderId = oldRuleDetailWithBLOBs.getFolderId();
//            String oldIsPublic = oldRuleDetailWithBLOBs.getIsPublic();
//            String oldModelGroupId = oldRuleDetailWithBLOBs.getModelGroupId();
//            // version 版本
//            String oldRuleId = oldRuleDetailWithBLOBs.getRuleId();
//            ruleId = oldRuleId;
//            String oldVersion = oldRuleDetailWithBLOBs.getVersion();
//            version = oldVersion;
//            String oldVersionDesc = oldRuleDetailWithBLOBs.getVersionDesc();
//            String oldRuleStatus = oldRuleDetailWithBLOBs.getRuleStatus();
//
//            String s = JsonUtils.toJSONString(oldRuleDetailWithBLOBs);
//            oldRuleHeaderPropertyMap = JSONObject.parseObject(s);
//
//
//            // 不null   null
//            if (newRuleDetailWithBLOBs == null) {
//                putToMap(baseMap, "ruleName", oldRuleName);
//                putToMap(baseMap, "modelName", oldModelName);
//                putToMap(baseMap, "ruleDesc", oldRuleDesc);
//                putToMap(baseMap, "ruleType", oldRuleType);
//                putToMap(baseMap, "folderId", oldFolderId);
//                putToMap(baseMap, "isPublic", oldIsPublic);
//                putToMap(baseMap, "modelGroupId", oldModelGroupId);
//
//                putToMap(versionMap, "ruleId", oldRuleId);
//                putToMap(versionMap, "version", oldVersion);
//                putToMap(versionMap, "versionDesc", oldVersionDesc);
//                putToMap(versionMap, "ruleStatus", oldRuleStatus);
//
//            } else {
//                // 不null  不null
//                String newRuleName = newRuleDetailWithBLOBs.getRuleName();
//                String newModelName = newRuleDetailWithBLOBs.getModuleName();
//                String newRuleDesc = newRuleDetailWithBLOBs.getRuleDesc();
//                String newRuleType = newRuleDetailWithBLOBs.getRuleType();
//                String newFolderId = newRuleDetailWithBLOBs.getFolderId();
//                String newIsPublic = newRuleDetailWithBLOBs.getIsPublic();
//                String newModelGroupId = newRuleDetailWithBLOBs.getModelGroupId();
//
//                String newRuleId = newRuleDetailWithBLOBs.getRuleId();
//                new_ruleId = newRuleId;
//                String newVersion = newRuleDetailWithBLOBs.getVersion();
//                new_version = newVersion;
//                String newVersionDesc = newRuleDetailWithBLOBs.getVersionDesc();
//                String newRuleStatus = newRuleDetailWithBLOBs.getRuleStatus();
//
//
//                compareStringToDifferentMap(baseMap, "ruleName", newRuleName, oldRuleName);
//                compareStringToDifferentMap(baseMap, "modelName", newModelName, oldModelName);
//                compareStringToDifferentMap(baseMap, "ruleDesc", newRuleDesc, oldRuleDesc);
//                compareStringToDifferentMap(baseMap, "ruleType", newRuleType, oldRuleType);
//                compareStringToDifferentMap(baseMap, "folderId", newFolderId, oldFolderId);
//                compareStringToDifferentMap(baseMap, "isPublic", newIsPublic, oldIsPublic);
//                compareStringToDifferentMap(baseMap, "modelGroupId", newModelGroupId, oldModelGroupId);
//
//                compareStringToDifferentMap(versionMap, "ruleId", newRuleId, oldRuleId);
//                compareStringToDifferentMap(versionMap, "version", newVersion, oldVersion);
//                compareStringToDifferentMap(versionMap, "versionDesc", newVersionDesc, oldVersionDesc);
//                compareStringToDifferentMap(versionMap, "ruleStatus", newRuleStatus, oldRuleStatus);
//            }
//        } else {
//            if (newRuleDetailWithBLOBs != null) {
//                //null 不null
//                String newRuleName = newRuleDetailWithBLOBs.getRuleName();
//                String modelName = newRuleDetailWithBLOBs.getModuleName();
//                String newRuleDesc = newRuleDetailWithBLOBs.getRuleDesc();
//                String newRuleType = newRuleDetailWithBLOBs.getRuleType();
//                String newFolderId = newRuleDetailWithBLOBs.getFolderId();
//                String newIsPublic = newRuleDetailWithBLOBs.getIsPublic();
//                String newModelGroupId = newRuleDetailWithBLOBs.getModelGroupId();
//
//                String newRuleId = newRuleDetailWithBLOBs.getRuleId();
//                new_ruleId = newRuleId;
//                String newVersion = newRuleDetailWithBLOBs.getVersion();
//                new_version = newVersion;
//                String newVersionDesc = newRuleDetailWithBLOBs.getVersionDesc();
//                String newRuleStatus = newRuleDetailWithBLOBs.getRuleStatus();
//
//                putToMap(baseMap, "ruleName", newRuleName);
//                putToMap(baseMap, "modelName", modelName);
//                putToMap(baseMap, "ruleDesc", newRuleDesc);
//                putToMap(baseMap, "ruleType", newRuleType);
//                putToMap(baseMap, "folderId", newFolderId);
//                putToMap(baseMap, "isPublic", newIsPublic);
//                putToMap(baseMap, "modelGroupId", newModelGroupId);
//
//                putToMap(versionMap, "ruleId", newRuleId);
//                putToMap(versionMap, "version", newVersion);
//                putToMap(versionMap, "versionDesc", newVersionDesc);
//                putToMap(versionMap, "ruleStatus", newRuleStatus);
//            }
//        }
//
//        if (baseMap != null && !baseMap.isEmpty()) {
//            differentBase.put("BASE", baseMap);
//        }
//
//        if (!StringUtils.isBlank(ruleId) || !StringUtils.isBlank(new_ruleId)) {
//            if (StringUtils.isBlank(ruleId)) {
//                //操作之前的Id为null
//                versionMap.put("ruleId", new_ruleId);
//            }
//            if (StringUtils.isBlank(new_ruleId)) {
//                //操作之后的Id为null
//                versionMap.put("ruleId", ruleId);
//            }
//            if (!StringUtils.isBlank(ruleId) && !StringUtils.isBlank(new_ruleId)) {
//                if (!ModelComparUtil.isEqualsString(ruleId, new_ruleId)) {
//                    // 两个都不为null, 并且两个不相等
//                    versionMap.put("ruleId", ruleId);
//                    versionMap.put("new_ruleId", new_ruleId);
//                } else {
//                    // 两个都不为null ,并且两个相等
//                    versionMap.put("ruleId", ruleId);
//                }
//            }
//        }
//
//
//        if (versionMap != null && !versionMap.isEmpty()) {
//            differentBase.put("VERSION", versionMap);
//        }
//        return differentBase;
//    }

}
