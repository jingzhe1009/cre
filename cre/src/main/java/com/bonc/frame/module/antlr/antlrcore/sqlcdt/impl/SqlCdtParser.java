package com.bonc.frame.module.antlr.antlrcore.sqlcdt.impl;
// Generated from SqlCdt.g4 by ANTLR 4.5.3


import com.bonc.frame.module.antlr.antlrcore.sqlcdt.SqlCdtListener;
import com.bonc.frame.module.antlr.antlrcore.sqlcdt.SqlCdtVisitor;
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
public class SqlCdtParser extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.5.3", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            DOT = 1, OPEN_PAR = 2, CLOSE_PAR = 3, COMMA = 4, ASSIGN = 5, STAR = 6, PLUS = 7, MINUS = 8,
            TILDE = 9, PIPE2 = 10, DIV = 11, MOD = 12, LT = 13, LT_EQ = 14, GT = 15, GT_EQ = 16, NOT_EQ1 = 17,
            NOT_EQ2 = 18, K_ADD = 19, K_AND = 20, K_BETWEEN = 21, K_IN = 22, K_IS = 23, K_ISNULL = 24,
            K_LIKE = 25, K_NOT = 26, K_NOTNULL = 27, K_NULL = 28, K_OR = 29, K_DISTINCT = 30,
            NUMBER = 31, ID = 32, ID1 = 33, ID2 = 34, STRING = 35, WS = 36;
    public static final int
            RULE_parse = 0, RULE_expr = 1, RULE_function_name = 2;
    public static final String[] ruleNames = {
            "parse", "expr", "function_name"
    };

    private static final String[] _LITERAL_NAMES = {
            null, "'.'", "'('", "')'", "','", "'='", "'*'", "'+'", "'-'", "'~'", "'||'",
            "'/'", "'%'", "'<'", "'<='", "'>'", "'>='", "'!='", "'<>'"
    };
    private static final String[] _SYMBOLIC_NAMES = {
            null, "DOT", "OPEN_PAR", "CLOSE_PAR", "COMMA", "ASSIGN", "STAR", "PLUS",
            "MINUS", "TILDE", "PIPE2", "DIV", "MOD", "LT", "LT_EQ", "GT", "GT_EQ",
            "NOT_EQ1", "NOT_EQ2", "K_ADD", "K_AND", "K_BETWEEN", "K_IN", "K_IS", "K_ISNULL",
            "K_LIKE", "K_NOT", "K_NOTNULL", "K_NULL", "K_OR", "K_DISTINCT", "NUMBER",
            "ID", "ID1", "ID2", "STRING", "WS"
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
        return "SqlCdt.g4";
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

    public SqlCdtParser(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    public static class ParseContext extends ParserRuleContext {
        public TerminalNode EOF() {
            return getToken(SqlCdtParser.EOF, 0);
        }

        public List<ExprContext> expr() {
            return getRuleContexts(ExprContext.class);
        }

        public ExprContext expr(int i) {
            return getRuleContext(ExprContext.class, i);
        }

        public ParseContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_parse;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SqlCdtListener) ((SqlCdtListener) listener).enterParse(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SqlCdtListener) ((SqlCdtListener) listener).exitParse(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SqlCdtVisitor) return ((SqlCdtVisitor<? extends T>) visitor).visitParse(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ParseContext parse() throws RecognitionException {
        ParseContext _localctx = new ParseContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_parse);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(9);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << OPEN_PAR) | (1L << NUMBER) | (1L << ID) | (1L << ID2) | (1L << STRING))) != 0)) {
                    {
                        {
                            setState(6);
                            expr(0);
                        }
                    }
                    setState(11);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
                setState(12);
                match(EOF);
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

        public Function_nameContext function_name() {
            return getRuleContext(Function_nameContext.class, 0);
        }

        public List<ExprContext> expr() {
            return getRuleContexts(ExprContext.class);
        }

        public ExprContext expr(int i) {
            return getRuleContext(ExprContext.class, i);
        }

        public TerminalNode K_DISTINCT() {
            return getToken(SqlCdtParser.K_DISTINCT, 0);
        }

        public TerminalNode OPEN_PAR() {
            return getToken(SqlCdtParser.OPEN_PAR, 0);
        }

        public TerminalNode CLOSE_PAR() {
            return getToken(SqlCdtParser.CLOSE_PAR, 0);
        }

        public TerminalNode STRING() {
            return getToken(SqlCdtParser.STRING, 0);
        }

        public TerminalNode ID2() {
            return getToken(SqlCdtParser.ID2, 0);
        }

        public TerminalNode NUMBER() {
            return getToken(SqlCdtParser.NUMBER, 0);
        }

        public TerminalNode PLUS() {
            return getToken(SqlCdtParser.PLUS, 0);
        }

        public TerminalNode MINUS() {
            return getToken(SqlCdtParser.MINUS, 0);
        }

        public TerminalNode STAR() {
            return getToken(SqlCdtParser.STAR, 0);
        }

        public TerminalNode DIV() {
            return getToken(SqlCdtParser.DIV, 0);
        }

        public TerminalNode MOD() {
            return getToken(SqlCdtParser.MOD, 0);
        }

        public TerminalNode LT() {
            return getToken(SqlCdtParser.LT, 0);
        }

        public TerminalNode LT_EQ() {
            return getToken(SqlCdtParser.LT_EQ, 0);
        }

        public TerminalNode GT() {
            return getToken(SqlCdtParser.GT, 0);
        }

        public TerminalNode GT_EQ() {
            return getToken(SqlCdtParser.GT_EQ, 0);
        }

        public TerminalNode ASSIGN() {
            return getToken(SqlCdtParser.ASSIGN, 0);
        }

        public TerminalNode NOT_EQ1() {
            return getToken(SqlCdtParser.NOT_EQ1, 0);
        }

        public TerminalNode NOT_EQ2() {
            return getToken(SqlCdtParser.NOT_EQ2, 0);
        }

        public TerminalNode PIPE2() {
            return getToken(SqlCdtParser.PIPE2, 0);
        }

        public TerminalNode K_IS() {
            return getToken(SqlCdtParser.K_IS, 0);
        }

        public TerminalNode K_NOT() {
            return getToken(SqlCdtParser.K_NOT, 0);
        }

        public TerminalNode K_IN() {
            return getToken(SqlCdtParser.K_IN, 0);
        }

        public TerminalNode K_LIKE() {
            return getToken(SqlCdtParser.K_LIKE, 0);
        }

        public TerminalNode K_AND() {
            return getToken(SqlCdtParser.K_AND, 0);
        }

        public TerminalNode K_OR() {
            return getToken(SqlCdtParser.K_OR, 0);
        }

        public TerminalNode K_BETWEEN() {
            return getToken(SqlCdtParser.K_BETWEEN, 0);
        }

        public TerminalNode K_ISNULL() {
            return getToken(SqlCdtParser.K_ISNULL, 0);
        }

        public TerminalNode K_NOTNULL() {
            return getToken(SqlCdtParser.K_NOTNULL, 0);
        }

        public TerminalNode K_NULL() {
            return getToken(SqlCdtParser.K_NULL, 0);
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
            if (listener instanceof SqlCdtListener) ((SqlCdtListener) listener).enterExpr(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SqlCdtListener) ((SqlCdtListener) listener).exitExpr(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SqlCdtVisitor) return ((SqlCdtVisitor<? extends T>) visitor).visitExpr(this);
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
        int _startState = 2;
        enterRecursionRule(_localctx, 2, RULE_expr, _p);
        int _la;
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                setState(47);
                switch (_input.LA(1)) {
                    case ID: {
                        setState(15);
                        function_name();
                        setState(16);
                        match(OPEN_PAR);
                        setState(29);
                        switch (_input.LA(1)) {
                            case OPEN_PAR:
                            case K_DISTINCT:
                            case NUMBER:
                            case ID:
                            case ID2:
                            case STRING: {
                                setState(18);
                                _la = _input.LA(1);
                                if (_la == K_DISTINCT) {
                                    {
                                        setState(17);
                                        match(K_DISTINCT);
                                    }
                                }

                                setState(20);
                                expr(0);
                                setState(25);
                                _errHandler.sync(this);
                                _la = _input.LA(1);
                                while (_la == COMMA) {
                                    {
                                        {
                                            setState(21);
                                            match(COMMA);
                                            setState(22);
                                            expr(0);
                                        }
                                    }
                                    setState(27);
                                    _errHandler.sync(this);
                                    _la = _input.LA(1);
                                }
                            }
                            break;
                            case STAR: {
                                setState(28);
                                match(STAR);
                            }
                            break;
                            case CLOSE_PAR:
                                break;
                            default:
                                throw new NoViableAltException(this);
                        }
                        setState(31);
                        match(CLOSE_PAR);
                    }
                    break;
                    case OPEN_PAR: {
                        setState(33);
                        match(OPEN_PAR);
                        setState(34);
                        expr(0);
                        setState(39);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        while (_la == COMMA) {
                            {
                                {
                                    setState(35);
                                    match(COMMA);
                                    setState(36);
                                    expr(0);
                                }
                            }
                            setState(41);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                        }
                        setState(42);
                        match(CLOSE_PAR);
                    }
                    break;
                    case STRING: {
                        setState(44);
                        match(STRING);
                    }
                    break;
                    case ID2: {
                        setState(45);
                        match(ID2);
                    }
                    break;
                    case NUMBER: {
                        setState(46);
                        match(NUMBER);
                    }
                    break;
                    default:
                        throw new NoViableAltException(this);
                }
                _ctx.stop = _input.LT(-1);
                setState(101);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 12, _ctx);
                while (_alt != 2 && _alt != ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        if (_parseListeners != null) triggerExitRuleEvent();
                        _prevctx = _localctx;
                        {
                            setState(99);
                            _errHandler.sync(this);
                            switch (getInterpreter().adaptivePredict(_input, 11, _ctx)) {
                                case 1: {
                                    _localctx = new ExprContext(_parentctx, _parentState);
                                    pushNewRecursionContext(_localctx, _startState, RULE_expr);
                                    setState(49);
                                    if (!(precpred(_ctx, 13)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 13)");
                                    setState(50);
                                    _la = _input.LA(1);
                                    if (!((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STAR) | (1L << PLUS) | (1L << MINUS) | (1L << DIV) | (1L << MOD))) != 0))) {
                                        _errHandler.recoverInline(this);
                                    } else {
                                        consume();
                                    }
                                    setState(51);
                                    expr(14);
                                }
                                break;
                                case 2: {
                                    _localctx = new ExprContext(_parentctx, _parentState);
                                    pushNewRecursionContext(_localctx, _startState, RULE_expr);
                                    setState(52);
                                    if (!(precpred(_ctx, 12)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 12)");
                                    setState(53);
                                    _la = _input.LA(1);
                                    if (!((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LT) | (1L << LT_EQ) | (1L << GT) | (1L << GT_EQ))) != 0))) {
                                        _errHandler.recoverInline(this);
                                    } else {
                                        consume();
                                    }
                                    setState(54);
                                    expr(13);
                                }
                                break;
                                case 3: {
                                    _localctx = new ExprContext(_parentctx, _parentState);
                                    pushNewRecursionContext(_localctx, _startState, RULE_expr);
                                    setState(55);
                                    if (!(precpred(_ctx, 11)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 11)");
                                    setState(65);
                                    _errHandler.sync(this);
                                    switch (getInterpreter().adaptivePredict(_input, 6, _ctx)) {
                                        case 1: {
                                            setState(56);
                                            match(ASSIGN);
                                        }
                                        break;
                                        case 2: {
                                            setState(57);
                                            match(NOT_EQ1);
                                        }
                                        break;
                                        case 3: {
                                            setState(58);
                                            match(NOT_EQ2);
                                        }
                                        break;
                                        case 4: {
                                            setState(59);
                                            match(PIPE2);
                                        }
                                        break;
                                        case 5: {
                                            setState(60);
                                            match(K_IS);
                                        }
                                        break;
                                        case 6: {
                                            setState(61);
                                            match(K_IS);
                                            setState(62);
                                            match(K_NOT);
                                        }
                                        break;
                                        case 7: {
                                            setState(63);
                                            match(K_IN);
                                        }
                                        break;
                                        case 8: {
                                            setState(64);
                                            match(K_LIKE);
                                        }
                                        break;
                                    }
                                    setState(67);
                                    expr(12);
                                }
                                break;
                                case 4: {
                                    _localctx = new ExprContext(_parentctx, _parentState);
                                    pushNewRecursionContext(_localctx, _startState, RULE_expr);
                                    setState(68);
                                    if (!(precpred(_ctx, 10)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 10)");
                                    setState(69);
                                    ((ExprContext) _localctx).op = _input.LT(1);
                                    _la = _input.LA(1);
                                    if (!(_la == K_AND || _la == K_OR)) {
                                        ((ExprContext) _localctx).op = (Token) _errHandler.recoverInline(this);
                                    } else {
                                        consume();
                                    }
                                    setState(70);
                                    expr(11);
                                }
                                break;
                                case 5: {
                                    _localctx = new ExprContext(_parentctx, _parentState);
                                    pushNewRecursionContext(_localctx, _startState, RULE_expr);
                                    setState(71);
                                    if (!(precpred(_ctx, 6)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 6)");
                                    setState(72);
                                    match(K_IS);
                                    setState(74);
                                    _la = _input.LA(1);
                                    if (_la == K_NOT) {
                                        {
                                            setState(73);
                                            match(K_NOT);
                                        }
                                    }

                                    setState(76);
                                    expr(7);
                                }
                                break;
                                case 6: {
                                    _localctx = new ExprContext(_parentctx, _parentState);
                                    pushNewRecursionContext(_localctx, _startState, RULE_expr);
                                    setState(77);
                                    if (!(precpred(_ctx, 4)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 4)");
                                    setState(79);
                                    _la = _input.LA(1);
                                    if (_la == K_NOT) {
                                        {
                                            setState(78);
                                            match(K_NOT);
                                        }
                                    }

                                    setState(81);
                                    match(K_BETWEEN);
                                    setState(82);
                                    expr(0);
                                    setState(83);
                                    match(K_AND);
                                    setState(84);
                                    expr(5);
                                }
                                break;
                                case 7: {
                                    _localctx = new ExprContext(_parentctx, _parentState);
                                    pushNewRecursionContext(_localctx, _startState, RULE_expr);
                                    setState(86);
                                    if (!(precpred(_ctx, 7)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 7)");
                                    setState(91);
                                    switch (_input.LA(1)) {
                                        case K_ISNULL: {
                                            setState(87);
                                            match(K_ISNULL);
                                        }
                                        break;
                                        case K_NOTNULL: {
                                            setState(88);
                                            match(K_NOTNULL);
                                        }
                                        break;
                                        case K_NOT: {
                                            setState(89);
                                            match(K_NOT);
                                            setState(90);
                                            match(K_NULL);
                                        }
                                        break;
                                        default:
                                            throw new NoViableAltException(this);
                                    }
                                }
                                break;
                                case 8: {
                                    _localctx = new ExprContext(_parentctx, _parentState);
                                    pushNewRecursionContext(_localctx, _startState, RULE_expr);
                                    setState(93);
                                    if (!(precpred(_ctx, 5)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 5)");
                                    setState(94);
                                    match(K_IS);
                                    setState(96);
                                    _la = _input.LA(1);
                                    if (_la == K_NOT) {
                                        {
                                            setState(95);
                                            match(K_NOT);
                                        }
                                    }

                                    setState(98);
                                    match(K_NULL);
                                }
                                break;
                            }
                        }
                    }
                    setState(103);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 12, _ctx);
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

    public static class Function_nameContext extends ParserRuleContext {
        public TerminalNode ID() {
            return getToken(SqlCdtParser.ID, 0);
        }

        public Function_nameContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_function_name;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SqlCdtListener) ((SqlCdtListener) listener).enterFunction_name(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SqlCdtListener) ((SqlCdtListener) listener).exitFunction_name(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SqlCdtVisitor)
                return ((SqlCdtVisitor<? extends T>) visitor).visitFunction_name(this);
            else return visitor.visitChildren(this);
        }
    }

    public final Function_nameContext function_name() throws RecognitionException {
        Function_nameContext _localctx = new Function_nameContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_function_name);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(104);
                match(ID);
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
            case 1:
                return expr_sempred((ExprContext) _localctx, predIndex);
        }
        return true;
    }

    private boolean expr_sempred(ExprContext _localctx, int predIndex) {
        switch (predIndex) {
            case 0:
                return precpred(_ctx, 13);
            case 1:
                return precpred(_ctx, 12);
            case 2:
                return precpred(_ctx, 11);
            case 3:
                return precpred(_ctx, 10);
            case 4:
                return precpred(_ctx, 6);
            case 5:
                return precpred(_ctx, 4);
            case 6:
                return precpred(_ctx, 7);
            case 7:
                return precpred(_ctx, 5);
        }
        return true;
    }

    public static final String _serializedATN =
            "\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3&m\4\2\t\2\4\3\t\3" +
                    "\4\4\t\4\3\2\7\2\n\n\2\f\2\16\2\r\13\2\3\2\3\2\3\3\3\3\3\3\3\3\5\3\25" +
                    "\n\3\3\3\3\3\3\3\7\3\32\n\3\f\3\16\3\35\13\3\3\3\5\3 \n\3\3\3\3\3\3\3" +
                    "\3\3\3\3\3\3\7\3(\n\3\f\3\16\3+\13\3\3\3\3\3\3\3\3\3\3\3\5\3\62\n\3\3" +
                    "\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3D\n" +
                    "\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3M\n\3\3\3\3\3\3\3\5\3R\n\3\3\3\3\3\3" +
                    "\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3^\n\3\3\3\3\3\3\3\5\3c\n\3\3\3\7\3f" +
                    "\n\3\f\3\16\3i\13\3\3\4\3\4\3\4\2\3\4\5\2\4\6\2\5\4\2\b\n\r\16\3\2\17" +
                    "\22\4\2\26\26\37\37\u0087\2\13\3\2\2\2\4\61\3\2\2\2\6j\3\2\2\2\b\n\5\4" +
                    "\3\2\t\b\3\2\2\2\n\r\3\2\2\2\13\t\3\2\2\2\13\f\3\2\2\2\f\16\3\2\2\2\r" +
                    "\13\3\2\2\2\16\17\7\2\2\3\17\3\3\2\2\2\20\21\b\3\1\2\21\22\5\6\4\2\22" +
                    "\37\7\4\2\2\23\25\7 \2\2\24\23\3\2\2\2\24\25\3\2\2\2\25\26\3\2\2\2\26" +
                    "\33\5\4\3\2\27\30\7\6\2\2\30\32\5\4\3\2\31\27\3\2\2\2\32\35\3\2\2\2\33" +
                    "\31\3\2\2\2\33\34\3\2\2\2\34 \3\2\2\2\35\33\3\2\2\2\36 \7\b\2\2\37\24" +
                    "\3\2\2\2\37\36\3\2\2\2\37 \3\2\2\2 !\3\2\2\2!\"\7\5\2\2\"\62\3\2\2\2#" +
                    "$\7\4\2\2$)\5\4\3\2%&\7\6\2\2&(\5\4\3\2\'%\3\2\2\2(+\3\2\2\2)\'\3\2\2" +
                    "\2)*\3\2\2\2*,\3\2\2\2+)\3\2\2\2,-\7\5\2\2-\62\3\2\2\2.\62\7%\2\2/\62" +
                    "\7$\2\2\60\62\7!\2\2\61\20\3\2\2\2\61#\3\2\2\2\61.\3\2\2\2\61/\3\2\2\2" +
                    "\61\60\3\2\2\2\62g\3\2\2\2\63\64\f\17\2\2\64\65\t\2\2\2\65f\5\4\3\20\66" +
                    "\67\f\16\2\2\678\t\3\2\28f\5\4\3\179C\f\r\2\2:D\7\7\2\2;D\7\23\2\2<D\7" +
                    "\24\2\2=D\7\f\2\2>D\7\31\2\2?@\7\31\2\2@D\7\34\2\2AD\7\30\2\2BD\7\33\2" +
                    "\2C:\3\2\2\2C;\3\2\2\2C<\3\2\2\2C=\3\2\2\2C>\3\2\2\2C?\3\2\2\2CA\3\2\2" +
                    "\2CB\3\2\2\2DE\3\2\2\2Ef\5\4\3\16FG\f\f\2\2GH\t\4\2\2Hf\5\4\3\rIJ\f\b" +
                    "\2\2JL\7\31\2\2KM\7\34\2\2LK\3\2\2\2LM\3\2\2\2MN\3\2\2\2Nf\5\4\3\tOQ\f" +
                    "\6\2\2PR\7\34\2\2QP\3\2\2\2QR\3\2\2\2RS\3\2\2\2ST\7\27\2\2TU\5\4\3\2U" +
                    "V\7\26\2\2VW\5\4\3\7Wf\3\2\2\2X]\f\t\2\2Y^\7\32\2\2Z^\7\35\2\2[\\\7\34" +
                    "\2\2\\^\7\36\2\2]Y\3\2\2\2]Z\3\2\2\2][\3\2\2\2^f\3\2\2\2_`\f\7\2\2`b\7" +
                    "\31\2\2ac\7\34\2\2ba\3\2\2\2bc\3\2\2\2cd\3\2\2\2df\7\36\2\2e\63\3\2\2" +
                    "\2e\66\3\2\2\2e9\3\2\2\2eF\3\2\2\2eI\3\2\2\2eO\3\2\2\2eX\3\2\2\2e_\3\2" +
                    "\2\2fi\3\2\2\2ge\3\2\2\2gh\3\2\2\2h\5\3\2\2\2ig\3\2\2\2jk\7\"\2\2k\7\3" +
                    "\2\2\2\17\13\24\33\37)\61CLQ]beg";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}
