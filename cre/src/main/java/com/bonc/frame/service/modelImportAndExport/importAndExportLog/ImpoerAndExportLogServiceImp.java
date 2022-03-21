package com.bonc.frame.service.modelImportAndExport.importAndExportLog;

import com.bonc.frame.config.ConstantFinal;
import com.bonc.frame.dao.DaoHelper;
import com.bonc.frame.entity.modelImportAndExport.ImportAndExportOperateLog;
import com.bonc.frame.service.auth.AuthorityService;
import com.bonc.frame.service.modelImportAndExport.ImpoerAndExportLogService;
import com.bonc.frame.util.ControllerUtil;
import com.bonc.frame.util.IdUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/4/16 14:14
 */
@Service
public class ImpoerAndExportLogServiceImp implements ImpoerAndExportLogService {

    Log log = LogFactory.getLog(ImpoerAndExportLogServiceImp.class);

    private final String _MYBITSID_PREFIX = "com.bonc.frame.dao.rule.ModelImportAndExportMapper.";

    @Autowired
    private DaoHelper daoHelper;
    @Autowired
    private AuthorityService authorityService;

    @Override
    public Map<String, Object> selectImportAndExportOperateLogBaseInfoPage(String logId, String operateType, String success, String systemVersion, String fileName, String modelImportStrategy,
                                                                           String operatePerson, String startDate, String endDate, String ip,
                                                                           String start, String length) {
        Map<String, Object> param = new HashMap<>();
        param.put("logId", logId);
        param.put("operateType", operateType);
        param.put("success", success);
        param.put("systemVersion", systemVersion);
        param.put("fileName", fileName);
        param.put("operatePerson", operatePerson);
        param.put("startDate", startDate);
        param.put("endDate", endDate);
        param.put("ip", ip);
        // 权限验证, 是否拥有全部权限
        String currentUser = ControllerUtil.getCurrentUser();
        if (!authorityService.isUserHasAllPermits(currentUser)) {
            param.put("currentUserId", currentUser);
        }

        return daoHelper.queryForPageList(_MYBITSID_PREFIX + "selectImportAndExportOperateLogBaseInfo",
                param, start, length);
    }

    @Override
    public Map<String, Object> selectImportAndExportOperateLogWithContentPage(String logId, String operateType, String success, String systemVersion, String fileName, String modelImportStrategy,
                                                                              String operatePerson, String startDate, String endDate, String ip,
                                                                              String start, String length) {
        Map<String, Object> param = new HashMap<>();
        param.put("logId", logId);
        param.put("operateType", operateType);
        param.put("success", success);
        param.put("systemVersion", systemVersion);
        param.put("fileName", fileName);
        param.put("modelImportStrategy", modelImportStrategy);
        param.put("operatePerson", operatePerson);
        param.put("startDate", startDate);
        param.put("endDate", endDate);
        param.put("ip", ip);
        // 权限验证, 是否拥有全部权限
        String currentUser = ControllerUtil.getCurrentUser();
        if (!authorityService.isUserHasAllPermits(currentUser)) {
            param.put("currentUserId", currentUser);
        }
        return daoHelper.queryForPageList(_MYBITSID_PREFIX + "selectImportAndExportOperateLogWithContent",
                param, start, length);
    }

    @Override
    public ImportAndExportOperateLog selectOneImportAndExportOperateLogWithContent(String logId, String operateType, String success, String systemVersion, String fileName,
                                                                                   String operatePerson, String startDate, String endDate, String ip) {
        Map<String, Object> param = new HashMap<>();
        param.put("logId", logId);
        param.put("operateType", operateType);
        param.put("success", success);
        param.put("systemVersion", systemVersion);
        param.put("fileName", fileName);
        param.put("operatePerson", operatePerson);
        param.put("startDate", startDate);
        param.put("endDate", endDate);
        param.put("ip", ip);

        return (ImportAndExportOperateLog) daoHelper.queryOne(_MYBITSID_PREFIX + "selectImportAndExportOperateLogWithContent", param);
    }


    @Override
    public void insertImportAndExportOperateLog(ImportAndExportOperateLog importAndExportOperateLog) {
        if (importAndExportOperateLog == null) {
            return;
        }
        String logId = importAndExportOperateLog.getLogId();
        if (StringUtils.isBlank(logId)) {
            logId = IdUtil.createId();
            importAndExportOperateLog.setLogId(logId);
        }
        Date operateDate = importAndExportOperateLog.getOperateDate();
        if (operateDate == null) {
            importAndExportOperateLog.setOperateDate(new Date());
        }
        try {
            // FIXME: 为业务团队对接临时实现的一种方案 真实用户
            HttpSession session = ControllerUtil.getCurrentSession();
            String realUser = (String) session.getAttribute(ConstantFinal.SESSION_KEY);
            importAndExportOperateLog.setOperatePerson(realUser);

            String ip = (String) session.getAttribute(ConstantFinal.SESSION_KEY_USER_IP);
            importAndExportOperateLog.setIp(ip);
        } catch (Exception e) {
            log.warn("插入导入导出日志获取当前用户和ip失败,日志ID:[" + importAndExportOperateLog.getLogId() + "]");
        }
        daoHelper.insert(_MYBITSID_PREFIX + "insertImportAndExportOperateLog", importAndExportOperateLog);
//        log.trace(importAndExportOperateLog);
    }

}
