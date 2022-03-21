package com.bonc.frame.entity.modelImportAndExport.modelExport.entity.report;

import com.bonc.frame.entity.api.ApiConf;
import com.bonc.frame.entity.commonresource.ApiGroup;
import com.bonc.frame.entity.commonresource.ModelGroup;
import com.bonc.frame.entity.commonresource.VariableGroup;
import com.bonc.frame.entity.kpi.KpiDefinition;
import com.bonc.frame.entity.kpi.KpiGroup;
import com.bonc.frame.entity.rule.RuleDetailHeader;
import com.bonc.frame.entity.rule.RuleDetailWithBLOBs;
import com.bonc.frame.entity.variable.Variable;
import com.bonc.frame.entity.modelImportAndExport.modelExport.entity.ExportConstant;
import com.bonc.frame.entity.modelImportAndExport.modelExport.entity.info.ExportStatistics;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 公共的导入导出报告
 *
 * @Author: wangzhengbao
 * @DATE: 2020/1/9 18:07
 */
public class PubReport {
    Log log = LogFactory.getLog(PubReport.class);

    private ExportStatistics pubStatistics = new ExportStatistics();

    private Map<String, ModelGroupReport> modelGroup;


    private Map<String, ApiGroupReport> apiGroup;


    private Map<String, KpiGroupReport> kpiGroup;


    private Map<String, VariableGroupReport> variableGroup;

    public boolean add(String type, Object o) throws Exception {
        if (StringUtils.isBlank(type)) {
            return false;
        }
        if (o == null) {
            return false;
        }
        if (pubStatistics == null) {
            pubStatistics = new ExportStatistics();
        }
        pubStatistics.put(type, o);
        switch (type) {
            case ExportConstant.MODEL_GROUP:
                if (o instanceof ModelGroup) {
                    addModelGroup((ModelGroup) o);
                } else {
                    log.error("数据的类型不匹配,Object:" + o);
                    throw new Exception("数据的类型不匹配,Object:" + o);
                }
                break;
            case ExportConstant.MODEL_HEADER:
                if (o instanceof RuleDetailHeader) {
                    addModelHeader((RuleDetailHeader) o);
                } else {
                    log.error("数据的类型不匹配,Object:" + o);
                    throw new Exception("数据的类型不匹配,Object:" + o);
                }
                break;
            case ExportConstant.MODEL_VERSION:
                if (o instanceof RuleDetailWithBLOBs) {
                    addModelVersion((RuleDetailWithBLOBs) o);
                } else {
                    log.error("数据的类型不匹配,Object:" + o);
                    throw new Exception("数据的类型不匹配,Object:" + o);
                }
                break;
//            case ExportConstant.VARIABLE_GROUP:
//                if (o instanceof VariableGroup) {
//                    addVariableGroup((VariableGroup) o);
//                } else {
//                    log.error("数据的类型不匹配,Object:" + o);
//                    throw new Exception("数据的类型不匹配,Object:" + o);
//                }
//                break;
//            case ExportConstant.VARIABLE:
//                if (o instanceof Variable) {
//                    addVariable((Variable) o);
//                } else {
//                    log.error("数据的类型不匹配,Object:" + o);
//                    throw new Exception("数据的类型不匹配,Object:" + o);
//                }
//                break;
//            case ExportConstant.API_GROUP:
//                if (o instanceof ApiGroup) {
//                    addApiGroup((ApiGroup) o);
//                } else {
//                    log.error("数据的类型不匹配,Object:" + o);
//                    throw new Exception("数据的类型不匹配,Object:" + o);
//                }
//                break;
//            case ExportConstant.API:
//                if (o instanceof ApiConf) {
//                    addApi((ApiConf) o);
//                } else {
//                    log.error("数据的类型不匹配,Object:" + o);
//                    throw new Exception("数据的类型不匹配,Object:" + o);
//                }
//                break;
//            case ExportConstant.KPI_GROUP:
//                if (o instanceof KpiGroup) {
//                    addKpiGroup((KpiGroup) o);
//                } else {
//                    log.error("数据的类型不匹配,Object:" + o);
//                    throw new Exception("数据的类型不匹配,Object:" + o);
//                }
//                break;
//            case ExportConstant.KPI:
//                if (o instanceof KpiDefinition) {
//                    addKpi((KpiDefinition) o);
//                } else {
//                    log.error("数据的类型不匹配,Object:" + o);
//                    throw new Exception("数据的类型不匹配,Object:" + o);
//                }
//                break;
//            default:
//                log.error("数据的类型不匹配,Object:" + o);
//                throw new Exception("数据的类型不匹配,Object:" + o);
        }
        return true;
    }

    private void addModelHeader(RuleDetailHeader ruleDetailWithBLOBs) {
        if (ruleDetailWithBLOBs == null) {
            return;
        }
        if (modelGroup == null) {
            modelGroup = new HashMap<>();
        }
        String modelGroupId = ruleDetailWithBLOBs.getModelGroupId();
        if (!modelGroup.containsKey(modelGroupId)) {
            addModelGroup(modelGroupId);
        }
        ModelGroupReport modelGroupReport = modelGroup.get(modelGroupId);
        if (modelGroupReport != null) {
            modelGroupReport.addModelHeader(ruleDetailWithBLOBs);
        }
    }

    private void addModelVersion(RuleDetailWithBLOBs ruleDetailWithBLOBs) {
        if (ruleDetailWithBLOBs == null) {
            return;
        }
        if (modelGroup == null) {
            modelGroup = new HashMap<>();
        }
        String modelGroupId = ruleDetailWithBLOBs.getModelGroupId();
        if (StringUtils.isBlank(modelGroupId)) {
            modelGroupId = "default";
        }
        if (!modelGroup.containsKey(modelGroupId)) {
            addModelGroup(modelGroupId);
        }
        ModelGroupReport modelGroupReport = modelGroup.get(modelGroupId);
        if (modelGroupReport != null) {
            modelGroupReport.addModelVersion(ruleDetailWithBLOBs);
        }
    }

    private void addKpi(KpiDefinition kpi) {
        if (kpi == null) {
            return;
        }
        if (kpiGroup == null) {
            kpiGroup = new HashMap<>();
        }
        String kpiGroupId = kpi.getKpiGroupId();
        if (StringUtils.isBlank(kpiGroupId)) {
            kpiGroupId = "default";
        }
        if (!kpiGroup.containsKey(kpiGroupId)) {
            addKpiGroup(kpiGroupId);
        }
        KpiGroupReport kpiGroupReport = kpiGroup.get(kpiGroupId);
        if (kpiGroupReport != null) {
            kpiGroupReport.addKpi(kpi);
        }
    }

    private void addVariable(Variable variable) {
        if (variable == null) {
            return;
        }
        if (variableGroup == null) {
            variableGroup = new HashMap<>();
        }
        String variableGroupId = variable.getVariableGroupId();
        if (StringUtils.isBlank(variableGroupId)) {
            variableGroupId = "default";
        }
        if (!variableGroup.containsKey(variableGroupId)) {
            addVariableGroup(variableGroupId);
        }
        VariableGroupReport folderReport = variableGroup.get(variableGroupId);
        if (folderReport != null) {
            folderReport.addVariable(variable);
        }
    }

    private void addApi(ApiConf apiConf) {
        if (apiConf == null) {
            return;
        }
        if (apiGroup == null) {
            apiGroup = new HashMap<>();
        }
        String apiGroupId = apiConf.getApiGroupId();
        if (StringUtils.isBlank(apiGroupId)) {
            apiGroupId = "default";
        }
        if (!apiGroup.containsKey(apiGroupId)) {
            addApiGroup(apiGroupId);
        }
        ApiGroupReport apiGroupReport = apiGroup.get(apiGroupId);
        if (apiGroupReport != null) {
            apiGroupReport.addApi(apiConf);
        }
    }

    private void addApiGroup(String apiGroupId) {
        if (!StringUtils.isBlank(apiGroupId)) {
            ApiGroupReport modelGroupReport = new ApiGroupReport();
            modelGroupReport.setApiGroupId(apiGroupId);
            // TODO : 添加模型组名称
            apiGroup.put(apiGroupId, modelGroupReport);
        }
    }

    private void addApiGroup(ApiGroup apiGroup) {
        if (apiGroup != null) {
            String apiGroupId = apiGroup.getApiGroupId();
            if (!StringUtils.isBlank(apiGroupId)) {
                if (this.apiGroup == null) {
                    this.apiGroup = new HashMap<>();
                }
                if (!this.apiGroup.containsKey(apiGroupId)) {
                    ApiGroupReport modelGroupReport = new ApiGroupReport();
                    modelGroupReport.setApiGroupId(apiGroupId);
                    modelGroupReport.setApiGroupName(apiGroup.getApiGroupName());
                    this.apiGroup.put(apiGroupId, modelGroupReport);
                }
            }
        }
    }

    private void addKpiGroup(String kpiGroupId) {
        if (!StringUtils.isBlank(kpiGroupId)) {
            KpiGroupReport kpiGroupReport = new KpiGroupReport();
            kpiGroupReport.setKpiGroupId(kpiGroupId);
            // TODO : 添加模型组名称
            kpiGroup.put(kpiGroupId, kpiGroupReport);
        }
    }

    private void addKpiGroup(KpiGroup kpiGroup) {
        if (kpiGroup == null) {
            return;
        }
        String kpiGroupId = kpiGroup.getKpiGroupId();
        if (!StringUtils.isBlank(kpiGroupId)) {
            if (this.kpiGroup == null) {
                this.kpiGroup = new HashMap<>();
            }
            if (!this.kpiGroup.containsKey(kpiGroupId)) {
                KpiGroupReport kpiGroupReport = new KpiGroupReport();
                kpiGroupReport.setKpiGroupId(kpiGroupId);
                kpiGroupReport.setKpiGroupName(kpiGroup.getKpiGroupName());
                kpiGroupReport.setKpiGroupDesc(kpiGroup.getKpiGroupDesc());
                this.kpiGroup.put(kpiGroupId, kpiGroupReport);
            }
        }
    }

    private void addVariableGroup(String variableGroupId) {
        if (!StringUtils.isBlank(variableGroupId)) {
            VariableGroupReport variableGroupReport = new VariableGroupReport();
            variableGroupReport.setVariableGroupId(variableGroupId);
            // TODO : 添加模型组名称
            variableGroup.put(variableGroupId, variableGroupReport);
        }
    }

    private void addVariableGroup(VariableGroup variableGroup) {
        if (variableGroup == null) {
            return;
        }
        String variableGroupId = variableGroup.getVariableGroupId();
        if (!StringUtils.isBlank(variableGroupId)) {
            if (this.variableGroup == null) {
                this.variableGroup = new HashMap<>();
            }
            if (!this.variableGroup.containsKey(variableGroupId)) {
                VariableGroupReport variableGroupReport = new VariableGroupReport();
                variableGroupReport.setVariableGroupId(variableGroupId);
                variableGroupReport.setVariableGroupName(variableGroup.getVariableGroupName());
                this.variableGroup.put(variableGroupId, variableGroupReport);
            }
        }
    }

    private void addModelGroup(String modelGroupId) {
        if (!StringUtils.isBlank(modelGroupId)) {
            ModelGroupReport modelGroupReport = new ModelGroupReport();
            modelGroupReport.setModelGroupId(modelGroupId);
            // TODO : 添加模型组名称
            modelGroup.put(modelGroupId, modelGroupReport);
        }
    }

    private void addModelGroup(ModelGroup modelGroup) {
        if (modelGroup == null) {
            return;
        }
        String modelGroupId = modelGroup.getModelGroupId();
        if (!StringUtils.isBlank(modelGroupId)) {
            if (this.modelGroup == null) {
                this.modelGroup = new HashMap<>();
            }
            if (!this.modelGroup.containsKey(modelGroupId)) {
                ModelGroupReport modelGroupReport = new ModelGroupReport();
                modelGroupReport.setModelGroupId(modelGroupId);
                modelGroupReport.setModelGroupName(modelGroup.getModelGroupName());
                this.modelGroup.put(modelGroupId, modelGroupReport);
            }
        }
    }

    public boolean containsModelGroup(String modelGroupId) {
        if (modelGroup == null || modelGroup.isEmpty()) {
            return false;
        }
        return modelGroup.containsKey(modelGroupId);
    }

    public boolean containsKpiGroup(String kpiGroupId) {
        if (kpiGroup == null || kpiGroup.isEmpty()) {
            return false;
        }
        return kpiGroup.containsKey(kpiGroupId);
    }

    public boolean containsApiGroup(String apiGroupId) {
        if (apiGroup == null || apiGroup.isEmpty()) {
            return false;
        }
        return apiGroup.containsKey(apiGroupId);
    }

    public boolean containsVariableGroup(String variableGroupId) {
        if (variableGroup == null || variableGroup.isEmpty()) {
            return false;
        }
        return variableGroup.containsKey(variableGroupId);
    }

    public ExportStatistics getPubStatistics() {
        return pubStatistics;
    }

    public void setPubStatistics(ExportStatistics pubStatistics) {
        this.pubStatistics = pubStatistics;
    }

    public Map<String, ModelGroupReport> getModelGroup() {
        return modelGroup;
    }

    public void setModelGroup(Map<String, ModelGroupReport> modelGroup) {
        this.modelGroup = modelGroup;
    }

    public Map<String, ApiGroupReport> getApiGroup() {
        return apiGroup;
    }

    public void setApiGroup(Map<String, ApiGroupReport> apiGroup) {
        this.apiGroup = apiGroup;
    }

    public Map<String, KpiGroupReport> getKpiGroup() {
        return kpiGroup;
    }

    public void setKpiGroup(Map<String, KpiGroupReport> kpiGroup) {
        this.kpiGroup = kpiGroup;
    }

    public Map<String, VariableGroupReport> getVariableGroup() {
        return variableGroup;
    }

    public void setVariableGroup(Map<String, VariableGroupReport> variableGroup) {
        this.variableGroup = variableGroup;
    }
}
