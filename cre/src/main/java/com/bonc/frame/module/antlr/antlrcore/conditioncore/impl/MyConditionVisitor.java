package com.bonc.frame.module.antlr.antlrcore.conditioncore.impl;


import com.bonc.frame.module.antlr.antlrcore.conditioncore.impl.ConditionParser.*;
import com.bonc.framework.util.FrameLogUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 表达式规则中的条件部分的解析规则
 * 其中[变量]-->m["变量"]
 * 函数-->包名.函数
 *
 * @author qxl
 * @version 1.0.0
 * @date 2016年11月15日 下午6:40:55
 */
public class MyConditionVisitor extends ConditionBaseVisitor<String> {
    private Map<String, String> funCallMap;

    public MyConditionVisitor() {
        super();
        funCallMap = new HashMap<String, String>();
    }

    public MyConditionVisitor(Map<String, String> funCallMap) {
        this.funCallMap = funCallMap;
    }


    @Override
    public String visitExpr(ExprContext ctx) {
//		System.out.println("visitExpr...");
        int cc = ctx.getChildCount();
        if (cc == 3) {
            switch (ctx.op.getType()) {
                case ConditionParser.AND:
                    return visit(ctx.expr(0)) + "&&" + visit(ctx.expr(1));
                case ConditionParser.OR:
                    return visit(ctx.expr(0)) + "||" + visit(ctx.expr(1));
                case ConditionParser.ADD:
                    return visit(ctx.expr(0)) + "+" + visit(ctx.expr(1));
                case ConditionParser.SUB:
                    return visit(ctx.expr(0)) + "-" + visit(ctx.expr(1));
                case ConditionParser.MUL:
                    return visit(ctx.expr(0)) + "*" + visit(ctx.expr(1));
                case ConditionParser.DIV:
                    return visit(ctx.expr(0)) + "/" + visit(ctx.expr(1));
                case ConditionParser.GT:
                    return visit(ctx.expr(0)) + ">" + visit(ctx.expr(1));
                case ConditionParser.GE:
                    return visit(ctx.expr(0)) + ">=" + visit(ctx.expr(1));
                case ConditionParser.LT:
                    return visit(ctx.expr(0)) + "<" + visit(ctx.expr(1));
                case ConditionParser.LE:
                    return visit(ctx.expr(0)) + "<=" + visit(ctx.expr(1));
                case ConditionParser.EQUAL:
                    return visit(ctx.expr(0)) + "==" + visit(ctx.expr(1));
                case ConditionParser.NE:
                    return visit(ctx.expr(0)) + "!=" + visit(ctx.expr(1));
                case ConditionParser.CT:
                    return visit(ctx.getChild(0)) + " contains " + visit(ctx.getChild(2));
                case ConditionParser.NCT:
                    return visit(ctx.getChild(0)) + " not contains " + visit(ctx.getChild(2));
                case ConditionParser.MO:
                    return visit(ctx.getChild(0)) + " memberOf " + visit(ctx.getChild(2));
                case ConditionParser.NMO:
                    return visit(ctx.getChild(0)) + " not memberOf " + visit(ctx.getChild(2));
            }
        } else if (cc == 1) {
            return visit(ctx.getChild(0));
        }
        FrameLogUtil.error(getClass(), "Invalid expression.");
        throw new RuntimeException();
    }

    @Override
    public String visitMinus(MinusContext ctx) {
//		System.out.println("visitMinus..."+ctx.getText());
        return ctx.getText();
    }

    @Override
    public String visitBracket(BracketContext ctx) {
//		System.out.println("visitBracket..."+ctx.getChildCount());
        int cc = ctx.getChildCount();
        if (cc == 3) {
            return "(" + visit(ctx.getChild(1)) + ")";
        }
        return super.visitBracket(ctx);
    }

    @Override
    public String visitVariable(VariableContext ctx) {
//		System.out.println("visitVariable..."+ctx.getText());
        String varCode = ctx.ID().getText();
        varCode = "m[\"" + varCode + "\"]";
        return varCode;
    }

    @Override
    public String visitFunction(FunctionContext ctx) {
        return super.visitFunction(ctx);
    }

    @Override
    public String visitMyString(MyStringContext ctx) {
//		System.out.println("visitMyString...");
        return ctx.getText();
    }

    @Override
    public String visitFunCall(FunCallContext ctx) {
//		System.out.println("visitFunCall..."+ctx.getChildCount());
        String txt = ctx.getText();
        String funName = ctx.name.getText();
        String fullFunName = null;
        if (funCallMap.containsKey(funName)) {
            fullFunName = funCallMap.get(funName) + "." + funName;
        } else {
            FrameLogUtil.error(getClass(), "Invalid function name." + funName);
            throw new RuntimeException("Invalid function name." + funName);
        }
        StringBuilder sb = new StringBuilder("(");
        if (ctx.getChildCount() == 4) {
            String value = visit(ctx.getChild(2));
            sb.append(value);
        }
        sb.append(")");
//		System.out.println("sb--"+sb);
        txt = txt.replace(funName, fullFunName);
        return fullFunName + sb.toString();
    }

    @Override
    public String visitParams(ParamsContext ctx) {
//		System.out.println("visitParams..."+ctx.getChildCount()+"--"+ctx.getText());
        int cc = ctx.getChildCount();
        if (cc == 3) {
            return visit(ctx.getChild(0)) + "," + visit(ctx.getChild(2));
        } else if (cc == 1) {
            return visit(ctx.getChild(0));
        }
        FrameLogUtil.error(getClass(), "Analysis error.");
        throw new RuntimeException("Analysis error.");
    }

    @Override
    public String visitArrExpr(ArrExprContext ctx) {
        return ctx.getText();
    }


}
