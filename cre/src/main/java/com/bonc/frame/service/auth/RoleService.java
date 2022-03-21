package com.bonc.frame.service.auth;

import com.bonc.frame.entity.auth.Role;
import com.bonc.frame.util.ResponseResult;

import java.util.List;
import java.util.Map;

public interface RoleService {
    ResponseResult save(Role role, String loginUserId);

    Map<String, Object> list(String roleName, String start, String size);

    List<Role> getRoleByUser(String user);

    List<Role> getRoleByroot();

    ResponseResult delete(String roleId);

    ResponseResult update(Role role, String loginUserId);
}
