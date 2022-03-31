package com.bonc.frame.service.auth;

import com.bonc.frame.entity.auth.Channel;
import com.bonc.frame.entity.auth.PlaceVo;
import com.bonc.frame.util.ResponseResult;


import java.util.List;
import java.util.Map;

public interface ChannelService {
    ResponseResult save(Channel channel, String loginUserId);

    Map<String, Object> list(String channelName, String start, String size);

    ResponseResult delete(String channelId);

    ResponseResult update(Channel channel, String loginUserId);

    List<Object> nameList();

    List<PlaceVo> channelTree();
}
