package com.bonc.frame.service.impl.rule;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bonc.frame.dao.DaoHelper;
import com.bonc.frame.entity.rulefolder.RuleFolder;
import com.bonc.frame.security.ResourceType;
import com.bonc.frame.service.auth.AuthorityService;
import com.bonc.frame.service.auth.SubjectService;
import com.bonc.frame.service.metadata.MetaDataMgrService;
import com.bonc.frame.service.rule.RuleDetailService;
import com.bonc.frame.service.rule.RuleFolderService;
import com.bonc.frame.service.syslog.SysLogService;
import com.bonc.frame.util.ConstantUtil;
import com.bonc.frame.util.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author qxl
 * @version 1.0
 * @date 2018年3月29日 下午3:42:43
 */
@Service
public class RuleFolderServiceImpl implements RuleFolderService {

    //文件夹相关
    private final String _MYBITSID_PREFIX = "com.bonc.frame.dao.rulefolder.RuleFolderMapper.";
    //规则相关
    private final String _MYBITSID_PREFIX_RULEDETAIL = "com.bonc.frame.dao.rule.RuleDetailMapper.";
    //变量相关
    private final String _MYBITSID_PREFIX_VARIABLE = "com.bonc.frame.dao.variable.VariableMapper.";
    //接口相关
    private final String _MYBITSID_PREFIX_API = "com.bonc.frame.dao.api.ApiMapper.";

    /**
     * 模型-参数引用中间表
     */
    private final String _VARIABLE_RULE_PREFIX = "com.bonc.frame.mapper.variable.VariableRuleMapper.";

    @Autowired
    private DaoHelper daoHelper;

    @Autowired
    private SysLogService sysLogService;

    @Autowired
    private RuleDetailService ruleDetailService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    MetaDataMgrService metaDataMgrService;

    @Override
    public String getModelBaseId() {
        return (String) daoHelper.queryOne(_MYBITSID_PREFIX + "getModelBaseId", null);
    }

    //获取文件夹详情
    @Override
    public RuleFolder getRuleFolderDetail(String folderId, String folderName) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("folderId", folderId);
        param.put("folderName", folderName);
        RuleFolder ruleFolder = (RuleFolder) daoHelper.queryOne(_MYBITSID_PREFIX + "selectByPrimaryKey", param);
        return ruleFolder;
    }

    public ResponseResult checkDeleteRuleAuth(List<Map<String, Object>> ruleNameList, String currentUser) {

        // 根据用户获取对应的多个主体（用户本身、用户的角色）
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        // 根据用户获取对应的多个主体（用户本身、用户的角色）
        List<String> subjects = subjectService.selectSubjectsByCurrentUser();


        for (Map<String, Object> rule : ruleNameList) {
//			final String ruleId = (String) rule.get("ruleId");
            final String ruleName = (String) rule.get("ruleName");

            // 根据主体获取权限
            Set<String> holdingPermits = authorityService.getDistinctPermitsBySubjectIdsResourceId(subjects,
                    ruleName, ResourceType.DATA_MODEL.getType());

            if (holdingPermits == null ||
                    holdingPermits.isEmpty()) {
                return ResponseResult.createFailInfo("当前用户没有模型[" + ruleName + "]的删除权限");
            }
            if (!holdingPermits.contains("*") &&
                    !holdingPermits.contains("/rule/delete")) {
                return ResponseResult.createFailInfo("当前用户没有模型[" + ruleName + "]的删除权限");
            }
        }
        return ResponseResult.createSuccessInfo();
    }

    //删除文件夹
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult deleteFolder(String folderId, String currentUser, String platformUpdateUserJobNumber) {
        if (folderId == null || folderId.isEmpty()) {
            return ResponseResult.createFailInfo("The folderId is null.");
        }

        final List<Map<String, Object>> ruleNameList = ruleDetailService.getRuleNameList(folderId);
        //检查权限
        final ResponseResult responseResult = checkDeleteRuleAuth(ruleNameList, currentUser);
        if (ResponseResult.ERROR_STAUS == responseResult.getStatus()) {
            return responseResult;
        }
        // 检查场景中的模型有没有启用的
        Integer count = (Integer) daoHelper.queryOne(_MYBITSID_PREFIX_RULEDETAIL + "selectRunRuleCountByFolderId", folderId);
        if (count != null && count > 0) {
            return ResponseResult.createFailInfo("存在正在启用的模型，不能删除");
        }

        //级联删除：删除模型
        for (Map<String, Object> rule : ruleNameList) {
            final String ruleName = (String) rule.get("ruleName");
            ruleDetailService.deleteRuleByName(ruleName, platformUpdateUserJobNumber);        // 需要删除模型对公共资源的引用
        }

        //删除模型
//		daoHelper.delete(_MYBITSID_PREFIX_RULEDETAIL + "deleteRuleByFolderId", folderId);

        // 级联删除：删除变量
        daoHelper.delete(_MYBITSID_PREFIX_VARIABLE + "deleteEntityByFolderId", folderId);
        daoHelper.delete(_MYBITSID_PREFIX_VARIABLE + "deleteEntityRefByFolderId", folderId);
        daoHelper.delete(_MYBITSID_PREFIX_VARIABLE + "deleteByFolderId", folderId);
        // 级联删除：删除接口
        daoHelper.delete(_MYBITSID_PREFIX_API + "deleteApiByFolderId", folderId);

        // 级联删除：当前使用oracle外键约束级联删除元数据表、字段
        metaDataMgrService.deleteMetaTable(folderId);

        // 删除文件夹
        daoHelper.delete(_MYBITSID_PREFIX + "deleteByPrimaryKey", folderId);

        final JSONObject info = new JSONObject();
        info.put("folderId", folderId);
        sysLogService.logOperate(ConstantUtil.OPERATE_DELETE_RULE_FOLDER,
                JSON.toJSONString(info));

        return ResponseResult.createSuccessInfo();
    }

}
