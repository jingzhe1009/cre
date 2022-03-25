package com.bonc.frame.entity.auth;

public class UserChannel {
    private String id;
    private String userId;
    private String channelId;

    public UserChannel(){

    }
    public UserChannel(String id, String userId, String channelId){
        this.id = id;
        this.userId = userId;
        this.channelId = channelId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    @Override
    public String toString() {
        return "UserChannel{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", channelId='" + channelId + '\'' +
                '}';
    }
}
