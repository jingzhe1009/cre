package com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity;

import com.bonc.frame.entity.modelImportAndExport.ImportAndExportOperateLog;
import com.bonc.frame.entity.modelImportAndExport.modelExport.entity.ExportConstant;
import com.bonc.frame.entity.modelImportAndExport.modelExport.entity.report.BaseReport;
import com.bonc.frame.entity.modelImportAndExport.modelExport.entity.report.ModelReport;
import com.bonc.frame.entity.modelImportAndExport.modelExport.entity.report.ModelVersionReport;
import com.bonc.frame.entity.modelImportAndExport.modelImport.ImportParam;
import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.importContext.ImportContext;
import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.report.ImportReport;
import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.report.ObjectImportReport;
import com.bonc.frame.entity.rule.RuleDetailWithBLOBs;
import com.bonc.frame.util.ExportUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/3/25 11:55
 */
public class ImportResult {
    private ImportAndExportOperateLog fromInfo;
    private ImportAndExportOperateLog toInfo;
    private List<ImportParam> importParams; //参数
    private ImportReport report;
    private Map<String, Map<String, BaseReport>> lack;
    private Map<String, Map<String, ObjectImportReport>> failImportData;


    // ------------------------------ report ----------------------------
    public void add(String type, ImportAdjustObject object, ImportContext context) {
        if (ExportConstant.FOLDER.equals(type)) {
            Object toData = object.getToData();
            String objectId = ExportUtil.getObjectId(type, toData);
            if (StringUtils.isNotBlank(objectId) && ExportConstant.FOLDER_PUB_ID.equals(objectId)) {
                return;
            }
        }
        try {
            if (report == null) {
                report = new ImportReport();
            }
            report.add(type, object, context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // ------------------------------- lack ----------------------------
    public void putlackData(String type, Object o, String fromType, Object fromObject) {
        try {
            addlackIdWithFrom(type, o, fromType, fromObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void putlackDataBatch(String type, List<Object> os, String fromType, Object fromObject) throws Exception {
        if (os == null || os.isEmpty()) {
            return;
        }

        for (Object o : os) {
            addlackIdWithFrom(type, o, fromType, fromObject);
        }
    }

    private void addlackIdWithFrom(String type, Object o, String fromObjType, Object fromObj) {
        if (StringUtils.isBlank(type)) {
            return;
        }
        if (o instanceof String) {
            o = ExportUtil.paseObjectIdToObject(type, (String) o);
        }
        if (lack == null) {
            lack = new HashMap<>();
        }
        Map<String, BaseReport> map = lack.get(type);
        if (map == null) {
            map = new HashMap<>();
            lack.put(type, map);
        }
        if (ExportConstant.MODEL_VERSION.equals(type)) {
            if (o instanceof RuleDetailWithBLOBs) {
                addModelVersionDataIdWithFrom(type, (RuleDetailWithBLOBs) o, fromObjType, fromObj);
            }
            return;
        }
        String key = ExportUtil.getObjectId(type, o);
        if (StringUtils.isBlank(key)) {
            key = ExportUtil.getObjectName(type, o);
        }
        BaseReport report = map.get(key);
        if (report == null) {
            report = ExportUtil.paseReport(type, o);
        }
        if (report != null) {
            report.addObjectFrom(fromObjType, fromObj);
            map.put(key, report);
        }
    }

    private void addModelVersionDataIdWithFrom(String type, RuleDetailWithBLOBs ruleDetailWithBLOBs, String
            fromObjType, Object fromObj) {
        if (ruleDetailWithBLOBs != null) {
            if (lack == null) {
                lack = new HashMap<>();
            }
            Map<String, BaseReport> map = lack.get(ExportConstant.MODEL_HEADER);

            String modelId = ruleDetailWithBLOBs.getRuleName();
            if (map == null) {
                map = new HashMap<>();
                lack.put(ExportConstant.MODEL_HEADER, map);
            }
            if (map.containsKey(modelId)) {
                ModelReport modelReport = (ModelReport) map.get(modelId);
                if (modelReport != null) {
                    modelReport.addVersion(ruleDetailWithBLOBs);

                    String versionId = ruleDetailWithBLOBs.getRuleId();
                    Map<String, ModelVersionReport> versionList = modelReport.getVersion();
                    if (versionList != null) {
                        ModelVersionReport modelVersionReport = versionList.get(versionId);
                        if (modelVersionReport != null)
                            modelVersionReport.addObjectFrom(fromObjType, fromObj);
                    }

                }
            } else {
                ModelReport modelReport = new ModelReport();
                modelReport.setRuleName(ruleDetailWithBLOBs.getRuleName());
                modelReport.setModelName(modelId);
                modelReport.addVersion(ruleDetailWithBLOBs);

                String versionId = ruleDetailWithBLOBs.getRuleId();
                Map<String, ModelVersionReport> versionList = modelReport.getVersion();
                if (versionList != null) {
                    ModelVersionReport modelVersionReport = versionList.get(versionId);
                    if (modelVersionReport != null)
                        modelVersionReport.addObjectFrom(fromObjType, fromObj);
                }
                map.put(modelId, modelReport);
            }
        }
    }

    // ---------------------------------------- failImportData --------------------------
    public void putFailImportData(ImportAdjustObject importAdjustObject) {
        if (importAdjustObject == null) {
            return;
        }
        String objectType = importAdjustObject.getType();
        String id = importAdjustObject.getId();
        if (StringUtils.isBlank(objectType) || StringUtils.isBlank(id)) {
            return;
        }
        ObjectImportReport objectImportReport = importAdjustObject.paseObjectReport();
        failImportData = putObjectToMap(failImportData, objectType, id, objectImportReport);
    }

    private <E> Map<String, Map<String, E>> putObjectToMap(Map<String, Map<String, E>> map, String key1, String key2, E o) {
        if (StringUtils.isBlank(key1) || o == null) {
            return map;
        }
        if (map == null) {
            map = new HashMap<>();
        }
        Map<String, E> map1 = map.get(key1);
        if (map1 == null) {
            map1 = new HashMap<>();
            map.put(key1, map1);
        }
        if (!StringUtils.isBlank(key2)) {
            map1.put(key2, o);
        }
        return map;
    }

    // ----------------------------------------get set ------------------------


    public ImportAndExportOperateLog getFromInfo() {
        return fromInfo;
    }

    public void setFromInfo(ImportAndExportOperateLog fromInfo) {
        this.fromInfo = fromInfo;
    }

    public ImportAndExportOperateLog getToInfo() {
        return toInfo;
    }

    public void setToInfo(ImportAndExportOperateLog toInfo) {
        this.toInfo = toInfo;
    }

    public ImportReport getReport() {
        return report;
    }

    public void setReport(ImportReport report) {
        this.report = report;
    }

    public Map<String, Map<String, BaseReport>> getLack() {
        return lack;
    }

    public void setLack(Map<String, Map<String, BaseReport>> lack) {
        this.lack = lack;
    }

    public List<ImportParam> getImportParams() {
        return importParams;
    }

    public void setImportParams(List<ImportParam> importParams) {
        this.importParams = importParams;
    }

    public Map<String, Map<String, ObjectImportReport>> getFailImportData() {
        return failImportData;
    }

    public void setFailImportData(Map<String, Map<String, ObjectImportReport>> failImportData) {
        this.failImportData = failImportData;
    }
}
