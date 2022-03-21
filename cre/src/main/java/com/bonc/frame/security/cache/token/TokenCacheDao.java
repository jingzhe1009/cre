package com.bonc.frame.security.cache.token;

/**
 * @author yedunyao
 * @since 2020/10/9 15:22
 */
public interface TokenCacheDao {

    String get(String token);

    void set(String token, String value);

    void del(String token);

}
