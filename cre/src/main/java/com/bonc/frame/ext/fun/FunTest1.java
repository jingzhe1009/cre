package com.bonc.frame.ext.fun;

import java.io.Serializable;

/**
 * 
 * @author qxl
 * @date 2018年5月2日 下午2:45:34
 * @version 1.0
 */
public class FunTest1 implements Serializable{
	private static final long serialVersionUID = 2314984636430122447L;

	public static String sub(Object a,Object b) {
		return String.valueOf(a)+String.valueOf(b);
	}
}