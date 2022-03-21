package com.bonc.frame.service.modelBase;

import com.bonc.frame.entity.commonresource.ModelGroup;
import com.bonc.frame.util.ResponseResult;

import java.util.Map;

/**
 * 模型库
 *
 * @author yedunyao
 * @date 2019/9/2 15:50
 */
public interface ModelBaseService {

    // ------------------------ 模型组管理 ------------------------

    ResponseResult getModelGroups(String modelGroupName);

    ModelGroup getModelGroupByModelId(String modelGroupId);

    ModelGroup getModelGroupByModelName(String modelGroupName);

    Map<String, Object> getModelGroupsPaged(String modelGroupName, String start, String length);

    ResponseResult createModelGroup(ModelGroup modelGroup, String userId);

    void insertModelGroupDataPersistence(ModelGroup modelGroup);

    ResponseResult updateModelGroup(ModelGroup modelGroup, String userId);

    boolean isGroupUsed(String modelGroupId);

    ResponseResult deleteModelGroup(String modelGroupId);

}
