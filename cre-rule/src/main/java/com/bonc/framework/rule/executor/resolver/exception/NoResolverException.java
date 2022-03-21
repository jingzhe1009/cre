package com.bonc.framework.rule.executor.resolver.exception;

/**
 * @author 作者: jxw
 * @version 版本: 1.0
 * @date 创建时间: 2018年1月8日 下午2:17:26
 */
public class NoResolverException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = -3910798174070368357L;

    public NoResolverException(String msg) {
        super(msg);
    }

    public NoResolverException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoResolverException(Throwable cause) {
        super(cause);
    }
}

