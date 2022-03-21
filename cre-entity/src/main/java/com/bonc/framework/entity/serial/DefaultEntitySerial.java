package com.bonc.framework.entity.serial;

import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.bonc.framework.entity.vo.EntityObjectValue;
import com.bonc.framework.util.JsonUtils;

/**
 * 实体序列化接口默认实现
 * @author qxl
 * @date 2018年3月12日 上午9:47:47
 * @version 1.0
 */
public class DefaultEntitySerial implements IEntitySerial {

	@Override
	public String toJson(EntityObjectValue entityObjectValue) {
		if(entityObjectValue == null){
			return null;
		}
		String jsonStr = JsonUtils.collectToString(entityObjectValue.getValueMap());
		return jsonStr;
	}

	@Override
	public String toXml(EntityObjectValue entityObjectValue) {
		if(entityObjectValue == null){
			return null;
		}
		Map<String,Object> valueMap = entityObjectValue.getValueMap();
		Document doc = DocumentHelper.createDocument();  
		Element root = doc.addElement("root");
		for (Map.Entry<String, Object> entry : valueMap.entrySet()) {
            String tag = entry.getKey();
            Object text = entry.getValue();
            if(tag != null){
            	Element ele = root.addElement(tag);
                ele.setText(parseText(text));
            }
        }
        String xml = doc.asXML();
		return xml;
	}
	
	//处理文本为空的情况
	private static String parseText(Object txt){
		if(txt==null){
			return "";
		}
		return txt.toString();
	}

}
