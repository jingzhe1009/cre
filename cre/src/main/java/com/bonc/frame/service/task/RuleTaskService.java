package com.bonc.frame.service.task;

import com.bonc.frame.entity.datasource.DataSource;
import com.bonc.frame.entity.task.RuleTask;
import com.bonc.frame.util.ResponseResult;

import java.util.List;
import java.util.Map;

/**
 * @author yedunyao
 * @date 2019/5/28 17:34
 */
public interface RuleTaskService {

    Map<String, Object> getRuleTaskList(String taskName, String packageName,
                                        String ruleName, String ruleVersion,
                                        String taskStatus,
                                        String sort, String order,
                                        String start, String size);

    List<RuleTask> getRuleTaskByProperty(String taskId, String taskName, String packageName,
                                         String ruleName, String ruleVersion,
                                         String taskStatus, String isAllowedRepeat);

    List<RuleTask> getRuleTaskByPropertyNoAuth(String taskId, String taskName, String packageName,
                                               String ruleName, String ruleVersion,
                                               String taskStatus, String isAllowedRepeat);

    Map<String, Object> pagedRuleTaskResource(String taskName, String packageName,
                                              String ruleName, String ruleVersion,
                                              String start, String size);

    RuleTask selectByPrimaryKey(String taskId);

    List<RuleTask> selectByPrimaryKeys(List<String> taskIds);

    List<RuleTask> getAllReadyTask();

    List<RuleTask> getAllNeedRunTask();

    List<RuleTask> getAllNeedStopTask();

    List<RuleTask> getAllStopTask();

    List<RuleTask> getAllDeleteTask();

    List<RuleTask> getAllExceptionTask();

    List<RuleTask> getAllTaskByStatus(String taskStatus);

    ResponseResult detail(String taskId);

    boolean isExists(RuleTask task);

    boolean isExists(String taskId);

    ResponseResult save(RuleTask ruleTask, String userId);

    ResponseResult update(RuleTask ruleTask, String userId);

    ResponseResult updateTask(RuleTask ruleTask, String userId);

    void updateScanNextOffsetByPrimaryKey(RuleTask ruleTask);

    void updateScanNextRowKeyByPrimaryKey(RuleTask ruleTask);

    ResponseResult delete(String taskId);

    ResponseResult delete(List<String> ids);

    ResponseResult readyTask(RuleTask ruleTask, String userId);

    ResponseResult runTask(String taskId, String userId);

    ResponseResult runningTask(RuleTask ruleTask, String userId);

    ResponseResult stoppingTask(RuleTask ruleTask, String userId);

    ResponseResult stopTask(RuleTask ruleTask, String userId);

    ResponseResult stopQuartzTask(String taskId, String userId);

    ResponseResult exceptionTask(RuleTask ruleTask, Exception e, String userId);

    ResponseResult abnomalStopTask(RuleTask ruleTask, String userId);

    ResponseResult deleteTask(String taskId, String userId);

    ResponseResult deleteTasks(List<String> ids, String userId);

    void processStopTaskWithQuartz(RuleTask ruleTask);

    String convertInserSql(RuleTask ruleTask, DataSource outputDataSource, Map<String, Object> data, Object keyColunmVal);


}
