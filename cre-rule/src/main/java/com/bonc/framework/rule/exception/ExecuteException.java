package com.bonc.framework.rule.exception;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/8/18 14:41
 */
public class ExecuteException extends Exception {
    private static final long serialVersionUID = -8734166329179507500L;

    // 用于发生异常的返回码
    // 0000--执行成功
    // 9999--规则引擎错误
    // 9998--hbase错误
    // 9997--oracle错误
    // 9996--红色接口错误
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    // 构建异常信息
    public ExecuteException(String message, String code) {
        super(message);
        this.code = code;
    }

    public ExecuteException(String message, Throwable cause, String code) {

        super(message, cause);
        this.code = code;
    }

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
