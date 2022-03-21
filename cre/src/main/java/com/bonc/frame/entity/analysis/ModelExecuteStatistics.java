package com.bonc.frame.entity.analysis;

import java.util.ArrayList;
import java.util.List;

public class ModelExecuteStatistics {
    private String ruleName;
    private String ruleId;
    private String folderName;
    private String folderId;
    private String deptId;
    private String deptName;

    //allCount;    // 总数
    //executeCount // 正在执行
    //successCount // 成功
    //falseCount;  //失败
    private List<ModelExecuteCountInfo> executeCountInfoList;


    public void setExecuteCountInfo(ModelExecuteCountInfo executeCountInfo){
        if(executeCountInfo==null){
            return;
        }
        if(executeCountInfoList == null){
            executeCountInfoList = new ArrayList<>();
        }
        executeCountInfoList.add(executeCountInfo);
    }


    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public List<ModelExecuteCountInfo> getExecuteCountInfoList() {
        return executeCountInfoList;
    }

    public void setExecuteCountInfoList(List<ModelExecuteCountInfo> executeCountInfoList) {
        this.executeCountInfoList = executeCountInfoList;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ModelExecuteStatistics{");
        sb.append("ruleName='").append(ruleName).append('\'');
        sb.append(", ruleId='").append(ruleId).append('\'');
        sb.append(", folderName='").append(folderName).append('\'');
        sb.append(", folderId='").append(folderId).append('\'');
        sb.append(", deptId='").append(deptId).append('\'');
        sb.append(", deptName='").append(deptName).append('\'');
        sb.append(", executeCountInfoList=").append(executeCountInfoList);
        sb.append('}');
        return sb.toString();
    }
}
