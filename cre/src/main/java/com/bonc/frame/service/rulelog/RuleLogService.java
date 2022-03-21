package com.bonc.frame.service.rulelog;

import com.bonc.frame.util.ResponseResult;

import java.util.Map;

/**
 * @author yedunyao
 * @date 2019/5/8 16:03
 */
public interface RuleLogService {

    Map<String, Object> getRuleLogList(String folderId, String ruleId,
                                       String ruleName, String isDraft,
                                       String state,
                                       String startDate, String endDate,
                                       String sort, String order,
                                       String start, String size);

    Map<String, Object> getRuleLogDetail(String modelTrialLogId, String nodeType, String start, String size);

    ResponseResult deleteRuleLogByDay(String day);

    ResponseResult deleteRuleLogDetailByDay(String day);
}
