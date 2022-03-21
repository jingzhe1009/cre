package com.bonc.frame.util.http;

import com.bonc.frame.util.JsonUtils;
import com.bonc.frame.util.retry.RetryPolicy;
import com.bonc.framework.api.server.http.HttpUtils;
import com.google.common.base.Preconditions;
import org.apache.curator.utils.ThreadUtils;

import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

/**
 * @author yedunyao
 * @since 2020/9/21 11:44
 */
public class HttpClient {

    public static <T> String doGet(String url, Map<String, T> paramMap) throws Exception {
        Preconditions.checkNotNull(paramMap);
        Set<String> keySet = paramMap.keySet();
        StringBuilder sb = new StringBuilder();
        sb.append(url).append("?");
        for (String paramKey : keySet) {
            String paramValue = paramMap.get(paramKey) == null ? "" : String.valueOf(paramMap.get(paramKey));
            sb.append(paramKey).append("=").append(URLEncoder.encode(paramValue, "UTF-8"));
            sb.append("&");
        }
        sb.deleteCharAt(sb.length() - 1);
        return HttpUtils.doGet(sb.toString());
    }

    public static <T> Map  doGetReturnMap(String url, Map<String, T> paramMap) throws Exception {
        String result = doGet(url, paramMap);
        return JsonUtils.stringToMap(result);
    }

    public static <T> String doGet(String url, Map<String, T> paramMap, RetryPolicy retryPolicy) throws Exception {
        HttpRetry retryLoop = new HttpRetry(retryPolicy);
        String result = null;
        while (retryLoop.shouldContinue()) {
            try {
                result = doGet(url, paramMap);
                retryLoop.markComplete();
            } catch (Exception e) {
                ThreadUtils.checkInterrupted(e);
                retryLoop.takeException(e);
            }
        }
        return result;
    }

    public static <T> Map  doGetReturnMap(String url, Map<String, T> paramMap, RetryPolicy retryPolicy) throws Exception {
        HttpRetry retryLoop = new HttpRetry(retryPolicy);
        Map result = null;
        while (retryLoop.shouldContinue()) {
            try {
                result = doGetReturnMap(url, paramMap);
                retryLoop.markComplete();
            } catch (Exception e) {
                ThreadUtils.checkInterrupted(e);
                retryLoop.takeException(e);
            }
        }
        return result;
    }

}
