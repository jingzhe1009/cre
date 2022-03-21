package com.bonc.frame.dao.rule;

import com.bonc.frame.entity.auth.resource.PubModelBaseResource;
import com.bonc.frame.entity.auth.resource.RuleResource;
import com.bonc.frame.entity.rule.RuleDetail;
import com.bonc.frame.security.ResourceType;
import com.bonc.frame.security.aop.PermissibleData;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author yedunyao
 * @date 2019/7/29 17:52
 */
@Mapper
@Repository
public interface RuleDetailMapper {

    @PermissibleData(value = "RULE_NAME", requiresPermission = "/rule/view",
            resourceType = ResourceType.DATA_MODEL, isPageHelper = true)
    List<RuleDetail> selectRuleDetailByFolderIdOrRuleName(Map<String, Object> param);

    /**
     *
     * @param param  needDraft 如果不传,则不会查   可以传 folderId , ruleName , isPublic ,
     * @return
     */
    @PermissibleData(value = "RULE_NAME", requiresPermission = "/rule/view",
            resourceType = ResourceType.DATA_MODEL, isPageHelper = true)
    List<RuleDetail> selectAllVersionRuleDetailByFolderIdOrRuleName(Map<String, Object> param);

    @PermissibleData(value = "\"ruleName\"", requiresPermission = "/rule/view",
            resourceType = ResourceType.DATA_MODEL, isPageHelper = true)
    List<Map<String, Object>> selectRuleNameInHeaderByFolder(String folderId);

    @PermissibleData(value = "\"ruleName\"", requiresPermission = "/rule/view",
            resourceType = ResourceType.DATA_MODEL, isPageHelper = true)
    List<RuleResource> pagedByRuleNameFolderNameRuleType(Map map);

    @PermissibleData(value = "RULE_NAME", requiresPermission = "/pub/rule/view",
            resourceType = ResourceType.DATA_PUB_MODEL, isPageHelper = true)
    List<RuleDetail> getHeaderList(Map map);

    @PermissibleData(value = "\"ruleName\"", requiresPermission = "/pub/rule/view",
            resourceType = ResourceType.DATA_PUB_MODEL, isPageHelper = true)
    List<PubModelBaseResource> getHeaderListResource(Map map);

    /*@PermissibleData(value = "RULE_NAME", requiresPermission = "/rule/view",
            resourceType = ResourceType.DATA_MODEL, isPageHelper = true)
    Map getEnableVersionBaseInfoByRuleName(String ruleName);*/


}
