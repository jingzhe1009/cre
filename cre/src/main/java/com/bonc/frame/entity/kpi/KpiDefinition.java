package com.bonc.frame.entity.kpi;

import com.bonc.frame.entity.BaseEntity;
import com.bonc.frame.util.CollectionUtil;
import io.swagger.annotations.ApiModelProperty;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 指标
 *
 * @author yedunyao
 * @date 2019/10/15 13:56
 */
public class KpiDefinition extends BaseEntity {

    private static final long serialVersionUID = -1103127725573851673L;
    private String kpiId;

    private String kpiName;

    /**
     * 指标编码 不能与参数和其他指标编码重复
     */
    private String kpiCode;
    //kpi默认值
    private String kpiDefaultValue;

    /**
     * 指标类型，如2:整型,4:浮点型、1:字符串
     */
    private String kpiType;
    /**
     * 指标的域, 该字段存在 KPI_RULE表中,表示,当前模型中指标的域的范围
     */
    private String kpiRange;

    private String kpiDesc;

    private String kpiGroupId;


    /**
     * 用于显示指标组名称
     */
    private String kpiGroupName;

    /**
     * 取数类型：0，数据源；1，接口； 2. 输入指标
     */
    private String fetchType;
    /**
     *
     * 关联规则集
     * */
    private String kpiRuleSet;
    /**
     * 指标值来源，可以是字段或参数的id
     */
    private String kpiValueSource;

    private String kpiValueSourceName;
    // ColumnCode
    private String kpiValueSourceCode;

    private String dbId;

    private String dbAlias;

    private String dbType;

    private String tableId;

    private String tableCode;

    private String fetchSql;

    private String apiId;

    private String apiName;

    @ApiModelProperty("部门编号")
    private String deptId;

    @ApiModelProperty("部门名称")
    private String deptName;

    @ApiModelProperty("合作方编号")
    private String partnerCode;

    @ApiModelProperty("合作方名称")
    private String partnerName;

    @ApiModelProperty("产品编号")
    private String productCode;

    @ApiModelProperty("产品名称")
    private String productName;

    @ApiModelProperty("系统编号")
    private String systemCode;

    @ApiModelProperty("系统名称")
    private String systemName;

    @ApiModelProperty("平台用户创建人")
    private String platformCreateUserJobNumber;
    @ApiModelProperty("平台用户修改人")
    private String platformUpdateUserJobNumber;

    private List<KpiFetchLimiters> kpiFetchLimitersList;

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

    public String getKpiDefaultValue() {
        return kpiDefaultValue;
    }

    public void setKpiDefaultValue(String kpiDefaultValue) {
        this.kpiDefaultValue = kpiDefaultValue;
    }

    public String getKpiRange() {
        return kpiRange;
    }

    public void setKpiRange(String kpiRange) {
        this.kpiRange = kpiRange;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getPartnerCode() {
        return partnerCode;
    }

    public void setPartnerCode(String partnerCode) {
        this.partnerCode = partnerCode;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSystemCode() {
        return systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getPlatformCreateUserJobNumber() {
        return platformCreateUserJobNumber;
    }

    public void setPlatformCreateUserJobNumber(String platformCreateUserJobNumber) {
        this.platformCreateUserJobNumber = platformCreateUserJobNumber;
    }

    public String getPlatformUpdateUserJobNumber() {
        return platformUpdateUserJobNumber;
    }

    public void setPlatformUpdateUserJobNumber(String platformUpdateUserJobNumber) {
        this.platformUpdateUserJobNumber = platformUpdateUserJobNumber;
    }
    public String getKpiRuleSet() {
        return kpiRuleSet;
    }

    public void setKpiRuleSet(String kpiRuleSet) {
        this.kpiRuleSet = kpiRuleSet;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("KpiDefinition{");
        sb.append("kpiId='").append(kpiId).append('\'');
        sb.append(", kpiName='").append(kpiName).append('\'');
        sb.append(", kpiCode='").append(kpiCode).append('\'');
        sb.append(", kpiDefaultValue='").append(kpiDefaultValue).append('\'');
        sb.append(", kpiType='").append(kpiType).append('\'');
        sb.append(", kpiRange='").append(kpiRange).append('\'');
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
        sb.append(", deptId='").append(deptId).append('\'');
        sb.append(", deptName='").append(deptName).append('\'');
        sb.append(", partnerCode='").append(partnerCode).append('\'');
        sb.append(", partnerName='").append(partnerName).append('\'');
        sb.append(", productCode='").append(productCode).append('\'');
        sb.append(", productName='").append(productName).append('\'');
        sb.append(", systemCode='").append(systemCode).append('\'');
        sb.append(", systemName='").append(systemName).append('\'');
        sb.append(", platformCreateUserJobNumber='").append(platformCreateUserJobNumber).append('\'');
        sb.append(", platformUpdateUserJobNumber='").append(platformUpdateUserJobNumber).append('\'');
        sb.append(", kpiFetchLimitersList=").append(kpiFetchLimitersList);
        sb.append(", createDate=").append(createDate);
        sb.append(", createPerson='").append(createPerson).append('\'');
        sb.append(", kpiRuleSet='").append(kpiRuleSet).append('\'');
        sb.append(", updatePerson='").append(updatePerson).append('\'');
        sb.append(", updateDate=").append(updateDate);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KpiDefinition that = (KpiDefinition) o;
        return Objects.equals(kpiCode, that.kpiCode) &&
                Objects.equals(kpiDefaultValue, that.kpiDefaultValue) &&
                Objects.equals(kpiType, that.kpiType) &&
                Objects.equals(kpiGroupId, that.kpiGroupId) &&
                Objects.equals(fetchType, that.fetchType) &&
                Objects.equals(kpiRuleSet, that.kpiRuleSet) &&
                Objects.equals(kpiValueSource, that.kpiValueSource) &&
                Objects.equals(kpiValueSourceCode, that.kpiValueSourceCode) &&
                Objects.equals(dbId, that.dbId) &&
                Objects.equals(dbType, that.dbType) &&
                Objects.equals(tableId, that.tableId) &&
                Objects.equals(tableCode, that.tableCode) &&
                Objects.equals(apiId, that.apiId) &&
                equalsKpiFetchLimitersList(kpiFetchLimitersList, that.kpiFetchLimitersList);
    }

    private boolean equalsKpiFetchLimitersList(List<KpiFetchLimiters> kpiFetchLimitersList, List<KpiFetchLimiters> thatKpiFetchLimitersList) {
        if (!CollectionUtil.isEmpty(kpiFetchLimitersList) && !CollectionUtil.isEmpty(thatKpiFetchLimitersList)) {
            Set<KpiFetchLimiters> KpiFetchLimitersSet = new HashSet<>(kpiFetchLimitersList);
            Set<KpiFetchLimiters> thatKpiFetchLimitersSet = new HashSet<>(thatKpiFetchLimitersList);
            return Objects.equals(KpiFetchLimitersSet, thatKpiFetchLimitersSet);
        }
        return CollectionUtil.isEmpty(kpiFetchLimitersList) && CollectionUtil.isEmpty(thatKpiFetchLimitersList);
    }


}
