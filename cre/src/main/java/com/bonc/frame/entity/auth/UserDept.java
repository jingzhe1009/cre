package com.bonc.frame.entity.auth;

/**
 * @author yedunyao
 * @date 2019/5/9 19:48
 */
public class UserDept {

    private String id;
    private String userId;
    private String deptId;

    public UserDept(){

    }
    public UserDept(String id, String userId, String deptId){
        this.id = id;
        this.userId = userId;
        this.deptId = deptId;
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

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    @Override
    public String toString() {
        return "UserDept{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", deptId='" + deptId + '\'' +
                '}';
    }
}
