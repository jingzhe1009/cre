package com.bonc.frame.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bonc.frame.entity.user.UserAccountEn;
import com.bonc.frame.entity.user.UserExt;
import com.bonc.frame.util.ResponseResult;

/**
 * @author 作者：limf
 * @version 版本： 1.0
 * 说明：
 * @date 创建时间：2018年1月26日 下午3:25:44
 */
public interface UserService {

    //根据用户名和密码查询出一个用户
    public UserAccountEn queryUserIdAndPassword(String userName, String password) throws Exception;

    //更新用户信息
    public int updateLoginDate(String userName, String loginDate) throws Exception;

    //更新密码
    public int updatePassword(String userName, String password, String newPassword) throws Exception;

    //通过用户名查询一个用户
    public UserAccountEn queryInfoByUsername(String userName) throws Exception;


    //通过用户名查询一个用户
    public Map findUserInfoByUserId(String userId) throws Exception;


    // 分页查询
    Map<String, Object> getUserList(String userName, String start, String size);

    /**
     * 根据用户名查找其角色
     *
     * @param username
     * @return
     */
    public Set<String> findRoles(String username);

    /**
     * 根据用户名查找其权限
     *
     * @param username
     * @return
     */
    public Set<String> findPermissions(String username);

    public ResponseResult createUser(UserExt user, String loginUserId);

    public ResponseResult updateUser(UserAccountEn user, String loginUserId);

    public ResponseResult deleteUser(String userId);

    Map<String, Object> list(String userId, String userName, String jobNumber, String start, String size);

    Map<String, Object> user2Role(String userId, String roleName, String start, String length);

    Map<String, Object> userRole(String userId, String roleName, String start, String length);

    ResponseResult userAddRole(List<String> roleIds, String userId);

    Map<String, Object> user2Group(String userId, String groupName, String start, String length);

    Map<String, Object> userGroup(String userId, String groupName, String start, String length);

    ResponseResult userAddGroup(List<String> groupIds, String userId);

    ResponseResult userAddDept(List<String> deptIds, String userId);

    ResponseResult userAddChannel(String channelIds,String userId);

    Map<String, Object> role2User(String roleId, String userName, String start, String length);

    Map<String, Object> roleUser(String roleId, String userName, String start, String length);

    ResponseResult roleAddUser(List<String> userIds, String roleId);

    Map<String, Object> group2User(String groupId, String userName, String start, String length);

    Map<String, Object> groupUser(String groupId, String userName, String start, String length);

    ResponseResult groupAddUser(List<String> userIds, String groupId);

    Map<String, Object> dept2User(String deptId, String userName, String start, String length);

    Map<String, Object> deptUser(String deptId, String userName, String start, String length);

    ResponseResult deptAddUser(List<String> userIds, String deptId);

    Map<String, Object> channel2User(String channelId, String userName, String start, String length);

    Map<String, Object> channelUser(String channelId, String userName, String start, String length);

    ResponseResult channelAddUser(List<String> userIds, String channelId);

    /**
     * 新建用户是绑定渠道
     * @param channelId 渠道id
     * @param userId 用户id
     * @return
     */
    ResponseResult userAddChannel(String channelId, String userId);
}

