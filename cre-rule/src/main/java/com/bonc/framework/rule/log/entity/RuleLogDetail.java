package com.bonc.framework.rule.log.entity;

import com.bonc.framework.api.log.entity.ConsumerInfo;
import com.bonc.framework.rule.kpi.KpiLog;
import com.bonc.framework.rule.resources.flow.basicflow.FlowNodeExecutionProcess;
import com.google.common.base.Objects;

import java.util.Date;
import java.util.List;

public class RuleLogDetail extends ConsumerInfo {
    private String id;

    private String logId;

    private String nodeId;

    private String nodeName;

    private String nodeType;

    private String state;

    private Date startTime;

    private Date endTime;

    private String result;

    private FlowNodeExecutionProcess executionProcess;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public FlowNodeExecutionProcess getExecutionProcess() {
        return executionProcess;
    }

    public void setExecutionProcess(FlowNodeExecutionProcess executionProcess) {
        this.executionProcess = executionProcess;
    }

    // --------------------------------- 执行过程 executionProcess ---------------------------------------------
    public void addKpiLog(KpiLog addKpiLog) {
        if (executionProcess == null) {
            executionProcess = new FlowNodeExecutionProcess();
        }
        executionProcess.addKpiLog(addKpiLog);
    }

    public void addAllKpiLog(List<KpiLog> addKpiLogs) {
        if (executionProcess == null) {
            executionProcess = new FlowNodeExecutionProcess();
        }
        executionProcess.addAllKpiLog(addKpiLogs);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", id)
                .add("logId", logId)
                .add("nodeId", nodeId)
                .add("nodeName", nodeName)
                .add("nodeType", nodeType)
                .add("state", state)
                .add("startTime", startTime)
                .add("endTime", endTime)
                .add("result", result)
                .add("consumerId", consumerId)
                .add("serverId", serverId)
                .add("consumerSeqNo", consumerSeqNo)
                .add("executionProcess", executionProcess)
                .toString();
    }
}