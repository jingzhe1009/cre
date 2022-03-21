package com.bonc.framework.rule.exception;

/**
 * @author qxl
 * @version 1.0
 * @date 2018年3月1日 下午3:58:12
 */
public class RuleParseException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = -6183113915239132232L;

    public RuleParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public RuleParseException(String message) {
        super(message);
    }

    public RuleParseException(Throwable cause) {
        super(cause);
    }


}
