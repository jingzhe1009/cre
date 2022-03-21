package com.bonc.frame.entity.variable;

import com.bonc.frame.util.CollectionUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class VariableTreeNode extends Variable {
    private String parentEntityId;

    private String aliasCode;//编码别名


    private Set<String> variableNestedIdList;
    private List<VariableTreeNode> variableNestedList;

    public void addVariableNestedList(VariableTreeNode variableNestedNode) {
        if (this.variableNestedList == null) {
            variableNestedList = new LinkedList<>();
        }
        variableNestedList.add(variableNestedNode);
    }

    public void addVariableNestedIdList(String nestedVariableId) {
        if (StringUtils.isBlank(nestedVariableId)) {
            return;
        }
        if (nestedVariableId.equals(getVariableId())) {
            return;
        }
        if (this.variableNestedIdList == null) {
            variableNestedIdList = new HashSet<>();
        }
        variableNestedIdList.add(nestedVariableId);
    }


    public String getParentEntityId() {
        return parentEntityId;
    }

    public void setParentEntityId(String parentEntityId) {
        this.parentEntityId = parentEntityId;
    }

    public List<VariableTreeNode> getVariableNestedList() {
        return variableNestedList;
    }

    public Set<String> getVariableNestedIdList() {
        return variableNestedIdList;
    }

    public void setVariableNestedIdList(Set<String> variableNestedIdList) {
        this.variableNestedIdList = variableNestedIdList;
    }

    public void setVariableNestedList(List<VariableTreeNode> variableNestedList) {
        this.variableNestedList = variableNestedList;
    }

    public String getAliasCode() {
        return aliasCode;
    }

    public void setAliasCode(String aliasCode) {
        this.aliasCode = aliasCode;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("VariableTree{");
        sb.append("variableId='").append(getVariableId()).append('\'');
        sb.append(", entityId='").append(getEntityId()).append('\'');
        sb.append(", folderId='").append(getFolderId()).append('\'');
        sb.append(", variableAlias='").append(getVariableAlias()).append('\'');
        sb.append(", variableCode='").append(getVariableCode()).append('\'');
        sb.append(", typeId='").append(getTypeId()).append('\'');
        sb.append(", kindId='").append(getKindId()).append('\'');
        sb.append(", variableFrom='").append(getVariableFrom()).append('\'');
        sb.append(", isDict='").append(getIsDict()).append('\'');
        sb.append(", defaultValue='").append(getDefaultValue()).append('\'');
        sb.append(", defaultValueType='").append(getDefaultValueType()).append('\'');
        sb.append(", variableRemarks='").append(getVariableRemarks()).append('\'');
        sb.append(", createPerson='").append(getCreatePerson()).append('\'');
        sb.append(", createDate=").append(getCreateDate());
        sb.append(", updatePerson='").append(getUpdatePerson()).append('\'');
        sb.append(", ruleDetailList=").append(getRuleDetailList());
        sb.append(", updateDate=").append(getUpdateDate());
        sb.append(", isPublic='").append(getIsPublic()).append('\'');
        sb.append(", variableGroupId='").append(getVariableGroupId()).append('\'');
        sb.append(", parentEntityId='").append(parentEntityId).append('\'');
        sb.append(", variableNestedIdList=").append(variableNestedIdList);
        sb.append(", variableNestedList=").append(variableNestedList);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VariableTreeNode variable = (VariableTreeNode) o;
        boolean variableNestedEquals = false;
        if (CollectionUtil.isEmpty(variableNestedIdList) && CollectionUtil.isEmpty(variable.variableNestedIdList)) {
            variableNestedEquals = true;
        }
        if ((!CollectionUtil.isEmpty(variableNestedIdList) && CollectionUtil.isEmpty(variable.variableNestedIdList)) || (CollectionUtil.isEmpty(variableNestedIdList) && !CollectionUtil.isEmpty(variable.variableNestedIdList))) {
            variableNestedEquals = false;
        }
        if (!CollectionUtil.isEmpty(variableNestedIdList) && !CollectionUtil.isEmpty(variable.variableNestedIdList)) {
            Set<String> otherVariableNestedIdSet = new HashSet<>(variable.variableNestedIdList);
            for (String variableNestedId : variableNestedIdList) {
                if (StringUtils.isNotBlank(variableNestedId)) {
                    if (!otherVariableNestedIdSet.contains(variableNestedId)) {
                        variableNestedEquals = false;
                        break;
                    } else {
                        otherVariableNestedIdSet.remove(variableNestedId);
                    }
                }
                variableNestedEquals = otherVariableNestedIdSet.isEmpty();
            }
        }

        boolean equals = Objects.equals(variableCode, variable.variableCode);
        boolean equals1 = Objects.equals(typeId, variable.typeId);
        boolean equals2 = Objects.equals(kindId, variable.kindId);
        boolean equals3 = Objects.equals(variableFrom, variable.variableFrom);
        boolean equals4 = Objects.equals(defaultValue, variable.defaultValue);
        boolean equals5 = Objects.equals(defaultValueType, variable.defaultValueType);
        boolean equals6 = Objects.equals(variableRemarks, variable.variableRemarks);
        boolean equals7 = Objects.equals(isPublic, variable.isPublic);
        boolean equals8 = Objects.equals(variableGroupId, variable.variableGroupId);
        boolean equals9 = Objects.equals(folderId, variable.folderId);

        return variableNestedEquals &&
                Objects.equals(variableCode, variable.variableCode) &&
                Objects.equals(typeId, variable.typeId) &&
                Objects.equals(kindId, variable.kindId) &&
                Objects.equals(variableFrom, variable.variableFrom) &&
                Objects.equals(defaultValue, variable.defaultValue) &&
                Objects.equals(defaultValueType, variable.defaultValueType) &&
                Objects.equals(variableRemarks, variable.variableRemarks) &&
                Objects.equals(isPublic, variable.isPublic) &&
                Objects.equals(variableGroupId, variable.variableGroupId) &&
                Objects.equals(folderId, variable.folderId);
    }
}
