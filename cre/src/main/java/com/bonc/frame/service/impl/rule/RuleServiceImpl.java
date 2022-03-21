package com.bonc.frame.service.impl.rule;

import com.alibaba.fastjson.JSON;
import com.bonc.frame.dao.DaoHelper;
import com.bonc.frame.entity.commonresource.ModelGroup;
import com.bonc.frame.entity.rulefolder.RuleFolder;
import com.bonc.frame.security.ResourceType;
import com.bonc.frame.service.auth.AuthorityService;
import com.bonc.frame.service.rule.RuleService;
import com.bonc.frame.service.syslog.SysLogService;
import com.bonc.frame.util.ConstantUtil;
import com.bonc.frame.util.ControllerUtil;
import com.bonc.frame.util.IdUtil;
import com.bonc.frame.util.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("ruleService")
public class RuleServiceImpl implements RuleService {

    private final String _MYBITSID_PREFIX = "com.bonc.frame.dao.ruletype.RuleTypeMapper.";

    @Autowired
    private DaoHelper daoHelper;

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private SysLogService sysLogService;

    @Override
    public List<Map<String, Object>> getRuleType() {
        List<Map<String, Object>> list = this.daoHelper.queryForList(_MYBITSID_PREFIX + "selectRuleType", null);
        return list;
    }

    @Override
    public List<Map<String, Object>> getRuleFolder() {
        List<Map<String, Object>> list = this.daoHelper.queryForList("com.bonc.frame.dao.rulefolder.RuleFolderMapper.selectRuleFolder");
        return list;
    }

    @Override
    public Map<String, Object> pagedRuleFolder(String folderName, String start, String size) {
        Map<String, String> param = new HashMap<>(1);
        param.put("folderName", folderName);
        return this.daoHelper.queryForPageList("com.bonc.frame.dao.rulefolder.RuleFolderMapper.pagedRuleFolder",
                param, start, size);
    }

    private boolean checkNameIsExist(String folderName, @Nullable String folderId) {
        Map<String, String> param = new HashMap<>(2);
        param.put("folderName", folderName);
        param.put("folderId", folderId);
        int obj = (int) daoHelper.queryOne("com.bonc.frame.dao.rulefolder.RuleFolderMapper." + "countByName", param);
        if (obj > 0) {
            return true;
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult insertRuleFolder(RuleFolder ruleFolder) {
        if (checkNameIsExist(ruleFolder.getFolderName(), null)) {
            return ResponseResult.createFailInfo("场景名称已存在");
        }

        String folderId = IdUtil.createId();
        ruleFolder.setFolderId(folderId);
//        daoHelper.insert("com.bonc.frame.dao.rulefolder.RuleFolderMapper." + "insertSelective", ruleFolder);
//
//        // 自动授权
//        authorityService.autoGrantAuthToCurrentUser(folderId, ResourceType.DATA_FOLDER);
//
//        sysLogService.logOperate(ConstantUtil.OPERATE_CREATE_RULE_FOLDER,
//                JSON.toJSONString(ruleFolder));
        insertRuleFolderDataPersistence(ruleFolder);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertRuleFolderDataPersistence(RuleFolder ruleFolder) {
        if (ruleFolder != null) {
            String folderId = ruleFolder.getFolderId();
            String currentUser = ControllerUtil.getCurrentUser();
            ruleFolder.setCreateDate(new Date());
            ruleFolder.setCreatePerson(currentUser);
            daoHelper.insert("com.bonc.frame.dao.rulefolder.RuleFolderMapper." + "insertSelective", ruleFolder);

            // 自动授权
            authorityService.autoGrantAuthToCurrentUser(folderId, ResourceType.DATA_FOLDER);

            sysLogService.logOperate(ConstantUtil.OPERATE_CREATE_RULE_FOLDER,
                    JSON.toJSONString(ruleFolder));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult updateRuleFolder(RuleFolder ruleFolder) {
        if (checkNameIsExist(ruleFolder.getFolderName(), ruleFolder.getFolderId())) {
            return ResponseResult.createFailInfo("场景名称已存在");
        }

        ruleFolder.setFolderId(IdUtil.createId());
        daoHelper.update(_MYBITSID_PREFIX + "updateByPrimaryKeySelective", ruleFolder);

        sysLogService.logOperate(ConstantUtil.OPERATE_UPDATE_RULE_FOLDER,
                JSON.toJSONString(ruleFolder));

        return ResponseResult.createSuccessInfo();
    }

}
