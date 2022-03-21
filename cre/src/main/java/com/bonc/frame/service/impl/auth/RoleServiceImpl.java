package com.bonc.frame.service.impl.auth;

import com.bonc.frame.dao.DaoHelper;
import com.bonc.frame.entity.auth.Role;
import com.bonc.frame.service.auth.RoleService;
import com.bonc.frame.service.auth.SubjectService;
import com.bonc.frame.util.IdUtil;
import com.bonc.frame.util.ResponseResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service("roleService")
public class RoleServiceImpl implements RoleService {
    private Log log = LogFactory.getLog(getClass());


    @Autowired
    private DaoHelper daoHelper;

    @Autowired
    private SubjectService subjectService;

    private final String _ROLE_PREFIX = "com.bonc.frame.mapper.auth.RoleMapper.";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult save(Role role, String loginUserId) {
        if (role == null) {
            return ResponseResult.createFailInfo("请求参数不能为null");
        }
        final Role oldRole = findByName(role.getRoleName());
        if (oldRole != null) {
            return ResponseResult.createFailInfo("角色已存在");
        }

        final String roleId = IdUtil.createId();
        role.setRoleId(roleId);
        role.setCreateDate(new Date());
        role.setCreatePerson(loginUserId);

        // 创建主体
        subjectService.createRoleSubject(roleId);

        daoHelper.insert(_ROLE_PREFIX + "insert", role);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    public Map<String, Object> list(String roleName, String start, String size) {
        Map<String, Object> param = new HashMap<>();
        param.put("roleName", roleName);
        Map<String, Object> result = daoHelper.queryForPageList(_ROLE_PREFIX + "listByRoleName", param, start, size);

        return result;
    }


    @Override
    public List<Role> getRoleByUser(String user) {
        Map<String, Object> param = new HashMap<>();
        param.put("user", user);
        List<Role> roles = daoHelper.queryForList(_ROLE_PREFIX + "getRoleByUser", param);
        if (roles != null) {
            return roles;
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public List<Role> getRoleByroot() {
        return getRoleByUser("root");
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult delete(String roleId) {
        if (roleId == null) {
            return ResponseResult.createFailInfo("请求参数[roleId]不能为null");
        }

        // 删除主体
        subjectService.deleteRoleSubject(roleId);

        daoHelper.delete(_ROLE_PREFIX + "deleteByPrimaryKey", roleId);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult update(Role role, String loginUserId) {
        if (role == null) {
            return ResponseResult.createFailInfo("请求参数不能为null");
        }
        if (!isExists(role)) {
            return ResponseResult.createFailInfo("角色不存在， roleId: " + role.getRoleId());
        }
        role.setUpdateDate(new Date());
        role.setUpdatePerson(loginUserId);
        daoHelper.update(_ROLE_PREFIX + "updateByPrimaryKeySelective", role);
        return ResponseResult.createSuccessInfo();
    }

    public boolean isExists(Role role) {
        final Role resultRole = selectByPrimaryKey(role.getRoleId());
        return resultRole != null;
    }

    public Role selectByPrimaryKey(String roleId) {
        return (Role) daoHelper.queryOne(_ROLE_PREFIX + "selectByPrimaryKey", roleId);
    }

    public Role findByName(String roleName) {
        return (Role) daoHelper.queryOne(_ROLE_PREFIX + "findByName", roleName);
    }
}
