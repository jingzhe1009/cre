package com.bonc.frame.entity.model;

import com.bonc.frame.util.CollectionUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * kpiId与varId的值为 中括号括起来的Id [kpiId] [varId]
 *
 * @Author: wangzhengbao
 * @DATE: 2019/12/23 9:59
 */
public class ConditionExpression {

    public static final String TYPE_KPIID = "kpiId";
    public static final String TYPE_VARID = "varId";
    private Set<String> kpiIdSet;
    private Set<String> variableIdSet;

    private String conditionId;
    private String kpiId;
    private String varId;
    private String opt;
    private String valueType;
    private String value;
    private List<ConditionExpressionReference> references;

    private VariableKpiRange range;

    public void putReference(ConditionExpressionReference conditionExpressionReferences) {
        if (references == null) {
            references = new ArrayList<>();
        }
        references.add(conditionExpressionReferences);

    }

    public void addReference(String kpiId, String varId) {
        if (references == null) {
            references = new ArrayList<>();
        }
        ConditionExpressionReference oneConditionExpressionReferences = getOneConditionExpressionReferences(kpiId, varId);
        references.add(oneConditionExpressionReferences);
    }

    private ConditionExpressionReference getOneConditionExpressionReferences(String kpiId, String varId) {
        ConditionExpressionReference conditionExpressionReference = null;
        if (StringUtils.isBlank(kpiId) && StringUtils.isBlank(varId)) {
            return conditionExpressionReference;
        }
        conditionExpressionReference = new ConditionExpressionReference();
        if (!StringUtils.isBlank(kpiId)) {
            kpiId = kpiId.replaceAll("(\\[|\\])", "");
            conditionExpressionReference.setKpiId(kpiId);
        }
        if (!StringUtils.isBlank(varId)) {
            varId = varId.replaceAll("(\\[|\\])", "");
            conditionExpressionReference.setVarId(varId);
        }
        return conditionExpressionReference;
    }


    public void addKpiId(String kpiId) {
        addToSet(kpiIdSet, kpiId);
    }

    public void addVariableId(String variableId) {
        addToSet(variableIdSet, variableId);
    }

    private void addToSet(Set<String> set, String value) {
        if (StringUtils.isBlank(value)) {
            return;
        }
        if (CollectionUtil.isEmpty(set)) {
            set = new HashSet<>();
        }
        set.add(value);
    }

    public String getConditionId() {
        return conditionId;
    }

    public void setConditionId(String conditionId) {
        this.conditionId = conditionId;
    }

    public String getKpiId() {
        return kpiId;
    }

    public void setKpiId(String kpiId) {
        if (!StringUtils.isBlank(kpiId)) {
            kpiId = kpiId.replaceAll("(\\[|\\])", "");
            this.kpiId = kpiId;
        }
    }

    public String getVarId() {
        return varId;
    }

    public void setVarId(String varId) {
        if (!StringUtils.isBlank(varId)) {
            varId = varId.replaceAll("(\\[|\\])", "");
            this.varId = varId;
        }
    }

    public String getOpt() {
        return opt;
    }

    public void setOpt(String opt) {
        this.opt = opt;
    }


    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    public List<ConditionExpressionReference> getReferences() {
        return references;
    }

    public void setReferences(List<ConditionExpressionReference> references) {
        this.references = references;
    }

    public Set<String> getKpiIdSet() {
        return kpiIdSet;
    }

    public void setKpiIdSet(Set<String> kpiIdSet) {
        this.kpiIdSet = kpiIdSet;
    }

    public Set<String> getVariableIdSet() {
        return variableIdSet;
    }

    public void setVariableIdSet(Set<String> variableIdSet) {
        this.variableIdSet = variableIdSet;
    }

    public VariableKpiRange getRange() {
        return range;
    }

    public void setRange(VariableKpiRange range) {
        this.range = range;
    }
}
