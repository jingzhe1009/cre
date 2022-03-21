package com.bonc.framework.entity.inner;

/**
 * 
 * @author qxl
 * @date 2018年3月13日 上午10:09:02
 * @version 1.0
 */
public enum InnerEntityNameEnum {

	name_string("String"),
	name_int("int"),
	name_integer("Integer"),
	name_double("double"),
	name_array("array");
	
	private String name;
	private InnerEntityNameEnum(String name){
		this.name = name;
	}
	
	public String toString(){
		return this.name;
	}
}
