package com.bonc.framework.entity.exception;

/**
 * 
 * @author qxl
 * @date 2018年3月1日 下午3:58:12
 * @version 1.0
 */
public class EntityFormatException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5830622332861548663L;

	public EntityFormatException(String message, Throwable cause) {
		super(message, cause);
	}

	public EntityFormatException(String message) {
		super(message);
	}

	public EntityFormatException(Throwable cause) {
		super(cause);
	}
	
	

}
