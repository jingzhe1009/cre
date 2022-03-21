package com.bonc.frame.module.antlr.antlrcore.actioncore.impl;


import com.bonc.frame.module.antlr.antlrcore.actioncore.impl.ActionParser.*;
import com.bonc.framework.util.FrameLogUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author qxl
 * @version 1.0.0
 * @date 2016年11月15日 上午11:12:42
 */
public class MyActionVisitor extends ActionBaseVisitor<String> {
    private Map<String, String> funCallMap;

    public MyActionVisitor() {
        super();
        funCallMap = new HashMap<String, String>();
    }

    public MyActionVisitor(Map<String, String> funCallMap) {
        this.funCallMap = funCallMap;
    }

    @Override
    public String visitTempExpr(ActionParser.TempExprContext ctx) {
//		System.out.println("visitTempExpr...");
        String varCode = ctx.ID().getText();
        String value = visit(ctx.expr());
        varCode = "m[\"" + varCode + "\"]";
        return varCode + "=" + value;
    }

    @Override
    public String visitTempFun(ActionParser.TempFunContext ctx) {
//		System.out.println("visitTempFun...");
        return super.visitTempFun(ctx);
    }

    @Override
    public String visitFunCall(ActionParser.FunCallContext ctx) throws Exception {
//		System.out.println("visitFunCall..."+ctx.getChildCount());
        String txt = ctx.getText();
        String funName = ctx.name.getText();
        String fullFunName = null;
        if (funCallMap.containsKey(funName)) {
            fullFunName = funCallMap.get(funName) + "." + funName;
        } else {
            FrameLogUtil.error(getClass(), "Invalid function name." + funName);
            throw new Exception("Invalid function name." + funName);
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
    public String visitVariable(ActionParser.VariableContext ctx) {
//		System.out.println("visitVariable..."+ctx.getText());
        String varCode = ctx.ID().getText();
        varCode = "m[\"" + varCode + "\"]";
        return varCode;
    }

    @Override
    public String visitExpr(ActionParser.ExprContext ctx) {
//		System.out.println("visitExpr...");
        int cc = ctx.getChildCount();
        if (cc == 3) {
            switch (ctx.op.getType()) {
                case ActionParser.ADD:
                    return visit(ctx.expr(0)) + "+" + visit(ctx.expr(1));
                case ActionParser.SUB:
                    return visit(ctx.expr(0)) + "-" + visit(ctx.expr(1));
                case ActionParser.MUL:
                    return visit(ctx.expr(0)) + "*" + visit(ctx.expr(1));
                case ActionParser.DIV:
                    return visit(ctx.expr(0)) + "/" + visit(ctx.expr(1));
            }
        } else if (cc == 1) {
            return visit(ctx.getChild(0));
        }
        FrameLogUtil.error(getClass(), "Invalid expression.");
        throw new RuntimeException();
    }

    @Override
    public String visitAction(ActionParser.ActionContext ctx) {
//		System.out.println("visitAction...");
        int cc = ctx.getChildCount();
        if (cc == 3) {
            return visit(ctx.getChild(0)) + ";" + visit(ctx.getChild(2));
        } else if (cc == 1) {
            return visit(ctx.getChild(0));
        }
        FrameLogUtil.error(getClass(), "Analysis error.");
        throw new RuntimeException("Analysis error.");
    }

    @Override
    public String visitTempFunCall(ActionParser.TempFunCallContext ctx) {
//		System.out.println("visitTempFunCall...");
        return super.visitTempFunCall(ctx);
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
    public String visitFunction(FunctionContext ctx) {
//		System.out.println("visitFunction...");
        return super.visitFunction(ctx);
    }

    @Override
    public String visitMyString(MyStringContext ctx) {
//		System.out.println("visitMyString...");
        return ctx.getText();
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

}
