package com.bonc.frame.service.auth;

import com.bonc.frame.entity.auth.Subject;
import com.bonc.frame.util.ResponseResult;

import java.util.List;

/**
 * @author yedunyao
 * @date 2019/8/13 10:59
 */
public interface SubjectService {

    List<String> selectSubjectsByCurrentUser();

    List<String> selectSubjectsByUserId(String userId);

    String selectUserSubjectByUserId(String userId);

    String selectByRoleId(String roleId);

    String selectBySubjectObjectId(String subjectObjectId, String subjectTypeId);

    void createUserSubject(String userId);

    void createRoleSubject(String roleId);

    ResponseResult insert(Subject subject);

    ResponseResult deleteByPrimaryKey(String subjectId);

    void deleteUserSubject(String userId);

    void deleteRoleSubject(String roleId);

    ResponseResult deleteBySubjectObjectId(String subjectObjectId, String subjectTypeId);

}
