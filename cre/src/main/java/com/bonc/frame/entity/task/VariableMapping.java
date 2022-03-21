package com.bonc.frame.entity.task;

import java.util.Objects;

/**
 * @author yedunyao
 * @date 2019/5/29 9:45
 */
public class VariableMapping {

    private String id;

    private String taskId;

    private String variableId;

    private String variableCode;

    private String variableName;

    // K1 输入变量 K2 输出变量
    private String variableKind;

    private String variableTypeId;

    private String dbId;

    private String tableId;

    private String tableCode;

    private String columnId;

    private String columnCode;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
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

    public String getVariableKind() {
        return variableKind;
    }

    public void setVariableKind(String variableKind) {
        this.variableKind = variableKind;
    }

    public String getDbId() {
        return dbId;
    }

    public void setDbId(String dbId) {
        this.dbId = dbId;
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public String getTableCode() {
        return tableCode;
    }

    public void setTableCode(String tableCode) {
        this.tableCode = tableCode;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VariableMapping that = (VariableMapping) o;
        return Objects.equals(variableCode, that.variableCode) &&
                Objects.equals(tableCode, that.tableCode) &&
                Objects.equals(columnCode, that.columnCode);
    }

    @Override
    public int hashCode() {

        return Objects.hash(variableCode, tableCode, columnCode);
    }

    @Override
    public String toString() {
        return "VariableMapping{" +
                "id='" + id + '\'' +
                ", taskId='" + taskId + '\'' +
                ", variableId='" + variableId + '\'' +
                ", variableCode='" + variableCode + '\'' +
                ", variableName='" + variableName + '\'' +
                ", variableTypeId='" + variableTypeId + '\'' +
                ", variableKind='" + variableKind + '\'' +
                ", dbId='" + dbId + '\'' +
                ", tableId='" + tableId + '\'' +
                ", tableCode='" + tableCode + '\'' +
                ", columnId='" + columnId + '\'' +
                ", columnCode='" + columnCode + '\'' +
                '}';
    }
}
