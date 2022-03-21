package com.bonc.frame.service.rulelog;

import com.bonc.frame.entity.rulelog.ModelTestLog;
import com.bonc.frame.util.ResponseResult;

import java.util.Map;

/**
 * @author yedunyao
 * @date 2019/11/11 9:43
 */
public interface ModelTestLogService {

    Map<String, Object> getModelTestLogList(String ruleName,
                                            String modelId,
                                            String operateType,
                                            String operateIsSuccess,
                                            String startDate, String endDate,
                                            String sort, String order,
                                            String start, String size);


    ResponseResult insert(ModelTestLog modelTestLog);

    ResponseResult updateStatus(String logId, String operateIsSuccess);

    ResponseResult trialSuccess(String logId);

    ResponseResult trialFailed(String logId);

    ResponseResult deleteRuleTrialLogByDay(String day);

}
