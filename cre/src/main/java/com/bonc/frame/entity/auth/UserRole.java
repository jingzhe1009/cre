package com.bonc.frame.entity.auth;

/**
 * @author yedunyao
 * @date 2019/5/9 19:48
 */
public class UserRole {

    private String id;
    private String userId;
    private String roleId;

    public UserRole(){

    }
    public UserRole(String id, String userId, String roleId){
        this.id = id;
        this.userId = userId;
        this.roleId = roleId;
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

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    @Override
    public String toString() {
        return "UserRole{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", roleId='" + roleId + '\'' +
                '}';
    }
}
