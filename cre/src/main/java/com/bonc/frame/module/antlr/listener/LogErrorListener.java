package com.bonc.frame.module.antlr.listener;


import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

/**
 * @author 作者: jxw
 * @version 版本: 1.0
 * @date 创建时间: 2016年10月31日 下午3:42:04
 */
public class LogErrorListener extends BaseErrorListener {

    private static final ThreadLocal<String> antlrErrorSession = new ThreadLocal<String>();

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
                            Object offendingSymbol,
                            int line,
                            int charPositionInLine,
                            String msg,
                            RecognitionException e) {
        StringBuffer sb = new StringBuffer();
        sb.append("第").append("[").append(line).append("]")
                .append("行，第").append("[").append(charPositionInLine).append("]")
                .append("列，发生错误：").append(msg);
        antlrErrorSession.set(sb.toString());
    }


    public static ThreadLocal<String> getLogErrorSession() {
        return antlrErrorSession;
    }
}

