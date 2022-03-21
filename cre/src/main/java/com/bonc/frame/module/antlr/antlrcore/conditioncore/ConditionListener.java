package com.bonc.frame.module.antlr.antlrcore.conditioncore;
// Generated from Condition.g4 by ANTLR 4.5.3


import com.bonc.frame.module.antlr.antlrcore.conditioncore.impl.ConditionParser;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ConditionParser}.
 */
public interface ConditionListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link ConditionParser#exprs}.
     *
     * @param ctx the parse tree
     */
    void enterExprs(ConditionParser.ExprsContext ctx);

    /**
     * Exit a parse tree produced by {@link ConditionParser#exprs}.
     *
     * @param ctx the parse tree
     */
    void exitExprs(ConditionParser.ExprsContext ctx);

    /**
     * Enter a parse tree produced by {@link ConditionParser#calcExpr}.
     *
     * @param ctx the parse tree
     */
    void enterCalcExpr(ConditionParser.CalcExprContext ctx);

    /**
     * Exit a parse tree produced by {@link ConditionParser#calcExpr}.
     *
     * @param ctx the parse tree
     */
    void exitCalcExpr(ConditionParser.CalcExprContext ctx);

    /**
     * Enter a parse tree produced by {@link ConditionParser#expr}.
     *
     * @param ctx the parse tree
     */
    void enterExpr(ConditionParser.ExprContext ctx);

    /**
     * Exit a parse tree produced by {@link ConditionParser#expr}.
     *
     * @param ctx the parse tree
     */
    void exitExpr(ConditionParser.ExprContext ctx);

    /**
     * Enter a parse tree produced by the {@code minus}
     * labeled alternative in {@link ConditionParser#factor}.
     *
     * @param ctx the parse tree
     */
    void enterMinus(ConditionParser.MinusContext ctx);

    /**
     * Exit a parse tree produced by the {@code minus}
     * labeled alternative in {@link ConditionParser#factor}.
     *
     * @param ctx the parse tree
     */
    void exitMinus(ConditionParser.MinusContext ctx);

    /**
     * Enter a parse tree produced by the {@code bracket}
     * labeled alternative in {@link ConditionParser#factor}.
     *
     * @param ctx the parse tree
     */
    void enterBracket(ConditionParser.BracketContext ctx);

    /**
     * Exit a parse tree produced by the {@code bracket}
     * labeled alternative in {@link ConditionParser#factor}.
     *
     * @param ctx the parse tree
     */
    void exitBracket(ConditionParser.BracketContext ctx);

    /**
     * Enter a parse tree produced by the {@code variable}
     * labeled alternative in {@link ConditionParser#factor}.
     *
     * @param ctx the parse tree
     */
    void enterVariable(ConditionParser.VariableContext ctx);

    /**
     * Exit a parse tree produced by the {@code variable}
     * labeled alternative in {@link ConditionParser#factor}.
     *
     * @param ctx the parse tree
     */
    void exitVariable(ConditionParser.VariableContext ctx);

    /**
     * Enter a parse tree produced by the {@code function}
     * labeled alternative in {@link ConditionParser#factor}.
     *
     * @param ctx the parse tree
     */
    void enterFunction(ConditionParser.FunctionContext ctx);

    /**
     * Exit a parse tree produced by the {@code function}
     * labeled alternative in {@link ConditionParser#factor}.
     *
     * @param ctx the parse tree
     */
    void exitFunction(ConditionParser.FunctionContext ctx);

    /**
     * Enter a parse tree produced by the {@code myString}
     * labeled alternative in {@link ConditionParser#factor}.
     *
     * @param ctx the parse tree
     */
    void enterMyString(ConditionParser.MyStringContext ctx);

    /**
     * Exit a parse tree produced by the {@code myString}
     * labeled alternative in {@link ConditionParser#factor}.
     *
     * @param ctx the parse tree
     */
    void exitMyString(ConditionParser.MyStringContext ctx);

    /**
     * Enter a parse tree produced by {@link ConditionParser#funCall}.
     *
     * @param ctx the parse tree
     */
    void enterFunCall(ConditionParser.FunCallContext ctx);

    /**
     * Exit a parse tree produced by {@link ConditionParser#funCall}.
     *
     * @param ctx the parse tree
     */
    void exitFunCall(ConditionParser.FunCallContext ctx);

    /**
     * Enter a parse tree produced by {@link ConditionParser#params}.
     *
     * @param ctx the parse tree
     */
    void enterParams(ConditionParser.ParamsContext ctx);

    /**
     * Exit a parse tree produced by {@link ConditionParser#params}.
     *
     * @param ctx the parse tree
     */
    void exitParams(ConditionParser.ParamsContext ctx);

    /**
     * Enter a parse tree produced by {@link ConditionParser#arrExpr}.
     *
     * @param ctx the parse tree
     */
    void enterArrExpr(ConditionParser.ArrExprContext ctx);

    /**
     * Exit a parse tree produced by {@link ConditionParser#arrExpr}.
     *
     * @param ctx the parse tree
     */
    void exitArrExpr(ConditionParser.ArrExprContext ctx);
}
