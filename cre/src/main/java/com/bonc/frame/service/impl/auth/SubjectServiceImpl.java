package com.bonc.frame.service.impl.auth;

import com.bonc.frame.config.Config;
import com.bonc.frame.config.ConstantFinal;
import com.bonc.frame.dao.DaoHelper;
import com.bonc.frame.entity.auth.Subject;
import com.bonc.frame.security.SubjectType;
import com.bonc.frame.service.auth.AuthorityService;
import com.bonc.frame.service.auth.SubjectService;
import com.bonc.frame.util.ControllerUtil;
import com.bonc.frame.util.IdUtil;
import com.bonc.frame.util.ResponseResult;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author yedunyao
 * @date 2019/8/13 10:59
 */
@Service
public class SubjectServiceImpl implements SubjectService {

    @Autowired
    private DaoHelper daoHelper;

    @Autowired
    private AuthorityService authorityService;

    private static final String _MYBITSID_PREFIX = "com.bonc.frame.mapper.auth.SubjectMapper.";

    @Override
    public List<String> selectSubjectsByCurrentUser() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session = request.getSession();
        Object user = session.getAttribute(ConstantFinal.SESSION_KEY);
        if (user != null &&  StringUtils.isNotBlank(user.toString())) {
            Object subjectTimestamp = session.getAttribute(ConstantFinal.SESSION_KEY_SUBJECT_TIMESTAMP);
            List<String> subjects = (List<String>) session.getAttribute(ConstantFinal.SESSION_KEY_SUBJECT_ID);
            // 判读 session中的权限主体 有没有过期
            if (isTimeOutSessionSubject(subjectTimestamp) && (subjects == null || subjects.isEmpty())) {

                subjects = selectSubjectsByUserId(user.toString());

                // session中权限主体的放置更新时间 以及 新查询到的session
                session.setAttribute(ConstantFinal.SESSION_KEY_SUBJECT_TIMESTAMP, new Date().getTime());
                session.setAttribute(ConstantFinal.SESSION_KEY_SUBJECT_ID, subjects);
            }
            return subjects;
        }
        // 从token中获取用户
        String userId = ControllerUtil.getUserFromToken();
        return selectSubjectsByUserId(userId);
    }


    @Override
    public List<String> selectSubjectsByUserId(String userId) {
        if (StringUtils.isBlank(userId)) {
            throw new IllegalArgumentException("参数[userId]不能为空");
        }

        List<String> subjects = daoHelper.queryForList(_MYBITSID_PREFIX + "selectSubjectsByUserId", userId);
        // 用户及其角色对应的主体
        return subjects;
    }

    public boolean isTimeOutSessionSubject(Object subjectTimestamp) {
        if (subjectTimestamp == null) {
            return true;
        }
        return System.currentTimeMillis() - Long.parseLong(subjectTimestamp.toString()) >= Config.RULE_TASK_CONSUMER_POOL_SIZE * 60 * 1000;
    }

    @Override
    public String selectUserSubjectByUserId(String userId) {
        return selectBySubjectObjectId(userId, SubjectType.USER.getType());
    }

    @Override
    public String selectByRoleId(String roleId) {
        return selectBySubjectObjectId(roleId, SubjectType.ROLE.getType());
    }

    @Override
    public String selectBySubjectObjectId(String subjectObjectId, String subjectTypeId) {
        if (StringUtils.isAnyEmpty(subjectObjectId, subjectTypeId)) {
            throw new IllegalArgumentException("参数[subjectId, subjectTypeId]不能为空");
        }
        Map<String, String> param = ImmutableMap
                .of("subjectObjectId", subjectObjectId,
                        "subjectTypeId", subjectTypeId);
        String subjectId = (String) daoHelper.queryOne(_MYBITSID_PREFIX + "selectBySubjectObjectId", param);
        return subjectId;
    }

    @Override
    @Transactional
    public void createUserSubject(String userId) {
        if (StringUtils.isBlank(userId)) {
            throw new IllegalArgumentException("参数[userId]不能为空");
        }

        Subject subject = new Subject();
        subject.setSubjectId(IdUtil.createId());
        subject.setSubjectObjectId(userId);
        subject.setSubjectTypeId(SubjectType.USER.getType());
        daoHelper.insert(_MYBITSID_PREFIX + "insert", subject);
    }

    @Override
    @Transactional
    public void createRoleSubject(String roleId) {
        if (StringUtils.isBlank(roleId)) {
            throw new IllegalArgumentException("参数[roleId]不能为空");
        }

        Subject subject = new Subject();
        subject.setSubjectId(IdUtil.createId());
        subject.setSubjectObjectId(roleId);
        subject.setSubjectTypeId(SubjectType.ROLE.getType());
        daoHelper.insert(_MYBITSID_PREFIX + "insert", subject);
    }

    @Override
    @Transactional
    public ResponseResult insert(Subject subject) {
        if (subject == null) {
            throw new NullPointerException();
        }
        daoHelper.insert(_MYBITSID_PREFIX + "insert", subject);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional
    public ResponseResult deleteByPrimaryKey(String subjectId) {
        if (StringUtils.isBlank(subjectId)) {
            throw new IllegalArgumentException("参数[subjectId]不能为空");
        }
        daoHelper.delete(_MYBITSID_PREFIX + "deleteByPrimaryKey", subjectId);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional
    public void deleteUserSubject(String userId) {
        if (StringUtils.isBlank(userId)) {
            throw new IllegalArgumentException("参数[userId]不能为空");
        }
        deleteBySubjectObjectId(userId, SubjectType.USER.getType());
    }

    @Override
    @Transactional
    public void deleteRoleSubject(String roleId) {
        if (StringUtils.isBlank(roleId)) {
            throw new IllegalArgumentException("参数[roleId]不能为空");
        }
        deleteBySubjectObjectId(roleId, SubjectType.ROLE.getType());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult deleteBySubjectObjectId(String subjectObjectId, String subjectTypeId) {
        if (StringUtils.isAnyEmpty(subjectObjectId, subjectTypeId)) {
            throw new IllegalArgumentException("参数[subjectId, subjectTypeId]不能为空");
        }
        Map<String, String> param = ImmutableMap
                .of("subjectObjectId", subjectObjectId,
                        "subjectTypeId", subjectTypeId);

        // 删除所有权限
        final String subjectId = selectBySubjectObjectId(subjectObjectId, subjectTypeId);
        authorityService.deleteBySubjectId(subjectId);

        daoHelper.delete(_MYBITSID_PREFIX + "deleteBySubjectObjectId", param);
        return ResponseResult.createSuccessInfo();
    }


}
