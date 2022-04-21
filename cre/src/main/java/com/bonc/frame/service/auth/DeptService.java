package com.bonc.frame.service.auth;

import com.bonc.frame.entity.auth.Channel;
import com.bonc.frame.entity.auth.DepartmentVo;
import com.bonc.frame.entity.auth.Dept;
import com.bonc.frame.util.ResponseResult;

import java.util.List;
import java.util.Map;

public interface DeptService {
    ResponseResult save(Dept dept, String loginUserId);

    Map<String, Object> list(String deptName, String start, String size);

    ResponseResult delete(String deptId);

    ResponseResult update(Dept dept, String loginUserId);

    List<Object> nameList();

    List<DepartmentVo> deptTree();

    Map<String, Object> deptChannel(String channelName,String deptId, String start, String size);

    /**
     * 校验权限后获取当前用户的所属渠道的id
     * @param userId 登录用户id
     * @return 用户所属渠道id
     */
    String getChannelIdByUserId(String userId);

}
