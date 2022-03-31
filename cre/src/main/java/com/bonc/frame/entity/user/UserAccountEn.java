package com.bonc.frame.entity.user;

import com.bonc.frame.entity.auth.Channel;
import com.bonc.frame.entity.auth.Dept;
import com.bonc.frame.entity.auth.Group;
import com.bonc.frame.entity.auth.Role;
import com.bonc.frame.util.MD5Util;


import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

/**
 * @author 作者：limf
 * @version 版本： 1.0
 * 说明：
 * @date 创建时间：2018年2月5日 上午10:55:53
 */
public class UserAccountEn {

    private String userId;
    // 工号
    private String jobNumber;
    private String userPassword;
    private String userName;
    private String userSex;
    private String loginDate;
    private String headThumb;
    //	private String userRole;
    private String phoneNumber;
    private String email;
    //	private String org;
//    	private String chan;

    /*public String getChan() {
        return chan;
    }

    public void setChan(String chan) {
        this.chan = chan;
    }*/

    //    private String deptId;    // 所属部门id
    private String workPhone;
    private Date createDate;
    private String createPerson;
    private Date updateDate;
    private String updatePerson;

    private List<Role> roleList;

    private List<Group> groupList;

    private List<Dept> deptList;

    private List<Channel> channelList;

    public UserAccountEn() {

    }

    public UserAccountEn(UserExt userExt) {
        this.userId = userExt.getUserId();
        this.jobNumber = userExt.getJobNumber();
        try {
            this.userPassword = MD5Util.Bit32(userExt.getUserPassword());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        this.userName = userExt.getUserName();
        this.userSex = userExt.getUserSex();
        this.loginDate = userExt.getLoginDate();
        this.headThumb = userExt.getHeadThumb();
        this.phoneNumber = userExt.getPhoneNumber();
        this.email = userExt.getEmail();
        this.workPhone = userExt.getWorkPhone();
        this.createDate = userExt.getCreateDate();
        this.createPerson = userExt.getCreatePerson();
        this.updateDate = userExt.getUpdateDate();
        this.updatePerson = userExt.getUpdatePerson();
    }

    public List<Channel> getChannelList() {
        return channelList;
    }

    public void setChannelList(List<Channel> channelList) {
        this.channelList = channelList;
    }

    public List<Dept> getDeptList() {
        return deptList;
    }

    public void setDeptList(List<Dept> deptList) {
        this.deptList = deptList;
    }

    public List<Group> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<Group> groupList) {
        this.groupList = groupList;
    }

    public List<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }

    public String getWorkPhone() {
        return workPhone;
    }

    public void setWorkPhone(String workPhone) {
        this.workPhone = workPhone;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getJobNumber() {
        return jobNumber;
    }

    public void setJobNumber(String jobNumber) {
        this.jobNumber = jobNumber;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserSex() {
        return userSex;
    }

    public void setUserSex(String userSex) {
        this.userSex = userSex;
    }

    public String getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(String loginDate) {
        this.loginDate = loginDate;
    }

    public String getHeadThumb() {
        return headThumb;
    }

    public void setHeadThumb(String headThumb) {
        this.headThumb = headThumb;
    }

    /*public String getUserRole() {
        return userRole;
    }
    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }*/
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
	/*public String getOrg() {
		return org;
	}
	public void setOrg(String org) {
		this.org = org;
	}*/

    @Override
    public String toString() {
        return "UserAccountEn{" +
                "userId='" + userId + '\'' +
                ", jobNumber='" + jobNumber + '\'' +
                ", userPassword='" + userPassword + '\'' +
                ", userName='" + userName + '\'' +
                ", userSex='" + userSex + '\'' +
                ", loginDate='" + loginDate + '\'' +
                ", headThumb='" + headThumb + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", workPhone='" + workPhone + '\'' +
                ", createDate=" + createDate +
                ", createPerson='" + createPerson + '\'' +
                ", updateDate=" + updateDate +
                ", updatePerson='" + updatePerson + '\'' +
                ", roleList=" + roleList +
                ", groupList=" + groupList +
                ", deptList=" + deptList +
                ", channelList=" + channelList +
                '}';
    }
}
