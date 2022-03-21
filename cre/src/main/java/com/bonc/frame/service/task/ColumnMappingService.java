package com.bonc.frame.service.task;

import com.bonc.frame.entity.task.ColumnMapping;
import com.bonc.frame.util.ResponseResult;

import java.util.List;

/**
 * @author yedunyao
 * @date 2019/6/12 10:00
 */
public interface ColumnMappingService {

    ColumnMapping getOne(String id);

    boolean isExists(String id);

    boolean isExistsByTaskId(String taskId);

    boolean isExists(ColumnMapping columnMapping);

    ColumnMapping selectByTaskId(String taskId);

    ResponseResult save(ColumnMapping columnMapping);

    ResponseResult update(ColumnMapping columnMapping);

    ResponseResult deleteByTaskId(String taskId);

    ResponseResult deleteByTaskIds(List<String> ids);

}
