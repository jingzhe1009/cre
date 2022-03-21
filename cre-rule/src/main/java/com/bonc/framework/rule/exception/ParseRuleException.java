package com.bonc.framework.rule.exception;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/8/19 9:19
 */
public class ParseRuleException extends CompileException {
    private static final long serialVersionUID = -2018553855104516600L;

    public ParseRuleException() {
    }

    public ParseRuleException(String message) {
        super(message);
    }

    public ParseRuleException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParseRuleException(Throwable cause) {
        super(cause);
    }

    public ParseRuleException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
