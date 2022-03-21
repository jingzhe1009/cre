package com.bonc.framework.rule.exception;

/**
 * 规则执行异常  包括规则集和路径上的规则
 *
 * @Author: wangzhengbao
 * @DATE: 2020/8/19 9:21
 */
public class ExecuteRuleException extends ExecuteModelException {
    private static final long serialVersionUID = 3619546383493503183L;

    public ExecuteRuleException() {
    }

    public ExecuteRuleException(String message) {
        super(message);
    }

    public ExecuteRuleException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExecuteRuleException(Throwable cause) {
        super(cause);
    }

    public ExecuteRuleException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
