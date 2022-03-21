package com.bonc.frame.service.kpi;

/**
 * @author yedunyao
 * @date 2019/10/28 14:27
 */
public enum FetchType {

    DB("0"), API("1");

    private String value;

    FetchType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
