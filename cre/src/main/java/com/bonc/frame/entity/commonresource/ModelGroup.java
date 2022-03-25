package com.bonc.frame.entity.commonresource;

import com.bonc.frame.entity.BaseEntity;
import com.bonc.frame.entity.rulefolder.RuleFolder;

import java.util.Objects;

/**
 * @author yedunyao
 * @date 2019/9/2 15:42
 */
public class ModelGroup extends BaseEntity {

    private String modelGroupId;

    private String modelGroupName;

    private String modelGroupCode;

    public String getModelGroupCode() {
        return modelGroupCode;
    }

    public void setModelGroupCode(String modelGroupCode) {
        this.modelGroupCode = modelGroupCode;
    }

    public String getModelGroupDesc() {
        return modelGroupDesc;
    }

    public void setModelGroupDesc(String modelGroupDesc) {
        this.modelGroupDesc = modelGroupDesc;
    }

    private String modelGroupDesc;

    public String getModelGroupId() {
        return modelGroupId;
    }

    public void setModelGroupId(String modelGroupId) {
        this.modelGroupId = modelGroupId;
    }

    public String getModelGroupName() {
        return modelGroupName;
    }

    public void setModelGroupName(String modelGroupName) {
        this.modelGroupName = modelGroupName;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ModelGroup{");
        sb.append("modelGroupId='").append(modelGroupId).append('\'');
        sb.append(", modelGroupName='").append(modelGroupName).append('\'');
        sb.append(", modelGroupCode='").append(modelGroupCode).append('\'');
        sb.append(", modelGroupDesc='").append(modelGroupDesc).append('\'');
        sb.append(", createDate=").append(createDate);
        sb.append(", createPerson='").append(createPerson).append('\'');
        sb.append(", updatePerson='").append(updatePerson).append('\'');
        sb.append(", updateDate=").append(updateDate);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModelGroup that = (ModelGroup) o;
        return Objects.equals(modelGroupName, that.modelGroupName);
    }

}
