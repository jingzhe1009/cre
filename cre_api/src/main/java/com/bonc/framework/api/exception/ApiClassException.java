package com.bonc.framework.api.exception;

public class ApiClassException extends Exception{
	
	public ApiClassException(String msg){
    	super(msg);
    }
    
    public ApiClassException(String msg , Throwable e){
    	super(msg,e);
    }

}
