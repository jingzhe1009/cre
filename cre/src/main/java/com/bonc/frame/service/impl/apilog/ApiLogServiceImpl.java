package com.bonc.frame.service.impl.apilog;

import com.bonc.frame.dao.DaoHelper;
import com.bonc.frame.service.apilog.ApiLogService;
import com.bonc.frame.service.auth.AuthorityService;
import com.bonc.frame.service.auth.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yedunyao
 * @date 2019/5/8 16:40
 */
@Service
public class ApiLogServiceImpl implements ApiLogService {

    @Autowired
    private DaoHelper daoHelper;

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private SubjectService subjectService;

    private final String _MYBITSID_PREFIX = "com.bonc.frame.mapper.apilog.ApiLogMapper.";

    @Override
    public Map<String, Object> getApiLogList(String apiName, String startDate, String endDate,
                                             String sort, String order, String start, String size) {
        Map<String,Object> param = new HashMap<String,Object>();
        param.put("apiName", apiName);
        param.put("startDate", startDate);
        param.put("endDate", endDate);
        param.put("sort", sort);
        param.put("order", order);

        // 注意：场下的接口和公共接口是两种类型的资源，场景下接口根据场景进行权限控制，公共接口根据自身（apiId）进行控制
        // 此处不在使用通用的权限注解，单独实现权限过滤
        if (authorityService.isCurrentUserHasAllPermits()) {
            return daoHelper.queryForPageList(_MYBITSID_PREFIX + "getApiLogList",
                    param, start, size);
        }
        // FIXME: 当前实现并未考虑到主体对某一类型拥有全部权限的情况，现在系统内暂未实现类型级的授权
        // 根据用户获取对应的多个主体（用户本身、用户的角色）
        List<String> subjects = subjectService.selectSubjectsByCurrentUser();
        param.put("subjectIds", subjects);
        return daoHelper.queryForPageList(_MYBITSID_PREFIX + "getApiLogListWithAuthFilter",
                param, start, size);
    }

}
