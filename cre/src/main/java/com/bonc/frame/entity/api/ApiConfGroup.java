package com.bonc.frame.entity.api;

/**
 * @author yedunyao
 * @date 2019/7/28 18:58
 */
public class ApiConfGroup extends ApiConf {

    private String apiGroupName;

    public String getApiGroupName() {
        return apiGroupName;
    }

    public void setApiGroupName(String apiGroupName) {
        this.apiGroupName = apiGroupName;
    }

    @Override
    public String toString() {
        return "ApiConfGroup{" +
                "apiGroupName='" + apiGroupName + '\'' +
                '}';
    }
}
