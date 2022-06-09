package com.bonc.framework.rule.executor.context.impl;

import com.alibaba.fastjson.JSON;
import com.bonc.framework.api.log.entity.ApiLog;
import com.bonc.framework.api.log.entity.ConsumerInfo;
import com.bonc.framework.rule.constant.RuleEngineConstant;
import com.bonc.framework.rule.kpi.KpiConstant;
import com.bonc.framework.rule.kpi.KpiLog;
import com.bonc.framework.rule.resources.RuleResource;
import com.bonc.framework.rule.util.RuleEnginePropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yedunyao
 * @since 2019/12/9 10:56
 */
public class ExecutorRequest implements Cloneable {

    /**
     * 调用场景
     *  测试
     *      输入参数 不跳过参数校验
     *      输出参数
     *  试算
     *      输入参数 不跳过参数校验
     *      输出参数 执行明细  logId
     *  离线跑批
     *      输入参数 跳过参数校验
     *  外部调用
     */

    private String modelLogId;
    /**
     * 编译后的模型
     */
    private RuleResource executable;

    /**
     * 场景id
     */
    private String folderId;

    /**
     * 模型id
     */
    private String ruleId;
    /**
     * 来源模型Id
     */
    private String sourceModelId;

    /**
     * 模型需要的输入参数，包括指标
     */
    private Map<String, Object> param;

    /**
     * 模型执行方式
     * 1:测试  2:试算  3:A/B测试  4:离线任务  5:外部服务调用(ws接口)
     */
    private ModelExecutorType modelExecutionType = ModelExecutorType.TEST;

    /**
     * 是否跳过参数校验，由参数校验拦截器实现
     */
    private boolean isSkipValidation;

    /**
     * 是否仅返回输出参数
     */
    private boolean isOnlyOutput;

    /**
     * 是否为测试，如果是非测试，需要检验模型是否启用
     */
    private boolean isTest;

    /**
     * 是否加载指标，试算时不需要加载指标
     */
    private boolean isLoadKpi;
    /**
     * 加载指标的策略类型
     */
    private String loaderKpiStrategyType = RuleEnginePropertiesUtil.getProperty(
            RuleEngineConstant.EXECUTOR_LOADER_KPI_STRATEGY_TYPE, KpiConstant.LOADER_KPI_STRATEGY_TYPE_RUNTIME_EVERY);
//    private String loaderKpiStrategyType = KpiConstant.LOADER_KPI_STRATEGY_TYPE_RUNTIME_EVERY;
//    private String loaderKpiStrategyType = KpiConstant.LOADER_KPI_STRATEGY_TYPE_COMPILE;
    /**
     * 是否返回执行详情
     */
    private boolean traceDetail;


    /**
     * 是否记录日志
     */
    private boolean isLog = true;
    /**
     * 日志Id
     */
    private String ruleLogId;

    /**
     * 调用的渠道信息
     */
    private ConsumerInfo[] consumerInfos;

    // -------  ABTest -----
    private String abTestId;

    private String sourceLogId;

    private String sourceRuleLog;

    private Map<String, KpiLog> sourceKpiLogMap;

    private Map<String, ApiLog> sourceApiLogMap;

    public ExecutorRequest() {
    }

    public Map<String, KpiLog> getSourceKpiLogMap() {
        return sourceKpiLogMap;
    }

    public void setSourceKpiLogMap(Map<String, KpiLog> sourceKpiLogMap) {
        this.sourceKpiLogMap = sourceKpiLogMap;
    }

    public void addSourceKpiLog(KpiLog sourceKpiLog) {
        if (sourceKpiLog == null) {
            return;
        }
        if (sourceKpiLogMap == null) {
            this.sourceKpiLogMap = new HashMap<>();
        }
        String key = sourceKpiLog.getKpiId() + "_" + JSON.toJSONString(sourceKpiLog.getInputData());
        this.sourceKpiLogMap.put(key, sourceKpiLog);
    }

    public void addSourceKpiLogList(List<KpiLog> sourceKpiLogList) {
        if (CollectionUtils.isEmpty(sourceKpiLogList)) {
            return;
        }
        for (KpiLog kpiLog : sourceKpiLogList) {
            addSourceKpiLog(kpiLog);
        }
    }

    public KpiLog getSourceKpi(String kpiId, Map<String, Object> param) {
        if (sourceKpiLogMap != null) {
            String key = kpiId + "_" + JSON.toJSONString(param);
            return sourceKpiLogMap.get(key);
        }
        return null;
    }

    public Map<String, ApiLog> getSourceApiLogMap() {
        return sourceApiLogMap;
    }

    public void setSourceApiLogMap(Map<String, ApiLog> sourceApiLogMap) {
        this.sourceApiLogMap = sourceApiLogMap;
    }


    public void addSourceApiLog(ApiLog sourceApiLog) {
        if (sourceApiLog == null) {
            return;
        }
        if (sourceApiLogMap == null) {
            this.sourceApiLogMap = new HashMap<>();
        }
        String key = sourceApiLog.getApiId() + "_" + sourceApiLog.getLogParam();
        this.sourceApiLogMap.put(key, sourceApiLog);
    }

    public void addSourceApiLogList(List<ApiLog> sourceApiLogList) {
        if (CollectionUtils.isEmpty(sourceApiLogList)) {
            return;
        }
        for (ApiLog kpiLog : sourceApiLogList) {
            addSourceApiLog(kpiLog);
        }
    }

    public ApiLog getApiLog(String apiId, Map<String, Object> param) {
        if (sourceApiLogMap != null) {
            String key = apiId + "_" + JSON.toJSONString(param);
            return sourceApiLogMap.get(key);
        }
        return null;
    }


    public RuleResource getExecutable() {
        return executable;
    }

    public void setExecutable(RuleResource executable) {
        this.executable = executable;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public Map<String, Object> getParam() {
        return param;
    }

    public void setParam(Map<String, Object> param) {
        this.param = param;
    }

    public boolean isSkipValidation() {
        return isSkipValidation;
    }

    public void setSkipValidation(boolean skipValidation) {
        isSkipValidation = skipValidation;
    }

    public boolean isOnlyOutput() {
        return isOnlyOutput;
    }

    public void setOnlyOutput(boolean onlyOutput) {
        isOnlyOutput = onlyOutput;
    }

    public boolean isTest() {
        return isTest;
    }

    public void setTest(boolean test) {
        isTest = test;
    }

    public boolean isLoadKpi() {
        return isLoadKpi;
    }

    public void setLoadKpi(boolean loadKpi) {
        isLoadKpi = loadKpi;
    }

    public boolean isTraceDetail() {
        return traceDetail;
    }

    public void setTraceDetail(boolean traceDetail) {
        this.traceDetail = traceDetail;
    }

    public boolean isLog() {
        return isLog;
    }

    public void setLog(boolean log) {
        isLog = log;
    }

    public String getRuleLogId() {
        return ruleLogId;
    }

    public void setRuleLogId(String ruleLogId) {
        this.ruleLogId = ruleLogId;
    }

    public String getSourceLogId() {
        return sourceLogId;
    }

    public void setSourceLogId(String sourceLogId) {
        this.sourceLogId = sourceLogId;
    }

    public ConsumerInfo[] getConsumerInfos() {
        return consumerInfos;
    }

    public void setConsumerInfos(ConsumerInfo[] consumerInfos) {
        this.consumerInfos = consumerInfos;
    }

    public String getAbTestId() {
        return abTestId;
    }

    public void setAbTestId(String abTestId) {
        this.abTestId = abTestId;
    }

    public String getLoaderKpiStrategyType() {
        return loaderKpiStrategyType;
    }

    public String getSourceModelId() {
        return sourceModelId;
    }

    public void setSourceModelId(String sourceModelId) {
        this.sourceModelId = sourceModelId;
    }

    public String getSourceRuleLog() {
        return sourceRuleLog;
    }

    public void setSourceRuleLog(String sourceRuleLog) {
        this.sourceRuleLog = sourceRuleLog;
    }

    public ModelExecutorType getModelExecutionType() {
        return modelExecutionType;
    }

    public void setModelExecutionType(ModelExecutorType modelExecutionType) {
        this.modelExecutionType = modelExecutionType;
    }

    public void setLoaderKpiStrategyType(String loaderKpiStrategyType) {
        if (StringUtils.isBlank(loaderKpiStrategyType)) {
            return;
        }
        this.loaderKpiStrategyType = loaderKpiStrategyType;
    }

    @Override
    public ExecutorRequest clone() throws CloneNotSupportedException {
        ExecutorRequest clone = (ExecutorRequest) super.clone();
        clone.setParam(cloneMap(param));
        return clone;
    }

    // map 复制
    private Map<String, Object> cloneMap(Map<String, Object> param) {
        Map<String, Object> result = new HashMap<>(param.size());
        for (Map.Entry<String, Object> entry : param.entrySet()) {
            // 因为输入的参数都是基本类型，直接put即可
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public String getModelLogId() {
        return modelLogId;
    }

    public void setModelLogId(String modelLogId) {
        this.modelLogId = modelLogId;
    }
}
