package com.bonc.frame.service.impl.rulelog;

import com.bonc.frame.dao.DaoHelper;
import com.bonc.frame.entity.rulelog.ModelTestLog;
import com.bonc.frame.service.reference.RuleReferenceService;
import com.bonc.frame.service.rulelog.ModelTestLogService;
import com.bonc.frame.util.CollectionUtil;
import com.bonc.frame.util.ConstantUtil;
import com.bonc.frame.util.JsonUtils;
import com.bonc.frame.util.ResponseResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author yedunyao
 * @date 2019/11/11 9:43
 */
@Service
public class ModelTestLogServiceImpl implements ModelTestLogService {

    @Autowired
    private DaoHelper daoHelper;

    @Autowired
    private RuleReferenceService ruleReferenceService;

    private final String _MYBITSID_PREFIX = "com.bonc.frame.mapper.rulelog.ModelTestLogMapper.";

    @Override
    public Map<String, Object> getModelTestLogList(String ruleName,
                                                   String modelId,
                                                   String operateType,
                                                   String operateIsSuccess,
                                                   String startDate, String endDate,
                                                   String sort, String order,
                                                   String start, String size) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("modelName", ruleName);
        param.put("modelId", modelId);
        param.put("operateType", operateType);
        param.put("operateIsSuccess", operateIsSuccess);
        param.put("startDate", startDate);
        param.put("endDate", endDate);
        param.put("sort", sort);
        param.put("order", order);
        Map<String, Object> result = daoHelper.queryForPageList(_MYBITSID_PREFIX + "getModelTestLogList",
                param, start, size);

        Map<String, Object> variableKpiCodeAndName = null;

        List<ModelTestLog> data = (List<ModelTestLog>) result.get("data");
        if (!CollectionUtil.isEmpty(data)) {
            for (ModelTestLog history : data) {
                if (history != null) {
                    if (variableKpiCodeAndName == null)
                        variableKpiCodeAndName = ruleReferenceService.getVariableKpiCodeAndName(ruleName, modelId);

                    // 汉化内容和结果
                    replaceOperateContentAndResult(variableKpiCodeAndName, history);

                    String operatePerson = history.getOperatePerson();
                    if (StringUtils.isNotBlank(operatePerson)) {
                        int index = operatePerson.indexOf("$");
                        if (index >= 0) {
                            String realUser = operatePerson.substring(index + 1);
                            history.setOperatePerson(realUser);
                        }
                    }
                }
            }
        }

        return result;
    }

    /**
     * 汉化内容和结果 -- 将内容和结果的key替换为中文名
     *
     * @param variableKpiCodeAndName
     * @param modelTestLog
     */
    public void replaceOperateContentAndResult(Map<String, Object> variableKpiCodeAndName, ModelTestLog modelTestLog) {
        modelTestLog.setOperateContent(replaceJsonStringKey(variableKpiCodeAndName, modelTestLog.getOperateContent()));
        modelTestLog.setOperateResult(replaceJsonStringKey(variableKpiCodeAndName, modelTestLog.getOperateResult()));
    }

    /**
     * 将JsonString的key替换为中文名
     *
     * @param variableKpiCodeAndName
     * @param jsonObjectString
     * @return
     */
    public String replaceJsonStringKey(Map<String, Object> variableKpiCodeAndName, String jsonObjectString) {
        Map map = JsonUtils.stringToMap(jsonObjectString);
        ArrayList arrayList = new ArrayList<>(map.keySet());
        for (Object key : arrayList) {
            if (variableKpiCodeAndName.containsKey(key)) {
                Object value = map.get(key);
                Object newKey = variableKpiCodeAndName.get(key);
                map.put(newKey, value);
                map.remove(key);
            }
        }
        return JsonUtils.toJSONString2(map);
    }

    @Override
    @Transactional
    public ResponseResult insert(ModelTestLog modelTestLog) {
        modelTestLog.setOperateTime(new Date());
        daoHelper.insert(_MYBITSID_PREFIX + "insertSelective", modelTestLog);
        ResponseResult result = ResponseResult.createSuccessInfo();
        return result;
    }

    @Override
    @Transactional
    public ResponseResult updateStatus(String logId, String operateIsSuccess) {
        Map<String, String> param = new HashMap<>(2);
        param.put("logId", logId);
        param.put("operateIsSuccess", operateIsSuccess);
        daoHelper.update(_MYBITSID_PREFIX + "updateStatusByPrimaryKey", param);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional
    public ResponseResult trialSuccess(String logId) {
        return updateStatus(logId, ConstantUtil.TRIAL_SUCCESS);
    }

    @Override
    @Transactional
    public ResponseResult trialFailed(String logId) {
        return updateStatus(logId, ConstantUtil.TRIAL_FAILED);
    }

    @Override
    public ResponseResult deleteRuleTrialLogByDay(String day) {
        Map<String, Object> param = new HashMap<>(1);
        param.put("day", day);
        daoHelper.update(_MYBITSID_PREFIX + "deleteRuleTrialLogByDay", param);
        return ResponseResult.createSuccessInfo();
    }
}
