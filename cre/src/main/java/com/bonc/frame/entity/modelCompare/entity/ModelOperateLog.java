package com.bonc.frame.entity.modelCompare.entity;

import java.util.*;

/**
 * 模型操作日志实体类
 */
public class ModelOperateLog {
    public static final String CREATE_MODEL_TYPE = "0";  // 0:创建模型基本;
    public static final String DELETE_MODEL_TYPE = "1";  // 1:删除模型;
    public static final String DELETE_VERSION_TYPE = "2";  // 2:删除版本;
    public static final String STAGE_VERSION_TYPE = "3";  // 3:暂存;
    public static final String COMMIT_MODEL_TYPE = "4";  // 4:提交;
    public static final String PUBLISH_TYPE = "5";  // 5:发布;
    public static final String ENABLE_TYPE = "6";   // 6:启用;
    public static final String DIS_ENABLE_TYPE = "7";   // 7:停用;
    public static final String CLONE_TYPE = "8";  // 8:克隆;
    public static final String UPDATE_MODEL_HEADER_TYPE = "9";  // 9:修改模型基本信息;
    public static final String FILE_IMPORT = "10";  // 9:修改模型基本信息;

    private String logId;
    private String folderId;
    private String folderName;
    private String ruleName;
    private String modelName;
    private String modelId;
    private String version;

    private String newFolderId;
    private String newFolderName;
    private String newRuleName;
    private String newModelName;
    private String newModelId;
    private String newVersion;

    /**
     * 操作类型
     */
    private String operateType;
    /**
     * 操作内容
     * JSON 格式
     */
    private String operateContent;
    private String operatePerson;
    private Date operateTime;
    private String ip;

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }


    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getNewFolderId() {
        return newFolderId;
    }

    public void setNewFolderId(String newFolderId) {
        this.newFolderId = newFolderId;
    }

    public String getNewModelName() {
        return newModelName;
    }

    public void setNewModelName(String newModelName) {
        this.newModelName = newModelName;
    }

    public String getNewModelId() {
        return newModelId;
    }

    public void setNewModelId(String newModelId) {
        this.newModelId = newModelId;
    }

    public String getNewVersion() {
        return newVersion;
    }

    public void setNewVersion(String newVersion) {
        this.newVersion = newVersion;
    }

    public String getOperateType() {
        return operateType;
    }


    public static List<Map<String, Object>> getAllOperateTypes() {
        List<Map<String, Object>> operateTypes = new ArrayList<>();
//        operateTypes.add(getOneOperateTypeMap(CREATE_MODEL_TYPE, "创建模型基本信息"));
        operateTypes.add(getOneOperateTypeMap(DELETE_MODEL_TYPE, "删除模型"));
        operateTypes.add(getOneOperateTypeMap(DELETE_VERSION_TYPE, "删除版本"));
        operateTypes.add(getOneOperateTypeMap(STAGE_VERSION_TYPE, "暂存"));
        operateTypes.add(getOneOperateTypeMap(COMMIT_MODEL_TYPE, "提交"));
        operateTypes.add(getOneOperateTypeMap(PUBLISH_TYPE, "发布"));
        operateTypes.add(getOneOperateTypeMap(ENABLE_TYPE, "启用"));
        operateTypes.add(getOneOperateTypeMap(DIS_ENABLE_TYPE, "停用"));
        operateTypes.add(getOneOperateTypeMap(CLONE_TYPE, "克隆"));
        operateTypes.add(getOneOperateTypeMap(UPDATE_MODEL_HEADER_TYPE, "修改模型基本信息"));
        operateTypes.add(getOneOperateTypeMap(FILE_IMPORT, "导入"));
        return operateTypes;
    }

    private static Map<String, Object> getOneOperateTypeMap(String operateCode, String operatedesc) {
        Map<String, Object> map = new HashMap<>();
        map.put("operateCode", operateCode);
        map.put("operatedesc", operatedesc);
        return map;
    }


    public void setOperateType(String operateType) {
        if (operateType == null) {
            return;
        }
        switch (operateType) {
            case CREATE_MODEL_TYPE:
                this.operateType = "创建模型基本";
            case DELETE_MODEL_TYPE:
                this.operateType = "删除模型";
            case DELETE_VERSION_TYPE:
                this.operateType = "删除版本";
            case STAGE_VERSION_TYPE:
                this.operateType = "暂存";
            case COMMIT_MODEL_TYPE:
                this.operateType = "提交";
            case PUBLISH_TYPE:
                this.operateType = "发布";
            case ENABLE_TYPE:
                this.operateType = "启用";
            case DIS_ENABLE_TYPE:
                this.operateType = "停用";
            case CLONE_TYPE:
                this.operateType = "克隆";
            case UPDATE_MODEL_HEADER_TYPE:
                this.operateType = "修改模型基本信息";
            default:
                this.operateType = operateType;
        }
    }

    public String getOperateContent() {
        return operateContent;
    }

    public void setOperateContent(String operateContent) {
        this.operateContent = operateContent;
    }

    public String getOperatePerson() {
        return operatePerson;
    }

    public void setOperatePerson(String operatePerson) {
        this.operatePerson = operatePerson;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getNewFolderName() {
        return newFolderName;
    }

    public void setNewFolderName(String newFolderName) {
        this.newFolderName = newFolderName;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getNewRuleName() {
        return newRuleName;
    }

    public void setNewRuleName(String newRuleName) {
        this.newRuleName = newRuleName;
    }

    @Override
    public String toString() {
        return "ModelOperateLog{" +
                "logId='" + logId + '\'' +
                ", folderId='" + folderId + '\'' +
                ", folderName='" + folderName + '\'' +
                ", ruleName='" + ruleName + '\'' +
                ", modelName='" + modelName + '\'' +
                ", modelId='" + modelId + '\'' +
                ", version='" + version + '\'' +
                ", newFolderId='" + newFolderId + '\'' +
                ", newFolderName='" + newFolderName + '\'' +
                ", newRuleName='" + newRuleName + '\'' +
                ", newModelName='" + newModelName + '\'' +
                ", newModelId='" + newModelId + '\'' +
                ", newVersion='" + newVersion + '\'' +
                ", operateType='" + operateType + '\'' +
                ", operateContent='" + operateContent + '\'' +
                ", operatePerson='" + operatePerson + '\'' +
                ", operateTime=" + operateTime +
                ", ip='" + ip + '\'' +
                '}';
    }
}
