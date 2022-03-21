package com.bonc.frame.security.cache.token;

import com.bonc.frame.config.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author yedunyao
 * @since 2020/10/9 15:33
 */
@Configuration
public class TokenCacheConfiguration {

    private Logger logger = LogManager.getLogger(TokenCacheConfiguration.class);

    @Autowired
    private StringRedisTemplate template;

    @Bean("tokenCacheDao")
    public TokenCacheDao tokenCacheDao() {
        if ("redis".equalsIgnoreCase(Config.SESSION_STORE_TYPE)) {
            logger.info("创建 TokenCacheDaoRedisImpl");
            return new TokenCacheDaoRedisImpl(template);
        } else {
            logger.info("创建 TokenCacheDaoHeapImpl");
            return new TokenCacheDaoHeapImpl();
        }
    }

}
