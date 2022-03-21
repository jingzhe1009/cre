package com.bonc.frame.service.analysis;

import com.bonc.frame.entity.analysis.ModelExecuteCountInfo;
import com.bonc.frame.entity.analysis.ModelExecuteStatistics;
import com.bonc.frame.entity.analysis.ModelStatisticsInfo;
import com.bonc.frame.entity.analysis.TaskStatusStatisInfo;

import java.util.List;
import java.util.Map;

public interface AnalysisService {
    // 统计一周以内 所有模型的执行情况
    ModelExecuteCountInfo allmodelExecuteStatis(int day, String folderId, String deptId);

    // 统计一周以内 被调用次数前三的 模型
    Map<String, Object> allmodelExecuteTopThree(int day, String size);

    // 统计所有模型的 类型,以及各类型的数量,版本数量
    List<ModelStatisticsInfo> allmodelStatis();

    List<ModelStatisticsInfo> allModelStatusStatis();

    List<TaskStatusStatisInfo> taskStatusStatis();

}
