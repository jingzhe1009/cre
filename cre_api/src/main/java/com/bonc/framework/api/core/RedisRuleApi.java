package com.bonc.framework.api.core;

import com.bonc.framework.api.entity.CallApiRequest;
import com.bonc.framework.api.entity.CallApiResponse;
import com.bonc.framework.api.server.entity.RedisServer;
import com.bonc.framework.api.server.redis.CodisOpt;
import com.bonc.framework.api.server.redis.IRedisOpt;
import com.bonc.framework.api.server.redis.RedisOpt;
import com.bonc.framework.util.JsonUtils;

import java.util.HashMap;
import java.util.Map;

public class RedisRuleApi extends AbstractRuleApi {

    /**
     *
     */
    private static final long serialVersionUID = 7884185913863610443L;
    private IRedisOpt redisOpt;

    @Override
    public String callApi(String param) throws Exception {
        RedisServer redisServer = JsonUtils.toBean(JsonUtils.collectToString(this.context), RedisServer.class);
        String redisType = redisServer.getRedisType();
        String redisKey = redisServer.getRedisKey();
        Map<String, Object> paramMap = JsonUtils.stringToCollect(param);
        String key = (String) paramMap.get("redisKey");
        if (redisOpt == null) {
            if ("redis".equals(redisType)) {
                redisOpt = new RedisOpt(redisServer.getIp(), redisServer.getPort(), redisServer.getAuth());
            } else if ("codis".equals(redisType)) {
                redisOpt = new CodisOpt(redisServer.getZkAddr(), redisServer.getZkProxyDir());
            }
        }
        String message = redisOpt.get(String.format(redisKey, key));
        return message;
    }

    public CallApiResponse callApi(CallApiRequest request, CallApiResponse respons) throws Exception {
        RedisServer redisServer = JsonUtils.toBean(JsonUtils.collectToString(this.context), RedisServer.class);
        String redisType = redisServer.getRedisType();
        String redisKey = redisServer.getRedisKey();
        Map<String, Object> paramMap = request.getParam();
        String key = (String) paramMap.get("redisKey");

        Map<String, Object> realParam = new HashMap<>();
        realParam.put("redisKey", key);
        respons.setRealParam(realParam);

        if (redisOpt == null) {
            if ("redis".equals(redisType)) {
                redisOpt = new RedisOpt(redisServer.getIp(), redisServer.getPort(), redisServer.getAuth());
            } else if ("codis".equals(redisType)) {
                redisOpt = new CodisOpt(redisServer.getZkAddr(), redisServer.getZkProxyDir());
            }
        }
        String message = redisOpt.get(String.format(redisKey, key));
        respons.setResult(message);
        return respons;
    }
}
