package com.bonc.frame.module.antlr.antlrcore.sqlcdt.impl;
// Generated from SqlCdt.g4 by ANTLR 4.5.3


import com.bonc.frame.module.antlr.antlrcore.sqlcdt.SqlCdtVisitor;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;

/**
 * This class provides an empty implementation of {@link SqlCdtVisitor},
 * which can be extended to create a visitor which only needs to handle a subset
 * of the available methods.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public class SqlCdtBaseVisitor<T> extends AbstractParseTreeVisitor<T> implements SqlCdtVisitor<T> {
    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override
    public T visitParse(SqlCdtParser.ParseContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override
    public T visitExpr(SqlCdtParser.ExprContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override
    public T visitFunction_name(SqlCdtParser.Function_nameContext ctx) {
        return visitChildren(ctx);
    }
}
