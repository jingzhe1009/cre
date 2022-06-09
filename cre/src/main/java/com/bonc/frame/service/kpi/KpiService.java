package com.bonc.frame.service.kpi;

import com.bonc.frame.entity.commonresource.RuleSetHeaderVo;
import com.bonc.frame.entity.datasource.DataSource;
import com.bonc.frame.entity.kpi.KpiDefinition;
import com.bonc.frame.entity.kpi.KpiGroup;
import com.bonc.frame.entity.metadata.MetaDataTable;
import com.bonc.frame.util.ResponseResult;
import com.bonc.framework.rule.exception.ExecuteException;
import com.bonc.framework.rule.executor.context.impl.ExecutorRequest;
import com.bonc.framework.rule.kpi.KpiResult;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * @author yedunyao
 * @date 2019/10/16 16:40
 */
public interface KpiService {

    Object getKpiValue(KpiDefinition kpiDefinition, Map<String, Object> params, ExecutorRequest executorRequest) throws ExecuteException;

    /**
     * 批量获取KPI的值
     *
     * @param fetchType 指标类型 数据源:FetchType.DB.getValue()="0"   指标:FetchType.API.getValue()="1"
     */
    List<KpiResult> getKpiValueBatchByFetchType(List<KpiDefinition> kpiDefinitionList, String fetchType, Map<String, Object> params, ExecutorRequest executorRequest);

    /**
     * 批量获取KPI的值
     * 会根据指标的类型进行分类,调用 getKpiValueBatchByFetchType
     */
    List<KpiResult> getKpiValueBatch(List<KpiDefinition> kpiDefinitionList, Map<String, Object> params, ExecutorRequest executorRequest);

    // ------------------------ 指标头信息管理 ------------------------

    // 当前指标是否在使用中
    boolean isKpiUsed(String kpiId);

    List<Map<String, Object>> getKpiType();

    List<RuleSetHeaderVo> getRuleSetGroupByKpiId(String KpiId);

    boolean checkNameIsExist(String kpiName, @Nullable String kpiHeaderId);

    boolean checkCodeIsExist(String kpiCode, @Nullable String kpiId);

    FetchService getFetchService(String fetchType);

    Map<String, Object> pagedKpiBaseInfo(@Nullable String kpiName,
                                         @Nullable String kpiGroupName,
                                         @Nullable String kpiType,
                                         @Nullable String fetchType,
                                         String start, String length);

    Map<String,Object> pagedKpiGroupBaseInfo(@Nullable String kpiGroupName,
                                             String startDate, String endDate,
                                             String start, String length);

    ResponseResult getKpiBaseInfo(@Nullable String kpiName,
                                  @Nullable String kpiGroupName,
                                  @Nullable String kpiType,
                                  @Nullable String fetchType,
                                  @Nullable String startDate,
                                  @Nullable String endDate);

    Map<String, Object> getKpiBaseInfo(@Nullable String kpiName,
                                       @Nullable String kpiGroupName,
                                       @Nullable String kpiType,
                                       @Nullable String fetchType,
                                       @Nullable String startDate,
                                       @Nullable String endDate,
                                       String start, String length);

    KpiDefinition getBaseInfo(String kpiId);

    Map<String, KpiDefinition> getKpiBaseInfoBatch(List<String> kpiId);

    ResponseResult getDetail(String kpiId);

    List<KpiDefinition> getKpiDetailWithLimiter(String kpiId, String kpiCode, String kpiName);

    List<KpiDefinition> getKpiDetailWithLimiterBatch(List<String> kpiIds);

    ResponseResult updateKpiDefinition(KpiDefinition kpiDefinition, String userId);

    ResponseResult updateKpiDefinitionOffer(KpiDefinition kpiDefinition, String userId);

    ResponseResult createKpiDefinition(KpiDefinition kpiDefinition, String userId);

    void insertKpiPersistence(KpiDefinition kpiDefinition);

    ResponseResult deleteKpiDefinition(String kpiId);

    /**
     * 修改指标的 fetchType 的值
     *
     * @param kpiDefinition
     */
    String convertKpiSql(KpiDefinition kpiDefinition, MetaDataTable metaDataTable, DataSource dataSource);
    // ------------------------ 指标组管理 ------------------------

    // 获取指标组列表
    ResponseResult getKpiGroups(String kpiGroupName);

    KpiGroup getKpiGroupByKpiGroupId(String kpiGroupId);

    /**
     * 通过id 或 name 完全匹配
     *
     * @param kpiGroupId
     * @param kpiGroupName
     * @return
     */
    KpiGroup getKpiGroupByKpiGroupProperty(String kpiGroupId, String kpiGroupName);

    Map<String, Object> getKpiGroupsPaged(String kpiGroupName, String start, String length);

    boolean checkGroupNameIsExist(String kpiGroupName, @Nullable String kpiGroupId);

    ResponseResult createKpiGroup(KpiGroup kpiGroup, String userId);

    void insertKpiGroupPersistence(KpiGroup kpiGroup);

    ResponseResult updateKpiGroup(KpiGroup kpiGroup, String userId);

    // 当前指标组是否在使用中
    boolean isGroupUsed(String kpiGroupId);

    ResponseResult deleteKpiGroup(String kpiGroupId);


    //通过 模型id 获取引用的所有的 指标信息
    ResponseResult getKpiBaseInfoByRuleId(@Nullable String ruleId);

    ResponseResult getKpiBaseInfoByRuleIdNoAuth(@Nullable String ruleId);

}
