package com.bonc.frame.entity.staticdata;

/**
 * 静态数据加载实体
 * 
 */
public class StaticDataVo {
	
	public String key;
	public String value;
	public String remarks;
	public String remarks1;
	public String remarks2;
	public String remarks3;
	
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getRemarks1() {
		return remarks1;
	}
	public void setRemarks1(String remarks1) {
		this.remarks1 = remarks1;
	}
	public String getRemarks2() {
		return remarks2;
	}
	public void setRemarks2(String remarks2) {
		this.remarks2 = remarks2;
	}
	public String getRemarks3() {
		return remarks3;
	}
	public void setRemarks3(String remarks3) {
		this.remarks3 = remarks3;
	}
	
	@Override
	public String toString() {
		return "StaticDataVo [key=" + key + ", value=" + value + ", remarks=" + remarks + ", remarks1=" + remarks1
				+ ", remarks2=" + remarks2 + ", remarks3=" + remarks3 + "]";
	}
	
}
