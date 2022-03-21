package com.bonc.frame.entity.auth;

import java.util.List;

/**
 * @author yedunyao
 * @date 2019/8/28 19:01
 */
public class GrantAuthRequest {

    public static final String IS_ALL_AUTH = "1"; // 判断是所有权限

    private String roleId;

    //用来判断是不是所有权限  1 : 所有权限
    private String isAllAuth ;

    private List<Authority> needInsertAuthorities;

    private List<Authority> needUpdateAuthorities;

    private List<String> needDelAuthorities;

    private List<String> needInsertEntityAuths;

    private List<String> needUpdateEntityAuths;

    private List<String> needDelEntityAuths;

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public List<Authority> getNeedInsertAuthorities() {
        return needInsertAuthorities;
    }

    public void setNeedInsertAuthorities(List<Authority> needInsertAuthorities) {
        this.needInsertAuthorities = needInsertAuthorities;
    }

    public List<Authority> getNeedUpdateAuthorities() {
        return needUpdateAuthorities;
    }

    public void setNeedUpdateAuthorities(List<Authority> needUpdateAuthorities) {
        this.needUpdateAuthorities = needUpdateAuthorities;
    }

    public List<String> getNeedDelAuthorities() {
        return needDelAuthorities;
    }

    public void setNeedDelAuthorities(List<String> needDelAuthorities) {
        this.needDelAuthorities = needDelAuthorities;
    }

    public String getIsAllAuth() {
        return isAllAuth;
    }

    public void setIsAllAuth(String isAllAuth) {
        this.isAllAuth = isAllAuth;
    }

    public List<String> getNeedInsertEntityAuths() {
        return needInsertEntityAuths;
    }

    public void setNeedInsertEntityAuths(List<String> needInsertEntityAuths) {
        this.needInsertEntityAuths = needInsertEntityAuths;
    }

    public List<String> getNeedUpdateEntityAuths() {
        return needUpdateEntityAuths;
    }

    public void setNeedUpdateEntityAuths(List<String> needUpdateEntityAuths) {
        this.needUpdateEntityAuths = needUpdateEntityAuths;
    }

    public List<String> getNeedDelEntityAuths() {
        return needDelEntityAuths;
    }

    public void setNeedDelEntityAuths(List<String> needDelEntityAuths) {
        this.needDelEntityAuths = needDelEntityAuths;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GrantAuthRequest{");
        sb.append("roleId='").append(roleId).append('\'');
        sb.append(", isAllAuth='").append(isAllAuth).append('\'');
        sb.append(", needInsertAuthorities=").append(needInsertAuthorities);
        sb.append(", needUpdateAuthorities=").append(needUpdateAuthorities);
        sb.append(", needDelAuthorities=").append(needDelAuthorities);
        sb.append(", needInsertEntityAuths=").append(needInsertEntityAuths);
        sb.append(", needUpdateEntityAuths=").append(needUpdateEntityAuths);
        sb.append(", needDelEntityAuths=").append(needDelEntityAuths);
        sb.append('}');
        return sb.toString();
    }
}
