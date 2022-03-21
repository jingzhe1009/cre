package com.bonc.frame.service.rule;

import com.bonc.frame.entity.rule.RuleDetailHeader;
import com.bonc.frame.entity.rule.RuleDetailWithBLOBs;
import com.bonc.frame.entity.modelCompare.entity.ModelOperateLog;
import com.bonc.frame.util.ResponseResult;

import java.util.List;
import java.util.Map;

public interface ModelOperateLogService {

    /**
     * 获取模型执行日志
     */
    Map<String, Object> selectModelOperate(String logId, String folderId, String modelName, String version,
                                           String operateType, String operatePerson,
                                           String startDate, String endDate, String ip,
                                           String start, String length);

    List<Map<String, Object>> getModelOperateType();

    Map<String, Object> selectModelOperatePersonAndIp(String logId, String folderId, String modelName, String version,
                                                      String operateType, String operatePerson,
                                                      String startDate, String endDate, String ip);

    /**
     * 将两个版本模型进行比较,并插入到数据库中
     * 如果oldRule为null,插入到数据库中的节点以及模型信息全是新建
     * 如果newRule为null,插入到数据库中的节点以及模型信息全是删除
     * 如果都不为null,则为修改
     */
    ResponseResult insertModelOperate(RuleDetailWithBLOBs oldRuleDetailWithBLOBs, RuleDetailWithBLOBs newRuleDetailWithBLOBs, RuleDetailWithBLOBs fromRuleDetailHeader, String operateType);

    /**
     * 将两个版本模型进行比较,并插入到数据库中
     * 如果oldRule为null,插入到数据库中的节点以及模型信息全是新建
     * 如果newRule为null,插入到数据库中的节点以及模型信息全是删除
     * 如果都不为null,则为修改
     */
    ResponseResult modeVersionAllInfoModelOperate(RuleDetailWithBLOBs oldRuleDetailWithBLOBs, RuleDetailWithBLOBs newRuleDetailWithBLOBs, RuleDetailWithBLOBs fromRuleDetailHeader, String operateType);

    /**
     * 将两个模型基本信息进行比较,并插入到数据库中
     * 如果oldRule为null,插入到数据库中的节点以及模型信息全是新建
     * 如果newRule为null,插入到数据库中的节点以及模型信息全是删除
     * 如果都不为null,则为修改
     */
    ResponseResult modelHeaderModelOperate(RuleDetailHeader oldRuleDetailHeader, RuleDetailHeader newRuleDetailHeader, RuleDetailWithBLOBs fromRuleDetailHeader, String operateType);

    /**
     * 克隆模型的操作日志写入,并插入到数据库中
     */
    ResponseResult cloneRuleModelOperate(RuleDetailWithBLOBs fromRuleDetailwithBLOBs, RuleDetailWithBLOBs newRuleDetailWithBLOBs, String operateType);

    /**
     * 插入模型操作日志
     */
    ResponseResult insertModelOperate(ModelOperateLog rule);
}
