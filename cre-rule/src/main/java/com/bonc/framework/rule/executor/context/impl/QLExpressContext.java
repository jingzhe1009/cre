package com.bonc.framework.rule.executor.context.impl;

import com.bonc.framework.rule.executor.context.IQLExpressContext;
import com.ql.util.express.IExpressContext;

import java.util.HashMap;
import java.util.Map;

/**
 * 基于 QLExpress框架实现的Context接口
 *
 * @author qxl
 * @version 1.0
 * @date 2018年1月8日 下午5:14:50
 */
public class QLExpressContext implements IQLExpressContext<String, Object>, IExpressContext<String, Object> {

    private Map<String, Object> content;

    public QLExpressContext() {
        this.content = new HashMap<String, Object>();
    }

    public QLExpressContext(Map<String, Object> content) {
        this.content = content;
    }

    @Override
    public Object get(Object key) {
        if (this.content != null && this.content.containsKey(key)) {
            return this.content.get(key);
        }
        return null;
    }

    @Override
    public Object put(String name, Object object) {
        if (this.content == null) {
            this.content = new HashMap<String, Object>();
        }
        return this.content.put(name, object);
    }

    @Override
    public String toString() {
        if (this.content == null) {
            return null;
        }
        return content.toString();
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> m) {
        if (this.content == null) {
            this.content = new HashMap<String, Object>();
        }
        this.content.putAll(m);
    }


}
