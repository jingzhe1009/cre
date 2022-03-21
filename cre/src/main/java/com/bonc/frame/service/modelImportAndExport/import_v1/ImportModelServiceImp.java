package com.bonc.frame.service.modelImportAndExport.import_v1;

import com.bonc.frame.entity.modelImportAndExport.ImportAndExportOperateLog;
import com.bonc.frame.entity.modelImportAndExport.modelExport.ExportParam;
import com.bonc.frame.entity.modelImportAndExport.modelExport.entity.ExportConstant;
import com.bonc.frame.entity.modelImportAndExport.modelExport.entity.ExportResult;
import com.bonc.frame.entity.modelImportAndExport.modelExport.entity.data.ResultData;
import com.bonc.frame.entity.modelImportAndExport.modelImport.ImportParam;
import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.ImportAdjustObject;
import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.importContext.ImportContext;
import com.bonc.frame.entity.rule.RuleDetailWithBLOBs;
import com.bonc.frame.service.modelImportAndExport.ImpoerAndExportLogService;
import com.bonc.frame.service.modelImportAndExport.ImportModelService;
import com.bonc.frame.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/4/7 2:57
 */
@Service
public class ImportModelServiceImp implements ImportModelService {
    //        private ImportHelper importHelper = ImportHelper.getInstance();
    Log log = LogFactory.getLog(ImportModelServiceImp.class);

    @Autowired
    private ImpoerAndExportLogService impoerAndExportLogService;

    private Map<String, ImportAdjustService> importAdjustServiceMap;

    @Autowired
    public ImportModelServiceImp(List<ImportAdjustService> importAdjustServices) {
        importAdjustServiceMap = new HashMap<>(2);
        for (ImportAdjustService fetchService : importAdjustServices) {
            importAdjustServiceMap.put(fetchService.getSupport().getValue(), fetchService);
        }
    }

    public ImportAndExportOperateLog import_V1(String importFileString, ExportParam target, String modelImportStrategy) throws Exception {
//        ImportParam

        ImportParam param = new ImportParam();
//        ExportParam toFolderParam = new ExportParam();
//        toFolderParam.setFolderId(target);
        param.setImportTo(target);
        param.putStrategy(ExportConstant.MODEL_HEADER, modelImportStrategy);
        List<ImportParam> params = new ArrayList<>();
        params.add(param);
        // 开始导入
        ImportContext context = new ImportContext(params, importFileString, null);
        context.putStrategy(ExportConstant.MODEL_HEADER, modelImportStrategy);
        ExportResult importFileObject = context.getImportFileObject();
        if (importFileObject != null) {
            ResultData importFileObjectData = importFileObject.getData();
            if (importFileObjectData != null) {
                Set<RuleDetailWithBLOBs> fileRuleDetailWithBLOBs = getFileRuleDetailWithBLOBs(importFileObjectData);
                for (RuleDetailWithBLOBs ruleDetailWithBLOBs : fileRuleDetailWithBLOBs) {
                    if (ruleDetailWithBLOBs == null) {
                        continue;
                    }
                    String ruleId = ruleDetailWithBLOBs.getRuleId();
                    ImportAdjustService importAdjustService = importAdjustServiceMap.get(ExportConstant.MODEL_VERSION);
                    if (importAdjustService != null) {
                        importAdjustService.adjust(ExportConstant.MODEL_VERSION, ruleId, param, context);

                        ImportAdjustObject successData = context.getSuccessData(ExportConstant.MODEL_VERSION, ruleId);
                        if (successData == null) {//如果缓存中不存在, 先去保存一次参数,再去缓存中拿
                            ImportHelper.getInstance().importAdjusObject(ExportConstant.MODEL_VERSION, ruleId, param, context);
                            successData = context.getSuccessData(ExportConstant.MODEL_VERSION, ruleId);
                        }
                        if (successData != null) {
                            context.initTmpMap(true);
                        } else {  // 如果还是空, 则表示参数保存失败, -1
                            context.putlackData(ExportConstant.MODEL_VERSION, ruleDetailWithBLOBs, null, null);
                            context.initTmpMap(false);
                        }
                    } else {
                        log.error("");
                    }
                }
            }
        }
        // 生成日志
        ImportAndExportOperateLog result = context.buildResult();
        // 日志入库
        impoerAndExportLogService.insertImportAndExportOperateLog(result);
        return result;
    }

    private Set<RuleDetailWithBLOBs> getFileRuleDetailWithBLOBs(ResultData importFileObjectData) {
        Map<String, RuleDetailWithBLOBs> modelVersion = importFileObjectData.getModelVersion();


        Comparator<RuleDetailWithBLOBs> comparator = new Comparator<RuleDetailWithBLOBs>() {
            /**
             *  o1为新添加的数据
             *  大于0: 放后面    小于0:放前面   0:不放
             *   ruleName 通过字符串的比较
             *   版本   null版本的放前面.先导
             *          字符串类型版本的放前面,先导
             *          数字类型的进行比较 , 小的放前面
             */
            @Override
            public int compare(RuleDetailWithBLOBs o1, RuleDetailWithBLOBs o2) {
                String o1RuleName = o1.getRuleName();
                String o2RuleName = o2.getRuleName();
                if (o1RuleName.equals(o2RuleName)) {
                    String o1VersionString = o1.getVersion();
                    String o2VersionString = o2.getVersion();
                    boolean o1VersionIsBlack = StringUtils.isBlank(o1VersionString);
                    boolean o2VersionIsBlack = StringUtils.isBlank(o2VersionString);
                    if (o1VersionIsBlack) {
                        return -1;
                    }
                    if (o2VersionIsBlack) {
                        return 1;
                    }
                    boolean o1VersionIsNumber = StringUtil.stringIsNumber(o1VersionString);
                    boolean o2VersionIsNumber = StringUtil.stringIsNumber(o2VersionString);
                    if (!o1VersionIsNumber) {
                        return -1;
                    }
                    if (!o2VersionIsNumber) {
                        return 1;
                    }
                    double v = Double.parseDouble(o1VersionString) - Double.parseDouble(o2VersionString);
                    if (v > 0) {
                        return 1;
                    } else {
                        return -1;
                    }
                } else {
                    return o1RuleName.compareTo(o2RuleName);
                }
            }
        };
        Set<RuleDetailWithBLOBs> ruleTreeSet = new TreeSet<>(comparator);
        if (modelVersion != null && !modelVersion.isEmpty()) {
            ruleTreeSet.addAll(modelVersion.values());
        }
        return ruleTreeSet;
    }
}
