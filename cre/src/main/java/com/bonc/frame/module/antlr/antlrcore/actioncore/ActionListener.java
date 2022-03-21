package com.bonc.frame.module.antlr.antlrcore.actioncore;
// Generated from Action.g4 by ANTLR 4.5.3


import com.bonc.frame.module.antlr.antlrcore.actioncore.impl.ActionParser;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ActionParser}.
 */
public interface ActionListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link ActionParser#exprs}.
     *
     * @param ctx the parse tree
     */
    void enterExprs(ActionParser.ExprsContext ctx);

    /**
     * Exit a parse tree produced by {@link ActionParser#exprs}.
     *
     * @param ctx the parse tree
     */
    void exitExprs(ActionParser.ExprsContext ctx);

    /**
     * Enter a parse tree produced by {@link ActionParser#calcExpr}.
     *
     * @param ctx the parse tree
     */
    void enterCalcExpr(ActionParser.CalcExprContext ctx);

    /**
     * Exit a parse tree produced by {@link ActionParser#calcExpr}.
     *
     * @param ctx the parse tree
     */
    void exitCalcExpr(ActionParser.CalcExprContext ctx);

    /**
     * Enter a parse tree produced by {@link ActionParser#action}.
     *
     * @param ctx the parse tree
     */
    void enterAction(ActionParser.ActionContext ctx);

    /**
     * Exit a parse tree produced by {@link ActionParser#action}.
     *
     * @param ctx the parse tree
     */
    void exitAction(ActionParser.ActionContext ctx);

    /**
     * Enter a parse tree produced by the {@code tempExpr}
     * labeled alternative in {@link ActionParser#temp}.
     *
     * @param ctx the parse tree
     */
    void enterTempExpr(ActionParser.TempExprContext ctx);

    /**
     * Exit a parse tree produced by the {@code tempExpr}
     * labeled alternative in {@link ActionParser#temp}.
     *
     * @param ctx the parse tree
     */
    void exitTempExpr(ActionParser.TempExprContext ctx);

    /**
     * Enter a parse tree produced by the {@code tempFunCall}
     * labeled alternative in {@link ActionParser#temp}.
     *
     * @param ctx the parse tree
     */
    void enterTempFunCall(ActionParser.TempFunCallContext ctx);

    /**
     * Exit a parse tree produced by the {@code tempFunCall}
     * labeled alternative in {@link ActionParser#temp}.
     *
     * @param ctx the parse tree
     */
    void exitTempFunCall(ActionParser.TempFunCallContext ctx);

    /**
     * Enter a parse tree produced by the {@code tempFun}
     * labeled alternative in {@link ActionParser#temp}.
     *
     * @param ctx the parse tree
     */
    void enterTempFun(ActionParser.TempFunContext ctx);

    /**
     * Exit a parse tree produced by the {@code tempFun}
     * labeled alternative in {@link ActionParser#temp}.
     *
     * @param ctx the parse tree
     */
    void exitTempFun(ActionParser.TempFunContext ctx);

    /**
     * Enter a parse tree produced by {@link ActionParser#expr}.
     *
     * @param ctx the parse tree
     */
    void enterExpr(ActionParser.ExprContext ctx);

    /**
     * Exit a parse tree produced by {@link ActionParser#expr}.
     *
     * @param ctx the parse tree
     */
    void exitExpr(ActionParser.ExprContext ctx);

    /**
     * Enter a parse tree produced by the {@code minus}
     * labeled alternative in {@link ActionParser#factor}.
     *
     * @param ctx the parse tree
     */
    void enterMinus(ActionParser.MinusContext ctx);

    /**
     * Exit a parse tree produced by the {@code minus}
     * labeled alternative in {@link ActionParser#factor}.
     *
     * @param ctx the parse tree
     */
    void exitMinus(ActionParser.MinusContext ctx);

    /**
     * Enter a parse tree produced by the {@code bracket}
     * labeled alternative in {@link ActionParser#factor}.
     *
     * @param ctx the parse tree
     */
    void enterBracket(ActionParser.BracketContext ctx);

    /**
     * Exit a parse tree produced by the {@code bracket}
     * labeled alternative in {@link ActionParser#factor}.
     *
     * @param ctx the parse tree
     */
    void exitBracket(ActionParser.BracketContext ctx);

    /**
     * Enter a parse tree produced by the {@code variable}
     * labeled alternative in {@link ActionParser#factor}.
     *
     * @param ctx the parse tree
     */
    void enterVariable(ActionParser.VariableContext ctx);

    /**
     * Exit a parse tree produced by the {@code variable}
     * labeled alternative in {@link ActionParser#factor}.
     *
     * @param ctx the parse tree
     */
    void exitVariable(ActionParser.VariableContext ctx);

    /**
     * Enter a parse tree produced by the {@code function}
     * labeled alternative in {@link ActionParser#factor}.
     *
     * @param ctx the parse tree
     */
    void enterFunction(ActionParser.FunctionContext ctx);

    /**
     * Exit a parse tree produced by the {@code function}
     * labeled alternative in {@link ActionParser#factor}.
     *
     * @param ctx the parse tree
     */
    void exitFunction(ActionParser.FunctionContext ctx);

    /**
     * Enter a parse tree produced by the {@code myString}
     * labeled alternative in {@link ActionParser#factor}.
     *
     * @param ctx the parse tree
     */
    void enterMyString(ActionParser.MyStringContext ctx);

    /**
     * Exit a parse tree produced by the {@code myString}
     * labeled alternative in {@link ActionParser#factor}.
     *
     * @param ctx the parse tree
     */
    void exitMyString(ActionParser.MyStringContext ctx);

    /**
     * Enter a parse tree produced by {@link ActionParser#funCall}.
     *
     * @param ctx the parse tree
     */
    void enterFunCall(ActionParser.FunCallContext ctx);

    /**
     * Exit a parse tree produced by {@link ActionParser#funCall}.
     *
     * @param ctx the parse tree
     */
    void exitFunCall(ActionParser.FunCallContext ctx);

    /**
     * Enter a parse tree produced by {@link ActionParser#params}.
     *
     * @param ctx the parse tree
     */
    void enterParams(ActionParser.ParamsContext ctx);

    /**
     * Exit a parse tree produced by {@link ActionParser#params}.
     *
     * @param ctx the parse tree
     */
    void exitParams(ActionParser.ParamsContext ctx);
}
