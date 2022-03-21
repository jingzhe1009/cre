package com.bonc.frame.module.antlr.antlrcore.sqlaction;
// Generated from SqlAction.g4 by ANTLR 4.5.3


import com.bonc.frame.module.antlr.antlrcore.sqlaction.impl.SqlActionParser;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link SqlActionParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface SqlActionVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link SqlActionParser#exprs}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitExprs(SqlActionParser.ExprsContext ctx);

    /**
     * Visit a parse tree produced by {@link SqlActionParser#calcExpr}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCalcExpr(SqlActionParser.CalcExprContext ctx);

    /**
     * Visit a parse tree produced by {@link SqlActionParser#action}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitAction(SqlActionParser.ActionContext ctx);

    /**
     * Visit a parse tree produced by the {@code tempExpr}
     * labeled alternative in {@link SqlActionParser#temp}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitTempExpr(SqlActionParser.TempExprContext ctx);

    /**
     * Visit a parse tree produced by the {@code tempFunCall}
     * labeled alternative in {@link SqlActionParser#temp}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitTempFunCall(SqlActionParser.TempFunCallContext ctx);

    /**
     * Visit a parse tree produced by the {@code tempFun}
     * labeled alternative in {@link SqlActionParser#temp}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitTempFun(SqlActionParser.TempFunContext ctx);

    /**
     * Visit a parse tree produced by {@link SqlActionParser#expr}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitExpr(SqlActionParser.ExprContext ctx);

    /**
     * Visit a parse tree produced by the {@code minus}
     * labeled alternative in {@link SqlActionParser#factor}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMinus(SqlActionParser.MinusContext ctx);

    /**
     * Visit a parse tree produced by the {@code bracket}
     * labeled alternative in {@link SqlActionParser#factor}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBracket(SqlActionParser.BracketContext ctx);

    /**
     * Visit a parse tree produced by the {@code variable}
     * labeled alternative in {@link SqlActionParser#factor}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitVariable(SqlActionParser.VariableContext ctx);

    /**
     * Visit a parse tree produced by the {@code function}
     * labeled alternative in {@link SqlActionParser#factor}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFunction(SqlActionParser.FunctionContext ctx);

    /**
     * Visit a parse tree produced by the {@code myString}
     * labeled alternative in {@link SqlActionParser#factor}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMyString(SqlActionParser.MyStringContext ctx);

    /**
     * Visit a parse tree produced by {@link SqlActionParser#funCall}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFunCall(SqlActionParser.FunCallContext ctx);

    /**
     * Visit a parse tree produced by {@link SqlActionParser#params}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitParams(SqlActionParser.ParamsContext ctx);
}
