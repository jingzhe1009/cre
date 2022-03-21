package com.bonc.framework.rule.executor.resolver.exception;

/**
 * @author 作者: jxw
 * @version 版本: 1.0
 * @date 创建时间: 2018年1月8日 下午2:17:26
 */
public class ResolverException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = -3910798174070368358L;

    public ResolverException(String msg) {
        super(msg);
    }

    public ResolverException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResolverException(Throwable cause) {
        super(cause);
    }
}

