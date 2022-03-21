package com.bonc.frame.entity.modelCompare.entity;

public enum ModelCompareType {

    START("start"), END("end"), FORK("fork"), JOIN("join"), TASK("task"), INTERFACE("interface"), PATH("path"), ABS("abstract");

    private String value;

    ModelCompareType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
