package com.bonc.frame.engine.builder;

import com.bonc.frame.entity.api.ApiConf;
import com.bonc.frame.util.JsonUtils;
import com.bonc.framework.api.RuleApiFacade;
import com.bonc.framework.rule.exception.LoadContextException;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ApiEngineBuilder extends AbstractEngineBuilder {
    Log log = LogFactory.getLog(getClass());

    private final String _mybitsId = "com.bonc.frame.engine.mapper.ApiMapper.";

    @Override
    public void build(String folderId, String ruleId, boolean isTest) throws Exception {
        List<ApiConf> list = new ArrayList<>();
        try {
            list = this.getDaoHelper().queryForList(_mybitsId + "selectApiProperties", folderId);

            // 获取模型引用的公共接口
            List<ApiConf> pubList = this.getDaoHelper().queryForList(_mybitsId + "selecPubApiProperties", folderId);
            list.addAll(pubList);

            //调用接口引擎build接口方法
            RuleApiFacade.getInstance().getRuleApiBuilder()
                    .buildAllRuleApiMapper()
                    .setContexts(apiListToContextList(list, folderId))
                    .buildRuleApi();
        } catch (Exception e) {
            log.error("加载模型引用的接口失败。" + e.getMessage(), e);
            throw new LoadContextException("加载模型引用的接口失败,失败原因:[" + e.getMessage() + "]", e);
        }
    }

    public List<Map<String, Object>> apiListToContextList(List<ApiConf> list, String folderId) {
        List<Map<String, Object>> contextList = new ArrayList<>();
        if (list == null || list.size() == 0) {
            return contextList;
        }
        for (ApiConf apiConf : list) {
            String apiId = apiConf.getApiId();
            String apiType = apiConf.getApiType();
            String isLog = apiConf.getIsLog();
            String apiContent = apiConf.getApiContent();
//			String folderId = apiConf.getFolderId();
            Map<String, Object> apiMap = JsonUtils.stringToCollect(apiContent);
            apiMap.put("apiId", apiId);
            apiMap.put("apiType", apiType);
            apiMap.put("isLog", isLog);
            apiMap.put("folderId", folderId);
            contextList.add(apiMap);
        }
        return contextList;
    }

}
