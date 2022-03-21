package com.bonc.frame.security.cache.token;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author yedunyao
 * @since 2020/10/9 15:07
 */
@Service
public class TokenCacheService {

    @Autowired
    private TokenCacheDao tokenCacheDao;

    public String get(String token) {
        return tokenCacheDao.get(token);
    }

    public void set(String token, String value) {
        tokenCacheDao.set(token, value);
    }

    public void del(String token) {
        tokenCacheDao.del(token);
    }

}
