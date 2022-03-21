package com.bonc.framework.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 * @author 作者: jxw 
 * @date 创建时间: 2016年10月21日 下午2:02:25 
 * @version 版本: 1.0 
 * 系统框架通用日志工具包
*/
public class FrameLogUtil {

	 private static Log createLog(Class clazz){
    	   return LogFactory.getLog(clazz);
     }
     
     public static void info(Class clazz,String msg){
    	   createLog(clazz).info(msg);
     }
     
     public static void debug(Class clazz,String msg){
    	   createLog(clazz).debug(msg);
     }
     
     public static void error(Class clazz,String msg){
    	   createLog(clazz).error(msg);
     }
     
     public static void error(Class clazz,String msg,Throwable e){
    	   createLog(clazz).error(msg,e);
     }
     
}

