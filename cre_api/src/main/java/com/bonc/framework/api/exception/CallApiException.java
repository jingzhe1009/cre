package com.bonc.framework.api.exception;

public class CallApiException extends Exception{
	public CallApiException(String msg){
    	super(msg);
    }
    
    public CallApiException(String msg , Throwable e){
    	super(msg,e);
    }


    public CallApiException(Throwable e) {
        super(e);
    }

}
