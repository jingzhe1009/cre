package com.bonc.frame.entity.variable.reference;

/**
 * 参数-接口中间表，保存接口对参数的引用
 *
 * @author yedunyao
 * @date 2019/7/30 18:14
 */
public class VariableApi {

    public static final String RETURN_VARIABLE_CODE = "0";
    public static final String REQUEST_VARIABLE_CODE = "1";

    private String apiId;

    private String variableId;

    /**
     * 0：返回值，1：请求参数
     */
    private String apiVariableType;

    public String getApiId() {
        return apiId;
    }

    public void setApiId(String apiId) {
        this.apiId = apiId;
    }

    public String getVariableId() {
        return variableId;
    }

    public void setVariableId(String variableId) {
        this.variableId = variableId;
    }

    public String getApiVariableType() {
        return apiVariableType;
    }

    public void setApiVariableType(String apiVariableType) {
        this.apiVariableType = apiVariableType;
    }

    @Override
    public String toString() {
        return "VariableApi{" +
                "apiId='" + apiId + '\'' +
                ", variableId='" + variableId + '\'' +
                ", apiVariableType='" + apiVariableType + '\'' +
                '}';
    }
}
