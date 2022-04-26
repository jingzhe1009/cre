package com.bonc.frame.dao.api;

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
public interface ApiMapper {

//    @PermissibleData(value = "API_ID", requiresPermission = "/pub/api/view",
//            resourceType = ResourceType.DATA_PUB_API, isPageHelper = true)
    List<ApiConfGroup> pubGetApiList(Map map);
//
//    @PermissibleData(value = "API_ID", requiresPermission = "/pub/api/view",
//            resourceType = ResourceType.DATA_PUB_API)
    List<ApiConf> pubGetAllApiList(Map map);
//
//    @PermissibleData(value = "\"apiId\"", requiresPermission = "/pub/api/view",
//            resourceType = ResourceType.DATA_PUB_API, isPageHelper = true)
    List<PubApiResource> pagedPubApiResource(Map map);

}
