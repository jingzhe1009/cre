package com.bonc.frame.module.antlr.antlrcore.tablerelation.impl;
// Generated from TableRelation.g4 by ANTLR 4.5.3


import com.bonc.frame.module.antlr.antlrcore.tablerelation.TableRelationListener;
import com.bonc.frame.module.antlr.antlrcore.tablerelation.TableRelationVisitor;
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
public class TableRelationParser extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.5.3", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            T__0 = 1, T__1 = 2, T__2 = 3, WS = 4, ID = 5, NUMBER = 6, AND = 7, OR = 8, GT = 9, GE = 10,
            LT = 11, LE = 12, EQUAL = 13, NE = 14;
    public static final int
            RULE_exprs = 0, RULE_calcExpr = 1, RULE_expr = 2, RULE_factor = 3;
    public static final String[] ruleNames = {
            "exprs", "calcExpr", "expr", "factor"
    };

    private static final String[] _LITERAL_NAMES = {
            null, "'check'", "'['", "']'", null, null, null, "'&&'", "'||'", "'>'",
            "'>='", "'<'", "'<='", "'='", "'!='"
    };
    private static final String[] _SYMBOLIC_NAMES = {
            null, null, null, null, "WS", "ID", "NUMBER", "AND", "OR", "GT", "GE",
            "LT", "LE", "EQUAL", "NE"
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
        return "TableRelation.g4";
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

    public TableRelationParser(TokenStream input) {
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
            if (listener instanceof TableRelationListener) ((TableRelationListener) listener).enterExprs(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof TableRelationListener) ((TableRelationListener) listener).exitExprs(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof TableRelationVisitor)
                return ((TableRelationVisitor<? extends T>) visitor).visitExprs(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ExprsContext exprs() throws RecognitionException {
        ExprsContext _localctx = new ExprsContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_exprs);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(8);
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
        public ExprContext expr() {
            return getRuleContext(ExprContext.class, 0);
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
            if (listener instanceof TableRelationListener) ((TableRelationListener) listener).enterCalcExpr(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof TableRelationListener) ((TableRelationListener) listener).exitCalcExpr(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof TableRelationVisitor)
                return ((TableRelationVisitor<? extends T>) visitor).visitCalcExpr(this);
            else return visitor.visitChildren(this);
        }
    }

    public final CalcExprContext calcExpr() throws RecognitionException {
        CalcExprContext _localctx = new CalcExprContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_calcExpr);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(10);
                match(T__0);
                setState(11);
                expr(0);
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

        public TerminalNode AND() {
            return getToken(TableRelationParser.AND, 0);
        }

        public TerminalNode OR() {
            return getToken(TableRelationParser.OR, 0);
        }

        public TerminalNode GT() {
            return getToken(TableRelationParser.GT, 0);
        }

        public TerminalNode GE() {
            return getToken(TableRelationParser.GE, 0);
        }

        public TerminalNode LT() {
            return getToken(TableRelationParser.LT, 0);
        }

        public TerminalNode LE() {
            return getToken(TableRelationParser.LE, 0);
        }

        public TerminalNode EQUAL() {
            return getToken(TableRelationParser.EQUAL, 0);
        }

        public TerminalNode NE() {
            return getToken(TableRelationParser.NE, 0);
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
            if (listener instanceof TableRelationListener) ((TableRelationListener) listener).enterExpr(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof TableRelationListener) ((TableRelationListener) listener).exitExpr(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof TableRelationVisitor)
                return ((TableRelationVisitor<? extends T>) visitor).visitExpr(this);
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
        int _startState = 4;
        enterRecursionRule(_localctx, 4, RULE_expr, _p);
        int _la;
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                {
                    setState(14);
                    factor();
                }
                _ctx.stop = _input.LT(-1);
                setState(24);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 1, _ctx);
                while (_alt != 2 && _alt != ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        if (_parseListeners != null) triggerExitRuleEvent();
                        _prevctx = _localctx;
                        {
                            setState(22);
                            _errHandler.sync(this);
                            switch (getInterpreter().adaptivePredict(_input, 0, _ctx)) {
                                case 1: {
                                    _localctx = new ExprContext(_parentctx, _parentState);
                                    pushNewRecursionContext(_localctx, _startState, RULE_expr);
                                    setState(16);
                                    if (!(precpred(_ctx, 3)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 3)");
                                    setState(17);
                                    ((ExprContext) _localctx).op = _input.LT(1);
                                    _la = _input.LA(1);
                                    if (!(_la == AND || _la == OR)) {
                                        ((ExprContext) _localctx).op = (Token) _errHandler.recoverInline(this);
                                    } else {
                                        consume();
                                    }
                                    setState(18);
                                    expr(4);
                                }
                                break;
                                case 2: {
                                    _localctx = new ExprContext(_parentctx, _parentState);
                                    pushNewRecursionContext(_localctx, _startState, RULE_expr);
                                    setState(19);
                                    if (!(precpred(_ctx, 2)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 2)");
                                    setState(20);
                                    ((ExprContext) _localctx).op = _input.LT(1);
                                    _la = _input.LA(1);
                                    if (!((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << GT) | (1L << GE) | (1L << LT) | (1L << LE) | (1L << EQUAL) | (1L << NE))) != 0))) {
                                        ((ExprContext) _localctx).op = (Token) _errHandler.recoverInline(this);
                                    } else {
                                        consume();
                                    }
                                    setState(21);
                                    expr(3);
                                }
                                break;
                            }
                        }
                    }
                    setState(26);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 1, _ctx);
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
        public FactorContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_factor;
        }

        public FactorContext() {
        }

        public void copyFrom(FactorContext ctx) {
            super.copyFrom(ctx);
        }
    }

    public static class VariableContext extends FactorContext {
        public Token id;

        public TerminalNode ID() {
            return getToken(TableRelationParser.ID, 0);
        }

        public VariableContext(FactorContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof TableRelationListener) ((TableRelationListener) listener).enterVariable(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof TableRelationListener) ((TableRelationListener) listener).exitVariable(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof TableRelationVisitor)
                return ((TableRelationVisitor<? extends T>) visitor).visitVariable(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FactorContext factor() throws RecognitionException {
        FactorContext _localctx = new FactorContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_factor);
        try {
            _localctx = new VariableContext(_localctx);
            enterOuterAlt(_localctx, 1);
            {
                setState(27);
                match(T__1);
                setState(28);
                ((VariableContext) _localctx).id = match(ID);
                setState(29);
                match(T__2);
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
            case 2:
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
            "\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\20\"\4\2\t\2\4\3" +
                    "\t\3\4\4\t\4\4\5\t\5\3\2\3\2\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3" +
                    "\4\3\4\7\4\31\n\4\f\4\16\4\34\13\4\3\5\3\5\3\5\3\5\3\5\2\3\6\6\2\4\6\b" +
                    "\2\4\3\2\t\n\3\2\13\20\37\2\n\3\2\2\2\4\f\3\2\2\2\6\17\3\2\2\2\b\35\3" +
                    "\2\2\2\n\13\5\4\3\2\13\3\3\2\2\2\f\r\7\3\2\2\r\16\5\6\4\2\16\5\3\2\2\2" +
                    "\17\20\b\4\1\2\20\21\5\b\5\2\21\32\3\2\2\2\22\23\f\5\2\2\23\24\t\2\2\2" +
                    "\24\31\5\6\4\6\25\26\f\4\2\2\26\27\t\3\2\2\27\31\5\6\4\5\30\22\3\2\2\2" +
                    "\30\25\3\2\2\2\31\34\3\2\2\2\32\30\3\2\2\2\32\33\3\2\2\2\33\7\3\2\2\2" +
                    "\34\32\3\2\2\2\35\36\7\4\2\2\36\37\7\7\2\2\37 \7\5\2\2 \t\3\2\2\2\4\30" +
                    "\32";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}
