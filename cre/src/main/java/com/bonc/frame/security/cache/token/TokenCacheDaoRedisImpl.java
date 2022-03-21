package com.bonc.frame.security.cache.token;

import com.bonc.frame.config.Config;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @author yedunyao
 * @since 2020/10/9 15:38
 */
public class TokenCacheDaoRedisImpl implements TokenCacheDao {

    private StringRedisTemplate template;

    public TokenCacheDaoRedisImpl(StringRedisTemplate template) {
        this.template = template;
    }

    public String get(String token) {
        return template.boundValueOps(token).get();
    }

    public void set(String token, String value) {
        template.boundValueOps(token).set(value, Config.CRE_TOKEN_TTL, TimeUnit.MINUTES);
    }

    public void del(String token) {
        template.delete(token);
    }

}
