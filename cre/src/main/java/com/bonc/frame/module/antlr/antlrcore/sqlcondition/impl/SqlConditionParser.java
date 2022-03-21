package com.bonc.frame.module.antlr.antlrcore.sqlcondition.impl;
// Generated from SqlCondition.g4 by ANTLR 4.5.3


import com.bonc.frame.module.antlr.antlrcore.sqlcondition.SqlConditionListener;
import com.bonc.frame.module.antlr.antlrcore.sqlcondition.SqlConditionVisitor;
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
public class SqlConditionParser extends Parser {
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
            IDENTIFIER = 31, NUMERIC_LITERAL = 32, STRING_LITERAL = 33, WS = 34;
    public static final int
            RULE_parse = 0, RULE_expr = 1, RULE_keyword = 2, RULE_name = 3, RULE_function_name = 4,
            RULE_database_name = 5, RULE_table_name = 6, RULE_column_name = 7, RULE_any_name = 8;
    public static final String[] ruleNames = {
            "parse", "expr", "keyword", "name", "function_name", "database_name",
            "table_name", "column_name", "any_name"
    };

    private static final String[] _LITERAL_NAMES = {
            null, "'.'", "'('", "')'", "','", "'='", "'*'", "'+'", "'-'", "'~'", "'||'",
            "'/'", "'%'", "'<'", "'<='", "'>'", "'>='", "'!='", "'<>'"
    };
    private static final String[] _SYMBOLIC_NAMES = {
            null, "DOT", "OPEN_PAR", "CLOSE_PAR", "COMMA", "ASSIGN", "STAR", "PLUS",
            "MINUS", "TILDE", "PIPE2", "DIV", "MOD", "LT", "LT_EQ", "GT", "GT_EQ",
            "NOT_EQ1", "NOT_EQ2", "K_ADD", "K_AND", "K_BETWEEN", "K_IN", "K_IS", "K_ISNULL",
            "K_LIKE", "K_NOT", "K_NOTNULL", "K_NULL", "K_OR", "K_DISTINCT", "IDENTIFIER",
            "NUMERIC_LITERAL", "STRING_LITERAL", "WS"
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
        return "SqlCondition.g4";
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

    public SqlConditionParser(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    public static class ParseContext extends ParserRuleContext {
        public TerminalNode EOF() {
            return getToken(SqlConditionParser.EOF, 0);
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
            if (listener instanceof SqlConditionListener) ((SqlConditionListener) listener).enterParse(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SqlConditionListener) ((SqlConditionListener) listener).exitParse(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SqlConditionVisitor)
                return ((SqlConditionVisitor<? extends T>) visitor).visitParse(this);
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
                setState(21);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << OPEN_PAR) | (1L << K_ADD) | (1L << K_AND) | (1L << K_BETWEEN) | (1L << K_IN) | (1L << K_IS) | (1L << K_ISNULL) | (1L << K_LIKE) | (1L << K_NOT) | (1L << K_NOTNULL) | (1L << K_NULL) | (1L << K_OR) | (1L << K_DISTINCT) | (1L << IDENTIFIER) | (1L << NUMERIC_LITERAL) | (1L << STRING_LITERAL))) != 0)) {
                    {
                        {
                            setState(18);
                            expr(0);
                        }
                    }
                    setState(23);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
                setState(24);
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

        public Column_nameContext column_name() {
            return getRuleContext(Column_nameContext.class, 0);
        }

        public Table_nameContext table_name() {
            return getRuleContext(Table_nameContext.class, 0);
        }

        public Database_nameContext database_name() {
            return getRuleContext(Database_nameContext.class, 0);
        }

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
            return getToken(SqlConditionParser.K_DISTINCT, 0);
        }

        public TerminalNode STAR() {
            return getToken(SqlConditionParser.STAR, 0);
        }

        public TerminalNode DIV() {
            return getToken(SqlConditionParser.DIV, 0);
        }

        public TerminalNode MOD() {
            return getToken(SqlConditionParser.MOD, 0);
        }

        public TerminalNode PLUS() {
            return getToken(SqlConditionParser.PLUS, 0);
        }

        public TerminalNode MINUS() {
            return getToken(SqlConditionParser.MINUS, 0);
        }

        public TerminalNode ASSIGN() {
            return getToken(SqlConditionParser.ASSIGN, 0);
        }

        public TerminalNode LT() {
            return getToken(SqlConditionParser.LT, 0);
        }

        public TerminalNode LT_EQ() {
            return getToken(SqlConditionParser.LT_EQ, 0);
        }

        public TerminalNode GT() {
            return getToken(SqlConditionParser.GT, 0);
        }

        public TerminalNode GT_EQ() {
            return getToken(SqlConditionParser.GT_EQ, 0);
        }

        public TerminalNode NOT_EQ1() {
            return getToken(SqlConditionParser.NOT_EQ1, 0);
        }

        public TerminalNode NOT_EQ2() {
            return getToken(SqlConditionParser.NOT_EQ2, 0);
        }

        public TerminalNode K_IS() {
            return getToken(SqlConditionParser.K_IS, 0);
        }

        public TerminalNode K_NOT() {
            return getToken(SqlConditionParser.K_NOT, 0);
        }

        public TerminalNode K_IN() {
            return getToken(SqlConditionParser.K_IN, 0);
        }

        public TerminalNode K_LIKE() {
            return getToken(SqlConditionParser.K_LIKE, 0);
        }

        public TerminalNode K_AND() {
            return getToken(SqlConditionParser.K_AND, 0);
        }

        public TerminalNode K_OR() {
            return getToken(SqlConditionParser.K_OR, 0);
        }

        public TerminalNode K_BETWEEN() {
            return getToken(SqlConditionParser.K_BETWEEN, 0);
        }

        public TerminalNode K_ISNULL() {
            return getToken(SqlConditionParser.K_ISNULL, 0);
        }

        public TerminalNode K_NOTNULL() {
            return getToken(SqlConditionParser.K_NOTNULL, 0);
        }

        public TerminalNode K_NULL() {
            return getToken(SqlConditionParser.K_NULL, 0);
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
            if (listener instanceof SqlConditionListener) ((SqlConditionListener) listener).enterExpr(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SqlConditionListener) ((SqlConditionListener) listener).exitExpr(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SqlConditionVisitor)
                return ((SqlConditionVisitor<? extends T>) visitor).visitExpr(this);
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
                setState(60);
                _errHandler.sync(this);
                switch (getInterpreter().adaptivePredict(_input, 6, _ctx)) {
                    case 1: {
                        setState(35);
                        _errHandler.sync(this);
                        switch (getInterpreter().adaptivePredict(_input, 2, _ctx)) {
                            case 1: {
                                setState(30);
                                _errHandler.sync(this);
                                switch (getInterpreter().adaptivePredict(_input, 1, _ctx)) {
                                    case 1: {
                                        setState(27);
                                        database_name();
                                        setState(28);
                                        match(DOT);
                                    }
                                    break;
                                }
                                setState(32);
                                table_name();
                                setState(33);
                                match(DOT);
                            }
                            break;
                        }
                        setState(37);
                        column_name();
                    }
                    break;
                    case 2: {
                        setState(38);
                        function_name();
                        setState(39);
                        match(OPEN_PAR);
                        setState(52);
                        switch (_input.LA(1)) {
                            case OPEN_PAR:
                            case K_ADD:
                            case K_AND:
                            case K_BETWEEN:
                            case K_IN:
                            case K_IS:
                            case K_ISNULL:
                            case K_LIKE:
                            case K_NOT:
                            case K_NOTNULL:
                            case K_NULL:
                            case K_OR:
                            case K_DISTINCT:
                            case IDENTIFIER:
                            case NUMERIC_LITERAL:
                            case STRING_LITERAL: {
                                setState(41);
                                _errHandler.sync(this);
                                switch (getInterpreter().adaptivePredict(_input, 3, _ctx)) {
                                    case 1: {
                                        setState(40);
                                        match(K_DISTINCT);
                                    }
                                    break;
                                }
                                setState(43);
                                expr(0);
                                setState(48);
                                _errHandler.sync(this);
                                _la = _input.LA(1);
                                while (_la == COMMA) {
                                    {
                                        {
                                            setState(44);
                                            match(COMMA);
                                            setState(45);
                                            expr(0);
                                        }
                                    }
                                    setState(50);
                                    _errHandler.sync(this);
                                    _la = _input.LA(1);
                                }
                            }
                            break;
                            case STAR: {
                                setState(51);
                                match(STAR);
                            }
                            break;
                            case CLOSE_PAR:
                                break;
                            default:
                                throw new NoViableAltException(this);
                        }
                        setState(54);
                        match(CLOSE_PAR);
                    }
                    break;
                    case 3: {
                        setState(56);
                        match(OPEN_PAR);
                        setState(57);
                        expr(0);
                        setState(58);
                        match(CLOSE_PAR);
                    }
                    break;
                }
                _ctx.stop = _input.LT(-1);
                setState(110);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 12, _ctx);
                while (_alt != 2 && _alt != ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        if (_parseListeners != null) triggerExitRuleEvent();
                        _prevctx = _localctx;
                        {
                            setState(108);
                            _errHandler.sync(this);
                            switch (getInterpreter().adaptivePredict(_input, 11, _ctx)) {
                                case 1: {
                                    _localctx = new ExprContext(_parentctx, _parentState);
                                    pushNewRecursionContext(_localctx, _startState, RULE_expr);
                                    setState(62);
                                    if (!(precpred(_ctx, 11)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 11)");
                                    setState(63);
                                    match(PIPE2);
                                    setState(64);
                                    expr(12);
                                }
                                break;
                                case 2: {
                                    _localctx = new ExprContext(_parentctx, _parentState);
                                    pushNewRecursionContext(_localctx, _startState, RULE_expr);
                                    setState(65);
                                    if (!(precpred(_ctx, 10)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 10)");
                                    setState(66);
                                    ((ExprContext) _localctx).op = _input.LT(1);
                                    _la = _input.LA(1);
                                    if (!((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STAR) | (1L << PLUS) | (1L << MINUS) | (1L << DIV) | (1L << MOD))) != 0))) {
                                        ((ExprContext) _localctx).op = (Token) _errHandler.recoverInline(this);
                                    } else {
                                        consume();
                                    }
                                    setState(67);
                                    expr(11);
                                }
                                break;
                                case 3: {
                                    _localctx = new ExprContext(_parentctx, _parentState);
                                    pushNewRecursionContext(_localctx, _startState, RULE_expr);
                                    setState(68);
                                    if (!(precpred(_ctx, 9)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 9)");
                                    setState(69);
                                    ((ExprContext) _localctx).op = _input.LT(1);
                                    _la = _input.LA(1);
                                    if (!((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ASSIGN) | (1L << LT) | (1L << LT_EQ) | (1L << GT) | (1L << GT_EQ) | (1L << NOT_EQ1) | (1L << NOT_EQ2))) != 0))) {
                                        ((ExprContext) _localctx).op = (Token) _errHandler.recoverInline(this);
                                    } else {
                                        consume();
                                    }
                                    setState(70);
                                    expr(10);
                                }
                                break;
                                case 4: {
                                    _localctx = new ExprContext(_parentctx, _parentState);
                                    pushNewRecursionContext(_localctx, _startState, RULE_expr);
                                    setState(71);
                                    if (!(precpred(_ctx, 8)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 8)");
                                    setState(77);
                                    _errHandler.sync(this);
                                    switch (getInterpreter().adaptivePredict(_input, 7, _ctx)) {
                                        case 1: {
                                            setState(72);
                                            match(K_IS);
                                        }
                                        break;
                                        case 2: {
                                            setState(73);
                                            match(K_IS);
                                            setState(74);
                                            match(K_NOT);
                                        }
                                        break;
                                        case 3: {
                                            setState(75);
                                            match(K_IN);
                                        }
                                        break;
                                        case 4: {
                                            setState(76);
                                            match(K_LIKE);
                                        }
                                        break;
                                    }
                                    setState(79);
                                    expr(9);
                                }
                                break;
                                case 5: {
                                    _localctx = new ExprContext(_parentctx, _parentState);
                                    pushNewRecursionContext(_localctx, _startState, RULE_expr);
                                    setState(80);
                                    if (!(precpred(_ctx, 7)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 7)");
                                    setState(81);
                                    match(K_AND);
                                    setState(82);
                                    expr(8);
                                }
                                break;
                                case 6: {
                                    _localctx = new ExprContext(_parentctx, _parentState);
                                    pushNewRecursionContext(_localctx, _startState, RULE_expr);
                                    setState(83);
                                    if (!(precpred(_ctx, 6)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 6)");
                                    setState(84);
                                    match(K_OR);
                                    setState(85);
                                    expr(7);
                                }
                                break;
                                case 7: {
                                    _localctx = new ExprContext(_parentctx, _parentState);
                                    pushNewRecursionContext(_localctx, _startState, RULE_expr);
                                    setState(86);
                                    if (!(precpred(_ctx, 2)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 2)");
                                    setState(87);
                                    match(K_IS);
                                    setState(89);
                                    _errHandler.sync(this);
                                    switch (getInterpreter().adaptivePredict(_input, 8, _ctx)) {
                                        case 1: {
                                            setState(88);
                                            match(K_NOT);
                                        }
                                        break;
                                    }
                                    setState(91);
                                    expr(3);
                                }
                                break;
                                case 8: {
                                    _localctx = new ExprContext(_parentctx, _parentState);
                                    pushNewRecursionContext(_localctx, _startState, RULE_expr);
                                    setState(92);
                                    if (!(precpred(_ctx, 1)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 1)");
                                    setState(94);
                                    _la = _input.LA(1);
                                    if (_la == K_NOT) {
                                        {
                                            setState(93);
                                            match(K_NOT);
                                        }
                                    }

                                    setState(96);
                                    match(K_BETWEEN);
                                    setState(97);
                                    expr(0);
                                    setState(98);
                                    match(K_AND);
                                    setState(99);
                                    expr(2);
                                }
                                break;
                                case 9: {
                                    _localctx = new ExprContext(_parentctx, _parentState);
                                    pushNewRecursionContext(_localctx, _startState, RULE_expr);
                                    setState(101);
                                    if (!(precpred(_ctx, 3)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 3)");
                                    setState(106);
                                    switch (_input.LA(1)) {
                                        case K_ISNULL: {
                                            setState(102);
                                            match(K_ISNULL);
                                        }
                                        break;
                                        case K_NOTNULL: {
                                            setState(103);
                                            match(K_NOTNULL);
                                        }
                                        break;
                                        case K_NOT: {
                                            setState(104);
                                            match(K_NOT);
                                            setState(105);
                                            match(K_NULL);
                                        }
                                        break;
                                        default:
                                            throw new NoViableAltException(this);
                                    }
                                }
                                break;
                            }
                        }
                    }
                    setState(112);
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

    public static class KeywordContext extends ParserRuleContext {
        public TerminalNode K_ADD() {
            return getToken(SqlConditionParser.K_ADD, 0);
        }

        public TerminalNode K_AND() {
            return getToken(SqlConditionParser.K_AND, 0);
        }

        public TerminalNode K_BETWEEN() {
            return getToken(SqlConditionParser.K_BETWEEN, 0);
        }

        public TerminalNode K_IN() {
            return getToken(SqlConditionParser.K_IN, 0);
        }

        public TerminalNode K_IS() {
            return getToken(SqlConditionParser.K_IS, 0);
        }

        public TerminalNode K_ISNULL() {
            return getToken(SqlConditionParser.K_ISNULL, 0);
        }

        public TerminalNode K_LIKE() {
            return getToken(SqlConditionParser.K_LIKE, 0);
        }

        public TerminalNode K_NOT() {
            return getToken(SqlConditionParser.K_NOT, 0);
        }

        public TerminalNode K_NOTNULL() {
            return getToken(SqlConditionParser.K_NOTNULL, 0);
        }

        public TerminalNode K_NULL() {
            return getToken(SqlConditionParser.K_NULL, 0);
        }

        public TerminalNode K_OR() {
            return getToken(SqlConditionParser.K_OR, 0);
        }

        public TerminalNode K_DISTINCT() {
            return getToken(SqlConditionParser.K_DISTINCT, 0);
        }

        public KeywordContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_keyword;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SqlConditionListener) ((SqlConditionListener) listener).enterKeyword(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SqlConditionListener) ((SqlConditionListener) listener).exitKeyword(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SqlConditionVisitor)
                return ((SqlConditionVisitor<? extends T>) visitor).visitKeyword(this);
            else return visitor.visitChildren(this);
        }
    }

    public final KeywordContext keyword() throws RecognitionException {
        KeywordContext _localctx = new KeywordContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_keyword);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(113);
                _la = _input.LA(1);
                if (!((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << K_ADD) | (1L << K_AND) | (1L << K_BETWEEN) | (1L << K_IN) | (1L << K_IS) | (1L << K_ISNULL) | (1L << K_LIKE) | (1L << K_NOT) | (1L << K_NOTNULL) | (1L << K_NULL) | (1L << K_OR) | (1L << K_DISTINCT))) != 0))) {
                    _errHandler.recoverInline(this);
                } else {
                    consume();
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

    public static class NameContext extends ParserRuleContext {
        public Any_nameContext any_name() {
            return getRuleContext(Any_nameContext.class, 0);
        }

        public NameContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_name;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SqlConditionListener) ((SqlConditionListener) listener).enterName(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SqlConditionListener) ((SqlConditionListener) listener).exitName(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SqlConditionVisitor)
                return ((SqlConditionVisitor<? extends T>) visitor).visitName(this);
            else return visitor.visitChildren(this);
        }
    }

    public final NameContext name() throws RecognitionException {
        NameContext _localctx = new NameContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_name);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(115);
                any_name();
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

    public static class Function_nameContext extends ParserRuleContext {
        public Any_nameContext any_name() {
            return getRuleContext(Any_nameContext.class, 0);
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
            if (listener instanceof SqlConditionListener) ((SqlConditionListener) listener).enterFunction_name(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SqlConditionListener) ((SqlConditionListener) listener).exitFunction_name(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SqlConditionVisitor)
                return ((SqlConditionVisitor<? extends T>) visitor).visitFunction_name(this);
            else return visitor.visitChildren(this);
        }
    }

    public final Function_nameContext function_name() throws RecognitionException {
        Function_nameContext _localctx = new Function_nameContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_function_name);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(117);
                any_name();
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

    public static class Database_nameContext extends ParserRuleContext {
        public Any_nameContext any_name() {
            return getRuleContext(Any_nameContext.class, 0);
        }

        public Database_nameContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_database_name;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SqlConditionListener) ((SqlConditionListener) listener).enterDatabase_name(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SqlConditionListener) ((SqlConditionListener) listener).exitDatabase_name(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SqlConditionVisitor)
                return ((SqlConditionVisitor<? extends T>) visitor).visitDatabase_name(this);
            else return visitor.visitChildren(this);
        }
    }

    public final Database_nameContext database_name() throws RecognitionException {
        Database_nameContext _localctx = new Database_nameContext(_ctx, getState());
        enterRule(_localctx, 10, RULE_database_name);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(119);
                any_name();
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

    public static class Table_nameContext extends ParserRuleContext {
        public Any_nameContext any_name() {
            return getRuleContext(Any_nameContext.class, 0);
        }

        public Table_nameContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_table_name;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SqlConditionListener) ((SqlConditionListener) listener).enterTable_name(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SqlConditionListener) ((SqlConditionListener) listener).exitTable_name(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SqlConditionVisitor)
                return ((SqlConditionVisitor<? extends T>) visitor).visitTable_name(this);
            else return visitor.visitChildren(this);
        }
    }

    public final Table_nameContext table_name() throws RecognitionException {
        Table_nameContext _localctx = new Table_nameContext(_ctx, getState());
        enterRule(_localctx, 12, RULE_table_name);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(121);
                any_name();
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

    public static class Column_nameContext extends ParserRuleContext {
        public Any_nameContext any_name() {
            return getRuleContext(Any_nameContext.class, 0);
        }

        public Column_nameContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_column_name;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SqlConditionListener) ((SqlConditionListener) listener).enterColumn_name(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SqlConditionListener) ((SqlConditionListener) listener).exitColumn_name(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SqlConditionVisitor)
                return ((SqlConditionVisitor<? extends T>) visitor).visitColumn_name(this);
            else return visitor.visitChildren(this);
        }
    }

    public final Column_nameContext column_name() throws RecognitionException {
        Column_nameContext _localctx = new Column_nameContext(_ctx, getState());
        enterRule(_localctx, 14, RULE_column_name);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(123);
                any_name();
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

    public static class Any_nameContext extends ParserRuleContext {
        public TerminalNode IDENTIFIER() {
            return getToken(SqlConditionParser.IDENTIFIER, 0);
        }

        public TerminalNode NUMERIC_LITERAL() {
            return getToken(SqlConditionParser.NUMERIC_LITERAL, 0);
        }

        public KeywordContext keyword() {
            return getRuleContext(KeywordContext.class, 0);
        }

        public TerminalNode STRING_LITERAL() {
            return getToken(SqlConditionParser.STRING_LITERAL, 0);
        }

        public Any_nameContext any_name() {
            return getRuleContext(Any_nameContext.class, 0);
        }

        public Any_nameContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_any_name;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SqlConditionListener) ((SqlConditionListener) listener).enterAny_name(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SqlConditionListener) ((SqlConditionListener) listener).exitAny_name(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SqlConditionVisitor)
                return ((SqlConditionVisitor<? extends T>) visitor).visitAny_name(this);
            else return visitor.visitChildren(this);
        }
    }

    public final Any_nameContext any_name() throws RecognitionException {
        Any_nameContext _localctx = new Any_nameContext(_ctx, getState());
        enterRule(_localctx, 16, RULE_any_name);
        try {
            setState(133);
            switch (_input.LA(1)) {
                case IDENTIFIER:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(125);
                    match(IDENTIFIER);
                }
                break;
                case NUMERIC_LITERAL:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(126);
                    match(NUMERIC_LITERAL);
                }
                break;
                case K_ADD:
                case K_AND:
                case K_BETWEEN:
                case K_IN:
                case K_IS:
                case K_ISNULL:
                case K_LIKE:
                case K_NOT:
                case K_NOTNULL:
                case K_NULL:
                case K_OR:
                case K_DISTINCT:
                    enterOuterAlt(_localctx, 3);
                {
                    setState(127);
                    keyword();
                }
                break;
                case STRING_LITERAL:
                    enterOuterAlt(_localctx, 4);
                {
                    setState(128);
                    match(STRING_LITERAL);
                }
                break;
                case OPEN_PAR:
                    enterOuterAlt(_localctx, 5);
                {
                    setState(129);
                    match(OPEN_PAR);
                    setState(130);
                    any_name();
                    setState(131);
                    match(CLOSE_PAR);
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
                return precpred(_ctx, 11);
            case 1:
                return precpred(_ctx, 10);
            case 2:
                return precpred(_ctx, 9);
            case 3:
                return precpred(_ctx, 8);
            case 4:
                return precpred(_ctx, 7);
            case 5:
                return precpred(_ctx, 6);
            case 6:
                return precpred(_ctx, 2);
            case 7:
                return precpred(_ctx, 1);
            case 8:
                return precpred(_ctx, 3);
        }
        return true;
    }

    public static final String _serializedATN =
            "\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3$\u008a\4\2\t\2\4" +
                    "\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\3\2\7\2" +
                    "\26\n\2\f\2\16\2\31\13\2\3\2\3\2\3\3\3\3\3\3\3\3\5\3!\n\3\3\3\3\3\3\3" +
                    "\5\3&\n\3\3\3\3\3\3\3\3\3\5\3,\n\3\3\3\3\3\3\3\7\3\61\n\3\f\3\16\3\64" +
                    "\13\3\3\3\5\3\67\n\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3?\n\3\3\3\3\3\3\3\3\3" +
                    "\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3P\n\3\3\3\3\3\3\3\3\3" +
                    "\3\3\3\3\3\3\3\3\3\3\3\3\5\3\\\n\3\3\3\3\3\3\3\5\3a\n\3\3\3\3\3\3\3\3" +
                    "\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3m\n\3\7\3o\n\3\f\3\16\3r\13\3\3\4\3\4\3" +
                    "\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n" +
                    "\5\n\u0088\n\n\3\n\2\3\4\13\2\4\6\b\n\f\16\20\22\2\5\4\2\b\n\r\16\4\2" +
                    "\7\7\17\24\3\2\25 \u009d\2\27\3\2\2\2\4>\3\2\2\2\6s\3\2\2\2\bu\3\2\2\2" +
                    "\nw\3\2\2\2\fy\3\2\2\2\16{\3\2\2\2\20}\3\2\2\2\22\u0087\3\2\2\2\24\26" +
                    "\5\4\3\2\25\24\3\2\2\2\26\31\3\2\2\2\27\25\3\2\2\2\27\30\3\2\2\2\30\32" +
                    "\3\2\2\2\31\27\3\2\2\2\32\33\7\2\2\3\33\3\3\2\2\2\34%\b\3\1\2\35\36\5" +
                    "\f\7\2\36\37\7\3\2\2\37!\3\2\2\2 \35\3\2\2\2 !\3\2\2\2!\"\3\2\2\2\"#\5" +
                    "\16\b\2#$\7\3\2\2$&\3\2\2\2% \3\2\2\2%&\3\2\2\2&\'\3\2\2\2\'?\5\20\t\2" +
                    "()\5\n\6\2)\66\7\4\2\2*,\7 \2\2+*\3\2\2\2+,\3\2\2\2,-\3\2\2\2-\62\5\4" +
                    "\3\2./\7\6\2\2/\61\5\4\3\2\60.\3\2\2\2\61\64\3\2\2\2\62\60\3\2\2\2\62" +
                    "\63\3\2\2\2\63\67\3\2\2\2\64\62\3\2\2\2\65\67\7\b\2\2\66+\3\2\2\2\66\65" +
                    "\3\2\2\2\66\67\3\2\2\2\678\3\2\2\289\7\5\2\29?\3\2\2\2:;\7\4\2\2;<\5\4" +
                    "\3\2<=\7\5\2\2=?\3\2\2\2>\34\3\2\2\2>(\3\2\2\2>:\3\2\2\2?p\3\2\2\2@A\f" +
                    "\r\2\2AB\7\f\2\2Bo\5\4\3\16CD\f\f\2\2DE\t\2\2\2Eo\5\4\3\rFG\f\13\2\2G" +
                    "H\t\3\2\2Ho\5\4\3\fIO\f\n\2\2JP\7\31\2\2KL\7\31\2\2LP\7\34\2\2MP\7\30" +
                    "\2\2NP\7\33\2\2OJ\3\2\2\2OK\3\2\2\2OM\3\2\2\2ON\3\2\2\2PQ\3\2\2\2Qo\5" +
                    "\4\3\13RS\f\t\2\2ST\7\26\2\2To\5\4\3\nUV\f\b\2\2VW\7\37\2\2Wo\5\4\3\t" +
                    "XY\f\4\2\2Y[\7\31\2\2Z\\\7\34\2\2[Z\3\2\2\2[\\\3\2\2\2\\]\3\2\2\2]o\5" +
                    "\4\3\5^`\f\3\2\2_a\7\34\2\2`_\3\2\2\2`a\3\2\2\2ab\3\2\2\2bc\7\27\2\2c" +
                    "d\5\4\3\2de\7\26\2\2ef\5\4\3\4fo\3\2\2\2gl\f\5\2\2hm\7\32\2\2im\7\35\2" +
                    "\2jk\7\34\2\2km\7\36\2\2lh\3\2\2\2li\3\2\2\2lj\3\2\2\2mo\3\2\2\2n@\3\2" +
                    "\2\2nC\3\2\2\2nF\3\2\2\2nI\3\2\2\2nR\3\2\2\2nU\3\2\2\2nX\3\2\2\2n^\3\2" +
                    "\2\2ng\3\2\2\2or\3\2\2\2pn\3\2\2\2pq\3\2\2\2q\5\3\2\2\2rp\3\2\2\2st\t" +
                    "\4\2\2t\7\3\2\2\2uv\5\22\n\2v\t\3\2\2\2wx\5\22\n\2x\13\3\2\2\2yz\5\22" +
                    "\n\2z\r\3\2\2\2{|\5\22\n\2|\17\3\2\2\2}~\5\22\n\2~\21\3\2\2\2\177\u0088" +
                    "\7!\2\2\u0080\u0088\7\"\2\2\u0081\u0088\5\6\4\2\u0082\u0088\7#\2\2\u0083" +
                    "\u0084\7\4\2\2\u0084\u0085\5\22\n\2\u0085\u0086\7\5\2\2\u0086\u0088\3" +
                    "\2\2\2\u0087\177\3\2\2\2\u0087\u0080\3\2\2\2\u0087\u0081\3\2\2\2\u0087" +
                    "\u0082\3\2\2\2\u0087\u0083\3\2\2\2\u0088\23\3\2\2\2\20\27 %+\62\66>O[" +
                    "`lnp\u0087";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}
