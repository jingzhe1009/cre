package com.bonc.frame.module.vo;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/** 
 * sql规则的规则内容vo
 * @author qxl
 * @date 2016年12月23日 上午10:50:44 
 * @version 1.0.0
 */
public class SqlRuleContentVo {

	private String dbResourceId;//资源id
	private String selectTxt;//查询部分内容
	private String selectCode;//查询部分内容-code形式
	private String cdtTxt;//条件部分内容
	private String cdtCode;//条件部分内容-code形式
	
	public SqlRuleContentVo() {
		super();
	}

	public String getDbResourceId() {
		return dbResourceId;
	}

	public void setDbResourceId(String dbResourceId) {
		this.dbResourceId = dbResourceId;
	}

	public String getSelectTxt() {
		return selectTxt;
	}

	public void setSelectTxt(String selectTxt) {
		this.selectTxt = selectTxt;
	}

	public String getSelectCode() {
		return selectCode;
	}

	public void setSelectCode(String selectCode) {
		this.selectCode = selectCode;
	}

	public String getCdtTxt() {
		return cdtTxt;
	}

	public void setCdtTxt(String cdtTxt) {
		this.cdtTxt = cdtTxt;
	}

	public String getCdtCode() {
		return cdtCode;
	}

	public void setCdtCode(String cdtCode) {
		this.cdtCode = cdtCode;
	}
	
	/**
	 * 给所有的属性设置空值
	 */
	public void setAllPropertyEmpty(){
		this.setDbResourceId("");
		this.setSelectTxt("");
		this.setSelectCode("");
		this.setCdtTxt("");
		this.setCdtCode("");
	}

	@Override
	public String toString() {
		return "SqlRuleContentVo [dbResourceId=" + dbResourceId + ", selectTxt=" + selectTxt + ", selectCode="
				+ selectCode + ", cdtTxt=" + cdtTxt + ", cdtCode=" + cdtCode + "]";
	}
	
	
	/**
	 * 将当前对象转成xml
	 * @return
	 */
	public static String parseToXml(SqlRuleContentVo sqlRuleContentVo){
		Document doc = DocumentHelper.createDocument();  
        Element root = doc.addElement("sqlRuleContent");
        Element dbResourceIdE = root.addElement("dbResourceId");
        dbResourceIdE.setText(parseText(sqlRuleContentVo.getDbResourceId()));
        Element selectTxtE = root.addElement("selectTxt");
        selectTxtE.setText(parseText(sqlRuleContentVo.getSelectTxt()));
        Element selectCodeE = root.addElement("selectCode");
        selectCodeE.setText(parseText(sqlRuleContentVo.getSelectCode()));
        Element cdtTxtE = root.addElement("cdtTxt");
        cdtTxtE.setText(parseText(sqlRuleContentVo.getCdtTxt()));
        Element cdtCodeE = root.addElement("cdtCode");
        cdtCodeE.setText(parseText(sqlRuleContentVo.getCdtCode()));
        String xml = doc.asXML();
		return xml;
	}
	//处理文本为空的情况
	private static String parseText(String txt){
		if(txt==null){
			return "";
		}
		return txt;
	}
	
	/**
	 * 将xml转成当前对象
	 * @param xml
	 * @return
	 * @throws Exception
	 */
	public static SqlRuleContentVo parseToSqlRuleContentVo(String xml) throws Exception{
		SqlRuleContentVo sqlRuleContentVo = new SqlRuleContentVo();
		if(xml==null || xml.isEmpty()){
			sqlRuleContentVo.setAllPropertyEmpty();
			return sqlRuleContentVo;
		}
		Document dom = null;
		try {
			dom = DocumentHelper.parseText(xml);
			Element root = dom.getRootElement();
			String dbResourceId = root.element("dbResourceId").getText();
			sqlRuleContentVo.setDbResourceId(dbResourceId);
			String selectTxt = root.element("selectTxt").getText();
			sqlRuleContentVo.setSelectTxt(selectTxt);
			String selectCode = root.element("selectCode").getText();
			sqlRuleContentVo.setSelectCode(selectCode);
			String cdtTxt = root.element("cdtTxt").getText();
			sqlRuleContentVo.setCdtTxt(cdtTxt);
			String cdtCode = root.element("cdtCode").getText();
			sqlRuleContentVo.setCdtCode(cdtCode);
			return sqlRuleContentVo;
		} catch (DocumentException e) {
			e.printStackTrace();
			throw e;
		}
	}
}
