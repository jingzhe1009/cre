package com.bonc.frame.module.vo;

import java.util.Date;

/**
 * 规则包查询条件
 * 
 * @author qxl
 * @date 2016年10月19日 上午9:39:41
 * @version 1.0.0
 */
public class RulePackageVo {
	private Integer start;//分页开始记录号
	private Integer length;//每页数据条数
	private Date startDate;//开始日期
	private Date endDate;//结束日期
	private String packageType;//规则包名字
	private String packageName;//规则包名字
	private String tenantId;//租户ID
	
	public Integer getStart() {
		return start;
	}
	public void setStart(Integer start) {
		this.start = start;
	}
	public Integer getLength() {
		return length;
	}
	public void setLength(Integer length) {
		this.length = length;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public String getPackageType() {
		return packageType;
	}
	public void setPackageType(String packageType) {
		this.packageType = packageType;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getTenantId() {
		return tenantId;
	}
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	
}
