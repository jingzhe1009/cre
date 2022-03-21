package com.bonc.frame.service.impl.modelBase;

import com.bonc.frame.dao.DaoHelper;
import com.bonc.frame.entity.commonresource.ModelGroup;
import com.bonc.frame.service.modelBase.ModelBaseService;
import com.bonc.frame.util.ControllerUtil;
import com.bonc.frame.util.IdUtil;
import com.bonc.frame.util.ResponseResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 模型库
 *
 * @author yedunyao
 * @date 2019/9/2 15:50
 */
@Service
public class ModelBaseServiceImpl implements ModelBaseService {

    private static final String _MODEL_GROUP_MAPPER = "com.bonc.frame.mapper.resource.ModelGroupMapper.";

    @Autowired
    private DaoHelper daoHelper;

    // ------------------------ 模型组管理 ------------------------

    // 获取参数组列表
    @Override
    @Transactional
    public ResponseResult getModelGroups(String modelGroupName) {
        Map<String, Object> param = new HashMap<>();
        param.put("modelGroupName", modelGroupName);
        final List<Object> list = daoHelper.queryForList(_MODEL_GROUP_MAPPER +
                "getByGroupName", param);
        return ResponseResult.createSuccessInfo("success", list);
    }

    @Override
    @Transactional
    public ModelGroup getModelGroupByModelId(String modelGroupId) {
        Map<String, Object> param = new HashMap<>();
        param.put("modelGroupId", modelGroupId);
        return (ModelGroup) daoHelper.queryOne(_MODEL_GROUP_MAPPER +
                "getByGroupName", param);
    }

    @Override
    @Transactional
    public ModelGroup getModelGroupByModelName(String modelGroupName) {
        Map<String, Object> param = new HashMap<>();
        param.put("modelGroupName", modelGroupName);
        return (ModelGroup) daoHelper.queryOne(_MODEL_GROUP_MAPPER +
                "getModelGroupByGroupName", param);
    }

    @Override
    @Transactional
    public Map<String, Object> getModelGroupsPaged(String modelGroupName, String start, String length) {
        Map<String, Object> param = new HashMap<>();
        param.put("modelGroupName", modelGroupName);
        final Map<String, Object> map = daoHelper.queryForPageList(_MODEL_GROUP_MAPPER +
                "getByGroupName", param, start, length);
        return map;
    }

    private boolean checkGroupNameIsExist(String modelGroupName, @Nullable String modelGroupId) {
        Map<String, String> param = new HashMap<>();
        param.put("modelGroupName", modelGroupName);
        param.put("modelGroupId", modelGroupId);
        int obj = (int) daoHelper.queryOne(_MODEL_GROUP_MAPPER + "countByName", param);
        if (obj > 0) {
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public ResponseResult createModelGroup(ModelGroup modelGroup, String userId) {
        if (modelGroup == null) {
            return ResponseResult.createFailInfo("请求参数[modelGroup]为空");
        }

        if (checkGroupNameIsExist(modelGroup.getModelGroupName(), null)) {
            return ResponseResult.createFailInfo("模型组名称已存在");
        }

        modelGroup.setModelGroupId(IdUtil.createId());
        modelGroup.setCreateDate(new Date());
        modelGroup.setCreatePerson(userId);
        insertModelGroupDataPersistence(modelGroup);
//        daoHelper.insert(_MODEL_GROUP_MAPPER + "insertSelective", modelGroup);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertModelGroupDataPersistence(ModelGroup modelGroup) {
        if (modelGroup != null) {
            String currentUser = ControllerUtil.getCurrentUser();
            modelGroup.setCreateDate(new Date());
            modelGroup.setCreatePerson(currentUser);
            daoHelper.insert(_MODEL_GROUP_MAPPER + "insertSelective", modelGroup);
        }
    }

    @Override
    @Transactional
    public ResponseResult updateModelGroup(ModelGroup modelGroup, String userId) {
        if (modelGroup == null) {
            return ResponseResult.createFailInfo("请求参数[modelGroup]不能为空");
        }

        if (checkGroupNameIsExist(modelGroup.getModelGroupName(), modelGroup.getModelGroupId())) {
            return ResponseResult.createFailInfo("模型组名称已存在");
        }

        modelGroup.setUpdateDate(new Date());
        modelGroup.setUpdatePerson(userId);
        daoHelper.update(_MODEL_GROUP_MAPPER + "updateByPrimaryKeySelective", modelGroup);
        return ResponseResult.createSuccessInfo();
    }

    // 当前模型组是否在使用中
    @Override
    @Transactional
    public boolean isGroupUsed(String modelGroupId) {
        int obj = (int) daoHelper.queryOne(_MODEL_GROUP_MAPPER +
                "countGroupUsed", modelGroupId);
        if (obj > 0) {
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public ResponseResult deleteModelGroup(String modelGroupId) {
        if (StringUtils.isBlank(modelGroupId)) {
            return ResponseResult.createFailInfo("请求参数[modelGroupId]不能为空");
        }

        if (isGroupUsed(modelGroupId)) {
            return ResponseResult.createFailInfo("当前模型组正在使用中，不能删除");
        }

        daoHelper.delete(_MODEL_GROUP_MAPPER + "deleteByPrimaryKey", modelGroupId);
        return ResponseResult.createSuccessInfo();
    }

}
