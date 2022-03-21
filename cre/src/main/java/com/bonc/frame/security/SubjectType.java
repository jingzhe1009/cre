package com.bonc.frame.security;

/**
 * @author yedunyao
 * @date 2019/7/28 11:45
 */
public enum SubjectType {

    USER("0"),

    ROLE("1"),

    USER_GROUP("2"),

    DEPT("3");

    private String type;

    SubjectType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
