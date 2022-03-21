package com.bonc.frame.service.impl.task;

import com.bonc.frame.dao.DaoHelper;
import com.bonc.frame.entity.datasource.DataSource;
import com.bonc.frame.entity.metadata.MetaDataColumn;
import com.bonc.frame.entity.metadata.MetaDataTable;
import com.bonc.frame.entity.metadata.RelationTable;
import com.bonc.frame.entity.rule.RuleDetailWithBLOBs;
import com.bonc.frame.entity.task.ColumnMapping;
import com.bonc.frame.entity.task.RuleTask;
import com.bonc.frame.entity.task.VariableMapping;
import com.bonc.frame.module.db.operator.hbase.HbaseOperator;
import com.bonc.frame.module.db.sqlconvert.ISqlConvert;
import com.bonc.frame.module.db.sqlconvert.SqlConvertFactory;
import com.bonc.frame.module.quartz.TaskService;
import com.bonc.frame.security.ResourceType;
import com.bonc.frame.service.auth.AuthorityService;
import com.bonc.frame.service.dbresource.DbResourceService;
import com.bonc.frame.service.metadata.MetaDataMgrService;
import com.bonc.frame.service.rule.RuleDetailService;
import com.bonc.frame.service.task.ColumnMappingService;
import com.bonc.frame.service.task.RuleTaskService;
import com.bonc.frame.service.task.VariableMappingService;
import com.bonc.frame.util.IdUtil;
import com.bonc.frame.util.MapUtil;
import com.bonc.frame.util.ResponseResult;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yedunyao
 * @date 2019/5/28 17:35
 */
@Service("ruleTaskService")
public class RuleTaskServiceImpl implements RuleTaskService {

    private Log log = LogFactory.getLog(RuleTaskServiceImpl.class);

    @Autowired
    private MetaDataMgrService metaDataMgrService;

    @Autowired
    private DbResourceService dbResourceService;

    @Autowired
    private VariableMappingService variableMappingService;

    @Autowired
    private ColumnMappingService columnMappingService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private RuleDetailService ruleDetailService;

    @Autowired
    private DaoHelper daoHelper;

    private final String _RULE_TASK_PREFIX = "com.bonc.frame.dao.task.RuleTaskMapper.";

    @Override
    public Map<String, Object> getRuleTaskList(String taskName, String packageName,
                                               String ruleName, String ruleVersion,
                                               String taskStatus,
                                               String sort, String order,
                                               String start, String size) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("taskName", taskName);
        param.put("packageName", packageName);
        param.put("ruleName", ruleName);
        param.put("ruleVersion", ruleVersion);
        param.put("taskStatus", taskStatus);
        param.put("sort", sort);
        param.put("order", order);
        Map<String, Object> result = daoHelper.queryForPageList(
                _RULE_TASK_PREFIX + "getRuleTaskList", param, start, size);
        return result;
    }

    @Override
    public List<RuleTask> getRuleTaskByProperty(String taskId, String taskName, String packageName,
                                                String ruleName, String ruleVersion,
                                                String taskStatus, String isAllowedRepeat) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("taskId", taskId);
        param.put("taskName", taskName);
        param.put("packageName", packageName);
        param.put("ruleName", ruleName);
        param.put("ruleVersion", ruleVersion);
        param.put("taskStatus", taskStatus);
        param.put("isAllowedRepeat", isAllowedRepeat);
        List<RuleTask> result = daoHelper.queryForList(
                _RULE_TASK_PREFIX + "getRuleTaskList", param);
        return result;
    }
    @Override
    public List<RuleTask> getRuleTaskByPropertyNoAuth(String taskId, String taskName, String packageName,
                                                String ruleName, String ruleVersion,
                                                String taskStatus, String isAllowedRepeat) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("taskId", taskId);
        param.put("taskName", taskName);
        param.put("packageName", packageName);
        param.put("ruleName", ruleName);
        param.put("ruleVersion", ruleVersion);
        param.put("taskStatus", taskStatus);
        param.put("isAllowedRepeat", isAllowedRepeat);
        List<RuleTask> result = daoHelper.queryForList(
                _RULE_TASK_PREFIX + "getRuleTaskListNoAuth", param);
        return result;
    }

    @Override
    public Map<String, Object> pagedRuleTaskResource(String taskName, String packageName,
                                                     String ruleName, String ruleVersion,
                                                     String start, String size) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("taskName", taskName);
        param.put("packageName", packageName);
        param.put("ruleName", ruleName);
        param.put("ruleVersion", ruleVersion);
        Map<String, Object> result = daoHelper.queryForPageList(
                _RULE_TASK_PREFIX + "pagedRuleTaskResource", param, start, size);
        return result;
    }

    @Override
    public ResponseResult detail(String taskId) {
        if (!isExists(taskId)) {
            return ResponseResult.createFailInfo("任务不存在，taskId: " + taskId);
        }
        final RuleTask ruleTask = selectByPrimaryKey(taskId);
        final List<VariableMapping> inputVariables = variableMappingService.getInputVariables(taskId);
        final List<VariableMapping> outputVariables = variableMappingService.getOutputVariables(taskId);
        ruleTask.setInputVariableMappings(inputVariables);
        ruleTask.setOutputVariableMappings(outputVariables);
        final ColumnMapping columnMapping = columnMappingService.selectByTaskId(taskId);
        ruleTask.setColumnMapping(columnMapping);
        Map<String, Object> result = MapUtil.convertObjectToLinkedMap(ruleTask, true);
        RuleDetailWithBLOBs rule = ruleDetailService.getRule(ruleTask.getRuleId());
        result.put("modelId", rule.getRuleName());
        return ResponseResult.createSuccessInfo("success", result);
    }

    @Override
    public RuleTask selectByPrimaryKey(String taskId) {
        return (RuleTask) daoHelper.queryOne(_RULE_TASK_PREFIX + "selectByPrimaryKey", taskId);
    }

    @Override
    public List<RuleTask> selectByPrimaryKeys(List<String> taskIds) {
        return daoHelper.queryForList(_RULE_TASK_PREFIX + "selectByPrimaryKeys", taskIds);
    }

    @Override
    public List<RuleTask> getAllReadyTask() {
        return getAllTaskByStatus(RuleTask.READY);
    }

    @Override
    public List<RuleTask> getAllNeedRunTask() {
        return daoHelper.queryForList(_RULE_TASK_PREFIX + "getAllNeedRunTask");
    }

    @Override
    public List<RuleTask> getAllNeedStopTask() {
        return daoHelper.queryForList(_RULE_TASK_PREFIX + "getAllNeedStopTask");
    }

    @Override
    public List<RuleTask> getAllTaskByStatus(String taskStatus) {
        return daoHelper.queryForList(_RULE_TASK_PREFIX + "getAllTaskByStatus", taskStatus);
    }

    @Override
    public List<RuleTask> getAllStopTask() {
        return getAllTaskByStatus(RuleTask.STOPPING);
    }

    @Override
    public List<RuleTask> getAllDeleteTask() {
        return getAllTaskByStatus(RuleTask.DELETE);
    }

    @Override
    public List<RuleTask> getAllExceptionTask() {
        return getAllTaskByStatus(RuleTask.EXCEPTION);
    }

    @Override
    public boolean isExists(RuleTask task) {
        final RuleTask ruleTask = selectByPrimaryKey(task.getTaskId());
        return ruleTask == null ? false : true;
    }

    @Override
    public boolean isExists(String taskId) {
        final RuleTask ruleTask = selectByPrimaryKey(taskId);
        return ruleTask == null ? false : true;
    }

    private boolean checkNameIsExist(String taskName, @Nullable String taskId) {
        Map<String, String> param = new HashMap<>(2);
        param.put("taskName", taskName);
        param.put("taskId", taskId);
        int obj = (int) daoHelper.queryOne(_RULE_TASK_PREFIX + "countByName", param);
        if (obj > 0) {
            return true;
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult save(RuleTask ruleTask, String userId) {
        if (ruleTask == null) {
            return ResponseResult.createFailInfo("请求参数不能为null");
        }

        if (checkNameIsExist(ruleTask.getTaskName(), null)) {
            return ResponseResult.createFailInfo("任务名称已存在");
        }

        final String taskId = IdUtil.createId();
        ruleTask.setTaskId(taskId);
        ruleTask.setCreateDate(new Date());
        ruleTask.setCreatePerson(userId);
        ruleTask.setTaskStatus(RuleTask.INIT);    // 未启用

        convertSql(ruleTask);
        daoHelper.insert(_RULE_TASK_PREFIX + "insertSelective", ruleTask);

        final List<VariableMapping> inputVariableMappings = ruleTask.getInputVariableMappings();
        final List<VariableMapping> outputVariableMappings = ruleTask.getOutputVariableMappings();
        if (inputVariableMappings != null &&
                outputVariableMappings != null) {
            setTaskId(inputVariableMappings, taskId);
            setTaskId(outputVariableMappings, taskId);
            variableMappingService.save(inputVariableMappings);
            variableMappingService.save(outputVariableMappings);
        }

        final ColumnMapping columnMapping = ruleTask.getColumnMapping();
        if (columnMapping != null) {
            columnMapping.setTaskId(taskId);
            columnMappingService.save(columnMapping);
        }

        // 自动授权
        authorityService.autoGrantAuthToCurrentUser(taskId, ResourceType.DATA_TASK);

        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional
    public void updateScanNextOffsetByPrimaryKey(RuleTask ruleTask) {
        if (ruleTask == null) {
            return;
        }
        ruleTask.setUpdateDate(new Date());
        daoHelper.update(_RULE_TASK_PREFIX + "updateScanNextOffsetByPrimaryKey", ruleTask);
    }

    @Override
    @Transactional
    public void updateScanNextRowKeyByPrimaryKey(RuleTask ruleTask) {
        if (ruleTask == null) {
            return;
        }
        ruleTask.setUpdateDate(new Date());
        daoHelper.update(_RULE_TASK_PREFIX + "updateScanNextRowKeyByPrimaryKey", ruleTask);
    }

    // 内部调用
    @Override
    @Transactional
    public ResponseResult updateTask(RuleTask ruleTask, String userId) {
        if (ruleTask == null) {
            return ResponseResult.createFailInfo("请求参数不能为null");
        }
        final String taskId = ruleTask.getTaskId();
        if (!isExists(ruleTask)) {
            return ResponseResult.createFailInfo("任务不存在， taskId: " + taskId);
        }
        ruleTask.setUpdateDate(new Date());
        ruleTask.setUpdatePerson(userId);
        daoHelper.update(_RULE_TASK_PREFIX + "updateByPrimaryKeySelective", ruleTask);

        return ResponseResult.createSuccessInfo();
    }

    // 页面调用
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult update(RuleTask ruleTask, String userId) {
        if (ruleTask == null) {
            return ResponseResult.createFailInfo("请求参数不能为null");
        }
        final String taskId = ruleTask.getTaskId();
        if (!isExists(ruleTask)) {
            return ResponseResult.createFailInfo("任务不存在， taskId: " + taskId);
        }

        if (checkNameIsExist(ruleTask.getTaskName(), taskId)) {
            return ResponseResult.createFailInfo("任务名称已存在");
        }

        ruleTask.setUpdateDate(new Date());
        ruleTask.setUpdatePerson(userId);
        convertSql(ruleTask);
        daoHelper.update(_RULE_TASK_PREFIX + "updateByPrimaryKeySelective", ruleTask);

        final List<VariableMapping> inputVariableMappings = ruleTask.getInputVariableMappings();
        final List<VariableMapping> outputVariableMappings = ruleTask.getOutputVariableMappings();

        if (inputVariableMappings != null &&
                outputVariableMappings != null) {
            variableMappingService.deleteByTaskId(taskId);
            setTaskId(inputVariableMappings, taskId);
            setTaskId(outputVariableMappings, taskId);
            variableMappingService.save(inputVariableMappings);
            variableMappingService.save(outputVariableMappings);
        }
        final ColumnMapping columnMapping = ruleTask.getColumnMapping();
        if (columnMapping != null) {
            columnMappingService.deleteByTaskId(taskId);
            columnMapping.setTaskId(taskId);
            columnMappingService.save(columnMapping);
        }
        return ResponseResult.createSuccessInfo();
    }

    // 真正的进行删除
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult delete(String taskId) {
        if (taskId == null) {
            return ResponseResult.createFailInfo("请求参数不能为null");
        }
        columnMappingService.deleteByTaskId(taskId);
        variableMappingService.deleteByTaskId(taskId);
        daoHelper.delete(_RULE_TASK_PREFIX + "deleteByPrimaryKey", taskId);
        return ResponseResult.createSuccessInfo();
    }

    // 真正的进行删除
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult delete(List<String> ids) {
        if (ids == null) {
            return ResponseResult.createFailInfo("请求参数不能为null");
        }
        columnMappingService.deleteByTaskIds(ids);
        variableMappingService.deleteByTaskIds(ids);
        daoHelper.delete(_RULE_TASK_PREFIX + "deleteByTaskIds", ids);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional
    public ResponseResult readyTask(RuleTask ruleTask, String userId) {
        if (ruleTask == null) {
            return ResponseResult.createFailInfo("请求参数不能为null");
        }
        ruleTask.setTaskStatus(RuleTask.READY);
        ruleTask.setResultDescribe("");
//        ruleTask.setScanNextOffset(0);
        updateTask(ruleTask, userId);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional
    public ResponseResult runTask(String taskId, String userId) {
        if (StringUtils.isBlank(taskId)) {
            return ResponseResult.createFailInfo("离线任务参数 [taskId] 不能为null");
        }
        final RuleTask ruleTask = selectByPrimaryKey(taskId);
        if (ruleTask == null) {
            return ResponseResult.createFailInfo("离线任务不存在，taskId: " + taskId);
        }
        return readyTask(ruleTask, userId);
    }

    @Override
    @Transactional
    public ResponseResult runningTask(RuleTask ruleTask, String userId) {
        if (ruleTask == null) {
            return ResponseResult.createFailInfo("请求参数不能为null");
        }
        ruleTask.setTaskStatus(RuleTask.RUNNING);
        updateTask(ruleTask, userId);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional
    public ResponseResult stoppingTask(RuleTask ruleTask, String userId) {
        if (ruleTask == null) {
            return ResponseResult.createFailInfo("请求参数不能为null");
        }
        ruleTask.setTaskStatus(RuleTask.STOPPING);
        updateTask(ruleTask, userId);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional
    public ResponseResult stopTask(RuleTask ruleTask, String userId) {
        if (ruleTask == null) {
            return ResponseResult.createFailInfo("请求参数不能为null");
        }
        ruleTask.setTaskStatus(RuleTask.STOP);
        updateTask(ruleTask, userId);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional
    public ResponseResult stopQuartzTask(String taskId, String userId) {
        if (StringUtils.isBlank(taskId)) {
            return ResponseResult.createFailInfo("停用离线任务参数 [taskId] 不能为null");
        }
        final RuleTask ruleTask = selectByPrimaryKey(taskId);
        if (ruleTask == null) {
            return ResponseResult.createFailInfo("离线任务不存在，taskId: " + taskId);
        }
        return stoppingTask(ruleTask, userId);
    }

    @Override
    @Transactional
    public ResponseResult exceptionTask(RuleTask ruleTask, Exception e, String userId) {
        String message = "";
        if (ruleTask == null) {
            return ResponseResult.createFailInfo("请求参数不能为null");
        }
        if (e != null) {
            message = e.getMessage();
        }
        ruleTask.setTaskStatus(RuleTask.EXCEPTION);
        ruleTask.setResultDescribe(message);
        updateTask(ruleTask, userId);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional
    public ResponseResult abnomalStopTask(RuleTask ruleTask, String userId) {
        if (ruleTask == null) {
            return ResponseResult.createFailInfo("请求参数不能为null");
        }
        ruleTask.setTaskStatus(RuleTask.ABNOMAL_STOP);
        updateTask(ruleTask, userId);
        return ResponseResult.createSuccessInfo();
    }

    // 软删除
    @Override
    @Transactional
    public ResponseResult deleteTask(String taskId, String userId) {
        if (taskId == null) {
            return ResponseResult.createFailInfo("请求参数不能为null");
        }
        final RuleTask ruleTask = selectByPrimaryKey(taskId);
        if (ruleTask == null) {
            return ResponseResult.createFailInfo("离线任务不存在，taskId: " + taskId);
        }
        ruleTask.setTaskStatus(RuleTask.DELETE);
        updateTask(ruleTask, userId);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult deleteTasks(List<String> ids, String userId) {
        if (ids == null) {
            return ResponseResult.createFailInfo("请求参数不能为null");
        }
        final List<RuleTask> ruleTasks = selectByPrimaryKeys(ids);
        for (RuleTask ruleTask : ruleTasks) {
            if (ruleTask != null && ruleTask.canModify()) {
                ruleTask.setTaskStatus(RuleTask.DELETE);
                ruleTask.setUpdateDate(new Date());
                ruleTask.setUpdatePerson(userId);
                updateTask(ruleTask, null);
            }
        }
        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional
    public void processStopTaskWithQuartz(RuleTask ruleTask) {
        final String taskId = ruleTask.getTaskId();
        final String taskStatus = ruleTask.getTaskStatus();

        if (RuleTask.DELETE.equals(taskStatus)) {
            delete(ruleTask.getTaskId());
        } else {
            try {
                if (RuleTask.STOPPING.equals(taskStatus)) {
                    if (taskService.isExistJob(taskId)) {
                        taskService.removeJob(taskId);
                    }
                    stopTask(ruleTask, null);
                }
                if (RuleTask.EXCEPTION.equals(taskStatus)) {
                    if (taskService.isExistJob(taskId)) {
                        taskService.removeJob(taskId);
                    }
                    abnomalStopTask(ruleTask, null);
                }

            } catch (SchedulerException e) {
                log.error(String.format("[CRE-%s] 停用定时任务失败", Thread.currentThread().getName()), e);
                exceptionTask(ruleTask, e, null);
            }
        }
    }

    private void setTaskId(List<VariableMapping> variableMappings, String taskId) {
        for (VariableMapping variableMapping : variableMappings) {
            variableMapping.setTaskId(taskId);
        }
    }

    private void convertSql(RuleTask ruleTask) {
        convertInputSql(ruleTask);
        convertOutputSql(ruleTask);
    }

    @Override
    @Transactional
    // insert 语句
    public String convertInserSql(RuleTask ruleTask, DataSource outputDataSource, Map<String, Object> data, Object keyColunmVal) {
        if (ruleTask == null) {
            throw new IllegalArgumentException("convertInserSql 参数[ruleTask]不能为null");
        }
        if (outputDataSource == null) {
            throw new IllegalArgumentException("convertInserSql 参数[outputDataSource]不能为null");
        }
        if (data == null) {
            throw new IllegalArgumentException("convertInserSql 参数[data]不能为null");
        }
        final String taskId = ruleTask.getTaskId();
        final List<VariableMapping> outputVariableMappings = variableMappingService.getOutputVariables(taskId);
        if (outputVariableMappings == null || outputVariableMappings.isEmpty()) {
            throw new IllegalStateException("当前任务的输出变量映射不存在，taskId: " + taskId);
        }

        // 添加输出表标识字段映射
        final ColumnMapping columnMapping = columnMappingService.selectByTaskId(taskId);
        if (outputVariableMappings == null) {
            throw new IllegalStateException("当前任务的输出表标识字段映射不存在，taskId: " + taskId);
        }
        final VariableMapping keyColumnMapping = new VariableMapping();
        keyColumnMapping.setVariableCode(columnMapping.getOutputColumnCode());
        keyColumnMapping.setColumnCode(columnMapping.getOutputColumnCode());
        List<VariableMapping> variableMappings = Lists.newLinkedList();
        variableMappings.add(keyColumnMapping);
        variableMappings.addAll(outputVariableMappings);

        final String outputTableId = ruleTask.getOutputTableId();
        final String outputTableCode = ruleTask.getOutputTableCode();
//        final MetaDataTable outputMetaDataTable = metaDataMgrService.selectTableByPrimaryKey(outputTableId);
//        final DataSource outputDataSource = metaDataMgrService.selectDbByTableId(outputTableId);
        final List<MetaDataColumn> outputMetaDataColumns = metaDataMgrService.selectAllColumnsByTableId(outputTableId);

        data.put(columnMapping.getOutputColumnCode(), keyColunmVal);

        final ISqlConvert iSqlConvert = SqlConvertFactory.getSqlConvertByDbType(outputDataSource.getDbType());

        final String insertSql = iSqlConvert.insert(outputTableCode, outputMetaDataColumns, data, variableMappings);
        return insertSql;
    }

    // 建表语句
    private void convertOutputSql(RuleTask ruleTask) {
        final List<VariableMapping> outputVariableMappings = ruleTask.getOutputVariableMappings();
        if (outputVariableMappings == null || outputVariableMappings.isEmpty()) {
            throw new IllegalArgumentException("构造写入sql失败，输出参数映射列表[outputVariableMappings]不能为空");
        }
        final String outputTableId = ruleTask.getOutputTableId();
        final String outputTableCode = ruleTask.getOutputTableCode();
        final MetaDataTable outputMetaDataTable = metaDataMgrService.selectTableByPrimaryKey(outputTableId);
        final DataSource outputDataSource = metaDataMgrService.selectDbByTableId(outputTableId);
        final List<MetaDataColumn> outputMetaDataColumns = metaDataMgrService.selectAllColumnsByTableId(outputTableId);

        if (outputMetaDataTable == null) {
            throw new IllegalStateException("元数据表不存在，tableId: " + outputTableId);
        }
        if (outputDataSource == null) {
            throw new IllegalStateException("数据源不存在，tableId: " + outputTableId);
        }

        final ISqlConvert iSqlConvert = SqlConvertFactory.getSqlConvertByDbType(outputDataSource.getDbType());

        String existTableSql = null;
        String createTabelSql = null;
        if (!outputMetaDataTable.isFromScan()) {    // 如果不是通过扫描创建的
            existTableSql = iSqlConvert.isExistTable(outputTableCode, outputDataSource.getDbUsername());
            createTabelSql = iSqlConvert.createTable(outputTableCode, outputMetaDataColumns);
        }

        ruleTask.setOutputDbId(outputDataSource.getDbId());
        ruleTask.setExistTableSql(existTableSql);
        ruleTask.setCreateTableSql(createTabelSql);
    }

    // select 语句
    private void convertInputSql(RuleTask ruleTask) {
        final List<VariableMapping> inputVariableMappings = ruleTask.getInputVariableMappings();
        final ColumnMapping columnMapping = ruleTask.getColumnMapping();
        if (inputVariableMappings == null || inputVariableMappings.isEmpty()) {
            throw new IllegalArgumentException("构造查询sql失败，输入参数映射列表[inputVariableMappings]不能为空");
        }
        if (columnMapping == null) {
            throw new IllegalArgumentException("构造查询sql失败，输入输出表字段映射[columnMapping]不能为空");
        }
        final String inputTableId = ruleTask.getInputTableId();
        final String inputTableCode = ruleTask.getInputTableCode();
        final MetaDataTable metaDataTable = metaDataMgrService.selectTableByPrimaryKey(inputTableId);
        final DataSource inputDataSource = metaDataMgrService.selectDbByTableId(inputTableId);
        if (metaDataTable == null) {
            throw new IllegalStateException("元数据表不存在，tableId: " + inputTableId);
        }
        if (inputDataSource == null) {
            throw new IllegalStateException("数据源不存在，tableId: " + inputTableId);
        }
        ruleTask.setInputDbId(inputDataSource.getDbId());

        if ("8".equals(inputDataSource.getDbType())) {  // 如果是hbase则不构造sql
            HbaseOperator.volidateCondition(ruleTask.getInputTableWhereClause());
            return;
        }

        final ISqlConvert iSqlConvert = SqlConvertFactory.getSqlConvertByDbType(inputDataSource.getDbType());

        // 构造输入表标识字段映射
        // table.id as "id"
        VariableMapping keyMapping = new VariableMapping();
        keyMapping.setColumnCode(columnMapping.getInputColumnCode());
        keyMapping.setTableCode(columnMapping.getInputTableCode());
        keyMapping.setVariableCode(columnMapping.getInputColumnCode());
        List<VariableMapping> variableMappings = Lists.newLinkedList();
        variableMappings.add(keyMapping);
        variableMappings.addAll(inputVariableMappings);

        // where clause  ( a.id = 1 [and|or b.id = 10] )
        final String inputTableWhereClause = ruleTask.getInputTableWhereClause();

        String selectSql;
        if (!metaDataTable.isRelationTable()) { // 如果不是关联表
            selectSql = iSqlConvert.select(variableMappings, inputTableCode);
        } else {
            final List<RelationTable> relationTables = dbResourceService.selectRelationTable(inputTableId);
            selectSql = iSqlConvert.selectWithJoin(variableMappings, relationTables);
        }

        final String selectWhere = iSqlConvert.selectWhere(selectSql, inputTableWhereClause);
        final String selectWithOrderBy = iSqlConvert.selectWithOrderBy(columnMapping, selectWhere);

        ruleTask.setInputSql(selectWithOrderBy);
    }

}
