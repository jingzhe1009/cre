package com.bonc.frame.entity.modelCompare.entity.entity;

import java.util.List;

/**
 * @Author: wangzhengbao
 * @DATE: 2019/12/12 15:20
 */
public class RECTObject {
    private String rectId;
    private String type;
    private ObjectValue text;
    private ObjectValue new_text;
    private ObjectValue ointerface;
    private ObjectValue new_ointerface;
    private ObjectValue isPublic;
    private ObjectValue new_isPublic;
    private ObjectValue ruleSetId;
    private ObjectValue new_ruleSetId;

    private RuleSetAction action;

    class RuleSetAction {
        List<RuleSetActionValue> value;
        RuleSetActionaaa ADD;
        RuleSetActionaaa UPDATE;
        RuleSetActionaaa DELETE;

        class RuleSetActionaaa {
            List<RuleSetActionValue> value;
        }
    }

}

class RuleSetActionValue {
    private String uid;
    private String actRuleName;
    private String new_actRuleName;
    private boolean isEndFlow;
    private boolean new_isEndFlow;
    private boolean isEndAction;
    private boolean new_isEndAction;


    class RuleSetActionValueLHS {
        private String union;
        private String new_union;
        private List<Object> condition;

        class RuleSetActionValueLHSaaa {
            private List<Object> condition;
        }

    }
}

class ConditionExpression {
    private String conditionId;
    private String kpiId;
    private String varId;
    private String opt;
    private String new_opt;
    private String valueType;
    private String new_valueType;
    private String value;
    private String new_value;

}
