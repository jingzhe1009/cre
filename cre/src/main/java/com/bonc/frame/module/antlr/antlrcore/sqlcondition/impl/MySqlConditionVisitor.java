package com.bonc.frame.module.antlr.antlrcore.sqlcondition.impl;


import java.util.HashMap;
import java.util.Map;

/**
 * @author qxl
 * @version 1.0.0
 * @date 2016年12月28日 上午9:10:33
 */
public class MySqlConditionVisitor extends SqlConditionBaseVisitor<String> {

    private Map<String, String> funCallMap;

    public MySqlConditionVisitor() {
        super();
        funCallMap = new HashMap<String, String>();
    }

    public MySqlConditionVisitor(Map<String, String> funCallMap) {
        this.funCallMap = funCallMap;
    }

    @Override
    public String visitFunction_name(SqlConditionParser.Function_nameContext ctx) {
        String funName = ctx.getText();
        if (funName == null || funName.isEmpty()) {
            return ctx.getText();
        }
        if (!funCallMap.containsKey(funName.toLowerCase())) {
            return "###ERROR###:" + funName + "###END###";
        }
        return ctx.getText();
    }

}
