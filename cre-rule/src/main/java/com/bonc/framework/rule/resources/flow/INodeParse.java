package com.bonc.framework.rule.resources.flow;

/**
 * node内容解析接口
 *
 * @author qxl
 * @version 1.0
 * @date 2018年4月3日 上午9:55:44
 */
public interface INodeParse {

    /**
     * 将node内容进一步解析转换
     *
     * @param content
     * @return
     */
    String parse(String content);

}
