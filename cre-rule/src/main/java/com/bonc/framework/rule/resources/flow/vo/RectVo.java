package com.bonc.framework.rule.resources.flow.vo;

import java.io.Serializable;
import java.util.Map;

/**
 * @author qxl
 * @version 1.0.0
 * @date 2016年11月14日 上午10:16:07
 */
public class RectVo implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 2191885064454543328L;
    private String id;
    private String type;
    private String name;
    private Map<String, String> props;

    //位置信息
    private String x;
    private String y;
    private String width;
    private String height;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getProps() {
        return props;
    }

    public void setProps(Map<String, String> props) {
        this.props = props;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "RectVo [id=" + id + ", type=" + type + ", props=" + props + "]";
    }

}
