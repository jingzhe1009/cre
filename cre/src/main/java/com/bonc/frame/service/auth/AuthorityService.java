package com.bonc.frame.service.auth;

import com.bonc.frame.entity.auth.Authority;
import com.bonc.frame.entity.auth.GrantAuthRequest;
import com.bonc.frame.entity.auth.resource.DataResource;
import com.bonc.frame.security.ResourceType;
import com.bonc.frame.util.ResponseResult;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 授权
 *
 * @author yedunyao
 * @date 2019/8/14 14:20
 */
public interface AuthorityService {
    //菜单资源
    Map<String, Object> getMenuResourcesAndPermits(String roleId);
    //模型资源
    Map<String, Object> getModelResourcesAndPermits(String roleId, String ruleName, String ruleType,
                                                    String folderName, String start, String size);

    //标签资源
    Map<String, Object> getMetaResourcesAndPermits(String roleId, String tableName, String dbAlias,
                                                   String folderName, String start, String size);
    //数据源
    Map<String, Object> getDataSourceResourcesAndPermits(String roleId,
                                                         String dbAlias,
                                                         String dbIp,
                                                         String dbType,
                                                         String start, String length);
    // 公共参数
    Map<String, Object> getPubVariableResourcesAndPermits(String roleId,
                                                          String variableAlias,
                                                          String variableGroupName,
                                                          String variableTypeId,
                                                          String startDate, String endDate,
                                                          String start, String length);
    // 参数组
    Map<String, Object> getPubVariableGroupsResourcesAndPermits(String roleId,String variableGroupId,
                                                                String  variableGroupName, Date createDate, Date updateDate,
                                                                String start, String length);
    // 公共接口
    Map<String, Object> getPubApiResourcesAndPermits(String roleId,
                                                     String apiName, String apiGroupName,
                                                     String startDate, String endDate,
                                                     String start, String length);
    // 接口组
    Map<String, Object> getPubApiGroupsResourcesAndPermits(String roleId,
                                                     String  apiGroupId, String apiGroupName,
                                                     String startDate, String endDate,
                                                     String start, String length);
    // 规则库
    Map<String, Object> getPubRuleSetResourcesAndPermits(String roleId,
                                                         String ruleSetName, String ruleSetGroupName,
                                                         String startDate, String endDate,
                                                         String start, String length);
    // 规则集组
    Map<String, Object> getPubRuleSetGroupsResourcesAndPermits(String roleId,
                                                         String ruleSetGroupId, String ruleSetGroupName,
                                                         String startDate, String endDate,
                                                         String start, String length);
    // 模型库
    Map<String, Object> getPubModelBasesResourcesAndPermits(String roleId ,
                                                            String moduleName, String ruleType,String modelGroupName,
                                                            String startDate, String endDate,
                                                            String start, String length);

    // 产品
    Map<String, Object> getPubModelGroupsResourcesAndPermits(String roleId ,
                                                            String moduleGroupId, String ruleType,String modelGroupName,
                                                            String startDate, String endDate,
                                                            String start, String length);
    /**
     * 离线任务
     * @param roleId
     * @param taskName
     * @param packageName
     * @param ruleName
     * @param ruleVersion
     * @param start
     * @param length
     * @return
     */
    //任务
    Map<String, Object> getTaskResourcesAndPermits(String roleId,
                                                   String taskName, String packageName,
                                                   String ruleName, String ruleVersion,
                                                   String start, String length);
    //指标
    Map<String, Object> getKpiResourcesAndPermits(String roleId,
                                                  @Nullable String kpiName,
                                                  @Nullable String kpiGroupName,
                                                  @Nullable String kpiType,
                                                  @Nullable String fetchType,
                                                  String start, String length);
    //指标组
    Map<String, Object> getKpiGroupResourcesAndPermits(String roleId,
                                                  @Nullable String kpiGroupName,
                                                  String startDate, String endDate,
                                                  String start, String length);
    //场景
    Map<String, Object> getFolderResourcesAndPermits(String roleId, String folderName,
                                                     String start, String length);

    // 当前用户是否拥有全部权限
    boolean isCurrentUserHasAllPermits();
    // 用户是否拥有全部权限
    boolean isUserHasAllPermits(String userId);

   // 角色是否拥有全部权限
    boolean isRoleHasAllPermits(String roleId);

    // 主体是否拥有全部权限
    boolean hasAllPermits(String subjectId);

    // 主体是否拥有某一类资源的全部权限（包括全部资源全部权限的判断）
    boolean hasAllPermits(String subjectId, String resourceTypeId);

    // 主体是否拥有某一类资源的全部权限
    boolean hasAllPermitsByResourceType(String subjectId, String resourceTypeId);

    // 主体列表是否拥有全部权限
    boolean hasAllPermitsBySubjects(List<String> subjectIds);

    // 主体列表是否拥有某一类资源的全部权限（包括全部资源全部权限的判断）
    boolean hasAllPermitsBySubjects(List<String> subjectIds, String resourceTypeId);

    // 主体列表是否拥有某一类资源的全部权限
    boolean hasAllPermitsBySubjectsResourceType(List<String> subjectIds, String resourceTypeId);


    // 主体列表是否对某一类资源的全部数据拥有某一操作类型权限
    boolean hasAllPermitsBySubjectsResourceTypeExpression(List<String> subjectIds,
                                                          String resourceTypeId,
                                                          String resourceExpression);

    // 获取对全部数据在操作级所拥有的权限，如全部模型可查看，不包括全部模型拥有全部操作权限（”*“号）
    List<Authority> getOperatePermitsWithoutAllByResourceType(String subjectId, String resourceTypeId);

    // 获取多个主体 对全部数据在操作级所拥有的权限，如全部模型可查看，不包括全部模型拥有全部操作权限（”*“号）
    List<Authority> getSubjectsOperatePermitsWithoutAllByResourceType(List<String> subjectId, String resourceTypeId);

    // 获取对全部数据在操作级所拥有的权限，如全部模型可查看、全部模型拥有全部操作权限
    List<Authority> getOperatePermitsByResourceType(String subjectId, String resourceTypeId);

    // 如果具有全部权限，则该类型资源拥有全部权限
    List<Authority> getOperatePermitsByResourceTypeAndAllPermits(String subjectId, String resourceTypeId);

    // 获取指定主体id和资源类型的权限
    List<Authority> getPermitsBySubjectResourceType(String subjectId, String resourceTypeId);

    // 获取指定主体id、资源id和资源类型的权限
    Authority getPermitBySubjectResourceId(String subjectId, String resourceId, String resourceTypeId);

    // 根据主体id、数据列表获取各个数据具有的权限
    List<Authority> getPermitsBySubjectDataResources(String subjectId, String resourceTypeId,
                                                     List<? extends DataResource> dataResources);

    // 根据多个主体id、数据列表获取各个数据具有的权限
    List<Authority> getPermitsBySubjectsDataResources(List<String> subjectIds, String resourceTypeId,
                                                             List<? extends DataResource> dataResources);

    // 获取指定主体id、资源ids和资源类型的权限
    List<Authority> getPermitsBySubjectResourceIds(String subjectId, List<String> resourceIds,
                                                   String resourceTypeId);

    // 获取指定的多个主体id、资源ids和资源类型的权限
    List<Authority> getPermitsBySubjectsResourceIds(List<String> subjectIds, List<String> resourceIds,
                                                          String resourceTypeId);

    // 获取多个主体对指定资源的去重后的权限集合
    Set<String> getDistinctPermitsBySubjectIdsResourceId(List<String> subjectIds, String resourceId,
                                                         String resourceTypeId);

    // 获取多个主体对指定资源的全部权限表达式
    List<String> getAllPermitsBySubjectIdsResourceId(List<String> subjectIds, String resourceId,
                                                     String resourceTypeId);

    // 获取有权限的资源id列表
    List<String> getAllResourceIdsBySubjectIdsResourceType(List<String> subjectIds,
                                                           String resourceTypeId);

    // 添加权限
    ResponseResult insert(Authority authority, String currentUser,
                          String roleId, String resourceTypeId);

    // 批量保存权限
    ResponseResult insertBatch(List<Authority> authorities, String currentUser,
                               String roleId, String resourceTypeId);

    ResponseResult upsertBatch(List<Authority> list, String currentUser,
                               String subjectId, String resourceTypeId);

    ResponseResult updateBatch(List<Authority> authorities, String currentUser,
                               String subjectId, String resourceTypeId);

    // 授予全部资源全部权限
    ResponseResult grantAllPermits(String roleId, String currentUser);

    // 取消全部资源全部权限，但仍对部分资源拥有权限
    ResponseResult cancleAllPermits(String roleId);

    // 删除全部资源的全部权限
    ResponseResult deleteAllPermits(String roleId);

    void autoGrantAuthToCurrentUser(String resourceId, ResourceType resourceType);

    void grantToUser(Authority authority, String resourceTypeId, String currentUser);

    // 授权
    ResponseResult grant(List<Authority> authorities, String currentUser,
                         String roleId, String resourceTypeId);

    ResponseResult grant(GrantAuthRequest grantAuthRequest, String currentUser, String resourceTypeId);

    void updatePubVariableAuthList(GrantAuthRequest grantAuthRequest);

//    void updatePubVariableGroupsAuthList(GrantAuthRequest grantAuthRequest);

    /**
     * 授权
     *
     * @param needDelAuthorities    需要删除权限的资源id列表
     * @param needInsertAuthorities 需要新增的权限列表
     * @param needUpdateAuthorities 需要修改的权限列表
     * @param currentUser           当前用户
     * @param roleId                授权角色
     * @param resourceTypeId        资源类型
     * @return
     */
    ResponseResult grant(List<String> needDelAuthorities,
                         List<Authority> needInsertAuthorities,
                         List<Authority> needUpdateAuthorities,
                         String currentUser,
                         String roleId,
                         String resourceTypeId , String isAllAuth);

    ResponseResult grantMenuAndButton(List<Authority> authorities, String currentUser,
                                      String roleId , String isAllAuth);

    ResponseResult deleteByPrimaryKey(String id);

    ResponseResult deleteBySubjectId(String subjectId);

    ResponseResult deleteBySubjectResourceType(String subjectId, String resourceTypeId);

    ResponseResult deleteBySubjectResourceId(String subjectId, String resourceId, String resourceTypeId);

    ResponseResult deleteBySubjectResources(String subjectId,
                                            List<String> resourceIds,
                                            String resourceTypeId);

    ResponseResult deleteByResourceId(String resourceId, String resourceTypeId);

    ResponseResult updateRuleIdByOldResourceId(String oldResourceId, String resourceTypeId,String newResourceId) ;

    ResponseResult deleteByResourceIds(List<String> resourceIds, String resourceTypeId);



    /** 赋予所有资源的所有权限 */
    ResponseResult addAllAuthByResourceIDAndType(String subjectId,String currentUser);





}
