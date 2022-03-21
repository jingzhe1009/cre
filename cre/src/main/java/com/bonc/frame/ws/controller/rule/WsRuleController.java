package com.bonc.frame.ws.controller.rule;

import com.bonc.frame.util.ResponseResult;
import com.bonc.frame.ws.service.rule.WsRuleService;
import com.bonc.framework.api.log.entity.ConsumerInfo;
import com.bonc.framework.rule.kpi.KpiConstant;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 规则相关webservice接口
 *
 * @author qxl
 * @version 1.0
 * @date 2018年4月17日 上午10:25:58
 */
@Controller
@RequestMapping("/ws/rule")
public class WsRuleController {
    Log log = LogFactory.getLog(getClass());

    @Autowired
    private WsRuleService wsRuleService;

    //执行规则
    @RequestMapping("/executeRule")
    @ResponseBody
    public ResponseResult executeRule(String folderId, String ruleId, String paramStr, boolean isOnlyOutput,
                                      @RequestParam(required = false, defaultValue = "true") boolean isSkipValidation,
                                      @RequestParam(required = false) String loaderKpiStrategyType,
                                      String consumerId, String serverId, String consumerSeqNo) {
        // 新增: 渠道号 consumerId渠道标识，serverId接口标识，consumerSeqNo流水号
        final ConsumerInfo consumerInfo = new ConsumerInfo(consumerId, serverId, consumerSeqNo);

        long start = System.currentTimeMillis();
        log.info("面向外部服务接口--执行模型");
        ResponseResult result = wsRuleService.executeRule(folderId, ruleId, paramStr, isSkipValidation, loaderKpiStrategyType, consumerInfo);
        //EngineManager.getInstance().testRule(folderId, ruleId, paramMap, isSkipValidation, true);
        //  return ResponseResult.createSuccessInfo("end", JsonUtils.collectToString(paramMap));
        long end = System.currentTimeMillis();
        log.info("面向外部服务接口--执行模型[用时:" + (end - start) + "ms]");
        return result;
    }

    //启用单个规则
    @RequestMapping("/startRule")
    @ResponseBody
    public ResponseResult startRule(String folderId, String ruleId) {
        ResponseResult result = wsRuleService.startRule(folderId, ruleId);
        return result;
    }

    //停用单个规则
    @RequestMapping("/stopRule")
    @ResponseBody
    public ResponseResult stopRule(String folderId, String ruleId) {
        ResponseResult result = wsRuleService.stopRule(folderId, ruleId);
        return result;
    }
}
