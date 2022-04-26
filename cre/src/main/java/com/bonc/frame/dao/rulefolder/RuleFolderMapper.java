package com.bonc.frame.dao.rulefolder;

import com.bonc.frame.entity.auth.resource.RuleFolderResource;
import com.bonc.frame.security.ResourceType;
import com.bonc.frame.security.aop.PermissibleData;

import java.util.List;
import java.util.Map;

public interface RuleFolderMapper {

//	@PermissibleData(value = "\"key\"", requiresPermission = "/folder/view",
//			resourceType = ResourceType.DATA_FOLDER, isPageHelper = true)
	Map<String, Object> selectRuleFolder();
//
//    @PermissibleData(value = "\"folderId\"", requiresPermission = "/folder/view",
//            resourceType = ResourceType.DATA_FOLDER, isPageHelper = true)
    List<RuleFolderResource> pagedRuleFolder(Map map);
	
}