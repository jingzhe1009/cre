package com.bonc.framework.api.core;

import com.bonc.framework.api.builder.IApiBuilder;
import com.bonc.framework.api.entity.CallApiRequest;
import com.bonc.framework.api.entity.CallApiResponse;
import com.bonc.framework.api.log.entity.ConsumerInfo;

import java.util.Map;

public interface IRuleApi {

    String callApi(String param) throws Exception;

    CallApiResponse callApi(CallApiRequest request, CallApiResponse response) throws Exception;

    void setBuilder(IApiBuilder apiBuilder);

    void setContext(Map<String, Object> context);

    void cleanCache();

    void before();

    @Deprecated
    void after(String param, String result, ConsumerInfo... consumerInfo);

    void after(CallApiRequest request, CallApiResponse response);

}
