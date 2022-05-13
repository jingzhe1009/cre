package com.bonc.frame.service.monitor;



import com.bonc.frame.entity.monitor.DescResult;
import com.bonc.frame.entity.monitor.MonitorParam;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;


public interface MonitorService {

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
}
