package com.bonc.frame.service.modelCompare;

import com.alibaba.fastjson.JSONObject;
import com.bonc.frame.entity.modelCompare.entity.ModelCompareResult;
import com.bonc.frame.entity.modelCompare.entity.ModelCompareType;
import com.bonc.frame.entity.modelCompare.entity.ConditionListCompareResult;
import com.bonc.frame.util.ModelComparUtil;
import org.springframework.stereotype.Service;

@Service
public class PathCompareImp extends AbstractModelCompare {
    @Override
    public ModelCompareType getSupport() {
        return ModelCompareType.PATH;
    }

    @Override
    public void comparNodePropsJson(ModelCompareResult modelComparResult, JSONObject oldNodeJson, JSONObject newNodeJson, String nodeKey) {
        // 节点类型一定一样,并且两个都不可能为空
        JSONObject pathNodeDifferent = new JSONObject();
        String oldNodeFrom = ModelComparUtil.getMapValue(oldNodeJson, "from");
        String newNodeFrom = ModelComparUtil.getMapValue(newNodeJson, "from");
        String oldNodeTo = ModelComparUtil.getMapValue(oldNodeJson, "to");
        String newNodeTo = ModelComparUtil.getMapValue(newNodeJson, "to");


        if (!ModelComparUtil.isEqualsString(oldNodeFrom, newNodeFrom)) {
            pathNodeDifferent.put("from", oldNodeFrom);
            pathNodeDifferent.put("new_from", newNodeFrom);
        }

        if (!ModelComparUtil.isEqualsString(oldNodeTo, newNodeTo)) {
            pathNodeDifferent.put("to", oldNodeTo);
            pathNodeDifferent.put("new_to", newNodeTo);
        }


        String oldNodePropsString = ModelComparUtil.getMapValue(oldNodeJson, "props");
        String newNodePropsString = ModelComparUtil.getMapValue(newNodeJson, "props");

        if (!ModelComparUtil.isEqualsString(oldNodePropsString, newNodePropsString)) {

            JSONObject oldNodeProps = JSONObject.parseObject(oldNodePropsString);
            JSONObject newNodeProps = JSONObject.parseObject(newNodePropsString);

            ModelComparUtil.comparJsonValueDifferent(oldNodeProps, newNodeProps, "text", pathNodeDifferent);
            //比较其他属性;
            comparNodePropsOthers(oldNodeProps, newNodeProps, pathNodeDifferent);
            //比较 props.test
            comparNodePropsTest(oldNodeProps, newNodeProps, pathNodeDifferent);

        }
        if (pathNodeDifferent != null && !pathNodeDifferent.isEmpty()) {
            pathNodeDifferent.put("pathId", nodeKey);

            modelComparResult.addUpdateNodeToList(pathNodeDifferent, ModelCompareResult.PATH_Type);
        }
//        modelComparResult.addUpdateNodeToList( pathNodeDifferent, newNodeDifferent, ModelComparResult.PATH_Type);
    }

    @Override
    public void comparNodePropsOthers(JSONObject oldNodeProps, JSONObject newNodeProps, JSONObject oldPropsDifferentJson) {


        String oldPropsPathCdt = oldNodeProps.getString("pathCdt");
        String newPropsPathCdt = newNodeProps.getString("pathCdt");
        if (!ModelComparUtil.isEqualsString(oldPropsPathCdt, newPropsPathCdt)) {
            JSONObject oldPropsPathCdtDifferent = new JSONObject();

            String oldPropsPathCdtValueString = ModelComparUtil.getJsonStringValueString(oldPropsPathCdt, "value");
            String newPropsPathCdtValueString = ModelComparUtil.getJsonStringValueString(newPropsPathCdt, "value");
            if (!ModelComparUtil.isEqualsString(oldPropsPathCdtValueString, newPropsPathCdtValueString)) {
                JSONObject oldPropsPathCdtValueDifferent = new JSONObject();
                // TODO: 判断oldPropsPathCdtValueString和newPropsPathCdtValueString 的类型,如果不是json,就不转化为json,如果是json.转化为json
                try {
                    JSONObject oldPropsPathCdtValue = JSONObject.parseObject(oldPropsPathCdtValueString);
                    JSONObject newPropsPathCdtValue = JSONObject.parseObject(newPropsPathCdtValueString);
                    //比较 union
                    ModelComparUtil.comparJsonValueDifferent(oldPropsPathCdtValue, newPropsPathCdtValue, "union", oldPropsPathCdtValueDifferent);
                    //比较 isElse
                    ModelComparUtil.comparJsonValueDifferent(oldPropsPathCdtValue, newPropsPathCdtValue, "isElse", oldPropsPathCdtValueDifferent);
                    //比较 condition
                    comparPropsPathCdtValueCondition(oldPropsPathCdtValue, newPropsPathCdtValue, oldPropsPathCdtValueDifferent);
                } catch (Exception e) {
                    oldPropsPathCdtDifferent.put("value", ModelComparUtil.getJsonStringValue(oldPropsPathCdt, "value"));
                    oldPropsPathCdtDifferent.put("new_value", ModelComparUtil.getJsonStringValue(newPropsPathCdt, "value"));
                }

                // 将比较结果放到 父节点中
                if (!oldPropsPathCdtValueDifferent.isEmpty()) {
                    ModelComparUtil.setChildToParentJSON(oldPropsPathCdtDifferent, oldPropsPathCdtValueDifferent, "value");
                }
            }
            if (!oldPropsPathCdtDifferent.isEmpty()) {
                ModelComparUtil.setChildToParentJSON(oldPropsDifferentJson, oldPropsPathCdtDifferent, "pathCdt");
            }

        }
    }

    private void comparPropsPathCdtValueCondition(JSONObject oldPropsPathCdtValue, JSONObject newPropsPathCdtValue, JSONObject oldPropsPathCdtValueDifferent) {

        ConditionListCompareImp conditionListCompareImp = null;

        String oldPropsPathCdtValueCondition = oldPropsPathCdtValue.getString("condition");
        String newPropsPathCdtValueCondition = newPropsPathCdtValue.getString("condition");
        String oldPropsPathCdtValueConditionTxt = oldPropsPathCdtValue.getString("pathCdtTxt");
        String newPropsPathCdtValueConditionTxt = newPropsPathCdtValue.getString("pathCdtTxt");
//        ConditionListCompareResult ruleComparResult = new ConditionListCompareResult();
        if (!ModelComparUtil.isEqualsString(oldPropsPathCdtValueCondition, newPropsPathCdtValueCondition)) {
            conditionListCompareImp = new ConditionListCompareImp(oldPropsPathCdtValueCondition, newPropsPathCdtValueCondition, ConditionListCompareResult.PATH_CONDITION_TYPE, oldPropsPathCdtValueConditionTxt, newPropsPathCdtValueConditionTxt, ConditionListCompareResult.PATH_CONDITION_TXT_TYPE);
//            comparConditionList(ruleComparResult, oldPropsPathCdtValueCondition, newPropsPathCdtValueCondition, ConditionListCompareResult.PATH_CONDITION_TYPE);
        }
        if (conditionListCompareImp != null) {
            putCompareResultToDifferent(oldPropsPathCdtValueDifferent, conditionListCompareImp.getConditionTxtListCompareResult());
            putCompareResultToDifferent(oldPropsPathCdtValueDifferent, conditionListCompareImp.getConditionListCompareResult());
//            oldPropsPathCdtValueDifferent.putAll(ruleComparResult.getResult());
        }

    }


}
