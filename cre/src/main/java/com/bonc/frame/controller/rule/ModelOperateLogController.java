package com.bonc.frame.controller.rule;


import com.bonc.frame.service.rule.ModelOperateLogService;
import com.bonc.frame.util.ResponseResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;
/**
* 模型操作日志
* */
@Controller
@RequestMapping("/modelOperateLog")
public class ModelOperateLogController {

    @Autowired
    private ModelOperateLogService modelOperateLogService;

    @RequestMapping("/selectModelOperateLog")
    @ResponseBody
    public Map<String, Object> selectModelOperateLog(String logId, String folderId, String modelName, String version,
                                                     String operateType, String operatePerson,
                                                     String startDate, String endDate, String ip,
                                                     String start, String length) {
        if (!StringUtils.isBlank(version)) {
            version = version.replaceFirst("\\(草稿\\)", "");
        }
        return modelOperateLogService.selectModelOperate(logId, folderId, modelName, version, operateType, operatePerson, startDate, endDate, ip, start, length);
    }

    @RequestMapping("/selectModelOperatePersonAndIp")
    @ResponseBody
    public ResponseResult selectModelOperatePersonAndIp(String logId, String folderId, String modelName, String version,
                                                        String operateType, String operatePerson,
                                                        String startDate, String endDate, String ip) {

        return ResponseResult.createSuccessInfo("", modelOperateLogService.selectModelOperatePersonAndIp(logId, folderId, modelName, version, operateType, operatePerson, startDate, endDate, ip));
    }

    @RequestMapping("/getModelOperateType")
    @ResponseBody
    public ResponseResult getModelOperateType() {
        return ResponseResult.createSuccessInfo("", modelOperateLogService.getModelOperateType());
    }


}
