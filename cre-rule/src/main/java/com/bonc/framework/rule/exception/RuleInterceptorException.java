package com.bonc.framework.rule.exception;

/**
 * @author qxl
 * @version 1.0
 * @date 2018年3月1日 下午3:58:12
 */
public class RuleInterceptorException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = -6183593915239132762L;

    public RuleInterceptorException(String message, Throwable cause) {
        super(message, cause);
    }

    public RuleInterceptorException(String message) {
        super(message);
    }


}
