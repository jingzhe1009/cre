package com.bonc.frame.module.task.outputTask;

import java.util.Map;

/**
 * 根据任务id，将不同数据源的结果映射到不同的队列中
 *
 * @author yedunyao
 * @date 2019/7/12 15:08
 */
public class OutputTaskMappedHandler<T> {

    // 单例模式，负责结果的转化、缓存、写入

    private Map<String, OutputTaskQueuedHandler> map;

    public void save(T t) throws Exception {
        // 任务信息：id datasource data

        // 获取参数到字段的映射表

        // 根据数据源、映射表转化数据 map? sql? csv?

        // 根据 taskId 放到不同的队列中
    }

    // 提供队列的释放

}
