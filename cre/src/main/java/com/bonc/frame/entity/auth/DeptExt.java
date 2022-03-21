package com.bonc.frame.entity.auth;

public class DeptExt extends Dept {
    private String parentName;

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }
}
