package com.bonc.frame.service.task;

import com.bonc.frame.entity.task.RuleTaskLog;
import com.bonc.frame.util.ResponseResult;

import java.util.List;

/**
 * @author yedunyao
 * @date 2019/6/13 15:52
 */
public interface RuleTaskLogService {

    RuleTaskLog selectByPrimaryKey(String finalKey);

    boolean isExists(String finalKey);

    boolean isExists(RuleTaskLog ruleTaskLog);

    ResponseResult insert(RuleTaskLog ruleTaskLog);

    ResponseResult update(RuleTaskLog ruleTaskLog);

    ResponseResult upsert(RuleTaskLog ruleTaskLog);

    ResponseResult deleteByTaskId(String taskId);

    ResponseResult deleteByTaskIdListAndDay(List<String> taskIds, String day);

    ResponseResult saveSendFailed(RuleTaskLog ruleTaskLog);

}
