package com.bonc.frame.service.impl.auth;

import com.bonc.frame.dao.DaoHelper;
import com.bonc.frame.entity.auth.resource.Resource;
import com.bonc.frame.security.ResourceType;
import com.bonc.frame.service.auth.ResourceService;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yedunyao
 * @date 2019/8/14 15:38
 */
@Service
public class ResourceServiceImpl implements ResourceService {

    @Autowired
    private DaoHelper daoHelper;

    private final String _MYBITSID_PREFIX = "com.bonc.frame.mapper.auth.ResourceMapper.";

    @Override
    public List<Resource> getMenuResources() {
        List<String> types = ImmutableList.of(
                ResourceType.MENU.getType(),
                ResourceType.BUTTON.getType()
        );
        final List<Resource> menuResources = getResourcesByResourceTypes(types);
//        final List<Resource> resources = Resource.parse2Tree(menuResources);
        return menuResources;
    }

    @Override
    public List<Resource> getModelResources() {
        final List<Resource> resources = getResourcesByResourceType(ResourceType.DATA_MODEL.getType());
        return resources;
    }

    @Override
    public List<Resource> getMetadataResources() {
        final List<Resource> resources = getResourcesByResourceType(ResourceType.DATA_METADATA.getType());
        return resources;
    }

    @Override
    public List<Resource> getResourcesByResourceType(String resourceTypeId) {
        if (StringUtils.isBlank(resourceTypeId)) {
            throw new IllegalArgumentException("参数[resourceTypeId]不能为空");
        }
        List<Resource> resources = daoHelper.queryForList(_MYBITSID_PREFIX +
                "getResourcesByResourceType", resourceTypeId);
        return resources;
    }

    @Override
    public List<Resource> getResourcesByResourceTypes(List<String> resourceTypeIds) {
        if (resourceTypeIds == null) {
            throw new IllegalArgumentException("参数[resourceTypeIds]不能为空");
        }
        List<Resource> resources = daoHelper.queryForList(_MYBITSID_PREFIX +
                "getResourcesByResourceTypes", resourceTypeIds);
        return resources;
    }

}
