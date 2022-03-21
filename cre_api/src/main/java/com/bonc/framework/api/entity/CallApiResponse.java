package com.bonc.framework.api.entity;


import java.util.List;
import java.util.Map;

/**
 * @author yedunyao
 * @since 2019/12/9 10:56
 */
public class CallApiResponse {

    /**
     * 是否执行成功
     */
    private boolean successed;

    /**
     * 结果描述，多为失败原因的描述
     */
    private String message;

    /**
     * 来源接口日志的Id
     */
    private String sourceLogId; // 执行来源日志的Id

    /**
     * 模型执行日志的Id
     */
    private String ruleLogId;

    /**
     * 传入的参数
     */
    private Map<String, Object> param;

    /**
     * 接口真实需要的参数
     */
    private List<String> paramCode; // 参数Code
    private Map<String, Object> realParam; // code:value

    /**
     * 接口的输出结果
     */
    private String result;


    public boolean isSuccessed() {
        return successed;
    }

    public void setSuccessed(boolean successed) {
        this.successed = successed;
    }


    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSourceLogId() {
        return sourceLogId;
    }

    public void setSourceLogId(String sourceLogId) {
        this.sourceLogId = sourceLogId;
    }

    public String getRuleLogId() {
        return ruleLogId;
    }

    public void setRuleLogId(String ruleLogId) {
        this.ruleLogId = ruleLogId;
    }

    public Map<String, Object> getParam() {
        return param;
    }

    public void setParam(Map<String, Object> param) {
        this.param = param;
    }

    public Map<String, Object> getRealParam() {
        return realParam;
    }

    public void setRealParam(Map<String, Object> realParam) {
        this.realParam = realParam;
    }

    public List<String> getParamCode() {
        return paramCode;
    }

    public void setParamCode(List<String> paramCode) {
        this.paramCode = paramCode;
    }

    public static CallApiResponse newFailedExecutorResponse(String message) {
        CallApiResponse callApiResponse = new CallApiResponse();
        callApiResponse.setMessage(message);
        return callApiResponse;
    }

}
