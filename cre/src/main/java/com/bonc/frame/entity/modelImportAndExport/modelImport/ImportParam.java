package com.bonc.frame.entity.modelImportAndExport.modelImport;

import com.bonc.frame.entity.modelImportAndExport.modelExport.ExportParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/3/24 23:56
 */
public class ImportParam {
    private List<ExportParam> importFrom;
    private String isAll;
    private ExportParam importTo;
    private Map<String, String> importStrategy;//{type : 策略}

    public void putStrategy(String type, String strategy) {
        if (importStrategy == null) {
            importStrategy = new HashMap<>();
        }
        importStrategy.put(type, strategy);
    }


    public List<ExportParam> getImportFrom() {
        return importFrom;
    }

    public void setImportFrom(List<ExportParam> importFrom) {
        this.importFrom = importFrom;
    }

    public ExportParam getImportTo() {
        return importTo;
    }

    public void setImportTo(ExportParam importTo) {
        this.importTo = importTo;
    }

    public Map<String, String> getImportStrategy() {
        return importStrategy;
    }

    public void setImportStrategy(Map<String, String> importStrategy) {
        this.importStrategy = importStrategy;
    }

    public String getStrategy(String type) {
        if (importStrategy == null || importStrategy.isEmpty()) {
            return null;
        }
        return importStrategy.get(type);
    }
}
