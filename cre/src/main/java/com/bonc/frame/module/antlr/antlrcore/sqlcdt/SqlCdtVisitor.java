package com.bonc.frame.module.antlr.antlrcore.sqlcdt;
// Generated from SqlCdt.g4 by ANTLR 4.5.3


import com.bonc.frame.module.antlr.antlrcore.sqlcdt.impl.SqlCdtParser;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link SqlCdtParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface SqlCdtVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link SqlCdtParser#parse}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitParse(SqlCdtParser.ParseContext ctx);

    /**
     * Visit a parse tree produced by {@link SqlCdtParser#expr}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitExpr(SqlCdtParser.ExprContext ctx);

    /**
     * Visit a parse tree produced by {@link SqlCdtParser#function_name}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFunction_name(SqlCdtParser.Function_nameContext ctx);
}
