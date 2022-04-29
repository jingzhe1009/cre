package com.bonc.frame.dao.apiGroup;

import com.bonc.frame.entity.api.ApiConf;
import com.bonc.frame.entity.api.ApiConfGroup;
import com.bonc.frame.entity.auth.resource.PubApiResource;
import com.bonc.frame.security.ResourceType;
import com.bonc.frame.security.aop.PermissibleData;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author yedunyao
 * @since 2020/3/10 18:34
 */
@Mapper
@Repository
public interface ApiGroupMapper {

//    @PermissibleData(value = "API_GROUP_ID", requiresPermission = "/pub/apiGroup/view",
//            resourceType = ResourceType.DATA_PUB_API_GROUP, isPageHelper = true)
    List<ApiConfGroup> select(Map map);

}
