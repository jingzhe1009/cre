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

    // 设置全量数据的权限（已勾选的）
    private List<String> needUpdateAllAuths;

    // 设置全量数据的权限（未勾选的）
    private List<String> needDelAllAuths;

    private String resourceType;

    public GrantAuthRequest() {
    }

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

    public List<String> getNeedUpdateAllAuths() {
        return needUpdateAllAuths;
    }

    public void setNeedUpdateAllAuths(List<String> needUpdateAllAuths) {
        this.needUpdateAllAuths = needUpdateAllAuths;
    }

    public List<String> getNeedDelAllAuths() {
        return needDelAllAuths;
    }

    public void setNeedDelAllAuths(List<String> needDelAllAuths) {
        this.needDelAllAuths = needDelAllAuths;
    }

    @Override
    public String toString() {
        return "GrantAuthRequest{" +
                "roleId='" + roleId + '\'' +
                ", isAllAuth='" + isAllAuth + '\'' +
                ", needInsertAuthorities=" + needInsertAuthorities +
                ", needUpdateAuthorities=" + needUpdateAuthorities +
                ", needDelAuthorities=" + needDelAuthorities +
                ", needInsertEntityAuths=" + needInsertEntityAuths +
                ", needUpdateEntityAuths=" + needUpdateEntityAuths +
                ", needDelEntityAuths=" + needDelEntityAuths +
                ", needUpdateAllAuths=" + needUpdateAllAuths +
                ", needDelAllAuths=" + needDelAllAuths +
                '}';
    }
}
