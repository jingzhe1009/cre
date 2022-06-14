package com.bonc.frame.dao.modelGroup;

import com.bonc.frame.entity.commonresource.ModelHeaderGroupExt;
import com.bonc.frame.security.ResourceType;
import com.bonc.frame.security.aop.PermissibleData;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface ModelGroupMapper {

//    @PermissibleData(value = "MODEL_GROUP_ID", requiresPermission = "/pub/modelGroup/view",
//            resourceType = ResourceType.DATA_PUB_MODEL_GROUP, isPageHelper = true)
//    List<ModelHeaderGroupExt> getByGroupName(Map map);
}
