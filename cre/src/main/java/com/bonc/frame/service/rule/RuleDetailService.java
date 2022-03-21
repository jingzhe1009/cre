package com.bonc.frame.service.rule;

import com.bonc.frame.entity.model.ModelContentInfo;
import com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.ImportAdjustObject;
import com.bonc.frame.entity.rule.RuleDetail;
import com.bonc.frame.entity.rule.RuleDetailHeader;
import com.bonc.frame.entity.rule.RuleDetailWithBLOBs;
import com.bonc.frame.entity.variable.reference.VariableRule;
import com.bonc.frame.util.ResponseResult;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * 规则详情相关操作service
 *
 * @author qxl
 * @version 1.0
 * @date 2018年3月27日 下午4:36:01
 */
public interface RuleDetailService {

    /**
     * 对传入的模型版本,通过版本进行排序,
     * 返回 ruleName:排序后的模型版本
     *
     * @return ruleName:排序后的模型版本
     */
    Map<String, List<RuleDetail>> sortRuleDetailByVersion(List<RuleDetail> needSortRuleDetailList);

    /**
     * 通过模型内容获取
     * 返回的对象中,包括参数Id,指标Id,接口Id
     *
     * @param model
     * @return
     */
    ModelContentInfo getModelContentInfoExcudeRange(String model);

    /**
     * 对模型内容进行修改
     *
     * @return 修改后的模型内容
     * @idType 目前有三种  kpi   variable   api
     */
    String updateRuleContentRefId(String ruleContent, String idType, String oldId, String newId);

    /**
     * 对模型内容进行修改
     *
     * @return 修改后的模型内容
     * @idType 目前有三种  kpi   variable   api
     */
    String updateRuleContentRefIdAndName(String ruleContent, ImportAdjustObject adjustObject);

    /**
     * 获取整个系统中所有的模型
     *
     * @return
     */
    List<RuleDetailWithBLOBs> getVersionWithBLOBSListSystemAll();

    /**
     * 获取规则内容
     *
     * @param ruleId
     * @return
     */
    ResponseResult getRuleContent(String ruleId);

    /**
     * 插入新规则
     *
     * @param rule
     * @param data
     * @return
     */
    ResponseResult insertRule(RuleDetail rule, String data, String currentUser);

    void insertRuleToOfficialDataPersistence(RuleDetailWithBLOBs rule, String operateType);

    /**
     * 修改规则内容
     *
     * @param rule
     * @param data
     * @return
     */
    ResponseResult updateRule(RuleDetail rule, String data);

    /**
     * 修改规则状态
     *
     * @param ruleId
     * @param status
     * @return
     */
    ResponseResult updateRuleStatus(String folderId, String ruleId, String status, String platformUpdateUserJobNumber);

    /**
     * 删除规则
     *
     * @param ruleId
     * @return
     */
    ResponseResult deleteRule(String ruleId, String platformUpdateUserJobNumber);

    ResponseResult deleteRuleByName(String ruleName, String platformUpdateUserJobNumber);

    /**
     * 获取规则列表，不包含规则内容,最新版本
     *
     * @param folderId
     * @return
     */
    List<RuleDetail> getRuleHeaderListByFolderId(String folderId);

    List<RuleDetailWithBLOBs> getRuleDetailWhthBOLOBListByFolderId(String folderId);

    ResponseResult cloneRuleWithFolder(RuleDetail cloneHeaderInfo, String loginUserId) throws Exception;

    Map<String, Object> pagedByRuleNameFolderNameRuleType(String moduleName, String ruleType, String folderName,
                                                          String start, String size);

    Map<String, Object> pagedBySubjectidAuthRuleNameFolderNameRuleType(List<String> subjects, String moduleName, String ruleType, String folderName,
                                                                       String start, String size);

    /**
     * 获取规则详情
     *
     * @param ruleId
     * @return
     */
    RuleDetailWithBLOBs getRule(String ruleId);

    RuleDetailWithBLOBs getRuleFromOfficial(String ruleId);

    RuleDetailWithBLOBs getRuleFromDraft(String ruleId);

    List<RuleDetailWithBLOBs> getRuleDetailWithBLOBsByProperty(String ruleName, String ruleId, String version, boolean needDraft);

    RuleDetail getOne(String ruleId);

    List<RuleDetailWithBLOBs> getRuleBlobList(String folderId);

    List<Map<String, Object>> getRuleNameList(String folderId);

    List<Map<String, Object>> getRuleNameInHeaderList(String folderId);

    List<Map<String, Object>> getEnableVersionBaseInfoByRuleName(String ruleName);

    // ------------------------------------------- AB test ------------------------------------------------

    List<Map<String, Object>> getModelByProduct(String moduleName, String productCode,
                                                String productName, String folderId, String ruleType);

    List<Map<String, Object>> getVersionListByStatus(String ruleName, String ruleStatus);

    /**
     * 是否可以修改规则.true-是,false-否
     *
     * @param ruleId
     * @return
     */
    boolean canModify(String ruleId);

    /**
     * 检查是否可以修改。
     * 即要修改的api、参数、函数等是否在已经启用的规则中。
     *
     * @param id
     * @param folderId
     * @return
     */
    ResponseResult checkCanModify(String id, String folderId);

    ResponseResult updateRuleCache(String folderId, String ruleId);

    /**
     * 通过模型内容,添加模型的引用
     */
    void insertModelReference(String modelContent, String ruleId);

    // ------------------------------ 引用参数 ------------------------------

    // 模型在分支上添加参数
    ResponseResult addVariableOnFork(VariableRule variableRule);

    // 模型在规则集上添加参数
    ResponseResult addVariableOnRuleSet(VariableRule variableRule);

    // 模型更新参数
    ResponseResult updateVariable(String ruleId, String variableId, String oldVariableId);

    // 模型删除参数
    ResponseResult deleteVariable(String ruleId, String variableId);

    // ------------------------------ 模型库 头信息管理 ------------------------------

    ResponseResult getOneHeaderInfo(String ruleName);

    /**
     * modelName为直接匹配
     *
     * @param moduleName
     * @return
     */
    public List<RuleDetailHeader> getModelHeaderListByProperty(@Nullable String ruleName, String moduleName,
                                                               @Nullable String isPublic,
                                                               @Nullable String folderId,
                                                               @Nullable String modelGroupId);

    /**
     * modelName为模糊匹配
     *
     * @param moduleName
     * @return
     */
    List<RuleDetailHeader> getHeaderList(@Nullable String ruleName, @Nullable String moduleName,
                                         @Nullable String ruleType,
                                         @Nullable String isPublic,
                                         @Nullable String folderId,
                                         @Nullable String modelGroupId,
                                         @Nullable String modelGroupName,
                                         @Nullable String deptId,
                                         @Nullable String deptName,
                                         @Nullable String partnerId,
                                         @Nullable String partnerName,
                                         @Nullable String productCode,
                                         @Nullable String productName,
                                         @Nullable String systemCode,
                                         @Nullable String systemName,
                                         @Nullable String platformCreateUserJobNumber,
                                         @Nullable String platformUpdateUserJobNumber,
                                         @Nullable String startDate,
                                         @Nullable String endDate);

    Map<String, Object> getHeaderList(@Nullable String ruleName, @Nullable String moduleName,
                                      @Nullable String ruleType,
                                      @Nullable String isPublic,
                                      @Nullable String folderId,
                                      @Nullable String modelGroupId,
                                      @Nullable String modelGroupName,
                                      @Nullable String deptId,
                                      @Nullable String deptName,
                                      @Nullable String partnerId,
                                      @Nullable String partnerName,
                                      @Nullable String productCode,
                                      @Nullable String productName,
                                      @Nullable String systemCode,
                                      @Nullable String systemName,
                                      @Nullable String platformCreateUserJobNumber,
                                      @Nullable String platformUpdateUserJobNumber,
                                      @Nullable String startDate,
                                      @Nullable String endDate,
                                      String start, String length);

    List<RuleDetailHeader> getHeaderListByModelGroupId(String modelGroupId);


    /**
     * 获取头信息列表 用于权限管理
     */
    Map<String, Object> getHeaderListResource(@Nullable String ruleName,
                                              @Nullable String ruleType,
                                              @Nullable String modelGroupName,
                                              @Nullable String startDate,
                                              @Nullable String endDate,
                                              String start, String length);

    /**
     * 创建模型的同时创建新版本
     */
    ResponseResult createHeaderAndVersion(RuleDetailWithBLOBs ruleDetailWithBLOBs, String userId);


    /**
     * 创建模型头
     */
    RuleDetailWithBLOBs createHeader(RuleDetailWithBLOBs ruleDetailWithBLOBs, String userId);


    /**
     * 修改头信息
     *
     * @param ruleDetailHeader
     * @param oldRuleName
     * @param userId
     * @return
     */
    ResponseResult updateHeader(RuleDetailHeader ruleDetailHeader, String oldRuleName, String oldModuleName, String userId);

    ResponseResult deleteHeader(String ruleName);

    // ------------------------ 模型库 版本管理 ------------------------

    boolean checkAnyVersionIsEnable(String ruleName);

    /**
     * 用于确保版本号在一模型中的唯一性
     *
     * @param version
     * @param ruleName
     * @param ruleId
     * @return
     */
    boolean checkVersionIsExist(String version, String ruleName,
                                @Nullable String ruleId);

    // 检查当前版本的规则集是否为启用状态
    boolean checkEnable(String ruleId);

    ResponseResult getVersionList(String ruleName,
                                  @Nullable String ruleStatus,
                                  @Nullable String isLog,
                                  @Nullable String startDate,
                                  @Nullable String endDate);

    Map<String, Object> getVersionList(String ruleName,
                                       @Nullable String ruleStatus,
                                       @Nullable String isLog,
                                       @Nullable String startDate,
                                       @Nullable String endDate,
                                       String start, String length);

    List<RuleDetailWithBLOBs> getVersionWithBLOBSList(String ruleName);

    ResponseResult createVersion(String ruleName, String version,
                                 @Nullable String versionDesc,
                                 String data, String currentUser);

    /**
     * 发布模型
     * 将私有模型发布到模型库中
     * <p>
     * 选择在模型库中新建模型或选择已存在的模型，
     * 生成新版本
     *
     * @param headerInfo  在模型库中新建模型或选择已存在的模型
     * @param oldFolderId 当前模型所在场景id
     * @param ruleId      当前模型id
     * @param version     新建版本的版本号
     * @param versionDesc 新建版本的版本描述
     * @param userId      当前用户id
     */
    ResponseResult publish(RuleDetailHeader headerInfo,
                           String oldFolderId,
                           String ruleId, String version,
                           @Nullable String versionDesc,
                           String userId);

    ResponseResult stageWithVersion(RuleDetail ruleDetail, String data,
                                    String userId) throws Exception;

    Map stageOrCommitFirst(RuleDetail ruleDetail, String data,
                           String userId, boolean isNoraml) throws Exception;


    ResponseResult commitWithVersion(RuleDetail ruleDetail, String data,
                                     String userId) throws Exception;

    @Deprecated
    ResponseResult publishWithVersion(RuleDetail ruleDetail, String data,
                                      String userId);

    ResponseResult getVersions(String ruleName, String folderId,
                               String isPublic);

    ResponseResult getAllVersions(String ruleName, String folderId,
                                  @Nullable String isPublic, String needDraft);

    Map<String, Object> getVersionsForPageList(String ruleName, String ruleStatus,
                                               @Nullable String startDate,
                                               @Nullable String endDate,
                                               String isPublic, String start, String length);

    ResponseResult changeStatus(String ruleId, String platformUpdateUserJobNumber);

    ResponseResult checkPreVersionStatus(String ruleId);

    String generateVersion(String ruleName, boolean isNormal, boolean isImport);

    boolean checkModuleNameIsExist(String moduleName, String ruleId);

    ResponseResult getVersionListWithDraftByStatus(String ruleName);

}
