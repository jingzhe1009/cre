package com.bonc.frame.service.impl.syslog;

import com.bonc.frame.config.ConstantFinal;
import com.bonc.frame.dao.DaoHelper;
import com.bonc.frame.entity.syslog.SysLog;
import com.bonc.frame.service.auth.AuthorityService;
import com.bonc.frame.service.syslog.SysLogService;
import com.bonc.frame.util.ConstantUtil;
import com.bonc.frame.util.ControllerUtil;
import com.bonc.frame.util.ResponseResult;
import com.bonc.framework.util.IdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yedunyao
 * @date 2019/5/7 16:34
 */
@Service
public class SysLogServiceImpl implements SysLogService {

    @Autowired
    private DaoHelper daoHelper;

    private final String _MYBITSID_PREFIX = "com.bonc.frame.mapper.syslog.SysLogMapper.";

    @Autowired
    private AuthorityService authorityService;

    @Override
    public Map<String, Object> getSysLogList(String userName, String startDate, String endDate, String operateType,
                                             String sort, String order, String logType, String start, String size) {
        String currentUser = ControllerUtil.getCurrentUser();
        Map<String,Object> param = new HashMap<String,Object>();
        param.put("logType", logType);
        param.put("userName", userName);
        param.put("operateType", operateType);
        param.put("startDate", startDate);
        param.put("endDate", endDate);
        param.put("sort", sort);
        param.put("order", order);
        if (!authorityService.isUserHasAllPermits(currentUser)) {
            param.put("userId", currentUser);
        }
        Map<String, Object> result = daoHelper.queryForPageList(_MYBITSID_PREFIX + "getSysLogList", param, start, size);
        return result;
    }

    @Override
    @Transactional
    public ResponseResult logSys(String operateType, String result, HttpSession session) {
        final SysLog sysLog = new SysLog();
        sysLog.setLogType(ConstantUtil.SYS_LOG_TYPE_SYS);
        sysLog.setOperateType(operateType);
        sysLog.setOperateResult(result);
        logUser(sysLog, session);
        return insert(sysLog);
    }

    @Override
    @Transactional
    public ResponseResult logSys(String operateType, String info, String result, HttpSession session) {
        final SysLog sysLog = new SysLog();
        sysLog.setLogType(ConstantUtil.SYS_LOG_TYPE_SYS);
        sysLog.setOperateType(operateType);
        sysLog.setOperateInfo(info);
        sysLog.setOperateResult(result);
        logUser(sysLog, session);
        return insert(sysLog);
    }

    @Override
    @Transactional
    public ResponseResult logOperate(String operateType, String info, String result, HttpSession session) {
        final SysLog sysLog = new SysLog();
        sysLog.setLogType(ConstantUtil.SYS_LOG_TYPE_OPERATE);
        sysLog.setOperateType(operateType);
        sysLog.setOperateInfo(info);
        sysLog.setOperateResult(result);
        logUser(sysLog, session);
        return insert(sysLog);
    }

    @Override
    @Transactional
    public ResponseResult logOperate(String operateType, String info) {
        return logOperate(operateType, info, "");
    }

    @Override
    @Transactional
    public ResponseResult logOperate(String operateType, String info, String result) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session = request.getSession();
        return logOperate(operateType, info, result, session);
    }

    private void logUser(SysLog sysLog, HttpSession session) {
        if (session != null) {
            sysLog.setUserName((String) session.getAttribute(ConstantFinal.SESSION_KEY_USER_NAME));
            sysLog.setUserId((String) session.getAttribute(ConstantFinal.SESSION_KEY_USER_ID));
            sysLog.setIp((String) session.getAttribute(ConstantFinal.SESSION_KEY_USER_IP));
        }
    }

    @Override
    @Transactional
    public ResponseResult insert(SysLog sysLog) {
        sysLog.setLogId(IdUtil.createId());
        sysLog.setOperateTime(new Date());
        daoHelper.insert(_MYBITSID_PREFIX + "insertSelective", sysLog);
        ResponseResult result = ResponseResult.createSuccessInfo();
        return result;
    }
}
