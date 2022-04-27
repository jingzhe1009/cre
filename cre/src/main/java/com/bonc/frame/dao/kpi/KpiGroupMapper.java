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
public interface KpiGroupMapper {

//    @PermissibleData(value = "KPI_GROUP_ID", requiresPermission = "/kpi/group/view",
//            resourceType = ResourceType.DATA_KPI_GROUP, isPageHelper = true)
    List<KpiDefinition> getByGroupName(Map map);

}
