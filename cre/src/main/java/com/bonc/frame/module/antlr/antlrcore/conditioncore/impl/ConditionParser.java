package com.bonc.frame.module.antlr.antlrcore.conditioncore.impl;

// Generated from Condition.g4 by ANTLR 4.5.3


import com.bonc.frame.module.antlr.antlrcore.conditioncore.ConditionListener;
import com.bonc.frame.module.antlr.antlrcore.conditioncore.ConditionVisitor;
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
public class ConditionParser extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.5.3", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            T__0 = 1, T__1 = 2, T__2 = 3, T__3 = 4, T__4 = 5, T__5 = 6, T__6 = 7, T__7 = 8, WS = 9,
            ID = 10, NUMBER = 11, STRING = 12, AND = 13, OR = 14, ADD = 15, SUB = 16, MUL = 17, DIV = 18,
            GT = 19, GE = 20, LT = 21, LE = 22, EQUAL = 23, NE = 24, CT = 25, NCT = 26, MO = 27, NMO = 28;
    public static final int
            RULE_exprs = 0, RULE_calcExpr = 1, RULE_expr = 2, RULE_factor = 3, RULE_funCall = 4,
            RULE_params = 5, RULE_arrExpr = 6;
    public static final String[] ruleNames = {
            "exprs", "calcExpr", "expr", "factor", "funCall", "params", "arrExpr"
    };

    private static final String[] _LITERAL_NAMES = {
            null, "'condition'", "'('", "')'", "'['", "']'", "','", "'{'", "'}'",
            null, null, null, null, "'&&'", "'||'", "'+'", "'-'", "'*'", "'/'", "'>'",
            "'>='", "'<'", "'<='", "'=='", "'!='", "'包含'", "'不包含'", "'属于'", "'不属于'"
    };
    private static final String[] _SYMBOLIC_NAMES = {
            null, null, null, null, null, null, null, null, null, "WS", "ID", "NUMBER",
            "STRING", "AND", "OR", "ADD", "SUB", "MUL", "DIV", "GT", "GE", "LT", "LE",
            "EQUAL", "NE", "CT", "NCT", "MO", "NMO"
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
        return "Condition.g4";
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

    public ConditionParser(TokenStream input) {
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
            if (listener instanceof ConditionListener) ((ConditionListener) listener).enterExprs(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ConditionListener) ((ConditionListener) listener).exitExprs(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ConditionVisitor) return ((ConditionVisitor<? extends T>) visitor).visitExprs(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ExprsContext exprs() throws RecognitionException {
        ExprsContext _localctx = new ExprsContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_exprs);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(14);
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
            if (listener instanceof ConditionListener) ((ConditionListener) listener).enterCalcExpr(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ConditionListener) ((ConditionListener) listener).exitCalcExpr(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ConditionVisitor)
                return ((ConditionVisitor<? extends T>) visitor).visitCalcExpr(this);
            else return visitor.visitChildren(this);
        }
    }

    public final CalcExprContext calcExpr() throws RecognitionException {
        CalcExprContext _localctx = new CalcExprContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_calcExpr);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(16);
                match(T__0);
                setState(17);
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
            return getToken(ConditionParser.AND, 0);
        }

        public TerminalNode OR() {
            return getToken(ConditionParser.OR, 0);
        }

        public TerminalNode MUL() {
            return getToken(ConditionParser.MUL, 0);
        }

        public TerminalNode DIV() {
            return getToken(ConditionParser.DIV, 0);
        }

        public TerminalNode ADD() {
            return getToken(ConditionParser.ADD, 0);
        }

        public TerminalNode SUB() {
            return getToken(ConditionParser.SUB, 0);
        }

        public TerminalNode GT() {
            return getToken(ConditionParser.GT, 0);
        }

        public TerminalNode GE() {
            return getToken(ConditionParser.GE, 0);
        }

        public TerminalNode LT() {
            return getToken(ConditionParser.LT, 0);
        }

        public TerminalNode LE() {
            return getToken(ConditionParser.LE, 0);
        }

        public TerminalNode EQUAL() {
            return getToken(ConditionParser.EQUAL, 0);
        }

        public TerminalNode NE() {
            return getToken(ConditionParser.NE, 0);
        }

        public ArrExprContext arrExpr() {
            return getRuleContext(ArrExprContext.class, 0);
        }

        public TerminalNode CT() {
            return getToken(ConditionParser.CT, 0);
        }

        public TerminalNode NCT() {
            return getToken(ConditionParser.NCT, 0);
        }

        public TerminalNode MO() {
            return getToken(ConditionParser.MO, 0);
        }

        public TerminalNode NMO() {
            return getToken(ConditionParser.NMO, 0);
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
            if (listener instanceof ConditionListener) ((ConditionListener) listener).enterExpr(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ConditionListener) ((ConditionListener) listener).exitExpr(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ConditionVisitor) return ((ConditionVisitor<? extends T>) visitor).visitExpr(this);
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
                    setState(20);
                    factor();
                }
                _ctx.stop = _input.LT(-1);
                setState(39);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 1, _ctx);
                while (_alt != 2 && _alt != ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        if (_parseListeners != null) triggerExitRuleEvent();
                        _prevctx = _localctx;
                        {
                            setState(37);
                            _errHandler.sync(this);
                            switch (getInterpreter().adaptivePredict(_input, 0, _ctx)) {
                                case 1: {
                                    _localctx = new ExprContext(_parentctx, _parentState);
                                    pushNewRecursionContext(_localctx, _startState, RULE_expr);
                                    setState(22);
                                    if (!(precpred(_ctx, 6)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 6)");
                                    setState(23);
                                    ((ExprContext) _localctx).op = _input.LT(1);
                                    _la = _input.LA(1);
                                    if (!(_la == AND || _la == OR)) {
                                        ((ExprContext) _localctx).op = (Token) _errHandler.recoverInline(this);
                                    } else {
                                        consume();
                                    }
                                    setState(24);
                                    expr(7);
                                }
                                break;
                                case 2: {
                                    _localctx = new ExprContext(_parentctx, _parentState);
                                    pushNewRecursionContext(_localctx, _startState, RULE_expr);
                                    setState(25);
                                    if (!(precpred(_ctx, 5)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 5)");
                                    setState(26);
                                    ((ExprContext) _localctx).op = _input.LT(1);
                                    _la = _input.LA(1);
                                    if (!(_la == MUL || _la == DIV)) {
                                        ((ExprContext) _localctx).op = (Token) _errHandler.recoverInline(this);
                                    } else {
                                        consume();
                                    }
                                    setState(27);
                                    expr(6);
                                }
                                break;
                                case 3: {
                                    _localctx = new ExprContext(_parentctx, _parentState);
                                    pushNewRecursionContext(_localctx, _startState, RULE_expr);
                                    setState(28);
                                    if (!(precpred(_ctx, 4)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 4)");
                                    setState(29);
                                    ((ExprContext) _localctx).op = _input.LT(1);
                                    _la = _input.LA(1);
                                    if (!(_la == ADD || _la == SUB)) {
                                        ((ExprContext) _localctx).op = (Token) _errHandler.recoverInline(this);
                                    } else {
                                        consume();
                                    }
                                    setState(30);
                                    expr(5);
                                }
                                break;
                                case 4: {
                                    _localctx = new ExprContext(_parentctx, _parentState);
                                    pushNewRecursionContext(_localctx, _startState, RULE_expr);
                                    setState(31);
                                    if (!(precpred(_ctx, 3)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 3)");
                                    setState(32);
                                    ((ExprContext) _localctx).op = _input.LT(1);
                                    _la = _input.LA(1);
                                    if (!((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << GT) | (1L << GE) | (1L << LT) | (1L << LE) | (1L << EQUAL) | (1L << NE))) != 0))) {
                                        ((ExprContext) _localctx).op = (Token) _errHandler.recoverInline(this);
                                    } else {
                                        consume();
                                    }
                                    setState(33);
                                    expr(4);
                                }
                                break;
                                case 5: {
                                    _localctx = new ExprContext(_parentctx, _parentState);
                                    pushNewRecursionContext(_localctx, _startState, RULE_expr);
                                    setState(34);
                                    if (!(precpred(_ctx, 2)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 2)");
                                    setState(35);
                                    ((ExprContext) _localctx).op = _input.LT(1);
                                    _la = _input.LA(1);
                                    if (!((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << CT) | (1L << NCT) | (1L << MO) | (1L << NMO))) != 0))) {
                                        ((ExprContext) _localctx).op = (Token) _errHandler.recoverInline(this);
                                    } else {
                                        consume();
                                    }
                                    setState(36);
                                    arrExpr();
                                }
                                break;
                            }
                        }
                    }
                    setState(41);
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

    public static class MinusContext extends FactorContext {
        public Token sign;
        public Token num;

        public TerminalNode NUMBER() {
            return getToken(ConditionParser.NUMBER, 0);
        }

        public TerminalNode ADD() {
            return getToken(ConditionParser.ADD, 0);
        }

        public TerminalNode SUB() {
            return getToken(ConditionParser.SUB, 0);
        }

        public MinusContext(FactorContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ConditionListener) ((ConditionListener) listener).enterMinus(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ConditionListener) ((ConditionListener) listener).exitMinus(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ConditionVisitor) return ((ConditionVisitor<? extends T>) visitor).visitMinus(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class VariableContext extends FactorContext {
        public Token id;

        public TerminalNode ID() {
            return getToken(ConditionParser.ID, 0);
        }

        public VariableContext(FactorContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ConditionListener) ((ConditionListener) listener).enterVariable(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ConditionListener) ((ConditionListener) listener).exitVariable(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ConditionVisitor)
                return ((ConditionVisitor<? extends T>) visitor).visitVariable(this);
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
            if (listener instanceof ConditionListener) ((ConditionListener) listener).enterBracket(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ConditionListener) ((ConditionListener) listener).exitBracket(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ConditionVisitor)
                return ((ConditionVisitor<? extends T>) visitor).visitBracket(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class MyStringContext extends FactorContext {
        public TerminalNode STRING() {
            return getToken(ConditionParser.STRING, 0);
        }

        public MyStringContext(FactorContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ConditionListener) ((ConditionListener) listener).enterMyString(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ConditionListener) ((ConditionListener) listener).exitMyString(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ConditionVisitor)
                return ((ConditionVisitor<? extends T>) visitor).visitMyString(this);
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
            if (listener instanceof ConditionListener) ((ConditionListener) listener).enterFunction(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ConditionListener) ((ConditionListener) listener).exitFunction(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ConditionVisitor)
                return ((ConditionVisitor<? extends T>) visitor).visitFunction(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FactorContext factor() throws RecognitionException {
        FactorContext _localctx = new FactorContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_factor);
        int _la;
        try {
            setState(55);
            switch (_input.LA(1)) {
                case NUMBER:
                case ADD:
                case SUB:
                    _localctx = new MinusContext(_localctx);
                    enterOuterAlt(_localctx, 1);
                {
                    setState(43);
                    _la = _input.LA(1);
                    if (_la == ADD || _la == SUB) {
                        {
                            setState(42);
                            ((MinusContext) _localctx).sign = _input.LT(1);
                            _la = _input.LA(1);
                            if (!(_la == ADD || _la == SUB)) {
                                ((MinusContext) _localctx).sign = (Token) _errHandler.recoverInline(this);
                            } else {
                                consume();
                            }
                        }
                    }

                    setState(45);
                    ((MinusContext) _localctx).num = match(NUMBER);
                }
                break;
                case T__1:
                    _localctx = new BracketContext(_localctx);
                    enterOuterAlt(_localctx, 2);
                {
                    setState(46);
                    match(T__1);
                    setState(47);
                    expr(0);
                    setState(48);
                    match(T__2);
                }
                break;
                case T__3:
                    _localctx = new VariableContext(_localctx);
                    enterOuterAlt(_localctx, 3);
                {
                    setState(50);
                    match(T__3);
                    setState(51);
                    ((VariableContext) _localctx).id = match(ID);
                    setState(52);
                    match(T__4);
                }
                break;
                case ID:
                    _localctx = new FunctionContext(_localctx);
                    enterOuterAlt(_localctx, 4);
                {
                    setState(53);
                    funCall();
                }
                break;
                case STRING:
                    _localctx = new MyStringContext(_localctx);
                    enterOuterAlt(_localctx, 5);
                {
                    setState(54);
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
            return getToken(ConditionParser.ID, 0);
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
            if (listener instanceof ConditionListener) ((ConditionListener) listener).enterFunCall(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ConditionListener) ((ConditionListener) listener).exitFunCall(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ConditionVisitor)
                return ((ConditionVisitor<? extends T>) visitor).visitFunCall(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FunCallContext funCall() throws RecognitionException {
        FunCallContext _localctx = new FunCallContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_funCall);
        try {
            setState(65);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 4, _ctx)) {
                case 1:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(57);
                    ((FunCallContext) _localctx).name = match(ID);
                    setState(58);
                    match(T__1);
                    setState(59);
                    params();
                    setState(60);
                    match(T__2);
                }
                break;
                case 2:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(62);
                    ((FunCallContext) _localctx).name = match(ID);
                    setState(63);
                    match(T__1);
                    setState(64);
                    match(T__2);
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
            if (listener instanceof ConditionListener) ((ConditionListener) listener).enterParams(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ConditionListener) ((ConditionListener) listener).exitParams(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ConditionVisitor) return ((ConditionVisitor<? extends T>) visitor).visitParams(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ParamsContext params() throws RecognitionException {
        ParamsContext _localctx = new ParamsContext(_ctx, getState());
        enterRule(_localctx, 10, RULE_params);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(67);
                expr(0);
                setState(70);
                _la = _input.LA(1);
                if (_la == T__5) {
                    {
                        setState(68);
                        match(T__5);
                        setState(69);
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

    public static class ArrExprContext extends ParserRuleContext {
        public ParamsContext params() {
            return getRuleContext(ParamsContext.class, 0);
        }

        public ArrExprContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_arrExpr;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ConditionListener) ((ConditionListener) listener).enterArrExpr(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ConditionListener) ((ConditionListener) listener).exitArrExpr(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ConditionVisitor)
                return ((ConditionVisitor<? extends T>) visitor).visitArrExpr(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ArrExprContext arrExpr() throws RecognitionException {
        ArrExprContext _localctx = new ArrExprContext(_ctx, getState());
        enterRule(_localctx, 12, RULE_arrExpr);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(72);
                match(T__6);
                setState(73);
                params();
                setState(74);
                match(T__7);
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
                return precpred(_ctx, 6);
            case 1:
                return precpred(_ctx, 5);
            case 2:
                return precpred(_ctx, 4);
            case 3:
                return precpred(_ctx, 3);
            case 4:
                return precpred(_ctx, 2);
        }
        return true;
    }

    public static final String _serializedATN =
            "\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\36O\4\2\t\2\4\3\t" +
                    "\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\3\2\3\2\3\3\3\3\3\3\3\4\3\4" +
                    "\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\7\4(" +
                    "\n\4\f\4\16\4+\13\4\3\5\5\5.\n\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3" +
                    "\5\5\5:\n\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\5\6D\n\6\3\7\3\7\3\7\5\7I" +
                    "\n\7\3\b\3\b\3\b\3\b\3\b\2\3\6\t\2\4\6\b\n\f\16\2\7\3\2\17\20\3\2\23\24" +
                    "\3\2\21\22\3\2\25\32\3\2\33\36S\2\20\3\2\2\2\4\22\3\2\2\2\6\25\3\2\2\2" +
                    "\b9\3\2\2\2\nC\3\2\2\2\fE\3\2\2\2\16J\3\2\2\2\20\21\5\4\3\2\21\3\3\2\2" +
                    "\2\22\23\7\3\2\2\23\24\5\6\4\2\24\5\3\2\2\2\25\26\b\4\1\2\26\27\5\b\5" +
                    "\2\27)\3\2\2\2\30\31\f\b\2\2\31\32\t\2\2\2\32(\5\6\4\t\33\34\f\7\2\2\34" +
                    "\35\t\3\2\2\35(\5\6\4\b\36\37\f\6\2\2\37 \t\4\2\2 (\5\6\4\7!\"\f\5\2\2" +
                    "\"#\t\5\2\2#(\5\6\4\6$%\f\4\2\2%&\t\6\2\2&(\5\16\b\2\'\30\3\2\2\2\'\33" +
                    "\3\2\2\2\'\36\3\2\2\2\'!\3\2\2\2\'$\3\2\2\2(+\3\2\2\2)\'\3\2\2\2)*\3\2" +
                    "\2\2*\7\3\2\2\2+)\3\2\2\2,.\t\4\2\2-,\3\2\2\2-.\3\2\2\2./\3\2\2\2/:\7" +
                    "\r\2\2\60\61\7\4\2\2\61\62\5\6\4\2\62\63\7\5\2\2\63:\3\2\2\2\64\65\7\6" +
                    "\2\2\65\66\7\f\2\2\66:\7\7\2\2\67:\5\n\6\28:\7\16\2\29-\3\2\2\29\60\3" +
                    "\2\2\29\64\3\2\2\29\67\3\2\2\298\3\2\2\2:\t\3\2\2\2;<\7\f\2\2<=\7\4\2" +
                    "\2=>\5\f\7\2>?\7\5\2\2?D\3\2\2\2@A\7\f\2\2AB\7\4\2\2BD\7\5\2\2C;\3\2\2" +
                    "\2C@\3\2\2\2D\13\3\2\2\2EH\5\6\4\2FG\7\b\2\2GI\5\f\7\2HF\3\2\2\2HI\3\2" +
                    "\2\2I\r\3\2\2\2JK\7\t\2\2KL\5\f\7\2LM\7\n\2\2M\17\3\2\2\2\b\')-9CH";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}
