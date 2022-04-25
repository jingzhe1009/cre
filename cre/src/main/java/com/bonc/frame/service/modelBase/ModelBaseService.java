package com.bonc.frame.service.modelBase;

import com.bonc.frame.entity.commonresource.*;
import com.bonc.frame.util.ResponseResult;

import java.util.List;
import java.util.Map;

/**
 * 模型库
 *
 * @author yedunyao
 * @date 2019/9/2 15:50
 */
public interface ModelBaseService {

    // ------------------------ 模型组管理 ------------------------

    ResponseResult getModelGroups(String modelGroupName,String loginUserId);

    ModelGroup getModelGroupByModelId(String modelGroupId);

    ModelGroup getModelGroupByModelName(String modelGroupName);

    Map<String, Object> getModelGroupsPaged(String loginUserId,String modelGroupName, String channelId, String start, String length,String startDate,
                                            String endDate);

    ResponseResult createModelGroup(ModelGroupDto modelGroup, String userId);

    void insertModelGroupDataPersistence(ModelGroup modelGroup);

    ResponseResult updateModelGroup(ModelGroupDto modelGroup, String userId);

    boolean isGroupUsed(String modelGroupId);

    ResponseResult deleteModelGroup(String modelGroupId);

    /**
     * 产品设置调用渠道
     * @param modelGroupId  产品的id
     * @param dto  渠道的id的集合
     * @return 操作结果
     */
    ResponseResult groupAddChannel(String modelGroupId, ModelChanIdDto dto);

    /**
     * 获取渠道树
     * @param loginUserId 用户id，区分权限
     * @param modelGroupId 当前产品id
     * @return 渠道树
     */
    List<ModelGroupChannelVo> channelList(String loginUserId, String modelGroupId);

    ModelGroup getGroupInfoById(String modelGroupId);

    List<ModelVersion> modelGetVersion(String modelId);

    List<RuleSetForModel> modelVersionWithRuleSet(String modelId);
}
