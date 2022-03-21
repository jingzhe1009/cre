package com.bonc.frame.entity.model;

import com.bonc.frame.util.CollectionUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: wangzhengbao
 * @DATE: 2019/12/30 16:54
 */
public class ConditionList {

    private Map<String, List<ConditionExpression>> kpiConditionExpressionsMap = new HashMap<>();
    private Map<String, List<ConditionExpression>> varConditionExpressionList = new HashMap<>();

    private Map<String, VariableKpiRange> idRangesMap = new HashMap<>();

    private String union;


    public void putKpiConditionExpression(ConditionExpression conditionExpression) {
        String kpiId = conditionExpression.getKpiId();
        String varId = conditionExpression.getVarId();
        if (!StringUtils.isBlank(kpiId)) {
            if (kpiConditionExpressionsMap == null) {
                kpiConditionExpressionsMap = new HashMap<>();
            }
            List<ConditionExpression> conditionExpressionList = kpiConditionExpressionsMap.get(kpiId);
            if (CollectionUtil.isEmpty(conditionExpressionList)) {
                conditionExpressionList = new ArrayList<>();
                kpiConditionExpressionsMap.put(kpiId, conditionExpressionList);
            }
            conditionExpressionList.add(conditionExpression);
        }
        if (!StringUtils.isBlank(varId)) {
            if (varConditionExpressionList == null) {
                varConditionExpressionList = new HashMap<>();
            }
            List<ConditionExpression> conditionExpressionList = varConditionExpressionList.get(varId);
            if (CollectionUtil.isEmpty(conditionExpressionList)) {
                conditionExpressionList = new ArrayList<>();
                varConditionExpressionList.put(varId, conditionExpressionList);
            }
            conditionExpressionList.add(conditionExpression);
        }
    }

    public Map<String, List<ConditionExpression>> getKpiConditionExpressionsMap() {
        return kpiConditionExpressionsMap;
    }

    public void setKpiConditionExpressionsMap(Map<String, List<ConditionExpression>> kpiConditionExpressionsMap) {
        this.kpiConditionExpressionsMap = kpiConditionExpressionsMap;
    }

    public Map<String, List<ConditionExpression>> getVarConditionExpressionList() {
        return varConditionExpressionList;
    }

    public void setVarConditionExpressionList(Map<String, List<ConditionExpression>> varConditionExpressionList) {
        this.varConditionExpressionList = varConditionExpressionList;
    }

    public String getUnion() {
        return union;
    }

    public void setUnion(String union) {
        this.union = union;
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