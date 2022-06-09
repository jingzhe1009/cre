package com.bonc.frame.service.auth;

import com.bonc.frame.entity.auth.Channel;
import com.bonc.frame.entity.auth.ChannelDto;
import com.bonc.frame.entity.auth.DeptChannelTree;
import com.bonc.frame.entity.auth.PlaceVo;
import com.bonc.frame.util.ResponseResult;


import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface ChannelService {
    ResponseResult save(Channel channel, String loginUserId);

    Map<String, Object> list(HttpServletRequest request, String channelName, String start, String size);

    ResponseResult delete(String channelId);

    ResponseResult update(Channel channel, String loginUserId);
    List<Object> nameList();
    
    List<Map<String,Object>> getChannelListByGroupId(String groupId);

    List<PlaceVo> channelTree();

    /**
     * 查看渠道下用户列表
     * @param channelId 渠道id
     * @return 用户信息
     */
    List<Object> userListByChannel(String channelId);

    /**
     * 获取机构-渠道树
     * @param  loginUserId-当前登录用户-校验权限用
     * @return 树形数据
     */
    List<DeptChannelTree> channelTreeWithDept(String loginUserId);

    /**
     * 展示渠道数据-拼接机构名版
     * @param loginUserId 用户id，用于判定权限
     * @return 集合
     */
    List<ChannelDto> channelNameList(String loginUserId);
}
