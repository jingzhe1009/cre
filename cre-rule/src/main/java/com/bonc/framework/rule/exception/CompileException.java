package com.bonc.framework.rule.exception;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/8/17 15:38
 */
public class CompileException extends ExecuteException {

    private static final long serialVersionUID = 6965619169636424446L;

    public CompileException() {
    }

    public CompileException(String message) {
        super(message);
    }

    public CompileException(String message, Throwable cause) {
        super(message, cause);
    }

    public CompileException(Throwable cause) {
        super(cause);
    }

    public CompileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
