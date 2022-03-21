package com.bonc.frame.module.antlr.exception;


import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.misc.Pair;

/**
 * @author qxl
 * @version 1.0.0
 * @date 2016年12月6日 上午11:33:30
 */
public class MyDefaultErrorStrategy extends DefaultErrorStrategy {

    public void reportError(Parser recognizer, RecognitionException e) {
        if (inErrorRecoveryMode(recognizer))
            return;
        beginErrorCondition(recognizer);
        if (e instanceof NoViableAltException)
            reportNoViableAlternative(recognizer, (NoViableAltException) e);
        else if (e instanceof InputMismatchException)
            reportInputMismatch(recognizer, (InputMismatchException) e);
        else if (e instanceof FailedPredicateException) {
            reportFailedPredicate(recognizer, (FailedPredicateException) e);
        } else {
            System.err.println((new StringBuilder()).append("未知的错误类型: ")
                    .append(e.getClass().getName()).toString());
            recognizer.notifyErrorListeners(e.getOffendingToken(), e.getMessage(), e);
        }
    }

    protected void reportNoViableAlternative(Parser recognizer, NoViableAltException e) {
        TokenStream tokens = recognizer.getInputStream();
        String input;
        if (tokens != null) {
            if (e.getStartToken().getType() == -1)
                input = "<结尾>";
            else
                input = tokens.getText(e.getStartToken(), e.getOffendingToken());
        } else {
            input = "<未知输入>";
        }
        String msg = (new StringBuilder()).append(escapeWSAndQuote(input)).append("的内容不合法 ")
                .toString();
        recognizer.notifyErrorListeners(e.getOffendingToken(), msg, e);
    }

    protected void reportInputMismatch(Parser recognizer, InputMismatchException e) {
        String msg = (new StringBuilder()).append("无法匹配输入的")
                .append(getTokenErrorDisplay(e.getOffendingToken())).append(" 需匹配此列表 ")
                .append(e.getExpectedTokens().toString(recognizer.getVocabulary())).toString();
        recognizer.notifyErrorListeners(e.getOffendingToken(), msg, e);
    }

    protected void reportUnwantedToken(Parser recognizer) {
        if (inErrorRecoveryMode(recognizer)) {
            return;
        } else {
            beginErrorCondition(recognizer);
            Token t = recognizer.getCurrentToken();
            String tokenName = getTokenErrorDisplay(t);
            IntervalSet expecting = getExpectedTokens(recognizer);
            String msg = (new StringBuilder()).append("外部输入 ").append(tokenName).append(" 不是合法的 ")
                    .append(expecting.toString(recognizer.getVocabulary())).toString();
            recognizer.notifyErrorListeners(t, msg, null);
            return;
        }
    }

    protected void reportMissingToken(Parser recognizer) {
        if (inErrorRecoveryMode(recognizer)) {
            return;
        } else {
            beginErrorCondition(recognizer);
            Token t = recognizer.getCurrentToken();
            IntervalSet expecting = getExpectedTokens(recognizer);
            String msg = (new StringBuilder()).append("缺失 ").append(expecting.toString(recognizer.getVocabulary()))
                    .append(" 在 ").append(getTokenErrorDisplay(t)).toString();
            recognizer.notifyErrorListeners(t, msg, null);
            return;
        }
    }

    protected Token getMissingSymbol(Parser recognizer) {
        Token currentSymbol = recognizer.getCurrentToken();
        IntervalSet expecting = getExpectedTokens(recognizer);
        int expectedTokenType = expecting.getMinElement();
        String tokenText;
        if (expectedTokenType == -1)
            tokenText = "<结尾缺失>";
        else
            tokenText = (new StringBuilder()).append("<缺失 ")
                    .append(recognizer.getVocabulary().getDisplayName(expectedTokenType)).append(">").toString();
        Token current = currentSymbol;
        Token lookback = recognizer.getInputStream().LT(-1);
        if (current.getType() == -1 && lookback != null)
            current = lookback;
        return (Token) recognizer.getTokenFactory().create(
                new Pair(current.getTokenSource(), current.getTokenSource().getInputStream()), expectedTokenType,
                tokenText, 0, -1, -1, current.getLine(), current.getCharPositionInLine());
    }

    protected String getTokenErrorDisplay(Token t) {
        if (t == null)
            return "<no token>";
        String s = getSymbolText(t);
        if (s == null)
            if (getSymbolType(t) == -1)
                s = "<结尾>";
            else
                s = (new StringBuilder()).append("<").append(getSymbolType(t)).append(">").toString();
        return escapeWSAndQuote(s);
    }

}
