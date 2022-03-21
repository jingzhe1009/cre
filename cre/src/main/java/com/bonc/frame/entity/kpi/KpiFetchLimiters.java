package com.bonc.frame.entity.kpi;

import java.io.Serializable;
import java.util.Objects;

/**
 * 指标取数限定条件
 * <p>
 * 记录指标和参数、字段的映射关系
 * kpiId-variableId[,columnId]。
 * 当取数类型为接口时，字段可以为空。
 *
 * @author yedunyao
 * @date 2019/10/24 11:00
 */
public class KpiFetchLimiters implements Serializable, Cloneable {

    private static final long serialVersionUID = -8157471074312865289L;

    private String id;

    private String kpiId;

    private String columnId;
    private String columnCode;
    private String columnName;

    private String variableId;
    private String variableCode;
    private String variableName;
    private String variableTypeId;

    /**
     * 取数类型：0，数据源；1，接口
     */
    private String fetchType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKpiId() {
        return kpiId;
    }

    public void setKpiId(String kpiId) {
        this.kpiId = kpiId;
    }

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    public String getColumnCode() {
        return columnCode;
    }

    public void setColumnCode(String columnCode) {
        this.columnCode = columnCode;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getVariableId() {
        return variableId;
    }

    public void setVariableId(String variableId) {
        this.variableId = variableId;
    }

    public String getVariableCode() {
        return variableCode;
    }

    public void setVariableCode(String variableCode) {
        this.variableCode = variableCode;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public String getVariableTypeId() {
        return variableTypeId;
    }

    public void setVariableTypeId(String variableTypeId) {
        this.variableTypeId = variableTypeId;
    }

    public String getFetchType() {
        return fetchType;
    }

    public void setFetchType(String fetchType) {
        this.fetchType = fetchType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KpiFetchLimiters that = (KpiFetchLimiters) o;
        return Objects.equals(columnId, that.columnId) &&
                Objects.equals(columnCode, that.columnCode) &&
                Objects.equals(columnName, that.columnName) &&
                Objects.equals(variableId, that.variableId) &&
                Objects.equals(variableCode, that.variableCode) &&
                Objects.equals(variableName, that.variableName) &&
                Objects.equals(variableTypeId, that.variableTypeId) &&
                Objects.equals(fetchType, that.fetchType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kpiId, columnId, columnCode, columnName, variableId, variableCode, variableName, variableTypeId, fetchType);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("KpiFetchLimiters{");
        sb.append("id='").append(id).append('\'');
        sb.append("kpiId='").append(kpiId).append('\'');
        sb.append(", columnId='").append(columnId).append('\'');
        sb.append(", columnCode='").append(columnCode).append('\'');
        sb.append(", columnName='").append(columnName).append('\'');
        sb.append(", variableId='").append(variableId).append('\'');
        sb.append(", variableCode='").append(variableCode).append('\'');
        sb.append(", variableName='").append(variableName).append('\'');
        sb.append(", variableTypeId='").append(variableTypeId).append('\'');
        sb.append(", fetchType='").append(fetchType).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public KpiFetchLimiters clone() throws CloneNotSupportedException {
        return (KpiFetchLimiters) super.clone();
    }
}
