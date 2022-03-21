package com.bonc.frame.module.antlr.antlrcore.sqlcdt;
// Generated from SqlCdt.g4 by ANTLR 4.5.3


import com.bonc.frame.module.antlr.antlrcore.sqlcdt.impl.SqlCdtParser;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link SqlCdtParser}.
 */
public interface SqlCdtListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link SqlCdtParser#parse}.
     *
     * @param ctx the parse tree
     */
    void enterParse(SqlCdtParser.ParseContext ctx);

    /**
     * Exit a parse tree produced by {@link SqlCdtParser#parse}.
     *
     * @param ctx the parse tree
     */
    void exitParse(SqlCdtParser.ParseContext ctx);

    /**
     * Enter a parse tree produced by {@link SqlCdtParser#expr}.
     *
     * @param ctx the parse tree
     */
    void enterExpr(SqlCdtParser.ExprContext ctx);

    /**
     * Exit a parse tree produced by {@link SqlCdtParser#expr}.
     *
     * @param ctx the parse tree
     */
    void exitExpr(SqlCdtParser.ExprContext ctx);

    /**
     * Enter a parse tree produced by {@link SqlCdtParser#function_name}.
     *
     * @param ctx the parse tree
     */
    void enterFunction_name(SqlCdtParser.Function_nameContext ctx);

    /**
     * Exit a parse tree produced by {@link SqlCdtParser#function_name}.
     *
     * @param ctx the parse tree
     */
    void exitFunction_name(SqlCdtParser.Function_nameContext ctx);
}
