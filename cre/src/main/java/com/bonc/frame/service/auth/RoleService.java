package com.bonc.frame.service.auth;

import com.bonc.frame.entity.auth.Role;
import com.bonc.frame.util.ResponseResult;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface RoleService {
    ResponseResult save(Role role, String loginUserId);

    Map<String, Object> list(String roleName, String start, String size);

    List<Role> getRoleByUser(String user);

    ResponseResult getRoleByUser2(String loginUserId);

    List<Role> getRoleByroot();

    ResponseResult delete(String roleId);

    ResponseResult update(Role role, String loginUserId);

    /**
     * 验证当前用户是否全权
     * @param loginUserId 用户id
     * @return 全权返回true
     */
    boolean checkAuthorityIsAll(String loginUserId);
}
