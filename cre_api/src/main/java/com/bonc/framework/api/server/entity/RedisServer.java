package com.bonc.framework.api.server.entity;

import com.bonc.framework.util.JsonUtils;

public class RedisServer extends BaseServer{
	
	private String redisType;
	private String redisKey;
	private String ip;
	private int port;
	private String auth;
	private String zkAddr;
	private String zkProxyDir;
	
	public String getRedisType() {
		return redisType;
	}
	public void setRedisType(String redisType) {
		this.redisType = redisType;
	}
	public String getRedisKey() {
		return redisKey;
	}
	public void setRedisKey(String redisKey) {
		this.redisKey = redisKey;
	}
	public String getIp() {
		return JsonUtils.checkString(ip);
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getAuth() {
		return JsonUtils.checkString(auth);
	}
	public void setAuth(String auth) {
		this.auth = auth;
	}
	public String getZkAddr() {
		return JsonUtils.checkString(zkAddr);
	}
	public void setZkAddr(String zkAddr) {
		this.zkAddr = zkAddr;
	}
	public String getZkProxyDir() {
		return JsonUtils.checkString(zkProxyDir);
	}
	public void setZkProxyDir(String zkProxyDir) {
		this.zkProxyDir = zkProxyDir;
	}
	

}
