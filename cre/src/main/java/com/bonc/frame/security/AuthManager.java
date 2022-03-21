package com.bonc.frame.security;

import com.bonc.frame.entity.auth.Subject;

import java.util.List;

/**
 * @author yedunyao
 * @date 2019/7/28 11:43
 */
public interface AuthManager {

    List<Subject> findSubjects(String userId);

    List<Subject> findSubjects(String userId, SubjectType subjectType);

    List<String> findAllPermissions(List<String> subjects);

    /**
     * 根据主体Id和资源类型进行过滤获取有权限的资源列表
     *
     * @param subjects     主体Id列表
     * @param resourceType 资源类型
     * @return 有权限的资源列表
     */
    List<String> findAllPermissions(List<String> subjects, ResourceType resourceType);

    List<String> findAllPermissionsBySubjects(List<Subject> subjects, ResourceType resourceType);

    boolean isPermitted(String requiresPermission, List<String> permissions);

    boolean[] isPermitted(String[] requiresPermissions, List<String> permissions);

    /**
     * @param requiresPermissions 需要的权限
     * @param permissions         允许的权限
     * @throws MyAuthorizationException
     */
    void checkPermissions(String[] requiresPermissions, List<String> permissions) throws MyAuthorizationException;


}
