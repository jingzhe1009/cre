package com.bonc.frame.entity.auth;

/**
 * 授权主体，可以为用户、角色、用户组等，
 * 该主体与用户、角色、用户组等为一对一关系。
 * 在创建用户、角色时即应创建主体，并以此主体进行授权。
 *
 * @author yedunyao
 * @date 2019/7/28 11:50
 */
public class Subject {

    /**
     * 主键
     */
    private String subjectId;

    /** 真实主体（用户、角色等）id */
    private String subjectObjectId;

    /**
     * 主体类型
     * @see com.bonc.frame.security.SubjectType
     */
    private String subjectTypeId;

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectObjectId() {
        return subjectObjectId;
    }

    public void setSubjectObjectId(String subjectObjectId) {
        this.subjectObjectId = subjectObjectId;
    }

    public String getSubjectTypeId() {
        return subjectTypeId;
    }

    public void setSubjectTypeId(String subjectTypeId) {
        this.subjectTypeId = subjectTypeId;
    }

    @Override
    public String toString() {
        return "Subject{" +
                "subjectId='" + subjectId + '\'' +
                ", subjectObjectId='" + subjectObjectId + '\'' +
                ", subjectTypeId='" + subjectTypeId + '\'' +
                '}';
    }
}
