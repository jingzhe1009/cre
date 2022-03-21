package com.bonc.frame.module.antlr.antlrcore.sqlaction.impl;
// Generated from SqlAction.g4 by ANTLR 4.5.3


import com.bonc.frame.module.antlr.antlrcore.sqlaction.SqlActionListener;
import com.bonc.frame.module.antlr.antlrcore.sqlaction.SqlActionVisitor;
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
public class SqlActionParser extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.5.3", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            T__0 = 1, T__1 = 2, T__2 = 3, T__3 = 4, T__4 = 5, T__5 = 6, T__6 = 7, T__7 = 8, T__8 = 9,
            NUMBER = 10, ID = 11, STRING = 12, EVAL = 13, ADD = 14, SUB = 15, MUL = 16, DIV = 17,
            WS = 18;
    public static final int
            RULE_exprs = 0, RULE_calcExpr = 1, RULE_action = 2, RULE_temp = 3, RULE_expr = 4,
            RULE_factor = 5, RULE_funCall = 6, RULE_params = 7;
    public static final String[] ruleNames = {
            "exprs", "calcExpr", "action", "temp", "expr", "factor", "funCall", "params"
    };

    private static final String[] _LITERAL_NAMES = {
            null, "'action'", "';'", "'['", "'.'", "']'", "'('", "')'", "'()'", "','",
            null, null, null, "'='", "'+'", "'-'", "'*'", "'/'"
    };
    private static final String[] _SYMBOLIC_NAMES = {
            null, null, null, null, null, null, null, null, null, null, "NUMBER",
            "ID", "STRING", "EVAL", "ADD", "SUB", "MUL", "DIV", "WS"
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
        return "SqlAction.g4";
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

    public SqlActionParser(TokenStream input) {
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
            if (listener instanceof SqlActionListener) ((SqlActionListener) listener).enterExprs(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SqlActionListener) ((SqlActionListener) listener).exitExprs(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SqlActionVisitor) return ((SqlActionVisitor<? extends T>) visitor).visitExprs(this);
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
        public ActionContext action() {
            return getRuleContext(ActionContext.class, 0);
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
            if (listener instanceof SqlActionListener) ((SqlActionListener) listener).enterCalcExpr(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SqlActionListener) ((SqlActionListener) listener).exitCalcExpr(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SqlActionVisitor)
                return ((SqlActionVisitor<? extends T>) visitor).visitCalcExpr(this);
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
                action();
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

    public static class ActionContext extends ParserRuleContext {
        public TempContext temp() {
            return getRuleContext(TempContext.class, 0);
        }

        public ActionContext action() {
            return getRuleContext(ActionContext.class, 0);
        }

        public ActionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_action;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SqlActionListener) ((SqlActionListener) listener).enterAction(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SqlActionListener) ((SqlActionListener) listener).exitAction(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SqlActionVisitor) return ((SqlActionVisitor<? extends T>) visitor).visitAction(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ActionContext action() throws RecognitionException {
        ActionContext _localctx = new ActionContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_action);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(21);
                temp();
                setState(24);
                _la = _input.LA(1);
                if (_la == T__1) {
                    {
                        setState(22);
                        match(T__1);
                        setState(23);
                        action();
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

    public static class TempContext extends ParserRuleContext {
        public TempContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_temp;
        }

        public TempContext() {
        }

        public void copyFrom(TempContext ctx) {
            super.copyFrom(ctx);
        }
    }

    public static class TempFunContext extends TempContext {
        public FunCallContext funCall() {
            return getRuleContext(FunCallContext.class, 0);
        }

        public TempFunContext(TempContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SqlActionListener) ((SqlActionListener) listener).enterTempFun(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SqlActionListener) ((SqlActionListener) listener).exitTempFun(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SqlActionVisitor)
                return ((SqlActionVisitor<? extends T>) visitor).visitTempFun(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class TempExprContext extends TempContext {
        public Token id;

        public ExprContext expr() {
            return getRuleContext(ExprContext.class, 0);
        }

        public List<TerminalNode> ID() {
            return getTokens(SqlActionParser.ID);
        }

        public TerminalNode ID(int i) {
            return getToken(SqlActionParser.ID, i);
        }

        public TempExprContext(TempContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SqlActionListener) ((SqlActionListener) listener).enterTempExpr(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SqlActionListener) ((SqlActionListener) listener).exitTempExpr(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SqlActionVisitor)
                return ((SqlActionVisitor<? extends T>) visitor).visitTempExpr(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class TempFunCallContext extends TempContext {
        public Token id;

        public FunCallContext funCall() {
            return getRuleContext(FunCallContext.class, 0);
        }

        public List<TerminalNode> ID() {
            return getTokens(SqlActionParser.ID);
        }

        public TerminalNode ID(int i) {
            return getToken(SqlActionParser.ID, i);
        }

        public TempFunCallContext(TempContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SqlActionListener) ((SqlActionListener) listener).enterTempFunCall(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SqlActionListener) ((SqlActionListener) listener).exitTempFunCall(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SqlActionVisitor)
                return ((SqlActionVisitor<? extends T>) visitor).visitTempFunCall(this);
            else return visitor.visitChildren(this);
        }
    }

    public final TempContext temp() throws RecognitionException {
        TempContext _localctx = new TempContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_temp);
        int _la;
        try {
            setState(51);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 3, _ctx)) {
                case 1:
                    _localctx = new TempExprContext(_localctx);
                    enterOuterAlt(_localctx, 1);
                {
                    setState(26);
                    match(T__2);
                    setState(27);
                    ((TempExprContext) _localctx).id = match(ID);
                    setState(32);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                    while (_la == T__3) {
                        {
                            {
                                setState(28);
                                match(T__3);
                                setState(29);
                                match(ID);
                            }
                        }
                        setState(34);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                    }
                    setState(35);
                    match(T__4);
                    setState(36);
                    match(EVAL);
                    setState(37);
                    expr(0);
                }
                break;
                case 2:
                    _localctx = new TempFunCallContext(_localctx);
                    enterOuterAlt(_localctx, 2);
                {
                    setState(38);
                    match(T__2);
                    setState(39);
                    ((TempFunCallContext) _localctx).id = match(ID);
                    setState(44);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                    while (_la == T__3) {
                        {
                            {
                                setState(40);
                                match(T__3);
                                setState(41);
                                match(ID);
                            }
                        }
                        setState(46);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                    }
                    setState(47);
                    match(T__4);
                    setState(48);
                    match(EVAL);
                    setState(49);
                    funCall();
                }
                break;
                case 3:
                    _localctx = new TempFunContext(_localctx);
                    enterOuterAlt(_localctx, 3);
                {
                    setState(50);
                    funCall();
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

        public TerminalNode MUL() {
            return getToken(SqlActionParser.MUL, 0);
        }

        public TerminalNode DIV() {
            return getToken(SqlActionParser.DIV, 0);
        }

        public TerminalNode ADD() {
            return getToken(SqlActionParser.ADD, 0);
        }

        public TerminalNode SUB() {
            return getToken(SqlActionParser.SUB, 0);
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
            if (listener instanceof SqlActionListener) ((SqlActionListener) listener).enterExpr(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SqlActionListener) ((SqlActionListener) listener).exitExpr(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SqlActionVisitor) return ((SqlActionVisitor<? extends T>) visitor).visitExpr(this);
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
        int _startState = 8;
        enterRecursionRule(_localctx, 8, RULE_expr, _p);
        int _la;
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                {
                    setState(54);
                    factor();
                }
                _ctx.stop = _input.LT(-1);
                setState(64);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 5, _ctx);
                while (_alt != 2 && _alt != ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        if (_parseListeners != null) triggerExitRuleEvent();
                        _prevctx = _localctx;
                        {
                            setState(62);
                            _errHandler.sync(this);
                            switch (getInterpreter().adaptivePredict(_input, 4, _ctx)) {
                                case 1: {
                                    _localctx = new ExprContext(_parentctx, _parentState);
                                    pushNewRecursionContext(_localctx, _startState, RULE_expr);
                                    setState(56);
                                    if (!(precpred(_ctx, 3)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 3)");
                                    setState(57);
                                    ((ExprContext) _localctx).op = _input.LT(1);
                                    _la = _input.LA(1);
                                    if (!(_la == MUL || _la == DIV)) {
                                        ((ExprContext) _localctx).op = (Token) _errHandler.recoverInline(this);
                                    } else {
                                        consume();
                                    }
                                    setState(58);
                                    expr(4);
                                }
                                break;
                                case 2: {
                                    _localctx = new ExprContext(_parentctx, _parentState);
                                    pushNewRecursionContext(_localctx, _startState, RULE_expr);
                                    setState(59);
                                    if (!(precpred(_ctx, 2)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 2)");
                                    setState(60);
                                    ((ExprContext) _localctx).op = _input.LT(1);
                                    _la = _input.LA(1);
                                    if (!(_la == ADD || _la == SUB)) {
                                        ((ExprContext) _localctx).op = (Token) _errHandler.recoverInline(this);
                                    } else {
                                        consume();
                                    }
                                    setState(61);
                                    expr(3);
                                }
                                break;
                            }
                        }
                    }
                    setState(66);
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

    public static class MinusContext extends FactorContext {
        public Token sign;
        public Token num;

        public TerminalNode NUMBER() {
            return getToken(SqlActionParser.NUMBER, 0);
        }

        public TerminalNode ADD() {
            return getToken(SqlActionParser.ADD, 0);
        }

        public TerminalNode SUB() {
            return getToken(SqlActionParser.SUB, 0);
        }

        public MinusContext(FactorContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SqlActionListener) ((SqlActionListener) listener).enterMinus(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SqlActionListener) ((SqlActionListener) listener).exitMinus(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SqlActionVisitor) return ((SqlActionVisitor<? extends T>) visitor).visitMinus(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class VariableContext extends FactorContext {
        public Token id;

        public List<TerminalNode> ID() {
            return getTokens(SqlActionParser.ID);
        }

        public TerminalNode ID(int i) {
            return getToken(SqlActionParser.ID, i);
        }

        public VariableContext(FactorContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SqlActionListener) ((SqlActionListener) listener).enterVariable(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SqlActionListener) ((SqlActionListener) listener).exitVariable(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SqlActionVisitor)
                return ((SqlActionVisitor<? extends T>) visitor).visitVariable(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class BracketContext extends FactorContext {
        public ExprContext expr() {
            return getRuleContext(ExprContext.class, 0);
        }

        public BracketContext(FactorContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SqlActionListener) ((SqlActionListener) listener).enterBracket(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SqlActionListener) ((SqlActionListener) listener).exitBracket(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SqlActionVisitor)
                return ((SqlActionVisitor<? extends T>) visitor).visitBracket(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class MyStringContext extends FactorContext {
        public TerminalNode STRING() {
            return getToken(SqlActionParser.STRING, 0);
        }

        public MyStringContext(FactorContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SqlActionListener) ((SqlActionListener) listener).enterMyString(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SqlActionListener) ((SqlActionListener) listener).exitMyString(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SqlActionVisitor)
                return ((SqlActionVisitor<? extends T>) visitor).visitMyString(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class FunctionContext extends FactorContext {
        public FunCallContext funCall() {
            return getRuleContext(FunCallContext.class, 0);
        }

        public FunctionContext(FactorContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SqlActionListener) ((SqlActionListener) listener).enterFunction(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SqlActionListener) ((SqlActionListener) listener).exitFunction(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SqlActionVisitor)
                return ((SqlActionVisitor<? extends T>) visitor).visitFunction(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FactorContext factor() throws RecognitionException {
        FactorContext _localctx = new FactorContext(_ctx, getState());
        enterRule(_localctx, 10, RULE_factor);
        int _la;
        try {
            setState(87);
            switch (_input.LA(1)) {
                case NUMBER:
                case ADD:
                case SUB:
                    _localctx = new MinusContext(_localctx);
                    enterOuterAlt(_localctx, 1);
                {
                    setState(68);
                    _la = _input.LA(1);
                    if (_la == ADD || _la == SUB) {
                        {
                            setState(67);
                            ((MinusContext) _localctx).sign = _input.LT(1);
                            _la = _input.LA(1);
                            if (!(_la == ADD || _la == SUB)) {
                                ((MinusContext) _localctx).sign = (Token) _errHandler.recoverInline(this);
                            } else {
                                consume();
                            }
                        }
                    }

                    setState(70);
                    ((MinusContext) _localctx).num = match(NUMBER);
                }
                break;
                case T__5:
                    _localctx = new BracketContext(_localctx);
                    enterOuterAlt(_localctx, 2);
                {
                    setState(71);
                    match(T__5);
                    setState(72);
                    expr(0);
                    setState(73);
                    match(T__6);
                }
                break;
                case T__2:
                    _localctx = new VariableContext(_localctx);
                    enterOuterAlt(_localctx, 3);
                {
                    setState(75);
                    match(T__2);
                    setState(76);
                    ((VariableContext) _localctx).id = match(ID);
                    setState(81);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                    while (_la == T__3) {
                        {
                            {
                                setState(77);
                                match(T__3);
                                setState(78);
                                match(ID);
                            }
                        }
                        setState(83);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                    }
                    setState(84);
                    match(T__4);
                }
                break;
                case ID:
                    _localctx = new FunctionContext(_localctx);
                    enterOuterAlt(_localctx, 4);
                {
                    setState(85);
                    funCall();
                }
                break;
                case STRING:
                    _localctx = new MyStringContext(_localctx);
                    enterOuterAlt(_localctx, 5);
                {
                    setState(86);
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
            return getToken(SqlActionParser.ID, 0);
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
            if (listener instanceof SqlActionListener) ((SqlActionListener) listener).enterFunCall(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SqlActionListener) ((SqlActionListener) listener).exitFunCall(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SqlActionVisitor)
                return ((SqlActionVisitor<? extends T>) visitor).visitFunCall(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FunCallContext funCall() throws RecognitionException {
        FunCallContext _localctx = new FunCallContext(_ctx, getState());
        enterRule(_localctx, 12, RULE_funCall);
        try {
            setState(96);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 9, _ctx)) {
                case 1:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(89);
                    ((FunCallContext) _localctx).name = match(ID);
                    setState(90);
                    match(T__5);
                    setState(91);
                    params();
                    setState(92);
                    match(T__6);
                }
                break;
                case 2:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(94);
                    ((FunCallContext) _localctx).name = match(ID);
                    setState(95);
                    match(T__7);
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
        public ExprContext expr() {
            return getRuleContext(ExprContext.class, 0);
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
            if (listener instanceof SqlActionListener) ((SqlActionListener) listener).enterParams(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SqlActionListener) ((SqlActionListener) listener).exitParams(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SqlActionVisitor) return ((SqlActionVisitor<? extends T>) visitor).visitParams(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ParamsContext params() throws RecognitionException {
        ParamsContext _localctx = new ParamsContext(_ctx, getState());
        enterRule(_localctx, 14, RULE_params);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(98);
                expr(0);
                setState(101);
                _la = _input.LA(1);
                if (_la == T__8) {
                    {
                        setState(99);
                        match(T__8);
                        setState(100);
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

    public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
        switch (ruleIndex) {
            case 4:
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
            "\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\24j\4\2\t\2\4\3\t" +
                    "\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\3\2\3\2\3\3\3\3\3\3" +
                    "\3\4\3\4\3\4\5\4\33\n\4\3\5\3\5\3\5\3\5\7\5!\n\5\f\5\16\5$\13\5\3\5\3" +
                    "\5\3\5\3\5\3\5\3\5\3\5\7\5-\n\5\f\5\16\5\60\13\5\3\5\3\5\3\5\3\5\5\5\66" +
                    "\n\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\7\6A\n\6\f\6\16\6D\13\6\3\7\5" +
                    "\7G\n\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\7\7R\n\7\f\7\16\7U\13\7\3" +
                    "\7\3\7\3\7\5\7Z\n\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\5\bc\n\b\3\t\3\t\3\t\5" +
                    "\th\n\t\3\t\2\3\n\n\2\4\6\b\n\f\16\20\2\4\3\2\22\23\3\2\20\21p\2\22\3" +
                    "\2\2\2\4\24\3\2\2\2\6\27\3\2\2\2\b\65\3\2\2\2\n\67\3\2\2\2\fY\3\2\2\2" +
                    "\16b\3\2\2\2\20d\3\2\2\2\22\23\5\4\3\2\23\3\3\2\2\2\24\25\7\3\2\2\25\26" +
                    "\5\6\4\2\26\5\3\2\2\2\27\32\5\b\5\2\30\31\7\4\2\2\31\33\5\6\4\2\32\30" +
                    "\3\2\2\2\32\33\3\2\2\2\33\7\3\2\2\2\34\35\7\5\2\2\35\"\7\r\2\2\36\37\7" +
                    "\6\2\2\37!\7\r\2\2 \36\3\2\2\2!$\3\2\2\2\" \3\2\2\2\"#\3\2\2\2#%\3\2\2" +
                    "\2$\"\3\2\2\2%&\7\7\2\2&\'\7\17\2\2\'\66\5\n\6\2()\7\5\2\2).\7\r\2\2*" +
                    "+\7\6\2\2+-\7\r\2\2,*\3\2\2\2-\60\3\2\2\2.,\3\2\2\2./\3\2\2\2/\61\3\2" +
                    "\2\2\60.\3\2\2\2\61\62\7\7\2\2\62\63\7\17\2\2\63\66\5\16\b\2\64\66\5\16" +
                    "\b\2\65\34\3\2\2\2\65(\3\2\2\2\65\64\3\2\2\2\66\t\3\2\2\2\678\b\6\1\2" +
                    "89\5\f\7\29B\3\2\2\2:;\f\5\2\2;<\t\2\2\2<A\5\n\6\6=>\f\4\2\2>?\t\3\2\2" +
                    "?A\5\n\6\5@:\3\2\2\2@=\3\2\2\2AD\3\2\2\2B@\3\2\2\2BC\3\2\2\2C\13\3\2\2" +
                    "\2DB\3\2\2\2EG\t\3\2\2FE\3\2\2\2FG\3\2\2\2GH\3\2\2\2HZ\7\f\2\2IJ\7\b\2" +
                    "\2JK\5\n\6\2KL\7\t\2\2LZ\3\2\2\2MN\7\5\2\2NS\7\r\2\2OP\7\6\2\2PR\7\r\2" +
                    "\2QO\3\2\2\2RU\3\2\2\2SQ\3\2\2\2ST\3\2\2\2TV\3\2\2\2US\3\2\2\2VZ\7\7\2" +
                    "\2WZ\5\16\b\2XZ\7\16\2\2YF\3\2\2\2YI\3\2\2\2YM\3\2\2\2YW\3\2\2\2YX\3\2" +
                    "\2\2Z\r\3\2\2\2[\\\7\r\2\2\\]\7\b\2\2]^\5\20\t\2^_\7\t\2\2_c\3\2\2\2`" +
                    "a\7\r\2\2ac\7\n\2\2b[\3\2\2\2b`\3\2\2\2c\17\3\2\2\2dg\5\n\6\2ef\7\13\2" +
                    "\2fh\5\20\t\2ge\3\2\2\2gh\3\2\2\2h\21\3\2\2\2\r\32\".\65@BFSYbg";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}
