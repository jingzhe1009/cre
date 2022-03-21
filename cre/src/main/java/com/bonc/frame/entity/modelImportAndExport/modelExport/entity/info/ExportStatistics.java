package com.bonc.frame.entity.modelImportAndExport.modelExport.entity.info;

import com.bonc.frame.entity.api.ApiConf;
import com.bonc.frame.entity.commonresource.ApiGroup;
import com.bonc.frame.entity.commonresource.ModelGroup;
import com.bonc.frame.entity.commonresource.VariableGroup;
import com.bonc.frame.entity.datasource.DataSource;
import com.bonc.frame.entity.kpi.KpiDefinition;
import com.bonc.frame.entity.kpi.KpiGroup;
import com.bonc.frame.entity.metadata.MetaDataColumn;
import com.bonc.frame.entity.metadata.MetaDataTable;
import com.bonc.frame.entity.rule.RuleDetailHeader;
import com.bonc.frame.entity.rule.RuleDetailWithBLOBs;
import com.bonc.frame.entity.rulefolder.RuleFolder;
import com.bonc.frame.entity.variable.Variable;
import com.bonc.frame.entity.modelImportAndExport.modelExport.entity.ExportConstant;
import org.apache.commons.lang3.StringUtils;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/3/15 16:22
 */
public class ExportStatistics {
    private int allNumber = 0;
    private Integer folderNumber = 0;
    private Integer modelGroupNumber = 0;
    private Integer modelHeaderNumber = 0;
    private Integer modelVersionNumber = 0;
    private Integer apiGroupNumber = 0;
    private Integer apiNumber = 0;
    private Integer kpiGroupNumber = 0;
    private Integer kpiNumber = 0;
    private Integer variableGroupNumber = 0;
    private Integer variableNumber = 0;
    private Integer dbNumber = 0;
    private Integer pubDbTableNumber = 0;
    private Integer priDbTableNumber = 0;
    private Integer dbColunmNumber = 0;

    public void put(String type, Object o) {
        if (StringUtils.isBlank(type)) {
            return;
        }
        switch (type) {
            case ExportConstant.FOLDER:
                if (o instanceof RuleFolder) {
                    folderNumber = numberAdd(folderNumber);
                }
                break;
            case ExportConstant.MODEL_GROUP:
                if (o instanceof ModelGroup) {
                    modelGroupNumber = numberAdd(modelGroupNumber);
                }
                break;
            case ExportConstant.MODEL_HEADER:
                if (o instanceof RuleDetailHeader) {
                    modelHeaderNumber = numberAdd(modelHeaderNumber);
                }
                break;
            case ExportConstant.MODEL_VERSION:
                if (o instanceof RuleDetailWithBLOBs) {
                    modelVersionNumber = numberAdd(modelVersionNumber);
                }
                break;
            case ExportConstant.VARIABLE_GROUP:
                if (o instanceof VariableGroup) {
                    variableGroupNumber = numberAdd(variableGroupNumber);
                }
                break;
            case ExportConstant.VARIABLE:
                if (o instanceof Variable) {
                    variableNumber = numberAdd(variableNumber);
                }
                break;
            case ExportConstant.KPI_GROUP:
                if (o instanceof KpiGroup) {
                    kpiGroupNumber = numberAdd(kpiGroupNumber);
                }
                break;
            case ExportConstant.KPI:
                if (o instanceof KpiDefinition) {
                    kpiNumber = numberAdd(kpiNumber);
                }
                break;
            case ExportConstant.API_GROUP:
                if (o instanceof ApiGroup) {
                    apiGroupNumber = numberAdd(apiGroupNumber);
                }
                break;
            case ExportConstant.API:
                if (o instanceof ApiConf) {
                    apiNumber = numberAdd(apiNumber);
                }
                break;
            case ExportConstant.DB:
                if (o instanceof DataSource) {
                    dbNumber = numberAdd(dbNumber);
                }
                break;
            case ExportConstant.PUB_DB_TABLE:
                if (o instanceof MetaDataTable) {
                    pubDbTableNumber = numberAdd(pubDbTableNumber);
                }
                break;
            case ExportConstant.PRI_DB_TABLE:
                if (o instanceof MetaDataTable) {
                    priDbTableNumber = numberAdd(priDbTableNumber);
                }
                break;
            case ExportConstant.DB_COLUNM:
                if (o instanceof MetaDataColumn) {
                    dbColunmNumber = numberAdd(dbColunmNumber);
                }
                break;
        }
    }

    public int get(String type) {
        if (StringUtils.isBlank(type)) {
            return 0;
        }
        int result = 0;
        switch (type) {
            case ExportConstant.FOLDER:
                result = folderNumber;
                break;
            case ExportConstant.MODEL_GROUP:
                result = modelGroupNumber;
                break;
            case ExportConstant.MODEL_HEADER:
                result = modelHeaderNumber;
                break;
            case ExportConstant.MODEL_VERSION:
                result = modelVersionNumber;
                break;
            case ExportConstant.VARIABLE_GROUP:
                result = variableGroupNumber;
                break;
            case ExportConstant.VARIABLE:
                result = variableNumber;
                break;
            case ExportConstant.KPI_GROUP:
                result = kpiGroupNumber;
                break;
            case ExportConstant.KPI:
                result = kpiNumber;
                break;
            case ExportConstant.API_GROUP:
                result = apiGroupNumber;
                break;
            case ExportConstant.API:
                result = apiNumber;
                break;
            case ExportConstant.DB:
                result = dbNumber;
                break;
            case ExportConstant.PUB_DB_TABLE:
                result = pubDbTableNumber;
                break;
            case ExportConstant.PRI_DB_TABLE:
                result = priDbTableNumber;
                break;
            case ExportConstant.DB_COLUNM:
                result = dbColunmNumber;
                break;
        }
        return result;
    }

    private Integer numberAdd(Integer i) {
        allNumber++;
        if (i == null) {
            i = 0;
        }
        i++;
        return i;
    }

    public Integer getFolderNumber() {
        return folderNumber;
    }

    public void setFolderNumber(Integer folderNumber) {
        this.folderNumber = folderNumber;
    }

    public Integer getModelGroupNumber() {
        return modelGroupNumber;
    }

    public void setModelGroupNumber(Integer modelGroupNumber) {
        this.modelGroupNumber = modelGroupNumber;
    }

    public Integer getModelHeaderNumber() {
        return modelHeaderNumber;
    }

    public void setModelHeaderNumber(Integer modelHeaderNumber) {
        this.modelHeaderNumber = modelHeaderNumber;
    }

    public Integer getModelVersionNumber() {
        return modelVersionNumber;
    }

    public void setModelVersionNumber(Integer modelVersionNumber) {
        this.modelVersionNumber = modelVersionNumber;
    }

    public Integer getApiGroupNumber() {
        return apiGroupNumber;
    }

    public void setApiGroupNumber(Integer apiGroupNumber) {
        this.apiGroupNumber = apiGroupNumber;
    }

    public Integer getApiNumber() {
        return apiNumber;
    }

    public void setApiNumber(Integer apiNumber) {
        this.apiNumber = apiNumber;
    }

    public Integer getKpiGroupNumber() {
        return kpiGroupNumber;
    }

    public void setKpiGroupNumber(Integer kpiGroupNumber) {
        this.kpiGroupNumber = kpiGroupNumber;
    }

    public Integer getKpiNumber() {
        return kpiNumber;
    }

    public void setKpiNumber(Integer kpiNumber) {
        this.kpiNumber = kpiNumber;
    }

    public Integer getVariableGroupNumber() {
        return variableGroupNumber;
    }

    public void setVariableGroupNumber(Integer variableGroupNumber) {
        this.variableGroupNumber = variableGroupNumber;
    }

    public Integer getVariableNumber() {
        return variableNumber;
    }

    public void setVariableNumber(Integer variableNumber) {
        this.variableNumber = variableNumber;
    }

    public Integer getDbNumber() {
        return dbNumber;
    }

    public void setDbNumber(Integer dbNumber) {
        this.dbNumber = dbNumber;
    }

    public Integer getPubDbTableNumber() {
        return pubDbTableNumber;
    }

    public void setPubDbTableNumber(Integer pubDbTableNumber) {
        this.pubDbTableNumber = pubDbTableNumber;
    }

    public Integer getPriDbTableNumber() {
        return priDbTableNumber;
    }

    public void setPriDbTableNumber(Integer priDbTableNumber) {
        this.priDbTableNumber = priDbTableNumber;
    }

    public Integer getDbColunmNumber() {
        return dbColunmNumber;
    }

    public void setDbColunmNumber(Integer dbColunmNumber) {
        this.dbColunmNumber = dbColunmNumber;
    }

    public int getAllNumber() {
        return allNumber;
    }

    public void setAllNumber(int allNumber) {
        this.allNumber = allNumber;
    }
}
