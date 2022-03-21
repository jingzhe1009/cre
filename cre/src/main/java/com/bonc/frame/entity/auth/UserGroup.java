package com.bonc.frame.entity.auth;

/**
 * @author yedunyao
 * @date 2019/5/9 19:48
 */
public class UserGroup {

    private String id;
    private String userId;
    private String groupId;

    public UserGroup(){

    }
    public UserGroup(String id, String userId, String groupId){
        this.id = id;
        this.userId = userId;
        this.groupId = groupId;
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

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return "UserGroup{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", groupId='" + groupId + '\'' +
                '}';
    }
}
