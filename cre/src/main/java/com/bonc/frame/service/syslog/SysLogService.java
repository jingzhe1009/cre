package com.bonc.frame.service.syslog;

import com.bonc.frame.entity.syslog.SysLog;
import com.bonc.frame.util.ResponseResult;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author yedunyao
 * @date 2019/5/7 16:17
 */
public interface SysLogService {

    /**
     * 分页获取函数列表
     * @param logType
     * @param start
     * @param size
     * @return
     */
    Map<String, Object> getSysLogList(String userName, String startDate, String endDate, String operateType,
                                      String sort, String order, String logType, String start, String size);

    ResponseResult insert(SysLog sysLog);

    ResponseResult logSys(String operateType, String result, HttpSession session);

    ResponseResult logSys(String operateType, String info, String result, HttpSession session);

    ResponseResult logOperate(String operateType, String info);

    ResponseResult logOperate(String operateType, String info, String result);

    ResponseResult logOperate(String operateType, String info, String result, HttpSession session);

}
