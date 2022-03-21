package com.bonc.frame.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
	private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };  
	  
	private static String toHexString(byte[] b) {  
	    StringBuilder sb = new StringBuilder(b.length * 2);  
	    for (int i = 0; i < b.length; i++) {  
	        sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);  
	        sb.append(HEX_DIGITS[b[i] & 0x0f]);  
	    }  
	    return sb.toString();  
	}  
	  
	public static String Bit32(String SourceString) throws NoSuchAlgorithmException {
	    MessageDigest digest = java.security.MessageDigest.getInstance("MD5");  
	    digest.update(SourceString.getBytes());  
	    byte messageDigest[] = digest.digest();  
	    return toHexString(messageDigest);  
	}

	public static String Bit32Silence(String SourceString) {
		MessageDigest digest;
		try {
			digest = java.security.MessageDigest.getInstance("MD5");
			digest.update(SourceString.getBytes());
			byte messageDigest[] = digest.digest();
			return toHexString(messageDigest);
		} catch (NoSuchAlgorithmException e) {
			return SourceString;
		}
	}

	public static String Bit16(String SourceString) throws NoSuchAlgorithmException {
	    return Bit32(SourceString).substring(8, 24);  
	}  
	
	public static void main(String[] args) throws Exception {
		String pwd = Bit32("123456");
		String pwd1 = Bit32("admin");
		System.out.println(pwd);
		System.out.println(pwd1);
	}
}
