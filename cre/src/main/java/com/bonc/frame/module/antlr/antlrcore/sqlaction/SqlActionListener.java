package com.bonc.frame.module.antlr.antlrcore.sqlaction;
// Generated from SqlAction.g4 by ANTLR 4.5.3


import com.bonc.frame.module.antlr.antlrcore.sqlaction.impl.SqlActionParser;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link SqlActionParser}.
 */
public interface SqlActionListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link SqlActionParser#exprs}.
     *
     * @param ctx the parse tree
     */
    void enterExprs(SqlActionParser.ExprsContext ctx);

    /**
     * Exit a parse tree produced by {@link SqlActionParser#exprs}.
     *
     * @param ctx the parse tree
     */
    void exitExprs(SqlActionParser.ExprsContext ctx);

    /**
     * Enter a parse tree produced by {@link SqlActionParser#calcExpr}.
     *
     * @param ctx the parse tree
     */
    void enterCalcExpr(SqlActionParser.CalcExprContext ctx);

    /**
     * Exit a parse tree produced by {@link SqlActionParser#calcExpr}.
     *
     * @param ctx the parse tree
     */
    void exitCalcExpr(SqlActionParser.CalcExprContext ctx);

    /**
     * Enter a parse tree produced by {@link SqlActionParser#action}.
     *
     * @param ctx the parse tree
     */
    void enterAction(SqlActionParser.ActionContext ctx);

    /**
     * Exit a parse tree produced by {@link SqlActionParser#action}.
     *
     * @param ctx the parse tree
     */
    void exitAction(SqlActionParser.ActionContext ctx);

    /**
     * Enter a parse tree produced by the {@code tempExpr}
     * labeled alternative in {@link SqlActionParser#temp}.
     *
     * @param ctx the parse tree
     */
    void enterTempExpr(SqlActionParser.TempExprContext ctx);

    /**
     * Exit a parse tree produced by the {@code tempExpr}
     * labeled alternative in {@link SqlActionParser#temp}.
     *
     * @param ctx the parse tree
     */
    void exitTempExpr(SqlActionParser.TempExprContext ctx);

    /**
     * Enter a parse tree produced by the {@code tempFunCall}
     * labeled alternative in {@link SqlActionParser#temp}.
     *
     * @param ctx the parse tree
     */
    void enterTempFunCall(SqlActionParser.TempFunCallContext ctx);

    /**
     * Exit a parse tree produced by the {@code tempFunCall}
     * labeled alternative in {@link SqlActionParser#temp}.
     *
     * @param ctx the parse tree
     */
    void exitTempFunCall(SqlActionParser.TempFunCallContext ctx);

    /**
     * Enter a parse tree produced by the {@code tempFun}
     * labeled alternative in {@link SqlActionParser#temp}.
     *
     * @param ctx the parse tree
     */
    void enterTempFun(SqlActionParser.TempFunContext ctx);

    /**
     * Exit a parse tree produced by the {@code tempFun}
     * labeled alternative in {@link SqlActionParser#temp}.
     *
     * @param ctx the parse tree
     */
    void exitTempFun(SqlActionParser.TempFunContext ctx);

    /**
     * Enter a parse tree produced by {@link SqlActionParser#expr}.
     *
     * @param ctx the parse tree
     */
    void enterExpr(SqlActionParser.ExprContext ctx);

    /**
     * Exit a parse tree produced by {@link SqlActionParser#expr}.
     *
     * @param ctx the parse tree
     */
    void exitExpr(SqlActionParser.ExprContext ctx);

    /**
     * Enter a parse tree produced by the {@code minus}
     * labeled alternative in {@link SqlActionParser#factor}.
     *
     * @param ctx the parse tree
     */
    void enterMinus(SqlActionParser.MinusContext ctx);

    /**
     * Exit a parse tree produced by the {@code minus}
     * labeled alternative in {@link SqlActionParser#factor}.
     *
     * @param ctx the parse tree
     */
    void exitMinus(SqlActionParser.MinusContext ctx);

    /**
     * Enter a parse tree produced by the {@code bracket}
     * labeled alternative in {@link SqlActionParser#factor}.
     *
     * @param ctx the parse tree
     */
    void enterBracket(SqlActionParser.BracketContext ctx);

    /**
     * Exit a parse tree produced by the {@code bracket}
     * labeled alternative in {@link SqlActionParser#factor}.
     *
     * @param ctx the parse tree
     */
    void exitBracket(SqlActionParser.BracketContext ctx);

    /**
     * Enter a parse tree produced by the {@code variable}
     * labeled alternative in {@link SqlActionParser#factor}.
     *
     * @param ctx the parse tree
     */
    void enterVariable(SqlActionParser.VariableContext ctx);

    /**
     * Exit a parse tree produced by the {@code variable}
     * labeled alternative in {@link SqlActionParser#factor}.
     *
     * @param ctx the parse tree
     */
    void exitVariable(SqlActionParser.VariableContext ctx);

    /**
     * Enter a parse tree produced by the {@code function}
     * labeled alternative in {@link SqlActionParser#factor}.
     *
     * @param ctx the parse tree
     */
    void enterFunction(SqlActionParser.FunctionContext ctx);

    /**
     * Exit a parse tree produced by the {@code function}
     * labeled alternative in {@link SqlActionParser#factor}.
     *
     * @param ctx the parse tree
     */
    void exitFunction(SqlActionParser.FunctionContext ctx);

    /**
     * Enter a parse tree produced by the {@code myString}
     * labeled alternative in {@link SqlActionParser#factor}.
     *
     * @param ctx the parse tree
     */
    void enterMyString(SqlActionParser.MyStringContext ctx);

    /**
     * Exit a parse tree produced by the {@code myString}
     * labeled alternative in {@link SqlActionParser#factor}.
     *
     * @param ctx the parse tree
     */
    void exitMyString(SqlActionParser.MyStringContext ctx);

    /**
     * Enter a parse tree produced by {@link SqlActionParser#funCall}.
     *
     * @param ctx the parse tree
     */
    void enterFunCall(SqlActionParser.FunCallContext ctx);

    /**
     * Exit a parse tree produced by {@link SqlActionParser#funCall}.
     *
     * @param ctx the parse tree
     */
    void exitFunCall(SqlActionParser.FunCallContext ctx);

    /**
     * Enter a parse tree produced by {@link SqlActionParser#params}.
     *
     * @param ctx the parse tree
     */
    void enterParams(SqlActionParser.ParamsContext ctx);

    /**
     * Exit a parse tree produced by {@link SqlActionParser#params}.
     *
     * @param ctx the parse tree
     */
    void exitParams(SqlActionParser.ParamsContext ctx);
}
