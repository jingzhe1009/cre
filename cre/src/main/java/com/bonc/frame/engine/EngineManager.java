package com.bonc.frame.engine;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bonc.frame.config.Config;
import com.bonc.frame.dao.DaoHelper;
import com.bonc.frame.engine.builder.AbstractEngineBuilder;
import com.bonc.frame.entity.function.RuleFunction;
import com.bonc.frame.ext.MyClassLoader;
import com.bonc.frame.service.aBTest.ABTestService;
import com.bonc.frame.service.kpi.KpiService;
import com.bonc.frame.service.variable.VariableService;
import com.bonc.frame.util.MapBeanUtil;
import com.bonc.frame.util.SpringUtils;
import com.bonc.frame.util.StringUtil;
import com.bonc.framework.api.log.entity.ApiLog;
import com.bonc.framework.api.log.entity.ConsumerInfo;
import com.bonc.framework.entity.format.VariableFormat;
import com.bonc.framework.rule.RuleEngineFactory;
import com.bonc.framework.rule.exception.LoadContextException;
import com.bonc.framework.rule.exception.NoCompiledRuleException;
import com.bonc.framework.rule.executor.context.impl.ExecutorRequest;
import com.bonc.framework.rule.executor.context.impl.ExecutorResponse;
import com.bonc.framework.rule.executor.context.impl.ModelExecutorType;
import com.bonc.framework.rule.kpi.KpiLog;
import com.bonc.framework.rule.resources.flow.basicflow.FlowNodeExecutionProcess;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 核心引擎管理器
 * 规则编译、启用、停用等操作都是通过此类完成
 *
 * @author qxl
 * @version 1.0
 * @date 2018年4月2日 上午10:29:04
 */
public class EngineManager {

    private static Log log = LogFactory.getLog(EngineManager.class);

    /**
     * 存放每个文件夹的函数class
     */
    private Map<String, Map<String, Object>> funClassMap = new ConcurrentHashMap<String, Map<String, Object>>();
    /**
     * 存放每个文件夹的系统常量
     */
    private Map<String, Map<String, Object>> sysConstantsMap = new ConcurrentHashMap<String, Map<String, Object>>();
    /**
     * 存放每个文件夹的输出变量编码
     */
    private Map<String, Map<String, Object>> outputVarMap = new ConcurrentHashMap<String, Map<String, Object>>();
    /**
     * 存放每个文件夹所有规则IDlist
     */
    private Map<String, List<String>> ruleIdListMap = new ConcurrentHashMap<String, List<String>>();

    //存放引擎builder
    private List<AbstractEngineBuilder> engineBuilderList = new Vector<AbstractEngineBuilder>();

    private DaoHelper daoHelper;

    private KpiService kpiService;

    private VariableService variableService;

    private ABTestService abTestService;

    private static EngineManager engineManager = new EngineManager();

    private EngineManager() {
        this.daoHelper = (DaoHelper) SpringUtils.getBean("daoHelper");
        this.kpiService = (KpiService) SpringUtils.getBean("kpiService");
        this.variableService = (VariableService) SpringUtils.getBean("variableService");
        this.abTestService = (ABTestService) SpringUtils.getBean(ABTestService.class);
        EngineMode.initEngine(daoHelper, engineBuilderList);
    }

    public static EngineManager getInstance() {
        return engineManager;
    }

    /**
     * 编译规则
     *
     * @param folderId
     * @param ruleId
     * @throws Exception
     */
    public void compileRule(String folderId, String ruleId) throws Exception {
        build(folderId, ruleId, false);
    }

    // 从数据库中加载规则
    private void build(String folderId, String ruleId, boolean isTest) throws Exception {
        try {
            //build
            for (AbstractEngineBuilder engineBuilder : engineBuilderList) {
                //解除方法级并发，将锁加入单个规则包中
                if (!engineBuilder.isBuild(folderId, ruleId)) {
                    synchronized (engineBuilder) {
                        if (engineBuilder.isBuild(folderId, ruleId)) {
                            continue;
                        }
                        engineBuilder.setDaoHelper(daoHelper);
                        engineBuilder.builder(folderId, ruleId, isTest);
                    }
                }

            }
        } catch (Exception e) {
            this.cleanRule(folderId, ruleId);
            throw e;
        }

    }

    //build-function
    private void loadFunction(String folderId, String ruleId) throws Exception {
        if (funClassMap.containsKey(folderId)) {
            return;
        }
        log.debug("执行模型--加载执行环境--加载函数");
        //解除方法锁，添加并发锁
        synchronized (funClassMap) {
            if (funClassMap.containsKey(folderId)) {
                return;
            }
            Map<String, Object> classMap = new ConcurrentHashMap<String, Object>();
            final String _mybitsId = "com.bonc.frame.engine.mapper.RuleEngineMapper.";
            final String FUN_PREFIX = "f_";
            try {
                List<RuleFunction> funList = this.daoHelper.queryForList(_mybitsId + "getFunList", folderId);
                for (RuleFunction fun : funList) {
                    String functionId = fun.getFunctionId();
                    String classType = fun.getClassType();
                    String classPath = fun.getFunctionClassPath();
                    String className = fun.getClassName();
                    if ("2".equals(classType)) {//系统外部的类
                        Class<?> clazz = null;
                        clazz = new MyClassLoader(classPath).loadClass(className);
                        Object obj = clazz.newInstance();
                        classMap.put(FUN_PREFIX + functionId, obj);
                    }
                }
                funClassMap.put(folderId, classMap);
            } catch (Exception e) {
                throw new LoadContextException("加载模型引用函数失败。失败原因:[" + e.getMessage() + "]", e);
            }
        }
    }

    //build-system constants
    private void loadSysConstant(String folderId, String ruleId) throws Exception {
        if (sysConstantsMap.containsKey(ruleId)) {
            return;
        }
        log.debug("执行模型--加载执行环境--加载系统常量");
        //解除方法锁，添加并发锁
        synchronized (sysConstantsMap) {
            if (sysConstantsMap.containsKey(ruleId)) {
                return;
            }
            Map<String, Object> constantsMap = new HashMap<>();
            final String _mybitsId = "com.bonc.frame.engine.mapper.RuleEngineMapper.";
            try {
                //update by jxw  修改将 输入参数、输出参数、中间变量、系统变量全部加载，并赋予初始值
                List<Map<String, Object>> sysConstantList = this.daoHelper.queryForList(_mybitsId + "getSysConstantsByFolderId", folderId);
                for (Map<String, Object> map : sysConstantList) {
                    String variableCode = (String) map.get("variableCode");
                    Object defaultValue = map.get("defaultValue");
                    String typeId = (String) map.get("typeId");
                    if (variableCode != null) {
                        //如果是数字类型的变量则统一转换成double   update by jxw
                        constantsMap.put(variableCode, VariableFormat.convertValue(typeId, variableCode, defaultValue));
                    }
                }
                // 添加模型引用的系统常量,并赋予默认值
                List<Map<String, Object>> sysConstantByRule = variableService.getVariableMapByRuleId(ruleId, "K4");
                for (Map<String, Object> map : sysConstantByRule) {
                    String variableCode = (String) map.get("variableCode");
                    Object defaultValue = map.get("defaultValue");
                    String typeId = (String) map.get("typeId");
                    if (variableCode != null) {
                        //如果是数字类型的变量则统一转换成double   update by jxw
                        constantsMap.put(variableCode, VariableFormat.convertValue(typeId, variableCode, defaultValue));
                    }
                }

                if (log.isDebugEnabled()) {
                    log.debug(String.format("执行模型--加载执行环境--加载系统变量[folderId: %s, constantsMap: %s]", folderId, constantsMap.toString()));
                }
                sysConstantsMap.put(ruleId, constantsMap);
            } catch (Exception e) {
                throw new LoadContextException("加载模型引用的系统常量失败。失败原因:[" + e.getMessage() + "]", e);
            }
        }
    }


    //build-loadOutputVar
    private void loadOutputVar(String folderId, String ruleId) throws Exception {
        if (outputVarMap.containsKey(folderId)) {
            return;
        }
        log.debug("执行模型--加载执行环境--加载输出变量");
        synchronized (outputVarMap) {
            if (outputVarMap.containsKey(folderId)) {
                return;
            }
            Map<String, Object> varMap = new ConcurrentHashMap<String, Object>();
            final String _mybitsId = "com.bonc.frame.engine.mapper.RuleEngineMapper.";
            try {
                List<Map<String, Object>> outputVarList = this.daoHelper.queryForList(_mybitsId +
                        "getOutputVar", folderId);

                // FIXME: 获取场景下模型引用的所有非实体公共参数 还要包括接口引用的 SQL优化
                //  获取场景下模型引用的所有非实体公共参数
                List<Map<String, Object>> pubVariables = this.daoHelper.queryForList(_mybitsId +
                        "getPubFlatVarByFolderId", folderId);

                List<Map<String, Object>> pubVariablesInApi = this.daoHelper.queryForList(_mybitsId +
                        "getPubFlatVarInApiByFolderId", folderId);

                outputVarList.addAll(pubVariables);
                outputVarList.addAll(pubVariablesInApi);

                for (Map<String, Object> map : outputVarList) {
                    String variableCode = (String) map.get("variableCode");
                    if (variableCode != null) {
                        varMap.put(variableCode, "--");
                    }
                }
                outputVarMap.put(folderId, varMap);
            } catch (Exception e) {
                throw new LoadContextException("加载模型引用的输出参数失败。失败原因:[" + e.getMessage() + "]", e);
            }
        }
    }

    /**
     * 执行规则
     *
     * @param folderId
     * @param ruleId
     * @param param
     * @throws Exception
     */
    public ExecutorResponse executeRule(String folderId, String ruleId, Map<String, Object> param, ConsumerInfo... consumerInfo) throws Exception {
        return this.executeRule(folderId, ruleId, param, false, true, false, null, null, null, null, consumerInfo);
    }

    public ExecutorResponse executeRule(String folderId, String ruleId,
                                        Map<String, Object> param,
                                        boolean isSkipValidation,
                                        boolean isLoadKpi, String loaderKpiStrategyType, ModelExecutorType modelExecutorType,
                                        String sourceRuleLog, ConsumerInfo... consumerInfo) throws Exception {
        return this.executeRule(folderId, ruleId, param, false, isSkipValidation, isLoadKpi, loaderKpiStrategyType,
                modelExecutorType, null, sourceRuleLog, consumerInfo);
    }

    public ExecutorResponse executeRule(String folderId, String ruleId, Map<String, Object> param, boolean isTest,
                                        boolean isSkipValidation, boolean isLoadKpi, String loaderKpiStrategyType,
                                        ModelExecutorType modelExecutionType, String abTestId, String sourceRuleLog,
                                        ConsumerInfo... consumerInfo) throws Exception {

//        this.executeRule(folderId, ruleId, param, isTest, true, isSkipValidation, isLoadKpi, consumerInfo);
        ExecutorRequest executorRequest = new ExecutorRequest();
        executorRequest.setFolderId(folderId);
        executorRequest.setRuleId(ruleId);
        executorRequest.setModelExecutionType(modelExecutionType);
        executorRequest.setParam(param);
        executorRequest.setLog(true);
        executorRequest.setTest(isTest);
        executorRequest.setOnlyOutput(true);
        executorRequest.setSkipValidation(isSkipValidation);
        executorRequest.setTraceDetail(true);
        executorRequest.setLoadKpi(isLoadKpi);
        executorRequest.setConsumerInfos(consumerInfo);
        executorRequest.setLoaderKpiStrategyType(loaderKpiStrategyType);
        executorRequest.setAbTestId(abTestId);
        executorRequest.setSourceRuleLog(sourceRuleLog);
        return this.executeRule(executorRequest);
    }

    public ExecutorResponse executeRule(String folderId, String ruleId, Map<String, Object> param, boolean isTest,
                                        boolean isSkipValidation, boolean isLoadKpi, String loaderKpiStrategyType,
                                        ModelExecutorType modelExecutionType, String abTestId, String sourceRuleLog,
                                        String sourceModelId,
                                        ConsumerInfo... consumerInfo) throws Exception {

//        this.executeRule(folderId, ruleId, param, isTest, true, isSkipValidation, isLoadKpi, consumerInfo);
        ExecutorRequest executorRequest = new ExecutorRequest();
        executorRequest.setFolderId(folderId);
        executorRequest.setRuleId(ruleId);
        executorRequest.setModelExecutionType(modelExecutionType);
        executorRequest.setParam(param);
        executorRequest.setLog(true);
        executorRequest.setTest(isTest);
        executorRequest.setOnlyOutput(true);
        executorRequest.setSkipValidation(isSkipValidation);
        executorRequest.setTraceDetail(true);
        executorRequest.setLoadKpi(isLoadKpi);
        executorRequest.setConsumerInfos(consumerInfo);
        executorRequest.setLoaderKpiStrategyType(loaderKpiStrategyType);
        executorRequest.setAbTestId(abTestId);
        executorRequest.setSourceRuleLog(sourceRuleLog);
        executorRequest.setSourceModelId(sourceModelId);
        return this.executeRule(executorRequest);
    }


    public ExecutorResponse executeRule(ExecutorRequest executorRequest) throws Exception {
        String folderId = executorRequest.getFolderId();
        String ruleId = executorRequest.getRuleId();
        boolean isTest = executorRequest.isTest();
        boolean isOnlyOutput = executorRequest.isOnlyOutput();
        boolean isLoadKpi = executorRequest.isLoadKpi();
        ConsumerInfo[] consumerInfo = executorRequest.getConsumerInfos();
        ModelExecutorType modelExecutionType = executorRequest.getModelExecutionType();

        if (ModelExecutorType.WS_INTERFACE == modelExecutionType) {
            String switchRule = abTestService.getSwitchRule(ruleId);
            if (StringUtils.isNotBlank(switchRule)) {
                log.info("A/B无缝切换:A:[" + ruleId + "],B:[" + switchRule + "]");
                executorRequest.setRuleId(switchRule);
                executorRequest.setSourceModelId(ruleId);
                ruleId = switchRule;
            }
        }
        if (ModelExecutorType.ABTEST == modelExecutionType) {
            String sourceRuleLog = executorRequest.getSourceRuleLog();
            if (StringUtils.isNotBlank(sourceRuleLog)) {
                paseRuleLogToObject(sourceRuleLog, executorRequest);
            }
        }
        Map<String, Object> param = executorRequest.getParam();

        if (param == null) {
            param = new HashMap<>(1);
        }
        try {
            // 加载规则到缓存中
            build(folderId, ruleId, isTest);
            loadFunction(folderId, ruleId);
            loadSysConstant(folderId, ruleId);
//            // 加载KPI的参数值
//            if (isLoadKpi) {
//                loadKpiVarByKpiDefinitionList(folderId, ruleId, param, consumerInfo);
//            }
            loadOutputVar(folderId, ruleId);

        } catch (Exception e) {
            log.error("编译模型异常。" + e.getMessage(), e);
            return ExecutorResponse.newFailedExecutorResponse("编译模型异常:" + e.getMessage());
        }
        try {
            // 设置规则上下文环境context
            Map<String, Object> funClassMap = this.funClassMap.get(folderId);
            if (funClassMap != null && funClassMap.size() > 0) {
                param.putAll(funClassMap);
            }
            Map<String, Object> sysConstantsMap = this.sysConstantsMap.get(ruleId);
            if (sysConstantsMap != null && sysConstantsMap.size() > 0) {
                param.putAll(sysConstantsMap);
            }

            if (log.isDebugEnabled()) {
                String debugInfo = String.format("执行模型--模型上下文环境[folderId: %s, param: %s]", folderId, param);
                log.debug(debugInfo);
            }

            // 执行规则
            log.debug(String.format("执行模型--执行单个模型[ruleId: %s, folderId: %s]", ruleId, folderId));
            ExecutorResponse executorResponse = RuleEngineFactory.getRuleEngine().execute(executorRequest);
            if (!executorResponse.isSuccessed()) {
                return executorResponse;
            }

            log.debug("执行模型--执行结束--处理输出结果");

            //移除输入变量，只保存输出变量值
            Map<String, Object> outputVarMap = this.outputVarMap.get(folderId);

            if (log.isDebugEnabled()) {
                String debugInfo = String.format("执行模型--执行结果[folderId: %s, ruleId: %s, param: %s]", folderId, ruleId, param);
                log.debug(debugInfo);
            }

            if (isOnlyOutput && outputVarMap != null && outputVarMap.size() > 0 && param != null && param.size() > 0) {
                Iterator<Map.Entry<String, Object>> it = param.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, Object> entry = it.next();
                    if ((!outputVarMap.containsKey(entry.getKey())))  //  || (entry.convertValue() == null || "".equals(entry.convertValue())) 要求显示空值
                        it.remove();//使用迭代器的remove()方法删除元素
                }
            }

            executorResponse.setResult(param);

            log.debug(String.format("执行模型--最终结果[folderId: %s, ruleId: %s, param: %s]", folderId, ruleId, param));
            return executorResponse;
        } catch (NoCompiledRuleException e1) {
            log.error(e1);
            //重新编译--防止缓存过期
            this.cleanRule(folderId, ruleId);
            build(folderId, ruleId, isTest);
            return ExecutorResponse.newFailedExecutorResponse("模型编译失败，模型未编译");
        }

    }


    /**
     * 测试规则
     *
     * @param folderId
     * @param ruleId
     * @param param
     * @throws Exception
     */
    public ExecutorResponse testRule(String folderId, String ruleId, Map<String, Object> param,
                                     boolean isSkipValidation,
                                     boolean isLoadKpi, String loaderKpiStrategyType, ModelExecutorType modelExecutorType) throws Exception {
        try {
            this.cleanRule(folderId, ruleId);
            // 渠道号
            final ConsumerInfo consumerInfo = new ConsumerInfo(Config.LOG_RULE_TEST_CONSUMERID, null, null);
            ExecutorResponse executorResponse = this.executeRule(folderId, ruleId, param, true, isSkipValidation,
                    isLoadKpi, loaderKpiStrategyType, modelExecutorType, null, null, consumerInfo);
            this.cleanRule(folderId, ruleId);
            return executorResponse;
        } catch (Exception e) {
            log.error(e);
            throw e;
        }
    }

    /**
     * 试算规则
     *
     * @param folderId
     * @param ruleId
     * @param param
     * @throws Exception
     */
    public ExecutorResponse trailRule(String folderId, String ruleId,
                                      Map<String, Object> param,
                                      boolean isSkipValidation,
                                      boolean isLoadKpi, String loaderKpiStrategyType,
                                      ModelExecutorType modelExecutorType) throws Exception {

        this.cleanRule(folderId, ruleId);
        // 渠道号
        final ConsumerInfo consumerInfo = new ConsumerInfo(Config.LOG_RULE_TRAIL_CONSUMERID, null, null);
        ExecutorRequest executorRequest = new ExecutorRequest();
        executorRequest.setFolderId(folderId);
        executorRequest.setRuleId(ruleId);
        executorRequest.setModelExecutionType(modelExecutorType);
        executorRequest.setParam(param);
        executorRequest.setLog(true);
        executorRequest.setTest(true);
        executorRequest.setOnlyOutput(true);
        executorRequest.setSkipValidation(isSkipValidation);
        executorRequest.setTraceDetail(true);
        executorRequest.setLoadKpi(isLoadKpi);
        executorRequest.setLoaderKpiStrategyType(loaderKpiStrategyType);
        executorRequest.setConsumerInfos(new ConsumerInfo[]{consumerInfo});
        ExecutorResponse executorResponse = this.executeRule(executorRequest);
        this.cleanRule(folderId, ruleId);
        return executorResponse;

    }

    /**
     * 清除已经编译好的规则
     *
     * @param folderId
     * @param ruleId
     */
    public void cleanRule(String folderId, String ruleId) {
        for (AbstractEngineBuilder engineBuilder : engineBuilderList) {
            engineBuilder.clean(folderId, ruleId);
        }
        this.funClassMap.remove(folderId);//移除函数
        this.sysConstantsMap.remove(ruleId);//移除函数
        this.outputVarMap.remove(folderId);
        this.ruleIdListMap.remove(folderId);
    }

    /**
     * 清除文件夹中所有已经编译好的规则
     *
     * @param folderId
     */
    public void cleanRule(String folderId) {

    }

    /**
     * 添加引擎builder
     *
     * @param engineBuilder
     */
    public void addEngineBuilder(AbstractEngineBuilder engineBuilder) {
        if (!engineBuilderList.contains(engineBuilder)) {
            engineBuilderList.add(engineBuilder);
        }
    }


    public void paseRuleLogToObject(String ruleLogString, ExecutorRequest request) {
        Map<String, String> ruleLogMap = StringUtil.stringToMap(ruleLogString);

        if (ruleLogMap == null || ruleLogMap.isEmpty()) {
            return;
        }
        // 去掉单引号
        for (String ruleLogProperty : ruleLogMap.keySet()) {
            String ruleLogValue = ruleLogMap.get(ruleLogProperty);
            if (ruleLogValue != null && ruleLogValue.length() > 2 && ruleLogValue.startsWith("'") && ruleLogValue.endsWith("'")) {
                ruleLogValue = ruleLogValue.substring(1, ruleLogValue.length() - 1);
                ruleLogMap.put(ruleLogProperty, ruleLogValue);
            }
        }
        // sourceLogId
        String sourceLogId = ruleLogMap.get("logId");
        request.setSourceLogId(sourceLogId);
        // 输入参数
        String inputData = ruleLogMap.get("inputData");
        JSONObject inputDataJson = JSON.parseObject(inputData);
        request.setParam(inputDataJson);
        log.debug("从历史日志中获取到的输入参数:" + JSON.toJSONString(inputDataJson));
//        RuleLog ruleLog = MapBeanUtil.convertMap2Object(RuleLog.class, ruleLogMap);
        // 指标日志,接口日志
        String detailLogString = ruleLogMap.get("detail");
        if (StringUtils.isBlank(detailLogString)) {
            return;
        }
        Map<String, List<String>> logDetailStringListMap = StringUtil.stringToList(detailLogString); // 返回的是  {type:[{}]}
        if (logDetailStringListMap == null || logDetailStringListMap.isEmpty()) {
            return;
        }
        for (String objectType : logDetailStringListMap.keySet()) {
            if (StringUtils.isBlank(objectType)) {
                continue;
            }
            List<String> logDetailStringList = logDetailStringListMap.get(objectType);
            if (logDetailStringList == null || logDetailStringList.isEmpty()) {
                continue;
            }
            for (String logDetailString : logDetailStringList) {
                Map<String, String> logDetailObjectMap = StringUtil.stringToMap(logDetailString);
                switch (objectType) {
                    case "RuleLogDetail":
                        String executionProcess1 = logDetailObjectMap.get("executionProcess");
                        FlowNodeExecutionProcess flowNodeExecutionProcess = JSONObject.parseObject(executionProcess1, FlowNodeExecutionProcess.class);
                        if (flowNodeExecutionProcess != null) {
                            List<KpiLog> kpiLog = flowNodeExecutionProcess.getKpiLog();
                            request.addSourceKpiLogList(kpiLog);
//                            log.debug("从历史日志中获取到的指标日志:" + JSON.toJSONString(kpiLog));
                        }
                        break;
                    case "ApiLog":
                        ApiLog apiLog = MapBeanUtil.convertMap2Object(ApiLog.class, logDetailObjectMap);
                        request.addSourceApiLog(apiLog);
//                        log.debug("从历史日志中获取到的指标日志:" + JSON.toJSONString(apiLog));
                        break;
                }
            }
        }
    }


    /**
     * 执行规则
     *
     * @param folderId
     * @param ruleId
     * @param param
     * @throws Exception
     */
    @Deprecated
    public void executeRule(String folderId, String ruleId, Map<String, Object> param,
                            boolean isTest, boolean isOnlyOutput,
                            boolean isSkipValidation, boolean isLoadKpi,
                            ConsumerInfo... consumerInfo) throws Exception {
        if (param == null) {
            param = new HashMap<>(1);
        }
        try {
            // 加载模型执行上下文到缓存中
            build(folderId, ruleId, isTest);
            /**
             * 暂不实现函数功能
             *
             * @since 2.0
             */
//            loadFunction(folderId, ruleId);
            loadSysConstant(folderId, ruleId);
            // 加载KPI的参数值
//            if (isLoadKpi) {
//                loadKpiVar(folderId, ruleId, param, consumerInfo);
//            }

            loadOutputVar(folderId, ruleId);
            try {
                // 设置规则上下文环境context
                Map<String, Object> funClassMap = this.funClassMap.get(folderId);
                if (funClassMap != null && funClassMap.size() > 0) {
                    param.putAll(funClassMap);
                }
                Map<String, Object> sysConstantsMap = this.sysConstantsMap.get(ruleId);
                if (sysConstantsMap != null && sysConstantsMap.size() > 0) {
                    param.putAll(sysConstantsMap);
                }

                if (log.isDebugEnabled()) {
                    String debugInfo = String.format("执行模型--模型上下文环境[folderId: %s, param: %s]", folderId, param);
                    log.debug(debugInfo);
                }

                // 执行规则
                log.info(String.format("执行模型--执行单个模型[ruleId: %s, folderId: %s]", ruleId, folderId));
                RuleEngineFactory.getRuleEngine().execute(ruleId, param, isSkipValidation, consumerInfo);

                log.debug("执行模型--执行结束--处理输出结果");

                //移除输入变量，只保存输出变量值
                Map<String, Object> outputVarMap = this.outputVarMap.get(folderId);

                if (log.isDebugEnabled()) {
                    String debugInfo = String.format("执行模型--执行结果[folderId: %s, ruleId: %s, param: %s]", folderId, ruleId, param);
                    log.debug(debugInfo);
                }

                if (isOnlyOutput && outputVarMap != null && outputVarMap.size() > 0 && param != null && param.size() > 0) {
                    Iterator<Map.Entry<String, Object>> it = param.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry<String, Object> entry = it.next();
                        if ((!outputVarMap.containsKey(entry.getKey())))  //  || (entry.convertValue() == null || "".equals(entry.convertValue())) 要求显示空值
                            it.remove();//使用迭代器的remove()方法删除元素
                    }
                }

                log.info(String.format("执行模型--最终结果[folderId: %s, ruleId: %s, param: %s]", folderId, ruleId, param));
            } catch (NoCompiledRuleException e1) {
                log.error(e1);
                //重新编译--防止缓存过期
                this.cleanRule(folderId, ruleId);
                build(folderId, ruleId, isTest);
            }
        } catch (Exception e) {
            throw e;
        }
    }
}
