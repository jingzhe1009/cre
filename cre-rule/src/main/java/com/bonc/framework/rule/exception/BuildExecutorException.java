package com.bonc.framework.rule.exception;

/**
 * @author qxl
 * @version 1.0
 * @date 2018年3月1日 下午3:58:12
 */
public class BuildExecutorException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = -6183593915239132232L;

    public BuildExecutorException(String message, Throwable cause) {
        super(message, cause);
    }

    public BuildExecutorException(String message) {
        super(message);
    }

    public BuildExecutorException(Throwable cause) {
        super(cause);
    }


}
