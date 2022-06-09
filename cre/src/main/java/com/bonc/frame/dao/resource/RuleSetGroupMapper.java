package com.bonc.frame.dao.resource;


import com.bonc.frame.entity.commonresource.ModelGroup;
import com.bonc.frame.entity.commonresource.RuleSetHeaderGroupExt;
import com.bonc.frame.security.ResourceType;
import com.bonc.frame.security.aop.PermissibleData;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author yedunyao
 * @since 2020/3/11 0:24
 */
@Mapper
@Repository
public interface RuleSetGroupMapper {

    @PermissibleData(value = "RULE_SET_GROUP_ID", requiresPermission = "/pub/ruleSetGroup/view",
            resourceType = ResourceType.DATA_PUB_RULE_SET_GROUP, isPageHelper = true)
    List<ModelGroup> getByGroupName(Map map);
}
