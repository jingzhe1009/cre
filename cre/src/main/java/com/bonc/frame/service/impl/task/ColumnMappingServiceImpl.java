package com.bonc.frame.service.impl.task;

import com.bonc.frame.dao.DaoHelper;
import com.bonc.frame.entity.task.ColumnMapping;
import com.bonc.frame.service.task.ColumnMappingService;
import com.bonc.frame.util.IdUtil;
import com.bonc.frame.util.ResponseResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author yedunyao
 * @date 2019/6/12 10:00
 */
@Service("columnMappingService")
public class ColumnMappingServiceImpl implements ColumnMappingService {

    private Log log = LogFactory.getLog(ColumnMappingServiceImpl.class);

    @Autowired
    private DaoHelper daoHelper;

    private final String _MAPPING_PREFIX = "com.bonc.frame.mapper.task.ColumnMappingMapper.";


    @Override
    public ColumnMapping getOne(String id) {
        return (ColumnMapping) daoHelper.queryOne(_MAPPING_PREFIX + "selectByPrimaryKey", id);
    }

    @Override
    public boolean isExists(String id) {
        if (getOne(id) == null) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isExistsByTaskId(String taskId) {
        if (selectByTaskId(taskId) == null) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isExists(ColumnMapping columnMapping) {
        final String id = columnMapping.getId();
        if (StringUtils.isNotEmpty(id)) {
            return isExists(id);
        }
        return isExistsByTaskId(columnMapping.getTaskId());
    }

    @Override
    public ColumnMapping selectByTaskId(String taskId) {
        return (ColumnMapping) daoHelper.queryOne(_MAPPING_PREFIX + "selectByTaskId", taskId);
    }

    @Override
    @Transactional
    public ResponseResult save(ColumnMapping columnMapping) {
        columnMapping.setId(IdUtil.createId());
        daoHelper.insert(_MAPPING_PREFIX + "insertSelective", columnMapping);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional
    public ResponseResult update(ColumnMapping columnMapping) {
        if (!isExists(columnMapping)) {
            ResponseResult.createFailInfo("该条记录不存在");
        }
        daoHelper.update(_MAPPING_PREFIX + "updateByPrimaryKeySelective", columnMapping);
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
