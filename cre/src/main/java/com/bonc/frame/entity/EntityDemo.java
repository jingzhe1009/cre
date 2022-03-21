package com.bonc.frame.entity;
/**
 *@author	作者：limf	
 *@date	创建时间：2018年1月29日 下午4:24:52
 *@version	版本： 1.0
 *说明：
 */
public class EntityDemo {

	private String str1;
	private String str2;
	public String getStr1() {
		return str1;
	}
	public void setStr1(String str1) {
		this.str1 = str1;
	}
	public String getStr2() {
		return str2;
	}
	public void setStr2(String str2) {
		this.str2 = str2;
	}
	public EntityDemo() {
		super();
	}
	public EntityDemo(String str1, String str2) {
		super();
		this.str1 = str1;
		this.str2 = str2;
	}
	@Override
	public String toString() {
		return "EntityDemo [str1=" + str1 + ", str2=" + str2 + "]";
	}
	
	
		
}
