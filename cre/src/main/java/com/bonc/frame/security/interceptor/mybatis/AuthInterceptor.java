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

package com.bonc.frame.security.interceptor.mybatis;

import com.bonc.frame.applicationrunner.PermissibleDataCache;
import com.bonc.frame.security.ResourceType;
import com.bonc.frame.security.aop.PermissibleData;
import com.bonc.frame.service.auth.AuthorityService;
import com.bonc.frame.service.auth.SubjectService;
import com.bonc.frame.util.ControllerUtil;
import com.bonc.frame.util.ReflectUtil;
import com.bonc.frame.util.SpringUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.page.PageMethod;
import com.google.common.base.Joiner;
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
public class AuthInterceptor implements Interceptor {

    private Log log = LogFactory.getLog(AuthInterceptor.class);

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
        SubjectService subjectService = (SubjectService) SpringUtils.getBean(SubjectService.class);
        AuthorityService authorityService = (AuthorityService) SpringUtils.getBean(AuthorityService.class);
        PermissibleDataCache permissibleDataCache = (PermissibleDataCache) SpringUtils.getBean(PermissibleDataCache.class);

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

        final PermissibleData annotation = findAnnotation(ms, PermissibleData.class, permissibleDataCache);
        if (annotation == null) {
            if (log.isTraceEnabled()) {
                log.trace("没有找到注解[PermissibleData]，不进行数据权限过滤");
            }
            return invocation.proceed();
        }

        final String key = annotation.value();
        final String requiresPermission = annotation.requiresPermission();
        final ResourceType resourceType = annotation.resourceType();
        final boolean isPageHelper = annotation.isPageHelper();

        Page oldPage = null;

        if (isPageHelper) {
            oldPage = PageHelper.getLocalPage();
            PageHelper.clearPage();
        }

        final String currentUser = ControllerUtil.getLoginUserId();

        log.info("开始进行数据权限过滤，拦截方法: " + ms.getId() +
                ", 过滤字段：" + key +
                "，资源类型：" + resourceType +
                "，资源表达式：" + requiresPermission +
                "，当前用户：" + currentUser);

        // 根据用户获取对应的多个主体（用户本身、用户的角色）
        List<String> subjects = subjectService.selectSubjectsByCurrentUser();

        final String resourceTypeId = resourceType.getType();
        if (authorityService.hasAllPermitsBySubjects(subjects, resourceTypeId)) {
            // 全部权限
            // 某一类型全部权限
            if (log.isDebugEnabled()) {
                log.debug("当前用户拥有全部权限或拥有资源类型[" + resourceType + "]的全部权限");
            }
        } else if (authorityService.hasAllPermitsBySubjectsResourceTypeExpression(subjects,
                resourceTypeId, requiresPermission)) {
            // 某一类型全部可读
            if (log.isDebugEnabled()) {
                log.debug("当前用户拥有资源类型[" + resourceType + "]全部可读权限");
            }
        } else {
            // 某一类型部分数据可读
            // 数据id列表
            String subjectsSqlList = Joiner.on("', '").join(subjects);
            StringBuilder inSqlBuilder = new StringBuilder("SELECT DISTINCT(A .RESOURCE_ID) FROM CRE_AUTH_AUTHORITY A " +
                    "WHERE A .SUBJECT_ID IN ('").append(subjectsSqlList).append("') AND A .RESOURCE_TYPE_ID = '")
                    .append(resourceTypeId).append("' AND A .RESOURCE_ID != '*'");

            String sql = boundSql.getSql();
            String inSql = inSqlBuilder.toString();
            sql = "select AUTH_FILTER.* from (" + sql + ") AUTH_FILTER where AUTH_FILTER."
                    + key + " in (" + inSql + ")";

            /*if (log.isDebugEnabled()) {
                log.debug("最终数据权限过滤sql：" + sql);
            }*/

            ReflectUtil.setField(boundSql, "sql", sql);
        }

        if (isPageHelper) {
            Method method = ReflectionUtils.findMethod(PageMethod.class, "setLocalPage", Page.class);
            ReflectionUtils.makeAccessible(method);
            ReflectionUtils.invokeMethod(method, null, oldPage);
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

    private <T extends Annotation> T findAnnotation(MappedStatement mappedStatement, Class<T> targetAnnotation, PermissibleDataCache permissibleDataCache) {
        final String namespace = mappedStatement.getId();
        if (log.isTraceEnabled()) {
            log.trace("即将拦截的方法：" + namespace);
        }
        final int dotIndex = namespace.lastIndexOf(".");
        final String className = namespace.substring(0, dotIndex);
        final String methodName = namespace.substring(dotIndex + 1);
        Method m = permissibleDataCache.get(className, methodName);
        if (m != null) {
            return m.getAnnotation(targetAnnotation);
        }
        return null;
    }

}
