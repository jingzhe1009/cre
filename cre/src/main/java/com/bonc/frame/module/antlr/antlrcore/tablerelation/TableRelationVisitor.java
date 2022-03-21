package com.bonc.frame.module.antlr.antlrcore.tablerelation;
// Generated from TableRelation.g4 by ANTLR 4.5.3


import com.bonc.frame.module.antlr.antlrcore.tablerelation.impl.TableRelationParser;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link TableRelationParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface TableRelationVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link TableRelationParser#exprs}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitExprs(TableRelationParser.ExprsContext ctx);

    /**
     * Visit a parse tree produced by {@link TableRelationParser#calcExpr}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCalcExpr(TableRelationParser.CalcExprContext ctx);

    /**
     * Visit a parse tree produced by {@link TableRelationParser#expr}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitExpr(TableRelationParser.ExprContext ctx);

    /**
     * Visit a parse tree produced by the {@code variable}
     * labeled alternative in {@link TableRelationParser#factor}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitVariable(TableRelationParser.VariableContext ctx);
}
