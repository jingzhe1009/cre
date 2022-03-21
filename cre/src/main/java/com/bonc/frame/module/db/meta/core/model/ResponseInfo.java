package com.bonc.frame.module.db.meta.core.model;

public class ResponseInfo {
   public String code;
   public Object msg;
   
 

  
public ResponseInfo(String code, Object msg) {
	this.code = code;
	this.msg = msg;
}
public String getCode() {
	return code;
}
public void setCode(String code) {
	this.code = code;
}
public Object getMsg() {
	return msg;
}
public void setMsg(Object msg) {
	this.msg = msg;
}
}
