package com.bonc.frame.module.vo;

import com.bonc.framework.util.JsonUtils;

import java.util.HashMap;
import java.util.Map;

public class QuotaHandRuleContentVo {
	
	private String inTableId;
	private String outTableId;
	private String inTableKind;
	private String outTableKind;
	private String tblObj;
	private String content; //规则内容
	private String cdtText;
	private String cdtCode;
	
	private String quotaGroupCode;
	private String quotaGroupText;
	
	public String getInTableId() {
		return inTableId;
	}
	public void setInTableId(String inTableId) {
		this.inTableId = inTableId;
	}
	public String getOutTableId() {
		return outTableId;
	}
	public void setOutTableId(String outTableId) {
		this.outTableId = outTableId;
	}
	public String getInTableKind() {
		return inTableKind;
	}
	public void setInTableKind(String inTableKind) {
		this.inTableKind = inTableKind;
	}
	public String getOutTableKind() {
		return outTableKind;
	}
	public void setOutTableKind(String outTableKind) {
		this.outTableKind = outTableKind;
	}
	public String getTblObj() {
		return tblObj;
	}
	public void setTblObj(String tblObj) {
		this.tblObj = tblObj;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	public String getCdtText() {
		return cdtText;
	}
	public void setCdtText(String cdtText) {
		this.cdtText = cdtText;
	}
	public String getCdtCode() {
		return cdtCode;
	}
	public void setCdtCode(String cdtCode) {
		this.cdtCode = cdtCode;
	}
	public String getQuotaGroupCode() {
		return quotaGroupCode;
	}
	public void setQuotaGroupCode(String quotaGroupCode) {
		this.quotaGroupCode = quotaGroupCode;
	}
	public String getQuotaGroupText() {
		return quotaGroupText;
	}
	public void setQuotaGroupText(String quotaGroupText) {
		this.quotaGroupText = quotaGroupText;
	}
	
	/**
	 * 给所有的属性设置空值
	 */
	public void setAllPropertyEmpty(){
		this.setInTableId("");
		this.setOutTableId("");
		this.setInTableKind("");
		this.setOutTableKind("");
		this.setTblObj("");
		this.setCdtText("");
		this.setCdtCode("");
		this.setContent("");
		this.setQuotaGroupCode("");
		this.setQuotaGroupText("");
	}
	
	/**
	 * 规则属性转化成各个属性
	 * */
	public static QuotaHandRuleContentVo contentToSub(String content){
		QuotaHandRuleContentVo quotaHandRuleContentVo = new QuotaHandRuleContentVo();
		Map<String, String> map = JsonUtils.stringToCollect(content);
		quotaHandRuleContentVo.setInTableId(map.get("inTableId"));
		quotaHandRuleContentVo.setOutTableId(map.get("outTableId"));
		quotaHandRuleContentVo.setInTableKind(map.get("inTableKind"));
		quotaHandRuleContentVo.setOutTableKind(map.get("outTableKind"));
		quotaHandRuleContentVo.setTblObj(map.get("tblObj"));
		quotaHandRuleContentVo.setCdtText(map.get("cdtText"));
		quotaHandRuleContentVo.setCdtCode(map.get("cdtCode"));
		quotaHandRuleContentVo.setQuotaGroupCode(map.get("quotaGroupCode"));
		quotaHandRuleContentVo.setQuotaGroupText(map.get("quotaGroupText"));
		quotaHandRuleContentVo.setContent(content);
		return quotaHandRuleContentVo;
	}
	
	/**
	 * 各个属性转化成规则
	 * */
	public static QuotaHandRuleContentVo subToContent(String inTableId,String outTableId,String inTableKind,String outTableKind,
			String cdtText,String cdtCode,String quotaGroupCode,String quotaGroupText,String tblObj){
		QuotaHandRuleContentVo quotaHandRuleContentVo = new QuotaHandRuleContentVo();
		Map<String, String> map = new HashMap<String, String>();
		map.put("inTableId", inTableId);
		map.put("outTableId", outTableId);
		map.put("tblObj", tblObj);
		map.put("cdtText", cdtText);
		map.put("cdtCode", cdtCode);
		map.put("inTableKind", inTableKind);
		map.put("outTableKind", outTableKind);
		map.put("quotaGroupCode", quotaGroupCode);
		map.put("quotaGroupText", quotaGroupText);
		quotaHandRuleContentVo.setInTableId(inTableId);
		quotaHandRuleContentVo.setOutTableId(outTableId);
		quotaHandRuleContentVo.setInTableKind(inTableKind);
		quotaHandRuleContentVo.setOutTableKind(outTableKind);
		quotaHandRuleContentVo.setTblObj(tblObj);
		quotaHandRuleContentVo.setCdtText(cdtText);
		quotaHandRuleContentVo.setCdtCode(cdtCode);
		quotaHandRuleContentVo.setQuotaGroupCode(quotaGroupCode);
		quotaHandRuleContentVo.setQuotaGroupText(quotaGroupText);
		quotaHandRuleContentVo.setContent(JsonUtils.collectToString(map));
		return quotaHandRuleContentVo;
	}

}
