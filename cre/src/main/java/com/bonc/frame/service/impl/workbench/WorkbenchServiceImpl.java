package com.bonc.frame.service.impl.workbench;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bonc.frame.service.auth.AuthorityService;
import com.bonc.frame.service.auth.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bonc.frame.dao.DaoHelper;
import com.bonc.frame.entity.rulelog.RuleLog;
import com.bonc.frame.entity.rulelog.RuleLogDetail;
import com.bonc.frame.service.workbench.WorkbenchService;

@Service
public class WorkbenchServiceImpl implements WorkbenchService {

    private final String _MYBITSID_PREFIX = "com.bonc.frame.dao.rulelog.RuleLogMapper.";
    private final String _MYBITSID_DETAIL_PREFIX = "com.bonc.frame.mapper.rulelog.RuleLogDetailMapper.";

    @Autowired
    private DaoHelper daoHelper;

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private SubjectService subjectService;

    /**
     * 查询从今天到n天前规则每天执行的总次数
     * return list
     */
    @Override
    public List<Map<String, Object>> ruleExecuteAll(int day) {
        Map<String, Object> param = new HashMap<>();
        param.put("day", day);
        List<Map<String, Object>> list = this.daoHelper.queryForList(_MYBITSID_PREFIX + "selectExecuteDayNums", param);
        return list;
    }

    /**
     * 查询规则包下规则执行的总次数
     * return list
     */
    @Override
    public List<Map<String, Object>> ruleExecuteAllByFolder(String folderId, int day) {
        Map<String, Object> param = new HashMap<>();
        param.put("folderId", folderId);
        param.put("day", day);
        List<Map<String, Object>> list = this.daoHelper.queryForList(_MYBITSID_PREFIX + "ruleExecuteAllByFolder", param);
        return list;
    }

    /**
     * 查询从今天到n天前规则每天执行的成功次数
     * return list
     */
    @Override
    public List<Map<String, Object>> ruleExecuteAllSuccess(int day) {
        Map<String, Object> param = new HashMap<>();
        param.put("day", day);
        param.put("state", "2");
        List<Map<String, Object>> list = this.daoHelper.queryForList(_MYBITSID_PREFIX + "selectExecuteDayNums", param);
        return list;
    }

    /**
     * 查询从今天到n天前规则每天执行的失败次数
     * return list
     */
    @Override
    public List<Map<String, Object>> ruleExecuteAllFail(int day) {
        Map<String, Object> param = new HashMap<>();
        param.put("day", day);
        param.put("state", "-1");
        List<Map<String, Object>> list = this.daoHelper.queryForList(_MYBITSID_PREFIX + "selectExecuteDayNums", param);
        return list;
    }

    @Override
    public List<Map<String, Object>> ruleHitRate(int day) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("day", day);
        List<Map<String, Object>> list = this.daoHelper.queryForList(_MYBITSID_PREFIX + "selectHitRate", param);
        return list;
    }

    /**
     *
     *
     * */
    @Override
    public List<Map<String, Object>> folderRuleNum(int day) {
        Map<String, Object> param = new HashMap<>();
        param.put("day", day);
        List<Map<String, Object>> list = this.daoHelper.queryForList(_MYBITSID_PREFIX + "selectExecuteDayNumsFolders", param);
        return list;
    }

    @Override
    public Map<String, Object> queryExecuteRule(RuleLog ruleLog, String start, String size) {
        Map<String, Object> result = this.daoHelper.queryForPageList(_MYBITSID_PREFIX + "queryExecuteRule", ruleLog, start, size);
        return result;
    }

    @Override
    public List<RuleLogDetail> ruleDetailList(String logId) {
        Map<String, Object> param = new HashMap<>();
        param.put("logId", logId);
        List<RuleLogDetail> result = this.daoHelper.queryForList(_MYBITSID_DETAIL_PREFIX + "selectByLogId", param);
        return result;
    }

    @Override
    @Deprecated
    public List<Map<String, Object>> ruleExecuteAll(int day, String folderId, String deptId) {
        Map<String, Object> param = new HashMap<>();
        param.put("day", day);
        param.put("folderId", folderId);
        param.put("deptId", deptId);
        List<Map<String, Object>> list = this.daoHelper.queryForList(_MYBITSID_PREFIX + "selectExecuteDayNums", param);
        return list;
    }

    @Override
    @Deprecated
    public List<Map<String, Object>> ruleExecuteAllSuccess(int day, String folderId, String deptId) {
        Map<String, Object> param = new HashMap<>();
        param.put("day", day);
        param.put("folderId", folderId);
        param.put("deptId", deptId);
        param.put("state", "2");
        List<Map<String, Object>> list = this.daoHelper.queryForList(_MYBITSID_PREFIX + "selectExecuteDayNums", param);
        return list;
    }

    /**
     * 查询从今天到n天前规则每天执行的失败次数
     * return list
     */
    @Override
    @Deprecated
    public List<Map<String, Object>> ruleExecuteAllFail(int day, String folderId, String deptId) {
        Map<String, Object> param = new HashMap<>();
        param.put("day", day);
        param.put("folderId", folderId);
        param.put("deptId", deptId);
        param.put("state", "-1");
        List<Map<String, Object>> list = this.daoHelper.queryForList(_MYBITSID_PREFIX + "selectExecuteDayNums", param);
        return list;
    }

    /**
     * 获取模型执行情况统计,(通过 TIME,state 分组)
     *
     * @param state "-1":失败    "2":成功
     * @return
     */
    @Override
    public List<Map<String, Object>> ruleExecuteGroupByTimeAndState(int day, String folderId, String deptId, String state) {
        Map<String, Object> param = new HashMap<>();
        param.put("day", day);
        param.put("folderId", folderId);
        param.put("deptId", deptId);
        param.put("state", state);

        // FIXME: 当前实现并未考虑到主体对某一类型拥有全部权限的情况，现在系统内暂未实现类型级的授权
        if (authorityService.isCurrentUserHasAllPermits()) {
            return daoHelper.queryForList(_MYBITSID_PREFIX + "selectExecuteDayNums",
                    param);
        }

        List<String> subjects = subjectService.selectSubjectsByCurrentUser();
        param.put("subjectIds", subjects);
        return daoHelper.queryForList(_MYBITSID_PREFIX + "selectExecuteDayNumsWithAuthFilter",
                param);
    }

    @Override
    public List<Map<String, Object>> selectHitRateByDays(int day, String folderId, String deptId) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("day", day);
        param.put("folderId", folderId);
        param.put("deptId", deptId);

        // FIXME: 当前实现并未考虑到主体对某一类型拥有全部权限的情况，现在系统内暂未实现类型级的授权
        if (authorityService.isCurrentUserHasAllPermits()) {
            return daoHelper.queryForList(_MYBITSID_PREFIX + "selectHitRateByDays",
                    param);
        }

        List<String> subjects = subjectService.selectSubjectsByCurrentUser();
        param.put("subjectIds", subjects);
        return daoHelper.queryForList(_MYBITSID_PREFIX + "selectHitRateByDaysWithAuthFilter",
                param);
    }

}
