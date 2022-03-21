package com.bonc.frame.entity.modelImportAndExport.modelExport.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bonc.frame.entity.modelImportAndExport.ImportAndExportOperateLog;
import com.bonc.frame.entity.modelImportAndExport.modelExport.ExportParam;
import com.bonc.frame.entity.modelImportAndExport.modelExport.entity.data.ResultData;
import com.bonc.frame.entity.modelImportAndExport.modelExport.entity.info.ExportStatistics;
import com.bonc.frame.entity.modelImportAndExport.modelExport.entity.report.*;
import com.bonc.frame.entity.rule.RuleDetailHeader;
import com.bonc.frame.entity.rule.RuleDetailWithBLOBs;
import com.bonc.frame.entity.rulefolder.RuleFolder;
import com.bonc.frame.util.DateFormatUtil;
import com.bonc.frame.util.ExportUtil;
import com.bonc.frame.util.IdUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/1/10 9:38
 */
public class ExportResult {
    Log log = LogFactory.getLog(ExportResult.class);
    List<ExportParam> exportParams;
    private ImportAndExportOperateLog info;
    private ResultData data;
    private ResultReport report = new ResultReport();
    private Map<String, Map<String, BaseReport>> lack;
    //    private ExportDetails dataIdsWithFrom;
    private Map<String, Map<String, BaseReport>> dataIdsWithFrom;

    public Map<String, Object> result() {
        ExportResult result = buildResultObject();
        Map<String, Object> resultMap = new HashMap<>(2);
        resultMap.put("data", JSONObject.toJSONString(result));
        resultMap.put("report", buildResultReport());
        return resultMap;
    }

    public ImportAndExportOperateLog paseExportOperateLog() {
        ExportResult result = buildResultObject();
        if (result == null) {
            return null;
        }
        ImportAndExportOperateLog info = result.getInfo();
        if (info == null) {
            return null;
        }
        ImportAndExportOperateLog importAndExportOperateLog = new ImportAndExportOperateLog();
        importAndExportOperateLog.setLogId(info.getLogId());
        importAndExportOperateLog.setOperateDate(info.getOperateDate());
        importAndExportOperateLog.setOperateType(info.getOperateType());
        importAndExportOperateLog.setSystemVersion(info.getSystemVersion());
        importAndExportOperateLog.setSuccess(info.getSuccess());
        importAndExportOperateLog.setFileName(info.getFileName());
        result.setData(null);
        result.setDataIdsWithFrom(null);
        importAndExportOperateLog.setOperateContent(JSON.toJSONString(result));
        return importAndExportOperateLog;
    }

    private String getFileName(Date date, String id) {
        if (date == null) {
            date = new Date();
        }
        return "export-v2.0-" + DateFormatUtil.fromatDate(date, "yyyyMMddHHmmss") + id + ".json";
    }

//    public boolean exportIsSuccess() {
//        if (info == null) {
//            buildResultObject();
//            if (info == null) {
//                return false;
//            }
//        }
//        return info.isSuccess();
//    }

    public void add(String type, Object o) throws Exception {
        if (StringUtils.isBlank(type)) {
            log.warn("数据类型为null,Object:" + o);
            return;
        }
        if (!containsObject(type, o)) {
            if (data == null) {
                data = new ResultData();
            }
            data.put(type, o);
            if (report == null) {
                report = new ResultReport();
            }
            if (ExportConstant.FOLDER.equals(type)) {
                if (o instanceof RuleFolder) {
                    String objectId = ExportUtil.getObjectId(type, o);
                    if (StringUtils.isNotBlank(objectId) && ExportConstant.FOLDER_PUB_ID.equals(objectId)) {
                        return;
                    }
                }
            }
            report.add(type, o);
        }
    }

    public boolean containsKey(String type, String id) {
        if (StringUtils.isBlank(type)) {
            log.warn("判断主键是否存在的类型为null,id:" + id);
            return false;
        }
        if (data != null) {
            return data.containsKey(type, id);
        }
        return false;
    }

    public boolean containsObject(String type, Object o) {
        if (StringUtils.isBlank(type) || o == null) {
            log.warn("数据类型为null或对象为null,type:" + type + ",o:" + o);
            return false;
        }
        if (data != null) {
            return data.containsObject(type, o);
        }
        return false;
    }

    public void putDataId(String type, Object o, String fromType, Object fromObject) {
        if (dataIdsWithFrom == null) {
            dataIdsWithFrom = new HashMap<>();
        }
        try {
            addDataIdWithFrom(type, o, fromType, fromObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void putDataIdBatch(String type, List<Object> os, String fromType, Object fromObject) {
        if (os == null || os.isEmpty()) {
            return;
        }

        for (Object o : os) {
            addDataIdWithFrom(type, o, fromType, fromObject);
        }
    }

    public void putlackData(String type, Object o, String fromType, Object fromObject) {
        try {
            addlackIdWithFrom(type, o, fromType, fromObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void putlackDataBatch(String type, List<Object> os, String fromType, Object fromObject) {
        if (os == null || os.isEmpty()) {
            return;
        }

        for (Object o : os) {
            addlackIdWithFrom(type, o, fromType, fromObject);
        }
    }

    private Map<String, Map<String, Object>> putObjectToMap(Map<String, Map<String, Object>> map, String type, Object o) {
        if (StringUtils.isBlank(type) || o == null) {
            return map;
        }
        if (map == null) {
            map = new HashMap<>();
        }
        Map<String, Object> map1 = map.get(type);
        if (map1 == null) {
            map1 = new HashMap<>();
            map.put(type, map1);
        }
        String objectId = ExportUtil.getObjectId(type, o);

        switch (type) {
            case ExportConstant.MODEL_HEADER:
                if (o instanceof RuleDetailHeader) {
                    RuleDetailHeader ruleDetailHeader = new RuleDetailHeader();
                    org.springframework.beans.BeanUtils.copyProperties(o, ruleDetailHeader);
                    map1.put(objectId, ruleDetailHeader);
//                    putMODEL_HEADER((RuleDetailHeader) o);
                    return map;
                }
                break;
        }
        if (!StringUtils.isBlank(objectId)) {
            map1.put(objectId, o);
        }
        return map;
    }

    public String buildResultDataToFile() {
        ExportResult result = buildResultObject();
        return JSONObject.toJSONString(result);
    }

    public ExportResult buildResultReport() {
        ExportResult result = buildResultObject();
        result.setDataIdsWithFrom(null);
        result.setData(null);
        return result;
    }

    public ExportResult buildResultObject() {
        if (data == null || data.isEmpty()) {
            return null;
        }
        if (dataIdsWithFrom != null && !dataIdsWithFrom.isEmpty()) {
            for (String type : dataIdsWithFrom.keySet()) {
                Map<String, BaseReport> typeDataIdsWithFrom = dataIdsWithFrom.get(type);
                if (typeDataIdsWithFrom != null && !typeDataIdsWithFrom.isEmpty()) {
                    for (String id : typeDataIdsWithFrom.keySet()) {
                        if (!containsKey(type, id)) {
                            if (lack == null) {
                                lack = new HashMap<>();
                            }
                            Map<String, BaseReport> typeLackIds = lack.get(type);
                            if (typeLackIds == null) {
                                typeLackIds = new HashMap<>();
                                lack.put(type, typeLackIds);
                            }
                            typeLackIds.put(id, typeDataIdsWithFrom.get(id));
                        }
                    }
                }
            }
        }
        if (info == null) {
            info = new ImportAndExportOperateLog();
        }
        String id = IdUtil.createId();
        info.setLogId(id);
        Date date = new Date();
        info.setOperateDate(date);
        info.setOperateType("export");
        info.setSystemVersion("v2.0");
        info.setFileName(getFileName(date, id));
        if (lack == null) {
            info.setSuccess(ImportAndExportOperateLog.ALL_SUCCESS);
        } else {
            info.setSuccess(ImportAndExportOperateLog.FAIL);
        }
        ExportResult resultObject = new ExportResult();
        if (!ImportAndExportOperateLog.FAIL.equals(info.getSuccess())) {
            resultObject.setData(data);
            resultObject.setReport(report);
        }
        resultObject.setLack(lack);
        resultObject.setInfo(info);
        resultObject.setExportParams(exportParams);
        resultObject.setDataIdsWithFrom(dataIdsWithFrom);
        return resultObject;
    }

    public String buildResultReport(ExportResult resultObject) {
        if (resultObject == null) {
            return null;
        }
        ImportAndExportOperateLog info = resultObject.getInfo();
        Map<String, Map<String, BaseReport>> lack = resultObject.getLack();
        ResultReport report = resultObject.getReport();
        if (info == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        Date date = info.getOperateDate();
        if (date == null) {
            date = new Date();
        }
        String exportSuccess = info.getSuccess();
        result.append("=================================================================================================\n");
        result.append("操作 : " + info.getOperateType() + " \t 系统版本 : " + info.getSystemVersion() + " \t 日期 : " + DateFormatUtil.fromatDate(date) + "  \t导出结果:" + (!ImportAndExportOperateLog.FAIL.equals(info.getSuccess()) ? "导出成功" : "导出失败") + "\n");
        result.append("=================================================================================================\n");
        if (!ImportAndExportOperateLog.FAIL.equals(info.getSuccess())) {
            result.append(buildSuccessReport(report));
        } else {
            result.append(buildLackReport(lack));
        }
        return result.toString();

    }

    public String buildSuccessReport(ResultReport report) {
        if (report == null) {
            return null;
        }
        StringBuilder result = new StringBuilder("\n");
        ExportStatistics statistics = report.getStatistics();
        if (statistics != null) {
            Integer folderNumber = statistics.getFolderNumber();
            Integer modelHeaderNumber = statistics.getModelHeaderNumber();
            Integer modelVersionNumber = statistics.getModelVersionNumber();
            Integer apiNumber = statistics.getApiNumber();
            Integer variableNumber = statistics.getVariableNumber();
            result.append("=================================================================================================\n");
            result.append("                           \t\t总数                                     \n");
            result.append("  导出场景个数          \t\t" + folderNumber + "\n" +
                    "  导出场景下模型个数    \t\t" + modelVersionNumber + "\n" +
                    "  导出场景下模型版本个数\t\t" + modelVersionNumber + "\n" +
                    "  导出接口个数          \t\t" + apiNumber + "\n" +
                    "  导出参数个数          \t\t" + variableNumber + "\n");
            result.append("=================================================================================================\n");
            result.append("\n");
        }
        PriReport pri = report.getPri();
        if (pri != null) {
            Map<String, FolderReport> folderMap = pri.getFolder();
            result.append("  导出场景列表：\n");
            result.append("=================================================================================================\n");
            result.append("  场景名称\t\t模型个数\t\t模型名称\n");

            if (folderMap != null && !folderMap.isEmpty()) {

                for (FolderReport folderReport : folderMap.values()) {
                    if (folderReport != null) {

                        String folderName = folderReport.getFolderName();
                        result.append(folderName);
                        Map<String, ModelReport> modelHeaderMap = folderReport.getModelHeader();
                        if (modelHeaderMap != null && !modelHeaderMap.isEmpty()) {

                            result.append("\t\t" + modelHeaderMap.size());
                            int i = 0;
                            for (ModelReport modelReport : modelHeaderMap.values()) {
                                if (modelReport != null) {
                                    String spacing1Column = "";
                                    if (i != 0) {
                                        spacing1Column = "    \t\t    ";
                                    }
                                    i++;
                                    String modelName = modelReport.getRuleName();
                                    result.append(spacing1Column + "\t\t" + modelName);
//                                Map<String, ModelVersionReport> versionMap = modelReport.getVersion();
//                                if (versionMap != null && !versionMap.isEmpty()) {
//                                    result.append("\t\t" + versionMap.size());
//                                    for (ModelVersionReport modelVersionReport : versionMap.values()) {
//                                        if (modelVersionReport != null) {
//                                            String version = modelVersionReport.getVersion();
//                                            result.append("\t\t" + version);
//                                        }
//                                        result.append("\n");
//                                    }
//                                } else {
//                                    result.append("\t\t" + "0\n");
//                                }
                                }
                            }
                        } else {
                            result.append("\t\t" + "0\n");
                        }
                    }
                }
            }
        }
        return result.toString();
    }

    public String buildLackReport(Map<String, Map<String, BaseReport>> lack) {
        if (lack == null || lack.isEmpty()) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        Map<String, List<String>> tmpMap = new HashMap<>();
        for (String type : lack.keySet()) {
            String typeName = ExportUtil.getTypeName(type);
            result.append("=================================================================================================\n");
            result.append("缺失类型\t\t\t缺失id\t\t\t缺失名称\t\t\t缺失来源类型\t\t\t缺失来源名称\n");
            result.append("=================================================================================================\n");
            Map<String, BaseReport> lackMap = lack.get(type);
            if (lackMap != null && !lackMap.isEmpty()) {
                result.append(typeName);
                int i = 0;
                for (BaseReport report : lackMap.values()) {
                    if (report != null) {
                        String spacing1Column = "\t\t";
                        if (i != 0) {
                            spacing1Column = "    \t\t";
                        }
                        i++;
                        result.append(spacing1Column + report.reportId() + "\t\t" + report.reportName());
                        Map<String, Map<String, BaseReport>> from = report.getFrom();
                        if (from != null && !from.isEmpty()) {
                            int j = 0;
                            for (String fromType : from.keySet()) {
                                String spacing23Column = "\t\t";
                                if (j != 0) {
                                    spacing23Column = "    \t\t" + ExportUtil.getSpacing(32) + "\t\t    \t\t";
                                }
                                j++;
                                String fromTypeName = ExportUtil.getTypeName(fromType);
                                Map<String, BaseReport> stringBaseReportMap = from.get(fromType);
                                if (stringBaseReportMap != null && !stringBaseReportMap.isEmpty()) {
                                    result.append(spacing23Column + fromTypeName);
                                    int h = 0;
                                    for (BaseReport fromReport : stringBaseReportMap.values()) {
                                        String spacing4Column = "\t\t";
                                        if (h != 0) {
                                            spacing4Column = "    \t\t" + ExportUtil.getSpacing(32) + "\t\t    \t\t    \t\t";
                                        }
                                        h++;
                                        result.append(spacing4Column + fromReport.reportName() + "\n");
                                    }
                                } else {
                                    System.out.println();
                                }
                            }
                        } else {
                            System.out.println();
                        }
                    }
                }
            }
        }
        return result.toString();
    }

    private void addDataIdWithFrom(String type, Object o, String fromObjType, Object fromObj) {
        if (StringUtils.isBlank(type)) {
            log.warn("数据类型为null,Object:" + o);
            return;
        }
        if (o instanceof String) {
            o = ExportUtil.paseObjectIdToObject(type, (String) o);
        }
        if (dataIdsWithFrom == null) {
            dataIdsWithFrom = new HashMap<>();
        }
        Map<String, BaseReport> map = dataIdsWithFrom.get(type);
        if (map == null) {
            map = new HashMap<>();
            dataIdsWithFrom.put(type, map);
        }
        if (ExportConstant.MODEL_VERSION.equals(type)) {
            if (o instanceof RuleDetailWithBLOBs) {
                addModelVersionDataIdWithFrom(type, (RuleDetailWithBLOBs) o, fromObjType, fromObj);
            }
            return;
        }
        String objectId = ExportUtil.getObjectId(type, o);
        BaseReport report = map.get(objectId);
        if (report == null) {
            report = ExportUtil.paseReport(type, o);
        }
        if (report != null) {
            report.addObjectFrom(fromObjType, fromObj);
            map.put(objectId, report);
        }
    }

    private void addlackIdWithFrom(String type, Object o, String fromObjType, Object fromObj) {
        if (StringUtils.isBlank(type)) {
            log.warn("数据类型为null,Object:" + o);
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
                addModelVersionLackWithFrom(type, (RuleDetailWithBLOBs) o, fromObjType, fromObj);
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

    private void addModelVersionLackWithFrom(String type, RuleDetailWithBLOBs ruleDetailWithBLOBs, String
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

    private void addModelVersionDataIdWithFrom(String type, RuleDetailWithBLOBs ruleDetailWithBLOBs, String
            fromObjType, Object fromObj) {
        if (ruleDetailWithBLOBs != null) {
            if (dataIdsWithFrom == null) {
                dataIdsWithFrom = new HashMap<>();
            }
            Map<String, BaseReport> map = dataIdsWithFrom.get(ExportConstant.MODEL_HEADER);

            String modelId = ruleDetailWithBLOBs.getRuleName();
            if (map == null) {
                map = new HashMap<>();
                dataIdsWithFrom.put(ExportConstant.MODEL_HEADER, map);
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

    public ResultData getData() {
        return data;
    }

    public void setData(ResultData data) {
        this.data = data;
    }

    public ResultReport getReport() {
        return report;
    }

    public void setReport(ResultReport report) {
        this.report = report;
    }

    public Map<String, Map<String, BaseReport>> getLack() {
        return lack;
    }

    public void setLack(Map<String, Map<String, BaseReport>> lack) {
        this.lack = lack;
    }

    public Map<String, Map<String, BaseReport>> getDataIdsWithFrom() {
        return dataIdsWithFrom;
    }

    public void setDataIdsWithFrom(Map<String, Map<String, BaseReport>> dataIdsWithFrom) {
        this.dataIdsWithFrom = dataIdsWithFrom;
    }

    public ImportAndExportOperateLog getInfo() {
        return info;
    }

    public void setInfo(ImportAndExportOperateLog info) {
        this.info = info;
    }

    public List<ExportParam> getExportParams() {
        return exportParams;
    }

    public void setExportParams(List<ExportParam> exportParams) {
        this.exportParams = exportParams;
    }
}
