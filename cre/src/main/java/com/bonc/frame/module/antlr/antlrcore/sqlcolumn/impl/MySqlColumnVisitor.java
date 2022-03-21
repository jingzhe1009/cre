package com.bonc.frame.module.antlr.antlrcore.sqlcolumn.impl;


import java.util.HashMap;
import java.util.Map;

/**
 * @author qxl
 * @version 1.0.0
 * @date 2016年12月27日 下午7:36:42
 */
public class MySqlColumnVisitor extends SqlColumnBaseVisitor<String> {

    private Map<String, String> funCallMap;

    public MySqlColumnVisitor() {
        super();
        funCallMap = new HashMap<String, String>();
    }

    public MySqlColumnVisitor(Map<String, String> funCallMap) {
        this.funCallMap = funCallMap;
    }

    @Override
    public String visitFunCall(SqlColumnParser.FunCallContext ctx) {
        String funName = ctx.name.getText();
        if (funName == null || funName.isEmpty()) {
            return ctx.getText();
        }
        if (!funCallMap.containsKey(funName.toLowerCase())) {
            return "###ERROR###:" + funName + "###END###";
        }
        return ctx.getText();
    }

}
