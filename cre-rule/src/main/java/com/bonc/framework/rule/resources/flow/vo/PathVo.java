package com.bonc.framework.rule.resources.flow.vo;

import java.io.Serializable;

/**
 * @author qxl
 * @version 1.0.0
 * @date 2016年11月14日 上午10:15:57
 */
public class PathVo implements Serializable {
    private static final long serialVersionUID = 1159966189003199643L;
    private String id;
    private String name;
    private String from;
    private String to;
    private String data;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "PathVo [id=" + id + ",name=" + name + " from=" + from + ", to=" + to + ", data=" + data + "]";
    }

}
