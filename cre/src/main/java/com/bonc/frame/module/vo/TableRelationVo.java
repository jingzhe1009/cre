package com.bonc.frame.module.vo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/** 
 * 数据资源中表与表的关联关系VO
 * @author qxl
 * @date 2016年12月15日 下午3:50:37 
 * @version 1.0.0
 */
public class TableRelationVo {

    private static Log log = LogFactory.getLog(TableRelationVo.class);

	private String joinStr;//left join/right join/inner join
	private String aliasOnStr;//原始的表关系
	private String codeOnStr;//转成对应的code的关系
	
	public String getJoinStr() {
		return joinStr;
	}
	public void setJoinStr(String joinStr) {
		this.joinStr = joinStr;
	}
	public String getAliasOnStr() {
		return aliasOnStr;
	}
	public void setAliasOnStr(String aliasOnStr) {
		this.aliasOnStr = aliasOnStr;
	}
	public String getCodeOnStr() {
		return codeOnStr;
	}
	public void setCodeOnStr(String codeOnStr) {
		this.codeOnStr = codeOnStr;
	}
	@Override
	public String toString() {
		return "TableRelationVo [joinStr=" + joinStr + ", aliasOnStr=" + aliasOnStr + ", codeOnStr=" + codeOnStr + "]";
	}
	
	
	/**
	 * 将当前对象转成xml
	 * @return
	 */
	public static String parseToXml(TableRelationVo tableRelationVo){
		Document doc = DocumentHelper.createDocument();  
        Element root = doc.addElement("tableRelation");
        Element joinStrE = root.addElement("joinStr");
        joinStrE.setText(parseText(tableRelationVo.getJoinStr()));
        Element codeOnStrE = root.addElement("codeOnStr");
        codeOnStrE.setText(parseText(tableRelationVo.codeOnStr));
        Element aliasOnStrE = root.addElement("aliasOnStr");
        aliasOnStrE.setText(parseText(tableRelationVo.aliasOnStr));
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
    public static TableRelationVo parseToTableRelationVo(String xml) {
		TableRelationVo tableRelationVo = new TableRelationVo();
		if(xml==null || xml.isEmpty()){
			tableRelationVo.setJoinStr("left join");
			tableRelationVo.setAliasOnStr("");
			tableRelationVo.setCodeOnStr("");
			return tableRelationVo;
		}
		Document dom = null;
		try {
			dom = DocumentHelper.parseText(xml);
			Element root = dom.getRootElement();
			String joinStr = root.element("joinStr").getText();
			if(joinStr==null || joinStr.isEmpty()){
				joinStr = "left join";
			}
			tableRelationVo.setJoinStr(joinStr);
			String codeOnStr = root.element("codeOnStr").getText();
			tableRelationVo.setCodeOnStr(codeOnStr);
			String aliasOnStr = root.element("aliasOnStr").getText();
			tableRelationVo.setAliasOnStr(aliasOnStr);
			return tableRelationVo;
		} catch (DocumentException e) {
            log.error(e);
            throw new RuntimeException("转换join条件异常", e);
		}
	}
}
