package com.bonc.framework.rule.exception;

/**
 * @author qxl
 * @version 1.0
 * @date 2018年3月1日 下午3:58:12
 */
public class NoCompiledRuleException extends CompileException {

    /**
     *
     */
    private static final long serialVersionUID = -6183593915239112332L;

    public NoCompiledRuleException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoCompiledRuleException(String message) {
        super(message);
    }

    public NoCompiledRuleException(Throwable cause) {
        super(cause);
    }


}
