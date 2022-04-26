package com.bonc.frame.dao.datasource;

import com.bonc.frame.entity.auth.resource.DataSourceResource;
import com.bonc.frame.entity.datasource.DataSource;
import com.bonc.frame.security.ResourceType;
import com.bonc.frame.security.aop.PermissibleData;

import java.util.List;
import java.util.Map;

/**
 * @author yedunyao
 * @since 2020/3/17 17:31
 */
public interface DataSourceMapper {
//
//    @PermissibleData(value = "DB_ID", requiresPermission = "/datasource/view",
//            resourceType = ResourceType.DATA_DATASOURCE, isPageHelper = true)
    List<DataSource> findDataSourceByPage(String dbAlias);
//
//    @PermissibleData(value = "DBID", requiresPermission = "/datasource/view",
//            resourceType = ResourceType.DATA_DATASOURCE, isPageHelper = true)
    Map getDataSourceList();
//
//    @PermissibleData(value = "\"dbId\"", requiresPermission = "/datasource/view",
//            resourceType = ResourceType.DATA_DATASOURCE, isPageHelper = true)
    List<DataSourceResource> pagedDataSource(Map param);

}
