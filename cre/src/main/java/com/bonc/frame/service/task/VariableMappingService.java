package com.bonc.frame.service.task;

import com.bonc.frame.entity.task.VariableMapping;
import com.bonc.frame.util.ResponseResult;

import java.util.List;

/**
 * @author yedunyao
 * @date 2019/5/29 10:08
 */
public interface VariableMappingService {

    VariableMapping getOne(String id);

    boolean isExists(String id);

    boolean isExists(VariableMapping variableMapping);

    List<VariableMapping> selectByTaskId(String taskId);

    List<VariableMapping> getInputVariables(String taskId);

    List<VariableMapping> getOutputVariables(String taskId);

    List<VariableMapping> selectByVariableKind(String taskId, String variableKind);

    ResponseResult save(VariableMapping variableMapping);

    ResponseResult save(List<VariableMapping> variableMappings);

    ResponseResult update(VariableMapping variableMapping);

    ResponseResult update(List<VariableMapping> variableMappings);

    ResponseResult deleteByTaskId(String taskId);

    ResponseResult deleteByTaskIds(List<String> ids);


}
