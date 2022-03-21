package com.bonc.frame.dao.variable;

import com.bonc.frame.entity.auth.resource.PubVariableResource;
import com.bonc.frame.entity.commonresource.VariableGroupExt;
import com.bonc.frame.entity.variable.Variable;
import com.bonc.frame.entity.variable.VariableExt;
import com.bonc.frame.security.ResourceType;
import com.bonc.frame.security.aop.PermissibleData;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author yedunyao
 * @since 2020/3/10 15:34
 */
@Mapper
@Repository
public interface VariableMapper {

    @PermissibleData(value = "VARIABLE_ID", requiresPermission = "/pub/variable/view",
            resourceType = ResourceType.DATA_PUB_VARIABLE, isPageHelper = true)
    List<VariableExt> pubSelectVariables(Map map);

    @PermissibleData(value = "VARIABLE_ID", requiresPermission = "/pub/variable/view",
            resourceType = ResourceType.DATA_PUB_VARIABLE, isPageHelper = true)
    List<VariableGroupExt> pubSelectFlatVariables(Map map);

    @PermissibleData(value = "\"variableId\"", requiresPermission = "/pub/variable/view",
            resourceType = ResourceType.DATA_PUB_VARIABLE, isPageHelper = true)
    List<PubVariableResource> pagedPubVariableResources(Map map);

    @PermissibleData(value = "VARIABLE_ID", requiresPermission = "/pub/variable/view",
            resourceType = ResourceType.DATA_PUB_VARIABLE)
    List<Variable> selectPubByVariableGroupIdIsNull();

    @PermissibleData(value = "VARIABLE_ID", requiresPermission = "/pub/variable/view",
            resourceType = ResourceType.DATA_PUB_VARIABLE)
    List<Map<String, String>> selectPubEntityByVariableGroupIdIsNull2();

    @PermissibleData(value = "\"variableId\"", requiresPermission = "/pub/variable/view",
            resourceType = ResourceType.DATA_PUB_VARIABLE)
    List<Map<String, String>> selectPubEntityRefByVariableGroupIdIsNull();

    @PermissibleData(value = "VARIABLE_ID", requiresPermission = "/pub/variable/view",
            resourceType = ResourceType.DATA_PUB_VARIABLE)
    List<Variable> pubSelectVariablesByGroupId(String variableGroupId);

    @PermissibleData(value = "VARIABLE_ID", requiresPermission = "/pub/variable/view",
            resourceType = ResourceType.DATA_PUB_VARIABLE)
    List<Map<String, String>> selectEntityByVariableGroupId2(String variableGroupId);

    @PermissibleData(value = "\"variableId\"", requiresPermission = "/pub/variable/view",
            resourceType = ResourceType.DATA_PUB_VARIABLE)
    List<Map<String, String>> selectEntityRefByVariableGroupId(String variableGroupId);

}
