/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2017 abel533@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.bonc.frame.security.offer;

import com.bonc.frame.security.cache.token.TokenCacheService;
import com.bonc.frame.util.*;
import com.bonc.frame.util.jwt.JwtUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.page.PageMethod;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * AuthInterceptor
 *
 * @version 1.0.0
 */
@Intercepts(
        {
                @Signature(type = Executor.class, method = "query",
                        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
                @Signature(type = Executor.class, method = "query",
                        args = {MappedStatement.class, Object.class, RowBounds.class,
                                ResultHandler.class, CacheKey.class, BoundSql.class}),
        }
)
public class AuthInterceptorOffer implements Interceptor {

    private Log log = LogFactory.getLog(AuthInterceptorOffer.class);

    /**
     * ???????????????
     *
     * @param invocation
     * @return
     * @throws Throwable
     * @history
     */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        TokenCacheService tokenCacheService = (TokenCacheService) SpringUtils.getBean(TokenCacheService.class);

        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        Object parameter = args[1];
        RowBounds rowBounds = (RowBounds) args[2];
        ResultHandler resultHandler = (ResultHandler) args[3];
        Executor executor = (Executor) invocation.getTarget();
        CacheKey cacheKey;
        BoundSql boundSql;
        //???????????????????????????????????????
        if (args.length == 4) {
            //4 ????????????
            boundSql = ms.getBoundSql(parameter);
            cacheKey = executor.createCacheKey(ms, parameter, rowBounds, boundSql);
        } else {
            //6 ????????????
            cacheKey = (CacheKey) args[4];
            boundSql = (BoundSql) args[5];
        }

        final PermissibleDataOffer annotation = findAnnotation(ms, PermissibleDataOffer.class);
        if (annotation == null) {
            if (log.isTraceEnabled()) {
                log.trace("??????????????????[PermissibleDataOffer]??????????????????????????????");
            }
            return invocation.proceed();
        }

        final String key = annotation.value();
        final boolean isPageHelper = annotation.isPageHelper();


        String token = ControllerUtil.getToken();
        log.info("?????????????????????????????????????????????: " + ms.getId() +
                ", ???????????????" + key +
                "???token???" + token);

        // ????????????????????????id??????
        List<String> resourceIds = Collections.emptyList();
        if (StringUtils.isNotBlank(token)) {
            log.debug("????????????token: " + token);
            if (JwtUtil.isVerify(token)) {
                String kpiStr = tokenCacheService.get(token);
                if (StringUtils.isNotBlank(kpiStr)) {
                    resourceIds = Splitter.on(",").splitToList(kpiStr);
                }

                if (CollectionUtil.isEmpty(resourceIds)) {
                    // FIXME: ???????????????????????????token?????????????????????????????????token???????????????????????????????????????
                    log.warn("??????????????????????????????????????????");
                } else {
                    Page oldPage = null;
                    if (isPageHelper) {
                        oldPage = PageHelper.getLocalPage();
                        PageHelper.clearPage();
                    }

                    String sql = boundSql.getSql();
                    String inSql = "''";
                    inSql = Joiner.on("','").join(resourceIds);

                    // FIXME: ?? ????????????in?????? ???????????? ??
                    sql = "select AUTH_FILTER.* from (" + sql + ") AUTH_FILTER where AUTH_FILTER."
                            + key + " in ('" + inSql + "')";

                    if (log.isDebugEnabled()) {
                        log.debug("????????????????????????sql???" + sql);
                    }

                    ReflectUtil.setField(boundSql, "sql", sql);
                    if (isPageHelper) {
                        Method method = ReflectionUtils.findMethod(PageMethod.class, "setLocalPage", Page.class);
                        ReflectionUtils.makeAccessible(method);
                        ReflectionUtils.invokeMethod(method, null, oldPage);
                    }
                }
            } else {
                throw new RuntimeException("token????????????????????????");
            }
        }

        return executor.query(ms, parameter, rowBounds, resultHandler, cacheKey, boundSql);
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }

    private <T extends Annotation> T findAnnotation(MappedStatement mappedStatement, Class<T> targetAnnotation) {
        final String namespace = mappedStatement.getId();
        if (log.isTraceEnabled()) {
            log.trace("????????????????????????" + namespace);
        }
        final int dotIndex = namespace.lastIndexOf(".");
        final String className = namespace.substring(0, dotIndex);
        final String methodName = namespace.substring(dotIndex + 1);
        Method m = null;
        try {
            m = ReflectUtil.findMethod(className, methodName);
        } catch (ReflectException e) {
            return null;
        }

        if (m != null) {
            return m.getAnnotation(targetAnnotation);
        }
        return null;
    }

}
