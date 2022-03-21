package com.bonc.frame.util.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bonc.frame.config.Config;
import com.bonc.frame.config.ConstantFinal;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.*;

public class JwtUtil {

    //生成jwt的秘钥
    private static String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgW2ypO2xnx89zmRXKOENntCl8husj8kD\n" +
            "Yipl5ORYAbbmSIycolkIzPKItgjfpNOjEw0FNOtjAahBAJmJQCxDB6udBIMDQSZmm7OaohJ6iXdi\n" +
            "Keud0gy4XsVEZFR+H/E4gG3A7Z+tjqzWXmQxET3/aZNBnhDN4j2NZFiVO2wMI2IsJOzAIPdYA6E+\n" +
            "LYVlImwnLV9L7BO/ZcgLsBuvEQN/Wa34p9Wj12y+CoI8lxaQYplWqsaSHS5zZhBjwt2Yljaz2ayw\n" +
            "jiNjUxPU23se7V+5GgYfYt2gu97M/FybOZuRaTF3jP0/WJfNb8Ic4NUmPoWZd+FXhy4dOV3Y62JM\n" +
            "XdyXqwIDAQAB\n";

    private static String PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCBbbKk7bGfHz3OZFco4Q2e0KXy\n" +
            "G6yPyQNiKmXk5FgBtuZIjJyiWQjM8oi2CN+k06MTDQU062MBqEEAmYlALEMHq50EgwNBJmabs5qi\n" +
            "EnqJd2Ip653SDLhexURkVH4f8TiAbcDtn62OrNZeZDERPf9pk0GeEM3iPY1kWJU7bAwjYiwk7MAg\n" +
            "91gDoT4thWUibCctX0vsE79lyAuwG68RA39Zrfin1aPXbL4KgjyXFpBimVaqxpIdLnNmEGPC3ZiW\n" +
            "NrPZrLCOI2NTE9Tbex7tX7kaBh9i3aC73sz8XJs5m5FpMXeM/T9Yl81vwhzg1SY+hZl34VeHLh05\n" +
            "XdjrYkxd3JerAgMBAAECggEAAtP6y0/GScvM9e2LWgFQ4t98QMYKBzpFMKfsggvbQG5Wqc8ovQNl\n" +
            "BdVm11NqT5ewQIJQUs+x4h9jDDhOn3BYJprBW4/hSlMketJ51oDA3LAXHQNg923/uVJmJCsUdm42\n" +
            "5deAF7EdgbiVForp546LPitRi7qiRQdDR6YH3LyHp149pDcN0LSYECcz2n+TASoxwzgugvCfJA1w\n" +
            "55D67wEkPMR21iYcBqHtcNmvYtJVht0VdhIyLx4Wx/FnMDD6MQkfGi748DIRBubo9/49qb9VFQZd\n" +
            "MitCA0TVGEnhsj4mNCsdXsS6TJjfelR/+q7M5xauILoJO9nayP//gt5/IoEZQQKBgQDiGRl6h7EP\n" +
            "xik25mEB6FA83FHi2MCrWOGYpw8GTn442LWLkIASYRttJGsQGrQuGx1m9yCx9Mc5HNGv3F4Ww95Z\n" +
            "LvROElbGRiz6LypcDtka1ijCRwj4Ybt2tJdwkile9QJ+ObpnmupQmI4XDDqUdLePr7wOZPzH9K1p\n" +
            "cDJbMWkNLwKBgQCSi7Nx+hhhIm1NDVKZcPTHBlYKIFnbcDFcsArIOe0ZHLu2/iMfoZaqgLRzMEcl\n" +
            "/4JQY56t3fxXPcVLNo7QHvteXdXcMlLUp18C6fQ4JSQlWiGdXveqC69trX67yzpXOU+XvLfs9OMb\n" +
            "BEHpX4xXh2BptYYvxK4AYMuEa8/DOrYWRQKBgCyElKezoAFjGypcIi2l+GaDWvw3gXg7f+/2tNah\n" +
            "DelTgiuAwDbeN/A9g3cgSunpHNjxP9bKQ4/TRNZDpaMF3fnhbOkSY1OW7bQ1JToLEmReVpgqNHHz\n" +
            "jF3LISZhIRQ5WcxQwYs0zX4hC5kuJzqj4KDCEV0kNUGJw7tHBCNAj1axAoGAPbBZRa2F/b3ETR9C\n" +
            "mh0WrYFZTsH3kYxU+zOQFmFsEZoq2wl4N6SKDBRJ4gN8r94zOSv2bDJ6Oy9hkhvRWoLwRfUyRZNZ\n" +
            "kvMx58QcblYUNTUP2XNUxlxM6NqT8vHGCVmyy6lBs5oS0BimtlrmdSIjz9v+NLWSmc4lXgUUXJam\n" +
            "eBECgYEAuJBzDwyh8C/XDblmGK6SPD9K2Mbl0cuK67nEpGoQaUtZuO9lwQHEJ7WSHDdxKhFSfqun\n" +
            "qDjT3aMTPvivSoc/FIt0s8Se/n+6QJ3fUKt1jsWBhRn3tUYTm6edaZRAmuaggLU3SmTfZu6tMGCJ\n" +
            "xvnLZUbqKp8qS+A2XbqQPLmyyyA=";


    //jwt 签发者
    private static final String SIGN_PERSON = "bonc-cre";

    private static final int MINUTE = 60 * 1000;

    //jwt 过期时间 单位分钟
    private static final int MIN_TIME_OUT = Config.CRE_TOKEN_TTL;

    private static Log log = LogFactory.getLog(JwtUtil.class);

    public static String createJWT(String user) throws Exception {
        //指定签名的时候使用的签名算法，也就是header那部分，jjwt已经将这部分内容封装好了。
        Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) RSAUtil.string2PublicKey(PUBLIC_KEY),
                (RSAPrivateKey) RSAUtil.string2PrivateKey(PRIVATE_KEY));
        //生成JWT的时间
        long nowMillis = System.currentTimeMillis();
        long ttlMillis = MIN_TIME_OUT * MINUTE;

        //生成签发人
        String subject = SIGN_PERSON;
        long expMillis = nowMillis + ttlMillis;
        Date exp = new Date(expMillis);

        final Map<String, String> payloadClaims = ImmutableMap.of(
                "user", user
        );

        return createJwtToken(subject, payloadClaims, exp, algorithm);
    }

    public static String createJWT(String user, String realUser) throws Exception {
        //指定签名的时候使用的签名算法，也就是header那部分，jjwt已经将这部分内容封装好了。
        Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) RSAUtil.string2PublicKey(PUBLIC_KEY),
                (RSAPrivateKey) RSAUtil.string2PrivateKey(PRIVATE_KEY));
        //生成JWT的时间
        long nowMillis = System.currentTimeMillis();
        long ttlMillis = MIN_TIME_OUT * MINUTE;

        //生成签发人
        final String subject = SIGN_PERSON;
        final long expMillis = nowMillis + ttlMillis;
        final Date exp = new Date(expMillis);

        final Map<String, String> payloadClaims = ImmutableMap.of(
                "user", user,
                ConstantFinal.REAL_USER, realUser
        );

        return createJwtToken(subject, payloadClaims, exp, algorithm);
    }

    private static String createJwtToken(String issuer, Map<String, String> payloadClaims, Date expiresAt, Algorithm algorithm) {
        JWTCreator.Builder jwtBuilder = JWT.create()
                .withJWTId(UUID.randomUUID().toString())
                .withIssuer(issuer);

        if (payloadClaims != null) {
            Set<Map.Entry<String, String>> payloadClaimSet = payloadClaims.entrySet();
            for (Map.Entry<String, String> payloadClaimEntry : payloadClaimSet) {
                String key = payloadClaimEntry.getKey();
                String value = payloadClaimEntry.getValue();
                jwtBuilder.withClaim(key, value);
            }
        }

        return jwtBuilder
                .withExpiresAt(expiresAt)
                .sign(algorithm);
    }

    /**
     * Token的解密
     *
     * @param token 加密后的token
     * @return
     * @throws Exception
     * @throws IllegalArgumentException
     */
    public static Map<String, String> parseJWT(String token) throws Exception {
        Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) RSAUtil.string2PublicKey(PUBLIC_KEY), null);
        JWTVerifier verifier = JWT.require(algorithm).acceptExpiresAt(5).build();
        DecodedJWT jwt = verifier.verify(token);
        Map<String, Claim> claims = jwt.getClaims();
        if (claims == null) {
            return Collections.emptyMap();
        }
        Map<String, String> payloadClaims = new HashMap<>(claims.size());
        for (String claimKey : claims.keySet()) {
            payloadClaims.put(claimKey, claims.get(claimKey).asString());
        }
        return payloadClaims;
    }


    /**
     * 校验token
     * 主要校验解密及token超时时长
     *
     * @param token
     * @return
     */
    public static Boolean isVerify(String token) {
        try {
            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) RSAUtil.string2PublicKey(PUBLIC_KEY), null);
            JWTVerifier verifier = JWT.require(algorithm).acceptExpiresAt(5).build();
            DecodedJWT jwt = verifier.verify(token);
            return true;
        } catch (Exception e) {

        }
        return false;
    }

    public static void main(String[] args) throws Exception {
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1ODkwMDM1MTQsImlzcyI6ImJvbmMtY3JlIiwianRpIjoiZGU5YThlYzctNGVkNy00M2Q1LWIwNmEtNTQwZWU2NTRmYWU5IiwicmVhbFVzZXIiOiIxMjMiLCJ1c2VyIjoiYWRtaW4ifQ.SF7bPel2bWZEKXgXBi7AS3WLysAPThUyf8G_SDRmaam_8eU7Hrqjkp80MSebPwF-r-JYz7NYgm_6FaOWFvEG-8QQ_2nUagufAMqWYGlyQXmJ9uk7oVgIGlzdceEBUPCgiDkX871idlMPHcaG0zgQgeYw5f3XQYmcQFo-p8PJYObnbD5Lnd6JNgg-AjX5sUV7LAnLmHaXraWO8FVnXtMHsRFxgg2cO_fnf2FjuJ6kSo4m7IqM283E3jDfUDEmGhYIEr5YBb_-WjTck5mJFVp5nolw1CCzzTifFEr4Hlglbptj1to1UxFNoeE1pgG-3caBncKtuZMLnc3bFePIoOMh-g";

//        String admin = createJWT("admin", "123");
//        System.out.println(admin);

        // System.out.println(isVerify(str));
        // parseJWT(str);
//        String token = createJWT("root");
//        System.out.println("token: " + token);
//        System.out.println("isVerify(token): " + isVerify(token));

        System.out.println(isVerify(token));
        Map<String, String> stringStringMap = parseJWT(token);
        System.out.println(stringStringMap);
    }


}