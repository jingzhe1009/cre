package com.bonc.frame.entity.modelImportAndExport.modelExport.entity.report;


/**
 * @Author: wangzhengbao
 * @DATE: 2020/2/11 20:02
 */
public class KpiReport extends BaseReport {
    private String kpiId;
    private String kpiName;
    private String kpiDesc;

    @Override
    public String reportId() {
        return kpiId;
    }

    @Override
    public String reportName() {
        return kpiName;
    }

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

    public String getKpiDesc() {
        return kpiDesc;
    }

    public void setKpiDesc(String kpiDesc) {
        this.kpiDesc = kpiDesc;
    }
}
