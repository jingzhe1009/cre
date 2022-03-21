package com.bonc.frame.module.log.deleteLog;

import com.bonc.frame.module.quartz.DeleteLogService;
import com.bonc.frame.util.ResponseResult;
import com.bonc.frame.util.SpringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/1/3 10:36
 */

public class DeleteLogTask {
    private Log log = LogFactory.getLog(DeleteLogService.class);
    private DeleteLogService deleteLogService;

    public DeleteLogTask() {
        this.deleteLogService = (DeleteLogService) SpringUtils.getBean("deleteLogService");
    }

    public void startDeleteLog() {
        deleteLogService.startDeleteRuleLog();
    }

    public void startDeleteRuleTaskLog() {
        deleteLogService.startDeleteRuleTaskLog();
    }

    public ResponseResult deleteLog(String day) {
        try {
            return deleteLogService.deleteLog(day);
        } catch (Exception e) {
            //FIXME : 要不要设置一个时间,失败后再次执行
            throw e;
        }
    }

    public ResponseResult deleteRuletaskLog(String day) {
        try {
            return deleteLogService.deleteRuleTaskLog(day);
        } catch (Exception e) {
            //FIXME : 要不要设置一个时间,失败后再次执行
            throw e;
        }
    }
}
