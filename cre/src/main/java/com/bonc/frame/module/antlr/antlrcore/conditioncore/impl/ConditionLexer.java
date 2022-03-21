package com.bonc.frame.module.antlr.antlrcore.conditioncore.impl;
// Generated from Condition.g4 by ANTLR 4.5.3

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class ConditionLexer extends Lexer {
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
    public static String[] modeNames = {
            "DEFAULT_MODE"
    };

    public static final String[] ruleNames = {
            "T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "WS",
            "ID", "NUMBER", "STRING", "AND", "OR", "ADD", "SUB", "MUL", "DIV", "GT",
            "GE", "LT", "LE", "EQUAL", "NE", "CT", "NCT", "MO", "NMO"
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


    public ConditionLexer(CharStream input) {
        super(input);
        _interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
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
    public String[] getModeNames() {
        return modeNames;
    }

    @Override
    public ATN getATN() {
        return _ATN;
    }

    public static final String _serializedATN =
            "\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\36\u00b2\b\1\4\2" +
                    "\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4" +
                    "\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22" +
                    "\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31" +
                    "\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\3\2\3\2\3\2\3\2\3\2\3\2" +
                    "\3\2\3\2\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3" +
                    "\t\3\n\6\nU\n\n\r\n\16\nV\3\n\3\n\3\13\3\13\7\13]\n\13\f\13\16\13`\13" +
                    "\13\3\f\6\fc\n\f\r\f\16\fd\3\f\3\f\6\fi\n\f\r\f\16\fj\5\fm\n\f\5\fo\n" +
                    "\f\3\r\3\r\3\r\3\r\7\ru\n\r\f\r\16\rx\13\r\3\r\3\r\3\r\3\r\3\r\7\r\177" +
                    "\n\r\f\r\16\r\u0082\13\r\3\r\5\r\u0085\n\r\3\16\3\16\3\16\3\17\3\17\3" +
                    "\17\3\20\3\20\3\21\3\21\3\22\3\22\3\23\3\23\3\24\3\24\3\25\3\25\3\25\3" +
                    "\26\3\26\3\27\3\27\3\27\3\30\3\30\3\30\3\31\3\31\3\31\3\32\3\32\3\32\3" +
                    "\33\3\33\3\33\3\33\3\34\3\34\3\34\3\35\3\35\3\35\3\35\4v\u0080\2\36\3" +
                    "\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37" +
                    "\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\34\67\359\36\3\2\6" +
                    "\5\2\13\f\17\17\"\"\5\2C\\aac|\6\2\62;C\\aac|\3\2\62;\u00bc\2\3\3\2\2" +
                    "\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3" +
                    "\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2" +
                    "\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2" +
                    "\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2" +
                    "\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\3;\3\2\2\2\5E\3" +
                    "\2\2\2\7G\3\2\2\2\tI\3\2\2\2\13K\3\2\2\2\rM\3\2\2\2\17O\3\2\2\2\21Q\3" +
                    "\2\2\2\23T\3\2\2\2\25Z\3\2\2\2\27b\3\2\2\2\31\u0084\3\2\2\2\33\u0086\3" +
                    "\2\2\2\35\u0089\3\2\2\2\37\u008c\3\2\2\2!\u008e\3\2\2\2#\u0090\3\2\2\2" +
                    "%\u0092\3\2\2\2\'\u0094\3\2\2\2)\u0096\3\2\2\2+\u0099\3\2\2\2-\u009b\3" +
                    "\2\2\2/\u009e\3\2\2\2\61\u00a1\3\2\2\2\63\u00a4\3\2\2\2\65\u00a7\3\2\2" +
                    "\2\67\u00ab\3\2\2\29\u00ae\3\2\2\2;<\7e\2\2<=\7q\2\2=>\7p\2\2>?\7f\2\2" +
                    "?@\7k\2\2@A\7v\2\2AB\7k\2\2BC\7q\2\2CD\7p\2\2D\4\3\2\2\2EF\7*\2\2F\6\3" +
                    "\2\2\2GH\7+\2\2H\b\3\2\2\2IJ\7]\2\2J\n\3\2\2\2KL\7_\2\2L\f\3\2\2\2MN\7" +
                    ".\2\2N\16\3\2\2\2OP\7}\2\2P\20\3\2\2\2QR\7\177\2\2R\22\3\2\2\2SU\t\2\2" +
                    "\2TS\3\2\2\2UV\3\2\2\2VT\3\2\2\2VW\3\2\2\2WX\3\2\2\2XY\b\n\2\2Y\24\3\2" +
                    "\2\2Z^\t\3\2\2[]\t\4\2\2\\[\3\2\2\2]`\3\2\2\2^\\\3\2\2\2^_\3\2\2\2_\26" +
                    "\3\2\2\2`^\3\2\2\2ac\t\5\2\2ba\3\2\2\2cd\3\2\2\2db\3\2\2\2de\3\2\2\2e" +
                    "n\3\2\2\2fl\7\60\2\2gi\t\5\2\2hg\3\2\2\2ij\3\2\2\2jh\3\2\2\2jk\3\2\2\2" +
                    "km\3\2\2\2lh\3\2\2\2lm\3\2\2\2mo\3\2\2\2nf\3\2\2\2no\3\2\2\2o\30\3\2\2" +
                    "\2pv\7$\2\2qr\7^\2\2ru\7$\2\2su\13\2\2\2tq\3\2\2\2ts\3\2\2\2ux\3\2\2\2" +
                    "vw\3\2\2\2vt\3\2\2\2wy\3\2\2\2xv\3\2\2\2y\u0085\7$\2\2z\u0080\7)\2\2{" +
                    "|\7^\2\2|\177\7$\2\2}\177\13\2\2\2~{\3\2\2\2~}\3\2\2\2\177\u0082\3\2\2" +
                    "\2\u0080\u0081\3\2\2\2\u0080~\3\2\2\2\u0081\u0083\3\2\2\2\u0082\u0080" +
                    "\3\2\2\2\u0083\u0085\7)\2\2\u0084p\3\2\2\2\u0084z\3\2\2\2\u0085\32\3\2" +
                    "\2\2\u0086\u0087\7(\2\2\u0087\u0088\7(\2\2\u0088\34\3\2\2\2\u0089\u008a" +
                    "\7~\2\2\u008a\u008b\7~\2\2\u008b\36\3\2\2\2\u008c\u008d\7-\2\2\u008d " +
                    "\3\2\2\2\u008e\u008f\7/\2\2\u008f\"\3\2\2\2\u0090\u0091\7,\2\2\u0091$" +
                    "\3\2\2\2\u0092\u0093\7\61\2\2\u0093&\3\2\2\2\u0094\u0095\7@\2\2\u0095" +
                    "(\3\2\2\2\u0096\u0097\7@\2\2\u0097\u0098\7?\2\2\u0098*\3\2\2\2\u0099\u009a" +
                    "\7>\2\2\u009a,\3\2\2\2\u009b\u009c\7>\2\2\u009c\u009d\7?\2\2\u009d.\3" +
                    "\2\2\2\u009e\u009f\7?\2\2\u009f\u00a0\7?\2\2\u00a0\60\3\2\2\2\u00a1\u00a2" +
                    "\7#\2\2\u00a2\u00a3\7?\2\2\u00a3\62\3\2\2\2\u00a4\u00a5\7\u5307\2\2\u00a5" +
                    "\u00a6\7\u542d\2\2\u00a6\64\3\2\2\2\u00a7\u00a8\7\u4e0f\2\2\u00a8\u00a9" +
                    "\7\u5307\2\2\u00a9\u00aa\7\u542d\2\2\u00aa\66\3\2\2\2\u00ab\u00ac\7\u5c60" +
                    "\2\2\u00ac\u00ad\7\u4e90\2\2\u00ad8\3\2\2\2\u00ae\u00af\7\u4e0f\2\2\u00af" +
                    "\u00b0\7\u5c60\2\2\u00b0\u00b1\7\u4e90\2\2\u00b1:\3\2\2\2\16\2V^djlnt" +
                    "v~\u0080\u0084\3\b\2\2";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}
