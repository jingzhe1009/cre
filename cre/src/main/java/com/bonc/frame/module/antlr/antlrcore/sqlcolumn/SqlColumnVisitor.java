package com.bonc.frame.module.antlr.antlrcore.sqlcolumn;
// Generated from SqlColumn.g4 by ANTLR 4.5.3


import com.bonc.frame.module.antlr.antlrcore.sqlcolumn.impl.SqlColumnParser;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link SqlColumnParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface SqlColumnVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link SqlColumnParser#exprs}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitExprs(SqlColumnParser.ExprsContext ctx);

    /**
     * Visit a parse tree produced by {@link SqlColumnParser#calcExpr}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCalcExpr(SqlColumnParser.CalcExprContext ctx);

    /**
     * Visit a parse tree produced by {@link SqlColumnParser#column}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitColumn(SqlColumnParser.ColumnContext ctx);

    /**
     * Visit a parse tree produced by {@link SqlColumnParser#expr}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitExpr(SqlColumnParser.ExprContext ctx);

    /**
     * Visit a parse tree produced by {@link SqlColumnParser#factor}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFactor(SqlColumnParser.FactorContext ctx);

    /**
     * Visit a parse tree produced by {@link SqlColumnParser#funCall}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFunCall(SqlColumnParser.FunCallContext ctx);

    /**
     * Visit a parse tree produced by {@link SqlColumnParser#params}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitParams(SqlColumnParser.ParamsContext ctx);

    /**
     * Visit a parse tree produced by {@link SqlColumnParser#param}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitParam(SqlColumnParser.ParamContext ctx);
}
