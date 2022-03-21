package com.bonc.framework.rule.parse;

import com.bonc.framework.rule.RuleType;
import com.bonc.framework.rule.constant.RuleEngineConstant;
import com.bonc.framework.rule.exception.RuleParseException;
import com.bonc.framework.rule.util.RuleEnginePropertiesUtil;

/**
 * 规则解析器工厂
 *
 * @author qxl
 * @version 1.0
 * @date 2018年3月2日 下午4:56:30
 */
public class RuleParseFactory {

//    private static Map<String, IRuleParse> ruleParseMap = new ConcurrentHashMap<String, IRuleParse>();

    /**
     * 根据规则类型获取规则解析器
     *
     * @param ruleType
     * @return
     * @throws Exception
     */
    public static IRuleParse getRuleParseByRuleType(RuleType ruleType) throws Exception {
        String ruleTypeStr = ruleType.toString();
        /*if (ruleParseMap.containsKey(ruleTypeStr)) {
            return ruleParseMap.get(ruleTypeStr);
        }*/
        String clazz = RuleEnginePropertiesUtil.getProperty(RuleEngineConstant.PREFIX_RULEPARSECLASS + ruleTypeStr);
        if (clazz == null) {
            throw new RuleParseException("There is no config this type[" + ruleTypeStr + "] " +
                    "ruleParse.Please check ruleEngine.properties.");
        }
        IRuleParse ruleParse = (IRuleParse) Class.forName(clazz).newInstance();
//        ruleParseMap.put(ruleTypeStr, ruleParse);
        return ruleParse;
    }

}
