package com.bonc.frame.service.auth;

public interface DeptPathService {
    void save(String childId, String parentId);

    void delete(String deptId);
}
