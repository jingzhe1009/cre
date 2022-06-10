package com.bonc.frame.service.impl.rule;

import com.bonc.frame.config.ConstantFinal;
import com.bonc.frame.dao.DaoHelper;
import com.bonc.frame.entity.rule.RuleDetailHeader;
import com.bonc.frame.entity.rule.RuleDetailWithBLOBs;
import com.bonc.frame.entity.rulefolder.RuleFolder;
import com.bonc.frame.service.auth.AuthorityService;
import com.bonc.frame.service.auth.SubjectService;
import com.bonc.frame.service.modelCompare.ModelComparOperator;
import com.bonc.frame.entity.modelCompare.entity.ModelOperateLog;
import com.bonc.frame.service.rule.ModelOperateLogService;
import com.bonc.frame.service.rule.RuleFolderService;
import com.bonc.frame.util.ControllerUtil;
import com.bonc.frame.util.IdUtil;
import com.bonc.frame.util.JsonUtils;
import com.bonc.frame.util.ResponseResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ModelOperateLogServiceImpl implements ModelOperateLogService {
    Log log = LogFactory.getLog(ModelOperateLogServiceImpl.class);

    private final String _MYBITSID_PREFIX = "com.bonc.frame.dao.rule.ModelOperateMapper.";

    @Autowired
    private DaoHelper daoHelper;

    @Autowired
    private ModelComparOperator modelComparService;

    @Autowired
    private RuleFolderService ruleFolderService;

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private SubjectService subjectService;

    @Override
    public Map<String, Object> selectModelOperate(String logId, String folderId, String modelName, String version,
                                                  String operateType, String operatePerson,
                                                  String startDate, String endDate, String ip,
                                                  String start, String length) {
        Map<String, Object> param = new HashMap<>();
        param.put("logId", logId);
        param.put("folderId", folderId);
        param.put("modelName", modelName);
        param.put("version", version);
        param.put("operateType", operateType);
        param.put("operatePerson", operatePerson);
        param.put("startDate", startDate);
        param.put("endDate", endDate);
        param.put("ip", ip);

        // FIXME: 当前实现并未考虑到主体对某一类型拥有全部权限的情况，现在系统内暂未实现类型级的授权
        if (authorityService.isCurrentUserHasAllPermits()) {
            return daoHelper.queryForPageList(_MYBITSID_PREFIX + "selectModelOperate",
                    param, start, length);
        }

        List<String> subjects = subjectService.selectSubjectsByCurrentUser();
        param.put("subjectIds", subjects);
        return daoHelper.queryForPageList(_MYBITSID_PREFIX + "selectModelOperateWithAuthFilter",
                param, start, length);
    }

    @Override
    public List<Map<String, Object>> getModelOperateType() {
        return ModelOperateLog.getAllOperateTypes();
    }

    @Override
    public Map<String, Object> selectModelOperatePersonAndIp(String logId, String folderId, String modelName, String version,
                                                             String operateType, String operatePerson,
                                                             String startDate, String endDate, String ip) {
        Map<String, Object> param = new HashMap<>();
        param.put("logId", logId);
        param.put("folderId", folderId);
        param.put("modelName", modelName);
        param.put("version", version);
        param.put("operateType", operateType);
        param.put("operatePerson", operatePerson);
        param.put("startDate", startDate);
        param.put("endDate", endDate);
        param.put("ip", ip);
        List<Object> persons = daoHelper.queryForList(_MYBITSID_PREFIX +
                "selectModelOperatePerson", param);
        List<Object> ips = daoHelper.queryForList(_MYBITSID_PREFIX +
                "selectModelOperateIp", param);
        Map<String, Object> result = new HashMap<>();
        result.put("operatePerson", persons);
        result.put("ip", ips);
        return result;
    }


    // --------------------  模型比较 ------------------
    @Override
    public ResponseResult insertModelOperate(RuleDetailWithBLOBs oldRuleDetailWithBLOBs, RuleDetailWithBLOBs newRuleDetailWithBLOBs, RuleDetailWithBLOBs fromRuleDetailHeader, String operateType) {
        // 模型内容比较
        Map<String, Object> operateContent = modelComparService.startModelCompar(oldRuleDetailWithBLOBs, newRuleDetailWithBLOBs, fromRuleDetailHeader);

        ModelOperateLog modelOperate = new ModelOperateLog();
        modelOperate.setOperateType(operateType);
        //TODO : 这里的逻辑需要改,先暂时这样,最快
        if (oldRuleDetailWithBLOBs == null) {
            oldRuleDetailWithBLOBs = fromRuleDetailHeader;
        }
        if (newRuleDetailWithBLOBs != null) {
            String newfolderId = newRuleDetailWithBLOBs.getFolderId();
            RuleFolder newFolder = ruleFolderService.getRuleFolderDetail(newfolderId, null);

            modelOperate.setNewFolderId(newfolderId);
//            modelOperate.setNewFolderName(newFolder.getFolderName());
            modelOperate.setNewRuleName(newRuleDetailWithBLOBs.getRuleName());
            modelOperate.setNewModelName(newRuleDetailWithBLOBs.getModuleName());
            modelOperate.setNewModelId(newRuleDetailWithBLOBs.getRuleId());
            modelOperate.setNewVersion(newRuleDetailWithBLOBs.getVersion());
            if (oldRuleDetailWithBLOBs != null) {
                String oldFolderId = oldRuleDetailWithBLOBs.getFolderId();
                RuleFolder oldFolder = ruleFolderService.getRuleFolderDetail(oldFolderId, null);

                modelOperate.setFolderId(oldFolderId);
                modelOperate.setFolderName(oldFolder.getFolderName());
                modelOperate.setRuleName(oldRuleDetailWithBLOBs.getRuleName());
                modelOperate.setModelName(oldRuleDetailWithBLOBs.getModuleName());
                modelOperate.setModelId(oldRuleDetailWithBLOBs.getRuleId());
                modelOperate.setVersion(oldRuleDetailWithBLOBs.getVersion());
            }
        } else if (oldRuleDetailWithBLOBs != null) {
            String oldFolderId = oldRuleDetailWithBLOBs.getFolderId();
            RuleFolder oldFolder = ruleFolderService.getRuleFolderDetail(oldFolderId, null);

            modelOperate.setFolderId(oldFolderId);
            modelOperate.setFolderName(oldFolder.getFolderName());
            modelOperate.setModelId(oldRuleDetailWithBLOBs.getRuleId());
            modelOperate.setRuleName(oldRuleDetailWithBLOBs.getRuleName());
            modelOperate.setModelName(oldRuleDetailWithBLOBs.getModuleName());
            modelOperate.setVersion(oldRuleDetailWithBLOBs.getVersion());
        }
        modelOperate.setOperateContent(JsonUtils.toJSONString2(operateContent));

        insertModelOperate(modelOperate);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    public ResponseResult insertModelOperate(ModelOperateLog modelOperate) {

        String modelName = modelOperate.getModelName();
        if (StringUtils.isBlank(modelName)) {
            modelName = modelOperate.getNewModelName();
            if (StringUtils.isBlank(modelName)) {
                return ResponseResult.createFailInfo("参数[modelName]不能为空");
            }
        }

        final String logId = IdUtil.createId();
        modelOperate.setLogId(logId);
        modelOperate.setOperateTime(new Date());
        try {
            // FIXME: 为业务团队对接临时实现的一种方案 真实用户
            HttpSession session = ControllerUtil.getCurrentSession();
            String realUser = ControllerUtil.getRealUserAndUserAccount();
            if (log.isDebugEnabled()) {
                log.debug("获取到token中的用户：" + realUser);
            }
            if (StringUtils.isBlank(realUser)) {
                realUser = (String) session.getAttribute(ConstantFinal.SESSION_KEY_USER_ID);
                if (log.isDebugEnabled()) {
                    log.debug("获取到session中的用户：" + realUser);
                }
            }
            modelOperate.setOperatePerson(realUser);

            String ip = (String) session.getAttribute(ConstantFinal.SESSION_KEY_USER_IP);
            modelOperate.setIp(ip);
        } catch (Exception e) {
            log.warn("插入模型操作日志获取当前用户和ip失败,日志ID:[" + modelOperate.getLogId() + "]");
        }
        daoHelper.insert(_MYBITSID_PREFIX + "insertModelOperate", modelOperate);
        log.trace(modelOperate);
        return ResponseResult.createSuccessInfo();
    }


    @Override
    @Deprecated
    public ResponseResult modeVersionAllInfoModelOperate(RuleDetailWithBLOBs oldRuleDetailWithBLOBs, RuleDetailWithBLOBs newRuleDetailWithBLOBs, RuleDetailWithBLOBs fromRuleDetailHeader, String operateType) {
        return insertModelOperate(oldRuleDetailWithBLOBs, newRuleDetailWithBLOBs, fromRuleDetailHeader, operateType);
    }


    @Override
    public ResponseResult modelHeaderModelOperate(RuleDetailHeader oldRuleDetailHeader, RuleDetailHeader newRuleDetailHeader, RuleDetailWithBLOBs fromRuleDetailHeader, String operateType) {
        RuleDetailWithBLOBs oldRuleDetailWithBLOBs = null;
        RuleDetailWithBLOBs newRuleDetailWithBLOBs = null;
        try {
            if (oldRuleDetailHeader != null) {
                oldRuleDetailWithBLOBs = new RuleDetailWithBLOBs();
                org.springframework.beans.BeanUtils.copyProperties(oldRuleDetailHeader, oldRuleDetailWithBLOBs);
            }
            if (newRuleDetailHeader != null) {
                newRuleDetailWithBLOBs = new RuleDetailWithBLOBs();
                org.springframework.beans.BeanUtils.copyProperties(newRuleDetailHeader, newRuleDetailWithBLOBs);
            }
        } catch (BeansException e) {
            throw new RuntimeException("转换请求参数失败");
        }
        return insertModelOperate(oldRuleDetailWithBLOBs, newRuleDetailWithBLOBs, fromRuleDetailHeader, operateType);
    }

    @Override
    @Deprecated
    public ResponseResult cloneRuleModelOperate(RuleDetailWithBLOBs fromRuleDetailHeader, RuleDetailWithBLOBs newRuleDetailWithBLOBs, String operateType) {
        return insertModelOperate(null, newRuleDetailWithBLOBs, fromRuleDetailHeader, operateType);
    }


}
