package com.bonc.frame.service.impl.kpi;

import com.bonc.frame.config.Config;
import com.bonc.frame.dao.DaoHelper;
import com.bonc.frame.entity.commonresource.RuleSetHeaderVo;
import com.bonc.frame.entity.datasource.DataSource;
import com.bonc.frame.entity.kpi.KpiDefinition;
import com.bonc.frame.entity.kpi.KpiFetchLimiters;
import com.bonc.frame.entity.kpi.KpiGroup;
import com.bonc.frame.entity.metadata.MetaDataTable;
import com.bonc.frame.entity.metadata.RelationTable;
import com.bonc.frame.entity.task.VariableMapping;
import com.bonc.frame.module.db.sqlconvert.ISqlConvert;
import com.bonc.frame.module.db.sqlconvert.SqlConvertFactory;
import com.bonc.frame.module.lock.distributed.curator.CuratorMutexLock;
import com.bonc.frame.module.lock.distributed.curator.LockDataType;
import com.bonc.frame.module.log.aop.operatorLog.OperatorLogDataPersistence;
import com.bonc.frame.security.ResourceType;
import com.bonc.frame.service.auth.AuthorityService;
import com.bonc.frame.service.dbresource.DbResourceService;
import com.bonc.frame.service.kpi.FetchService;
import com.bonc.frame.service.kpi.FetchType;
import com.bonc.frame.service.kpi.KpiService;
import com.bonc.frame.service.metadata.DBMetaDataMgrService;
import com.bonc.frame.service.reference.RuleReferenceService;
import com.bonc.frame.util.*;
import com.bonc.framework.rule.exception.ExecuteException;
import com.bonc.framework.rule.executor.context.impl.ExecutorRequest;
import com.bonc.framework.rule.kpi.KpiResult;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import java.util.*;

/**
 * @author yedunyao
 * @date 2019/10/16 16:46
 */
@Service("kpiService")
public class KpiServiceImpl implements KpiService {

    private static final String _KPI_GROUP = "com.bonc.frame.dao.kpi.KpiGroupMapper.";
    private static final String _KPI_DEFINITION = "com.bonc.frame.dao.kpi.KpiMapper.";

    @Autowired
    private DaoHelper daoHelper;
    //元数据管理
    @Autowired
    private DBMetaDataMgrService dbMetaDataMgrService;
    //数据源
    @Autowired
    private DbResourceService dbResourceService;

    @Autowired
    private RuleReferenceService ruleReferenceService;
    //认证
    @Autowired
    private AuthorityService authorityService;

    private Map<String, FetchService> fetchServiceMap;

    @Autowired
    public KpiServiceImpl(List<FetchService> fetchServices) {
        fetchServiceMap = new HashMap<>(2);
        for (FetchService fetchService : fetchServices) {
            fetchServiceMap.put(fetchService.getSupport().getValue(), fetchService);
        }
    }

    @Override
    public KpiResult getKpiValue(KpiDefinition kpiDefinition, Map<String, Object> params, ExecutorRequest executorRequest) throws ExecuteException {
        FetchService fetchService = fetchServiceMap.get(kpiDefinition.getFetchType());
        return fetchService.getKpiValue(kpiDefinition, params, executorRequest);
    }

    @Override
    public List<KpiResult> getKpiValueBatchByFetchType(List<KpiDefinition> kpiDefinitionList, String fetchType, Map<String, Object> params, ExecutorRequest executorRequest) {
        FetchService fetchService = fetchServiceMap.get(fetchType);
        return fetchService.getKpiValueBatch(kpiDefinitionList, params, executorRequest);
    }

    @Override
    public List<KpiResult> getKpiValueBatch(List<KpiDefinition> kpiDefinitionList, Map<String, Object> params, ExecutorRequest executorRequest) {
        if (CollectionUtil.isEmpty(kpiDefinitionList)) {
            return new ArrayList<>();
        }
        List<KpiResult> results = new ArrayList<>(kpiDefinitionList.size());
        List<KpiDefinition> dbKpiDefinitions = new ArrayList<>();
        List<KpiDefinition> interfaceKpiDefinitions = new ArrayList<>();
        List<KpiDefinition> inputKpiDefinitions = new ArrayList<>();
        for (KpiDefinition kpiDefinition : kpiDefinitionList) {
            if (FetchType.DB.getValue().equals(kpiDefinition.getFetchType())) {//数据源
                dbKpiDefinitions.add(kpiDefinition);
            } else if (FetchType.API.getValue().equals(kpiDefinition.getFetchType())) {//接口
                interfaceKpiDefinitions.add(kpiDefinition);
            } else if (FetchType.KPI.getValue().equals(kpiDefinition.getFetchType())) {//输入指标
                inputKpiDefinitions.add(kpiDefinition);
            }
        }

        //获取返回结果,并将返回结果
        if (!CollectionUtil.isEmpty(dbKpiDefinitions)) {
            // DB
            List<KpiResult> dbKpiValueBatch = getKpiValueBatchByFetchType(dbKpiDefinitions, FetchType.DB.getValue(), params, executorRequest);
            if (dbKpiValueBatch != null && !dbKpiValueBatch.isEmpty())
                results.addAll(dbKpiValueBatch);
        }
        if (!CollectionUtil.isEmpty(interfaceKpiDefinitions)) {
            // API
            List<KpiResult> apiKpiValueBatch = getKpiValueBatchByFetchType(interfaceKpiDefinitions, FetchType.API.getValue(), params, executorRequest);
            if (apiKpiValueBatch != null && !apiKpiValueBatch.isEmpty())
                results.addAll(apiKpiValueBatch);
        }
        if (!CollectionUtil.isEmpty(inputKpiDefinitions)) {
            // KPI
            List<KpiResult> inputKpiValueBatch = getKpiValueBatchByFetchType(inputKpiDefinitions, FetchType.KPI.getValue(), params, executorRequest);
            if (inputKpiValueBatch !=null && !inputKpiValueBatch.isEmpty())
                results.addAll(inputKpiValueBatch);
        }
        return results;
    }


    // ------------------------ 指标管理 ------------------------

    @Override
    public boolean isKpiUsed(String kpiId) {
        if (!Config.CRE_REFERENCE_CHECK_ENABLE) {
            return false;
        }
        int count = (int) daoHelper.queryOne(_KPI_DEFINITION + "countKpiUsedByKpiId", kpiId);
        if (count > 0) {
            return true;
        }
        return false;
    }

    @Override
    public List<Map<String, Object>> getKpiType() {
        List<Map<String, Object>> list = this.daoHelper.queryForList(_KPI_DEFINITION + "selectKpiType", null);
        return list;
    }

    @Override
    public List<RuleSetHeaderVo> getRuleSetGroupByKpiId(String kpiId){
        List<RuleSetHeaderVo> list = daoHelper.queryForList(_KPI_DEFINITION + "getRuleSetGroupByKpiId", kpiId);
        return list;
    }

    /**
     * 需要保证指标名称不重复且与变量名称不重复
     *
     * @param kpiName
     * @param kpiId
     * @return
     */
    @Override
    public boolean checkNameIsExist(String kpiName, @Nullable String kpiId) {
        if (!Config.CRE_REFERENCE_CHECK_ENABLE) {
            return false;
        }
        Map<String, String> param = new HashMap<>(2);
        param.put("kpiName", kpiName);
        param.put("kpiId", kpiId);
        int obj = (int) daoHelper.queryOne(_KPI_DEFINITION + "countByName", param);
        if (obj > 0) {
            return true;
        }
        return false;
    }

    /**
     * 需要保证指标编码不重复且与变量编码不重复
     *
     * @param kpiCode
     * @param kpiId
     * @return
     */
    @Override
    public boolean checkCodeIsExist(String kpiCode, @Nullable String kpiId) {
        if (!Config.CRE_REFERENCE_CHECK_ENABLE) {
            return false;
        }
        Map<String, String> param = new HashMap<>(2);
        param.put("kpiCode", kpiCode);
        param.put("kpiId", kpiId);
        int obj = (int) daoHelper.queryOne(_KPI_DEFINITION + "countByCode", param);
        if (obj > 0) {
            return true;
        }
        return false;
    }

    @Override
    public FetchService getFetchService(String fetchType) {
        return fetchServiceMap.get(fetchType);
    }

    @Override
    public ResponseResult getKpiBaseInfo(@Nullable String kpiName,
                                         @Nullable String kpiGroupName,
                                         @Nullable String kpiType,
                                         @Nullable String fetchType,
                                         @Nullable String startDate,
                                         @Nullable String endDate) {
        Map<String, String> param = new HashMap<>(4);
        param.put("kpiName", kpiName);
        param.put("kpiGroupName", kpiGroupName);
        param.put("kpiType", kpiType);
        param.put("fetchType", fetchType);
        param.put("startDate", startDate);
        param.put("endDate", endDate);
        List<KpiDefinition> kpiDefinitionList = daoHelper.queryForList(_KPI_DEFINITION +
                "getKpiBaseInfo", param);
        return ResponseResult.createSuccessInfo("", kpiDefinitionList);
    }

    @Override
    public ResponseResult getKpiBaseInfoByRuleId(@Nullable String ruleId) {
        Map<String, String> param = new HashMap<>(4);
        param.put("ruleId", ruleId);

        List<KpiDefinition> kpiDefinitionList = daoHelper.queryForList(_KPI_DEFINITION +
                "getKpiBaseInfoByRuleId", param);

        return ResponseResult.createSuccessInfo("", kpiDefinitionList);
    }

    @Override
    public ResponseResult getKpiBaseInfoByRuleIdNoAuth(@Nullable String ruleId) {
        Map<String, String> param = new HashMap<>(4);
        param.put("ruleId", ruleId);

        List<KpiDefinition> kpiDefinitionList = daoHelper.queryForList(_KPI_DEFINITION +
                "getKpiBaseInfoByRuleIdNoAuth", param);

        return ResponseResult.createSuccessInfo("", kpiDefinitionList);
    }

    @Override
    public Map<String, Object> pagedKpiBaseInfo(@Nullable String kpiName,
                                                @Nullable String kpiGroupName,
                                                @Nullable String kpiType,
                                                @Nullable String fetchType,
                                                String start, String length) {
        Map<String, String> param = new HashMap<>(4);
        param.put("kpiName", kpiName);
        param.put("kpiGroupName", kpiGroupName);
        param.put("kpiType", kpiType);
        param.put("fetchType", fetchType);
        return daoHelper.queryForPageList(_KPI_DEFINITION +
                "pagedKpiBaseInfo", param, start, length);
    }

    @Override
    public Map<String, Object> pagedKpiGroupBaseInfo(
            @Nullable String kpiGroupName,
            String startDate,String endDate,
            String start, String length) {
        Map<String, String> param = new HashMap<>(4);
        param.put("kpiGroupName", kpiGroupName);
        param.put("startDate", startDate);
        param.put("endDate", endDate);
        return daoHelper.queryForPageList(_KPI_GROUP +
                "pagedKpiGroupBaseInfo", param, start, length);
    }


    @Override
    public Map<String, Object> getKpiBaseInfo(@Nullable String kpiName,
                                              @Nullable String kpiGroupName,
                                              @Nullable String kpiType,
                                              @Nullable String fetchType,
                                              @Nullable String startDate,
                                              @Nullable String endDate,
                                              String start, String length) {
        Map<String, String> param = new HashMap<>(6);
        param.put("kpiName", kpiName);
        param.put("kpiGroupName", kpiGroupName);
        param.put("kpiType", kpiType);
        param.put("fetchType", fetchType);
        param.put("startDate", startDate);
        param.put("endDate", endDate);
        return daoHelper.queryForPageList(_KPI_DEFINITION +
                "getKpiBaseInfo", param, start, length);
    }

    public List<KpiFetchLimiters> getFetchLimiters(String kpiId) {
        return daoHelper.queryForList(_KPI_DEFINITION + "getKpiLimiters", kpiId);
    }

    private void insertBatchLimiters(List<KpiFetchLimiters> kpiFetchLimitersList) {
        daoHelper.insertBatch(_KPI_DEFINITION + "insertBatchLimiters", kpiFetchLimitersList);
    }

    private void deleteKpiFetchLimiters(String kpiId) {
        daoHelper.delete(_KPI_DEFINITION + "deleteKpiFetchLimiters", kpiId);
    }

    @Override
    public KpiDefinition getBaseInfo(String kpiId) {
        KpiDefinition kpiDefinition = (KpiDefinition) daoHelper.queryOne(_KPI_DEFINITION +
                "getOneDetail", kpiId);
        return kpiDefinition;
    }

    @Override
    public Map<String, KpiDefinition> getKpiBaseInfoBatch(List<String> kpiIdList) {
        if (CollectionUtil.isEmpty(kpiIdList)) {
            return new HashMap<>();
        }
        List<KpiDefinition> kpiDefinitionList = daoHelper.queryForList(_KPI_DEFINITION +
                "getKpiDetailBatch", kpiIdList);
        if (CollectionUtil.isEmpty(kpiDefinitionList)) {
            return new HashMap<>();
        }
        Map<String, KpiDefinition> results = new HashMap<>(kpiDefinitionList.size());
        for (KpiDefinition kpiDefinition : kpiDefinitionList) {
            if (kpiDefinition != null) {
                results.put(kpiDefinition.getKpiId(), kpiDefinition);
            }
        }
        return results;
    }

    @Override
    public ResponseResult getDetail(String kpiId) {
        if (StringUtils.isBlank(kpiId)) {
            return ResponseResult.createFailInfo("请求参数[kpiId]不能为空");
        }
        Map<String, Object> param = new HashMap<>(1);
        param.put("kpiId", kpiId);
        KpiDefinition kpiDefinition = (KpiDefinition) daoHelper.queryOne(_KPI_DEFINITION +
                "getKpiDetailByKpiProperty", param);
        if (kpiDefinition == null) {
            return ResponseResult.createFailInfo("当前指标不存在");
        }
        return ResponseResult.createSuccessInfo("", kpiDefinition);
    }

    @Override
    public List<KpiDefinition> getKpiDetailWithLimiter(String kpiId, String kpiCode, String kpiName) {
        if (StringUtils.isBlank(kpiId) && StringUtils.isBlank(kpiCode) && StringUtils.isBlank(kpiName)) {
            return null;
        }
        Map<String, Object> param = new HashMap<>(3);
        param.put("kpiId", kpiId);
        param.put("kpiCode", kpiCode);
        param.put("kpiName", kpiName);
        List<KpiDefinition> kpiDefinition = daoHelper.queryForList(_KPI_DEFINITION +
                "getKpiDetailByKpiProperty", param);

        return kpiDefinition;
    }

    @Override
    public List<KpiDefinition> getKpiDetailWithLimiterBatch(List<String> kpiIds) {
        if (CollectionUtil.isEmpty(kpiIds)) {
            return Collections.emptyList();
        }
        Map<String, Object> param = new HashMap<>();
        param.put("kpiIdList", kpiIds);
        List<KpiDefinition> kpiDefinitions = daoHelper.queryForList(_KPI_DEFINITION +
                "getKpiDetailWithLimiterBatch", param);


        return kpiDefinitions;
    }

    @Override
    @CuratorMutexLock(value = {"kpiName", "kpiCode"}, lockAlias = {"name", "code"}, lockDataType = LockDataType.VARIABLE)
    @OperatorLogDataPersistence(operatorType = ConstantUtil.OPERATE_CREATE_KPI, logType = "1")
    public ResponseResult createKpiDefinition(KpiDefinition kpiDefinition, String userId) {
        String kpiName = kpiDefinition.getKpiName();
        String kpiCode = kpiDefinition.getKpiCode();
        String fetchType= kpiDefinition.getFetchType();
        if (StringUtils.isBlank(kpiName)) {
            return ResponseResult.createFailInfo("请求参数[指标名称]不能为空");
        }
        if (StringUtils.isBlank(kpiCode)) {
            return ResponseResult.createFailInfo("请求参数[指标编码]不能为空");
        }

        if (Config.CRE_REFERENCE_CHECK_ENABLE) {
            if (checkNameIsExist(kpiName, null)) {
                return ResponseResult.createFailInfo("指标名称已存在");
            }
            if (checkCodeIsExist(kpiCode, null)) {
                return ResponseResult.createFailInfo("指标编码已存在");
            }
        }

        String kpiId = IdUtil.createId();
        kpiDefinition.setKpiId(kpiId);
        kpiDefinition.setCreateDate(new Date());
        kpiDefinition.setCreatePerson(userId);

        if ("0".equals(kpiDefinition.getFetchType())) {
            String tableId = kpiDefinition.getTableId();
            MetaDataTable metaDataTable = dbMetaDataMgrService.selectTableByPrimaryKey(tableId);
            DataSource dataSource = dbMetaDataMgrService.selectDbByTableId(tableId);
            // 为数据源
            convertKpiSql(kpiDefinition, metaDataTable, dataSource);

        }

//        daoHelper.insert(_KPI_DEFINITION + "insertKpiDefinition", kpiDefinition);

        final List<KpiFetchLimiters> kpiFetchLimitersList = kpiDefinition.getKpiFetchLimitersList();
        if (!CollectionUtil.isEmpty(kpiFetchLimitersList)) {
            setKpiId(kpiFetchLimitersList, kpiId, true);
//            insertBatchLimiters(kpiFetchLimitersList);
        }

        // 自动授权
//        authorityService.autoGrantAuthToCurrentUser(kpiId, ResourceType.DATA_KPI);
        insertKpiPersistence(kpiDefinition);
        return ResponseResult.createSuccessInfo("", kpiId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertKpiPersistence(KpiDefinition kpiDefinition) {
        if (kpiDefinition != null) {
            String kpiId = kpiDefinition.getKpiId();
            String currentUser = ControllerUtil.getCurrentUser();
            kpiDefinition.setCreateDate(new Date());
            kpiDefinition.setCreatePerson(currentUser);
            daoHelper.insert(_KPI_DEFINITION + "insertKpiDefinition", kpiDefinition);
            final List<KpiFetchLimiters> kpiFetchLimitersList = kpiDefinition.getKpiFetchLimitersList();
            if (!CollectionUtil.isEmpty(kpiFetchLimitersList)) {
                insertBatchLimiters(kpiFetchLimitersList);
            }
            authorityService.autoGrantAuthToCurrentUser(kpiId, ResourceType.DATA_KPI);

        }

    }

    public String convertKpiSql(KpiDefinition kpiDefinition, MetaDataTable metaDataTable, DataSource dataSource) {

        String tableId = kpiDefinition.getTableId();
        if (metaDataTable == null) {
            metaDataTable = dbMetaDataMgrService.selectTableByPrimaryKey(tableId);
        }
        if (dataSource == null) {
            dataSource = dbMetaDataMgrService.selectDbByTableId(tableId);
        }

        if (metaDataTable == null) {
            throw new IllegalStateException("元数据表不存在，tableId: " + tableId);
        }
        if (dataSource == null) {
            throw new IllegalStateException("数据源不存在，tableId: " + tableId);
        }
        String tableCode = metaDataTable.getTableCode();
        kpiDefinition.setDbId(dataSource.getDbId());
        //TODO :hbase实现
        if ("8".equals(dataSource.getDbType())) {  // 如果是hbase则不构造sql
            return null;
        }

        final ISqlConvert iSqlConvert = SqlConvertFactory.getSqlConvertByDbType(dataSource.getDbType());

//        "select table.column as "
        VariableMapping selectkeyMapping = new VariableMapping();
        selectkeyMapping.setColumnCode(kpiDefinition.getKpiValueSourceCode());
        selectkeyMapping.setTableCode(tableCode);
        selectkeyMapping.setVariableCode(kpiDefinition.getKpiCode());
        List<VariableMapping> selectVariableMappings = Lists.newLinkedList();
        selectVariableMappings.add(selectkeyMapping);

        String selectSql = "";
        if (!metaDataTable.isRelationTable()) { // 如果不是关联表
            selectSql = iSqlConvert.select(selectVariableMappings, tableCode);
        } else {
            final List<RelationTable> relationTables = dbResourceService.selectRelationTable(tableId);
            selectSql = iSqlConvert.selectWithJoin(selectVariableMappings, relationTables);
        }

        //  where "tableCode"."columnCode" = "${VariableCode}"
        List<KpiFetchLimiters> kpiFetchLimitersList = kpiDefinition.getKpiFetchLimitersList();
        String whereClausesSql = genWhereClauses(kpiFetchLimitersList, tableCode, iSqlConvert);
        String sql = selectSql + whereClausesSql;

        kpiDefinition.setFetchSql(sql);

        return sql;
    }

    private String genWhereClauses(List<KpiFetchLimiters> kpiFetchLimitersList, String tableCode, ISqlConvert
            iSqlConvert) {
        if (kpiFetchLimitersList == null || kpiFetchLimitersList.isEmpty()) {
            return null;
        }
        List<VariableMapping> whereVariableMappings = Lists.newLinkedList();
        for (KpiFetchLimiters kpiFetchLimiters : kpiFetchLimitersList) {
            if (kpiFetchLimiters != null) {
                VariableMapping variableMapping = new VariableMapping();
                variableMapping.setColumnCode(kpiFetchLimiters.getColumnCode());
                variableMapping.setTableCode(tableCode);
                variableMapping.setVariableCode(kpiFetchLimiters.getVariableCode());
                whereVariableMappings.add(variableMapping);
            }
        }
        if (whereVariableMappings.isEmpty()) {
            return null;
        }
        return iSqlConvert.genWhereClauses(whereVariableMappings);
    }

    private void setKpiId(List<KpiFetchLimiters> kpiFetchLimitersList, String kpiId, boolean isCreateId) {
        for (KpiFetchLimiters kpiFetchLimiters : kpiFetchLimitersList) {
            if (isCreateId) {
                kpiFetchLimiters.setId(IdUtil.createId());
            }
            kpiFetchLimiters.setKpiId(kpiId);
        }
    }

    public boolean needLock(KpiDefinition kpiDefinition) {
        KpiDefinition oldKpiDefinition = getBaseInfo(kpiDefinition.getKpiId());
        if (kpiDefinition.getKpiCode().equals(oldKpiDefinition.getKpiCode()) && kpiDefinition.getKpiName().equals(oldKpiDefinition.getKpiName())) {
            return false;
        }
        return true;
    }

    @Override
    //  @Transactional(rollbackFor = Exception.class)
    @CuratorMutexLock(value = {"kpiName", "kpiCode"}, lockAlias = {"name", "code"}, lockDataType = LockDataType.VARIABLE,
            addLockCondition = "#{#myService.needLock(#kpiDefinition)}")
    @OperatorLogDataPersistence(operatorType = ConstantUtil.OPERATE_UPDATE_KPI, logType = "1")
    public ResponseResult updateKpiDefinition(KpiDefinition kpiDefinition, String userId) {
        String kpiId = kpiDefinition.getKpiId();

        if (StringUtils.isBlank(kpiId)) {
            return ResponseResult.createFailInfo("请求参数[kpiId]不能为空");
        }

        if (Config.CRE_REFERENCE_CHECK_ENABLE) {
            if (checkNameIsExist(kpiDefinition.getKpiName(), kpiId)) {
                return ResponseResult.createFailInfo("指标名称已存在");
            }
            if (checkCodeIsExist(kpiDefinition.getKpiCode(), kpiId)) {
                return ResponseResult.createFailInfo("指标编码已存在");
            }
        }

        kpiDefinition.setUpdateDate(new Date());
        kpiDefinition.setUpdatePerson(userId);

        kpiDefinition.setPlatformUpdateUserJobNumber(ControllerUtil.getRealUserAccount());

        if ("0".equals(kpiDefinition.getFetchType())) {
            // 数据源类型
            // 置空数据源相关属性
//            kpiDefinition.setDbId("");
//            kpiDefinition.setDbAlias("");
//            kpiDefinition.setDbType("");
//            kpiDefinition.setTableId("");
//            kpiDefinition.setTableCode("");
//            kpiDefinition.setFetchSql("");

            // 根据定义生成sql
            String tableId = kpiDefinition.getTableId();
            MetaDataTable metaDataTable = dbMetaDataMgrService.selectTableByPrimaryKey(tableId);
            DataSource dataSource = dbMetaDataMgrService.selectDbByTableId(tableId);
            convertKpiSql(kpiDefinition, metaDataTable, dataSource);
        } else if ("1".equals(kpiDefinition.getFetchType())) {
            // 接口类型
            // 置空接口相关属性
//            kpiDefinition.setApiId("");
//            kpiDefinition.setApiName("");
        } else if ("2".equals(kpiDefinition.getFetchType())) {
            // 输入指标类型
            // 置空相关属性
        }

        daoHelper.update(_KPI_DEFINITION + "updateKpiDefinition", kpiDefinition);

        final List<KpiFetchLimiters> kpiFetchLimitersList = kpiDefinition.getKpiFetchLimitersList();
        if (!CollectionUtil.isEmpty(kpiFetchLimitersList)) {
            deleteKpiFetchLimiters(kpiId);
            setKpiId(kpiFetchLimitersList, kpiId, true);
            insertBatchLimiters(kpiFetchLimitersList);
        }

        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult updateKpiDefinitionOffer(KpiDefinition kpiDefinition, String userId) {
        String kpiId = kpiDefinition.getKpiId();

        if (StringUtils.isBlank(kpiId)) {
            return ResponseResult.createFailInfo("请求参数[kpiId]不能为空");
        }
        kpiDefinition.setUpdateDate(new Date());
        kpiDefinition.setUpdatePerson(userId);
        kpiDefinition.setPlatformUpdateUserJobNumber(ControllerUtil.getRealUserAccount());
        daoHelper.update(_KPI_DEFINITION + "updateKpiDefinitionOffer", kpiDefinition);
        return ResponseResult.createSuccessInfo();
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperatorLogDataPersistence(logType = "1", operatorType = ConstantUtil.OPERATE_DELETE_KPI)
    public ResponseResult deleteKpiDefinition(String kpiId) {
        if (StringUtils.isBlank(kpiId)) {
            return ResponseResult.createFailInfo("请求参数[kpiId]不能为空");
        }
        if (isKpiUsed(kpiId)) {
            return ResponseResult.createFailInfo("指标正在被使用,不能删除");
        }

        // 级联删除: 删除限定条件
        deleteKpiFetchLimiters(kpiId);

        daoHelper.delete(_KPI_DEFINITION + "deleteKpiDefinitionByKpiId", kpiId);
        return ResponseResult.createSuccessInfo();
    }

    // ------------------------ 指标版本管理 ------------------------


    // ------------------------ 指标组管理 ------------------------

    // 获取参数组列表
    @Override
    public ResponseResult getKpiGroups(String kpiGroupName) {
        final List<Object> list = daoHelper.queryForList(_KPI_GROUP +
                "getByGroupName", kpiGroupName);
        return ResponseResult.createSuccessInfo("success", list);
    }

    public KpiGroup getKpiGroupByKpiGroupId(String kpiGroupId) {
        KpiGroup kpiGroup = (KpiGroup) daoHelper.queryOne(_KPI_GROUP +
                "getKpiByKpiGroupId", kpiGroupId);
        return kpiGroup;
    }

    //getKpiByKpiGroupProperty
    public KpiGroup getKpiGroupByKpiGroupProperty(String kpiGroupId, String kpiGroupName) {
        if (StringUtils.isBlank(kpiGroupId) && StringUtils.isBlank(kpiGroupName)) {
            return null;
        }
        Map<String, Object> param = new HashMap<>();
        param.put("kpiGroupId", kpiGroupId);
        param.put("kpiGroupName", kpiGroupName);
        KpiGroup kpiGroup = (KpiGroup) daoHelper.queryOne(_KPI_GROUP +
                "getKpiGroupByKpiGroupProperty", param);
        return kpiGroup;
    }

    @Override
    public Map<String, Object> getKpiGroupsPaged(String kpiGroupName, String start, String length) {
        Map<String, Object> param = new HashMap<>();
        param.put("kpiGroupName", kpiGroupName);
        final Map<String, Object> map = daoHelper.queryForPageList(_KPI_GROUP +
                "getByGroupName", param, start, length);
        return map;
    }

    @Override
    public boolean checkGroupNameIsExist(String kpiGroupName, @Nullable String kpiGroupId) {
        Map<String, String> param = new HashMap<>(2);
        param.put("kpiGroupName", kpiGroupName);
        param.put("kpiGroupId", kpiGroupId);
        int obj = (int) daoHelper.queryOne(_KPI_GROUP + "countByName", param);
        if (obj > 0) {
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public ResponseResult createKpiGroup(KpiGroup kpiGroup, String userId) {
        String kpiGroupName = kpiGroup.getKpiGroupName();
        if (StringUtils.isBlank(kpiGroupName)) {
            return ResponseResult.createFailInfo("请求参数[kpiGroupName]不能为空");
        }

        if (checkGroupNameIsExist(kpiGroupName, null)) {
            return ResponseResult.createFailInfo("指标组名称已存在");
        }

        kpiGroup.setKpiGroupId(IdUtil.createId());
        kpiGroup.setCreateDate(new Date());
        kpiGroup.setCreatePerson(userId);
        daoHelper.insert(_KPI_GROUP + "insertSelective", kpiGroup);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertKpiGroupPersistence(KpiGroup kpiGroup) {
        if (kpiGroup != null) {
            String currentUser = ControllerUtil.getCurrentUser();
            kpiGroup.setCreateDate(new Date());
            kpiGroup.setCreatePerson(currentUser);
            daoHelper.insert(_KPI_GROUP + "insertSelective", kpiGroup);
        }
    }

    @Override
    @Transactional
    public ResponseResult updateKpiGroup(KpiGroup kpiGroup, String userId) {
        String kpiGroupId = kpiGroup.getKpiGroupId();
        if (StringUtils.isBlank(kpiGroupId)) {
            return ResponseResult.createFailInfo("请求参数[kpiGroupId]不能为空");
        }

        if (checkGroupNameIsExist(kpiGroup.getKpiGroupName(), kpiGroupId)) {
            return ResponseResult.createFailInfo("指标组名称已存在");
        }

        kpiGroup.setUpdateDate(new Date());
        kpiGroup.setUpdatePerson(userId);
        daoHelper.update(_KPI_GROUP + "updateByPrimaryKeySelective", kpiGroup);
        return ResponseResult.createSuccessInfo();
    }

    // 当前指标组是否在使用中
    @Override
    public boolean isGroupUsed(String kpiGroupId) {
        int obj = (int) daoHelper.queryOne(_KPI_GROUP +
                "countGroupUsed", kpiGroupId);
        if (obj > 0) {
            return true;
        }
        return false;
    }


    @Override
    @Transactional
    public ResponseResult deleteKpiGroup(String kpiGroupId) {
        if (StringUtils.isBlank(kpiGroupId)) {
            return ResponseResult.createFailInfo("请求参数[kpiGroupId]不能为空");
        }

        if (isGroupUsed(kpiGroupId)) {
            return ResponseResult.createFailInfo("当前指标组正在使用中，不能删除");
        }

        daoHelper.delete(_KPI_GROUP + "deleteByPrimaryKey", kpiGroupId);
        return ResponseResult.createSuccessInfo();
    }

}
