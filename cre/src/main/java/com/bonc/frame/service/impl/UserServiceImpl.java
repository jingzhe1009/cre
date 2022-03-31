package com.bonc.frame.service.impl;

import com.bonc.frame.dao.DaoHelper;
import com.bonc.frame.entity.auth.*;
import com.bonc.frame.entity.user.UserAccountEn;
import com.bonc.frame.entity.user.UserExt;
import com.bonc.frame.service.UserService;
import com.bonc.frame.service.auth.AuthorityService;
import com.bonc.frame.service.auth.SubjectService;
import com.bonc.frame.util.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.data.Id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scala.util.parsing.combinator.testing.Str;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

/**
 * @author 作者：limf
 * @version 版本： 1.0
 * 说明：
 * @date 创建时间：2018年1月26日 下午3:26:28
 */

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private DaoHelper daoHelper;

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private SubjectService subjectService;

    private final String _MYBITSID_PREFIX = "com.bonc.frame.mapper.oracle.user.UserMapper.";

    private final String _MIDDLETABLE_PREFIX = "com.bonc.frame.mapper.auth.MiddleTableMapper.";
    private final String _DEPT_PREFIX = "com.bonc.frame.mapper.auth.DeptMapper.";

    @Override
    public UserAccountEn queryUserIdAndPassword(String userId, String password) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", userId);
        params.put("password", password);
        UserAccountEn userAccountEn = (UserAccountEn) daoHelper.queryOne(_MYBITSID_PREFIX + "queryByUserIdPwd", params);
        return userAccountEn;
    }

    @Transactional
    public int updateLoginDate(String userName, String loginDate) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("username", userName);
        params.put("loginDate", loginDate);
        daoHelper.update(_MYBITSID_PREFIX + "updateLoginDate", params);
        return 0;
    }

    @Transactional
    public int updatePassword(String userName, String password, String newPassword) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("username", userName);
        params.put("password", password);
        params.put("newPassword", newPassword);

        daoHelper.update(_MYBITSID_PREFIX + "updatePassword", params);
        return 0;
    }

    @Override
    public UserAccountEn queryInfoByUsername(String userName) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("username", userName);
        UserAccountEn userAccountEn = (UserAccountEn) daoHelper.queryOne(_MYBITSID_PREFIX + "queryInfoByUsername", params);
        return userAccountEn;
    }

    @Override
    public Map findUserInfoByUserId(String userId) throws Exception {
        Map userAccountEn = (HashMap) daoHelper.queryOne(_MYBITSID_PREFIX + "queryInfoByUserId", userId);
        return userAccountEn;
    }

    private List<UserAccountEn> findByUsername(String userName) {
        List<UserAccountEn> userAccountEns = daoHelper.queryForList(_MYBITSID_PREFIX + "findByUsername", userName);
        return userAccountEns;
    }

    private UserAccountEn findBy2UserId(String userId) {
        UserAccountEn userAccountEns = (UserAccountEn) daoHelper.queryOne(_MYBITSID_PREFIX + "findBy2UserId", userId);
        return userAccountEns;
    }

    @Override
    public Map<String, Object> getUserList(String userName, String start, String size) {
        Map<String, String> param = new HashMap<>();
        param.put("userName", userName);
        Map<String, Object> result = daoHelper.queryForPageList(_MYBITSID_PREFIX + "getUserList", param, start, size);
        return result;
    }

    //新增用户
    @Override
    @Transactional
    public ResponseResult createUser(UserExt user, String loginUserId) {
        final UserAccountEn userAccountEn = findBy2UserId(user.getUserId());
        if (userAccountEn != null) {
            return ResponseResult.createFailInfo("用户已存在");
        }
        if (user.getChannelId().isEmpty()) {
            return ResponseResult.createFailInfo("未选择渠道");
        }

        user.setCreateDate(new Date());
        user.setCreatePerson(loginUserId);
        UserAccountEn userBase = new UserAccountEn(user);
        daoHelper.insert(_MYBITSID_PREFIX + "createUser", userBase);
        String userId = user.getUserId();
        String deptStr = user.getDeptId();
        String groupStr = user.getGroupId();
        String roleStr = user.getRoleId();
        String channelStr=user.getChannelId();
        saveMiddleTable(groupStr, "insertBatchUserGroup", userId, UserGroup.class);
        saveMiddleTable(roleStr, "insertBatchUserRole", userId, UserRole.class);
        saveMiddleTable(deptStr, "insertBatchUserDept", userId, UserDept.class);
        saveMiddleTable(channelStr,"insertBatchUserChannel",userId,UserChannel.class);


        // 创建主体
        subjectService.createUserSubject(userId);

        ResponseResult result = ResponseResult.createSuccessInfo();
        return result;
    }

    private void saveMiddleTable(String str, String mybatisId, String userId, Class type) {
        if (StringUtils.isNotEmpty(str)) {
            List<String> idList = str2ListUtil(str);
            List<Object> newList = new ArrayList<>();
            for (String s : idList) {
                Object o = null;
                if (UserDept.class == type) {
                    o = new UserDept(IdUtil.createId(), userId, s);
                } else if (UserRole.class == type) {
                    o = new UserRole(IdUtil.createId(), userId, s);
                } else if (UserGroup.class == type) {
                    o = new UserGroup(IdUtil.createId(), userId, s);
                } else if (UserChannel.class == type){
                    o =new UserChannel(IdUtil.createId(), userId, s);
                }
                newList.add(o);
            }
            if (newList.size() > 0) {
                daoHelper.insert(_MIDDLETABLE_PREFIX + mybatisId, newList);
            }
        }
    }

    private void saveMiddleTable2(List<String> ids, String mybatisId, String userId, Class type) {
        if (ids.size() > 0) {
            List<Object> newList = new ArrayList<>();
            for (String s : ids) {
                Object o = null;
                if (UserDept.class == type) {
                    o = new UserDept(IdUtil.createId(), userId, s);
                } else if (UserRole.class == type) {
                    o = new UserRole(IdUtil.createId(), userId, s);
                } else if (UserGroup.class == type) {
                    o = new UserGroup(IdUtil.createId(), userId, s);
                } else if (UserChannel.class == type){
                    o = new UserChannel(IdUtil.createId(), userId, s);
                }
                newList.add(o);
            }
            if (newList.size() > 0) {
                daoHelper.insert(_MIDDLETABLE_PREFIX + mybatisId, newList);
            }
        }
    }

    private void saveMiddleTable3(List<String> ids, String mybatisId, String otherId, Class type) {
        if (ids.size() > 0) {
            List<Object> newList = new ArrayList<>();
            for (String s : ids) {
                Object o = null;
                if (UserDept.class == type) {
                    o = new UserDept(IdUtil.createId(), s, otherId);
                } else if (UserRole.class == type) {
                    o = new UserRole(IdUtil.createId(), s, otherId);
                } else if (UserGroup.class == type) {
                    o = new UserGroup(IdUtil.createId(), s, otherId);
                } else if (UserChannel.class == type){
                    o = new UserChannel(IdUtil.createId(), s, otherId);
                }
                newList.add(o);
            }
            if (newList.size() > 0) {
                daoHelper.insert(_MIDDLETABLE_PREFIX + mybatisId, newList);
            }
        }
    }

    private void deleteMiddleTable(String mybatisId, String userId) {
        daoHelper.delete(_MIDDLETABLE_PREFIX + mybatisId, userId);
    }

    private void deleteMiddleTable2(String mybatisId, String otherId) {
        daoHelper.delete(_MIDDLETABLE_PREFIX + mybatisId, otherId);
    }

    private <T extends UserMiddle> void saveMiddleTableByList(List<T> list, String mybatisId, String userId, Class type) {
        if (list == null) {
            return;
        }
        List<Object> newList = new ArrayList<>();
        for (T u : list) {
            if (UserDept.class == type) {
                UserDept userDept = new UserDept(IdUtil.createId(), userId, u.getDeptId());
                newList.add(userDept);
            } else if (UserRole.class == type) {
                UserRole userRole = new UserRole(IdUtil.createId(), userId, u.getRoleId());
                newList.add(userRole);
            } else if (UserGroup.class == type) {
                UserGroup userGroup = new UserGroup(IdUtil.createId(), userId, u.getGroupId());
                newList.add(userGroup);
            } else if (UserChannel.class == type){
                UserChannel userChannel = new UserChannel(IdUtil.createId(), userId, u.getChannelId());
                newList.add(userChannel);
            }
        }
        if (newList.size() > 0) {
            daoHelper.insert(_MIDDLETABLE_PREFIX + mybatisId, newList);
        }
    }

    private UserAccountEn findByUserId(String userId) {
        UserAccountEn userAccountEn = (UserAccountEn) daoHelper.queryOne(_MYBITSID_PREFIX + "findByUserId", userId);
        return userAccountEn;
    }

    //修改用户
    @Override
    @Transactional
    public ResponseResult updateUser(UserAccountEn user, String loginUserId) {
        user.setUpdateDate(new Date());
        user.setUpdatePerson(loginUserId);
        UserAccountEn users = findByUserId(user.getUserId());
        if (StringUtils.isNotBlank(user.getUserPassword()) && !users.getUserPassword().equals(user.getUserPassword())) {
            try {
                user.setUserPassword(MD5Util.Bit32(user.getUserPassword()));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        daoHelper.update(_MYBITSID_PREFIX + "updateUser", user);
        String userId = user.getUserId();
//        deleteMiddleTable("deleteByUserIdRole", userId);
        deleteMiddleTable("deleteByUserIdGroup", userId);
//        deleteMiddleTable("deleteByUserIdDept", userId);
//        deleteMiddleTable("deleteByUserIdChannel", userId);
        List<Role> roleList = user.getRoleList();
        List<Group> groupList = user.getGroupList();
        List<Dept> deptList = user.getDeptList();
        List<Channel> channelList=user.getChannelList();
        saveMiddleTableByList(groupList, "insertBatchUserGroup", userId, UserGroup.class);
//        saveMiddleTableByList(roleList, "insertBatchUserRole", userId, UserRole.class);
//        saveMiddleTableByList(deptList, "insertBatchUserDept", userId, UserDept.class);
//        saveMiddleTableByList(channelList, "insertBatchUserChannel", userId, UserChannel.class);
        ResponseResult result = ResponseResult.createSuccessInfo();
        return result;
    }

    @Override
    @Transactional
    public ResponseResult deleteUser(String userId) {
        // 删除主体
        subjectService.deleteUserSubject(userId);
        deleteMiddleTable("deleteByUserIdRole", userId);
        deleteMiddleTable("deleteByUserIdGroup", userId);
        deleteMiddleTable("deleteByUserIdDept", userId);
//        deleteMiddleTable("deleteByUserIdChannel", userId);
        daoHelper.delete(_MYBITSID_PREFIX + "deleteUser", userId);
        ResponseResult result = ResponseResult.createSuccessInfo();
        return result;
    }

    @Override
    public Map<String, Object> list(String userId, String userName, String jobNumber, String start, String size) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("userName", userName);
        params.put("jobNumber", jobNumber);
        // 除超级管理员外，只显示当前角色下的用户
        if (authorityService.isCurrentUserHasAllPermits()) {
            Map<String, Object> map = daoHelper.queryForPageList(_MYBITSID_PREFIX + "selectUser", params, start, size);
            return map;
        }
        String currentUser = ControllerUtil.getCurrentUser();
        params.put("currentUser", currentUser);
        Map<String, Object> map = daoHelper.queryForPageList(_MYBITSID_PREFIX + "selectUser", params, start, size);
        return map;
    }

    @Override
    @Transactional
    public Map<String, Object> user2Role(String userId, String roleName, String start, String length) {
        Map<String, Object> param = new HashMap<>();
        param.put("userId", userId);
        param.put("roleName", roleName);
        Map<String, Object> result = daoHelper.queryForPageList(_MIDDLETABLE_PREFIX + "listRoleForUserHad", param, start, length);
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> userRole(String userId, String roleName, String start, String length) {
        Map<String, Object> param = new HashMap<>();
        param.put("userId", userId);
        param.put("roleName", roleName);
        Map<String, Object> result = daoHelper.queryForPageList(_MIDDLETABLE_PREFIX + "listRoleForUserNotHad", param, start, length);
        return result;
    }

    @Override
    @Transactional
    public ResponseResult userAddRole(List<String> roleIds, String userId) {
        deleteMiddleTable("deleteByUserIdRole", userId);
        saveMiddleTable2(roleIds, "insertBatchUserRole", userId, UserRole.class);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    public Map<String, Object> user2Group(String userId, String groupName, String start, String length) {
        Map<String, Object> param = new HashMap<>();
        param.put("userId", userId);
        param.put("groupName", groupName);
        Map<String, Object> result = daoHelper.queryForPageList(_MIDDLETABLE_PREFIX + "listGroupForUserHad", param, start, length);
        return result;
    }

    @Override
    public Map<String, Object> userGroup(String userId, String groupName, String start, String length) {
        Map<String, Object> param = new HashMap<>();
        param.put("userId", userId);
        param.put("groupName", groupName);
        Map<String, Object> result = daoHelper.queryForPageList(_MIDDLETABLE_PREFIX + "listGroupForUserNotHad", param, start, length);
        return result;
    }

    @Override
    public ResponseResult userAddGroup(List<String> groupIds, String userId) {
        deleteMiddleTable("deleteByUserIdGroup", userId);
        saveMiddleTable2(groupIds, "insertBatchUserGroup", userId, UserRole.class);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    public ResponseResult userAddDept(List<String> deptIds, String userId) {
        deleteMiddleTable("deleteByUserIdDept", userId);
        saveMiddleTable2(deptIds, "insertBatchUserDept", userId, UserDept.class);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional
    public Map<String, Object> role2User(String roleId, String userName, String start, String length) {
        Map<String, Object> param = new HashMap<>();
        param.put("roleId", roleId);
        param.put("userName", userName);
        Map<String, Object> result = daoHelper.queryForPageList(_MIDDLETABLE_PREFIX + "listUserForRoleHad", param, start, length);
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> roleUser(String roleId, String userName, String start, String length) {
        Map<String, Object> param = new HashMap<>();
        param.put("roleId", roleId);
        param.put("userName", userName);
        Map<String, Object> result = daoHelper.queryForPageList(_MIDDLETABLE_PREFIX + "listUserForRoleNotHad", param, start, length);
        return result;
    }

    @Override
    @Transactional
    public ResponseResult roleAddUser(List<String> userIds, String roleId) {
        deleteMiddleTable2("deleteByRoleIdUser", roleId);
        saveMiddleTable3(userIds, "insertBatchUserRole", roleId, UserRole.class);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional
    public Map<String, Object> group2User(String groupId, String userName, String start, String length) {
        Map<String, Object> param = new HashMap<>();
        param.put("groupId", groupId);
        param.put("userName", userName);
        Map<String, Object> result = daoHelper.queryForPageList(_MIDDLETABLE_PREFIX + "listUserForGroupHad", param, start, length);
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> groupUser(String groupId, String userName, String start, String length) {
        Map<String, Object> param = new HashMap<>();
        param.put("groupId", groupId);
        param.put("userName", userName);
        Map<String, Object> result = daoHelper.queryForPageList(_MIDDLETABLE_PREFIX + "listUserForGroupNotHad", param, start, length);
        return result;
    }

    @Override
    @Transactional
    public ResponseResult groupAddUser(List<String> userIds, String groupId) {
        deleteMiddleTable2("deleteByGroupIdUser", groupId);
        saveMiddleTable3(userIds, "insertBatchUserGroup", groupId, UserGroup.class);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    public Map<String, Object> dept2User(String deptId, String userName, String start, String length) {
        Map<String, Object> param = new HashMap<>();
        param.put("deptId", deptId);
        param.put("userName", userName);
        Map<String, Object> result = daoHelper.queryForPageList(_MIDDLETABLE_PREFIX + "listUserForDeptHad", param, start, length);
        return result;
    }

    @Override
    public Map<String, Object> deptUser(String deptId, String userName, String start, String length) {
        Map<String, Object> param = new HashMap<>();
        param.put("deptId", deptId);
        param.put("userName", userName);
        Map<String, Object> result = daoHelper.queryForPageList(_MIDDLETABLE_PREFIX + "listUserForDeptNotHad", param, start, length);
        return result;
    }

    @Override
    public ResponseResult deptAddUser(List<String> userIds, String deptId) {
        deleteMiddleTable2("deleteByDeptIdUser", deptId);
        saveMiddleTable3(userIds, "insertBatchUserDept", deptId, UserDept.class);
        return ResponseResult.createSuccessInfo();
    }

    /**
     * 新建用户是绑定渠道
     * @param channelId 渠道id
     * @param userId 用户id
     * @return
     */
    @Override
    public ResponseResult userAddChannel(String channelId, String userId) {
        // 校验是不是渠道
        List<Channel> channel = daoHelper.queryForList(_DEPT_PREFIX + "selectChannelById", channelId);
        if (CollectionUtil.isEmpty(channel)) {
            return ResponseResult.createFailInfo("未获取到渠道数据，请重新选择");
        }
        // 建立关联 userChannel表
        String id = IdUtil.createId();
        UserChannel userChannel = new UserChannel(id, userId, channelId);
        // 移除原来的关联
        daoHelper.delete(_DEPT_PREFIX + "userRemoveChannel", userId);
        daoHelper.insert(_DEPT_PREFIX + "userAddChannel", userChannel);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    public ResponseResult channelAddUser(List<String> userIds, String channelId) {
        deleteMiddleTable2("deleteByChannelIdUser", channelId);
        saveMiddleTable3(userIds, "insertBatchUserChannel", channelId, UserChannel.class);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    public Map<String, Object> channel2User(String channelId, String userName, String start, String length) {
        Map<String, Object> param = new HashMap<>();
        param.put("channelId", channelId);
        param.put("userName", userName);
        Map<String, Object> result = daoHelper.queryForPageList(_MIDDLETABLE_PREFIX + "listUserForChannelHad", param, start, length);
        return result;
    }

    @Override
    public Map<String, Object> channelUser(String channelId, String userName, String start, String length) {
        Map<String, Object> param = new HashMap<>();
        param.put("channelId", channelId);
        param.put("userName", userName);
        Map<String, Object> result = daoHelper.queryForPageList(_MIDDLETABLE_PREFIX + "listUserForChannelNotHad", param, start, length);
        return result;
    }
    @Override
    @Transactional
    public Set<String> findRoles(String username) {
        return null;
    }

    @Override
    @Transactional
    public Set<String> findPermissions(String username) {
        return null;
    }


    /**
     * 将（a,b,c）转为list
     *
     * @param str
     * @return
     */
    private List<String> str2ListUtil(String str) {
        String[] newStrs = str.split(",");
        List<String> list = Arrays.asList(newStrs);
        return list;
    }

}

