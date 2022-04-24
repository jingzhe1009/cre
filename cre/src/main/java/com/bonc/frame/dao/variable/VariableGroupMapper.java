package com.bonc.frame.dao.variable;

import com.bonc.frame.entity.commonresource.VariableGroupExt;
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
public interface VariableGroupMapper {


    @PermissibleData(value = "VARIABLE_GROUP_ID", requiresPermission = "/pub/variableGroup/view",
            resourceType = ResourceType.DATA_PUB_VARIABLE_GROUP, isPageHelper = true)
    List<VariableGroupExt> select(Map map);


}
