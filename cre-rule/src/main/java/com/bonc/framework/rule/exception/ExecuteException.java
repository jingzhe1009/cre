package com.bonc.framework.rule.exception;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/8/18 14:41
 */
public class ExecuteException extends Exception {
    private static final long serialVersionUID = -8734166329179507500L;

    public ExecuteException() {
    }

    public ExecuteException(String message) {
        super(message);
    }

    public ExecuteException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExecuteException(Throwable cause) {
        super(cause);
    }

    public ExecuteException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
