package com.bonc.frame.entity.modelImportAndExport.modelExport.entity.report;

import com.bonc.frame.entity.api.ApiConf;
import com.bonc.frame.entity.rule.RuleDetailHeader;
import com.bonc.frame.entity.rule.RuleDetailWithBLOBs;
import com.bonc.frame.entity.rulefolder.RuleFolder;
import com.bonc.frame.entity.variable.Variable;
import com.bonc.frame.entity.modelImportAndExport.modelExport.entity.ExportConstant;
import com.bonc.frame.entity.modelImportAndExport.modelExport.entity.info.ExportStatistics;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 场景下的导入导出报告
 *
 * @Author: wangzhengbao
 * @DATE: 2020/1/9 18:07
 */
public class PriReport {
    Log log = LogFactory.getLog(PriReport.class);

    private ExportStatistics priStatistics = new ExportStatistics();

    private Map<String, FolderReport> folder;


    public boolean add(String type, Object o) throws Exception {
        if (StringUtils.isBlank(type)) {
            return false;
        }
        if (o == null) {
            return false;
        }
        if (folder == null) {
            folder = new HashMap<>();
        }
        if (priStatistics == null) {
            priStatistics = new ExportStatistics();
        }
        priStatistics.put(type, o);
        switch (type) {
            case ExportConstant.FOLDER:
                if (o instanceof RuleFolder) {
                    addFolder((RuleFolder) o);
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
//            case ExportConstant.VARIABLE:
//                if (o instanceof Variable) {
//                    addVariable((Variable) o);
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
//            default:
//                log.error("数据的类型不匹配,Object:" + o);
//                throw new Exception("数据的类型不匹配,Object:" + o);
        }
        return true;
    }

    private void addModelHeader(RuleDetailHeader ruleDetailHeader) {
        if (ruleDetailHeader == null) {
            return;
        }
        if (folder == null) {
            folder = new HashMap<>();
        }
        String folderId = ruleDetailHeader.getFolderId();
        if (!folder.containsKey(folderId)) {
            addFolder(folderId);
        }
        FolderReport folderReport = folder.get(folderId);
        if (folderReport != null) {
            folderReport.addModelHeader(ruleDetailHeader);
        }
    }


    private void addModelVersion(RuleDetailWithBLOBs ruleDetailWithBLOBs) {
        if (ruleDetailWithBLOBs == null) {
            return;
        }
        if (folder == null) {
            folder = new HashMap<>();
        }
        String folderId = ruleDetailWithBLOBs.getFolderId();
        if (!folder.containsKey(folderId)) {
            addFolder(folderId);
        }
        FolderReport folderReport = folder.get(folderId);
        if (folderReport != null) {
            folderReport.addModelVersion(ruleDetailWithBLOBs);
        }
    }

    private void addVariable(Variable variable) {
        if (variable == null) {
            return;
        }
        if (folder == null) {
            folder = new HashMap<>();
        }
        String folderId = variable.getFolderId();
        if (!folder.containsKey(folderId)) {
            addFolder(folderId);
        }
        FolderReport folderReport = folder.get(folderId);
        if (folderReport != null) {
            folderReport.addVariable(variable);
        }
    }

    private void addApi(ApiConf apiConf) {
        if (apiConf == null) {
            return;
        }
        if (folder == null) {
            folder = new HashMap<>();
        }
        String folderId = apiConf.getFolderId();
        if (!folder.containsKey(folderId)) {
            addFolder(folderId);
        }
        FolderReport folderReport = folder.get(folderId);
        if (folderReport != null) {
            folderReport.addApi(apiConf);
        }
    }

    private void addFolder(String folderId) {
        if (!StringUtils.isBlank(folderId)) {
            FolderReport folderReport = new FolderReport();
            folderReport.setFolderId(folderId);
            // TODO : 添加场景名称
            folder.put(folderId, folderReport);
        }
    }

    private void addFolder(RuleFolder folder) {
        if (folder != null) {
            String folderId = folder.getFolderId();
            if (!StringUtils.isBlank(folderId)) {
                if (!this.folder.containsKey(folderId)) {
                    FolderReport folderReport = new FolderReport();
                    folderReport.setFolderId(folderId);
                    folderReport.setFolderName(folder.getFolderName());
                    folderReport.setFolderDesc(folder.getFolderDesc());
                    this.folder.put(folderId, folderReport);
                }
            }
        }
    }

    public ExportStatistics getPriStatistics() {
        return priStatistics;
    }

    public void setPriStatistics(ExportStatistics priStatistics) {
        this.priStatistics = priStatistics;
    }

    public Map<String, FolderReport> getFolder() {
        return folder;
    }

    public void setFolder(Map<String, FolderReport> folder) {
        this.folder = folder;
    }
}
