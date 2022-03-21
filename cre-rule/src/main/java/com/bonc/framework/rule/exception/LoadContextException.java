package com.bonc.framework.rule.exception;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/8/19 9:17
 */
public class LoadContextException extends ExecuteException {
    private static final long serialVersionUID = 7692221820769650115L;

    public LoadContextException() {
    }

    public LoadContextException(String message) {
        super(message);
    }

    public LoadContextException(String message, Throwable cause) {
        super(message, cause);
    }

    public LoadContextException(Throwable cause) {
        super(cause);
    }

    public LoadContextException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
