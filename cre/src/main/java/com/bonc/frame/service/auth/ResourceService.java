package com.bonc.frame.service.auth;

import com.bonc.frame.entity.auth.resource.Resource;

import java.util.List;

/**
 * @author yedunyao
 * @date 2019/8/14 15:37
 */
public interface ResourceService {

    List<Resource> getMenuResources();

    List<Resource> getModelResources();

    List<Resource> getMetadataResources();

    List<Resource> getResourcesByResourceType(String resourceTypeId);

    List<Resource> getResourcesByResourceTypes(List<String> resourceTypeIds);

}
