package com.bonc.frame.module.task.ruleTask;

import com.bonc.frame.module.task.TaskInfo;

import java.io.Serializable;
import java.util.Map;

/**
 * 由定时任务扫描到的子任务的信息
 *
 * @author yedunyao
 * @date 2019/6/12 17:03
 */
public class RuleTaskJobInfo extends TaskInfo<Map<String, Object>> implements Serializable {

    private static final long serialVersionUID = -4632641037821315187L;

    public RuleTaskJobInfo(String taskId, Object jobKey, Map<String, Object> data) {
        this.taskId = taskId;
        this.jobKey = jobKey;
        this.finalKey = buildKey();
        this.data = data;
    }

    @Override
    public String toString() {
        return "RuleTaskJobInfo{" +
                "taskId='" + taskId + '\'' +
                ", jobKey=" + jobKey +
                ", finalKey='" + finalKey + '\'' +
//                ", data=" + data +
                '}';
    }
}
