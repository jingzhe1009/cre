package com.bonc.frame.dao.task;

import com.bonc.frame.entity.auth.resource.RuleTaskResource;
import com.bonc.frame.entity.task.RuleTask;
import com.bonc.frame.security.ResourceType;
import com.bonc.frame.security.aop.PermissibleData;

import java.util.List;
import java.util.Map;

/**
 * @author yedunyao
 * @since 2020/3/17 17:31
 */
public interface RuleTaskMapper {

//    @PermissibleData(value = "TASK_ID", requiresPermission = "/task/view",
//            resourceType = ResourceType.DATA_TASK, isPageHelper = true)
    List<RuleTask> getRuleTaskList(Map param);
//
//    @PermissibleData(value = "\"taskId\"", requiresPermission = "/task/view",
//            resourceType = ResourceType.DATA_TASK, isPageHelper = true)
    List<RuleTaskResource> pagedRuleTaskResource(Map param);






}
