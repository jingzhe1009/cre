package com.bonc.framework.rule.exception;

/**
 * @author qxl
 * @version 1.0
 * @date 2018年3月1日 下午3:58:12
 */
public class ExecuteModelException extends ExecuteException {

    private static final long serialVersionUID = -6123113915239132232L;

    public ExecuteModelException() {
    }

    public ExecuteModelException(String message) {
        super(message);
    }

    public ExecuteModelException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExecuteModelException(Throwable cause) {
        super(cause);
    }

    public ExecuteModelException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
