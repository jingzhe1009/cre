package com.bonc.framework.api.core;

import com.alibaba.fastjson.JSONObject;
import com.bonc.framework.api.entity.CallApiRequest;
import com.bonc.framework.api.entity.CallApiResponse;
import com.bonc.framework.api.log.entity.ApiLog;
import com.bonc.framework.api.server.http.HttpUtils;
import com.bonc.framework.entity.EntityEngineFactory;
import com.bonc.framework.entity.format.DataTypeEnum;
import com.bonc.framework.util.JsonUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestfulRuleApi extends AbstractRuleApi {

    private Log log = LogFactory.getLog(getClass());

    /**
     *
     */
    private static final long serialVersionUID = 5216077501716584504L;

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    @Deprecated
    public String callApi(String param) throws Exception {

        String result = "";
        try {
            Map<String, Object> map = this.context;
            String url = String.valueOf(map.get("url"));
            List<String> params = (List) map.get("param");
            String type = String.valueOf(map.get("httpType"));
            String returnValueType = String.valueOf(map.get("returnValueType"));
            String folderId = String.valueOf(map.get("folderId"));
            String returnValue = String.valueOf(map.get("returnValue"));

            Map<String, Object> paramMap = JsonUtils.stringToCollect(param);
            switch (type) {
                case "get":
                    result = doGet(url, params, paramMap);
                    break;
                case "post":
                    result = doPost(url, params, paramMap);
                    break;

                default:
                    break;
            }
            log.debug("接口引擎--调用接口[url: " + url + ", param: " + param + ", result: " + result + "]");
            result = paseResult(result, folderId, returnValueType, returnValue);
        } catch (Exception e) {
            log.error("call ruleApi param:" + param + ";result:" + result + ";exception:" + e.toString(), e);
            throw e;
        }

        return result;
    }


    @Deprecated
    public String doGet(String url, List<String> paramKeys, Map<String, Object> paramMap) throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append(url).append("?");
        for (String paramKey : paramKeys) {
            String paramValue = paramMap.get(paramKey) == null ? "" : String.valueOf(paramMap.get(paramKey));
            sb.append(paramKey).append("=").append(URLEncoder.encode(paramValue, "UTF-8"));
            sb.append("&");
        }
        sb.deleteCharAt(sb.length() - 1);

        return HttpUtils.doGet(sb.toString());
    }

    @Deprecated
    public String doPost(String url, List<String> paramKeys, Map<String, Object> paramMap) throws Exception {
        JSONObject json = new JSONObject();
        for (String paramKey : paramKeys) {
            if (paramMap.get(paramKey) == null) continue;
            json.put(paramKey, paramMap.get(paramKey));
        }
        return HttpUtils.doPost(url, json);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public CallApiResponse callApi(CallApiRequest request, CallApiResponse respons) throws Exception {
        if (respons == null) {
            respons = request.newCallApiResponse();
        }

        String result = "";
        Map<String, Object> paramMap = request.getParam();
        String param = JsonUtils.collectToString(paramMap);
        try {
            Map<String, Object> map = this.context;
            String url = String.valueOf(map.get("url"));
            List<String> params = (List) map.get("param");
            respons.setParamCode(params);
            String type = String.valueOf(map.get("httpType"));
            String returnValueType = String.valueOf(map.get("returnValueType"));
            String folderId = String.valueOf(map.get("folderId"));
            String returnValue = String.valueOf(map.get("returnValue"));

//            Map<String, Object> paramMap = JsonUtils.stringToCollect(param);

            switch (type) {
                case "get":
                    result = doGet(url, request, respons);
                    break;
                case "post":
                    result = doPost(url, request, respons);
                    break;
                default:
                    break;
            }
            log.debug("接口引擎--调用接口[url: " + url + ", param: " + param + ", result: " + result + "]");
            result = paseResult(result, folderId, returnValueType, returnValue);
            respons.setResult(result);
        } catch (Exception e) {
            log.error("call ruleApi param:" + param + ";result:" + result + ";exception:" + e.toString(), e);
            throw e;
        }

        return respons;
    }

    public String doGet(String url, CallApiRequest request, CallApiResponse response) throws Exception {
        List<String> paramKeys = response.getParamCode();

        Map<String, Object> paramMap = request.getParam() == null ? new HashMap<String, Object>() : request.getParam();
        Map<String, Object> realParam = response.getRealParam();
        if (realParam == null) {
            realParam = new HashMap<>();
            response.setRealParam(realParam);
        }
        StringBuffer sb = new StringBuffer();
        sb.append(url).append("?");
        if (paramKeys != null && !paramKeys.isEmpty()) {
            for (String paramKey : paramKeys) {
                String paramValue = paramMap.get(paramKey) == null ? "" : String.valueOf(paramMap.get(paramKey));
                sb.append(paramKey).append("=").append(URLEncoder.encode(paramValue, "UTF-8"));
                sb.append("&");
                realParam.put(paramKey, paramValue);
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        // 判断是从历史日志中获取, 还是发送请求
        ApiLog sourceApiLog = request.getSourceApiLog(request.getApiId(), realParam);
        if (sourceApiLog != null) {
            response.setSourceLogId(sourceApiLog.getLogId());
            return sourceApiLog.getCallResult();
        }
        return HttpUtils.doGet(sb.toString());
    }

    public String doPost(String url, CallApiRequest request, CallApiResponse response) throws Exception {
        List<String> paramKeys = response.getParamCode();

        Map<String, Object> paramMap = request.getParam() == null ? new HashMap<String, Object>() : request.getParam();
        Map<String, Object> realParam = response.getRealParam();
        if (realParam == null) {
            realParam = new HashMap<>();
            response.setRealParam(realParam);
            response.setRealParam(realParam);
        }
        JSONObject json = new JSONObject();
        for (String paramKey : paramKeys) {
            if (paramMap.get(paramKey) == null) continue;
            json.put(paramKey, paramMap.get(paramKey));
            realParam.put(paramKey, paramMap.get(paramKey));
        }
        // 判断是从历史日志中获取, 还是发送请求
        ApiLog sourceApiLog = request.getSourceApiLog(request.getApiId(), realParam);
        if (sourceApiLog != null) {
            response.setSourceLogId(sourceApiLog.getLogId());
            return sourceApiLog.getCallResult();
        }
        return HttpUtils.doPost(url, json);
    }

    /**
     * 将result 转换为 json  {returnValue:result}
     * 实体类型 转换为 json   // 只展示所有有值的属性
     * {
     * propertycode1:propertyValue1
     * propertycode2:propertyValue2
     * propertycode3:propertyValue3
     * }
     *
     * @param result
     * @param folderId
     * @param returnValueType
     * @param returnValue
     * @return
     * @throws Exception
     */
    public String paseResult(String result, String folderId, String returnValueType, String returnValue) throws Exception {
        Map<String, Object> rtnMap = new HashMap<String, Object>();
        if ("2".equals(returnValueType)) { // 对象
            String entityName = returnValue;
            result = JsonUtils.collectToString(EntityEngineFactory.getInstance().getEntityEngine(folderId).parseToMap(entityName, result, DataTypeEnum.json));
        } else if ("1".equals(returnValueType)) { // String 基本数据类型
            String code = returnValue;
            rtnMap.put(code, result);
            result = JsonUtils.collectToString(rtnMap);
        } else if ("0".equals(returnValueType)) {
            result = JsonUtils.collectToString(rtnMap);
        }
        return result;
    }

}
