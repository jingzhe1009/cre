package com.bonc.frame.module.antlr.antlrcore.sqlcolumn;
// Generated from SqlColumn.g4 by ANTLR 4.5.3


import com.bonc.frame.module.antlr.antlrcore.sqlcolumn.impl.SqlColumnParser;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link SqlColumnParser}.
 */
public interface SqlColumnListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link SqlColumnParser#exprs}.
     *
     * @param ctx the parse tree
     */
    void enterExprs(SqlColumnParser.ExprsContext ctx);

    /**
     * Exit a parse tree produced by {@link SqlColumnParser#exprs}.
     *
     * @param ctx the parse tree
     */
    void exitExprs(SqlColumnParser.ExprsContext ctx);

    /**
     * Enter a parse tree produced by {@link SqlColumnParser#calcExpr}.
     *
     * @param ctx the parse tree
     */
    void enterCalcExpr(SqlColumnParser.CalcExprContext ctx);

    /**
     * Exit a parse tree produced by {@link SqlColumnParser#calcExpr}.
     *
     * @param ctx the parse tree
     */
    void exitCalcExpr(SqlColumnParser.CalcExprContext ctx);

    /**
     * Enter a parse tree produced by {@link SqlColumnParser#column}.
     *
     * @param ctx the parse tree
     */
    void enterColumn(SqlColumnParser.ColumnContext ctx);

    /**
     * Exit a parse tree produced by {@link SqlColumnParser#column}.
     *
     * @param ctx the parse tree
     */
    void exitColumn(SqlColumnParser.ColumnContext ctx);

    /**
     * Enter a parse tree produced by {@link SqlColumnParser#expr}.
     *
     * @param ctx the parse tree
     */
    void enterExpr(SqlColumnParser.ExprContext ctx);

    /**
     * Exit a parse tree produced by {@link SqlColumnParser#expr}.
     *
     * @param ctx the parse tree
     */
    void exitExpr(SqlColumnParser.ExprContext ctx);

    /**
     * Enter a parse tree produced by {@link SqlColumnParser#factor}.
     *
     * @param ctx the parse tree
     */
    void enterFactor(SqlColumnParser.FactorContext ctx);

    /**
     * Exit a parse tree produced by {@link SqlColumnParser#factor}.
     *
     * @param ctx the parse tree
     */
    void exitFactor(SqlColumnParser.FactorContext ctx);

    /**
     * Enter a parse tree produced by {@link SqlColumnParser#funCall}.
     *
     * @param ctx the parse tree
     */
    void enterFunCall(SqlColumnParser.FunCallContext ctx);

    /**
     * Exit a parse tree produced by {@link SqlColumnParser#funCall}.
     *
     * @param ctx the parse tree
     */
    void exitFunCall(SqlColumnParser.FunCallContext ctx);

    /**
     * Enter a parse tree produced by {@link SqlColumnParser#params}.
     *
     * @param ctx the parse tree
     */
    void enterParams(SqlColumnParser.ParamsContext ctx);

    /**
     * Exit a parse tree produced by {@link SqlColumnParser#params}.
     *
     * @param ctx the parse tree
     */
    void exitParams(SqlColumnParser.ParamsContext ctx);

    /**
     * Enter a parse tree produced by {@link SqlColumnParser#param}.
     *
     * @param ctx the parse tree
     */
    void enterParam(SqlColumnParser.ParamContext ctx);

    /**
     * Exit a parse tree produced by {@link SqlColumnParser#param}.
     *
     * @param ctx the parse tree
     */
    void exitParam(SqlColumnParser.ParamContext ctx);
}
