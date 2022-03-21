package com.bonc.frame.entity.model;

import com.bonc.frame.entity.variable.Variable;
import com.bonc.frame.util.CollectionUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * @Author: wangzhengbao
 * @DATE: 2019/12/26 18:10
 */
public class ModelContentInfo {

    private List<ConditionList> conditionListList = new ArrayList<>();

    private Map<String, VariableKpiRange> idRangesMap = new HashMap<>();


    private Set<String> variableIdSet = new HashSet<>();
    private Set<String> kpiIdSet = new HashSet<>();
    private Set<String> apiIdSet = new HashSet<>();
    private Set<String> ruleSetIdSet = new HashSet<>();


    public void addAllKpiId(Collection<String> kpiId) {
        kpiIdSet = addAllToSet(kpiIdSet, kpiId);
    }

    public void addAllVariableId(Collection<String> variableId) {
        variableIdSet = addAllToSet(variableIdSet, variableId);
    }

    public void addAllApiId(Collection<String> apiId) {
        apiIdSet = addAllToSet(apiIdSet, apiId);
    }

    public void addAllRuleSetId(List<String> ruleSetId) {
        ruleSetIdSet = addAllToSet(ruleSetIdSet, ruleSetId);
    }

    public void addKpiId(String kpiId) {
        kpiIdSet = addToSet(kpiIdSet, kpiId);
    }

    public void addVariableId(String variableId) {
        variableIdSet = addToSet(variableIdSet, variableId);
    }

    public void addApiId(String apiId) {
        apiIdSet = addToSet(apiIdSet, apiId);
    }

    public void addRuleSetId(String ruleSetId) {
        ruleSetIdSet = addToSet(ruleSetIdSet, ruleSetId);
    }

    private Set<String> addToSet(Set<String> set, String value) {
        if (StringUtils.isBlank(value)) {
            return set;
        }
        if (CollectionUtil.isEmpty(set)) {
            set = new HashSet<>();
        }
        value = value.replaceAll("(\\[|\\])", "");
        set.add(value);
        return set;
    }

    private Set<String> addAllToSet(Set<String> set, Collection<String> value) {
        if (CollectionUtil.isEmpty(value)) {
            return set;
        }
        if (CollectionUtil.isEmpty(set)) {
            set = new HashSet<>();
        }
        set.addAll(value);
        return set;
    }

    public List<String> getVariableIdSet() {
        if (variableIdSet != null && !variableIdSet.isEmpty()) {
            return new ArrayList<>(variableIdSet);
        } else {
            return new ArrayList<>();
        }
    }

    public void setVariableIdSet(Set<String> variableIdSet) {
        this.variableIdSet = variableIdSet;
    }

    public List<String> getKpiIdSet() {
        if (kpiIdSet != null && !kpiIdSet.isEmpty()) {
            return new ArrayList<>(kpiIdSet);
        } else {
            return new ArrayList<>();
        }
    }

    public void setKpiIdSet(Set<String> kpiIdSet) {
        this.kpiIdSet = kpiIdSet;
    }

    public List<String> getApiIdSet() {
        if (apiIdSet != null && !apiIdSet.isEmpty()) {
            return new ArrayList<>(apiIdSet);
        } else {
            return new ArrayList<>();
        }
    }

    public void setApiIdSet(Set<String> apiIdSet) {
        this.apiIdSet = apiIdSet;
    }

    public List<String> getRuleSetIdSet() {
        if (ruleSetIdSet != null && !ruleSetIdSet.isEmpty()) {
            return new ArrayList<>(ruleSetIdSet);
        } else {
            return new ArrayList<>();
        }
    }

    public void setRuleSetIdSet(Set<String> ruleSetIdSet) {
        this.ruleSetIdSet = ruleSetIdSet;
    }

    public List<ConditionList> getConditionListList() {
        return conditionListList;
    }

    public void setConditionListList(List<ConditionList> conditionListList) {
        this.conditionListList = conditionListList;
    }

    public void addConditionList(ConditionList conditionList) {
        if (conditionList == null) {
            return;
        }
        if (conditionListList == null) {
            conditionListList = new ArrayList<>();
        }
        conditionListList.add(conditionList);
    }

    public void putIdRange(String id, VariableKpiRange range) {
        if (idRangesMap == null) {
            idRangesMap = new HashMap<>();
        }
        idRangesMap.put(id, range);
    }

    public VariableKpiRange getRange(String id) {
        if (idRangesMap == null || idRangesMap.isEmpty()) {
            return null;
        }
        return idRangesMap.get(id);
    }

    public Map<String, VariableKpiRange> getIdRangesMap() {
        return idRangesMap;
    }

    public void setIdRangesMap(Map<String, VariableKpiRange> idRangesMap) {
        this.idRangesMap = idRangesMap;
    }
}
