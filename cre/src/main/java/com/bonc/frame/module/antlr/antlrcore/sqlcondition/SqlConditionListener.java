package com.bonc.frame.module.antlr.antlrcore.sqlcondition;
// Generated from SqlCondition.g4 by ANTLR 4.5.3


import com.bonc.frame.module.antlr.antlrcore.sqlcondition.impl.SqlConditionParser;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link SqlConditionParser}.
 */
public interface SqlConditionListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link SqlConditionParser#parse}.
     *
     * @param ctx the parse tree
     */
    void enterParse(SqlConditionParser.ParseContext ctx);

    /**
     * Exit a parse tree produced by {@link SqlConditionParser#parse}.
     *
     * @param ctx the parse tree
     */
    void exitParse(SqlConditionParser.ParseContext ctx);

    /**
     * Enter a parse tree produced by {@link SqlConditionParser#expr}.
     *
     * @param ctx the parse tree
     */
    void enterExpr(SqlConditionParser.ExprContext ctx);

    /**
     * Exit a parse tree produced by {@link SqlConditionParser#expr}.
     *
     * @param ctx the parse tree
     */
    void exitExpr(SqlConditionParser.ExprContext ctx);

    /**
     * Enter a parse tree produced by {@link SqlConditionParser#keyword}.
     *
     * @param ctx the parse tree
     */
    void enterKeyword(SqlConditionParser.KeywordContext ctx);

    /**
     * Exit a parse tree produced by {@link SqlConditionParser#keyword}.
     *
     * @param ctx the parse tree
     */
    void exitKeyword(SqlConditionParser.KeywordContext ctx);

    /**
     * Enter a parse tree produced by {@link SqlConditionParser#name}.
     *
     * @param ctx the parse tree
     */
    void enterName(SqlConditionParser.NameContext ctx);

    /**
     * Exit a parse tree produced by {@link SqlConditionParser#name}.
     *
     * @param ctx the parse tree
     */
    void exitName(SqlConditionParser.NameContext ctx);

    /**
     * Enter a parse tree produced by {@link SqlConditionParser#function_name}.
     *
     * @param ctx the parse tree
     */
    void enterFunction_name(SqlConditionParser.Function_nameContext ctx);

    /**
     * Exit a parse tree produced by {@link SqlConditionParser#function_name}.
     *
     * @param ctx the parse tree
     */
    void exitFunction_name(SqlConditionParser.Function_nameContext ctx);

    /**
     * Enter a parse tree produced by {@link SqlConditionParser#database_name}.
     *
     * @param ctx the parse tree
     */
    void enterDatabase_name(SqlConditionParser.Database_nameContext ctx);

    /**
     * Exit a parse tree produced by {@link SqlConditionParser#database_name}.
     *
     * @param ctx the parse tree
     */
    void exitDatabase_name(SqlConditionParser.Database_nameContext ctx);

    /**
     * Enter a parse tree produced by {@link SqlConditionParser#table_name}.
     *
     * @param ctx the parse tree
     */
    void enterTable_name(SqlConditionParser.Table_nameContext ctx);

    /**
     * Exit a parse tree produced by {@link SqlConditionParser#table_name}.
     *
     * @param ctx the parse tree
     */
    void exitTable_name(SqlConditionParser.Table_nameContext ctx);

    /**
     * Enter a parse tree produced by {@link SqlConditionParser#column_name}.
     *
     * @param ctx the parse tree
     */
    void enterColumn_name(SqlConditionParser.Column_nameContext ctx);

    /**
     * Exit a parse tree produced by {@link SqlConditionParser#column_name}.
     *
     * @param ctx the parse tree
     */
    void exitColumn_name(SqlConditionParser.Column_nameContext ctx);

    /**
     * Enter a parse tree produced by {@link SqlConditionParser#any_name}.
     *
     * @param ctx the parse tree
     */
    void enterAny_name(SqlConditionParser.Any_nameContext ctx);

    /**
     * Exit a parse tree produced by {@link SqlConditionParser#any_name}.
     *
     * @param ctx the parse tree
     */
    void exitAny_name(SqlConditionParser.Any_nameContext ctx);
}
