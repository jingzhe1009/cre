package com.bonc.frame.service.impl.auth;

import com.bonc.frame.dao.DaoHelper;
import com.bonc.frame.entity.auth.Group;
import com.bonc.frame.service.auth.GroupService;
import com.bonc.frame.util.IdUtil;
import com.bonc.frame.util.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service("groupService")
public class GroupServiceImpl implements GroupService {
    @Autowired
    private DaoHelper daoHelper;
    private final String _GROUP_PREFIX = "com.bonc.frame.mapper.auth.GroupMapper.";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult save(Group group, String loginUserId) {
        if (group == null) {
            return ResponseResult.createFailInfo("请求参数不能为null");
        }
        final Group oldGroup = findByName(group.getGroupName());
        if (oldGroup != null) {
            return ResponseResult.createFailInfo("用户组已存在");
        }
        final String groupId = IdUtil.createId();
        group.setGroupId(groupId);
        group.setCreateDate(new Date());
        group.setCreatePerson(loginUserId);
        daoHelper.insert(_GROUP_PREFIX + "insert", group);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    public Map<String, Object> list(String groupName, String start, String size) {
        Map<String,Object> param = new HashMap<>();
        param.put("groupName", groupName);
        Map<String, Object> result = daoHelper.queryForPageList(_GROUP_PREFIX + "listByGroupName", param, start, size);
        return result;
    }

    @Override
    @Transactional
    public ResponseResult delete(String groupId) {
        if (groupId == null) {
            return ResponseResult.createFailInfo("请求参数不能为null");
        }
        daoHelper.delete(_GROUP_PREFIX + "deleteByPrimaryKey", groupId);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult update(Group group, String loginUserId) {
        if (group == null) {
            return ResponseResult.createFailInfo("请求参数不能为null");
        }
        if (!isExists(group)) {
            return ResponseResult.createFailInfo("角色不存在， groupId: " + group.getGroupId());
        }
        group.setUpdateDate(new Date());
        group.setUpdatePerson(loginUserId);
        daoHelper.update(_GROUP_PREFIX + "updateByPrimaryKeySelective", group);
        return ResponseResult.createSuccessInfo();
    }
    public boolean isExists(Group group) {
        final Group resultGroup = selectByPrimaryKey(group.getGroupId());
        return resultGroup == null ? false : true;
    }
    public Group selectByPrimaryKey(String groupId) {
        return (Group) daoHelper.queryOne(_GROUP_PREFIX + "selectByPrimaryKey", groupId);
    }
    public Group findByName(String groupName) {
        return (Group) daoHelper.queryOne(_GROUP_PREFIX + "findByName", groupName);
    }
}
