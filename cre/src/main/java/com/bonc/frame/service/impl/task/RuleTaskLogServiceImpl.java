package com.bonc.frame.service.impl.task;

import com.bonc.frame.dao.DaoHelper;
import com.bonc.frame.entity.task.RuleTaskLog;
import com.bonc.frame.service.task.RuleTaskLogService;
import com.bonc.frame.util.ResponseResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yedunyao
 * @date 2019/6/13 15:54
 */
@Service("ruleTaskLogService")
public class RuleTaskLogServiceImpl implements RuleTaskLogService {

    private Log log = LogFactory.getLog(RuleTaskLogServiceImpl.class);

    @Autowired
    private DaoHelper daoHelper;

    private final String _MAPPER_PREFIX = "com.bonc.frame.mapper.task.RuleTaskLogMapper.";

    private final String _SEND_FAILED_MAPPER_PREFIX = "com.bonc.frame.mapper.task.RuleTaskSendFailedMapper.";

    @Override
    public RuleTaskLog selectByPrimaryKey(String finalKey) {
        return (RuleTaskLog) daoHelper.queryOne(_MAPPER_PREFIX + "selectByFinalKey", finalKey);
    }

    @Override
    public boolean isExists(String finalKey) {
        final RuleTaskLog ruleTaskLog = selectByPrimaryKey(finalKey);
        if (ruleTaskLog != null) {
            if (RuleTaskLog.EXCEPTION.equals(ruleTaskLog.getStatus())) {
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isExists(RuleTaskLog ruleTaskLog) {
        return isExists(ruleTaskLog.getFinalKey());
    }

    @Override
    @Transactional
    public ResponseResult insert(RuleTaskLog ruleTaskLog) {
        if (ruleTaskLog == null) {
            return ResponseResult.createFailInfo("请求参数不能为null");
        }
        ruleTaskLog.setCreateDate(new Date());
        daoHelper.insert(_MAPPER_PREFIX + "insertSelective", ruleTaskLog);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional
    public ResponseResult update(RuleTaskLog ruleTaskLog) {
        if (ruleTaskLog == null) {
            return ResponseResult.createFailInfo("请求参数不能为null");
        }
        final String finalKey = ruleTaskLog.getFinalKey();
        if (!isExists(finalKey)) {
            return ResponseResult.createFailInfo("任务日志不存在， finalKey: " + finalKey);
        }
        ruleTaskLog.setUpdateDate(new Date());
        daoHelper.insert(_MAPPER_PREFIX + "updateByPrimaryKeySelective", ruleTaskLog);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional
    public ResponseResult upsert(RuleTaskLog ruleTaskLog) {
        if (ruleTaskLog == null) {
            return ResponseResult.createFailInfo("请求参数不能为null");
        }
        ruleTaskLog.setCreateDate(new Date());
        ruleTaskLog.setUpdateDate(new Date());
        daoHelper.insert(_MAPPER_PREFIX + "upsert", ruleTaskLog);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    public ResponseResult deleteByTaskId(String taskId) {
        if (taskId == null) {
            return ResponseResult.createFailInfo("请求参数不能为null");
        }
        daoHelper.delete(_MAPPER_PREFIX + "deleteByTaskId", taskId);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    public ResponseResult deleteByTaskIdListAndDay(List<String> taskIds, String day) {
        if (taskIds == null || taskIds.isEmpty()) {
            return ResponseResult.createFailInfo("请求参数不能为null");
        }
        Map<String, Object> param = new HashMap<>(1);
        param.put("taskIds", taskIds);
        param.put("day", day);
        daoHelper.delete(_MAPPER_PREFIX + "deleteByTaskIdListAndDay", param);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional
    public ResponseResult saveSendFailed(RuleTaskLog ruleTaskLog) {
        if (ruleTaskLog == null) {
            return ResponseResult.createFailInfo("请求参数不能为null");
        }
        ruleTaskLog.setCreateDate(new Date());
        daoHelper.insert(_SEND_FAILED_MAPPER_PREFIX + "insertSelective", ruleTaskLog);
        return ResponseResult.createSuccessInfo();
    }
}
