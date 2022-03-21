package com.bonc.frame.module.antlr.antlrcore.sqlcdt.impl;
// Generated from SqlCdt.g4 by ANTLR 4.5.3

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SqlCdtLexer extends Lexer {
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
    public static String[] modeNames = {
            "DEFAULT_MODE"
    };

    public static final String[] ruleNames = {
            "DOT", "OPEN_PAR", "CLOSE_PAR", "COMMA", "ASSIGN", "STAR", "PLUS", "MINUS",
            "TILDE", "PIPE2", "DIV", "MOD", "LT", "LT_EQ", "GT", "GT_EQ", "NOT_EQ1",
            "NOT_EQ2", "K_ADD", "K_AND", "K_BETWEEN", "K_IN", "K_IS", "K_ISNULL",
            "K_LIKE", "K_NOT", "K_NOTNULL", "K_NULL", "K_OR", "K_DISTINCT", "NUMBER",
            "ID", "ID1", "ID2", "STRING", "WS", "DIGIT", "A", "B", "C", "D", "E",
            "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S",
            "T", "U", "V", "W", "X", "Y", "Z"
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


    public SqlCdtLexer(CharStream input) {
        super(input);
        _interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
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
    public String[] getModeNames() {
        return modeNames;
    }

    @Override
    public ATN getATN() {
        return _ATN;
    }

    public static final String _serializedATN =
            "\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2&\u015f\b\1\4\2\t" +
                    "\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13" +
                    "\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22" +
                    "\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31" +
                    "\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!" +
                    "\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4" +
                    ",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t" +
                    "\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t=" +
                    "\4>\t>\4?\t?\4@\t@\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b" +
                    "\3\b\3\t\3\t\3\n\3\n\3\13\3\13\3\13\3\f\3\f\3\r\3\r\3\16\3\16\3\17\3\17" +
                    "\3\17\3\20\3\20\3\21\3\21\3\21\3\22\3\22\3\22\3\23\3\23\3\23\3\24\3\24" +
                    "\3\24\3\24\3\25\3\25\3\25\3\25\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26" +
                    "\3\27\3\27\3\27\3\30\3\30\3\30\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\32" +
                    "\3\32\3\32\3\32\3\32\3\33\3\33\3\33\3\33\3\34\3\34\3\34\3\34\3\34\3\34" +
                    "\3\34\3\34\3\35\3\35\3\35\3\35\3\35\3\36\3\36\3\36\3\37\3\37\3\37\3\37" +
                    "\3\37\3\37\3\37\3\37\3\37\3 \6 \u00eb\n \r \16 \u00ec\3 \3 \6 \u00f1\n" +
                    " \r \16 \u00f2\5 \u00f5\n \5 \u00f7\n \3!\3!\7!\u00fb\n!\f!\16!\u00fe" +
                    "\13!\3\"\6\"\u0101\n\"\r\"\16\"\u0102\3#\3#\3#\3#\5#\u0109\n#\3#\3#\3" +
                    "$\3$\3$\3$\7$\u0111\n$\f$\16$\u0114\13$\3$\3$\3$\3$\3$\7$\u011b\n$\f$" +
                    "\16$\u011e\13$\3$\5$\u0121\n$\3%\6%\u0124\n%\r%\16%\u0125\3%\3%\3&\3&" +
                    "\3\'\3\'\3(\3(\3)\3)\3*\3*\3+\3+\3,\3,\3-\3-\3.\3.\3/\3/\3\60\3\60\3\61" +
                    "\3\61\3\62\3\62\3\63\3\63\3\64\3\64\3\65\3\65\3\66\3\66\3\67\3\67\38\3" +
                    "8\39\39\3:\3:\3;\3;\3<\3<\3=\3=\3>\3>\3?\3?\3@\3@\4\u0112\u011c\2A\3\3" +
                    "\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21" +
                    "!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\34\67\359\36;\37= ?!" +
                    "A\"C#E$G%I&K\2M\2O\2Q\2S\2U\2W\2Y\2[\2]\2_\2a\2c\2e\2g\2i\2k\2m\2o\2q" +
                    "\2s\2u\2w\2y\2{\2}\2\177\2\3\2 \3\2\62;\5\2C\\aac|\6\2\62;C\\aac|\5\2" +
                    "\13\f\17\17\"\"\4\2CCcc\4\2DDdd\4\2EEee\4\2FFff\4\2GGgg\4\2HHhh\4\2II" +
                    "ii\4\2JJjj\4\2KKkk\4\2LLll\4\2MMmm\4\2NNnn\4\2OOoo\4\2PPpp\4\2QQqq\4\2" +
                    "RRrr\4\2SSss\4\2TTtt\4\2UUuu\4\2VVvv\4\2WWww\4\2XXxx\4\2YYyy\4\2ZZzz\4" +
                    "\2[[{{\4\2\\\\||\u0150\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2" +
                    "\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25" +
                    "\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2" +
                    "\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2" +
                    "\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3" +
                    "\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2" +
                    "\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\3\u0081\3\2\2\2\5\u0083\3\2\2\2\7" +
                    "\u0085\3\2\2\2\t\u0087\3\2\2\2\13\u0089\3\2\2\2\r\u008b\3\2\2\2\17\u008d" +
                    "\3\2\2\2\21\u008f\3\2\2\2\23\u0091\3\2\2\2\25\u0093\3\2\2\2\27\u0096\3" +
                    "\2\2\2\31\u0098\3\2\2\2\33\u009a\3\2\2\2\35\u009c\3\2\2\2\37\u009f\3\2" +
                    "\2\2!\u00a1\3\2\2\2#\u00a4\3\2\2\2%\u00a7\3\2\2\2\'\u00aa\3\2\2\2)\u00ae" +
                    "\3\2\2\2+\u00b2\3\2\2\2-\u00ba\3\2\2\2/\u00bd\3\2\2\2\61\u00c0\3\2\2\2" +
                    "\63\u00c7\3\2\2\2\65\u00cc\3\2\2\2\67\u00d0\3\2\2\29\u00d8\3\2\2\2;\u00dd" +
                    "\3\2\2\2=\u00e0\3\2\2\2?\u00ea\3\2\2\2A\u00f8\3\2\2\2C\u0100\3\2\2\2E" +
                    "\u0104\3\2\2\2G\u0120\3\2\2\2I\u0123\3\2\2\2K\u0129\3\2\2\2M\u012b\3\2" +
                    "\2\2O\u012d\3\2\2\2Q\u012f\3\2\2\2S\u0131\3\2\2\2U\u0133\3\2\2\2W\u0135" +
                    "\3\2\2\2Y\u0137\3\2\2\2[\u0139\3\2\2\2]\u013b\3\2\2\2_\u013d\3\2\2\2a" +
                    "\u013f\3\2\2\2c\u0141\3\2\2\2e\u0143\3\2\2\2g\u0145\3\2\2\2i\u0147\3\2" +
                    "\2\2k\u0149\3\2\2\2m\u014b\3\2\2\2o\u014d\3\2\2\2q\u014f\3\2\2\2s\u0151" +
                    "\3\2\2\2u\u0153\3\2\2\2w\u0155\3\2\2\2y\u0157\3\2\2\2{\u0159\3\2\2\2}" +
                    "\u015b\3\2\2\2\177\u015d\3\2\2\2\u0081\u0082\7\60\2\2\u0082\4\3\2\2\2" +
                    "\u0083\u0084\7*\2\2\u0084\6\3\2\2\2\u0085\u0086\7+\2\2\u0086\b\3\2\2\2" +
                    "\u0087\u0088\7.\2\2\u0088\n\3\2\2\2\u0089\u008a\7?\2\2\u008a\f\3\2\2\2" +
                    "\u008b\u008c\7,\2\2\u008c\16\3\2\2\2\u008d\u008e\7-\2\2\u008e\20\3\2\2" +
                    "\2\u008f\u0090\7/\2\2\u0090\22\3\2\2\2\u0091\u0092\7\u0080\2\2\u0092\24" +
                    "\3\2\2\2\u0093\u0094\7~\2\2\u0094\u0095\7~\2\2\u0095\26\3\2\2\2\u0096" +
                    "\u0097\7\61\2\2\u0097\30\3\2\2\2\u0098\u0099\7\'\2\2\u0099\32\3\2\2\2" +
                    "\u009a\u009b\7>\2\2\u009b\34\3\2\2\2\u009c\u009d\7>\2\2\u009d\u009e\7" +
                    "?\2\2\u009e\36\3\2\2\2\u009f\u00a0\7@\2\2\u00a0 \3\2\2\2\u00a1\u00a2\7" +
                    "@\2\2\u00a2\u00a3\7?\2\2\u00a3\"\3\2\2\2\u00a4\u00a5\7#\2\2\u00a5\u00a6" +
                    "\7?\2\2\u00a6$\3\2\2\2\u00a7\u00a8\7>\2\2\u00a8\u00a9\7@\2\2\u00a9&\3" +
                    "\2\2\2\u00aa\u00ab\5M\'\2\u00ab\u00ac\5S*\2\u00ac\u00ad\5S*\2\u00ad(\3" +
                    "\2\2\2\u00ae\u00af\5M\'\2\u00af\u00b0\5g\64\2\u00b0\u00b1\5S*\2\u00b1" +
                    "*\3\2\2\2\u00b2\u00b3\5O(\2\u00b3\u00b4\5U+\2\u00b4\u00b5\5s:\2\u00b5" +
                    "\u00b6\5y=\2\u00b6\u00b7\5U+\2\u00b7\u00b8\5U+\2\u00b8\u00b9\5g\64\2\u00b9" +
                    ",\3\2\2\2\u00ba\u00bb\5]/\2\u00bb\u00bc\5g\64\2\u00bc.\3\2\2\2\u00bd\u00be" +
                    "\5]/\2\u00be\u00bf\5q9\2\u00bf\60\3\2\2\2\u00c0\u00c1\5]/\2\u00c1\u00c2" +
                    "\5q9\2\u00c2\u00c3\5g\64\2\u00c3\u00c4\5u;\2\u00c4\u00c5\5c\62\2\u00c5" +
                    "\u00c6\5c\62\2\u00c6\62\3\2\2\2\u00c7\u00c8\5c\62\2\u00c8\u00c9\5]/\2" +
                    "\u00c9\u00ca\5a\61\2\u00ca\u00cb\5U+\2\u00cb\64\3\2\2\2\u00cc\u00cd\5" +
                    "g\64\2\u00cd\u00ce\5i\65\2\u00ce\u00cf\5s:\2\u00cf\66\3\2\2\2\u00d0\u00d1" +
                    "\5g\64\2\u00d1\u00d2\5i\65\2\u00d2\u00d3\5s:\2\u00d3\u00d4\5g\64\2\u00d4" +
                    "\u00d5\5u;\2\u00d5\u00d6\5c\62\2\u00d6\u00d7\5c\62\2\u00d78\3\2\2\2\u00d8" +
                    "\u00d9\5g\64\2\u00d9\u00da\5u;\2\u00da\u00db\5c\62\2\u00db\u00dc\5c\62" +
                    "\2\u00dc:\3\2\2\2\u00dd\u00de\5i\65\2\u00de\u00df\5o8\2\u00df<\3\2\2\2" +
                    "\u00e0\u00e1\5S*\2\u00e1\u00e2\5]/\2\u00e2\u00e3\5q9\2\u00e3\u00e4\5s" +
                    ":\2\u00e4\u00e5\5]/\2\u00e5\u00e6\5g\64\2\u00e6\u00e7\5Q)\2\u00e7\u00e8" +
                    "\5s:\2\u00e8>\3\2\2\2\u00e9\u00eb\t\2\2\2\u00ea\u00e9\3\2\2\2\u00eb\u00ec" +
                    "\3\2\2\2\u00ec\u00ea\3\2\2\2\u00ec\u00ed\3\2\2\2\u00ed\u00f6\3\2\2\2\u00ee" +
                    "\u00f4\7\60\2\2\u00ef\u00f1\t\2\2\2\u00f0\u00ef\3\2\2\2\u00f1\u00f2\3" +
                    "\2\2\2\u00f2\u00f0\3\2\2\2\u00f2\u00f3\3\2\2\2\u00f3\u00f5\3\2\2\2\u00f4" +
                    "\u00f0\3\2\2\2\u00f4\u00f5\3\2\2\2\u00f5\u00f7\3\2\2\2\u00f6\u00ee\3\2" +
                    "\2\2\u00f6\u00f7\3\2\2\2\u00f7@\3\2\2\2\u00f8\u00fc\t\3\2\2\u00f9\u00fb" +
                    "\t\4\2\2\u00fa\u00f9\3\2\2\2\u00fb\u00fe\3\2\2\2\u00fc\u00fa\3\2\2\2\u00fc" +
                    "\u00fd\3\2\2\2\u00fdB\3\2\2\2\u00fe\u00fc\3\2\2\2\u00ff\u0101\t\4\2\2" +
                    "\u0100\u00ff\3\2\2\2\u0101\u0102\3\2\2\2\u0102\u0100\3\2\2\2\u0102\u0103" +
                    "\3\2\2\2\u0103D\3\2\2\2\u0104\u0105\7]\2\2\u0105\u0108\5C\"\2\u0106\u0107" +
                    "\7\60\2\2\u0107\u0109\5C\"\2\u0108\u0106\3\2\2\2\u0108\u0109\3\2\2\2\u0109" +
                    "\u010a\3\2\2\2\u010a\u010b\7_\2\2\u010bF\3\2\2\2\u010c\u0112\7$\2\2\u010d" +
                    "\u010e\7^\2\2\u010e\u0111\7$\2\2\u010f\u0111\13\2\2\2\u0110\u010d\3\2" +
                    "\2\2\u0110\u010f\3\2\2\2\u0111\u0114\3\2\2\2\u0112\u0113\3\2\2\2\u0112" +
                    "\u0110\3\2\2\2\u0113\u0115\3\2\2\2\u0114\u0112\3\2\2\2\u0115\u0121\7$" +
                    "\2\2\u0116\u011c\7)\2\2\u0117\u0118\7^\2\2\u0118\u011b\7$\2\2\u0119\u011b" +
                    "\13\2\2\2\u011a\u0117\3\2\2\2\u011a\u0119\3\2\2\2\u011b\u011e\3\2\2\2" +
                    "\u011c\u011d\3\2\2\2\u011c\u011a\3\2\2\2\u011d\u011f\3\2\2\2\u011e\u011c" +
                    "\3\2\2\2\u011f\u0121\7)\2\2\u0120\u010c\3\2\2\2\u0120\u0116\3\2\2\2\u0121" +
                    "H\3\2\2\2\u0122\u0124\t\5\2\2\u0123\u0122\3\2\2\2\u0124\u0125\3\2\2\2" +
                    "\u0125\u0123\3\2\2\2\u0125\u0126\3\2\2\2\u0126\u0127\3\2\2\2\u0127\u0128" +
                    "\b%\2\2\u0128J\3\2\2\2\u0129\u012a\t\2\2\2\u012aL\3\2\2\2\u012b\u012c" +
                    "\t\6\2\2\u012cN\3\2\2\2\u012d\u012e\t\7\2\2\u012eP\3\2\2\2\u012f\u0130" +
                    "\t\b\2\2\u0130R\3\2\2\2\u0131\u0132\t\t\2\2\u0132T\3\2\2\2\u0133\u0134" +
                    "\t\n\2\2\u0134V\3\2\2\2\u0135\u0136\t\13\2\2\u0136X\3\2\2\2\u0137\u0138" +
                    "\t\f\2\2\u0138Z\3\2\2\2\u0139\u013a\t\r\2\2\u013a\\\3\2\2\2\u013b\u013c" +
                    "\t\16\2\2\u013c^\3\2\2\2\u013d\u013e\t\17\2\2\u013e`\3\2\2\2\u013f\u0140" +
                    "\t\20\2\2\u0140b\3\2\2\2\u0141\u0142\t\21\2\2\u0142d\3\2\2\2\u0143\u0144" +
                    "\t\22\2\2\u0144f\3\2\2\2\u0145\u0146\t\23\2\2\u0146h\3\2\2\2\u0147\u0148" +
                    "\t\24\2\2\u0148j\3\2\2\2\u0149\u014a\t\25\2\2\u014al\3\2\2\2\u014b\u014c" +
                    "\t\26\2\2\u014cn\3\2\2\2\u014d\u014e\t\27\2\2\u014ep\3\2\2\2\u014f\u0150" +
                    "\t\30\2\2\u0150r\3\2\2\2\u0151\u0152\t\31\2\2\u0152t\3\2\2\2\u0153\u0154" +
                    "\t\32\2\2\u0154v\3\2\2\2\u0155\u0156\t\33\2\2\u0156x\3\2\2\2\u0157\u0158" +
                    "\t\34\2\2\u0158z\3\2\2\2\u0159\u015a\t\35\2\2\u015a|\3\2\2\2\u015b\u015c" +
                    "\t\36\2\2\u015c~\3\2\2\2\u015d\u015e\t\37\2\2\u015e\u0080\3\2\2\2\20\2" +
                    "\u00ec\u00f2\u00f4\u00f6\u00fc\u0102\u0108\u0110\u0112\u011a\u011c\u0120" +
                    "\u0125\3\b\2\2";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}
