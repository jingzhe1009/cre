package com.bonc.framework.api.core;

import com.alibaba.fastjson.JSON;
import com.bonc.framework.api.RuleApiFacade;
import com.bonc.framework.api.builder.IApiBuilder;
import com.bonc.framework.api.entity.CallApiRequest;
import com.bonc.framework.api.entity.CallApiResponse;
import com.bonc.framework.api.log.entity.ApiLog;
import com.bonc.framework.api.log.entity.ConsumerInfo;
import com.bonc.framework.util.IdUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractRuleApi implements IRuleApi, Serializable {


    /**
     *
     */
    private static final long serialVersionUID = 9059570700552496606L;

    private Log log = LogFactory.getLog(getClass());
    Logger logger = LogManager.getLogger(AbstractRuleApi.class);

    protected IApiBuilder apiBuilder;
    protected Map<String, Object> context = new ConcurrentHashMap<String, Object>();

    @Override
    public abstract String callApi(String param) throws Exception;

    public abstract CallApiResponse callApi(CallApiRequest request, CallApiResponse response) throws Exception;

    public void setBuilder(IApiBuilder apiBuilder) {
        this.apiBuilder = apiBuilder;
    }

    ;

    public void setContext(Map<String, Object> context) {
        this.context = context;
    }

    ;

    @Override
    public void cleanCache() {
        RuleApiCache.clear();
    }


    @Override
    public void before() {

    }

    @Override
    @Deprecated
    public void after(String param, String result, ConsumerInfo... consumerInfo) {
        String isLog = (String) this.context.get("isLog");
        if ("1".equals(isLog)) {
            String apiId = (String) this.context.get("apiId");

            final ApiLog apiLog = new ApiLog();
            apiLog.setApiId(apiId);
            apiLog.setLogId(IdUtil.createId());
            apiLog.setLogOccurtime(new Date());
            apiLog.setCallResult(result);
            apiLog.setLogParam(param);
            // 增加渠道号
            if (consumerInfo != null && consumerInfo.length == 1 && consumerInfo[0] != null) {
                apiLog.setConsumerId(consumerInfo[0].getConsumerId());
                apiLog.setServerId(consumerInfo[0].getServerId());
                apiLog.setConsumerSeqNo(consumerInfo[0].getConsumerSeqNo());
            }

            RuleApiFacade.getInstance().getApiLog().recordRuleLog(apiLog, true);
            if (log.isTraceEnabled()) {
                log.trace(apiLog);
            }
            // TODO: 记录AB测试 B模型日志
            Level verbose = Level.getLevel("VERBOSE");
            if (logger.isEnabled(verbose)) {
                logger.log(verbose, apiLog);
            }
        }
    }

    public void after(CallApiRequest request, CallApiResponse response) {
        String isLog = (String) this.context.get("isLog");
        if ("1".equals(isLog)) {
            String apiId = (String) this.context.get("apiId");
            String result = response.getResult();
            Map<String, Object> realParam = response.getRealParam();
            ConsumerInfo[] consumerInfo = request.getConsumerInfos();
            String ruleLogId = response.getRuleLogId();
            String sourceLogId = response.getSourceLogId();

            final ApiLog apiLog = new ApiLog();
            apiLog.setApiId(apiId);
            apiLog.setLogId(IdUtil.createId());
            apiLog.setRuleLogId(ruleLogId);
            apiLog.setSourceLogId(sourceLogId);
            apiLog.setLogOccurtime(new Date());
            apiLog.setCallResult(result);
            apiLog.setLogParam(JSON.toJSONString(realParam));
            // 增加渠道号
            if (consumerInfo != null && consumerInfo.length == 1 && consumerInfo[0] != null) {
                apiLog.setConsumerId(consumerInfo[0].getConsumerId());
                apiLog.setServerId(consumerInfo[0].getServerId());
                apiLog.setConsumerSeqNo(consumerInfo[0].getConsumerSeqNo());
            }
            // TODO: 记录AB测试 B模型日志
            String modelExecutionType = request.getModelExecutionType();
            if (modelExecutionType != null && modelExecutionType.equals(CallApiRequest.MODEL_EXECUTION_ABTEST)) {
                Level verbose = Level.getLevel("VERBOSE");
                if (logger.isEnabled(verbose)) {
                    logger.log(verbose, apiLog);
                }
            } else {
                RuleApiFacade.getInstance().getApiLog().recordRuleLog(apiLog, request.isLog());
                if (log.isTraceEnabled()) {
                    log.trace(apiLog);
                }
            }
        }
    }

}
