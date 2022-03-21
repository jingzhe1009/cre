package com.bonc.framework.api.server.entity;

import java.util.List;

public class RestfulServer extends BaseServer{
	
	private String url;
	private List<String> params;
	private String type;
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public List<String> getParams() {
		return params;
	}
	public void setParams(List<String> params) {
		this.params = params;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	

}
