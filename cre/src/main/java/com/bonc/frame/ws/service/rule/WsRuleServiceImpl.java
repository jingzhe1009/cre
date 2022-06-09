package com.bonc.frame.ws.service.rule;

import com.bonc.frame.dao.DaoHelper;
import com.bonc.frame.engine.EngineManager;
import com.bonc.frame.entity.monitor.RuleLog;
import com.bonc.frame.entity.rule.RuleDetailWithBLOBs;
import com.bonc.frame.service.monitor.MonitorService;
import com.bonc.frame.util.ConstantUtil;
import com.bonc.frame.util.DateFormatUtil;
import com.bonc.frame.util.IdUtil;
import com.bonc.frame.util.ResponseResult;
import com.bonc.framework.api.log.entity.ConsumerInfo;
import com.bonc.framework.rule.executor.context.impl.ExecutorResponse;
import com.bonc.framework.rule.executor.context.impl.ModelExecutorType;
import com.bonc.framework.util.DateUtil;
import com.bonc.framework.util.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author qxl
 * @version 1.0
 * @date 2018年5月9日 下午4:01:46
 */
@Service
public class WsRuleServiceImpl implements WsRuleService {

    private Log log = LogFactory.getLog(getClass());

    @Autowired
    private DaoHelper daoHelper;

    private final String _MYBITSID_PREFIX = "com.bonc.cre.rule.RuleDetailMapper.";

    @SuppressWarnings("unchecked")
    @Override
    public ResponseResult executeRule(String folderId, String ruleId, String paramStr,
                                      boolean isSkipValidation, String loaderKpiStrategyType, ConsumerInfo... consumerInfo) {
        if (StringUtils.isBlank(ruleId)) {
            throw new IllegalArgumentException("参数[ruleId]不能为空");
        }
        if (StringUtils.isBlank(folderId)) {
            throw new IllegalArgumentException("参数[folderId]不能为空");
        }

        Map<String, Object> param = null;
        try {
            param = JsonUtils.stringToCollect(paramStr);
        } catch (Exception e) {
            log.error(e);
        }
        if (param == null) {
            param = new HashMap<String, Object>();
        }
        try {
            ExecutorResponse executorResponse = EngineManager.getInstance().executeRule(folderId, ruleId, param,
                    isSkipValidation, true, loaderKpiStrategyType, ModelExecutorType.WS_INTERFACE, null, consumerInfo);
            if (executorResponse.isSuccessed()) {
                return ResponseResult.createSuccessInfo("执行成功", param.toString());
            }
            return ResponseResult.createFailInfo("执行异常.", executorResponse.getMessage());
        } catch (Exception e) {
            log.error(e);
            return ResponseResult.createFailInfo("执行异常.", e.getMessage());
        }
    }

    @Override
    public ResponseResult startRule(String folderId, String ruleId) {
        try {
            //修改数据库规则状态
            if (ruleId == null || ruleId.isEmpty()) {
                throw new Exception("The ruleId is null.");
            }
            EngineManager.getInstance().cleanRule(folderId, ruleId);
            RuleDetailWithBLOBs rule = new RuleDetailWithBLOBs();
            rule.setRuleId(ruleId);
            rule.setRuleStatus(ConstantUtil.RULE_STATUS_RUNNING);
            daoHelper.update(_MYBITSID_PREFIX + "updateByPrimaryKeySelective", rule);
            //编译规则
            EngineManager.getInstance().compileRule(folderId, ruleId);
        } catch (Exception e) {
            log.error(e);
            return ResponseResult.createFailInfo("启用失败.", e.getMessage());
        }
        return ResponseResult.createSuccessInfo("启用成功.");
    }

    @Override
    public ResponseResult stopRule(String folderId, String ruleId) {
        try {
            //修改数据库规则状态
            if (ruleId == null || ruleId.isEmpty()) {
                throw new Exception("The ruleId is null.");
            }
            RuleDetailWithBLOBs rule = new RuleDetailWithBLOBs();
            rule.setRuleId(ruleId);
            rule.setRuleStatus(ConstantUtil.RULE_STATUS_OVER);
            daoHelper.update(_MYBITSID_PREFIX + "updateByPrimaryKeySelective", rule);
            //清除缓存
            EngineManager.getInstance().cleanRule(folderId, ruleId);
        } catch (Exception e) {
            log.error(e);
            return ResponseResult.createFailInfo("停用失败.", e.getMessage());
        }
        return ResponseResult.createSuccessInfo("停用成功.");
    }

}
