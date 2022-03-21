package com.bonc.frame.module.antlr.antlrcore.sqlcondition.impl;
// Generated from SqlCondition.g4 by ANTLR 4.5.3

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SqlConditionLexer extends Lexer {
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
    public static String[] modeNames = {
            "DEFAULT_MODE"
    };

    public static final String[] ruleNames = {
            "DOT", "OPEN_PAR", "CLOSE_PAR", "COMMA", "ASSIGN", "STAR", "PLUS", "MINUS",
            "TILDE", "PIPE2", "DIV", "MOD", "LT", "LT_EQ", "GT", "GT_EQ", "NOT_EQ1",
            "NOT_EQ2", "K_ADD", "K_AND", "K_BETWEEN", "K_IN", "K_IS", "K_ISNULL",
            "K_LIKE", "K_NOT", "K_NOTNULL", "K_NULL", "K_OR", "K_DISTINCT", "IDENTIFIER",
            "NUMERIC_LITERAL", "STRING_LITERAL", "WS", "DIGIT", "A", "B", "C", "D",
            "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",
            "S", "T", "U", "V", "W", "X", "Y", "Z"
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


    public SqlConditionLexer(CharStream input) {
        super(input);
        _interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
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
    public String[] getModeNames() {
        return modeNames;
    }

    @Override
    public ATN getATN() {
        return _ATN;
    }

    public static final String _serializedATN =
            "\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2$\u017e\b\1\4\2\t" +
                    "\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13" +
                    "\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22" +
                    "\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31" +
                    "\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!" +
                    "\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4" +
                    ",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t" +
                    "\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t=" +
                    "\4>\t>\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t" +
                    "\3\n\3\n\3\13\3\13\3\13\3\f\3\f\3\r\3\r\3\16\3\16\3\17\3\17\3\17\3\20" +
                    "\3\20\3\21\3\21\3\21\3\22\3\22\3\22\3\23\3\23\3\23\3\24\3\24\3\24\3\24" +
                    "\3\25\3\25\3\25\3\25\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\27\3\27" +
                    "\3\27\3\30\3\30\3\30\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\32\3\32\3\32" +
                    "\3\32\3\32\3\33\3\33\3\33\3\33\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34" +
                    "\3\35\3\35\3\35\3\35\3\35\3\36\3\36\3\36\3\37\3\37\3\37\3\37\3\37\3\37" +
                    "\3\37\3\37\3\37\3 \3 \3 \3 \7 \u00ea\n \f \16 \u00ed\13 \3 \3 \3 \3 \3" +
                    " \7 \u00f4\n \f \16 \u00f7\13 \3 \3 \3 \7 \u00fc\n \f \16 \u00ff\13 \3" +
                    " \3 \3 \7 \u0104\n \f \16 \u0107\13 \5 \u0109\n \3!\6!\u010c\n!\r!\16" +
                    "!\u010d\3!\3!\7!\u0112\n!\f!\16!\u0115\13!\5!\u0117\n!\3!\3!\5!\u011b" +
                    "\n!\3!\6!\u011e\n!\r!\16!\u011f\5!\u0122\n!\3!\3!\6!\u0126\n!\r!\16!\u0127" +
                    "\3!\3!\5!\u012c\n!\3!\6!\u012f\n!\r!\16!\u0130\5!\u0133\n!\5!\u0135\n" +
                    "!\3\"\3\"\3\"\3\"\7\"\u013b\n\"\f\"\16\"\u013e\13\"\3\"\3\"\3#\6#\u0143" +
                    "\n#\r#\16#\u0144\3#\3#\3$\3$\3%\3%\3&\3&\3\'\3\'\3(\3(\3)\3)\3*\3*\3+" +
                    "\3+\3,\3,\3-\3-\3.\3.\3/\3/\3\60\3\60\3\61\3\61\3\62\3\62\3\63\3\63\3" +
                    "\64\3\64\3\65\3\65\3\66\3\66\3\67\3\67\38\38\39\39\3:\3:\3;\3;\3<\3<\3" +
                    "=\3=\3>\3>\2\2?\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31" +
                    "\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65" +
                    "\34\67\359\36;\37= ?!A\"C#E$G\2I\2K\2M\2O\2Q\2S\2U\2W\2Y\2[\2]\2_\2a\2" +
                    "c\2e\2g\2i\2k\2m\2o\2q\2s\2u\2w\2y\2{\2\3\2%\3\2$$\3\2bb\3\2__\5\2C\\" +
                    "aac|\6\2\62;C\\aac|\4\2--//\3\2))\5\2\13\f\17\17\"\"\3\2\62;\4\2CCcc\4" +
                    "\2DDdd\4\2EEee\4\2FFff\4\2GGgg\4\2HHhh\4\2IIii\4\2JJjj\4\2KKkk\4\2LLl" +
                    "l\4\2MMmm\4\2NNnn\4\2OOoo\4\2PPpp\4\2QQqq\4\2RRrr\4\2SSss\4\2TTtt\4\2" +
                    "UUuu\4\2VVvv\4\2WWww\4\2XXxx\4\2YYyy\4\2ZZzz\4\2[[{{\4\2\\\\||\u0179\2" +
                    "\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2" +
                    "\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2" +
                    "\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2" +
                    "\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2" +
                    "\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2" +
                    "\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\3}\3\2\2\2" +
                    "\5\177\3\2\2\2\7\u0081\3\2\2\2\t\u0083\3\2\2\2\13\u0085\3\2\2\2\r\u0087" +
                    "\3\2\2\2\17\u0089\3\2\2\2\21\u008b\3\2\2\2\23\u008d\3\2\2\2\25\u008f\3" +
                    "\2\2\2\27\u0092\3\2\2\2\31\u0094\3\2\2\2\33\u0096\3\2\2\2\35\u0098\3\2" +
                    "\2\2\37\u009b\3\2\2\2!\u009d\3\2\2\2#\u00a0\3\2\2\2%\u00a3\3\2\2\2\'\u00a6" +
                    "\3\2\2\2)\u00aa\3\2\2\2+\u00ae\3\2\2\2-\u00b6\3\2\2\2/\u00b9\3\2\2\2\61" +
                    "\u00bc\3\2\2\2\63\u00c3\3\2\2\2\65\u00c8\3\2\2\2\67\u00cc\3\2\2\29\u00d4" +
                    "\3\2\2\2;\u00d9\3\2\2\2=\u00dc\3\2\2\2?\u0108\3\2\2\2A\u0134\3\2\2\2C" +
                    "\u0136\3\2\2\2E\u0142\3\2\2\2G\u0148\3\2\2\2I\u014a\3\2\2\2K\u014c\3\2" +
                    "\2\2M\u014e\3\2\2\2O\u0150\3\2\2\2Q\u0152\3\2\2\2S\u0154\3\2\2\2U\u0156" +
                    "\3\2\2\2W\u0158\3\2\2\2Y\u015a\3\2\2\2[\u015c\3\2\2\2]\u015e\3\2\2\2_" +
                    "\u0160\3\2\2\2a\u0162\3\2\2\2c\u0164\3\2\2\2e\u0166\3\2\2\2g\u0168\3\2" +
                    "\2\2i\u016a\3\2\2\2k\u016c\3\2\2\2m\u016e\3\2\2\2o\u0170\3\2\2\2q\u0172" +
                    "\3\2\2\2s\u0174\3\2\2\2u\u0176\3\2\2\2w\u0178\3\2\2\2y\u017a\3\2\2\2{" +
                    "\u017c\3\2\2\2}~\7\60\2\2~\4\3\2\2\2\177\u0080\7*\2\2\u0080\6\3\2\2\2" +
                    "\u0081\u0082\7+\2\2\u0082\b\3\2\2\2\u0083\u0084\7.\2\2\u0084\n\3\2\2\2" +
                    "\u0085\u0086\7?\2\2\u0086\f\3\2\2\2\u0087\u0088\7,\2\2\u0088\16\3\2\2" +
                    "\2\u0089\u008a\7-\2\2\u008a\20\3\2\2\2\u008b\u008c\7/\2\2\u008c\22\3\2" +
                    "\2\2\u008d\u008e\7\u0080\2\2\u008e\24\3\2\2\2\u008f\u0090\7~\2\2\u0090" +
                    "\u0091\7~\2\2\u0091\26\3\2\2\2\u0092\u0093\7\61\2\2\u0093\30\3\2\2\2\u0094" +
                    "\u0095\7\'\2\2\u0095\32\3\2\2\2\u0096\u0097\7>\2\2\u0097\34\3\2\2\2\u0098" +
                    "\u0099\7>\2\2\u0099\u009a\7?\2\2\u009a\36\3\2\2\2\u009b\u009c\7@\2\2\u009c" +
                    " \3\2\2\2\u009d\u009e\7@\2\2\u009e\u009f\7?\2\2\u009f\"\3\2\2\2\u00a0" +
                    "\u00a1\7#\2\2\u00a1\u00a2\7?\2\2\u00a2$\3\2\2\2\u00a3\u00a4\7>\2\2\u00a4" +
                    "\u00a5\7@\2\2\u00a5&\3\2\2\2\u00a6\u00a7\5I%\2\u00a7\u00a8\5O(\2\u00a8" +
                    "\u00a9\5O(\2\u00a9(\3\2\2\2\u00aa\u00ab\5I%\2\u00ab\u00ac\5c\62\2\u00ac" +
                    "\u00ad\5O(\2\u00ad*\3\2\2\2\u00ae\u00af\5K&\2\u00af\u00b0\5Q)\2\u00b0" +
                    "\u00b1\5o8\2\u00b1\u00b2\5u;\2\u00b2\u00b3\5Q)\2\u00b3\u00b4\5Q)\2\u00b4" +
                    "\u00b5\5c\62\2\u00b5,\3\2\2\2\u00b6\u00b7\5Y-\2\u00b7\u00b8\5c\62\2\u00b8" +
                    ".\3\2\2\2\u00b9\u00ba\5Y-\2\u00ba\u00bb\5m\67\2\u00bb\60\3\2\2\2\u00bc" +
                    "\u00bd\5Y-\2\u00bd\u00be\5m\67\2\u00be\u00bf\5c\62\2\u00bf\u00c0\5q9\2" +
                    "\u00c0\u00c1\5_\60\2\u00c1\u00c2\5_\60\2\u00c2\62\3\2\2\2\u00c3\u00c4" +
                    "\5_\60\2\u00c4\u00c5\5Y-\2\u00c5\u00c6\5]/\2\u00c6\u00c7\5Q)\2\u00c7\64" +
                    "\3\2\2\2\u00c8\u00c9\5c\62\2\u00c9\u00ca\5e\63\2\u00ca\u00cb\5o8\2\u00cb" +
                    "\66\3\2\2\2\u00cc\u00cd\5c\62\2\u00cd\u00ce\5e\63\2\u00ce\u00cf\5o8\2" +
                    "\u00cf\u00d0\5c\62\2\u00d0\u00d1\5q9\2\u00d1\u00d2\5_\60\2\u00d2\u00d3" +
                    "\5_\60\2\u00d38\3\2\2\2\u00d4\u00d5\5c\62\2\u00d5\u00d6\5q9\2\u00d6\u00d7" +
                    "\5_\60\2\u00d7\u00d8\5_\60\2\u00d8:\3\2\2\2\u00d9\u00da\5e\63\2\u00da" +
                    "\u00db\5k\66\2\u00db<\3\2\2\2\u00dc\u00dd\5O(\2\u00dd\u00de\5Y-\2\u00de" +
                    "\u00df\5m\67\2\u00df\u00e0\5o8\2\u00e0\u00e1\5Y-\2\u00e1\u00e2\5c\62\2" +
                    "\u00e2\u00e3\5M\'\2\u00e3\u00e4\5o8\2\u00e4>\3\2\2\2\u00e5\u00eb\7$\2" +
                    "\2\u00e6\u00ea\n\2\2\2\u00e7\u00e8\7$\2\2\u00e8\u00ea\7$\2\2\u00e9\u00e6" +
                    "\3\2\2\2\u00e9\u00e7\3\2\2\2\u00ea\u00ed\3\2\2\2\u00eb\u00e9\3\2\2\2\u00eb" +
                    "\u00ec\3\2\2\2\u00ec\u00ee\3\2\2\2\u00ed\u00eb\3\2\2\2\u00ee\u0109\7$" +
                    "\2\2\u00ef\u00f5\7b\2\2\u00f0\u00f4\n\3\2\2\u00f1\u00f2\7b\2\2\u00f2\u00f4" +
                    "\7b\2\2\u00f3\u00f0\3\2\2\2\u00f3\u00f1\3\2\2\2\u00f4\u00f7\3\2\2\2\u00f5" +
                    "\u00f3\3\2\2\2\u00f5\u00f6\3\2\2\2\u00f6\u00f8\3\2\2\2\u00f7\u00f5\3\2" +
                    "\2\2\u00f8\u0109\7b\2\2\u00f9\u00fd\7]\2\2\u00fa\u00fc\n\4\2\2\u00fb\u00fa" +
                    "\3\2\2\2\u00fc\u00ff\3\2\2\2\u00fd\u00fb\3\2\2\2\u00fd\u00fe\3\2\2\2\u00fe" +
                    "\u0100\3\2\2\2\u00ff\u00fd\3\2\2\2\u0100\u0109\7_\2\2\u0101\u0105\t\5" +
                    "\2\2\u0102\u0104\t\6\2\2\u0103\u0102\3\2\2\2\u0104\u0107\3\2\2\2\u0105" +
                    "\u0103\3\2\2\2\u0105\u0106\3\2\2\2\u0106\u0109\3\2\2\2\u0107\u0105\3\2" +
                    "\2\2\u0108\u00e5\3\2\2\2\u0108\u00ef\3\2\2\2\u0108\u00f9\3\2\2\2\u0108" +
                    "\u0101\3\2\2\2\u0109@\3\2\2\2\u010a\u010c\5G$\2\u010b\u010a\3\2\2\2\u010c" +
                    "\u010d\3\2\2\2\u010d\u010b\3\2\2\2\u010d\u010e\3\2\2\2\u010e\u0116\3\2" +
                    "\2\2\u010f\u0113\7\60\2\2\u0110\u0112\5G$\2\u0111\u0110\3\2\2\2\u0112" +
                    "\u0115\3\2\2\2\u0113\u0111\3\2\2\2\u0113\u0114\3\2\2\2\u0114\u0117\3\2" +
                    "\2\2\u0115\u0113\3\2\2\2\u0116\u010f\3\2\2\2\u0116\u0117\3\2\2\2\u0117" +
                    "\u0121\3\2\2\2\u0118\u011a\5Q)\2\u0119\u011b\t\7\2\2\u011a\u0119\3\2\2" +
                    "\2\u011a\u011b\3\2\2\2\u011b\u011d\3\2\2\2\u011c\u011e\5G$\2\u011d\u011c" +
                    "\3\2\2\2\u011e\u011f\3\2\2\2\u011f\u011d\3\2\2\2\u011f\u0120\3\2\2\2\u0120" +
                    "\u0122\3\2\2\2\u0121\u0118\3\2\2\2\u0121\u0122\3\2\2\2\u0122\u0135\3\2" +
                    "\2\2\u0123\u0125\7\60\2\2\u0124\u0126\5G$\2\u0125\u0124\3\2\2\2\u0126" +
                    "\u0127\3\2\2\2\u0127\u0125\3\2\2\2\u0127\u0128\3\2\2\2\u0128\u0132\3\2" +
                    "\2\2\u0129\u012b\5Q)\2\u012a\u012c\t\7\2\2\u012b\u012a\3\2\2\2\u012b\u012c" +
                    "\3\2\2\2\u012c\u012e\3\2\2\2\u012d\u012f\5G$\2\u012e\u012d\3\2\2\2\u012f" +
                    "\u0130\3\2\2\2\u0130\u012e\3\2\2\2\u0130\u0131\3\2\2\2\u0131\u0133\3\2" +
                    "\2\2\u0132\u0129\3\2\2\2\u0132\u0133\3\2\2\2\u0133\u0135\3\2\2\2\u0134" +
                    "\u010b\3\2\2\2\u0134\u0123\3\2\2\2\u0135B\3\2\2\2\u0136\u013c\7)\2\2\u0137" +
                    "\u013b\n\b\2\2\u0138\u0139\7)\2\2\u0139\u013b\7)\2\2\u013a\u0137\3\2\2" +
                    "\2\u013a\u0138\3\2\2\2\u013b\u013e\3\2\2\2\u013c\u013a\3\2\2\2\u013c\u013d" +
                    "\3\2\2\2\u013d\u013f\3\2\2\2\u013e\u013c\3\2\2\2\u013f\u0140\7)\2\2\u0140" +
                    "D\3\2\2\2\u0141\u0143\t\t\2\2\u0142\u0141\3\2\2\2\u0143\u0144\3\2\2\2" +
                    "\u0144\u0142\3\2\2\2\u0144\u0145\3\2\2\2\u0145\u0146\3\2\2\2\u0146\u0147" +
                    "\b#\2\2\u0147F\3\2\2\2\u0148\u0149\t\n\2\2\u0149H\3\2\2\2\u014a\u014b" +
                    "\t\13\2\2\u014bJ\3\2\2\2\u014c\u014d\t\f\2\2\u014dL\3\2\2\2\u014e\u014f" +
                    "\t\r\2\2\u014fN\3\2\2\2\u0150\u0151\t\16\2\2\u0151P\3\2\2\2\u0152\u0153" +
                    "\t\17\2\2\u0153R\3\2\2\2\u0154\u0155\t\20\2\2\u0155T\3\2\2\2\u0156\u0157" +
                    "\t\21\2\2\u0157V\3\2\2\2\u0158\u0159\t\22\2\2\u0159X\3\2\2\2\u015a\u015b" +
                    "\t\23\2\2\u015bZ\3\2\2\2\u015c\u015d\t\24\2\2\u015d\\\3\2\2\2\u015e\u015f" +
                    "\t\25\2\2\u015f^\3\2\2\2\u0160\u0161\t\26\2\2\u0161`\3\2\2\2\u0162\u0163" +
                    "\t\27\2\2\u0163b\3\2\2\2\u0164\u0165\t\30\2\2\u0165d\3\2\2\2\u0166\u0167" +
                    "\t\31\2\2\u0167f\3\2\2\2\u0168\u0169\t\32\2\2\u0169h\3\2\2\2\u016a\u016b" +
                    "\t\33\2\2\u016bj\3\2\2\2\u016c\u016d\t\34\2\2\u016dl\3\2\2\2\u016e\u016f" +
                    "\t\35\2\2\u016fn\3\2\2\2\u0170\u0171\t\36\2\2\u0171p\3\2\2\2\u0172\u0173" +
                    "\t\37\2\2\u0173r\3\2\2\2\u0174\u0175\t \2\2\u0175t\3\2\2\2\u0176\u0177" +
                    "\t!\2\2\u0177v\3\2\2\2\u0178\u0179\t\"\2\2\u0179x\3\2\2\2\u017a\u017b" +
                    "\t#\2\2\u017bz\3\2\2\2\u017c\u017d\t$\2\2\u017d|\3\2\2\2\30\2\u00e9\u00eb" +
                    "\u00f3\u00f5\u00fd\u0105\u0108\u010d\u0113\u0116\u011a\u011f\u0121\u0127" +
                    "\u012b\u0130\u0132\u0134\u013a\u013c\u0144\3\b\2\2";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}
