package com.bonc.frame.service.auth;

public interface ChannelPathService {
    void save(String childId, String parentId);

    void delete(String channelId);
}
