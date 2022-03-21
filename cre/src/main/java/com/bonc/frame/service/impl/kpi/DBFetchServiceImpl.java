package com.bonc.frame.service.impl.kpi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bonc.frame.entity.datasource.DataSource;
import com.bonc.frame.entity.kpi.KpiDefinition;
import com.bonc.frame.entity.kpi.KpiFetchLimiters;
import com.bonc.frame.entity.task.VariableMapping;
import com.bonc.frame.exception.FetchException;
import com.bonc.frame.module.db.DbOperatorFactory;
import com.bonc.frame.module.db.operator.hbase.HbaseOperator;
import com.bonc.frame.service.kpi.FetchType;
import com.bonc.frame.service.metadata.DBMetaDataMgrService;
import com.bonc.frame.util.CollectionUtil;
import com.bonc.frame.util.ResponseResult;
import com.bonc.framework.api.log.entity.ConsumerInfo;
import com.bonc.framework.entity.format.VariableFormat;
import com.bonc.framework.rule.executor.context.impl.ExecutorRequest;
import com.bonc.framework.rule.executor.context.impl.ModelExecutorType;
import com.bonc.framework.rule.kpi.KpiResult;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author yedunyao
 * @date 2019/10/28 11:59
 */
@Service
public class DBFetchServiceImpl extends AbstractFetchServiceImpl {


    @Autowired
    private DBMetaDataMgrService dbMetaDataMgrService;

    @Override
    public boolean isSupport(KpiDefinition kpiDefinition) {
        Preconditions.checkArgument(kpiDefinition != null,
                "参数[kpiDefinition]不能为空");
        if (getSupport().getValue().equals(kpiDefinition.getFetchType())) {
            return true;
        }
        return false;
    }

    @Override
    public FetchType getSupport() {
        return FetchType.DB;
    }

    @Override
    public KpiResult getKpiValue(KpiDefinition kpiDefinition, Map<String, Object> params, ExecutorRequest executorRequest) {
        // 从历史日志中获取指标值
        if (ModelExecutorType.ABTEST == executorRequest.getModelExecutionType()) {
            KpiResult kpiResultFromSourceKpiLog = getKpiResultFromSourceKpiLog(kpiDefinition, executorRequest);
            if (kpiResultFromSourceKpiLog != null) {
                return kpiResultFromSourceKpiLog;
            }
        }
        // 历史日志中不存在, 执行
        KpiResult kpiResult = new KpiResult();

        ConsumerInfo[] consumerInfo = null;
        String ruleLogId = null;
        if (executorRequest != null) {
            consumerInfo = executorRequest.getConsumerInfos();
            ruleLogId = executorRequest.getRuleLogId();
        }
        log.info("指标[" + kpiDefinition.getKpiId() + "]数据源方式获取指标值");
        if (!isSupport(kpiDefinition)) {
            throw new IllegalArgumentException("不支持该类型");
        }
        Object resultData = null;
        String dbType = kpiDefinition.getDbType();
        String kpiDefaultValue = kpiDefinition.getKpiDefaultValue();

        ResponseResult responseResult;
        try {
            if ("8".equals(dbType)) {  // hbase
                VariableMapping keyMapping = new VariableMapping();
                keyMapping.setColumnCode(kpiDefinition.getKpiValueSourceCode());
                keyMapping.setTableCode(kpiDefinition.getTableCode());
                keyMapping.setVariableCode(kpiDefinition.getKpiCode());
                keyMapping.setVariableTypeId(kpiDefinition.getKpiType());
                List<VariableMapping> variableMappings = Lists.newLinkedList();
                variableMappings.add(keyMapping);

                String rowKey = getHbaseRowKey(kpiDefinition, params); // 获取行键值

                if (rowKey == null) {
                    throw new FetchException("指标[" + kpiDefinition.getKpiId() + "]获取指标[" + kpiDefinition.getKpiCode() + "]值失败," +
                            "从hbase中获取参数值失败: 行键值为空");
                }

                HbaseOperator hbaseOperator = new HbaseOperator(kpiDefinition.getDbId());
                responseResult = hbaseOperator.get(kpiDefinition.getTableCode(), rowKey, variableMappings, null);
                log.info("指标[" + kpiDefinition.getKpiId() + "]通过HBASE获取到的结果responseResult : " + responseResult);
            } else {
                final DataSource dataSource = dbMetaDataMgrService.selectDbByTableId(kpiDefinition.getTableId());
                String fetchSql = convertFetchSql(kpiDefinition, params, kpiResult); // 构建sql 将sql中的 {aaa} ==> aaaValue
                responseResult = DbOperatorFactory.execute(fetchSql, dataSource);
            }
        } catch (Exception e) {
            log.warn("指标[" + kpiDefinition.getKpiId() + "]执行sql异常,指标名称：" + kpiDefinition.getKpiCode(), e);
//            return kpiDefaultValue;
            throw new FetchException("指标[" + kpiDefinition.getKpiId() + "]执行sql异常,指标名称：" + kpiDefinition.getKpiCode(), e);
        }
        // 处理结果
        List<Map<String, Object>> resultMapList = (List<Map<String, Object>>) responseResult.getData();
        // 如果查到的值为空,返回默认值
        if (CollectionUtil.isEmpty(resultMapList)) {
            log.warn("指标[" + kpiDefinition.getKpiId() + "]从数据源中获取的结果为空," +
                    "指标[" + kpiDefinition.getKpiCode() + "]值赋予默认值[" + kpiDefaultValue + "]");
            resultData = kpiDefaultValue;

            Map<String, Object> outputData = new HashMap<>();
            outputData.put(kpiDefinition.getKpiCode(), VariableFormat.convertValue(kpiDefinition.getKpiType(), kpiDefinition.getKpiCode(), kpiDefaultValue));

            BeanUtils.copyProperties(kpiDefinition, kpiResult);
            kpiResult.setSuccessLog(ruleLogId, null, kpiResult.getInputData(), outputData);
        } else if (resultMapList.size() == 1) {  // 正常返回
            resultData = resultMapList.get(0).get(kpiDefinition.getKpiCode());
            Map<String, Object> outputData = new HashMap<>();
            outputData.put(kpiDefinition.getKpiCode(), VariableFormat.convertValue(kpiDefinition.getKpiType(), kpiDefinition.getKpiCode(), resultData));

            BeanUtils.copyProperties(kpiDefinition, kpiResult);
            kpiResult.setSuccessLog(ruleLogId, null, kpiResult.getInputData(), outputData);
        } else { // 查到多个值,返回异常
            log.error("指标[" + kpiDefinition.getKpiId() + "]指标的fetchSql获取获取到的值不唯一");
            throw new FetchException("指标[" + kpiDefinition.getKpiCode() + "]获取到的值不唯一");
        }
        log.debug("指标[" + kpiDefinition.getKpiId() + "]指标[" + kpiDefinition.getKpiCode() + "]值赋予值[" + resultData + "]");

        return kpiResult;
    }


    @Override
    // TODO 添加判断 保证所有的指标的都是从数据源获取
    public List<KpiResult> getKpiValueBatch(List<KpiDefinition> kpiDefinitionList, Map<String, Object> params, ExecutorRequest executorRequest) {

        List<KpiResult> results = new ArrayList<>(kpiDefinitionList.size());
        // 从历史指标日志中获取
        if (ModelExecutorType.ABTEST == executorRequest.getModelExecutionType()) {
            List<KpiResult> kpiResultFromSourceKpiLogBatch = getKpiResultFromSourceKpiLogBatch(kpiDefinitionList, executorRequest);
            if (kpiResultFromSourceKpiLogBatch != null && !kpiResultFromSourceKpiLogBatch.isEmpty()) {
                results.addAll(kpiResultFromSourceKpiLogBatch);
            }
            if (CollectionUtils.isEmpty(kpiDefinitionList)) {
                return results;
            }
        }

        Map<String, List<KpiDefinition>> dbKpi = sortDBForKpi(kpiDefinitionList);

        for (String allSourceKey : dbKpi.keySet()) {
            List<KpiDefinition> kpiDefinitionDBS = dbKpi.get(allSourceKey);
            List<KpiResult> kpiValueBatchBySourceId = getKpiValueBatchBySourceId(kpiDefinitionDBS, params, allSourceKey, executorRequest);
            if (kpiValueBatchBySourceId != null && !kpiValueBatchBySourceId.isEmpty()) {
                results.addAll(kpiValueBatchBySourceId);
            }
        }
        return results;
    }

    /**
     * @param kpiDefinitionList
     * @param params
     * @param allSourceKey      allSourceKey=dbId_aaa_tableId_bbb_condition_[{columnId_ddd_variableId_eee}{...}]
     * @return
     */
    private List<KpiResult> getKpiValueBatchBySourceId(List<KpiDefinition> kpiDefinitionList, Map<String, Object> params,
                                                       String allSourceKey, ExecutorRequest executorRequest) {
        List<KpiResult> restlts = new ArrayList<>(kpiDefinitionList.size());

        StringBuilder kpiIdStrBuilder = new StringBuilder();
        StringBuilder kpiCodeStrBuilder = new StringBuilder();
        for (KpiDefinition defaultV : kpiDefinitionList) {
            kpiIdStrBuilder.append(defaultV.getKpiId()).append(",");
            kpiCodeStrBuilder.append(defaultV.getKpiCode()).append(",");
        }
        kpiIdStrBuilder.deleteCharAt(kpiIdStrBuilder.length() - 1);
        kpiCodeStrBuilder.deleteCharAt(kpiCodeStrBuilder.length() - 1);
        String kpiIdsStr = kpiIdStrBuilder.toString();
        String kpiCodeStr = kpiCodeStrBuilder.toString();
        log.info("指标[" + kpiIdsStr + "]数据源方式获取指标值");

        //执行sql获取数据
        KpiResult batchkpiResult = executeSql(kpiDefinitionList, params, kpiIdsStr, kpiCodeStr);

        Map<String, Object> executeSqlResults = batchkpiResult.getOutputData();
        Map<String, Object> kpiInputData = batchkpiResult.getInputData();
        String ruleLogId = null;
        if (executorRequest != null) {
            ruleLogId = executorRequest.getRuleLogId();
        }

        for (KpiDefinition dbKpiDef : kpiDefinitionList) {
            String kpiId = dbKpiDef.getKpiId();
            String kpiCode = dbKpiDef.getKpiCode();
            String kpiDefaultValue = dbKpiDef.getKpiDefaultValue();

            if (executeSqlResults == null || executeSqlResults.isEmpty()) {
                //返回默认值
                log.warn("指标[" + kpiId + "]从数据源中获取的结果为空," +
                        "指标[" + kpiCode + "]值赋予默认值[" + kpiDefaultValue + "]");

                KpiResult kpiResult = new KpiResult();
                BeanUtils.copyProperties(dbKpiDef, kpiResult);
                Map<String, Object> outputData = new HashMap<>();
                outputData.put(kpiCode, kpiDefaultValue == null ? null : VariableFormat.convertValue(kpiResult.getKpiType(), kpiCode, kpiDefaultValue));
                kpiResult.setSuccessLog(ruleLogId, null, kpiInputData, outputData);

                restlts.add(kpiResult);
            } else {
                Object kpiValue = executeSqlResults.get(dbKpiDef.getKpiCode());
                log.info(" 指标[" + kpiId + "]获取到的值为 {code:" + kpiCode + ",value:" + kpiValue + "}");

                KpiResult kpiResult = new KpiResult();
                BeanUtils.copyProperties(dbKpiDef, kpiResult);
                Map<String, Object> outputData = new HashMap<>();
                outputData.put(kpiCode, kpiValue == null ? null : VariableFormat.convertValue(kpiResult.getKpiType(), kpiCode, kpiValue));
                kpiResult.setSuccessLog(ruleLogId, null, kpiInputData, outputData);


                restlts.add(kpiResult);
            }
        }
        log.debug("指标[" + kpiIdsStr + "]指标[" + kpiCodeStr + "]值赋予值[" + JSONObject.toJSONString(restlts) + "]");

        return restlts;
    }


    /**
     * 例: 将 select age from t_user where idCard = '{id_card}' AND name = '{name}' ==>
     * select age from t_user where idCard = 'id_cardValue' AND name = 'nameValue'
     *
     * @return
     */
    private String convertFetchSql(KpiDefinition kpiDefinition, Map<String, Object> params, KpiResult kpiResult) {
        Map<String, Object> inputData = new HashMap<>();
        String fetchSql = kpiDefinition.getFetchSql();
        try {
            if (StringUtils.isBlank(fetchSql)) {
                throw new Exception("指标[" + kpiDefinition.getKpiId() + "],[" + kpiDefinition.getKpiCode() + "]从数据源中获取数据的 fetchSql 为空");
            }

            List<KpiFetchLimiters> kpiFetchLimitersList = kpiDefinition.getKpiFetchLimitersList();
            log.info("指标[" + kpiDefinition.getKpiId() + "],[" + kpiDefinition.getKpiCode() + "]Old fetchSql" + fetchSql);
            if (kpiFetchLimitersList == null) {
                log.warn("指标[" + kpiDefinition.getKpiId() + "],[" + kpiDefinition.getKpiCode() + "]过滤条件 : kpiFetchLimitersList == NULL , KpiDefinition : " + kpiDefinition);
                return fetchSql;
            }
            for (KpiFetchLimiters kpiFetchLimiters : kpiFetchLimitersList) {
                String variableCode = kpiFetchLimiters.getVariableCode();
                if (params.get(variableCode) == null) {
                    log.warn("指标[" + kpiDefinition.getKpiId() + "],[" + kpiDefinition.getKpiCode() + "]参数variableCode的传入值" + variableCode + "为空 , KpiDefinition : " + kpiDefinition);
                    continue;
                }
                String variabValue = "";
                if (params.get(variableCode) != null) {
                    variabValue = params.get(variableCode) + "";
                }
                fetchSql = fetchSql.replaceAll("\\$\\{" + variableCode + "\\}", variabValue);
                inputData.put(variableCode, variabValue);
            }
            log.info("指标[" + kpiDefinition.getKpiId() + "],[" + kpiDefinition.getKpiCode() + "]New fetchSql : " + fetchSql);
            // 将指标的输入参数放到日志中
            if (kpiResult != null) {
                kpiResult.setInputData(inputData);
            }
//           // 第二种: 正则
//           Pattern pattern = Pattern.compile("\\{(.*?)}");
//           Matcher matcher = pattern.matcher(fetchSql);
//           while (matcher.find()) {
//               String placeHolder = matcher.group(0);
//               String variableCode = matcher.group(1);
//               String variableValue = params.get(variableCode).toString();
//               variableValue = "'"+variableValue+"'";
//               fetchSql.replaceFirst(placeHolder, variableValue );
//           }
        } catch (Exception e) {
            log.error("指标[" + kpiDefinition.getKpiId() + "],[" + kpiDefinition.getKpiCode() + "]转换SQL异常", e);
            throw new FetchException("指标[" + kpiDefinition.getKpiId() + "],[" + kpiDefinition.getKpiCode() + "]转换SQL异常", e);
        }
        return fetchSql;
    }

    /**
     * @param kpiDefinitionList 所有指标的 数据源 , 表 ,以及条件 必须完全一致
     * @return
     */
    private String convertFetchSql(List<KpiDefinition> kpiDefinitionList, Map<String, Object> params, KpiResult kpiResult) {
        if (CollectionUtil.isEmpty(kpiDefinitionList)) {
            return null;
        }
        String fetchSql = convertFetchSql(kpiDefinitionList.get(0), params, kpiResult); // 构建sql 将sql中的 {aaa} ==> aaaValue
        StringBuilder sql = new StringBuilder();
        sql.append("select  ");
        for (KpiDefinition dbKpi : kpiDefinitionList) {
            String select = dbKpi.getFetchSql().split("from")[0].substring(6);
            sql.append(select + ",");
        }
        sql.deleteCharAt(sql.length() - 1);
        sql.append(" from ");
        sql.append(fetchSql.split("from")[1]);
        return sql.toString();
    }

    /**
     * 通过指标 和 传入的参数获取行键值
     */
    private String getHbaseRowKey(KpiDefinition kpiDefinition, Map<String, Object> params) {

        String rowKey = null;
        if (kpiDefinition == null) {
            return rowKey;
        }

        List<KpiFetchLimiters> kpiFetchLimitersList = kpiDefinition.getKpiFetchLimitersList();
        if (kpiFetchLimitersList != null && kpiFetchLimitersList.size() == 1) {
            for (KpiFetchLimiters kpiFetchLimiters : kpiFetchLimitersList) {
                String variableCode = kpiFetchLimiters.getVariableCode();
                if (params.get(variableCode) == null) {
                    log.warn("指标[" + kpiDefinition.getKpiId() + "],[" + kpiDefinition.getKpiCode() + "]参数variableCode的传入值" + variableCode + "为空");
                    continue;
                }
                rowKey = params.get(variableCode) + "";
            }
        } else {
            log.warn("指标[" + kpiDefinition.getKpiId() + "],[" + kpiDefinition.getKpiCode() + "]过滤条件 : kpiFetchLimitersList == NULL");
        }

        return rowKey;
    }

    /**
     * 对指标 通过 数据源ID , 表ID , 以及限制条件 , 进行分类
     *
     * @param kpiDefinitionList
     * @return key=dbId_aaa_tableId_bbb_condition_[{columnId_ddd_variableId_eee}{...}]       value=List<KpiDefinition>
     */
    private Map<String, List<KpiDefinition>> sortDBForKpi(List<KpiDefinition> kpiDefinitionList) {
        Map<String, List<KpiDefinition>> keyMap = new HashMap<>();//key存的是数据源比较的字符串，value存的是指标ID
        for (KpiDefinition kpiDefinition : kpiDefinitionList) {
            StringBuilder key = new StringBuilder();
            key.append("dbId_");
            key.append(kpiDefinition.getDbId());
            key.append("_tableId_");
            key.append(kpiDefinition.getTableId());
            List<KpiFetchLimiters> kpiFetchLimitersList = kpiDefinition.getKpiFetchLimitersList();
            Collections.sort(kpiFetchLimitersList, new Comparator<KpiFetchLimiters>() {
                public int compare(KpiFetchLimiters a, KpiFetchLimiters b) {
                    int ret = 1;
                    try {
                        ret = a.getColumnId().compareTo(b.getColumnId());
                    } catch (Exception e) {
                        log.warn(e);
                    }
                    return ret;
                }
            });
            key.append("_condition_[");

            for (KpiFetchLimiters kpiFetchLimiters : kpiFetchLimitersList) {
                //condition.add("columnId:"+kpiFetchLimiters.getColumnId()+"variableId:"+kpiFetchLimiters.getVariableId());
                key.append("{columnId_").append(kpiFetchLimiters.getColumnId()).append("_variableId_").append(kpiFetchLimiters.getVariableId()).append("}");
            }
            key.append("]");
            String tmp = key.toString();
            if (!keyMap.containsKey(tmp)) {
                keyMap.put(tmp, new ArrayList());
            }
            keyMap.get(tmp).add(kpiDefinition);
        }
        return keyMap;

    }

//    private Map<String, Object> executeSql(KpiDefinition kpiDefinition, Map<String, Object> params, String kpiIdsStr, String kpiCodeStr) throws Exception {
//        List<KpiDefinition> kpiDefinitionList = new ArrayList<>(1);
//        kpiDefinitionList.add(kpiDefinition);
//        return executeSql(kpiDefinitionList, params, kpiIdsStr, kpiCodeStr);
//    }

    /**
     * @param kpiDefinitionList 所有指标的 数据源 , 表 ,以及条件 必须完全一致
     * @param kpiIdsStr         用做日志
     * @param kpiCodeStr        用做日志
     */
    private KpiResult executeSql(List<KpiDefinition> kpiDefinitionList, Map<String, Object> params, String kpiIdsStr, String kpiCodeStr) {
        KpiResult result = new KpiResult();
        Map<String, Object> resultData = new HashMap<>();
        KpiDefinition firstKpi = kpiDefinitionList.get(0);
        String dbType = firstKpi.getDbType();
        ResponseResult responseResult = null;
        log.info("指标[" + kpiIdsStr + "]请求数据源," +
                "指标名称：" + kpiCodeStr + ",参数:[" + JSON.toJSONString(params) + "]");
        try {
            if ("8".equals(dbType)) {  // hbase
                List<VariableMapping> variableMappings = Lists.newLinkedList();
                for (KpiDefinition hbaseKpi : kpiDefinitionList) {
                    VariableMapping keyMapping = new VariableMapping();
                    keyMapping.setColumnCode(hbaseKpi.getKpiValueSourceCode());
                    keyMapping.setTableCode(hbaseKpi.getTableCode());
                    keyMapping.setVariableCode(hbaseKpi.getKpiCode());
                    keyMapping.setVariableTypeId(hbaseKpi.getKpiType());
                    variableMappings.add(keyMapping);
                }

                String rowKey = getHbaseRowKey(firstKpi, params); // 获取行键值

                if (rowKey == null) {
                    throw new FetchException("指标[" + kpiIdsStr + "]获取指标[" + kpiCodeStr + "]值失败," +
                            "从hbase中获取参数值失败: 行键值为空");
                }
                HbaseOperator hbaseOperator = new HbaseOperator(firstKpi.getDbId());
                responseResult = hbaseOperator.get(firstKpi.getTableCode(), rowKey, variableMappings, null);
                log.info("指标[" + kpiIdsStr + "]通过Hbase，get请求到的结果为 : " + JSONObject.toJSONString(responseResult));
            } else {
                final DataSource dataSource = dbMetaDataMgrService.selectDbByTableId(firstKpi.getTableId());
                String sql = convertFetchSql(kpiDefinitionList, params, result);
                responseResult = DbOperatorFactory.execute(sql, dataSource);
            }
        } catch (Exception e) {
            log.error("指标[" + kpiIdsStr + "]执行SQL异常," +
                    "指标名称：" + kpiCodeStr + ",参数:[" + JSON.toJSONString(params) + "]", e);
            throw new FetchException("指标[" + kpiIdsStr + "]执行SQL异常," +
                    "指标名称：" + kpiCodeStr + ",参数:[" + JSON.toJSONString(params) + "]", e);
        }
        if (responseResult != null) {
            // 处理结果 打印信息了面只记录了一个kpi,没有记录全部
            List<Map<String, Object>> resultMapList = (List<Map<String, Object>>) responseResult.getData();
            // 如果查到的值为空,返回默认值
            if (!CollectionUtil.isEmpty(resultMapList)) {
                if (resultMapList.size() == 1) {  // 正常返回
                    resultData = resultMapList.get(0);
                    // 将 输出 放到指标结果中
                    result.setOutputData(resultData);
                } else { // 查到多个值,返回异常
                    log.error("指标[" + kpiIdsStr + "]指标的fetchSql获取获取到的值不唯一");
                    throw new FetchException("指标[" + kpiCodeStr + "]获取到的值不唯一");
                }
            }
        }
        return result;
    }

}
