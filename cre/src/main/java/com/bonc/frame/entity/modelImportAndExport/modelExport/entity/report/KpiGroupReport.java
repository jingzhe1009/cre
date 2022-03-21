package com.bonc.frame.entity.modelImportAndExport.modelExport.entity.report;

import com.bonc.frame.entity.kpi.KpiDefinition;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/2/12 18:08
 */
public class KpiGroupReport extends BaseReport {
    private String kpiGroupName;
    private String kpiGroupId;
    private String kpiGroupDesc;
    private Map<String, KpiReport> kpi;

    public void addKpi(KpiDefinition kpiDefinition) {
        if (kpiDefinition != null) {
            String kpiId = kpiDefinition.getKpiId();
            if (kpi == null) {
                kpi = new HashMap<>();
            }
            if (!kpi.containsKey(kpiId)) {
                KpiReport apiReport = new KpiReport();
                apiReport.setKpiId(kpiId);
                apiReport.setKpiDesc(kpiDefinition.getKpiDesc());
                apiReport.setKpiName(kpiDefinition.getKpiName());
                kpi.put(kpiId, apiReport);
            }
        }
    }

    @Override
    public String reportId() {
        return kpiGroupId;
    }

    @Override
    public String reportName() {
        return kpiGroupName;
    }

    public String getKpiGroupName() {
        return kpiGroupName;
    }

    public void setKpiGroupName(String kpiGroupName) {
        this.kpiGroupName = kpiGroupName;
    }

    public String getKpiGroupId() {
        return kpiGroupId;
    }

    public void setKpiGroupId(String kpiGroupId) {
        this.kpiGroupId = kpiGroupId;
    }

    public String getKpiGroupDesc() {
        return kpiGroupDesc;
    }

    public void setKpiGroupDesc(String kpiGroupDesc) {
        this.kpiGroupDesc = kpiGroupDesc;
    }

    public Map<String, KpiReport> getKpi() {
        return kpi;
    }

    public void setKpi(Map<String, KpiReport> kpi) {
        this.kpi = kpi;
    }
}
