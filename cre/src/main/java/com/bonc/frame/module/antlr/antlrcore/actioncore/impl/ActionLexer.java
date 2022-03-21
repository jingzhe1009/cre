package com.bonc.frame.module.antlr.antlrcore.actioncore.impl;
// Generated from Action.g4 by ANTLR 4.5.3

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class ActionLexer extends Lexer {
    static {
        RuntimeMetaData.checkVersion("4.5.3", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            T__0 = 1, T__1 = 2, T__2 = 3, T__3 = 4, T__4 = 5, T__5 = 6, T__6 = 7, T__7 = 8, WS = 9,
            ID = 10, NUMBER = 11, STRING = 12, EVAL = 13, ADD = 14, SUB = 15, MUL = 16, DIV = 17;
    public static String[] modeNames = {
            "DEFAULT_MODE"
    };

    public static final String[] ruleNames = {
            "T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "WS",
            "ID", "NUMBER", "STRING", "EVAL", "ADD", "SUB", "MUL", "DIV"
    };

    private static final String[] _LITERAL_NAMES = {
            null, "'action'", "';'", "'['", "']'", "'('", "')'", "'()'", "','", null,
            null, null, null, "'='", "'+'", "'-'", "'*'", "'/'"
    };
    private static final String[] _SYMBOLIC_NAMES = {
            null, null, null, null, null, null, null, null, null, "WS", "ID", "NUMBER",
            "STRING", "EVAL", "ADD", "SUB", "MUL", "DIV"
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


    public ActionLexer(CharStream input) {
        super(input);
        _interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @Override
    public String getGrammarFileName() {
        return "Action.g4";
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
            "\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\23x\b\1\4\2\t\2\4" +
                    "\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t" +
                    "\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22" +
                    "\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3" +
                    "\b\3\b\3\b\3\t\3\t\3\n\6\n=\n\n\r\n\16\n>\3\n\3\n\3\13\3\13\7\13E\n\13" +
                    "\f\13\16\13H\13\13\3\f\6\fK\n\f\r\f\16\fL\3\f\3\f\6\fQ\n\f\r\f\16\fR\5" +
                    "\fU\n\f\5\fW\n\f\3\r\3\r\3\r\3\r\7\r]\n\r\f\r\16\r`\13\r\3\r\3\r\3\r\3" +
                    "\r\3\r\7\rg\n\r\f\r\16\rj\13\r\3\r\5\rm\n\r\3\16\3\16\3\17\3\17\3\20\3" +
                    "\20\3\21\3\21\3\22\3\22\4^h\2\23\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23" +
                    "\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23\3\2\6\5\2\13\f\17\17\"\"" +
                    "\5\2C\\aac|\6\2\62;C\\aac|\3\2\62;\u0082\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3" +
                    "\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2" +
                    "\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35" +
                    "\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\3%\3\2\2\2\5,\3\2\2\2\7.\3" +
                    "\2\2\2\t\60\3\2\2\2\13\62\3\2\2\2\r\64\3\2\2\2\17\66\3\2\2\2\219\3\2\2" +
                    "\2\23<\3\2\2\2\25B\3\2\2\2\27J\3\2\2\2\31l\3\2\2\2\33n\3\2\2\2\35p\3\2" +
                    "\2\2\37r\3\2\2\2!t\3\2\2\2#v\3\2\2\2%&\7c\2\2&\'\7e\2\2\'(\7v\2\2()\7" +
                    "k\2\2)*\7q\2\2*+\7p\2\2+\4\3\2\2\2,-\7=\2\2-\6\3\2\2\2./\7]\2\2/\b\3\2" +
                    "\2\2\60\61\7_\2\2\61\n\3\2\2\2\62\63\7*\2\2\63\f\3\2\2\2\64\65\7+\2\2" +
                    "\65\16\3\2\2\2\66\67\7*\2\2\678\7+\2\28\20\3\2\2\29:\7.\2\2:\22\3\2\2" +
                    "\2;=\t\2\2\2<;\3\2\2\2=>\3\2\2\2><\3\2\2\2>?\3\2\2\2?@\3\2\2\2@A\b\n\2" +
                    "\2A\24\3\2\2\2BF\t\3\2\2CE\t\4\2\2DC\3\2\2\2EH\3\2\2\2FD\3\2\2\2FG\3\2" +
                    "\2\2G\26\3\2\2\2HF\3\2\2\2IK\t\5\2\2JI\3\2\2\2KL\3\2\2\2LJ\3\2\2\2LM\3" +
                    "\2\2\2MV\3\2\2\2NT\7\60\2\2OQ\t\5\2\2PO\3\2\2\2QR\3\2\2\2RP\3\2\2\2RS" +
                    "\3\2\2\2SU\3\2\2\2TP\3\2\2\2TU\3\2\2\2UW\3\2\2\2VN\3\2\2\2VW\3\2\2\2W" +
                    "\30\3\2\2\2X^\7$\2\2YZ\7^\2\2Z]\7$\2\2[]\13\2\2\2\\Y\3\2\2\2\\[\3\2\2" +
                    "\2]`\3\2\2\2^_\3\2\2\2^\\\3\2\2\2_a\3\2\2\2`^\3\2\2\2am\7$\2\2bh\7)\2" +
                    "\2cd\7^\2\2dg\7$\2\2eg\13\2\2\2fc\3\2\2\2fe\3\2\2\2gj\3\2\2\2hi\3\2\2" +
                    "\2hf\3\2\2\2ik\3\2\2\2jh\3\2\2\2km\7)\2\2lX\3\2\2\2lb\3\2\2\2m\32\3\2" +
                    "\2\2no\7?\2\2o\34\3\2\2\2pq\7-\2\2q\36\3\2\2\2rs\7/\2\2s \3\2\2\2tu\7" +
                    ",\2\2u\"\3\2\2\2vw\7\61\2\2w$\3\2\2\2\16\2>FLRTV\\^fhl\3\b\2\2";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}
