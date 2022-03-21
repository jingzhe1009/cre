package com.bonc.frame.util;

import java.io.Serializable;
/**
 * 	返回结果实体
 *  格式：{"status":0,"msg":"xxx","data":xxx}
 * 
 * @author qxl
 * @date 2016年10月11日 下午6:44:38
 * @version 1.0.0
 */
public class ResponseResult implements Serializable{
	private static final long serialVersionUID = 248564336504804986L;
	
	public static final int SUCCESS_STAUS = 0;
	public static final int ERROR_STAUS = -1;
    public static final int AUTHENTICATION_FAILURE_STAUS = -2;

	private int status;//状态,0正确;其他错误
	private String msg;//消息
	private Object data;//传出去的数据
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	
	
	/**
	 * 创建执行正确的返回结果信息
	 * @return com.bonc.framework.web.vo.ResponseResult
	 */
	public static ResponseResult createSuccessInfo(){
		ResponseResult result = new ResponseResult();
		result.setStatus(ResponseResult.SUCCESS_STAUS);
		return result;
	}
	/**
	 * 创建执行正确的返回结果信息
	 * @param msg 提示信息
	 * @return com.bonc.framework.web.vo.ResponseResult
	 */
	public static ResponseResult createSuccessInfo(String msg){
		ResponseResult result = createSuccessInfo();
		result.setMsg(msg);
		return result;
	}
	/**
	 * 创建执行正确的返回结果信息
	 * @param msg 提示信息
	 * @param data 返回数据
	 * @return com.bonc.framework.web.vo.ResponseResult
	 */
	public static ResponseResult createSuccessInfo(String msg,Object data){
		ResponseResult result = createSuccessInfo();
		result.setMsg(msg);
		result.setData(data);
		return result;
	}
	
	/**
	 * 创建执行异常的返回结果信息
	 * @return com.bonc.framework.web.vo.ResponseResult
	 */
	public static ResponseResult createFailInfo(){
		ResponseResult result = new ResponseResult();
		result.setStatus(ResponseResult.ERROR_STAUS);
		return result;
	}
	/**
	 * 创建执行异常的返回结果信息
	 * @param msg 提示信息
	 * @return com.bonc.framework.web.vo.ResponseResult
	 */
	public static ResponseResult createFailInfo(String msg){
		ResponseResult result = createFailInfo();
		result.setMsg(msg);
		return result;
	}

    public static ResponseResult createAuthFailInfo(String msg) {
        ResponseResult result = new ResponseResult();
        result.setStatus(ResponseResult.AUTHENTICATION_FAILURE_STAUS);
        result.setMsg(msg);
        return result;
    }

	/**
	 * 创建执行异常的返回结果信息
	 * @param msg 提示信息
	 * @param data 返回数据
	 * @return com.bonc.framework.web.vo.ResponseResult
	 */
	public static ResponseResult createFailInfo(String msg,Object data){
		ResponseResult result = createFailInfo();
		result.setMsg(msg);
		result.setData(data);
		return result;
	}
	@Override
	public String toString() {
		return "ResponseResult [status=" + status + ", msg=" + msg + ", data=" + data + "]";
	}
	
}
