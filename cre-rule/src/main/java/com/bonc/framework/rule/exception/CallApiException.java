package com.bonc.framework.rule.exception;

/**
 * 请求接口异常
 *
 * @Author: wangzhengbao
 * @DATE: 2020/8/19 9:31
 */
public class CallApiException extends ExecuteModelException {
    private static final long serialVersionUID = -7558825765561515841L;

    public CallApiException() {
    }

    public CallApiException(String message) {
        super(message);
    }

    public CallApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public CallApiException(Throwable cause) {
        super(cause);
    }

    public CallApiException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
