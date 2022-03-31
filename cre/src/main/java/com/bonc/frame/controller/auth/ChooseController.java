package com.bonc.frame.controller.auth;

import com.bonc.frame.entity.auth.Channel;
import com.bonc.frame.entity.auth.DepartmentVo;
import com.bonc.frame.entity.auth.PlaceVo;
import com.bonc.frame.service.UserService;
import com.bonc.frame.service.auth.ChannelService;
import com.bonc.frame.service.auth.DeptService;
import com.bonc.frame.util.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;
import java.util.List;
/**
 * web路径：用户-选择判断
 * */
@Controller
@RequestMapping("/choose")
public class ChooseController {
    @Autowired
    UserService userService;
    @Autowired
    DeptService deptService;
    @Autowired
    ChannelService channelService;

    /**
     * 展示用户已有角色/user2Role
     *
     * @param userId
     * @param roleName
     * @param start
     * @param length
     * @return
     */
    @RequestMapping(value = "/user2Role", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> user2Role(String userId, String roleName, String start, String length) {
        Map<String, Object> result = userService.user2Role(userId, roleName, start, length);
        return result;
    }

    /**
     * 展示用户未有角色/userRole
     *
     * @param userId
     * @param roleName
     * @param start
     * @param length
     * @return
     */
    @RequestMapping(value = "/userRole", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> userRole(String userId, String roleName, String start, String length) {
        Map<String, Object> result = userService.userRole(userId, roleName, start, length);
        return result;
    }

    /**
     * 用户添加角色
     *
     * @param roleIds
     * @param userId
     * @return
     */
    @RequestMapping(value = "/userAddRole", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult userAddRole(@RequestParam(name = "roleIds[]", defaultValue = "") List<String> roleIds, String userId) {
        return userService.userAddRole(roleIds, userId);
    }

    /**
     * 展示用户已有用户组
     *
     * @param userId
     * @param groupName
     * @param start
     * @param length
     * @return
     */
    @RequestMapping(value = "/user2Group", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> user2Group(String userId, String groupName, String start, String length) {
        Map<String, Object> result = userService.user2Group(userId, groupName, start, length);
        return result;
    }

    /**
     * 展示用户未有用户组
     *
     * @param userId
     * @param groupName
     * @param start
     * @param length
     * @return
     */
    @RequestMapping(value = "/userGroup", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> userGroup(String userId, String groupName, String start, String length) {
        Map<String, Object> result = userService.userGroup(userId, groupName, start, length);
        return result;
    }

    /**
     * 用户添加用户组
     *
     * @param groupIds
     * @param userId
     * @return
     */
    @RequestMapping(value = "/userAddGroup", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult userAddGroup(@RequestParam(name = "groupIds[]", defaultValue = "") List<String> groupIds, String userId) {
        return userService.userAddGroup(groupIds, userId);
    }

    /**
     * 展示机构树
     *
     * @return
     */
    @RequestMapping(value = "/deptTree")
    @ResponseBody
    public ResponseResult deptTree() {
        List<DepartmentVo> voList = deptService.deptTree();
        return ResponseResult.createSuccessInfo("success", voList);
    }

    /**
     * 展示渠道树
     *
     * @return
     */
    @RequestMapping(value ="/channelTree")
    @ResponseBody
     public ResponseResult channelTree(){
         List<PlaceVo> channelList = channelService.channelTree();
         return ResponseResult.createFailInfo("success",channelList);
     }
    /**
     * 用户添加机构
     *
     * @param deptIds
     * @param userId
     * @return
     */
    @RequestMapping(value = "/userAddDept", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult userAddDept(@RequestParam(name = "deptIds[]", defaultValue = "") List<String> deptIds, String userId) {
        return userService.userAddDept(deptIds, userId);
    }

    /**
     * 用户添加渠道
     *
     * @param channelId
     * @param userId
     * @return
     */
    @RequestMapping(value = "/userAddChannel", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult userAddChannel(@RequestParam(name = "channelId", defaultValue = "") String channelId, String userId) {
        return userService.userAddChannel(channelId, userId);
    }




    /**
     * 展示角色已有用户
     *
     * @param roleId
     * @param userName
     * @param start
     * @param length
     * @return
     */
    @RequestMapping(value = "/role2User", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> role2User(String roleId, String userName, String start, String length) {
        Map<String, Object> result = userService.role2User(roleId, userName, start, length);
        return result;
    }


    /**
     * 展示角色未有用户
     *
     * @param roleId
     * @param userName
     * @param start
     * @param length
     * @return
     */
    @RequestMapping(value = "/roleUser", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> roleUser(String roleId, String userName, String start, String length) {
        Map<String, Object> result = userService.roleUser(roleId, userName, start, length);
        return result;
    }

    /**
     * 角色添加用户
     *
     * @param userIds
     * @param roleId
     * @return
     */
    @RequestMapping(value = "/roleAddUser", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult roleAddUser(@RequestParam(name = "userIds[]", defaultValue = "") List<String> userIds, String roleId) {
        return userService.roleAddUser(userIds, roleId);
    }


    /**
     * 用户组已有用户
     *
     * @param groupId
     * @param userName
     * @param start
     * @param length
     * @return
     */
    @RequestMapping(value = "/group2User", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> group2User(String groupId, String userName, String start, String length) {
        Map<String, Object> result = userService.group2User(groupId, userName, start, length);
        return result;
    }


    /**
     * 用户组未有用户
     *
     * @param groupId
     * @param userName
     * @param start
     * @param length
     * @return
     */
    @RequestMapping(value = "/groupUser", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> groupUser(String groupId, String userName, String start, String length) {
        Map<String, Object> result = userService.groupUser(groupId, userName, start, length);
        return result;
    }


    /**
     * 用户组添加用户
     *
     * @param userIds
     * @param groupId
     * @return
     */
    @RequestMapping(value = "/groupAddUser", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult groupAddUser(@RequestParam(name = "userIds[]", defaultValue = "") List<String> userIds, String groupId) {
        return userService.groupAddUser(userIds, groupId);
    }

    /**
     * 组织机构已有用户
     *
     * @param deptId
     * @param userName
     * @param start
     * @param length
     * @return
     */
    @RequestMapping(value = "/dept2User", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> dept2User(String deptId, String userName, String start, String length) {
        Map<String, Object> result = userService.dept2User(deptId, userName, start, length);
        return result;
    }


    /**
     * 组织机构未有用户
     *
     * @param deptId
     * @param userName
     * @param start
     * @param length
     * @return
     */
    @RequestMapping(value = "/deptUser", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> deptUser(String deptId, String userName, String start, String length) {
        Map<String, Object> result = userService.deptUser(deptId, userName, start, length);
        return result;
    }


    /**
     * 组织机构添加用户
     *
     * @param userIds
     * @param deptId
     * @return
     */
    @RequestMapping(value = "/deptAddUser", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult deptAddUser(@RequestParam(name = "userIds[]", defaultValue = "") List<String> userIds, String deptId) {
        return userService.deptAddUser(userIds, deptId);
    }

    /**
     * 渠道已有用户
     *
     * @param channelId
     * @param userName
     * @param start
     * @param length
     * @return
     */
    @RequestMapping(value = "/channel2User", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> channel2User(String channelId, String userName, String start, String length) {
        Map<String, Object> result = userService.channel2User(channelId, userName, start, length);
        return result;
    }


    /**
     * 渠道未有用户
     *
     * @param channelId
     * @param userName
     * @param start
     * @param length
     * @return
     */
    @RequestMapping(value = "/channelUser", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> channelUser(String channelId, String userName, String start, String length) {
        Map<String, Object> result = userService.channelUser(channelId, userName, start, length);
        return result;
    }


    /**
     * 渠道添加用户
     *
     * @param userIds
     * @param channelId
     * @return
     */
    @RequestMapping(value = "/channelAddUser", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult ChannelAddUser(@RequestParam(name = "userIds[]", defaultValue = "") List<String> userIds, String channelId) {
        return userService.channelAddUser(userIds, channelId);
    }


    /**
     * 机构已有渠道
     *
     * @param deptId
     *
     * @return
     */
    @RequestMapping(value = "/deptChannel",method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> DeptChannel(String deptId, String start,String length){
        Map<String, Object> result = deptService.deptChannel(deptId,start,length);
        return result;
    }
}
