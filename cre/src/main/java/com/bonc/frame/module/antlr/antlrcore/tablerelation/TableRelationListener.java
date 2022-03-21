package com.bonc.frame.module.antlr.antlrcore.tablerelation;
// Generated from TableRelation.g4 by ANTLR 4.5.3


import com.bonc.frame.module.antlr.antlrcore.tablerelation.impl.TableRelationParser;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link TableRelationParser}.
 */
public interface TableRelationListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link TableRelationParser#exprs}.
     *
     * @param ctx the parse tree
     */
    void enterExprs(TableRelationParser.ExprsContext ctx);

    /**
     * Exit a parse tree produced by {@link TableRelationParser#exprs}.
     *
     * @param ctx the parse tree
     */
    void exitExprs(TableRelationParser.ExprsContext ctx);

    /**
     * Enter a parse tree produced by {@link TableRelationParser#calcExpr}.
     *
     * @param ctx the parse tree
     */
    void enterCalcExpr(TableRelationParser.CalcExprContext ctx);

    /**
     * Exit a parse tree produced by {@link TableRelationParser#calcExpr}.
     *
     * @param ctx the parse tree
     */
    void exitCalcExpr(TableRelationParser.CalcExprContext ctx);

    /**
     * Enter a parse tree produced by {@link TableRelationParser#expr}.
     *
     * @param ctx the parse tree
     */
    void enterExpr(TableRelationParser.ExprContext ctx);

    /**
     * Exit a parse tree produced by {@link TableRelationParser#expr}.
     *
     * @param ctx the parse tree
     */
    void exitExpr(TableRelationParser.ExprContext ctx);

    /**
     * Enter a parse tree produced by the {@code variable}
     * labeled alternative in {@link TableRelationParser#factor}.
     *
     * @param ctx the parse tree
     */
    void enterVariable(TableRelationParser.VariableContext ctx);

    /**
     * Exit a parse tree produced by the {@code variable}
     * labeled alternative in {@link TableRelationParser#factor}.
     *
     * @param ctx the parse tree
     */
    void exitVariable(TableRelationParser.VariableContext ctx);
}
