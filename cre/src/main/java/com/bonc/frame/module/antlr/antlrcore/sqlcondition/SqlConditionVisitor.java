package com.bonc.frame.module.antlr.antlrcore.sqlcondition;
// Generated from SqlCondition.g4 by ANTLR 4.5.3


import com.bonc.frame.module.antlr.antlrcore.sqlcondition.impl.SqlConditionParser;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link SqlConditionParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface SqlConditionVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link SqlConditionParser#parse}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitParse(SqlConditionParser.ParseContext ctx);

    /**
     * Visit a parse tree produced by {@link SqlConditionParser#expr}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitExpr(SqlConditionParser.ExprContext ctx);

    /**
     * Visit a parse tree produced by {@link SqlConditionParser#keyword}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitKeyword(SqlConditionParser.KeywordContext ctx);

    /**
     * Visit a parse tree produced by {@link SqlConditionParser#name}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitName(SqlConditionParser.NameContext ctx);

    /**
     * Visit a parse tree produced by {@link SqlConditionParser#function_name}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFunction_name(SqlConditionParser.Function_nameContext ctx);

    /**
     * Visit a parse tree produced by {@link SqlConditionParser#database_name}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDatabase_name(SqlConditionParser.Database_nameContext ctx);

    /**
     * Visit a parse tree produced by {@link SqlConditionParser#table_name}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitTable_name(SqlConditionParser.Table_nameContext ctx);

    /**
     * Visit a parse tree produced by {@link SqlConditionParser#column_name}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitColumn_name(SqlConditionParser.Column_nameContext ctx);

    /**
     * Visit a parse tree produced by {@link SqlConditionParser#any_name}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitAny_name(SqlConditionParser.Any_nameContext ctx);
}
