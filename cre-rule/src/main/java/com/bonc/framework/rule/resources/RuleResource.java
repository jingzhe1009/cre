package com.bonc.framework.rule.resources;

import com.bonc.framework.rule.RuleType;
import com.bonc.framework.rule.resources.flow.FlowNode;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 规则资源基类，不同的规则类型可以继承此类，增加特殊的熟悉属性
 *
 * @author qxl
 * @version 1.0
 * @date 2018年3月5日 下午4:32:18
 */
public class RuleResource implements Serializable {

    private static final long serialVersionUID = -4654015833179129347L;

    /**
     * 文件夹ID
     */
    private String folderId;

    /**
     * 规则ID
     */
    private String ruleId;

    /**
     * 规则名
     */
    private String ruleName;

    /**
     * 规则类型
     */
    private RuleType ruleType;

    /**
     * 是否记录日志
     */
    private boolean isLog;

    /**
     * 规则解析转换编译前的原始内容
     */
    private String ruleContent;

    /**
     * 规则编译后的字节码
     */
    private byte[] ruleBytes;

    /**
     * 子规则list
     */
    private List<RuleResource> childResourceList;

    /**
     * 子规则map
     */
    private Map<String, RuleResource> childResourceMap;

    /** 规则执行器  */
//	private IExecutor executor;

    /**
     * 决策树规则解析后的FlowNode
     */
    private FlowNode flowNode;

    public RuleResource() {
    }


    public FlowNode getFlowNode() {
        return flowNode;
    }

    public void setFlowNode(FlowNode flowNode) {
        this.flowNode = flowNode;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public RuleType getRuleType() {
        return ruleType;
    }

    public void setRuleType(RuleType ruleType) {
        this.ruleType = ruleType;
    }

    public boolean isLog() {
        return isLog;
    }

    public void setLog(boolean isLog) {
        this.isLog = isLog;
    }

    public String getRuleContent() {
        return ruleContent;
    }

    public void setRuleContent(String ruleContent) {
        this.ruleContent = ruleContent;
    }

    public byte[] getRuleBytes() {
        return ruleBytes;
    }

    public void setRuleBytes(byte[] ruleBytes) {
        this.ruleBytes = ruleBytes;
    }

    public List<RuleResource> getChildResourceList() {
        return childResourceList;
    }

    public void setChildResourceList(List<RuleResource> childResourceList) {
        this.childResourceList = childResourceList;
    }

    public Map<String, RuleResource> getChildResourceMap() {
        return childResourceMap;
    }

    public void setChildResourceMap(Map<String, RuleResource> childResourceMap) {
        this.childResourceMap = childResourceMap;
    }

    @Override
    public String toString() {
        return "RuleResource [ruleId=" + ruleId + ", ruleName=" + ruleName + ", ruleType=" + ruleType + ", ruleContent="
                + ruleContent + ", childResourceList=" + childResourceList + ", childResourceMap=" + childResourceMap
                + "]";
    }

    public String info() {
        return "RuleResource [ruleId=" + ruleId + ", ruleName=" + ruleName + ", ruleType=" + ruleType + "]";
    }

}
