package com.bonc.frame.security;

import com.bonc.frame.entity.auth.Subject;

import java.util.List;

/**
 * @author yedunyao
 * @date 2019/7/28 18:44
 */
//@Component
public class AuthManagerImpl implements AuthManager {

//    @Autowired
//    private Service service;
//    ...

    @Override
    public List<Subject> findSubjects(String userId) {
        return null;
    }

    @Override
    public List<Subject> findSubjects(String userId, SubjectType subjectType) {
        return null;
    }

    @Override
    public List<String> findAllPermissions(List<String> subjects) {
        return null;
    }

    @Override
    public List<String> findAllPermissions(List<String> subjects, ResourceType resourceType) {
        return null;
    }

    @Override
    public List<String> findAllPermissionsBySubjects(List<Subject> subjects, ResourceType resourceType) {
        return null;
    }

    @Override
    public boolean isPermitted(String requiresPermission, List<String> permissions) {
        return true;
    }

    @Override
    public boolean[] isPermitted(String[] requiresPermissions, List<String> permissions) {
        return new boolean[0];
    }

    @Override
    public void checkPermissions(String[] requiresPermissions, List<String> permissions) throws MyAuthorizationException {
        if (false) {
            throw new MyAuthorizationException("没有权限");
        }
    }
}
