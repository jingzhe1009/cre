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
     * 权限拦截器
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
        //由于逻辑关系，只会进入一次
        if (args.length == 4) {
            //4 个参数时
            boundSql = ms.getBoundSql(parameter);
            cacheKey = executor.createCacheKey(ms, parameter, rowBounds, boundSql);
        } else {
            //6 个参数时
            cacheKey = (CacheKey) args[4];
            boundSql = (BoundSql) args[5];
        }

        final PermissibleDataOffer annotation = findAnnotation(ms, PermissibleDataOffer.class);
        if (annotation == null) {
            if (log.isTraceEnabled()) {
                log.trace("没有找到注解[PermissibleDataOffer]，不进行数据权限过滤");
            }
            return invocation.proceed();
        }

        final String key = annotation.value();
        final boolean isPageHelper = annotation.isPageHelper();


        String token = ControllerUtil.getToken();
        log.info("开始进行数据权限过滤，拦截方法: " + ms.getId() +
                ", 过滤字段：" + key +
                "，token：" + token);

        // 从缓存中获取数据id列表
        List<String> resourceIds = Collections.emptyList();
        if (StringUtils.isNotBlank(token)) {
            log.debug("开始校验token: " + token);
            if (JwtUtil.isVerify(token)) {
                String kpiStr = tokenCacheService.get(token);
                if (StringUtils.isNotBlank(kpiStr)) {
                    resourceIds = Splitter.on(",").splitToList(kpiStr);
                }

                if (CollectionUtil.isEmpty(resourceIds)) {
                    // FIXME: 如果是因为淘汰机制token被淘汰，则需要同时判定token为已过期，要求用户重新登录
                    log.warn("当前用户没有有权限的指标列表");
                } else {
                    Page oldPage = null;
                    if (isPageHelper) {
                        oldPage = PageHelper.getLocalPage();
                        PageHelper.clearPage();
                    }

                    String sql = boundSql.getSql();
                    String inSql = "''";
                    inSql = Joiner.on("','").join(resourceIds);

                    // FIXME: ?? 是否优化in查询 优化方案 ??
                    sql = "select AUTH_FILTER.* from (" + sql + ") AUTH_FILTER where AUTH_FILTER."
                            + key + " in ('" + inSql + "')";

                    if (log.isDebugEnabled()) {
                        log.debug("最终数据权限过滤sql：" + sql);
                    }

                    ReflectUtil.setField(boundSql, "sql", sql);
                    if (isPageHelper) {
                        Method method = ReflectionUtils.findMethod(PageMethod.class, "setLocalPage", Page.class);
                        ReflectionUtils.makeAccessible(method);
                        ReflectionUtils.invokeMethod(method, null, oldPage);
                    }
                }
            } else {
                throw new RuntimeException("token无效，请重新登录");
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
            log.trace("即将拦截的方法：" + namespace);
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
