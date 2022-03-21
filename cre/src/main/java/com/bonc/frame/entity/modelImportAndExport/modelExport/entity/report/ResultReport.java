package com.bonc.frame.entity.modelImportAndExport.modelExport.entity.report;

import com.bonc.frame.entity.api.ApiConf;
import com.bonc.frame.entity.commonresource.ApiGroup;
import com.bonc.frame.entity.commonresource.ModelGroup;
import com.bonc.frame.entity.commonresource.VariableGroup;
import com.bonc.frame.entity.kpi.KpiDefinition;
import com.bonc.frame.entity.kpi.KpiGroup;
import com.bonc.frame.entity.rule.RuleDetailHeader;
import com.bonc.frame.entity.rule.RuleDetailWithBLOBs;
import com.bonc.frame.entity.rulefolder.RuleFolder;
import com.bonc.frame.entity.variable.Variable;
import com.bonc.frame.entity.modelImportAndExport.modelExport.entity.ExportConstant;
import com.bonc.frame.entity.modelImportAndExport.modelExport.entity.info.ExportStatistics;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/2/10 18:44
 */
public class ResultReport {
    Log log = LogFactory.getLog(ResultReport.class);
    private ExportStatistics statistics;
    private PriReport pri;
    private PubReport pub;


    public void add(String type, Object o) throws Exception {
        if (StringUtils.isBlank(type)) {
            log.warn("数据类型为null,Object:" + o);
            return;
        }
        if (o == null) {
            log.warn("保存对象对null,Object:" + o);
            return;
        }
        if (statistics == null) {
            statistics = new ExportStatistics();
        }
        statistics.put(type, o);
        switch (type) {
            case ExportConstant.FOLDER:
                if (o instanceof RuleFolder) {
                    if (pri == null) {
                        pri = new PriReport();
                    }
                    pri.add(type, o);
                } else {
                    log.error("数据的类型不匹配,Object:" + o);
                    throw new Exception("数据的类型不匹配,Object:" + o);
                }
                break;
            case ExportConstant.MODEL_GROUP:
                if (o instanceof ModelGroup) {
                    if (pub == null) {
                        pub = new PubReport();
                    }
                    pub.add(type, (ModelGroup) o);
                } else {
                    log.error("数据的类型不匹配,Object:" + o);
                    throw new Exception("数据的类型不匹配,Object:" + o);
                }
                break;
            case ExportConstant.MODEL_HEADER:
                if (o instanceof RuleDetailHeader) {
                    RuleDetailHeader rule = (RuleDetailHeader) o;
                    String isPublic = rule.getIsPublic();
                    if ("0".equals(isPublic)) {
                        if (pri == null) {
                            pri = new PriReport();
                        }
                        pri.add(type, rule);
                    } else {
                        if (pub == null) {
                            pub = new PubReport();
                        }
                        pub.add(type, rule);
                    }
                } else {
                    log.error("数据的类型不匹配,Object:" + o);
                    throw new Exception("数据的类型不匹配,Object:" + o);
                }
                break;
            case ExportConstant.MODEL_VERSION:
                if (o instanceof RuleDetailWithBLOBs) {
                    RuleDetailWithBLOBs rule = (RuleDetailWithBLOBs) o;
                    String isPublic = rule.getIsPublic();
                    if ("0".equals(isPublic)) {
                        if (pri == null) {
                            pri = new PriReport();
                        }
                        pri.add(type, rule);
                    } else {
                        if (pub == null) {
                            pub = new PubReport();
                        }
                        pub.add(type, rule);
                    }
                } else {
                    log.error("数据的类型不匹配,Object:" + o);
                    throw new Exception("数据的类型不匹配,Object:" + o);
                }
                break;
            case ExportConstant.VARIABLE_GROUP:
                if (o instanceof VariableGroup) {
                    if (pub == null) {
                        pub = new PubReport();
                    }
                    pub.add(type, (VariableGroup) o);
                } else {
                    log.error("数据的类型不匹配,Object:" + o);
                    throw new Exception("数据的类型不匹配,Object:" + o);
                }
                break;
            case ExportConstant.VARIABLE:
                if (o instanceof Variable) {
                    Variable variable = (Variable) o;
                    String isPublic = variable.getIsPublic();
                    if ("0".equals(isPublic)) {
                        if (pri == null) {
                            pri = new PriReport();
                        }
                        pri.add(type, variable);
                    } else {
                        if (pub == null) {
                            pub = new PubReport();
                        }
                        pub.add(type, variable);
                    }
                } else {
                    log.error("数据的类型不匹配,Object:" + o);
                    throw new Exception("数据的类型不匹配,Object:" + o);
                }
                break;
            case ExportConstant.KPI_GROUP:
                if (o instanceof KpiGroup) {
                    if (pub == null) {
                        pub = new PubReport();
                    }
                    pub.add(type, (KpiGroup) o);
                } else {
                    log.error("数据的类型不匹配,Object:" + o);
                    throw new Exception("数据的类型不匹配,Object:" + o);
                }
                break;
            case ExportConstant.KPI:
                if (o instanceof KpiDefinition) {
                    KpiDefinition kpiDefinition = (KpiDefinition) o;
                    if (pub == null) {
                        pub = new PubReport();
                    }
                    pub.add(type, kpiDefinition);
                } else {
                    log.error("数据的类型不匹配,Object:" + o);
                    throw new Exception("数据的类型不匹配,Object:" + o);
                }
                break;
            case ExportConstant.API_GROUP:
                if (o instanceof ApiGroup) {
                    if (pub == null) {
                        pub = new PubReport();
                    }
                    pub.add(type, (ApiGroup) o);
                } else {
                    log.error("数据的类型不匹配,Object:" + o);
                    throw new Exception("数据的类型不匹配,Object:" + o);
                }
                break;
            case ExportConstant.API:
                if (o instanceof ApiConf) {
                    ApiConf apiConf = (ApiConf) o;
                    String isPublic = apiConf.getIsPublic();
                    if ("0".equals(isPublic)) {
                        if (pri == null) {
                            pri = new PriReport();
                        }
                        pri.add(type, apiConf);
                    } else {
                        if (pub == null) {
                            pub = new PubReport();
                        }
                        pub.add(type, apiConf);
                    }
                } else {
                    log.error("数据的类型不匹配,Object:" + o);
                    throw new Exception("数据的类型不匹配,Object:" + o);
                }
                break;
            default:
//                log.warn("数据的类型不匹配,Object:" + o);
                break;
        }

    }

    public ExportStatistics getStatistics() {
        return statistics;
    }

    public void setStatistics(ExportStatistics statistics) {
        this.statistics = statistics;
    }

    public PriReport getPri() {
        return pri;
    }

    public void setPri(PriReport pri) {
        this.pri = pri;
    }

    public PubReport getPub() {
        return pub;
    }

    public void setPub(PubReport pub) {
        this.pub = pub;
    }

//    public void addModelVersion(RuleDetailWithBLOBs rule) {
//        if (rule == null) {
//            return;
//        }
//        String isPublic = rule.getIsPublic();
//        if ("0".equals(isPublic)) {
//            if (pri == null) {
//                pri = new PriReport();
//            }
//            String folderId = rule.getFolderId();
//            if (pri.containsFolder(folderId)) {
//                pri.addModelVersion(rule);
//            } else {
//                //添加场景
//            }
//        } else {
//            if (pub == null) {
//                pub = new PubReport();
//            }
//            String modelGroupId = rule.getModelGroupId();
//            if (pub.containsModelGroup(modelGroupId)) {
//                pub.addModelVersion(rule);
//            } else {
//                //添加模型组
//            }
//        }
//    }
}
