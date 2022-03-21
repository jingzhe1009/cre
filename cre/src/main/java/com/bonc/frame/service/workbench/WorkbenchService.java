package com.bonc.frame.service.workbench;

import java.util.List;
import java.util.Map;

import com.bonc.frame.entity.rulelog.RuleLog;
import com.bonc.frame.entity.rulelog.RuleLogDetail;

public interface WorkbenchService {

    List<Map<String, Object>> ruleExecuteAll(int day);

    List<Map<String, Object>> ruleExecuteAllSuccess(int day);

    List<Map<String, Object>> ruleExecuteAllFail(int day);

    List<Map<String, Object>> ruleHitRate(int day);

    List<Map<String, Object>> folderRuleNum(int day);

    List<Map<String, Object>> ruleExecuteAllByFolder(String folderId, int day);

    Map<String, Object> queryExecuteRule(RuleLog ruleLog, String start, String size);

    List<RuleLogDetail> ruleDetailList(String logId);


    List<Map<String, Object>> ruleExecuteAll(int day, String folderId, String deptId);

    List<Map<String, Object>> ruleExecuteAllSuccess(int day, String folderId, String deptId);

    List<Map<String, Object>> ruleExecuteAllFail(int day, String folderId, String deptId);

    List<Map<String, Object>> ruleExecuteGroupByTimeAndState(int day, String folderId, String deptId, String state);

    List<Map<String, Object>> selectHitRateByDays(int day, String folderId, String deptId);
}
