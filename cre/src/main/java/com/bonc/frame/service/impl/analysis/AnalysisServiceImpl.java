package com.bonc.frame.service.impl.analysis;

import com.bonc.frame.dao.DaoHelper;
import com.bonc.frame.entity.analysis.ModelExecuteCountInfo;
import com.bonc.frame.entity.analysis.ModelExecuteStatistics;
import com.bonc.frame.entity.analysis.ModelStatisticsInfo;
import com.bonc.frame.entity.analysis.TaskStatusStatisInfo;
import com.bonc.frame.service.analysis.AnalysisService;
import com.bonc.frame.service.auth.AuthorityService;
import com.bonc.frame.service.auth.SubjectService;
import com.bonc.framework.util.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scala.annotation.meta.param;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("analysisService")
public class AnalysisServiceImpl implements AnalysisService {
    Log log = LogFactory.getLog(AnalysisServiceImpl.class);

    @Autowired
    private DaoHelper daoHelper;

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private SubjectService subjectService;

    private final String _MYBITSID_PREFIX = "com.bonc.frame.mapper.analysis.AnalysisMapper.";

    @Override
    public ModelExecuteCountInfo allmodelExecuteStatis(int day, String folderId, String deptId) {
        ModelExecuteCountInfo executeCountInfo = new ModelExecuteCountInfo();
        Map<String, Object> params = new HashMap<>();
        params.put("day", day);
        params.put("folderId", folderId);
        params.put("deptId", deptId);

        // FIXME: 当前实现并未考虑到主体对某一类型拥有全部权限的情况，现在系统内暂未实现类型级的授权
        List<Map<String, Object>> allmodelExecuteList = null;
        if (authorityService.isCurrentUserHasAllPermits()) {
            allmodelExecuteList = daoHelper.queryForList(_MYBITSID_PREFIX + "modelExecuteStatis",
                    params);
        } else {
            List<String> subjects = subjectService.selectSubjectsByCurrentUser();
            params.put("subjectIds", subjects);
            allmodelExecuteList = daoHelper.queryForList(_MYBITSID_PREFIX + "modelExecuteStatisWithAuthFilter",
                    params);
        }

//        List<Map<String, Object>> allmodelExecuteList = daoHelper.queryForList(_MYBITSID_PREFIX + "modelExecuteStatis", params);
        log.debug("allmodelExecuteList : " + allmodelExecuteList);
        int allCount = 0;
        for (Map<String, Object> modelExecute : allmodelExecuteList) {
            if (StringUtils.isBlank(objectToString(modelExecute.get("state"))) || StringUtils.isBlank(objectToString(modelExecute.get("count")))) {
                continue;
            }
            executeCountInfo.setStatesCount(objectToString(modelExecute.get("state")), objectToString(modelExecute.get("count")));

            allCount = allCount + Integer.parseInt(objectToString(modelExecute.get("count")));
        }
        executeCountInfo.setStatesCount("allCount", objectToString(allCount));
        return executeCountInfo;
    }

    //一周以内被调用次数前三
    @Override
    public Map<String, Object> allmodelExecuteTopThree(int day, String size) {
        Map<String, Object> params = new HashMap<>();
        params.put("day", day);

        // FIXME: 当前实现并未考虑到主体对某一类型拥有全部权限的情况，现在系统内暂未实现类型级的授权
        if (authorityService.isCurrentUserHasAllPermits()) {
            return daoHelper.queryForPageList(_MYBITSID_PREFIX + "allmodelExecuteTopThree",
                    params, "0", size);
        }

        List<String> subjects = subjectService.selectSubjectsByCurrentUser();
        params.put("subjectIds", subjects);
        return daoHelper.queryForPageList(_MYBITSID_PREFIX + "allmodelExecuteTopThreeWithAuthFilter",
                params, "0", size);
    }

    @Override
    public List<ModelStatisticsInfo> allmodelStatis() {
        // FIXME: 当前实现并未考虑到主体对某一类型拥有全部权限的情况，现在系统内暂未实现类型级的授权
        if (authorityService.isCurrentUserHasAllPermits()) {
            return daoHelper.queryForList(_MYBITSID_PREFIX + "allmodelStatis");
        }

        List<String> subjects = subjectService.selectSubjectsByCurrentUser();
        Map<String, Object> params = new HashMap<>();
        params.put("subjectIds", subjects);
        return daoHelper.queryForList(_MYBITSID_PREFIX + "allmodelStatisWithAuthFilter",
                params);
    }

    @Override
    public List<ModelStatisticsInfo> allModelStatusStatis() {
        // FIXME: 当前实现并未考虑到主体对某一类型拥有全部权限的情况，现在系统内暂未实现类型级的授权
        if (authorityService.isCurrentUserHasAllPermits()) {
            return daoHelper.queryForList(_MYBITSID_PREFIX + "allModelStatusStatis");
        }

        List<String> subjects = subjectService.selectSubjectsByCurrentUser();
        Map<String, Object> params = new HashMap<>();
        params.put("subjectIds", subjects);
        return daoHelper.queryForList(_MYBITSID_PREFIX + "allModelStatusStatisWithAuthFilter",
                params);
    }


    @Override
    public List<TaskStatusStatisInfo> taskStatusStatis() {
        // FIXME: 当前实现并未考虑到主体对某一类型拥有全部权限的情况，现在系统内暂未实现类型级的授权
        if (authorityService.isCurrentUserHasAllPermits()) {
            return daoHelper.queryForList(_MYBITSID_PREFIX + "taskStatusStatis");
        }

        List<String> subjects = subjectService.selectSubjectsByCurrentUser();
        Map<String, Object> params = new HashMap<>();
        params.put("subjectIds", subjects);
        return daoHelper.queryForList(_MYBITSID_PREFIX + "taskStatusStatisWithAuthFilter",
                params);
    }


    private String objectToString(Object o) {
        if (o == null) {
            return null;
        }
        return o + "";
    }

    private static Date parse(String dateSring, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date date = null;
        try {
            date = sdf.parse(dateSring);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
