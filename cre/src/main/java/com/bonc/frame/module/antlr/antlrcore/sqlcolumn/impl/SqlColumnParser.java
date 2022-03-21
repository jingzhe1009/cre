package com.bonc.frame.module.antlr.antlrcore.sqlcolumn.impl;

// Generated from SqlColumn.g4 by ANTLR 4.5.3


import com.bonc.frame.module.antlr.antlrcore.sqlcolumn.SqlColumnListener;
import com.bonc.frame.module.antlr.antlrcore.sqlcolumn.SqlColumnVisitor;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SqlColumnParser extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.5.3", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            T__0 = 1, T__1 = 2, T__2 = 3, T__3 = 4, T__4 = 5, T__5 = 6, T__6 = 7, T__7 = 8, T__8 = 9,
            T__9 = 10, T__10 = 11, T__11 = 12, T__12 = 13, T__13 = 14, T__14 = 15, T__15 = 16, T__16 = 17,
            T__17 = 18, T__18 = 19, T__19 = 20, T__20 = 21, T__21 = 22, WS = 23, ID = 24, NUMBER = 25,
            STRING = 26, ADD = 27, SUB = 28, EVAL = 29, LT = 30, LT_EQ = 31, GT = 32, GT_EQ = 33,
            NOT_EQ1 = 34, NOT_EQ2 = 35;
    public static final int
            RULE_exprs = 0, RULE_calcExpr = 1, RULE_column = 2, RULE_expr = 3, RULE_factor = 4,
            RULE_funCall = 5, RULE_params = 6, RULE_param = 7;
    public static final String[] ruleNames = {
            "exprs", "calcExpr", "column", "expr", "factor", "funCall", "params",
            "param"
    };

    private static final String[] _LITERAL_NAMES = {
            null, "'check'", "'case'", "'CASE'", "'WHEN'", "'when'", "'then'", "'THEN'",
            "'else'", "'ELSE'", "'end'", "'END'", "'is null'", "'IS NULL'", "'is not null'",
            "'IS NOT NULL'", "'('", "')'", "'(*)'", "','", "'['", "'.'", "']'", null,
            null, null, null, "'+'", "'-'", "'='", "'<'", "'<='", "'>'", "'>='", "'!='",
            "'<>'"
    };
    private static final String[] _SYMBOLIC_NAMES = {
            null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null, null, null, "WS",
            "ID", "NUMBER", "STRING", "ADD", "SUB", "EVAL", "LT", "LT_EQ", "GT", "GT_EQ",
            "NOT_EQ1", "NOT_EQ2"
    };
    public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

    /**
     * @deprecated Use {@link #VOCABULARY} instead.
     */
    @Deprecated
    public static final String[] tokenNames;

    static {
        tokenNames = new String[_SYMBOLIC_NAMES.length];
        for (int i = 0; i < tokenNames.length; i++) {
            tokenNames[i] = VOCABULARY.getLiteralName(i);
            if (tokenNames[i] == null) {
                tokenNames[i] = VOCABULARY.getSymbolicName(i);
            }

            if (tokenNames[i] == null) {
                tokenNames[i] = "<INVALID>";
            }
        }
    }

    @Override
    @Deprecated
    public String[] getTokenNames() {
        return tokenNames;
    }

    @Override

    public Vocabulary getVocabulary() {
        return VOCABULARY;
    }

    @Override
    public String getGrammarFileName() {
        return "SqlColumn.g4";
    }

    @Override
    public String[] getRuleNames() {
        return ruleNames;
    }

    @Override
    public String getSerializedATN() {
        return _serializedATN;
    }

    @Override
    public ATN getATN() {
        return _ATN;
    }

    public SqlColumnParser(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    public static class ExprsContext extends ParserRuleContext {
        public CalcExprContext calcExpr() {
            return getRuleContext(CalcExprContext.class, 0);
        }

        public ExprsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_exprs;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SqlColumnListener) ((SqlColumnListener) listener).enterExprs(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SqlColumnListener) ((SqlColumnListener) listener).exitExprs(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SqlColumnVisitor) return ((SqlColumnVisitor<? extends T>) visitor).visitExprs(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ExprsContext exprs() throws RecognitionException {
        ExprsContext _localctx = new ExprsContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_exprs);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(16);
                calcExpr();
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class CalcExprContext extends ParserRuleContext {
        public ColumnContext column() {
            return getRuleContext(ColumnContext.class, 0);
        }

        public CalcExprContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_calcExpr;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SqlColumnListener) ((SqlColumnListener) listener).enterCalcExpr(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SqlColumnListener) ((SqlColumnListener) listener).exitCalcExpr(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SqlColumnVisitor)
                return ((SqlColumnVisitor<? extends T>) visitor).visitCalcExpr(this);
            else return visitor.visitChildren(this);
        }
    }

    public final CalcExprContext calcExpr() throws RecognitionException {
        CalcExprContext _localctx = new CalcExprContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_calcExpr);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(18);
                match(T__0);
                setState(19);
                column();
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class ColumnContext extends ParserRuleContext {
        public FunCallContext funCall() {
            return getRuleContext(FunCallContext.class, 0);
        }

        public List<ExprContext> expr() {
            return getRuleContexts(ExprContext.class);
        }

        public ExprContext expr(int i) {
            return getRuleContext(ExprContext.class, i);
        }

        public ColumnContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_column;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SqlColumnListener) ((SqlColumnListener) listener).enterColumn(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SqlColumnListener) ((SqlColumnListener) listener).exitColumn(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SqlColumnVisitor) return ((SqlColumnVisitor<? extends T>) visitor).visitColumn(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ColumnContext column() throws RecognitionException {
        ColumnContext _localctx = new ColumnContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_column);
        int _la;
        try {
            setState(41);
            switch (_input.LA(1)) {
                case ID:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(21);
                    funCall();
                }
                break;
                case T__1:
                case T__2:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(22);
                    _la = _input.LA(1);
                    if (!(_la == T__1 || _la == T__2)) {
                        _errHandler.recoverInline(this);
                    } else {
                        consume();
                    }
                    setState(24);
                    _la = _input.LA(1);
                    if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__15) | (1L << T__19) | (1L << NUMBER) | (1L << STRING) | (1L << ADD) | (1L << SUB))) != 0)) {
                        {
                            setState(23);
                            expr(0);
                        }
                    }

                    setState(31);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                    do {
                        {
                            {
                                setState(26);
                                _la = _input.LA(1);
                                if (!(_la == T__3 || _la == T__4)) {
                                    _errHandler.recoverInline(this);
                                } else {
                                    consume();
                                }
                                setState(27);
                                expr(0);
                                setState(28);
                                _la = _input.LA(1);
                                if (!(_la == T__5 || _la == T__6)) {
                                    _errHandler.recoverInline(this);
                                } else {
                                    consume();
                                }
                                setState(29);
                                expr(0);
                            }
                        }
                        setState(33);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                    } while (_la == T__3 || _la == T__4);
                    setState(37);
                    _la = _input.LA(1);
                    if (_la == T__7 || _la == T__8) {
                        {
                            setState(35);
                            _la = _input.LA(1);
                            if (!(_la == T__7 || _la == T__8)) {
                                _errHandler.recoverInline(this);
                            } else {
                                consume();
                            }
                            setState(36);
                            expr(0);
                        }
                    }

                    setState(39);
                    _la = _input.LA(1);
                    if (!(_la == T__9 || _la == T__10)) {
                        _errHandler.recoverInline(this);
                    } else {
                        consume();
                    }
                }
                break;
                default:
                    throw new NoViableAltException(this);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class ExprContext extends ParserRuleContext {
        public Token op;

        public FactorContext factor() {
            return getRuleContext(FactorContext.class, 0);
        }

        public List<ExprContext> expr() {
            return getRuleContexts(ExprContext.class);
        }

        public ExprContext expr(int i) {
            return getRuleContext(ExprContext.class, i);
        }

        public TerminalNode EVAL() {
            return getToken(SqlColumnParser.EVAL, 0);
        }

        public TerminalNode LT() {
            return getToken(SqlColumnParser.LT, 0);
        }

        public TerminalNode LT_EQ() {
            return getToken(SqlColumnParser.LT_EQ, 0);
        }

        public TerminalNode GT() {
            return getToken(SqlColumnParser.GT, 0);
        }

        public TerminalNode GT_EQ() {
            return getToken(SqlColumnParser.GT_EQ, 0);
        }

        public TerminalNode NOT_EQ1() {
            return getToken(SqlColumnParser.NOT_EQ1, 0);
        }

        public TerminalNode NOT_EQ2() {
            return getToken(SqlColumnParser.NOT_EQ2, 0);
        }

        public ExprContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_expr;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SqlColumnListener) ((SqlColumnListener) listener).enterExpr(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SqlColumnListener) ((SqlColumnListener) listener).exitExpr(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SqlColumnVisitor) return ((SqlColumnVisitor<? extends T>) visitor).visitExpr(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ExprContext expr() throws RecognitionException {
        return expr(0);
    }

    private ExprContext expr(int _p) throws RecognitionException {
        ParserRuleContext _parentctx = _ctx;
        int _parentState = getState();
        ExprContext _localctx = new ExprContext(_ctx, _parentState);
        ExprContext _prevctx = _localctx;
        int _startState = 6;
        enterRecursionRule(_localctx, 6, RULE_expr, _p);
        int _la;
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                {
                    setState(44);
                    factor();
                }
                _ctx.stop = _input.LT(-1);
                setState(53);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 5, _ctx);
                while (_alt != 2 && _alt != ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        if (_parseListeners != null) triggerExitRuleEvent();
                        _prevctx = _localctx;
                        {
                            setState(51);
                            _errHandler.sync(this);
                            switch (getInterpreter().adaptivePredict(_input, 4, _ctx)) {
                                case 1: {
                                    _localctx = new ExprContext(_parentctx, _parentState);
                                    pushNewRecursionContext(_localctx, _startState, RULE_expr);
                                    setState(46);
                                    if (!(precpred(_ctx, 3)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 3)");
                                    setState(47);
                                    ((ExprContext) _localctx).op = _input.LT(1);
                                    _la = _input.LA(1);
                                    if (!((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << EVAL) | (1L << LT) | (1L << LT_EQ) | (1L << GT) | (1L << GT_EQ) | (1L << NOT_EQ1) | (1L << NOT_EQ2))) != 0))) {
                                        ((ExprContext) _localctx).op = (Token) _errHandler.recoverInline(this);
                                    } else {
                                        consume();
                                    }
                                    setState(48);
                                    expr(4);
                                }
                                break;
                                case 2: {
                                    _localctx = new ExprContext(_parentctx, _parentState);
                                    pushNewRecursionContext(_localctx, _startState, RULE_expr);
                                    setState(49);
                                    if (!(precpred(_ctx, 2)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 2)");
                                    setState(50);
                                    _la = _input.LA(1);
                                    if (!((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__11) | (1L << T__12) | (1L << T__13) | (1L << T__14))) != 0))) {
                                        _errHandler.recoverInline(this);
                                    } else {
                                        consume();
                                    }
                                }
                                break;
                            }
                        }
                    }
                    setState(55);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 5, _ctx);
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            unrollRecursionContexts(_parentctx);
        }
        return _localctx;
    }

    public static class FactorContext extends ParserRuleContext {
        public Token sign;
        public Token num;

        public TerminalNode NUMBER() {
            return getToken(SqlColumnParser.NUMBER, 0);
        }

        public TerminalNode ADD() {
            return getToken(SqlColumnParser.ADD, 0);
        }

        public TerminalNode SUB() {
            return getToken(SqlColumnParser.SUB, 0);
        }

        public ExprContext expr() {
            return getRuleContext(ExprContext.class, 0);
        }

        public ParamContext param() {
            return getRuleContext(ParamContext.class, 0);
        }

        public TerminalNode STRING() {
            return getToken(SqlColumnParser.STRING, 0);
        }

        public FactorContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_factor;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SqlColumnListener) ((SqlColumnListener) listener).enterFactor(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SqlColumnListener) ((SqlColumnListener) listener).exitFactor(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SqlColumnVisitor) return ((SqlColumnVisitor<? extends T>) visitor).visitFactor(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FactorContext factor() throws RecognitionException {
        FactorContext _localctx = new FactorContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_factor);
        int _la;
        try {
            setState(66);
            switch (_input.LA(1)) {
                case NUMBER:
                case ADD:
                case SUB:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(57);
                    _la = _input.LA(1);
                    if (_la == ADD || _la == SUB) {
                        {
                            setState(56);
                            ((FactorContext) _localctx).sign = _input.LT(1);
                            _la = _input.LA(1);
                            if (!(_la == ADD || _la == SUB)) {
                                ((FactorContext) _localctx).sign = (Token) _errHandler.recoverInline(this);
                            } else {
                                consume();
                            }
                        }
                    }

                    setState(59);
                    ((FactorContext) _localctx).num = match(NUMBER);
                }
                break;
                case T__15:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(60);
                    match(T__15);
                    setState(61);
                    expr(0);
                    setState(62);
                    match(T__16);
                }
                break;
                case T__19:
                    enterOuterAlt(_localctx, 3);
                {
                    setState(64);
                    param();
                }
                break;
                case STRING:
                    enterOuterAlt(_localctx, 4);
                {
                    setState(65);
                    match(STRING);
                }
                break;
                default:
                    throw new NoViableAltException(this);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class FunCallContext extends ParserRuleContext {
        public Token name;

        public ParamsContext params() {
            return getRuleContext(ParamsContext.class, 0);
        }

        public TerminalNode ID() {
            return getToken(SqlColumnParser.ID, 0);
        }

        public FunCallContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_funCall;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SqlColumnListener) ((SqlColumnListener) listener).enterFunCall(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SqlColumnListener) ((SqlColumnListener) listener).exitFunCall(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SqlColumnVisitor)
                return ((SqlColumnVisitor<? extends T>) visitor).visitFunCall(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FunCallContext funCall() throws RecognitionException {
        FunCallContext _localctx = new FunCallContext(_ctx, getState());
        enterRule(_localctx, 10, RULE_funCall);
        try {
            setState(75);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 8, _ctx)) {
                case 1:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(68);
                    ((FunCallContext) _localctx).name = match(ID);
                    setState(69);
                    match(T__15);
                    setState(70);
                    params();
                    setState(71);
                    match(T__16);
                }
                break;
                case 2:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(73);
                    ((FunCallContext) _localctx).name = match(ID);
                    setState(74);
                    match(T__17);
                }
                break;
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class ParamsContext extends ParserRuleContext {
        public ParamContext param() {
            return getRuleContext(ParamContext.class, 0);
        }

        public ParamsContext params() {
            return getRuleContext(ParamsContext.class, 0);
        }

        public ParamsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_params;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SqlColumnListener) ((SqlColumnListener) listener).enterParams(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SqlColumnListener) ((SqlColumnListener) listener).exitParams(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SqlColumnVisitor) return ((SqlColumnVisitor<? extends T>) visitor).visitParams(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ParamsContext params() throws RecognitionException {
        ParamsContext _localctx = new ParamsContext(_ctx, getState());
        enterRule(_localctx, 12, RULE_params);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(77);
                param();
                setState(80);
                _la = _input.LA(1);
                if (_la == T__18) {
                    {
                        setState(78);
                        match(T__18);
                        setState(79);
                        params();
                    }
                }

            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class ParamContext extends ParserRuleContext {
        public List<TerminalNode> ID() {
            return getTokens(SqlColumnParser.ID);
        }

        public TerminalNode ID(int i) {
            return getToken(SqlColumnParser.ID, i);
        }

        public ParamContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_param;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SqlColumnListener) ((SqlColumnListener) listener).enterParam(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SqlColumnListener) ((SqlColumnListener) listener).exitParam(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SqlColumnVisitor) return ((SqlColumnVisitor<? extends T>) visitor).visitParam(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ParamContext param() throws RecognitionException {
        ParamContext _localctx = new ParamContext(_ctx, getState());
        enterRule(_localctx, 14, RULE_param);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(82);
                match(T__19);
                setState(89);
                _errHandler.sync(this);
                switch (getInterpreter().adaptivePredict(_input, 11, _ctx)) {
                    case 1: {
                        setState(85);
                        _errHandler.sync(this);
                        switch (getInterpreter().adaptivePredict(_input, 10, _ctx)) {
                            case 1: {
                                setState(83);
                                match(ID);
                                setState(84);
                                match(T__20);
                            }
                            break;
                        }
                        setState(87);
                        match(ID);
                        setState(88);
                        match(T__20);
                    }
                    break;
                }
                setState(91);
                match(ID);
                setState(92);
                match(T__21);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
        switch (ruleIndex) {
            case 3:
                return expr_sempred((ExprContext) _localctx, predIndex);
        }
        return true;
    }

    private boolean expr_sempred(ExprContext _localctx, int predIndex) {
        switch (predIndex) {
            case 0:
                return precpred(_ctx, 3);
            case 1:
                return precpred(_ctx, 2);
        }
        return true;
    }

    public static final String _serializedATN =
            "\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3%a\4\2\t\2\4\3\t\3" +
                    "\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\3\2\3\2\3\3\3\3\3\3\3" +
                    "\4\3\4\3\4\5\4\33\n\4\3\4\3\4\3\4\3\4\3\4\6\4\"\n\4\r\4\16\4#\3\4\3\4" +
                    "\5\4(\n\4\3\4\3\4\5\4,\n\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\7\5\66\n\5" +
                    "\f\5\16\59\13\5\3\6\5\6<\n\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\5\6E\n\6\3\7" +
                    "\3\7\3\7\3\7\3\7\3\7\3\7\5\7N\n\7\3\b\3\b\3\b\5\bS\n\b\3\t\3\t\3\t\5\t" +
                    "X\n\t\3\t\3\t\5\t\\\n\t\3\t\3\t\3\t\3\t\2\3\b\n\2\4\6\b\n\f\16\20\2\n" +
                    "\3\2\4\5\3\2\6\7\3\2\b\t\3\2\n\13\3\2\f\r\3\2\37%\3\2\16\21\3\2\35\36" +
                    "f\2\22\3\2\2\2\4\24\3\2\2\2\6+\3\2\2\2\b-\3\2\2\2\nD\3\2\2\2\fM\3\2\2" +
                    "\2\16O\3\2\2\2\20T\3\2\2\2\22\23\5\4\3\2\23\3\3\2\2\2\24\25\7\3\2\2\25" +
                    "\26\5\6\4\2\26\5\3\2\2\2\27,\5\f\7\2\30\32\t\2\2\2\31\33\5\b\5\2\32\31" +
                    "\3\2\2\2\32\33\3\2\2\2\33!\3\2\2\2\34\35\t\3\2\2\35\36\5\b\5\2\36\37\t" +
                    "\4\2\2\37 \5\b\5\2 \"\3\2\2\2!\34\3\2\2\2\"#\3\2\2\2#!\3\2\2\2#$\3\2\2" +
                    "\2$\'\3\2\2\2%&\t\5\2\2&(\5\b\5\2\'%\3\2\2\2\'(\3\2\2\2()\3\2\2\2)*\t" +
                    "\6\2\2*,\3\2\2\2+\27\3\2\2\2+\30\3\2\2\2,\7\3\2\2\2-.\b\5\1\2./\5\n\6" +
                    "\2/\67\3\2\2\2\60\61\f\5\2\2\61\62\t\7\2\2\62\66\5\b\5\6\63\64\f\4\2\2" +
                    "\64\66\t\b\2\2\65\60\3\2\2\2\65\63\3\2\2\2\669\3\2\2\2\67\65\3\2\2\2\67" +
                    "8\3\2\2\28\t\3\2\2\29\67\3\2\2\2:<\t\t\2\2;:\3\2\2\2;<\3\2\2\2<=\3\2\2" +
                    "\2=E\7\33\2\2>?\7\22\2\2?@\5\b\5\2@A\7\23\2\2AE\3\2\2\2BE\5\20\t\2CE\7" +
                    "\34\2\2D;\3\2\2\2D>\3\2\2\2DB\3\2\2\2DC\3\2\2\2E\13\3\2\2\2FG\7\32\2\2" +
                    "GH\7\22\2\2HI\5\16\b\2IJ\7\23\2\2JN\3\2\2\2KL\7\32\2\2LN\7\24\2\2MF\3" +
                    "\2\2\2MK\3\2\2\2N\r\3\2\2\2OR\5\20\t\2PQ\7\25\2\2QS\5\16\b\2RP\3\2\2\2" +
                    "RS\3\2\2\2S\17\3\2\2\2T[\7\26\2\2UV\7\32\2\2VX\7\27\2\2WU\3\2\2\2WX\3" +
                    "\2\2\2XY\3\2\2\2YZ\7\32\2\2Z\\\7\27\2\2[W\3\2\2\2[\\\3\2\2\2\\]\3\2\2" +
                    "\2]^\7\32\2\2^_\7\30\2\2_\21\3\2\2\2\16\32#\'+\65\67;DMRW[";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}
