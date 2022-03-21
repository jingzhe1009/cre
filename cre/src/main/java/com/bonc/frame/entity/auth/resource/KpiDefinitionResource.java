package com.bonc.frame.entity.auth.resource;

/**
 * @author yedunyao
 * @since 2020/3/23 10:46
 */
public class KpiDefinitionResource extends DataResource implements Cloneable {

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

    private String kpiDesc;

    private String kpiGroupId;


    /**
     * 用于显示指标组名称
     */
    private String kpiGroupName;

    /**
     * 取数类型：0，数据源；1，接口
     */
    private String fetchType;

    public String getKpiId() {
        return kpiId;
    }

    public void setKpiId(String kpiId) {
        this.kpiId = kpiId;
        super.resourceId = kpiId;
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

    public String getKpiDefaultValue() {
        return kpiDefaultValue;
    }

    public void setKpiDefaultValue(String kpiDefaultValue) {
        this.kpiDefaultValue = kpiDefaultValue;
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("KpiDefinitionResource{");
        sb.append("kpiId='").append(kpiId).append('\'');
        sb.append(", kpiName='").append(kpiName).append('\'');
        sb.append(", kpiCode='").append(kpiCode).append('\'');
        sb.append(", kpiDefaultValue='").append(kpiDefaultValue).append('\'');
        sb.append(", kpiType='").append(kpiType).append('\'');
        sb.append(", kpiDesc='").append(kpiDesc).append('\'');
        sb.append(", kpiGroupId='").append(kpiGroupId).append('\'');
        sb.append(", kpiGroupName='").append(kpiGroupName).append('\'');
        sb.append(", fetchType='").append(fetchType).append('\'');
        sb.append(", resourceId='").append(resourceId).append('\'');
        sb.append(", resources=").append(resources);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
