package com.bonc.frame.service.rule;

import java.util.List;
import java.util.Map;

import com.bonc.frame.entity.rulefolder.RuleFolder;
import com.bonc.frame.util.ResponseResult;

public interface RuleService {

    List<Map<String, Object>> getRuleType();

    List<Map<String, Object>> getRuleFolder();

    Map<String, Object> pagedRuleFolder(String folderName, String start, String size);

    ResponseResult insertRuleFolder(RuleFolder ruleFolder);

    void insertRuleFolderDataPersistence(RuleFolder ruleFolder);

    ResponseResult updateRuleFolder(RuleFolder ruleFolder);

}
