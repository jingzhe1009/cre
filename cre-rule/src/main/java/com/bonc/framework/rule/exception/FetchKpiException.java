package com.bonc.framework.rule.exception;

/**
 * 获取指标异常
 * 此类异常可能因为接口不稳定，或是连接的数据源，或是指标失效，需要调用者注意
 *
 * @Author: wangzhengbao
 * @DATE: 2020/8/19 9:20
 */
public class FetchKpiException extends ExecuteModelException {
    private static final long serialVersionUID = -6725794783223106873L;

    public FetchKpiException() {
    }

    public FetchKpiException(String message) {
        super(message);
    }

    public FetchKpiException(String message, Throwable cause) {
        super(message, cause);
    }

    public FetchKpiException(Throwable cause) {
        super(cause);
    }

    public FetchKpiException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
