package com.bonc.frame.dao.resource;

import com.bonc.frame.entity.api.ApiConf;
import com.bonc.frame.entity.api.ApiConfGroup;
import com.bonc.frame.entity.auth.resource.PubApiResource;
import com.bonc.frame.entity.commonresource.RuleSetHeaderGroupExt;
import com.bonc.frame.security.ResourceType;
import com.bonc.frame.security.aop.PermissibleData;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author yedunyao
 * @since 2020/3/11 0:24
 */
@Mapper
@Repository
public interface RuleSetMapper {

//    @PermissibleData(value = "RULE_SET_HEADER_ID", requiresPermission = "/pub/ruleSet/view",
//            resourceType = ResourceType.DATA_PUB_RULE_SET, isPageHelper = true)
    List<RuleSetHeaderGroupExt> getHeaderList(Map map);
//
//    @PermissibleData(value = "\"ruleSetHeaderId\"", requiresPermission = "/pub/ruleSet/view",
//            resourceType = ResourceType.DATA_PUB_RULE_SET, isPageHelper = true)
    List<PubApiResource> getHeaderListResource(Map map);

}
