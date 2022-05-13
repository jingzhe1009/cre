package com.bonc.frame.service.monitor;



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
}
