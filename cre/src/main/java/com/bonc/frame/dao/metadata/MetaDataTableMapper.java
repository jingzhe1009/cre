package com.bonc.frame.dao.metadata;

import com.bonc.frame.entity.auth.resource.MetadataResource;
import com.bonc.frame.security.ResourceType;
import com.bonc.frame.security.aop.PermissibleData;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author yedunyao
 * @date 2019/8/19 20:46
 */
@Mapper
@Repository
public interface MetaDataTableMapper {

    @PermissibleData(value = "TABLEID", requiresPermission = "/metaTable/view", resourceType = ResourceType.DATA_METADATA)
    List<Map<String, Object>> findMetaTable(Map<String, String> param);

    @PermissibleData(value = "TABLEID", requiresPermission = "/metaTable/view", resourceType = ResourceType.DATA_METADATA)
    List<Map<String, Object>> findMetaTableByPage(Map<String, String> param);

    @PermissibleData(value = "\"tableId\"", requiresPermission = "/metaTable/view",
            resourceType = ResourceType.DATA_METADATA, isPageHelper = true)
    List<MetadataResource> pagedMetaTablesByDsNameTableNamePackageName(Map<String, String> param);

}
