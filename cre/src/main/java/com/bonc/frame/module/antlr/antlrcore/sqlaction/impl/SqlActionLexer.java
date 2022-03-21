package com.bonc.frame.module.antlr.antlrcore.sqlaction.impl;
// Generated from SqlAction.g4 by ANTLR 4.5.3

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SqlActionLexer extends Lexer {
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
    public static String[] modeNames = {
            "DEFAULT_MODE"
    };

    public static final String[] ruleNames = {
            "T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8",
            "NUMBER", "ID", "STRING", "EVAL", "ADD", "SUB", "MUL", "DIV", "WS"
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


    public SqlActionLexer(CharStream input) {
        super(input);
        _interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
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
    public String[] getModeNames() {
        return modeNames;
    }

    @Override
    public ATN getATN() {
        return _ATN;
    }

    public static final String _serializedATN =
            "\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\24|\b\1\4\2\t\2\4" +
                    "\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t" +
                    "\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22" +
                    "\4\23\t\23\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6" +
                    "\3\7\3\7\3\b\3\b\3\t\3\t\3\t\3\n\3\n\3\13\6\13A\n\13\r\13\16\13B\3\13" +
                    "\3\13\6\13G\n\13\r\13\16\13H\5\13K\n\13\5\13M\n\13\3\f\3\f\7\fQ\n\f\f" +
                    "\f\16\fT\13\f\3\r\3\r\3\r\3\r\7\rZ\n\r\f\r\16\r]\13\r\3\r\3\r\3\r\3\r" +
                    "\3\r\7\rd\n\r\f\r\16\rg\13\r\3\r\5\rj\n\r\3\16\3\16\3\17\3\17\3\20\3\20" +
                    "\3\21\3\21\3\22\3\22\3\23\6\23w\n\23\r\23\16\23x\3\23\3\23\4[e\2\24\3" +
                    "\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37" +
                    "\21!\22#\23%\24\3\2\5\3\2\62;\6\2\62;C\\aac|\5\2\13\f\17\17\"\"\u0086" +
                    "\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2" +
                    "\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2" +
                    "\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2" +
                    "\2\2\2%\3\2\2\2\3\'\3\2\2\2\5.\3\2\2\2\7\60\3\2\2\2\t\62\3\2\2\2\13\64" +
                    "\3\2\2\2\r\66\3\2\2\2\178\3\2\2\2\21:\3\2\2\2\23=\3\2\2\2\25@\3\2\2\2" +
                    "\27N\3\2\2\2\31i\3\2\2\2\33k\3\2\2\2\35m\3\2\2\2\37o\3\2\2\2!q\3\2\2\2" +
                    "#s\3\2\2\2%v\3\2\2\2\'(\7c\2\2()\7e\2\2)*\7v\2\2*+\7k\2\2+,\7q\2\2,-\7" +
                    "p\2\2-\4\3\2\2\2./\7=\2\2/\6\3\2\2\2\60\61\7]\2\2\61\b\3\2\2\2\62\63\7" +
                    "\60\2\2\63\n\3\2\2\2\64\65\7_\2\2\65\f\3\2\2\2\66\67\7*\2\2\67\16\3\2" +
                    "\2\289\7+\2\29\20\3\2\2\2:;\7*\2\2;<\7+\2\2<\22\3\2\2\2=>\7.\2\2>\24\3" +
                    "\2\2\2?A\t\2\2\2@?\3\2\2\2AB\3\2\2\2B@\3\2\2\2BC\3\2\2\2CL\3\2\2\2DJ\7" +
                    "\60\2\2EG\t\2\2\2FE\3\2\2\2GH\3\2\2\2HF\3\2\2\2HI\3\2\2\2IK\3\2\2\2JF" +
                    "\3\2\2\2JK\3\2\2\2KM\3\2\2\2LD\3\2\2\2LM\3\2\2\2M\26\3\2\2\2NR\t\3\2\2" +
                    "OQ\t\3\2\2PO\3\2\2\2QT\3\2\2\2RP\3\2\2\2RS\3\2\2\2S\30\3\2\2\2TR\3\2\2" +
                    "\2U[\7$\2\2VW\7^\2\2WZ\7$\2\2XZ\13\2\2\2YV\3\2\2\2YX\3\2\2\2Z]\3\2\2\2" +
                    "[\\\3\2\2\2[Y\3\2\2\2\\^\3\2\2\2][\3\2\2\2^j\7$\2\2_e\7)\2\2`a\7^\2\2" +
                    "ad\7$\2\2bd\13\2\2\2c`\3\2\2\2cb\3\2\2\2dg\3\2\2\2ef\3\2\2\2ec\3\2\2\2" +
                    "fh\3\2\2\2ge\3\2\2\2hj\7)\2\2iU\3\2\2\2i_\3\2\2\2j\32\3\2\2\2kl\7?\2\2" +
                    "l\34\3\2\2\2mn\7-\2\2n\36\3\2\2\2op\7/\2\2p \3\2\2\2qr\7,\2\2r\"\3\2\2" +
                    "\2st\7\61\2\2t$\3\2\2\2uw\t\4\2\2vu\3\2\2\2wx\3\2\2\2xv\3\2\2\2xy\3\2" +
                    "\2\2yz\3\2\2\2z{\b\23\2\2{&\3\2\2\2\16\2BHJLRY[ceix\3\b\2\2";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}
