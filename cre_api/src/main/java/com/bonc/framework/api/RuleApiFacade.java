package com.bonc.framework.api;

import com.bonc.framework.api.builder.IApiBuilder;
import com.bonc.framework.api.builder.ParamApiBuilder;
import com.bonc.framework.api.cache.DefaultApiCache;
import com.bonc.framework.api.cache.IApiCache;
import com.bonc.framework.api.core.IRuleApi;
import com.bonc.framework.api.entity.CallApiRequest;
import com.bonc.framework.api.entity.CallApiResponse;
import com.bonc.framework.api.exception.CallApiException;
import com.bonc.framework.api.log.DefaultApiLog;
import com.bonc.framework.api.log.IApiLog;
import com.bonc.framework.api.log.entity.ApiLog;
import com.bonc.framework.api.log.entity.ConsumerInfo;
import com.bonc.framework.util.JsonUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

public class RuleApiFacade {
    //apiMap key:apiID,value:接口执行器,提前加载,如果有添加或修改只可添加即可
//	private Map<String, IRuleApi> apiMap = new HashMap<String, IRuleApi>();
    private IApiCache<String, IRuleApi> apiCache;
    //接口构造器
    private IApiBuilder apiBuilder;
    private IApiLog apiLog;

    Log log = LogFactory.getLog(getClass());
    //

    public RuleApiFacade() {
        apiLog = new DefaultApiLog();
        apiCache = new DefaultApiCache<>();
    }

    private static RuleApiFacade ruleApiFacade;

    public static RuleApiFacade getInstance() {
        if (ruleApiFacade == null) {
            ruleApiFacade = new RuleApiFacade();
        }
        return ruleApiFacade;
    }

    public void put(String key, IRuleApi value) {
        apiCache.put(key, value);
    }

    public void clean(String key) {
        if (apiCache.containsKey(key)) {
            apiCache.remove(key);
        }
    }

    @Deprecated
    public String callRuleApi(String apiId, String param, ConsumerInfo... consumerInfo) throws Exception {
        if (!apiCache.containsKey(apiId)) throw new CallApiException("apiId " + apiId + " is not userful");
        CallApiResponse response = callRuleApi(apiId, param, null, true, null, null, consumerInfo);
        return response.getResult();
    }

    public CallApiResponse callRuleApi(String apiId, String param, String ruleLogId, boolean isLog, String modelExecutionType,
                                       Map<String, ApiLog> sourceApiLogMap, ConsumerInfo... consumerInfo) throws Exception {
        if (!apiCache.containsKey(apiId)) throw new CallApiException("apiId " + apiId + " is not userful");
        CallApiRequest request = new CallApiRequest();
        request.setApiId(apiId);
        request.setLog(isLog);
        request.setParam(JsonUtils.stringToCollect(param));
        request.setRuleLogId(ruleLogId);
        request.setModelExecutionType(modelExecutionType);
        request.setConsumerInfos(consumerInfo);
        request.setSourceApiLogMap(sourceApiLogMap);
        return callRuleApi(apiId, request);
    }

    public CallApiResponse callRuleApi(String apiId, CallApiRequest request) throws Exception {
        if (request == null) {
            request = new CallApiRequest();
        }
        CallApiResponse response = request.newCallApiResponse();

        if (!apiCache.containsKey(apiId)) {
            throw new CallApiException("apiId " + apiId + " is not userful");
        }
        IRuleApi ruleApi = apiCache.get(apiId);
        ruleApi.before();
        try {
            response = ruleApi.callApi(request, response);
        } finally {
            ruleApi.after(request, response);
        }
        return response;
    }

    public IApiBuilder getRuleApiBuilder() {
        if (apiBuilder == null) {
            apiBuilder = new ParamApiBuilder();
        }
        return apiBuilder;
    }

    public IApiLog getApiLog() {
        return apiLog;
    }

    public void setApiLog(IApiLog apiLog) {
        this.apiLog = apiLog;
    }

    public IApiCache<String, IRuleApi> getApiCache() {
        return apiCache;
    }

    public void setApiCache(IApiCache<String, IRuleApi> apiCache) {
        this.apiCache = apiCache;
    }

}
