package com.bonc.frame.entity.kpi;

import com.bonc.frame.entity.BaseEntity;

import java.util.Objects;

/**
 * 指标组
 *
 * @author yedunyao
 * @date 2019/10/15 13:51
 */
public class KpiGroup extends BaseEntity {

    private String kpiGroupId;

    private String kpiGroupName;

    private String kpiGroupDesc;

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

    public String getKpiGroupDesc() {
        return kpiGroupDesc;
    }

    public void setKpiGroupDesc(String kpiGroupDesc) {
        this.kpiGroupDesc = kpiGroupDesc;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("KpiGroup{");
        sb.append("kpiGroupId='").append(kpiGroupId).append('\'');
        sb.append(", kpiGroupName='").append(kpiGroupName).append('\'');
        sb.append(", kpiGroupDesc='").append(kpiGroupDesc).append('\'');
        sb.append(", createDate=").append(createDate);
        sb.append(", createPerson='").append(createPerson).append('\'');
        sb.append(", updatePerson='").append(updatePerson).append('\'');
        sb.append(", updateDate=").append(updateDate);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KpiGroup kpiGroup = (KpiGroup) o;
        return Objects.equals(kpiGroupName, kpiGroup.kpiGroupName);
    }


}
