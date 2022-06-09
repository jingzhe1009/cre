package com.bonc.frame.service.monitor;



import com.bonc.frame.entity.monitor.DescResult;
import com.bonc.frame.entity.monitor.MonitorParam;
import com.bonc.frame.entity.monitor.RuleLog;
import com.bonc.framework.rule.log.IRuleLog;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;


public interface MonitorService extends IRuleLog {

    Map<String, Object>getUseTime(@RequestBody MonitorParam monitorParam);

    Map<String,Object> excuteResult(MonitorParam monitorParam);

    Map<String, Object> getExcuteCount(MonitorParam monitorParam);

    List<String> getTitle(String flag,String method);

    Map<String, Object> excuteState(@RequestBody MonitorParam monitorParam);

    Map<String, Object> ruleHit(@RequestBody MonitorParam monitorParam);

    Map<String, Object> scoreHit(@RequestBody MonitorParam monitorParam);

    void exportExcel(HttpServletResponse response, HttpServletRequest request);

    Map<String, Object> desc(@RequestBody MonitorParam monitorParam);

    /**
     * 监控-日志检索-分页展示日志数据
     * @param monitorParam 筛选参数
     * @param start 分页参数-页码
     * @param length 分页参数-每页数量
     * @return 日志数据
     */
    Map<String, Object> getLogPage(MonitorParam monitorParam,String start,String length);

    /**
     * 获取日志详情
     * @param logId 日志id
     * @return 详情信息
     */
    DescResult getRuleLogDesc(String logId);

    /**
     * 日志初步存储
     * @param log 日志详情
     */
    void saveModelLog (RuleLog log);

    /**
     * 更新模型调用日志
     * @param ruleLogId id
     * @param start 开始时间
     * @param msg 返回数据
     * @param excep 异常数据
     */
    void saveEndTimeAndMsg(String ruleLogId,Date start, String msg, StringBuffer excep);

    /**
     * 查询跳转日志检索
     * @param
     * @return 详情信息
     */
    Map<String, Object> search(MonitorParam monitorParam,String start,String length);
}
