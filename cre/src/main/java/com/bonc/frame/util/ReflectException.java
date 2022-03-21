package com.bonc.frame.util;

/**
 * @author yedunyao
 * @date 2019/7/29 15:02
 */
public class ReflectException extends Exception {

    /**
     * Creates a new ReflectException.
     */
    public ReflectException() {
        super();
    }

    /**
     * Constructs a new ReflectException.
     *
     * @param message the reason for the exception
     */
    public ReflectException(String message) {
        super(message);
    }

    /**
     * Constructs a new ReflectException.
     *
     * @param cause the underlying Throwable that caused this exception to be thrown.
     */
    public ReflectException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new ReflectException.
     *
     * @param message the reason for the exception
     * @param cause   the underlying Throwable that caused this exception to be thrown.
     */
    public ReflectException(String message, Throwable cause) {
        super(message, cause);
    }

}
