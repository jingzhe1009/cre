package com.bonc.frame.service.impl.monitor;

import com.bonc.frame.dao.DaoHelper;
import com.bonc.frame.util.IdUtil;
import com.bonc.frame.util.JsonUtils;
import com.bonc.frame.util.MapUtil;
import com.bonc.framework.rule.log.IRuleLog;
import com.bonc.framework.rule.log.entity.RuleLog;
import com.bonc.framework.rule.log.entity.RuleLogDetail;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

public class ModelLogImpl implements IRuleLog {

    @Autowired
    private DaoHelper daoHelper;

    private static final String _RULE_DETAIL = "com.bonc.frame.dao.rule.RuleDetailMapper.";

    @Override
    public void recordRuleLog(RuleLog ruleLog, boolean isLog) {

    }

    @Override
    public void recordRuleDetailLog(RuleLogDetail ruleLogDetail, boolean isLog) {

    }

    /**
     * 保存模型调用的日志
     * @param map
     */
    @Override
    public void saveModelLog(Map<String, String> map,RuleLog ruleLog) {
        com.bonc.frame.entity.monitor.RuleLog modelLog = new com.bonc.frame.entity.monitor.RuleLog();
        // 基础日志参数
        modelLog.setLogId(ruleLog.getLogId());
        modelLog.setRuleId(ruleLog.getRuleId());
        modelLog.setFolderId(ruleLog.getFolderId());
        modelLog.setState(ruleLog.getState());
        modelLog.setHitRuleNumber(ruleLog.getHitRuleNum());
        modelLog.setStartTime(ruleLog.getStartTime());
        modelLog.setEndTime(ruleLog.getEndTime());
        modelLog.setException(ruleLog.getException());
        modelLog.setInputData(ruleLog.getInputData());
        modelLog.setOutputData(ruleLog.getOutputData());
        modelLog.setConsumerId(ruleLog.getConsumerId());
        modelLog.setConsumerSeqNo(ruleLog.getConsumerSeqNo());
        modelLog.setUseTime(ruleLog.getEndTime().compareTo(ruleLog.getStartTime()));
        modelLog.setMethodType(ruleLog.getExecutionType());
        modelLog.setReturnCode(map.get("returnCode"));
        // 模型参数
        Object o = daoHelper.queryOne(_RULE_DETAIL + "getModelForLog", ruleLog.getRuleId());
        Map<String, Object> info = MapUtil.convertObjectToMap(o);
        info.remove(null);
        String ruletype = info.get("RULETYPE").toString();
        String version = info.get("VERSION").toString();
        if (null != ruletype && null != version) {
            modelLog.setModelType(ruletype);
            modelLog.setModelVersion(version);
            Map result = JsonUtils.stringToCollect(ruleLog.getOutputData());
            Object data = result.get("data");
            if (data != null) {
                // 返回数据不为空说明模型执行成功
                if (ruletype.equals("0")) {
                    // 评分模型
                    String score = MapUtil.convertObjectToMap(data).get("score").toString();
                    modelLog.setReturnScore(score);
                } else {
                    // 规则模型
                    String res = MapUtil.convertObjectToMap(data).get("result").toString();
                    modelLog.setReturnResult(res);
                }
            }
        }


    }
}
