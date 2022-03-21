package com.bonc.framework.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/** 
 * 
 * @author  qxl
 * @date    2017年6月14日 下午5:21:37 
 * @version 1.0
 */
public class DateUtil {
	public static final long nd = 1000 * 24 * 60 * 60;
	public static final long nh = 1000 * 60 * 60;
	public static final long nm = 1000 * 60;
    
	public static int daysOfTwo(String storeDateStr,String eventDateStr) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");//转换日期格式
		Calendar calendar = Calendar.getInstance();//初始化日期工具
		Date storeDate = sdf.parse(storeDateStr);
		Date eventDate = sdf.parse(eventDateStr);
		calendar.setTime(storeDate);
		int storeDay = calendar.get(Calendar.DAY_OF_YEAR);
		calendar.setTime(eventDate);
		int eventDay = calendar.get(Calendar.DAY_OF_YEAR);
		return Math.abs(eventDay-storeDay);
	}
	
	public static int minuteOfTwo(String storeDateStr,String eventDateStr) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");//转换日期格式
		Date storeDate = sdf.parse(storeDateStr);
		Date eventDate = sdf.parse(eventDateStr);
		long diff = eventDate.getTime() - storeDate.getTime();
		// 计算差多少分钟
		long hou = diff % nd / nh;
	    long min = diff % nd % nh / nm;
		return new Long(hou*60+min).intValue();
	}
	
	/**
	 * 当前时间，格式：yyyyMMdd HH:mm:ss
	 * @return
	 */
	public static String getNow(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");//转换日期格式
		return sdf.format(new Date());
	}
	
	/**
	 * 当前时间，格式：yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static String getNow1(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//转换日期格式
		return sdf.format(new Date());
	}
	
	/**
	 * 当前时间，格式：yyyy-MM-dd HH:mm:ss.SSS
	 * @return
	 */
	public static String getNow2(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");//转换日期格式
		return sdf.format(new Date());
	}
	
	/**
	 * 当前时间，格式：yyyyMMddHHmmss
	 * @return
	 */
	public static String getTimeStamp(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");//转换日期格式
		return sdf.format(new Date());
	}
	
	/**
	 * 获取当天的最大时间的时间戳
	 * 即当天23:59:59对应的时间戳
	 * @return
	 */
	public static long getMaxTodayTimestamp(){
		Calendar calendar = Calendar.getInstance();//初始化日期工具
		calendar.set(Calendar.HOUR_OF_DAY,23);           
		calendar.set( Calendar.MINUTE,59);           
		calendar.set(Calendar.SECOND,59);
		long t = calendar.getTimeInMillis();
		return t;
	}
	
	public static void main(String[] args) {
		System.out.println("22".compareTo("21"));
		System.out.println(getDate("yyyyMM"));
		System.out.println(getNextDay());
	}

	/**
	 * 当前时间，格式：yyyyMMdd
	 * @return
	 */
	public static String getDay() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");//转换日期格式
		return sdf.format(new Date());
	}

	/**
	 * 获取指定格式的当前日期
	 * @param format
	 * @return
	 */
	public static String getDate(String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);//转换日期格式
		return sdf.format(new Date());
	}
	
	/**
	 * 获取指定格式的上个月日期
	 * @param format
	 * @return
	 */
	public static String getLastMonth() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, -1);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");//转换日期格式
		return sdf.format(c.getTime());
	}

	/**
	 * 获取下一天的日期
	 * @return
	 */
	public static String getNextDay() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, 1);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");//转换日期格式
		return sdf.format(c.getTime());
	}
}
