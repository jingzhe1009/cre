package com.bonc.frame.service.impl.task;

import com.bonc.frame.dao.DaoHelper;
import com.bonc.frame.entity.task.VariableMapping;
import com.bonc.frame.service.task.VariableMappingService;
import com.bonc.frame.util.IdUtil;
import com.bonc.frame.util.ResponseResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yedunyao
 * @date 2019/5/29 10:08
 */
@Service
public class VariableMappingServiceImpl implements VariableMappingService {

    private Log log = LogFactory.getLog(VariableMappingServiceImpl.class);

    @Autowired
    private DaoHelper daoHelper;

    private final String _MAPPING_PREFIX = "com.bonc.frame.mapper.task.VariableMappingMapper.";

    @Override
    public VariableMapping getOne(String id) {
        return (VariableMapping) daoHelper.queryOne(_MAPPING_PREFIX + "selectByPrimaryKey", id);
    }

    @Override
    public boolean isExists(String id) {
        if (getOne(id) == null) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isExists(VariableMapping variableMapping) {
        return isExists(variableMapping.getId());
    }

    @Override
    public List<VariableMapping> selectByTaskId(String taskId) {
        return daoHelper.queryForList(_MAPPING_PREFIX + "selectByTaskId", taskId);
    }

    @Override
    public List<VariableMapping> getInputVariables(String taskId) {
        return selectByVariableKind(taskId, "K1");
    }

    @Override
    public List<VariableMapping> getOutputVariables(String taskId) {
        return selectByVariableKind(taskId, "K2");
    }

    @Override
    public List<VariableMapping> selectByVariableKind(String taskId, String variableKind) {
        Map<String, String> param = new HashMap<>();
        param.put("taskId", taskId);
        param.put("variableKind", variableKind);
        return daoHelper.queryForList(_MAPPING_PREFIX + "selectByVariableKind", param);
    }

    @Override
    @Transactional
    public ResponseResult save(VariableMapping variableMapping) {
        variableMapping.setId(IdUtil.createId());
        daoHelper.insert(_MAPPING_PREFIX + "insertSelective", variableMapping);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional
    public ResponseResult save(List<VariableMapping> variableMappings) {
        if (variableMappings == null) {
            return ResponseResult.createFailInfo("参数-元数据映射为空");
        }
        for (VariableMapping variableMapping : variableMappings) {
            variableMapping.setId(IdUtil.createId());
        }
        daoHelper.insert(_MAPPING_PREFIX + "insertBatch", variableMappings);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional
    public ResponseResult update(VariableMapping variableMapping) {
        if (!isExists(variableMapping)) {
            ResponseResult.createFailInfo("该条记录不存在");
        }
        daoHelper.update(_MAPPING_PREFIX + "updateByPrimaryKeySelective", variableMapping);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional
    public ResponseResult update(List<VariableMapping> variableMappings) {
        if (variableMappings == null) {
            return ResponseResult.createFailInfo("参数-元数据映射为空");
        }
        daoHelper.update(_MAPPING_PREFIX + "updateBatch", variableMappings);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional
    public ResponseResult deleteByTaskId(String taskId) {
        if (StringUtils.isBlank(taskId)) {
            return ResponseResult.createFailInfo("参数[taskId]不能为null");
        }
        daoHelper.delete(_MAPPING_PREFIX + "deleteByTaskId", taskId);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional
    public ResponseResult deleteByTaskIds(List<String> ids) {
        if (ids == null) {
            return ResponseResult.createFailInfo("请求参数不能为null");
        }
        daoHelper.delete(_MAPPING_PREFIX + "deleteByTaskIds", ids);
        return ResponseResult.createSuccessInfo();
    }

}
