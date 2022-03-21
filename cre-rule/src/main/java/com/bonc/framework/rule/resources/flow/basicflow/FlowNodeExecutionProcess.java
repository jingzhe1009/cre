package com.bonc.framework.rule.resources.flow.basicflow;

import com.alibaba.fastjson.JSON;
import com.bonc.framework.rule.kpi.KpiLog;

import java.util.ArrayList;
import java.util.List;

/**
 * 节点的执行过程
 *
 * @Author: wangzhengbao
 * @DATE: 2020/9/14 9:41
 */
public class FlowNodeExecutionProcess {
    private List<KpiLog> kpiLog; // 指标的执行日志

    public void addKpiLog(KpiLog addKpiLog) {
        if (kpiLog == null) {
            kpiLog = new ArrayList<>();
        }
        kpiLog.add(addKpiLog);
    }

    public void addAllKpiLog(List<KpiLog> addKpiLogs) {
        if (kpiLog == null) {
            kpiLog = new ArrayList<>();
        }
        kpiLog.addAll(addKpiLogs);
    }

    public List<KpiLog> getKpiLog() {
        return kpiLog;
    }

    public void setKpiLog(List<KpiLog> kpiLog) {
        this.kpiLog = kpiLog;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
