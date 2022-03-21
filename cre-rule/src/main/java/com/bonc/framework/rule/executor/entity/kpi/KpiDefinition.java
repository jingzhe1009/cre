package com.bonc.framework.rule.executor.entity.kpi;


import com.bonc.framework.rule.executor.entity.BaseEntity;
import com.bonc.framework.rule.executor.entity.IVariable;

import java.io.Serializable;
import java.util.List;

/**
 * 指标
 *
 * @author yedunyao
 * @date 2019/10/15 13:56
 */
public class KpiDefinition extends BaseEntity implements IVariable, Serializable {

    private static final long serialVersionUID = 3760701668644011633L;

    protected String kpiId;

    protected String kpiName;

    /**
     * 指标编码
     */
    protected String kpiCode;

    /**
     * 指标类型，如整型、字符串等
     */
    protected String kpiType;

    /**
     * 指标默认值
     */
    protected String kpiDefaultValue;

    protected String kpiDesc;

    protected String kpiGroupId;

    /**
     * 用于显示指标组名称
     */
    protected String kpiGroupName;

    /**
     * 取数类型：0，数据源；1，接口
     */
    protected String fetchType;

    /**
     * 指标值来源，可以是字段或参数的id
     */
    protected String kpiValueSource;

    protected String kpiValueSourceName;

    protected String kpiValueSourceCode;

    protected String dbId;

    protected String dbAlias;

    protected String dbType;

    protected String tableId;

    protected String tableCode;

    protected String fetchSql;

    protected String apiId;

    protected String apiName;

    protected List<KpiFetchLimiters> kpiFetchLimitersList;

    public String getKpiId() {
        return kpiId;
    }

    public void setKpiId(String kpiId) {
        this.kpiId = kpiId;
    }

    public String getKpiName() {
        return kpiName;
    }

    public void setKpiName(String kpiName) {
        this.kpiName = kpiName;
    }

    public String getKpiCode() {
        return kpiCode;
    }

    public void setKpiCode(String kpiCode) {
        this.kpiCode = kpiCode;
    }

    public String getKpiType() {
        return kpiType;
    }

    public void setKpiType(String kpiType) {
        this.kpiType = kpiType;
    }

    public String getKpiDefaultValue() {
        return kpiDefaultValue;
    }

    public void setKpiDefaultValue(String kpiDefaultValue) {
        this.kpiDefaultValue = kpiDefaultValue;
    }

    public String getKpiDesc() {
        return kpiDesc;
    }

    public void setKpiDesc(String kpiDesc) {
        this.kpiDesc = kpiDesc;
    }

    public String getKpiGroupId() {
        return kpiGroupId;
    }

    public void setKpiGroupId(String kpiGroupId) {
        this.kpiGroupId = kpiGroupId;
    }

    public String getKpiGroupName() {
        return kpiGroupName;
    }

    public void setKpiGroupName(String kpiGroupName) {
        this.kpiGroupName = kpiGroupName;
    }

    public String getFetchType() {
        return fetchType;
    }

    public void setFetchType(String fetchType) {
        this.fetchType = fetchType;
    }

    public String getKpiValueSource() {
        return kpiValueSource;
    }

    public void setKpiValueSource(String kpiValueSource) {
        this.kpiValueSource = kpiValueSource;
    }

    public String getKpiValueSourceName() {
        return kpiValueSourceName;
    }

    public void setKpiValueSourceName(String kpiValueSourceName) {
        this.kpiValueSourceName = kpiValueSourceName;
    }

    public String getKpiValueSourceCode() {
        return kpiValueSourceCode;
    }

    public void setKpiValueSourceCode(String kpiValueSourceCode) {
        this.kpiValueSourceCode = kpiValueSourceCode;
    }

    public String getDbId() {
        return dbId;
    }

    public void setDbId(String dbId) {
        this.dbId = dbId;
    }

    public String getDbAlias() {
        return dbAlias;
    }

    public void setDbAlias(String dbAlias) {
        this.dbAlias = dbAlias;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public String getTableCode() {
        return tableCode;
    }

    public void setTableCode(String tableCode) {
        this.tableCode = tableCode;
    }

    public String getFetchSql() {
        return fetchSql;
    }

    public void setFetchSql(String fetchSql) {
        this.fetchSql = fetchSql;
    }

    public String getApiId() {
        return apiId;
    }

    public void setApiId(String apiId) {
        this.apiId = apiId;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public List<KpiFetchLimiters> getKpiFetchLimitersList() {
        return kpiFetchLimitersList;
    }

    public void setKpiFetchLimitersList(List<KpiFetchLimiters> kpiFetchLimitersList) {
        this.kpiFetchLimitersList = kpiFetchLimitersList;
    }

    @Override
    public String getId() {
        return this.kpiId;
    }

    @Override
    public String getCode() {
        return this.kpiCode;
    }

    @Override
    public String getName() {
        return this.kpiName;
    }

    @Override
    public String getType() {
        return this.kpiType;
    }

    @Override
    public String getDefaultValue() {
        return this.kpiDefaultValue;
    }

    @Override
    public String getVarCode() {
        return this.kpiCode;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("KpiDefinition{");
        sb.append("kpiId='").append(kpiId).append('\'');
        sb.append(", kpiName='").append(kpiName).append('\'');
        sb.append(", kpiCode='").append(kpiCode).append('\'');
        sb.append(", kpiType='").append(kpiType).append('\'');
        sb.append(", kpiDefaultValue='").append(kpiDefaultValue).append('\'');
        sb.append(", kpiDesc='").append(kpiDesc).append('\'');
        sb.append(", kpiGroupId='").append(kpiGroupId).append('\'');
        sb.append(", kpiGroupName='").append(kpiGroupName).append('\'');
        sb.append(", fetchType='").append(fetchType).append('\'');
        sb.append(", kpiValueSource='").append(kpiValueSource).append('\'');
        sb.append(", kpiValueSourceName='").append(kpiValueSourceName).append('\'');
        sb.append(", kpiValueSourceCode='").append(kpiValueSourceCode).append('\'');
        sb.append(", dbId='").append(dbId).append('\'');
        sb.append(", dbAlias='").append(dbAlias).append('\'');
        sb.append(", dbType='").append(dbType).append('\'');
        sb.append(", tableId='").append(tableId).append('\'');
        sb.append(", tableCode='").append(tableCode).append('\'');
        sb.append(", fetchSql='").append(fetchSql).append('\'');
        sb.append(", apiId='").append(apiId).append('\'');
        sb.append(", apiName='").append(apiName).append('\'');
        sb.append(", kpiFetchLimitersList=").append(kpiFetchLimitersList);
        sb.append(", createDate=").append(createDate);
        sb.append(", createPerson='").append(createPerson).append('\'');
        sb.append(", updatePerson='").append(updatePerson).append('\'');
        sb.append(", updateDate=").append(updateDate);
        sb.append('}');
        return sb.toString();
    }
}
