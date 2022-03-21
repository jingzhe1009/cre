package com.bonc.frame.entity.task;

import com.bonc.frame.util.MyBeanUtilsBean;
import org.apache.commons.beanutils.BeanUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author yedunyao
 * @date 2019/5/28 15:57
 */
public class RuleTask {

    public static final String INIT = "0";          // 初始化 可运行
    public static final String READY = "1";         // 就绪 可停止
    public static final String RUNNING = "2";       // 运行中 可停止
    public static final String PAUSE = "3";         // 暂停
    public static final String STOPPING = "4";       // 停止中 不可操作 运行中的
    public static final String STOP = "5";          // 停止 可运行
    public static final String DELETE = "6";          // 删除
    public static final String EXCEPTION = "-1";    // 异常运行中 不可操作 运行中的
    public static final String ABNOMAL_STOP = "-2";    // 异常停止 可运行

    public static final String IS_ALLOWED_REPEAT_ONLY_ONE = "0";    // 只执行一次
    public static final String IS_ALLOWED_REPEAT_REPETITION = "1";    // 允许重复执行

    private String taskId;

    private String taskName;

    private String packageId;

    private String packageName;

    private String ruleId;

    private String ruleName;

    private String ruleVersion;

    //是否允许重复, 0:不允许; 1:允许;
    private String isAllowedRepeat;

    /**
     * 0 未启用 1 就绪 2 运行中
     */
    private String taskStatus;

    /**
     * quartz cron 表达式
     */
    private String cron;

    /**
     * 执行次数，-1表示不限次数
     */
    private long executeCount;

    /**
     * 查询输入表的sql语句
     */
    private String inputSql;

    /**
     * 插入输出表的sql语句
     */
    private String outputSql;

    private String existTableSql;

    private String createTableSql;

    private String inputDbId;

    private String outputDbId;

    private String inputDbType;

    private String outputDbType;

    private String inputTableId;

    private String inputTableCode;

    private String inputTableKind;

    private String outputTableId;

    private String outputTableCode;

    /**
     * 任务描述
     */
    private String describe;

    /**
     * 结果描述
     */
    private String resultDescribe;

    private Date createDate;

    private String createPerson;

    private Date updateDate;

    private String updatePerson;

    private long num;

    private long scanOffset;

    private long scanMaxSize;

    private long scanNextOffset;

    // hbse 最后一次扫描的行键 SCAN_NEXT_ROW_KEY
    private String scanNextRowKey;

    private List<VariableMapping> inputVariableMappings;

    private List<VariableMapping> outputVariableMappings;

    private ColumnMapping columnMapping;

    private String inputTableOrderField;

    private String inputTableWhereClause;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getIsAllowedRepeat() {
        return isAllowedRepeat;
    }

    public void setIsAllowedRepeat(String isAllowedRepeat) {
        this.isAllowedRepeat = isAllowedRepeat;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public long getExecuteCount() {
        return executeCount;
    }

    public void setExecuteCount(long executeCount) {
        this.executeCount = executeCount;
    }

    public String getInputSql() {
        return inputSql;
    }

    public void setInputSql(String inputSql) {
        this.inputSql = inputSql;
    }

    public String getOutputSql() {
        return outputSql;
    }

    public void setOutputSql(String outputSql) {
        this.outputSql = outputSql;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getResultDescribe() {
        return resultDescribe;
    }

    public void setResultDescribe(String resultDescribe) {
        this.resultDescribe = resultDescribe;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCreatePerson() {
        return createPerson;
    }

    public void setCreatePerson(String createPerson) {
        this.createPerson = createPerson;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getUpdatePerson() {
        return updatePerson;
    }

    public void setUpdatePerson(String updatePerson) {
        this.updatePerson = updatePerson;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRuleVersion() {
        return ruleVersion;
    }

    public void setRuleVersion(String ruleVersion) {
        this.ruleVersion = ruleVersion;
    }

    public long getNum() {
        return num;
    }

    public void setNum(long num) {
        this.num = num;
    }

    public String getScanNextRowKey() {
        return scanNextRowKey;
    }

    public void setScanNextRowKey(String scanNextRowKey) {
        this.scanNextRowKey = scanNextRowKey;
    }

    public List<VariableMapping> getInputVariableMappings() {
        return inputVariableMappings;
    }

    public void setInputVariableMappings(List<VariableMapping> inputVariableMappings) {
        this.inputVariableMappings = inputVariableMappings;
    }

    public List<VariableMapping> getOutputVariableMappings() {
        return outputVariableMappings;
    }

    public void setOutputVariableMappings(List<VariableMapping> outputVariableMappings) {
        this.outputVariableMappings = outputVariableMappings;
    }

    public String getExistTableSql() {
        return existTableSql;
    }

    public void setExistTableSql(String existTableSql) {
        this.existTableSql = existTableSql;
    }

    public String getCreateTableSql() {
        return createTableSql;
    }

    public void setCreateTableSql(String createTableSql) {
        this.createTableSql = createTableSql;
    }

    public String getInputDbId() {
        return inputDbId;
    }

    public void setInputDbId(String inputDbId) {
        this.inputDbId = inputDbId;
    }

    public String getOutputDbId() {
        return outputDbId;
    }

    public void setOutputDbId(String outputDbId) {
        this.outputDbId = outputDbId;
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

    public long getScanOffset() {
        return scanOffset;
    }

    public void setScanOffset(long scanOffset) {
        this.scanOffset = scanOffset;
    }

    public long getScanMaxSize() {
        return scanMaxSize;
    }

    public void setScanMaxSize(long scanMaxSize) {
        this.scanMaxSize = scanMaxSize;
    }

    public long getScanNextOffset() {
        return scanNextOffset;
    }

    public void setScanNextOffset(long scanNextOffset) {
        this.scanNextOffset = scanNextOffset;
    }

    public ColumnMapping getColumnMapping() {
        return columnMapping;
    }

    public void setColumnMapping(ColumnMapping columnMapping) {
        this.columnMapping = columnMapping;
    }

    public String getInputTableKind() {
        return inputTableKind;
    }

    public void setInputTableKind(String inputTableKind) {
        this.inputTableKind = inputTableKind;
    }

    public String getInputTableOrderField() {
        return inputTableOrderField;
    }

    public void setInputTableOrderField(String inputTableOrderField) {
        this.inputTableOrderField = inputTableOrderField;
    }

    public String getInputTableWhereClause() {
        return inputTableWhereClause;
    }

    public void setInputTableWhereClause(String inputTableWhereClause) {
        this.inputTableWhereClause = inputTableWhereClause;
    }

    public String getInputDbType() {
        return inputDbType;
    }

    public void setInputDbType(String inputDbType) {
        this.inputDbType = inputDbType;
    }

    public String getOutputDbType() {
        return outputDbType;
    }

    public void setOutputDbType(String outputDbType) {
        this.outputDbType = outputDbType;
    }

    public static Map<String, String> covertToMap(RuleTask ruleTask) throws Exception {
        if (ruleTask == null) {
            throw new IllegalArgumentException("序列化任务对象参数不能为null");
        }
        // 避免BeanUtils转换异常
        ruleTask.setCreateDate(null);
        ruleTask.setUpdateDate(null);
        return BeanUtils.describe(ruleTask);
    }

    public static RuleTask convertToObj(Map<String, Object> wrappedMap) throws Exception {
        final RuleTask ruleTask = new RuleTask();
        MyBeanUtilsBean.getInstance().populate(ruleTask, wrappedMap);
        return ruleTask;
    }

    public boolean canModify() {
        switch (this.taskStatus) {
            case READY:
            case RUNNING:
            case PAUSE:
            case STOPPING:
                return false;
            default:
                return true;
        }
    }

    @Override
    public String toString() {
        return "RuleTask{" +
                "taskId='" + taskId + '\'' +
                ", taskName='" + taskName + '\'' +
                ", packageId='" + packageId + '\'' +
                ", packageName='" + packageName + '\'' +
                ", ruleId='" + ruleId + '\'' +
                ", ruleName='" + ruleName + '\'' +
                ", taskStatus='" + taskStatus + '\'' +
                ", cron='" + cron + '\'' +
                ", executeCount=" + executeCount +
                ", inputSql='" + inputSql + '\'' +
                ", existTableSql='" + existTableSql + '\'' +
                ", createTableSql='" + createTableSql + '\'' +
                ", inputDbId='" + inputDbId + '\'' +
                ", outputDbId='" + outputDbId + '\'' +
                ", inputDbType='" + inputDbType + '\'' +
                ", outputDbType='" + outputDbType + '\'' +
                ", inputTableId='" + inputTableId + '\'' +
                ", inputTableCode='" + inputTableCode + '\'' +
                ", inputTableKind='" + inputTableKind + '\'' +
                ", outputTableId='" + outputTableId + '\'' +
                ", outputTableCode='" + outputTableCode + '\'' +
                ", inputTableOrderField='" + inputTableOrderField + '\'' +
                ", inputTableWhereClause='" + inputTableWhereClause + '\'' +
                ", createDate=" + createDate +
                ", createPerson='" + createPerson + '\'' +
                ", updateDate=" + updateDate +
                ", updatePerson='" + updatePerson + '\'' +
                ", scanOffset=" + scanOffset +
                ", scanMaxSize=" + scanMaxSize +
                ", scanNextOffset=" + scanNextOffset +
                ", scanNextRowKey=" + scanNextRowKey +
                '}';
    }
}
