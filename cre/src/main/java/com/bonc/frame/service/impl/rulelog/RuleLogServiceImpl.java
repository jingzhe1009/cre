package com.bonc.frame.service.impl.rulelog;

import com.bonc.frame.dao.DaoHelper;
import com.bonc.frame.service.auth.AuthorityService;
import com.bonc.frame.service.auth.SubjectService;
import com.bonc.frame.service.rulelog.RuleLogService;
import com.bonc.frame.util.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yedunyao
 * @date 2019/5/8 16:03
 */
@Service
public class RuleLogServiceImpl implements RuleLogService {

    @Autowired
    private DaoHelper daoHelper;

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private SubjectService subjectService;

    private final String _MYBITSID_PREFIX = "com.bonc.frame.dao.rulelog.RuleLogMapper.";
    private final String RULE_LOG_DETAIL_MYBITSID_PREFIX = "com.bonc.frame.mapper.rulelog.RuleLogDetailMapper.";

    @Override
    public Map<String, Object> getRuleLogList(String folderId, String ruleId,
                                              String ruleName, String isDraft,
                                              String state,
                                              String startDate, String endDate,
                                              String sort, String order,
                                              String start, String size) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("folderId", folderId);
        param.put("ruleId", ruleId);
        param.put("ruleName", ruleName);
        param.put("isDraft", isDraft);
        param.put("state", state);
        param.put("startDate", startDate);
        param.put("endDate", endDate);
        param.put("sort", sort);
        param.put("order", order);

        // FIXME: 当前实现并未考虑到主体对某一类型拥有全部权限的情况，现在系统内暂未实现类型级的授权
        if (authorityService.isCurrentUserHasAllPermits()) {
            return daoHelper.queryForPageList(_MYBITSID_PREFIX + "getRuleLogList",
                    param, start, size);
        }

        List<String> subjects = subjectService.selectSubjectsByCurrentUser();
        param.put("subjectIds", subjects);
        return daoHelper.queryForPageList(_MYBITSID_PREFIX + "getRuleLogListWithAuthFilter",
                param, start, size);
    }

    @Override
    public Map<String, Object> getRuleLogDetail(String modelTrialLogId, String nodeType, String start, String size) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("modelTrialLogId", modelTrialLogId);
        param.put("nodeType", nodeType);

        Map<String, Object> stringObjectMap = daoHelper.queryForPageList(_MYBITSID_PREFIX + "getRuleLogDetailByModelTrialLogId",
                param, start, size);
        return stringObjectMap;
    }

    public ResponseResult deleteRuleLogByDay(String day) {
        Map<String, Object> params = new HashMap<>();
        params.put("day", day);
        daoHelper.delete(_MYBITSID_PREFIX + "deleteRuleLogByDay", params);
        return ResponseResult.createSuccessInfo();

    }

    public ResponseResult deleteRuleLogDetailByDay(String day) {
        Map<String, Object> params = new HashMap<>();
        params.put("day", day);
        daoHelper.delete(RULE_LOG_DETAIL_MYBITSID_PREFIX + "deleteRuleLogDetailByDay", params);
        return ResponseResult.createSuccessInfo();

    }

}
