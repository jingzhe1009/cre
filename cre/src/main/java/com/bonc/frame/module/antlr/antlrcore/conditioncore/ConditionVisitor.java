package com.bonc.frame.module.antlr.antlrcore.conditioncore;
// Generated from Condition.g4 by ANTLR 4.5.3


import com.bonc.frame.module.antlr.antlrcore.conditioncore.impl.ConditionParser;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link ConditionParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface ConditionVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link ConditionParser#exprs}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitExprs(ConditionParser.ExprsContext ctx);

    /**
     * Visit a parse tree produced by {@link ConditionParser#calcExpr}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCalcExpr(ConditionParser.CalcExprContext ctx);

    /**
     * Visit a parse tree produced by {@link ConditionParser#expr}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitExpr(ConditionParser.ExprContext ctx);

    /**
     * Visit a parse tree produced by the {@code minus}
     * labeled alternative in {@link ConditionParser#factor}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMinus(ConditionParser.MinusContext ctx);

    /**
     * Visit a parse tree produced by the {@code bracket}
     * labeled alternative in {@link ConditionParser#factor}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBracket(ConditionParser.BracketContext ctx);

    /**
     * Visit a parse tree produced by the {@code variable}
     * labeled alternative in {@link ConditionParser#factor}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitVariable(ConditionParser.VariableContext ctx);

    /**
     * Visit a parse tree produced by the {@code function}
     * labeled alternative in {@link ConditionParser#factor}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFunction(ConditionParser.FunctionContext ctx);

    /**
     * Visit a parse tree produced by the {@code myString}
     * labeled alternative in {@link ConditionParser#factor}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMyString(ConditionParser.MyStringContext ctx);

    /**
     * Visit a parse tree produced by {@link ConditionParser#funCall}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFunCall(ConditionParser.FunCallContext ctx);

    /**
     * Visit a parse tree produced by {@link ConditionParser#params}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitParams(ConditionParser.ParamsContext ctx);

    /**
     * Visit a parse tree produced by {@link ConditionParser#arrExpr}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitArrExpr(ConditionParser.ArrExprContext ctx);
}
