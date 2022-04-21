package com.bonc.frame.service.impl.auth;

import com.bonc.frame.dao.DaoHelper;
import com.bonc.frame.entity.auth.Authority;
import com.bonc.frame.entity.auth.GrantAuthRequest;
import com.bonc.frame.entity.auth.resource.*;
import com.bonc.frame.entity.kpi.KpiGroup;
import com.bonc.frame.security.ResourceType;
import com.bonc.frame.security.SubjectType;
import com.bonc.frame.service.api.ApiService;
import com.bonc.frame.service.auth.AuthorityService;
import com.bonc.frame.service.auth.ResourceService;
import com.bonc.frame.service.auth.SubjectService;
import com.bonc.frame.service.datasource.DataSourceService;
import com.bonc.frame.service.kpi.KpiService;
import com.bonc.frame.service.metadata.MetaDataMgrService;
import com.bonc.frame.service.rule.RuleDetailService;
import com.bonc.frame.service.rule.RuleService;
import com.bonc.frame.service.ruleSetBase.RuleSetBaseService;
import com.bonc.frame.service.task.RuleTaskService;
import com.bonc.frame.service.variable.VariableService;
import com.bonc.frame.util.CollectionUtil;
import com.bonc.frame.util.ControllerUtil;
import com.bonc.frame.util.IdUtil;
import com.bonc.frame.util.ResponseResult;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import java.util.*;

/**
 * @author yedunyao
 * @date 2019/8/14 14:20
 */
@Service
public class AuthorityServiceImpl implements AuthorityService {
    //权限访问日志
    private Log log = LogFactory.getLog(getClass());
    //持久层
    @Autowired
    private DaoHelper daoHelper;
    //主体业务
    @Autowired
    private SubjectService subjectService;
    //资源池业务
    @Autowired
    private ResourceService resourceService;
    //规则详情业务
    @Autowired
    private RuleDetailService ruleDetailService;
    //元数据管理
    @Autowired
    private MetaDataMgrService metaDataMgrService;
    //公共参数管理
    @Autowired
    private VariableService variableService;
    //接口业务
    @Autowired
    private ApiService apiService;
    //规则集基础业务   规则头信息  规则集组  规则版本
    @Autowired
    private RuleSetBaseService ruleSetBaseService;
    //数据源业务
    @Autowired
    private DataSourceService dataSourceService;
    //规则离线任务
    @Autowired
    private RuleTaskService ruleTaskService;
    //指标业务
    @Autowired
    private KpiService kpiService;
    //规则业务
    @Autowired
    private RuleService ruleService;
    //权限数据表
    private static final String _MYBITSID_PREFIX = "com.bonc.frame.mapper.auth.AuthorityMapper.";


   /**
    * 菜单权限
    *
    *
    * */

    //获取带权限的菜单列表(通过角色)
    @Override
    public Map<String, Object> getMenuResourcesAndPermits(String roleId) {
        // 获取主体
        final String subjectIdRole = subjectService.selectByRoleId(roleId);

        //获取当前用户的主体
        final List<String> subjectIdUser = subjectService.selectSubjectsByCurrentUser();

        // 获取所有菜单资源
        final List<Resource> menuResources = resourceService.getMenuResources();
//        log.debug(" ------------------- -menuResources : " + menuResources);

        //如果用户具有全部的权限  则全部显示
        if (hasAllPermitsBySubjects(subjectIdUser, ResourceType.MENU.getType()) &&
                hasAllPermitsBySubjects(subjectIdUser, ResourceType.BUTTON.getType())) {
            allResourcesDisplay(menuResources);
        }

        // 获取主体具有的菜单资源权限     单个或者多个权限
        if (hasAllPermits(subjectIdRole, ResourceType.MENU.getType()) &&
                hasAllPermits(subjectIdRole, ResourceType.BUTTON.getType())) {
            allResourcesChoosed(menuResources);
        }

        {   //主体表 将当前用户的主体 角色全部放入到列表中
            List<String> subjectsAll = new ArrayList<>();
            subjectsAll.addAll(subjectIdUser);
            subjectsAll.add(subjectIdRole);

            //通过主体资源id获取不同的权限    将持有权限全部放入到表中
            Set<String> holdingMenuPermits = getDistinctPermitsBySubjectIdsResourceId(
                    ImmutableList.<String>of(subjectIdRole), null, ResourceType.MENU.getType());
            Set<String> holdingButtonPermits = getDistinctPermitsBySubjectIdsResourceId(
                    ImmutableList.<String>of(subjectIdRole), null, ResourceType.BUTTON.getType());
            holdingMenuPermits.addAll(holdingButtonPermits);


            if (!holdingMenuPermits.isEmpty()) {
                // 构造显示树
                for (Resource menu : menuResources) {
                    menu.setDisplay(true);
                    for (String permit : holdingMenuPermits) {
                        if (permit != null) {
                            if ("*".equals(permit) || permit.equals(menu.getResourceUrl())) {
                                menu.setChoosed(true);
                            }
                        }
                    }
                }
            }
        }


        final List<Resource> resources = Resource.parse2Tree(menuResources);
        //通过资源类型和许可获取操作权限    主体角色
        List<Authority> menuOperatePermits = getOperatePermitsByResourceTypeAndAllPermits(
                subjectIdRole, ResourceType.MENU.getType());
        final List<Authority> buttonOperatePermits = getOperatePermitsByResourceTypeAndAllPermits(
                subjectIdRole, ResourceType.BUTTON.getType());
        final LinkedList<Object> operatePermits = new LinkedList<>();
        //菜单权限和按钮权限全部授权操作
        operatePermits.addAll(menuOperatePermits);
        operatePermits.addAll(buttonOperatePermits);

        Map<String, Object> dataArr = ImmutableMap.<String, Object>of(
                "data", resources
        );
        Map<String, Object> result = ImmutableMap.<String, Object>of(
                "operatePermits", operatePermits,
                "permits", dataArr
        );
        return result;
    }

    // 分页获取带权限的模型列表
    @Override
    public Map<String, Object> getModelResourcesAndPermits(String roleId, String ruleName, String ruleType,
                                                           String folderName, String start, String length) {
        //获取当前用户的主体
        final List<String> subjectIdUser = subjectService.selectSubjectsByCurrentUser();

        // 获取需要赋予权限的主体
        final String subjectIdRole = subjectService.selectByRoleId(roleId);

        // 分页获取模型
        final Map<String, Object> pagedModles = ruleDetailService.pagedByRuleNameFolderNameRuleType(
                ruleName, ruleType, folderName, start, length);

        List<RuleResource> ruleResources = (List<RuleResource>) pagedModles.get("data");

        // 为每条数据添加带有权限的操作
//        dataResourcesPermits(ruleResources, subjectIdRole, ResourceType.DATA_MODEL.getType());
        List<Resource> displayHeadPermits = dataResourcesPermits(ruleResources, subjectIdUser, subjectIdRole, ResourceType.DATA_MODEL.getType());

        Map<String, Object> result = ImmutableMap.<String, Object>of(
                "operatePermits", getOperatePermitsByResourceTypeAndAllPermits(
                        subjectIdRole, ResourceType.DATA_MODEL.getType()),
                "permits", pagedModles,
                "displayHeadPermits", displayHeadPermits
        );

        return result;
    }

    // 获取带权限的元数据列表
    @Override
    public Map<String, Object> getMetaResourcesAndPermits(String roleId, String tableName, String dbAlias,
                                                          String folderName, String start, String length) {

        //获取当前用户的主体
        final List<String> subjectIdUser = subjectService.selectSubjectsByCurrentUser();
        // 获取主体
        final String subjectId = subjectService.selectByRoleId(roleId);

        // 分页获取元数据
        final Map<String, Object> pagedTables = metaDataMgrService.pagedMetaTablesByDsNameTableNamePackageName(
                tableName, dbAlias, folderName, start, length);
        List<MetadataResource> metadataResources = (List<MetadataResource>) pagedTables.get("data");

        List<Resource> displayHeadPermits = dataResourcesPermits(metadataResources, subjectIdUser, subjectId, ResourceType.DATA_METADATA.getType());

        Map<String, Object> result = ImmutableMap.<String, Object>of(
                "operatePermits", getOperatePermitsByResourceTypeAndAllPermits(
                        subjectId, ResourceType.DATA_METADATA.getType()),
                "permits", pagedTables,
                "displayHeadPermits", displayHeadPermits
        );


        return result;
    }

    /**
     * 数据源
     */
    //获取带权限的数据源列表
    @Override
    public Map<String, Object> getDataSourceResourcesAndPermits(String roleId,
                                                                String dbAlias,
                                                                String dbIp,
                                                                String dbType,
                                                                String start, String length) {
        //获取当前用户的主体
        final List<String> subjectIdUser = subjectService.selectSubjectsByCurrentUser();
        // 获取主体
        final String subjectId = subjectService.selectByRoleId(roleId);

        // 分页获取元数据
        final Map<String, Object> pagedDataSources = dataSourceService.pagedDataSource(dbAlias, dbIp,
                dbType, start, length);

        List<DataSourceResource> resources = (List<DataSourceResource>) pagedDataSources.get("data");
        List<Resource> displayHeadPermits = dataResourcesPermits(resources, subjectIdUser, subjectId, ResourceType.DATA_DATASOURCE.getType());
        Map<String, Object> result = ImmutableMap.<String, Object>of(
                "operatePermits", getOperatePermitsByResourceTypeAndAllPermits(
                        subjectId, ResourceType.DATA_DATASOURCE.getType()),
                "permits", pagedDataSources,
                "displayHeadPermits", displayHeadPermits
        );
        return result;
    }

    // 获取带权限的公共参数列表
    @Override
    public Map<String, Object> getPubVariableResourcesAndPermits(String roleId,
                                                                 String variableAlias,
                                                                 String variableGroupName,
                                                                 String variableTypeId,
                                                                 String startDate, String endDate,
                                                                 String start, String length) {
        //获取当前用户的主体
        final List<String> subjectIdUser = subjectService.selectSubjectsByCurrentUser();
        // 获取主体
        final String subjectIdRoleId = subjectService.selectByRoleId(roleId);

        // 分页获取元数据
        final Map<String, Object> pagedVaraibles = variableService.pagedPubVariableResources(variableAlias, variableGroupName,
                variableTypeId, startDate, endDate, start, length);

        List<PubVariableResource> resources = (List<PubVariableResource>) pagedVaraibles.get("data");


        List<Resource> displayHeadPermits = dataResourcesPermits(resources, subjectIdUser, subjectIdRoleId,
                ResourceType.DATA_PUB_VARIABLE.getType());

        Map<String, Object> result = ImmutableMap.<String, Object>of(
                "operatePermits", getOperatePermitsByResourceTypeAndAllPermits(
                        subjectIdRoleId, ResourceType.DATA_PUB_VARIABLE.getType()),
                "permits", pagedVaraibles,
                "displayHeadPermits", displayHeadPermits
        );

        return result;
    }

    // 获取带权限的参数组列表
    @Override
    public Map<String, Object> getPubVariableGroupsResourcesAndPermits(String roleId,
                                                                       @Nullable String variableGroupId,
                                                                       @Nullable String variableGroupName,
                                                                       @Nullable Date createDate,
                                                                       @Nullable Date updateDate,
                                                                       String start, String length) {
        //获取当前用户的主体
        final List<String> subjectIdUser = subjectService.selectSubjectsByCurrentUser();

        // 获取主体
        final String subjectIdRoleId = subjectService.selectByRoleId(roleId);

        // 分页获取元数据
        final Map<String, Object> pagedVariableGroups = variableService.pagedPubVariableGroupsResources( variableGroupName,
              variableGroupId, createDate, updateDate, start, length);

        List<PubVariableResource> resources = (List<PubVariableResource>) pagedVariableGroups.get("data");


        List<Resource> displayHeadPermits = dataResourcesPermits(resources, subjectIdUser, subjectIdRoleId,
                ResourceType.DATA_PUB_VARIABLE_GROUP.getType());

        Map<String, Object> result = ImmutableMap.<String, Object>of(
                "operatePermits", getOperatePermitsByResourceTypeAndAllPermits(
                        subjectIdRoleId, ResourceType.DATA_PUB_VARIABLE_GROUP.getType()),
                "permits", pagedVariableGroups,
                "displayHeadPermits", displayHeadPermits
        );

        return result;
    }

    // 获取带权限的公共接口列表
    @Override
    public Map<String, Object> getPubApiResourcesAndPermits(String roleId,
                                                            String apiName, String apiGroupName,
                                                            String startDate, String endDate,
                                                            String start, String length) {
        //获取当前用户的主体
        final List<String> subjectIdUser = subjectService.selectSubjectsByCurrentUser();
        // 获取主体
        final String subjectId = subjectService.selectByRoleId(roleId);

        // 分页获取数据
        final Map<String, Object> pagedApis = apiService.pagedPubApiResource(apiName, apiGroupName,
                startDate, endDate, start, length);

        List<PubApiResource> resources = (List<PubApiResource>) pagedApis.get("data");

        List<Resource> displayHeadPermits = dataResourcesPermits(resources, subjectIdUser, subjectId, ResourceType.DATA_PUB_API.getType());

        Map<String, Object> result = ImmutableMap.<String, Object>of(
                "operatePermits", getOperatePermitsByResourceTypeAndAllPermits(
                        subjectId, ResourceType.DATA_PUB_API.getType()),
                "permits", pagedApis,
                "displayHeadPermits", displayHeadPermits
        );

        return result;
    }

    // 获取带权限的公共接口组列表
    @Override
    public Map<String, Object> getPubApiGroupsResourcesAndPermits(String roleId,
                                                            String  apiGroupId, String apiGroupName,
                                                            String startDate, String endDate,
                                                            String start, String length) {
        //获取当前用户的主体
        final List<String> subjectIdUser = subjectService.selectSubjectsByCurrentUser();
        // 获取主体
        final String subjectId = subjectService.selectByRoleId(roleId);

        // 分页获取数据
        final Map<String, Object> pagedApiGroups = apiService.pagedPubApiGroupResource( apiGroupId, apiGroupName,
                startDate, endDate, start, length);

        List<PubApiGroupResource> resources = (List<PubApiGroupResource>) pagedApiGroups.get("data");

        List<Resource> displayHeadPermits = dataResourcesPermits(resources, subjectIdUser, subjectId, ResourceType.DATA_PUB_API_GROUP.getType());

        Map<String, Object> result = ImmutableMap.<String, Object>of(
                "operatePermits", getOperatePermitsByResourceTypeAndAllPermits(
                        subjectId, ResourceType.DATA_PUB_API_GROUP.getType()),
                "permits", pagedApiGroups,
                "displayHeadPermits", displayHeadPermits
        );

        return result;
    }

    //获取带权限的规则库列表
    @Override
    public Map<String, Object> getPubRuleSetResourcesAndPermits(String roleId,
                                                                String ruleSetName, String ruleSetGroupName,
                                                                String startDate, String endDate,
                                                                String start, String length) {
        //获取当前用户的主体
        final List<String> subjectIdUser = subjectService.selectSubjectsByCurrentUser();
        // 获取主体
        final String subjectId = subjectService.selectByRoleId(roleId);

        // 分页获取数据
        final Map<String, Object> pagedRuleSets = ruleSetBaseService.getHeaderListResource(ruleSetName, ruleSetGroupName,
                startDate, endDate, start, length);

        List<PubRuleSetResource> resources = (List<PubRuleSetResource>) pagedRuleSets.get("data");

        List<Resource> displayHeadPermits = dataResourcesPermits(resources, subjectIdUser, subjectId, ResourceType.DATA_PUB_RULE_SET.getType());

        Map<String, Object> result = ImmutableMap.<String, Object>of(
                "operatePermits", getOperatePermitsByResourceTypeAndAllPermits(
                        subjectId, ResourceType.DATA_PUB_API.getType()),
                "permits", pagedRuleSets,
                "displayHeadPermits", displayHeadPermits
        );
        return result;
    }

    //获取带权限的规则集组列表
    @Override
    public Map<String, Object> getPubRuleSetGroupsResourcesAndPermits(String roleId,
                                                                      String ruleSetGroupId, String ruleSetGroupName,
                                                                      String startDate, String endDate,
                                                                      String start, String length) {
        //获取当前用户的主体
        final List<String> subjectIdUser = subjectService.selectSubjectsByCurrentUser();
        // 获取主体
        final String subjectId = subjectService.selectByRoleId(roleId);

        // 分页获取数据
        final Map<String, Object> pagedRuleSetGroups = ruleSetBaseService.getRuleSetGroupHeaderListResource(ruleSetGroupId, ruleSetGroupName,
                startDate, endDate, start, length);

        List<PubRuleSetGroupResource> resources = (List<PubRuleSetGroupResource>) pagedRuleSetGroups.get("data");

        List<Resource> displayHeadPermits = dataResourcesPermits(resources, subjectIdUser, subjectId, ResourceType.DATA_PUB_RULE_SET_GROUP.getType());

        Map<String, Object> result = ImmutableMap.<String, Object>of(
                "operatePermits", getOperatePermitsByResourceTypeAndAllPermits(
                        subjectId, ResourceType.DATA_PUB_API_GROUP.getType()),
                "permits", pagedRuleSetGroups,
                "displayHeadPermits", displayHeadPermits
        );
        return result;
    }

    //获取带权限的模型库列表
    @Override
    public Map<String, Object> getPubModelBasesResourcesAndPermits(String roleId,
                                                                   String moduleName, String ruleType, String modelGroupName,
                                                                   String startDate, String endDate,
                                                                   String start, String length) {
        //获取当前用户的主体
        final List<String> subjectIdUser = subjectService.selectSubjectsByCurrentUser();
        // 获取主体
        final String subjectId = subjectService.selectByRoleId(roleId);

        // 分页获取数据
        final Map<String, Object> pagedModelBases = ruleDetailService.getHeaderListResource(moduleName, ruleType, modelGroupName,
                startDate, endDate, start, length);

        List<PubModelBaseResource> resources = (List<PubModelBaseResource>) pagedModelBases.get("data");

        List<Resource> displayHeadPermits = dataResourcesPermits(resources, subjectIdUser, subjectId, ResourceType.DATA_PUB_MODEL.getType());

        Map<String, Object> result = ImmutableMap.<String, Object>of(
                "operatePermits", getOperatePermitsByResourceTypeAndAllPermits(
                        subjectId, ResourceType.DATA_PUB_API.getType()),
                "permits", pagedModelBases,
                "displayHeadPermits", displayHeadPermits
        );
        return result;
    }

    //获取带权限的产品列表
    @Override
    public Map<String, Object> getPubModelGroupsResourcesAndPermits(String roleId,
                                                                   String moduleGroupId, String ruleType, String modelGroupName,
                                                                   String startDate, String endDate,
                                                                   String start, String length) {
        //获取当前用户的主体
        final List<String> subjectIdUser = subjectService.selectSubjectsByCurrentUser();
        // 获取主体
        final String subjectId = subjectService.selectByRoleId(roleId);

        // 分页获取数据
        final Map<String, Object> pagedModelGroupBases = ruleDetailService.getGroupHeaderListResource(moduleGroupId, ruleType, modelGroupName,
                startDate, endDate, start, length);

        List<PubModelBaseResource> resources = (List<PubModelBaseResource>) pagedModelGroupBases.get("data");

        List<Resource> displayHeadPermits = dataResourcesPermits(resources, subjectIdUser, subjectId, ResourceType.DATA_PUB_MODEL_GROUP.getType());

        Map<String, Object> result = ImmutableMap.<String, Object>of(
                "operatePermits", getOperatePermitsByResourceTypeAndAllPermits(
                        subjectId, ResourceType.DATA_PUB_API_GROUP.getType()),
                "permits", pagedModelGroupBases,
                "displayHeadPermits", displayHeadPermits
        );
        return result;
    }

    /**
     * 离线任务
     */
    //获取带权限的离线任务列表
    @Override
    public Map<String, Object> getTaskResourcesAndPermits(String roleId,
                                                          String taskName, String packageName,
                                                          String ruleName, String ruleVersion,
                                                          String start, String length) {
        //获取当前用户的主体
        final List<String> subjectIdUser = subjectService.selectSubjectsByCurrentUser();
        // 获取主体
        final String subjectId = subjectService.selectByRoleId(roleId);

        // 分页获取数据
        Map<String, Object> pagedRuleTaskResource = ruleTaskService.pagedRuleTaskResource(
                taskName, packageName,
                ruleName, ruleVersion,
                start, length
        );

        List<RuleTaskResource> resources = (List<RuleTaskResource>) pagedRuleTaskResource.get("data");
        List<Resource> displayHeadPermits = dataResourcesPermits(resources, subjectIdUser, subjectId,
                ResourceType.DATA_TASK.getType());
        Map<String, Object> result = ImmutableMap.<String, Object>of(
                "operatePermits", getOperatePermitsByResourceTypeAndAllPermits(
                        subjectId, ResourceType.DATA_TASK.getType()),
                "permits", pagedRuleTaskResource,
                "displayHeadPermits", displayHeadPermits
        );
        return result;
    }

    /**
     * 指标
     */
    //获取带权限的指标列表
    @Override
    public Map<String, Object> getKpiResourcesAndPermits(String roleId,
                                                         @Nullable String kpiName,
                                                         @Nullable String kpiGroupName,
                                                         @Nullable String kpiType,
                                                         @Nullable String fetchType,
                                                         String start, String length) {
        //获取当前用户的主体
        final List<String> subjectIdUser = subjectService.selectSubjectsByCurrentUser();
        // 获取主体
        final String subjectId = subjectService.selectByRoleId(roleId);

        // 分页获取数据
        Map<String, Object> pagedKpiResource = kpiService.pagedKpiBaseInfo(
                kpiName, kpiGroupName,
                kpiType, fetchType,
                start, length
        );

        List<KpiDefinitionResource> resources = (List<KpiDefinitionResource>) pagedKpiResource.get("data");
        List<Resource> displayHeadPermits = dataResourcesPermits(resources, subjectIdUser, subjectId,
                ResourceType.DATA_KPI.getType());
        Map<String, Object> result = ImmutableMap.<String, Object>of(
                "operatePermits", getOperatePermitsByResourceTypeAndAllPermits(
                        subjectId, ResourceType.DATA_KPI.getType()),
                "permits", pagedKpiResource,
                "displayHeadPermits", displayHeadPermits
        );
        return result;
    }

    /**
     * 指标组
     */
    //获取带权限的指标组列表
    @Override
    public Map<String, Object> getKpiGroupResourcesAndPermits(String roleId,
                                                         @Nullable String kpiGroupName,
                                                         String startDate, String endDate,
                                                         String start, String length) {
        //获取当前用户的主体
        final List<String> subjectIdUser = subjectService.selectSubjectsByCurrentUser();
        // 获取主体
        final String subjectId = subjectService.selectByRoleId(roleId);

        // 分页获取数据
        Map<String, Object> pagedKpiGroupResource = kpiService.pagedKpiGroupBaseInfo(kpiGroupName, startDate,endDate,start, length);

        List<KpiGroupDefinitionResource> resources = (List<KpiGroupDefinitionResource>) pagedKpiGroupResource.get("data");
        List<Resource> displayHeadPermits = dataResourcesPermits(resources, subjectIdUser, subjectId,
                ResourceType.DATA_KPI_GROUP.getType());
        Map<String, Object> result = ImmutableMap.<String, Object>of(
                "operatePermits", getOperatePermitsByResourceTypeAndAllPermits(
                        subjectId, ResourceType.DATA_KPI_GROUP.getType()),
                "permits", pagedKpiGroupResource,
                "displayHeadPermits", displayHeadPermits
        );
        return result;
    }

    /**
     * 场景
     */
    //获取带权限的场景列表
    @Override
    public Map<String, Object> getFolderResourcesAndPermits(String roleId, String folderName,
                                                            String start, String length) {
        //获取当前用户的主体
        final List<String> subjectIdUser = subjectService.selectSubjectsByCurrentUser();
        // 获取主体
        final String subjectId = subjectService.selectByRoleId(roleId);

        // 分页获取数据
        Map<String, Object> pagedFolderResource = ruleService.pagedRuleFolder(
                folderName,
                start, length
        );

        List<RuleFolderResource> resources = (List<RuleFolderResource>) pagedFolderResource.get("data");
        List<Resource> displayHeadPermits = dataResourcesPermits(resources, subjectIdUser, subjectId,
                ResourceType.DATA_FOLDER.getType());
        Map<String, Object> result = ImmutableMap.<String, Object>of(
                "operatePermits", getOperatePermitsByResourceTypeAndAllPermits(
                        subjectId, ResourceType.DATA_FOLDER.getType()),
                "permits", pagedFolderResource,
                "displayHeadPermits", displayHeadPermits
        );
        return result;
    }

    // 给 DataResource 数据 添加是否有操作的权限   并返回  这个分页的 操作级别的 头
    private List<Resource> dataResourcesPermits(List<? extends DataResource> dataResources,
                                                List<String> subjectIdUser, String subjectIdRole, String resourceTypeId) {
        // 数据是否为空判断
        if (dataResources == null || dataResources.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> subjectsAll = new ArrayList<>();
        subjectsAll.addAll(subjectIdUser);
        subjectsAll.add(subjectIdRole);

        // 从CRE_AUTH_RESOURCE中获取模型具有的操作
        final List<Resource> headeResources = resourceService.getResourcesByResourceType(resourceTypeId);

        boolean hasAllPermitsUser = hasAllPermitsBySubjects(subjectIdUser, resourceTypeId);
        boolean hasAllPermitsRole = hasAllPermits(subjectIdRole, resourceTypeId);

        // 获取全部数据在操作级所拥有的权限
        final List<Authority> operatePermits = getSubjectsOperatePermitsWithoutAllByResourceType(
                subjectsAll, resourceTypeId);
        // 获取各项数据的权限
        final List<Authority> permits = getPermitsBySubjectsDataResources(
                subjectsAll, resourceTypeId, dataResources);

        //将全部数据所拥有的所有权限 添加到operatePermits中 , 之后遍历DataResource,每一个的操作都会复制这个数组.
        // 如果用户具有全部权限   设置所有的显示为 true
        for (Resource resource : headeResources) {
            setDisplayAndChooesd(resource, hasAllPermitsUser, hasAllPermitsRole);
        }

        //将 全部数据在操作级所拥有的权限 添加到operatePermits中 , 之后遍历DataResource,每一个的操作都会复制这个数组.
        for (Authority authority : operatePermits) {
            addResourcePermitsByExpression(authority.getResourceExpression(), authority.getSubjectId(),
                    headeResources, subjectIdRole);
        }

        // 遍历各项数据列表，添加操作
        for (DataResource dataResource : dataResources) {
            final List<Resource> copyResources = deepCopy(headeResources);
            if (permits != null && !permits.isEmpty()) {
                for (Authority authority : permits) {
                    if (authority.getResourceId().equals(dataResource.getResourceId())) {
                        addDataResourcePermits(copyResources, authority, subjectIdRole, subjectIdUser);
                    }
                }
            }
            dataResource.setResources(copyResources);
        }
        return headeResources;
    }

    /**
     * 通过查询出来的权限数据  给 dataResource的 的操作授权
     *
     * @param resources      dataResource的 List<Resources>
     * @param authority      查询出来的这条数据的权限
     * @param subjectIdRole  被授权的角色的主体iD
     * @param subjectIdsUser 当前用户的主体id
     */
    private void addDataResourcePermits(List<Resource> resources, Authority authority,
                                        String subjectIdRole, List<String> subjectIdsUser) {
        if (authority == null) {
            return;
        }
        final String resourceExpression = authority.getResourceExpression();
        if (StringUtils.isBlank(resourceExpression)) {
            return;
        }

        if ("*".equals(resourceExpression)) {
            boolean isDisplay = !subjectIdRole.equals(authority.getSubjectId());
            setResourceListDisplayAndChooesd(resources, isDisplay);
        } else {
            addResourcePermitsByExpression(resourceExpression, authority.getSubjectId(),
                    resources, subjectIdRole);
        }

    }

    private void setDisplayOrChooesd(Resource resource, boolean isDisplay) {
        setDisplayAndChooesd(resource, isDisplay, !isDisplay);
    }

    private void setDisplayAndChooesd(Resource resource, boolean isDisplay, boolean isChooesd) {
        if (isDisplay) {
            resource.setDisplay(true);
        }
        if (isChooesd) {
            //如果 被授权的角色具有全部权限 , 设置所有的勾选为 true
            resource.setChoosed(true);
        }
    }

    private void setResourceListDisplayAndChooesd(List<Resource> resources, boolean isDisplay) {
        if (isDisplay) {
            allResourcesDisplay(resources);
        } else {
            //如果 被授权的角色具有全部权限 , 设置所有的勾选为 true
            allResourcesChoosed(resources);
        }
    }


    // 通过权限表达式给resources授权
    private void addResourcePermitsByExpression(String resourceExpression, String subjectId, List<Resource> resources,
                                                String subjectIdRole) {
        List<String> expressions = Splitter.on(",").splitToList(resourceExpression);
        for (String expression : expressions) {
            if (!StringUtils.isBlank(expression)) {
                for (Resource resource : resources) {
                    if (expression.equals(resource.getResourceUrl())) {
                        //判断  如果是 被授权的角色, 将 表头的操作的勾选改(choosed)为true
                        setDisplayOrChooesd(resource, !subjectIdRole.equals(subjectId));
                    }
                }
            }
        }
    }


    private List<Resource> deepCopy(List<Resource> source) {
        if (source != null) {
            List<Resource> dest = new LinkedList<>();
            for (Resource dataResource : source) {
                try {
                    dest.add((Resource) dataResource.clone());
                } catch (CloneNotSupportedException e) {
                    throw new RuntimeException("复制失败");
                }
            }
            return dest;
        }
        return Collections.emptyList();
    }

    private void allResourcesDisplay(List<Resource> resources) {
        allResourcesDisplay(resources, true);
    }

    private void allResourcesDisplay(List<Resource> resources, boolean display) {
        for (Resource resource : resources) {
            resource.setDisplay(display);
        }
    }

    private void allResourcesChoosed(List<Resource> resources) {
        allResourcesChoosed(resources, true);
    }

    private void allResourcesChoosed(List<Resource> resources, boolean choosed) {
        for (Resource resource : resources) {
            resource.setChoosed(choosed);
        }
    }

    /**
     * 当前用户是否拥有全部权限     subjectIdUser
     * @return
     */
    @Override
    public boolean isCurrentUserHasAllPermits() {
        // 根据用户获取对应的多个主体（用户本身、用户的角色）
        List<String> subjects = subjectService.selectSubjectsByCurrentUser();
        return hasAllPermitsBySubjects(subjects);
    }

    /**
     * 用户是否拥有全部权限
     * @param userId
     * @return
     */
    @Override
    public boolean isUserHasAllPermits(String userId) {
        // 根据用户获取对应的多个主体（用户本身、用户的角色）
        List<String> subjects = subjectService.selectSubjectsByUserId(userId);
        return hasAllPermitsBySubjects(subjects);
    }

    // 角色是否拥有全部权限
    @Override
    public boolean isRoleHasAllPermits(String roleId) {
        final String subjectId = subjectService.selectByRoleId(roleId);
        return hasAllPermits(subjectId);
    }

    @Override
    // 主体是否拥有全部权限
    public boolean hasAllPermits(String subjectId) {
        return hasAllPermitsByResourceType(subjectId, ResourceType.ALL.getType());
    }

    @Override
    public boolean hasAllPermits(String subjectId, String resourceTypeId) {
        return hasAllPermits(subjectId) || hasAllPermitsByResourceType(subjectId, resourceTypeId);
    }

    // 主体对某一资源类型是否拥有全部权限  menu * *
    @Override
    public boolean hasAllPermitsByResourceType(String subjectId, String resourceTypeId) {
        if (StringUtils.isBlank(subjectId)) {
            throw new IllegalArgumentException("获取权限失败，参数[subjectId]不能为空");
        }
        if (StringUtils.isBlank(resourceTypeId)) {
            throw new IllegalArgumentException("获取权限失败，参数[resourceTypeId]不能为空");
        }


        Map<String, String> param = ImmutableMap.of(
                "subjectId", subjectId,
                "resourceTypeId", resourceTypeId
        );
        int count = (int) daoHelper.queryOne(_MYBITSID_PREFIX + "hasAllPermitsByResourceType", param);
        if (count > 0) {
            return true;
        }
        return false;
    }

    @Override
    // 主体是否拥有全部权限
    public boolean hasAllPermitsBySubjects(List<String> subjectIds) {
        return hasAllPermitsBySubjectsResourceType(subjectIds, ResourceType.ALL.getType());
    }

    // 主体的资源类型是否拥有全部权限
    @Override
    public boolean hasAllPermitsBySubjects(List<String> subjectIds, String resourceTypeId) {
        return hasAllPermitsBySubjects(subjectIds) || hasAllPermitsBySubjectsResourceType(subjectIds, resourceTypeId);
    }

    //通过主体资源类型获取是否拥有全部权限
    @Override
    public boolean hasAllPermitsBySubjectsResourceType(List<String> subjectIds, String resourceTypeId) {
        if (subjectIds == null) {
            throw new IllegalArgumentException("获取权限失败，参数[subjectIds]不能为空");
        }
        if (StringUtils.isBlank(resourceTypeId)) {
            throw new IllegalArgumentException("获取权限失败，参数[resourceTypeId]不能为空");
        }

        Map<String, Object> param = ImmutableMap.of(
                "subjectIds", subjectIds,
                "resourceTypeId", resourceTypeId
        );
        int count = (int) daoHelper.queryOne(_MYBITSID_PREFIX +
                "hasAllPermitsBySubjectsResourceType", param);
        if (count > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean hasAllPermitsBySubjectsResourceTypeExpression(List<String> subjectIds,
                                                                 String resourceTypeId,
                                                                 String resourceExpression) {
        if (subjectIds == null) {
            throw new IllegalArgumentException("获取权限失败，参数[subjectIds]不能为空");
        }
        if (StringUtils.isBlank(resourceTypeId)) {
            throw new IllegalArgumentException("获取权限失败，参数[resourceTypeId]不能为空");
        }
        if (StringUtils.isBlank(resourceExpression)) {
            throw new IllegalArgumentException("获取权限失败，参数[resourceExpression]不能为空");
        }


        Map<String, Object> param = ImmutableMap.of(
                "subjectIds", subjectIds,
                "resourceTypeId", resourceTypeId,
                "resourceExpression", resourceExpression
        );
        int count = (int) daoHelper.queryOne(_MYBITSID_PREFIX +
                "hasAllPermitsBySubjectsResourceTypeExpression", param);
        if (count > 0) {
            return true;
        }
        return false;
    }

    // 获取对全部数据在操作级所拥有的权限，如全部模型可查看
    @Override
    public List<Authority> getOperatePermitsWithoutAllByResourceType(String subjectId,
                                                                     String resourceTypeId) {
        Map<String, String> param = ImmutableMap.of(
                "subjectId", subjectId,
                "resourceTypeId", resourceTypeId
        );
        List<Authority> operatePermits = daoHelper.queryForList(_MYBITSID_PREFIX +
                "getOperatePermitsWithoutAllByResourceType", param);
        return operatePermits;
    }

    // 获取对全部数据在操作级所拥有的权限，如全部模型可查看
    @Override
    public List<Authority> getSubjectsOperatePermitsWithoutAllByResourceType(List<String> subjectIds,
                                                                             String resourceTypeId) {
        Map<String, Object> param = ImmutableMap.of(
                "subjectIds", subjectIds,
                "resourceTypeId", resourceTypeId
        );
        List<Authority> operatePermits = daoHelper.queryForList(_MYBITSID_PREFIX +
                "getSubjectsOperatePermitsWithoutAllByResourceType", param);
        return operatePermits;
    }

    @Override
    public List<Authority> getOperatePermitsByResourceTypeAndAllPermits(String subjectId,
                                                                        String resourceTypeId) {
        if (hasAllPermits(subjectId)) {
            final Authority authority = new Authority();
            authority.setResourceId("*");
            authority.setResourceExpression("*");
            authority.setResourceTypeId(resourceTypeId);
            return ImmutableList.of(authority);
        }
        return getOperatePermitsByResourceType(subjectId, resourceTypeId);
    }

    @Override
    public List<Authority> getOperatePermitsByResourceType(String subjectId, String resourceTypeId) {
        Map<String, String> param = ImmutableMap.of(
                "subjectId", subjectId,
                "resourceTypeId", resourceTypeId
        );
        List<Authority> operatePermits = daoHelper.queryForList(_MYBITSID_PREFIX +
                "getOperatePermitsByResourceType", param);
        return operatePermits;
    }

    // 获取指定主体id和资源类型的权限表达式
    @Override
    public List<Authority> getPermitsBySubjectResourceType(String subjectId, String resourceTypeId) {
        if (StringUtils.isBlank(subjectId)) {
            throw new IllegalArgumentException("获取权限失败，参数[subjectId]不能为空");
        }
        if (StringUtils.isBlank(resourceTypeId)) {
            throw new IllegalArgumentException("获取权限失败，参数[resourceTypeId]不能为空");
        }

        Map<String, String> param = ImmutableMap.of(
                "subjectId", subjectId,
                "resourceTypeId", resourceTypeId
        );
        List<Authority> permits = daoHelper.queryForList(_MYBITSID_PREFIX +
                "getPermitsBySubjectResourceType", param);
        return permits;
    }

    // 获取指定主体id、资源id和资源类型的权限表达式
    @Override
    public Authority getPermitBySubjectResourceId(String subjectId,
                                                  String resourceId,
                                                  String resourceTypeId) {
        if (StringUtils.isBlank(subjectId)) {
            throw new IllegalArgumentException("获取权限失败，参数[subjectId]不能为空");
        }
        if (StringUtils.isBlank(resourceId)) {
            throw new IllegalArgumentException("获取权限失败，参数[resourceId]不能为空");
        }
        if (StringUtils.isBlank(resourceTypeId)) {
            throw new IllegalArgumentException("获取权限失败，参数[resourceTypeId]不能为空");
        }

        Map<String, String> param = ImmutableMap.of(
                "subjectId", subjectId,
                "resourceId", resourceId,
                "resourceTypeId", resourceTypeId
        );
        Authority permit = (Authority) daoHelper.queryOne(_MYBITSID_PREFIX +
                "getPermitBySubjectResourceId", param);
        return permit;
    }

    // 根据主体id、数据列表获取各个数据具有的权限
    @Override
    public List<Authority> getPermitsBySubjectDataResources(String subjectId, String resourceTypeId,
                                                            List<? extends DataResource> dataResources) {
        List<String> resourceIds = new ArrayList<>(dataResources.size());
        for (DataResource dataResource : dataResources) {
            resourceIds.add(dataResource.getResourceId());
        }
        List<Authority> permits = getPermitsBySubjectResourceIds(subjectId, resourceIds,
                resourceTypeId);
        return permits;
    }

    @Override
    public List<Authority> getPermitsBySubjectsDataResources(List<String> subjectIds, String resourceTypeId,
                                                             List<? extends DataResource> dataResources) {
        List<String> resourceIds = new ArrayList<>(dataResources.size());
        for (DataResource dataResource : dataResources) {
            resourceIds.add(dataResource.getResourceId());
        }
        List<Authority> permits = getPermitsBySubjectsResourceIds(subjectIds, resourceIds,
                resourceTypeId);
        return permits;
    }

    // 获取指定主体id、资源ids和资源类型的权限表达式
    @Override
    public List<Authority> getPermitsBySubjectResourceIds(String subjectId, List<String> resourceIds,
                                                          String resourceTypeId) {
        if (StringUtils.isBlank(subjectId)) {
            throw new IllegalArgumentException("获取权限失败，参数[subjectId]不能为空");
        }
        if (resourceIds == null) {
            throw new IllegalArgumentException("获取权限失败，参数[resourceIds]不能为空");
        }
        if (StringUtils.isBlank(resourceTypeId)) {
            throw new IllegalArgumentException("获取权限失败，参数[resourceTypeId]不能为空");
        }

        Map<String, Object> param = ImmutableMap.of(
                "subjectId", subjectId,
                "resourceIds", resourceIds,
                "resourceTypeId", resourceTypeId
        );
        List<Authority> permits = daoHelper.queryForList(_MYBITSID_PREFIX +
                "getPermitsBySubjectResourceIds", param);
        return permits;
    }

    @Override
    public List<Authority> getPermitsBySubjectsResourceIds(List<String> subjectIds, List<String> resourceIds,
                                                           String resourceTypeId) {
        if (subjectIds == null || subjectIds.isEmpty()) {
            throw new IllegalArgumentException("获取权限失败，参数[subjectId]不能为空");
        }
        if (resourceIds == null) {
            throw new IllegalArgumentException("获取权限失败，参数[resourceIds]不能为空");
        }
        if (StringUtils.isEmpty(resourceTypeId)) {
            throw new IllegalArgumentException("获取权限失败，参数[resourceTypeId]不能为空");
        }

        Map<String, Object> param = ImmutableMap.of(
                "subjectIds", subjectIds,
                "resourceIds", resourceIds,
                "resourceTypeId", resourceTypeId
        );
        List<Authority> permits = daoHelper.queryForList(_MYBITSID_PREFIX +
                "getPermitsBySubjectsResourceIds", param);
        return permits;
    }

    @Override
    public Set<String> getDistinctPermitsBySubjectIdsResourceId(List<String> subjectIds,
                                                                @Nullable String resourceId,
                                                                String resourceTypeId) {
        final List<String> allPermits = getAllPermitsBySubjectIdsResourceId(subjectIds, resourceId, resourceTypeId);
        if (allPermits == null || allPermits.isEmpty()) {
            return Collections.emptySet();
        }
        // 取并集
        Set<String> distinctPermits = new HashSet<>(allPermits.size());
        for (String permit : allPermits) {
            final List<String> list = Splitter.on(",").splitToList(permit); // 可能为“,”分隔的多值表达式
            distinctPermits.addAll(list);
        }
        return distinctPermits;
    }

    @Override
    public List<String> getAllPermitsBySubjectIdsResourceId(List<String> subjectIds,
                                                            @Nullable String resourceId,
                                                            String resourceTypeId) {
        if (subjectIds == null || subjectIds.isEmpty()) {
            throw new IllegalArgumentException("获取权限失败，参数[subjectIds]不能为空");
        }
        if (StringUtils.isBlank(resourceTypeId)) {
            throw new IllegalArgumentException("获取权限失败，参数[resourceTypeId]不能为空");
        }

        Map<String, Object> param = new HashMap<>();
        param.put("subjectIds", subjectIds);
        param.put("resourceId", resourceId);
        param.put("resourceTypeId", resourceTypeId);

        List<String> permits = daoHelper.queryForList(_MYBITSID_PREFIX +
                "getAllPermitsBySubjectIdsResourceId", param);
        return permits;
    }


    @Override
    public List<String> getAllResourceIdsBySubjectIdsResourceType(List<String> subjectIds,
                                                                  String resourceTypeId) {
        if (subjectIds == null || subjectIds.isEmpty()) {
            throw new IllegalArgumentException("获取权限失败，参数[subjectIds]不能为空");
        }
        if (StringUtils.isBlank(resourceTypeId)) {
            throw new IllegalArgumentException("获取权限失败，参数[resourceTypeId]不能为空");
        }

        Map<String, Object> param = ImmutableMap.of(
                "subjectIds", subjectIds,
                "resourceTypeId", resourceTypeId
        );
        List<String> resourceIds = daoHelper.queryForList(_MYBITSID_PREFIX +
                "getAllResourceIdsBySubjectIdsResourceType", param);
        return resourceIds;
    }

    private void buildAuthority(Authority authority, String currentUser,
                                String subjectId, String resourceTypeId,
                                boolean update) {
        buildAuthorityWithoutResourceType(authority, currentUser, subjectId, update);
        if (resourceTypeId != null) {
            authority.setResourceTypeId(resourceTypeId);
        }
    }

    private void buildAuthorityWithoutResourceType(Authority authority, String currentUser,
                                                   String subjectId, boolean update) {
        if (StringUtils.isBlank(authority.getResourceId())) {
            throw new IllegalArgumentException("参数[resourceId]不能为null");
        }
        if (StringUtils.isBlank(authority.getResourceExpression())) {
            throw new IllegalArgumentException("参数[resourceExpression]不能为null");
        }
        if (!update) {
            authority.setId(IdUtil.createId());
            authority.setCreateDate(new Date());
            authority.setCreatePerson(currentUser);
        } else {
            authority.setUpdateDate(new Date());
            authority.setUpdatePerson(currentUser);
        }
        authority.setSubjectId(subjectId);
    }

    @Override
    @Transactional
    public ResponseResult insert(Authority authority, String currentUser,
                                 String subjectId, String resourceTypeId) {
        if (authority != null) {
            buildAuthority(authority, currentUser, subjectId, resourceTypeId, false);
            daoHelper.insert(_MYBITSID_PREFIX + "insertSelective", authority);
            return ResponseResult.createSuccessInfo();
        }

        return ResponseResult.createFailInfo("参数[authority]不能为null");
    }

    @Override
    @Transactional
    public ResponseResult insertBatch(List<Authority> authorities, String currentUser,
                                      String subjectId, String resourceTypeId) {
        if (CollectionUtil.isEmpty(authorities)) {
            return ResponseResult.createFailInfo("参数[authorities]不能为空");
        }

        for (Authority authority : authorities) {
            buildAuthority(authority, currentUser, subjectId, resourceTypeId, false);
        }

        daoHelper.insert(_MYBITSID_PREFIX + "insertBatch", authorities);
        return ResponseResult.createSuccessInfo();
    }


    @Override
    @Transactional
    public ResponseResult upsertBatch(List<Authority> list, String currentUser,
                                      String subjectId, String resourceTypeId) {
        if (CollectionUtil.isEmpty(list)) {
            return ResponseResult.createFailInfo("参数[authorities]不能为空");
        }

        for (Authority authority : list) {
            buildAuthority(authority, currentUser, subjectId, resourceTypeId, true);
        }
        daoHelper.update(_MYBITSID_PREFIX + "upsertBatch", list);
        return ResponseResult.createSuccessInfo();
    }


    @Override
    @Transactional
    public ResponseResult updateBatch(List<Authority> list, String currentUser,
                                      String subjectId, String resourceTypeId) {
        if (CollectionUtil.isEmpty(list)) {
            return ResponseResult.createFailInfo("参数[authorities]不能为空");
        }

        for (Authority authority : list) {
            buildAuthority(authority, currentUser, subjectId, resourceTypeId, true);
        }
        daoHelper.update(_MYBITSID_PREFIX + "updateBatch", list);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    public ResponseResult grantAllPermits(String roleId, String currentUser) {
        final String subjectId = subjectService.selectByRoleId(roleId);

        final Authority authority = new Authority();
        authority.setResourceId("*");
        authority.setResourceExpression("*");
        buildAuthority(authority, currentUser, subjectId, ResourceType.ALL.getType(), false);
        daoHelper.insert(_MYBITSID_PREFIX + "insertSelective", authority);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional
    public ResponseResult cancleAllPermits(String roleId) {
        final String subjectId = subjectService.selectByRoleId(roleId);
        return deleteBySubjectResourceType(subjectId, ResourceType.ALL.getType());
    }

    @Override
    @Transactional
    public ResponseResult deleteAllPermits(String roleId) {
        log.debug("delete all Permits --- roleId = " + roleId);
        final String subjectId = subjectService.selectByRoleId(roleId);
        return deleteBySubjectId(subjectId);
    }

    /**
     * 为当前用户自动授予对资源的全部权限
     *
     * @param resourceId
     * @param resourceType
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void autoGrantAuthToCurrentUser(String resourceId, ResourceType resourceType) {
        final Authority authority = new Authority();
        authority.setResourceId(resourceId);
        authority.setResourceExpression("*");
        String currentUser = ControllerUtil.getCurrentUser();
        grantToUser(authority, resourceType.getType(), currentUser);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void grantToUser(Authority authority, String resourceTypeId, String currentUser) {
        final String subjectId = subjectService.selectBySubjectObjectId(currentUser, SubjectType.USER.getType());
        if (StringUtils.isBlank(subjectId)) {
            log.warn("授权失败，用户的主体id不存在");
            throw new RuntimeException("授权失败，用户的主体id不存在");
        }
        insert(authority, currentUser, subjectId, resourceTypeId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Deprecated
    public ResponseResult grant(List<Authority> authorities, String currentUser,
                                String roleId, String resourceTypeId) {
        final String subjectId = subjectService.selectByRoleId(roleId);
        if (StringUtils.isBlank(subjectId)) {
            log.warn("授权失败，角色的主体id不存在");
            return ResponseResult.createFailInfo("授权失败，角色的主体id不存在");
        }
        // 先将旧的权限全部删除再批量插入
        deleteBySubjectResourceType(subjectId, resourceTypeId);
        insertBatch(authorities, currentUser, subjectId, resourceTypeId);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult grant(GrantAuthRequest grantAuthRequest, String currentUser, String resourceTypeId) {
        final String roleId = grantAuthRequest.getRoleId();
        final String isAllAuth = grantAuthRequest.getIsAllAuth();
        final List<String> needDelAuthorities = grantAuthRequest.getNeedDelAuthorities();
        final List<Authority> needInsertAuthorities = grantAuthRequest.getNeedInsertAuthorities();
        final List<Authority> needUpdateAuthorities = grantAuthRequest.getNeedUpdateAuthorities();
        return grant(needDelAuthorities, needInsertAuthorities, needUpdateAuthorities, currentUser, roleId, resourceTypeId, isAllAuth);
    }


    /**
     * 公共参数需要对对象类型参数的内嵌参数进行权限修改
     */
    @Override
    public void updatePubVariableAuthList(GrantAuthRequest grantAuthRequest) {
        final List<String> needDelAuthorities = grantAuthRequest.getNeedDelAuthorities();
        final List<Authority> needInsertAuthorities = grantAuthRequest.getNeedInsertAuthorities();

        final List<String> needDelEntityAuths = grantAuthRequest.getNeedDelEntityAuths();
        final List<String> needInsertEntityAuths = grantAuthRequest.getNeedInsertEntityAuths();
//        final List<Authority> needUpdateEntityAuths = grantAuthRequest.getNeedUpdateEntityAuths();

        if (!CollectionUtil.isEmpty(needDelEntityAuths)) {
            List<String> nestedVariableIds = variableService.getNestedVariableIdsByEntityIds(needDelEntityAuths);
            needDelAuthorities.addAll(nestedVariableIds);
        }

        if (!CollectionUtil.isEmpty(needInsertEntityAuths)) {
            List<Authority> needInsertNestedAuths = new LinkedList<>();
            for (String resourceId : needInsertEntityAuths) {
//                String resourceId = authority.getResourceId();

                // 查询嵌套参数
                List<String> nestedVariableIds = variableService.getNestedVariableIds(resourceId);

                for (String variableId : nestedVariableIds) {
                    Authority nestedAuth = new Authority();
                    nestedAuth.setResourceId(variableId);
                    nestedAuth.setResourceExpression("*");
                    needInsertNestedAuths.add(nestedAuth);
                }
            }
            needInsertAuthorities.addAll(needInsertNestedAuths);
        }
    }

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
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public ResponseResult grant(List<String> needDelAuthorities,
                                List<Authority> needInsertAuthorities,
                                List<Authority> needUpdateAuthorities,
                                String currentUser,
                                String roleId,
                                String resourceTypeId, String isAllAuth) {
        final String subjectId = subjectService.selectByRoleId(roleId);
        if (StringUtils.isBlank(subjectId)) {
            log.warn("授权失败，角色的主体id不存在");
            return ResponseResult.createFailInfo("授权失败，角色的主体id不存在");
        }

        //如果是全权,则删除所有权限,再添加所有资源的所有权限
        if (GrantAuthRequest.IS_ALL_AUTH.equals(isAllAuth)) {
            deleteAllPermits(roleId);
            addAllAuthByResourceIDAndType(subjectId, currentUser);
        }

        // 删除指定资源的权限
        if (!CollectionUtil.isEmpty(needDelAuthorities)) {
            deleteBySubjectResources(subjectId, needDelAuthorities, resourceTypeId);
        }

        //添加
        if (!CollectionUtil.isEmpty(needInsertAuthorities)) {
            insertBatch(needInsertAuthorities, currentUser, subjectId, resourceTypeId);
//            upsertBatch(needInsertAuthorities, currentUser, subjectId, resourceTypeId);
        }

        //更新
        if (!CollectionUtil.isEmpty(needUpdateAuthorities)) {
            upsertBatch(needUpdateAuthorities, currentUser, subjectId, resourceTypeId);
        }
        return ResponseResult.createSuccessInfo();
    }


    // 由前端确定 resourceTypeId

    //授权菜单和按钮全部权限
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult grantMenuAndButton(List<Authority> authorities, String currentUser,
                                             String roleId, String isAllAuth) {
        final String subjectId = subjectService.selectByRoleId(roleId);
        if (StringUtils.isBlank(subjectId)) {
            log.warn("授权失败，角色的主体id不存在");
            return ResponseResult.createFailInfo("授权失败，角色的主体id不存在");
        }

        //如果是全权,则删除所有权限,再添加所有资源的所有权限
        if (GrantAuthRequest.IS_ALL_AUTH.equals(isAllAuth)) {
            log.debug("isAllAuth");
            deleteAllPermits(roleId);
            addAllAuthByResourceIDAndType(subjectId, currentUser);
        }


        deleteBySubjectResourceType(subjectId, ResourceType.MENU.getType());
        deleteBySubjectResourceType(subjectId, ResourceType.BUTTON.getType());

        /*Set<String> resourceTypeIds = new HashSet<>();
        for (Authority authority : authorities) {
            final String resourceTypeId = authority.getResourceTypeId();
            if (StringUtils.isNotEmpty(resourceTypeId)) {
                resourceTypeIds.add(resourceTypeId);
            }
        }
        if (resourceTypeIds.isEmpty()) {
            return ResponseResult.createFailInfo("授权失败，未指定授权资源类型");
        }
        // 先将旧的权限全部删除再批量插入
        for (String resourceTypeId : resourceTypeIds) {
            deleteBySubjectResourceType(subjectId, resourceTypeId);
        }*/

        if (!CollectionUtil.isEmpty(authorities)) {
            insertBatch(authorities, currentUser, subjectId, null);
        }
        return ResponseResult.createSuccessInfo();

    }

    //主键id删除
    @Override
    @Transactional
    public ResponseResult deleteByPrimaryKey(String id) {
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("参数[id]不能为空");
        }
        daoHelper.delete(_MYBITSID_PREFIX + "deleteByPrimaryKey", id);
        return ResponseResult.createSuccessInfo();
    }
   //主体id删除
    @Override
    @Transactional
    public ResponseResult deleteBySubjectId(String subjectId) {
        if (StringUtils.isBlank(subjectId)) {
            throw new IllegalArgumentException("参数[subjectId]不能为空");
        }
        daoHelper.delete(_MYBITSID_PREFIX + "deleteBySubjectId", subjectId);
        return ResponseResult.createSuccessInfo();
    }

    //主体类型删除
    @Override
    @Transactional
    public ResponseResult deleteBySubjectResourceType(String subjectId, String resourceTypeId) {
        if (StringUtils.isBlank(subjectId)) {
            throw new IllegalArgumentException("参数[subjectId]不能为空");
        }
        if (StringUtils.isBlank(resourceTypeId)) {
            throw new IllegalArgumentException("参数[resourceTypeId]不能为空");
        }

        Map<String, String> param = ImmutableMap.of(
                "subjectId", subjectId,
                "resourceTypeId", resourceTypeId
        );

        daoHelper.delete(_MYBITSID_PREFIX + "deleteBySubjectResource", param);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional
    public ResponseResult deleteBySubjectResourceId(String subjectId,
                                                    String resourceId,
                                                    String resourceTypeId) {
        if (StringUtils.isBlank(subjectId)) {
            throw new IllegalArgumentException("参数[subjectId]不能为空");
        }
        if (StringUtils.isBlank(resourceId)) {
            throw new IllegalArgumentException("参数[resourceId]不能为空");
        }

        if (StringUtils.isBlank(resourceTypeId)) {
            throw new IllegalArgumentException("参数[resourceTypeId]不能为空");
        }

        Map<String, String> param = ImmutableMap.of(
                "subjectId", subjectId,
                "resourceId", resourceId,
                "resourceTypeId", resourceTypeId
        );

        daoHelper.delete(_MYBITSID_PREFIX + "deleteBySubjectResource", param);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional
    public ResponseResult deleteBySubjectResources(String subjectId,
                                                   List<String> resourceIds,
                                                   String resourceTypeId) {
        if (StringUtils.isBlank(subjectId)) {
            throw new IllegalArgumentException("参数[subjectId]不能为空");
        }
        if (resourceIds == null) {
            throw new IllegalArgumentException("参数[resourceIds]不能为空");
        }

        if (StringUtils.isBlank(resourceTypeId)) {
            throw new IllegalArgumentException("参数[resourceTypeId]不能为空");
        }

        Map<String, Object> param = ImmutableMap.of(
                "subjectId", subjectId,
                "resourceIds", resourceIds,
                "resourceTypeId", resourceTypeId
        );

        daoHelper.delete(_MYBITSID_PREFIX + "deleteBySubjectResources", param);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional
    public ResponseResult deleteByResourceId(String resourceId, String resourceTypeId) {
        if (StringUtils.isBlank(resourceId)) {
            throw new IllegalArgumentException("参数[resourceId]不能为空");
        }

        if (StringUtils.isBlank(resourceTypeId)) {
            throw new IllegalArgumentException("参数[resourceTypeId]不能为空");
        }

        Map<String, String> param = ImmutableMap.of(
                "resourceId", resourceId,
                "resourceTypeId", resourceTypeId
        );

        daoHelper.delete(_MYBITSID_PREFIX + "deleteByResourceId", param);
        return ResponseResult.createSuccessInfo();
    }

    /**
     * 修改 权限表中的 ruleId
     *
     * @return
     */
    @Override
    @Transactional
    public ResponseResult updateRuleIdByOldResourceId(String oldResourceId, String resourceTypeId, String newResourceId) {
        if (StringUtils.isBlank(oldResourceId)) {
            throw new IllegalArgumentException("参数[oldResourceId]不能为空");
        }

        if (StringUtils.isBlank(resourceTypeId)) {
            throw new IllegalArgumentException("参数[resourceTypeId]不能为空");
        }
        if (StringUtils.isBlank(newResourceId)) {
            throw new IllegalArgumentException("参数[newResourceId]不能为空");
        }

        Map<String, String> param = ImmutableMap.of(
                "oldResourceId", oldResourceId,
                "resourceTypeId", resourceTypeId,
                "newResourceId", newResourceId
        );

        daoHelper.delete(_MYBITSID_PREFIX + "deleteByResourceId", param);
        return ResponseResult.createSuccessInfo();
    }


    @Override
    @Transactional
    public ResponseResult deleteByResourceIds(List<String> resourceIds, String resourceTypeId) {
        if (resourceIds == null) {
            throw new IllegalArgumentException("参数[resourceIds]不能为空");
        }

        if (StringUtils.isBlank(resourceTypeId)) {
            throw new IllegalArgumentException("参数[resourceTypeId]不能为空");
        }

        Map<String, Object> param = ImmutableMap.of(
                "resourceIds", resourceIds,
                "resourceTypeId", resourceTypeId
        );

        daoHelper.delete(_MYBITSID_PREFIX + "deleteByResourceIds", param);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult addAllAuthByResourceIDAndType(String subjectId, String currentUser) {


//        final String subjectId = subjectService.selectByRoleId(grantAuthRequest.getRoleId());
        if (StringUtils.isBlank(subjectId)) {
            log.warn("授权失败，角色的主体id不存在");
            return ResponseResult.createFailInfo("授权失败，角色的主体id不存在");
        }
        List<Authority> needInsertAuthorities = new ArrayList<>();
        String start = "0";
        String length = "" + Integer.MAX_VALUE;
        needInsertAuthorities.addAll(getMenuAndButtonAllAuth());
        needInsertAuthorities.addAll(selectAllMetaTableToAuthority(null, null, null, start, length));
        needInsertAuthorities.addAll(selectAllModleToAuthority(null, null, null, start, length));
        needInsertAuthorities.addAll(selectAllPubApisToAuthority(null, null, null, null, start, length));
        needInsertAuthorities.addAll(selectAllPubModelBasesToAuthority(null, null, null, null, null, start, length));
        needInsertAuthorities.addAll(selectAllPubruleSetToAuthority(null, null, null, null, start, length));
        needInsertAuthorities.addAll(selectAllPubVariablesToAuthority(null, null, null, null, null, start, length));
        needInsertAuthorities.addAll(selectAllDataSourcesToAuthority(null, null, null, start, length));
        needInsertAuthorities.addAll(selectAllTasksToAuthority(null, null, null, null, start, length));

        //添加
        if (!CollectionUtil.isEmpty(needInsertAuthorities)) {
            insertBatch(needInsertAuthorities, currentUser, subjectId, null);
        }
        return ResponseResult.createSuccessInfo();
    }

    public List<Authority> getMenuAndButtonAllAuth() {
        List<Authority> authorities = new ArrayList<>();
        //{resourceId: "*", resourceTypeId: "1", resourceExpression: "*"}
        Authority menuAuthority = new Authority();
        menuAuthority.setResourceTypeId("1");
        menuAuthority.setResourceId("*");
        menuAuthority.setResourceExpression("*");
        authorities.add(menuAuthority);

        Authority buttonAuthority = new Authority();
        buttonAuthority.setResourceTypeId("2");
        buttonAuthority.setResourceId("*");
        buttonAuthority.setResourceExpression("*");

        authorities.add(buttonAuthority);

        return authorities;

    }

    /**
     * 获取所有的 元数据  转换成Authority 用于赋予所有权限
     */
    public List<Authority> selectAllMetaTableToAuthority(String tableName, String dbAlias,
                                                         String folderName, String start, String length) {
        Map<String, Object> pagedTables = metaDataMgrService.pagedMetaTablesByDsNameTableNamePackageName(tableName, dbAlias, folderName, start, length);
        List<MetadataResource> metadataResources = (List<MetadataResource>) pagedTables.get("data");

        List<Authority> authorities = paseDataResourceToAuthority(metadataResources, ResourceType.DATA_METADATA.getType(), "*");

        return authorities;
    }

    /**
     * 获取所有的 模型  转换成Authority 用于赋予所有权限
     */
    public List<Authority> selectAllModleToAuthority(String ruleName, String ruleType,
                                                     String folderName, String start, String length) {
        final Map<String, Object> pagedModles = ruleDetailService.pagedByRuleNameFolderNameRuleType(
                ruleName, ruleType, folderName, start, length);
        List<RuleResource> ruleResources = (List<RuleResource>) pagedModles.get("data");

        List<Authority> authorities = paseDataResourceToAuthority(ruleResources, ResourceType.DATA_MODEL.getType(), "*");


        return authorities;
    }

    /**
     * 获取所有的 公共参数  转换成Authority 用于赋予所有权限
     */
    public List<Authority> selectAllPubVariablesToAuthority(String variableAlias,
                                                            String variableGroupName,
                                                            String variableTypeId,
                                                            String startDate, String endDate,
                                                            String start, String length) {

        // 分页获取元数据
        final Map<String, Object> pagedVaraibles = variableService.pagedPubVariableResources(variableAlias, variableGroupName,
                variableTypeId, startDate, endDate, start, length);

        List<PubVariableResource> resources = (List<PubVariableResource>) pagedVaraibles.get("data");

        List<Authority> authorities = paseDataResourceToAuthority(resources, ResourceType.DATA_PUB_VARIABLE.getType(), "*");


        return authorities;
    }

    /**
     * 获取所有的 公共接口  转换成Authority  用于赋予所有权限
     */
    public List<Authority> selectAllPubApisToAuthority(String apiName, String apiGroupName,
                                                       String startDate, String endDate,
                                                       String start, String size) {


        final Map<String, Object> pagedApis = apiService.pagedPubApiResource(apiName, apiGroupName,
                startDate, endDate, start, size);

        List<PubApiResource> resources = (List<PubApiResource>) pagedApis.get("data");

        List<Authority> authorities = paseDataResourceToAuthority(resources, ResourceType.DATA_PUB_API.getType(), "*");


        return authorities;
    }


    /**
     * 获取所有的 规则库  转换成Authority 用于赋予所有权限
     */
    public List<Authority> selectAllPubruleSetToAuthority(String ruleSetName, String ruleSetGroupName,
                                                          String startDate, String endDate,
                                                          String start, String length) {


        final Map<String, Object> pagedRuleSets = ruleSetBaseService.getHeaderListResource(ruleSetName, ruleSetGroupName,
                startDate, endDate, start, length);

        List<PubRuleSetResource> resources = (List<PubRuleSetResource>) pagedRuleSets.get("data");

        List<Authority> authorities = paseDataResourceToAuthority(resources, ResourceType.DATA_PUB_RULE_SET.getType(), "*");

        return authorities;
    }


    /**
     * 获取所有的 模型库  转换成Authority 用于赋予所有权限
     */
    public List<Authority> selectAllPubModelBasesToAuthority(String ruleName, String ruleType, String modelGroupName,
                                                             String startDate, String endDate,
                                                             String start, String length) {
        final Map<String, Object> pagedModelBases = ruleDetailService.getHeaderListResource(ruleName, ruleType, modelGroupName,
                startDate, endDate, start, length);

        List<PubModelBaseResource> resources = (List<PubModelBaseResource>) pagedModelBases.get("data");

        List<Authority> authorities = paseDataResourceToAuthority(resources, ResourceType.DATA_PUB_MODEL.getType(), "*");

        return authorities;
    }

    /**
     * 获取所有的 数据源  转换成Authority 用于赋予所有权限
     */
    public List<Authority> selectAllDataSourcesToAuthority(String dbAlias,
                                                           String dbIp,
                                                           String dbType,
                                                           String start, String length) {
        final Map<String, Object> pagedDataSources = dataSourceService.pagedDataSource(dbAlias, dbIp,
                dbType, start, length);

        List<DataSourceResource> resources = (List<DataSourceResource>) pagedDataSources.get("data");

        List<Authority> authorities = paseDataResourceToAuthority(resources,
                ResourceType.DATA_DATASOURCE.getType(), "*");

        return authorities;
    }

    /**
     * 获取所有的 离线任务  转换成Authority 用于赋予所有权限
     */
    public List<Authority> selectAllTasksToAuthority(String taskName, String packageName,
                                                     String ruleName, String ruleVersion,
                                                     String start, String length) {
        // 分页获取数据
        Map<String, Object> pagedRuleTaskResource = ruleTaskService.pagedRuleTaskResource(
                taskName, packageName,
                ruleName, ruleVersion,
                start, length
        );

        List<RuleTaskResource> resources = (List<RuleTaskResource>) pagedRuleTaskResource.get("data");

        List<Authority> authorities = paseDataResourceToAuthority(resources,
                ResourceType.DATA_TASK.getType(), "*");

        return authorities;
    }

    /**
     * 将 DataResource 转化为  Authority  用于权限管理
     * 注 Authority 仅仅包括 ResourceId  ResourceExpression=*   ResourceTypeId
     */
    public List<Authority> paseDataResourceToAuthority(List<? extends DataResource> resources, String resourceType, String esourceExpression) {

        if (resources == null || resources.isEmpty()) {
            return Collections.emptyList();
        }
        List<Authority> needInsertAuthorities = new ArrayList<>();
        for (DataResource dataResource : resources) {
            if (dataResource != null && dataResource.getResourceId() != null && !dataResource.getResourceId().isEmpty()) {
                Authority authority = new Authority();
                authority.setResourceId(dataResource.getResourceId());
                authority.setResourceExpression(esourceExpression);
                authority.setResourceTypeId(resourceType);
                needInsertAuthorities.add(authority);
            }
        }

        return needInsertAuthorities;
    }


}
