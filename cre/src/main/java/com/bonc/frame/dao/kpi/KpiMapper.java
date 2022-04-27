package com.bonc.frame.dao.kpi;

import com.bonc.frame.entity.auth.resource.KpiDefinitionResource;
import com.bonc.frame.entity.kpi.KpiDefinition;
import com.bonc.frame.security.ResourceType;
import com.bonc.frame.security.aop.PermissibleData;
import com.bonc.frame.security.offer.PermissibleDataOffer;

import java.util.List;
import java.util.Map;

/**
 * @author yedunyao
 * @since 2020/3/23 11:31
 */
public interface KpiMapper {

//    @PermissibleData(value = "\"kpiId\"", requiresPermission = "/kpi/view",
//            resourceType = ResourceType.DATA_KPI, isPageHelper = true)
    List<KpiDefinitionResource> pagedKpiBaseInfo(Map map);
//
//    @PermissibleDataOffer(value = "KPI_ID", isPageHelper = true)
//    @PermissibleData(value = "KPI_ID", requiresPermission = "/kpi/view",
//            resourceType = ResourceType.DATA_KPI, isPageHelper = true)
    List<KpiDefinition> getKpiBaseInfo(Map map);
//
//    @PermissibleData(value = "KPI_ID", requiresPermission = "/kpi/view",
//            resourceType = ResourceType.DATA_KPI, isPageHelper = true)
    List<KpiDefinition> getKpiBaseInfoByRuleId(Map map);
//
//    @PermissibleData(value = "KPI_ID", requiresPermission = "/kpi/view",
//            resourceType = ResourceType.DATA_KPI, isPageHelper = true)
    List<KpiDefinition> getKpiDetailBatch(List<String> list);


}
