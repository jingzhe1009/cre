package com.bonc.frame.entity.auth;

import java.util.Date;

/**
 * @author yedunyao
 * @date 2019/5/9 19:48
 */
public class Role extends UserMiddle{

    private String roleId;

    private String roleName;

    private String roleDesc;

    private Date createDate;

    private String createPerson;

    private Date updateDate;

    private String updatePerson;

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCreatePerson() {
        return createPerson;
    }

    public void setCreatePerson(String createPerson) {
        this.createPerson = createPerson;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getUpdatePerson() {
        return updatePerson;
    }

    public void setUpdatePerson(String updatePerson) {
        this.updatePerson = updatePerson;
    }

    public String getRoleDesc() {
        return roleDesc;
    }

    public void setRoleDesc(String roleDesc) {
        this.roleDesc = roleDesc;
    }

    @Override
    public String toString() {
        return "Role{" +
                "roleId='" + roleId + '\'' +
                ", roleName='" + roleName + '\'' +
                ", roleDesc=" + roleDesc + '\'' +
                ", createDate=" + createDate + '\'' +
                ", createPerson='" + createPerson + '\'' +
                ", updateDate=" + updateDate +
                ", updatePerson='" + updatePerson + '\'' +
                '}';
    }
}
