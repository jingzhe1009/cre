package com.bonc.frame.module.antlr.antlrcore.tablerelation.impl;
// Generated from TableRelation.g4 by ANTLR 4.5.3


import com.bonc.frame.module.antlr.antlrcore.tablerelation.TableRelationVisitor;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;

/**
 * This class provides an empty implementation of {@link TableRelationVisitor},
 * which can be extended to create a visitor which only needs to handle a subset
 * of the available methods.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public class TableRelationBaseVisitor<T> extends AbstractParseTreeVisitor<T> implements TableRelationVisitor<T> {
    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override
    public T visitExprs(TableRelationParser.ExprsContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override
    public T visitCalcExpr(TableRelationParser.CalcExprContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override
    public T visitExpr(TableRelationParser.ExprContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override
    public T visitVariable(TableRelationParser.VariableContext ctx) {
        return visitChildren(ctx);
    }
}
