package com.bonc.frame.entity.task;

/**
 * @author yedunyao
 * @date 2019/6/12 9:33
 */
public class ColumnMapping {

    private String id;
    private String taskId;
    private String inputDbId;
    private String inputTableId;
    private String inputTableCode;
    private String inputColumnId;
    private String inputColumnCode;
    private String outputDbId;
    private String outputTableId;
    private String outputTableCode;
    private String outputColumnId;
    private String outputColumnCode;

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

    public String getInputDbId() {
        return inputDbId;
    }

    public void setInputDbId(String inputDbId) {
        this.inputDbId = inputDbId;
    }

    public String getInputTableId() {
        return inputTableId;
    }

    public void setInputTableId(String inputTableId) {
        this.inputTableId = inputTableId;
    }

    public String getInputTableCode() {
        return inputTableCode;
    }

    public void setInputTableCode(String inputTableCode) {
        this.inputTableCode = inputTableCode;
    }

    public String getInputColumnId() {
        return inputColumnId;
    }

    public void setInputColumnId(String inputColumnId) {
        this.inputColumnId = inputColumnId;
    }

    public String getInputColumnCode() {
        return inputColumnCode;
    }

    public void setInputColumnCode(String inputColumnCode) {
        this.inputColumnCode = inputColumnCode;
    }

    public String getOutputDbId() {
        return outputDbId;
    }

    public void setOutputDbId(String outputDbId) {
        this.outputDbId = outputDbId;
    }

    public String getOutputTableId() {
        return outputTableId;
    }

    public void setOutputTableId(String outputTableId) {
        this.outputTableId = outputTableId;
    }

    public String getOutputTableCode() {
        return outputTableCode;
    }

    public void setOutputTableCode(String outputTableCode) {
        this.outputTableCode = outputTableCode;
    }

    public String getOutputColumnId() {
        return outputColumnId;
    }

    public void setOutputColumnId(String outputColumnId) {
        this.outputColumnId = outputColumnId;
    }

    public String getOutputColumnCode() {
        return outputColumnCode;
    }

    public void setOutputColumnCode(String outputColumnCode) {
        this.outputColumnCode = outputColumnCode;
    }

    @Override
    public String toString() {
        return "ColumnMapping{" +
                "id='" + id + '\'' +
                ", taskId='" + taskId + '\'' +
                ", inputDbId='" + inputDbId + '\'' +
                ", inputTableId='" + inputTableId + '\'' +
                ", inputTableCode='" + inputTableCode + '\'' +
                ", inputColumnId='" + inputColumnId + '\'' +
                ", inputColumnCode='" + inputColumnCode + '\'' +
                ", outputDbId='" + outputDbId + '\'' +
                ", outputTableId='" + outputTableId + '\'' +
                ", outputTableCode='" + outputTableCode + '\'' +
                ", outputColumnId='" + outputColumnId + '\'' +
                ", outputColumnCode='" + outputColumnCode + '\'' +
                '}';
    }
}
