package com.bonc.frame.service.ruleSetBase;

import com.bonc.frame.entity.commonresource.RuleSet;
import com.bonc.frame.entity.commonresource.RuleSetGroup;
import com.bonc.frame.entity.commonresource.RuleSetHeader;
import com.bonc.frame.entity.commonresource.RuleSetReferenceExt;
import com.bonc.frame.util.ResponseResult;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * @author yedunyao
 * @date 2019/9/2 15:50
 */
public interface RuleSetBaseService {

    // ------------------------ 规则集头信息管理 ------------------------

    boolean checkNameIsExist(String ruleSetName, @Nullable String ruleSetHeaderId);

    /**
     * 查询规则集头信息列表，无分页
     *
     * @param ruleSetName
     * @param ruleSetGroupName
     * @param startDate
     * @param endDate
     * @return
     */
    ResponseResult getHeaderList(@Nullable String ruleSetName,
                                 @Nullable String ruleSetGroupName,
                                 @Nullable String startDate,
                                 @Nullable String endDate);

    /**
     * 分页查询规则集头信息列表
     *
     * @param ruleSetName
     * @param ruleSetGroupName
     * @param startDate
     * @param endDate
     * @param start
     * @param length
     * @return
     */
    Map<String, Object> getHeaderList(@Nullable String ruleSetName,
                                      @Nullable String ruleSetGroupName,
                                      @Nullable String startDate,
                                      @Nullable String endDate,
                                      String start, String length);

    /** 获取规则集头信息列表  用于权限管理查询 */
    public Map<String, Object> getHeaderListResource(@Nullable String ruleSetName,
                                                   @Nullable String ruleSetGroupName,
                                                   @Nullable String startDate,
                                                   @Nullable String endDate,
                                                   String start, String size);

    Map<String, Object> getRuleSetGroupHeaderListResource(@Nullable String ruleSetGroupId,
                                                                 @Nullable String ruleSetGroupName,
                                                                 @Nullable String startDate,
                                                                 @Nullable String endDate,
                                                                 String start, String size);

    ResponseResult createRuleSetHeader(RuleSetHeader ruleSetHeader, String userId);

    ResponseResult updateRuleSetHeader(RuleSetHeader ruleSetHeader, String userId);

    boolean checkAnyVersionIsEnable(String ruleSetHeaderId);

    ResponseResult deleteRuleSetHeader(String ruleSetHeaderId);

    // ------------------------ 规则集版本管理 ------------------------

    boolean checkVersionIsExist(String version, String ruleSetHeaderId,
                                @Nullable String ruleSetId);

    RuleSetReferenceExt getRuleSetByRuleSetId(String ruleSetId);

    ResponseResult getRuleSetVersionList(String ruleSetHeaderId,
                                         @Nullable String enable,
                                         @Nullable String startDate,
                                         @Nullable String endDate);

    Map<String, Object> getRuleSetVersionList(String ruleSetHeaderId,
                                              @Nullable String enable,
                                              @Nullable String startDate,
                                              @Nullable String endDate,
                                              String start, String length);

    ResponseResult createRuleSet(RuleSet ruleSet, String userId);

    ResponseResult updateRuleSet(RuleSet ruleSet, String oldRuleContent, String userId);

    boolean checkRuleSetEnable(String ruleSetId);

    boolean checkRuleSetUsed(String ruleSetId);

    ResponseResult enable(String ruleSetId, String userId);

    ResponseResult disable(String ruleSetId, String userId);

    ResponseResult deleteRuleSet(String ruleSetId);

    ResponseResult deleteRuleSetsByHeaderId(String ruleSetHeaderId);

    /**
     * 发布模型中的规则集到规则库中
     * <p>
     * 发布可以选择新建规则集或已存在的规则集并新建版本，
     * 最终保存模型对发布后的规则集的引用。
     * <p>
     * 如果{@link RuleSetHeader#}为空则新建规则集
     *
     * @param ruleSetHeader
     * @param ruleSet
     * @param ruleId
     * @param folderId
     * @param userId
     * @return
     */
    ResponseResult publish(RuleSetHeader ruleSetHeader,
                           RuleSet ruleSet, String ruleId,
                           final String folderId, String userId);

    // ------------------------ 规则集组管理 ------------------------

    ResponseResult getRuleSetGroups(String ruleSetGroupName);

    List<String> getKpiByRuleSetId(String ruleSetId);

    List<String> getModelByRuleSetId(String ruleSetId);

    Map<String, Object> getRuleSetGroupsPaged(String ruleSetGroupName, String start, String length);

    boolean checkGroupNameIsExist(String ruleSetGroupName, @Nullable String ruleSetGroupId);

    ResponseResult createRuleSetGroup(RuleSetGroup ruleSetGroup, String userId);

    ResponseResult updateRuleSetGroup(RuleSetGroup ruleSetGroup, String userId);

    boolean isGroupUsed(String ruleSetGroupId);

    ResponseResult deleteRuleSetGroup(String ruleSetGroupId);
    /**
     * 根據ruleSetHeader获取版本信息
     * @param ruleSetHeaderId id
     * @return 键值对信息的list
     */
    List<Map<String, String>> getRuleSetIdByHeader(String ruleSetHeaderId);
}
