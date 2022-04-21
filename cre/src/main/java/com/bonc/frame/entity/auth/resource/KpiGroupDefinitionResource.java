package com.bonc.frame.entity.auth.resource;

public class KpiGroupDefinitionResource extends DataResource implements Cloneable{

    private String kpiGroupId;

    private String kpiGroupName;

    private String kpiGroupDesc;

    private String createDate;

    private String endDate;

    public String getKpiGroupId() {
        return kpiGroupId;
    }

    public void setKpiGroupId(String kpiGroupId) {
        this.kpiGroupId = kpiGroupId;
        super.resourceId = kpiGroupId;
    }

    public String getKpiGroupName() {
        return kpiGroupName;
    }

    public void setKpiGroupName(String kpiGroupName) {
        this.kpiGroupName = kpiGroupName;
    }

    public String getKpiGroupDesc() {
        return kpiGroupDesc;
    }

    public void setKpiGroupDesc(String kpiGroupDesc) {
        this.kpiGroupDesc = kpiGroupDesc;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("KpiGroupDefinitionResource{");
        sb.append(", kpiGroupId='").append(kpiGroupId).append('\'');
        sb.append(", kpiGroupName='").append(kpiGroupName).append('\'');
        sb.append(", kpiGroupDesc='").append(kpiGroupDesc).append('\'');
        sb.append(", resourceId='").append(resourceId).append('\'');
        sb.append(", resources=").append(resources);
        sb.append('}');
        return sb.toString();
    }
}
