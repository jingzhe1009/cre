package com.bonc.frame.service.modelCompare;

import com.alibaba.fastjson.JSONObject;
import com.bonc.frame.entity.modelCompare.entity.ModelCompareType;
import com.bonc.frame.entity.modelCompare.entity.ConditionListCompareResult;
import com.bonc.frame.entity.modelCompare.entity.ActionCompareResult;
import com.bonc.frame.util.ModelComparUtil;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.bonc.frame.util.ModelComparUtil.*;
import static com.bonc.frame.util.ModelComparUtil.getJsonStringValueString;

@Service
public class RuleSetCompareImp extends AbstractModelCompare {

    @Override
    public ModelCompareType getSupport() {
        return ModelCompareType.TASK;
    }


    @Override
    public void comparNodePropsOthers(JSONObject oldNodeProps, JSONObject newNodeProps, JSONObject rectNodePropsDifferentJson) {

        ModelComparUtil.comparJsonValueDifferent(oldNodeProps, newNodeProps, "isPublic", rectNodePropsDifferentJson);
        ModelComparUtil.comparJsonValueDifferent(oldNodeProps, newNodeProps, "ruleSetId", rectNodePropsDifferentJson);

        // 比较规则
        String oldAction = oldNodeProps.getString("action");
        String newNction = newNodeProps.getString("action");
        String oldActionValue = ModelComparUtil.getJsonStringValueString(oldAction, "value");
        String newActionValue = ModelComparUtil.getJsonStringValueString(newNction, "value");

        if (!ModelComparUtil.isEqualsString(oldActionValue, newActionValue)) {
            JSONObject actionDifferent = new JSONObject();

            ActionCompareResult actionCompareResult = new ActionCompareResult();
            comparAction(actionCompareResult, oldActionValue, newActionValue);
            if (actionCompareResult != null && !actionCompareResult.isEmptyDifferent()) {
                actionDifferent.putAll(actionCompareResult.getResult());
            }
            if (actionDifferent != null && !actionDifferent.isEmpty()) {
                rectNodePropsDifferentJson.put("action", actionDifferent);
            }
//            setChildToParentJSON(rectNodePropsDifferentJson, newPropsDifferentJson, oldActionDifferent, newActionDifferent, "pathCdt");

        }
    }

    // 比较规则
    public void comparAction(ActionCompareResult actionCompareResult, String oldActionJSONString, String newActionJSONString) {

        //转化为Map ----- 先转化为List 然后遍历List ,放到 Map中, key为uid ,value为 List中的值
        JSONObject oldActions = paseMapByKey(oldActionJSONString, "uid");
        JSONObject newActions = paseMapByKey(newActionJSONString, "uid");


        // 比较OldMap和newMap中的key
        if (!ModelComparUtil.isEmpty(oldActions) || !ModelComparUtil.isEmpty(newActions)) {
            if (ModelComparUtil.isEmpty(oldActions)) {
                //如果旧的模型中的规则为空，那么新的模型中的所有的规则都是  新加的  create
                actionCompareResult.addOrDeleteAllInfoToList(newActions, newActions.keySet(), ActionCompareResult.RULE_Type, true);
            } else if (ModelComparUtil.isEmpty(newActions)) {
                // 如果新的模型中的规则为空,那么旧的模型中的所有的规则都是被  删除的  delete
                actionCompareResult.addOrDeleteAllInfoToList(oldActions, oldActions.keySet(), ActionCompareResult.RULE_Type, false);
            } else {
                // 两个都不为空 则比较

                Set<String> oldActionsKeySet = oldActions.keySet();
                Set<String> newActionsKeySet = newActions.keySet();

                Set<String> addKeySet = differenceSet(newActionsKeySet, oldActionsKeySet); // 新的减去旧的 --- 都是add
                Set<String> deleteKeySet = differenceSet(oldActionsKeySet, newActionsKeySet); // 旧的减去新的 --- 都是delete
                Set<String> updateKeySet = intersectionSet(newActionsKeySet, oldActionsKeySet); // 交集 新旧模型里面都有,则判断有没有更新

                // 处理 添加/删除的规则
                actionCompareResult.addOrDeleteAllInfoToList(newActions, addKeySet, ActionCompareResult.RULE_Type, true);  // add的规则路径永远来自于新的模型
                actionCompareResult.addOrDeleteAllInfoToList(oldActions, deleteKeySet, ActionCompareResult.RULE_Type, false); // delete的规则路径永远来自于旧的模型

                // 处理 更新的规则
                for (String key : updateKeySet) {
                    JSONObject oldRule = JSONObject.parseObject(getMapValue(oldActions, key));
                    JSONObject newRule = JSONObject.parseObject(getMapValue(newActions, key));

                    comparRuleJson(actionCompareResult, key, oldRule, newRule);

                }
            }
        }
        return;

    }

    private void comparRuleJson(ActionCompareResult ruleSetComparResult, String key, JSONObject oldRule, JSONObject newRule) {
        log.debug("比较模型的规则,oldRule:" + oldRule);
        log.debug("比较模型的规则,newRule:" + newRule);
        JSONObject ruleDifferent = new JSONObject();
        // 添加规则集的标识 uid 以及新旧规则集的名称


        //比较 isElse,isEndFlow
        ModelComparUtil.comparJsonValueDifferent(oldRule, newRule, "actRuleName", ruleDifferent);
        ModelComparUtil.comparJsonValueDifferent(oldRule, newRule, "isEndAction", ruleDifferent);
        ModelComparUtil.comparJsonValueDifferent(oldRule, newRule, "isEndFlow", ruleDifferent);

        //比较 LHS.union
        comparLHS(oldRule, newRule, ruleDifferent);
        // 比较 Rule:规则
        comparRHS(oldRule, newRule, ruleDifferent);
        //添加rule的标识
        if (ruleDifferent != null && !ruleDifferent.isEmpty()) {
            String uid = null;
            String ruleName = null;
            if (oldRule != null) {
                ruleName = oldRule.getString("actRuleName");
                uid = oldRule.getString("uid");
            }
            if (newRule != null) {
                uid = newRule.getString("uid");
                ruleName = newRule.getString("actRuleName");
            }
            ruleDifferent.put("uid", uid);
            ruleDifferent.put("actRuleName", ruleName);
        }

        ruleSetComparResult.addUpdateNodeToList(ruleDifferent, ActionCompareResult.RULE_Type);


    }

    private void comparLHS(JSONObject oldRule, JSONObject newRule, JSONObject oldRuleDifferent) {
        ConditionListCompareImp conditionListCompareImp = null;
        String oldLHS = getMapValue(oldRule, "LHS");
        String newLHS = getMapValue(newRule, "LHS");
        String oldLHSTxtConditionList = getMapValue(oldRule, "LHSTxt");
        String newLHSTxtConditionList = getMapValue(newRule, "LHSTxt");
        String oldLHSConditionList = null;
        String newLHSConditionList = null;
        if (!ModelComparUtil.isEqualsString(oldLHS, newLHS)) {
            JSONObject lhsDifferent = new JSONObject();
            String oldUnion = getJsonStringValueString(oldLHS, "union");
            String newUnion = getJsonStringValueString(newLHS, "union");
            if (!ModelComparUtil.isEqualsString(oldUnion, newUnion)) {
                lhsDifferent.put("union", oldUnion);
                lhsDifferent.put("new_union", newUnion);
            }

//            ConditionListCompareResult ruleComparResult = new ConditionListCompareResult();
            oldLHSConditionList = getJsonStringValueString(oldLHS, "condition");
            newLHSConditionList = getJsonStringValueString(newLHS, "condition");
            if (!ModelComparUtil.isEqualsString(oldLHSConditionList, newLHSConditionList)) {
                conditionListCompareImp = new ConditionListCompareImp(oldLHSConditionList, newLHSConditionList, "condition", oldLHSTxtConditionList, newLHSTxtConditionList, "LHSTxt");
            }
            if (conditionListCompareImp != null) {
                putCompareResultToDifferent(lhsDifferent, conditionListCompareImp.getConditionListCompareResult());
//                lhsDifferent.putAll(conditionListCompareImp.getConditionListCompareResult());
            }

            oldRuleDifferent.put("LHS", lhsDifferent);
        }

        if (conditionListCompareImp != null) {
            putCompareResultToDifferent(oldRuleDifferent, conditionListCompareImp.getConditionTxtListCompareResult());
        }

    }

    private void comparRHS(JSONObject oldRule, JSONObject newRule, JSONObject oldRuleDifferent) {
        ConditionListCompareImp conditionListCompareImp = null;

//        ConditionListCompareResult ruleComparResult = new ConditionListCompareResult();

        String oldRHSConditionList = getMapValue(oldRule, "RHS");
        String newRHSConditionList = getMapValue(newRule, "RHS");
        String oldRHSConditionTxtList = getMapValue(oldRule, "RHSTxt");
        String newRHSConditionTxtList = getMapValue(newRule, "RHSTxt");
        if (!ModelComparUtil.isEqualsString(oldRHSConditionList, newRHSConditionList)) {
            conditionListCompareImp = new ConditionListCompareImp(oldRHSConditionList, newRHSConditionList, ConditionListCompareResult.RHS_TYPE, oldRHSConditionTxtList, newRHSConditionTxtList, ConditionListCompareResult.RHSTxt_TYPE);
//            comparConditionList(ruleComparResult, oldRHSConditionList, newRHSConditionList, ConditionListCompareResult.RHS_TYPE);
        }

        if (conditionListCompareImp != null) {
            putCompareResultToDifferent(oldRuleDifferent, conditionListCompareImp.getConditionTxtListCompareResult());
            putCompareResultToDifferent(oldRuleDifferent, conditionListCompareImp.getConditionListCompareResult());
//            oldRuleDifferent.putAll(ruleComparResult.getResult());
        }

    }

}























