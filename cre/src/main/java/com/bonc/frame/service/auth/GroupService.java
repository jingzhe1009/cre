package com.bonc.frame.service.auth;

import com.bonc.frame.entity.auth.Group;
import com.bonc.frame.util.ResponseResult;

import java.util.Map;

public interface GroupService {
    ResponseResult save(Group group, String loginUserId);

    Map<String, Object> list(String groupName, String start, String size);

    ResponseResult delete(String groupId);

    ResponseResult update(Group group, String loginUserId);
}
