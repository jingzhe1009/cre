package com.bonc.frame.module.antlr.antlrcore.actioncore;
// Generated from Action.g4 by ANTLR 4.5.3


import com.bonc.frame.module.antlr.antlrcore.actioncore.impl.ActionParser;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link ActionParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface ActionVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link ActionParser#exprs}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitExprs(ActionParser.ExprsContext ctx);

    /**
     * Visit a parse tree produced by {@link ActionParser#calcExpr}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCalcExpr(ActionParser.CalcExprContext ctx);

    /**
     * Visit a parse tree produced by {@link ActionParser#action}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitAction(ActionParser.ActionContext ctx);

    /**
     * Visit a parse tree produced by the {@code tempExpr}
     * labeled alternative in {@link ActionParser#temp}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitTempExpr(ActionParser.TempExprContext ctx);

    /**
     * Visit a parse tree produced by the {@code tempFunCall}
     * labeled alternative in {@link ActionParser#temp}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitTempFunCall(ActionParser.TempFunCallContext ctx);

    /**
     * Visit a parse tree produced by the {@code tempFun}
     * labeled alternative in {@link ActionParser#temp}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitTempFun(ActionParser.TempFunContext ctx);

    /**
     * Visit a parse tree produced by {@link ActionParser#expr}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitExpr(ActionParser.ExprContext ctx);

    /**
     * Visit a parse tree produced by the {@code minus}
     * labeled alternative in {@link ActionParser#factor}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMinus(ActionParser.MinusContext ctx);

    /**
     * Visit a parse tree produced by the {@code bracket}
     * labeled alternative in {@link ActionParser#factor}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBracket(ActionParser.BracketContext ctx);

    /**
     * Visit a parse tree produced by the {@code variable}
     * labeled alternative in {@link ActionParser#factor}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitVariable(ActionParser.VariableContext ctx);

    /**
     * Visit a parse tree produced by the {@code function}
     * labeled alternative in {@link ActionParser#factor}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFunction(ActionParser.FunctionContext ctx);

    /**
     * Visit a parse tree produced by the {@code myString}
     * labeled alternative in {@link ActionParser#factor}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMyString(ActionParser.MyStringContext ctx);

    /**
     * Visit a parse tree produced by {@link ActionParser#funCall}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     * @throws Exception
     */
    T visitFunCall(ActionParser.FunCallContext ctx) throws Exception;

    /**
     * Visit a parse tree produced by {@link ActionParser#params}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitParams(ActionParser.ParamsContext ctx);
}
