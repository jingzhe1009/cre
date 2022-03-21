package com.bonc.frame.module.vo;

/** 
 * 表达式规则的规则内容VO
 * @author qxl
 * @date 2016年11月1日 下午6:55:05 
 * @version 1.0.0
 */
public class ExpRuleContentVo {
	private String cdtTxt;//条件
	private String cdtTxtCode;//条件 中变量对应编码
	private String meetTxt;//满足条件
	private String meetTxtCode;//满足条件 中变量对应编码
	private String nmTxt;//不满足条件
	private String nmTxtCode;//不满足条件 中变量对应编码
	
	private Integer salience;//规则执行的优先级,数字(数字越大执行优先级越高)
	private String effectiveDate;//规则生效时间,默认为dd-MMM-yyyy
	private String expiresDate;//规则中失效时间,默认为dd-MMM-yyyy
	
	
	public ExpRuleContentVo() {
		super();
	}
	public String getCdtTxt() {
		return cdtTxt;
	}
	public void setCdtTxt(String cdtTxt) {
		this.cdtTxt = cdtTxt;
	}
	public String getMeetTxt() {
		return meetTxt;
	}
	public void setMeetTxt(String meetTxt) {
		this.meetTxt = meetTxt;
	}
	public String getNmTxt() {
		return nmTxt;
	}
	public void setNmTxt(String nmTxt) {
		this.nmTxt = nmTxt;
	}
	public String getCdtTxtCode() {
		return cdtTxtCode;
	}
	public void setCdtTxtCode(String cdtTxtCode) {
		this.cdtTxtCode = cdtTxtCode;
	}
	public String getMeetTxtCode() {
		return meetTxtCode;
	}
	public void setMeetTxtCode(String meetTxtCode) {
		this.meetTxtCode = meetTxtCode;
	}
	public String getNmTxtCode() {
		return nmTxtCode;
	}
	public void setNmTxtCode(String nmTxtCode) {
		this.nmTxtCode = nmTxtCode;
	}
	
	public Integer getSalience() {
		return salience;
	}
	public void setSalience(Integer salience) {
		this.salience = salience;
	}
	public String getEffectiveDate() {
		return effectiveDate;
	}
	public void setEffectiveDate(String effectiveDate) throws Exception {
		this.effectiveDate = effectiveDate;
	}
	public String getExpiresDate() {
		return expiresDate;
	}
	public void setExpiresDate(String expiresDate) {
		this.expiresDate = expiresDate;
	}
	
	/**
	 * 给所有的属性设置空值
	 */
	public void setAllPropertyEmpty(){
		this.setCdtTxt("");
		this.setMeetTxt("");
		this.setNmTxt("");
		this.setCdtTxtCode("");
		this.setMeetTxtCode("");
		this.setNmTxtCode("");
	}
	
	@Override
	public String toString() {
		return "ExpRuleContentVo [cdtTxt=" + cdtTxt + ", cdtTxtCode=" + cdtTxtCode + ", meetTxt=" + meetTxt
				+ ", meetTxtCode=" + meetTxtCode + ", nmTxt=" + nmTxt + ", nmTxtCode=" + nmTxtCode + "]";
	}
	
}
