package com.bonc.frame.exception;

public class FetchException extends RuntimeException {

    public FetchException() {
    }

    public FetchException(String message) {
        super(message);
    }

    public FetchException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
