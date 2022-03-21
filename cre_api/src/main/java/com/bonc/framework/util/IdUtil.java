package com.bonc.framework.util;

import java.net.InetAddress;
import java.util.UUID;

/**
 * ID生成工具类
 * 
 * @author qxl
 * @date 2016年10月18日 上午9:06:17
 * @version 1.0.0
 */
public class IdUtil {
	
	private static final String ROLE_HEADER = "ROLE_";
	
    /** 
     * 产生一个32位的UUID 
     * @return 
     */  
    public static String createId() {  
        return new StringBuilder(32).append(format(getIP())).append(  
                format(getJVM())).append(format(getHiTime())).append(  
                format(getLoTime())).append(format(getCount())).toString();  
    }  
    
    public static String getUUID() {
    	 String uuid = UUID.randomUUID().toString(); //获取UUID并转化为String对象  
         uuid = uuid.replace("-", "");               //因为UUID本身为32位只是生成时多了“-”，所以将它们去点就可  
         return uuid;
    }
    
    /**
     * 产生一个37位的role id
     */
    public static String getGenerateRoleID(){
    	return ROLE_HEADER+createId();
    }
    
    private static final int IP;  
    static {  
        int ipadd;  
        try {  
            ipadd = toInt(InetAddress.getLocalHost().getAddress());  
        } catch (Exception e) {  
            ipadd = 0;  
        }  
        IP = ipadd;  
    }  
  
    private static short counter = (short) 0;  
  
    private static final int JVM = (int) (System.currentTimeMillis() >>> 8);  
  
    private final static String format(int intval) {  
        String formatted = Integer.toHexString(intval);  
        StringBuilder buf = new StringBuilder("00000000");  
        buf.replace(8 - formatted.length(), 8, formatted);  
        return buf.toString();  
    }  
  
    private final static String format(short shortval) {  
        String formatted = Integer.toHexString(shortval);  
        StringBuilder buf = new StringBuilder("0000");  
        buf.replace(4 - formatted.length(), 4, formatted);  
        return buf.toString();  
    }  
  
    private final static int getJVM() {  
        return JVM;  
    }  
  
    private final static short getCount() {  
        synchronized (IdUtil.class) {  
            if (counter < 0)  
                counter = 0;  
            return counter++;  
        }  
    }  
  
    /** 
     * Unique in a local network 
     */  
    private final static int getIP() {  
        return IP;  
    }  
  
    /** 
     * Unique down to millisecond 
     */  
    private final static short getHiTime() {  
        return (short) (System.currentTimeMillis() >>> 32);  
    }  
  
    private final static int getLoTime() {  
        return (int) System.currentTimeMillis();  
    }  
  
    private final static int toInt(byte[] bytes) {  
        int result = 0;  
        for (int i = 0; i < 4; i++) {  
            result = (result << 8) - Byte.MIN_VALUE + (int) bytes[i];  
        }  
        return result;  
    }  
	
	public static void main(String[] args) {
		for(int i=0;i<10;i++){
			System.out.println(IdUtil.createId());
		}
	}
}
