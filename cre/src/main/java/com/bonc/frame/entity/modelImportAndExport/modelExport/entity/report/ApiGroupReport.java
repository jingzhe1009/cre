package com.bonc.frame.entity.modelImportAndExport.modelExport.entity.report;

import com.bonc.frame.entity.api.ApiConf;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/2/12 10:55
 */
public class ApiGroupReport extends BaseReport {
    private String apiGroupName;
    private String apiGroupId;
    private String apiNumber;
    private Map<String, ApiReport> api;

    public void addApi(ApiConf apiConf) {
        if (apiConf != null) {
            String apiId = apiConf.getApiId();
            if (api == null) {
                api = new HashMap<>();
            }
            if (!api.containsKey(apiId)) {
                ApiReport apiReport = new ApiReport();
                apiReport.setApiId(apiId);
                apiReport.setApiDesc(apiConf.getApiDesc());
                apiReport.setApiName(apiConf.getApiName());
                api.put(apiId, apiReport);
            }
        }
    }

    @Override
    public String reportId() {
        return apiGroupId;
    }

    @Override
    public String reportName() {
        return apiGroupName;
    }

    public String getApiGroupName() {
        return apiGroupName;
    }

    public void setApiGroupName(String apiGroupName) {
        this.apiGroupName = apiGroupName;
    }

    public String getApiGroupId() {
        return apiGroupId;
    }

    public void setApiGroupId(String apiGroupId) {
        this.apiGroupId = apiGroupId;
    }

    public String getApiNumber() {
        return apiNumber;
    }

    public void setApiNumber(String apiNumber) {
        this.apiNumber = apiNumber;
    }

    public Map<String, ApiReport> getApi() {
        return api;
    }

    public void setApi(Map<String, ApiReport> api) {
        this.api = api;
    }
}
