package com.bonc.frame.security.aop;

import com.bonc.frame.config.ConstantFinal;
import com.bonc.frame.entity.auth.Authority;
import com.bonc.frame.security.MyAuthorizationException;
import com.bonc.frame.security.MyRedirectAuthorizationException;
import com.bonc.frame.security.ResourceType;
import com.bonc.frame.service.auth.AuthorityService;
import com.bonc.frame.service.auth.SubjectService;
import com.bonc.frame.util.ControllerUtil;
import com.bonc.frame.util.ReflectException;
import com.bonc.frame.util.ReflectUtil;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author yedunyao
 * @date 2019/7/28 13:50
 */
@Aspect
@Component
public class PermissionsRequiresAop {

    private Log log = LogFactory.getLog(PermissionsRequiresAop.class);

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private AuthorityService authorityService;

    @Before("@annotation(com.bonc.frame.security.aop.PermissionsRequires)")
    public void checkPermission(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        final Method method = methodSignature.getMethod();

        // 获取注解
        final PermissionsRequires annotation = method.getAnnotation(PermissionsRequires.class);
        if (annotation == null) {
            /**
             * 如果注解是在service层的impl类的方法上，因为cglib的原因导致无法找到注解。
             * 本项目中{@link PermissionsRequires}注解目前只应用在controller层。
             * 如果有需要，可以将{@link com.bonc.frame.Application}上的
             * {@link org.springframework.context.annotation.EnableAspectJAutoProxy}
             * 的{@link proxyTargetClass}属性设为{@code true},如：
             * <p>
             *     @EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = false)
             * </p>
             *
             */
            log.error("鉴权失败：在方法[" + method.getName() + "]上没有找到注解[PermissionsRequires]");
            throw new IllegalStateException("鉴权失败：在方法[" + method.getName() + "]上没有找到注解[PermissionsRequires]");
        }

        // 获取注解属性
        final String requiresPermission = annotation.value();
        final ResourceType resourceType = annotation.resourceType();

        if (StringUtils.isBlank(requiresPermission)) {
            if (log.isWarnEnabled()) {
                log.warn("请求需要的权限为空");
            }
            return;
        }

        // 方法的参数名列表
        final String[] parameterNames = methodSignature.getParameterNames();
        // 方法的参数值
        final Object[] args = joinPoint.getArgs();

        if (log.isDebugEnabled()) {
            log.debug("拦截方法[" + method.getName() + "]，" +
                    "参数名列表" + Arrays.toString(parameterNames) + "，" +
                    "参数值列表" + Arrays.toString(args) + "，" +
                    "请求需要的权限表达式[" + requiresPermission + "]，" +
                    "访问资源类型[" + resourceType + "]");
        }

        // 转化表达式
        final Authority authority = parse2Permission(requiresPermission, parameterNames, args);

        HttpSession session = ControllerUtil.getCurrentSession();

        String currentUser = (String) session.getAttribute(ConstantFinal.SESSION_KEY);
        if (StringUtils.isBlank(currentUser)) {
            String token = ControllerUtil.getToken();
            if (StringUtils.isBlank(token)) {
                throw new RuntimeException("当前系统未登录且没有有效的token信息");
            }
            currentUser = ControllerUtil.getUserFromToken(token);
        }
        if (StringUtils.isBlank(currentUser)) {
           throw new RuntimeException("当前系统未登录且没有有效的token信息");
        }


        log.info("开始校验权限，请求需要的权限：" + authority.getResourceExpression() + "，" +
                "访问资源类型：" + resourceType + "，" +
                "访问资源id：" + authority.getResourceId() + "，" +
                "当前用户：" + currentUser);

        // 根据用户获取对应的多个主体（用户本身、用户的角色）
        List<String> subjects = subjectService.selectSubjectsByCurrentUser();
        log.debug("-------------subjects : "+subjects);

        // 根据主体获取权限
        Set<String> holdingPermits = authorityService.getDistinctPermitsBySubjectIdsResourceId(subjects,
                authority.getResourceId(), resourceType.getType());

        if (log.isDebugEnabled()) {
            log.debug("用户主体列表" + subjects + "，" +
                    "用户拥有的权限列表" + holdingPermits);
        }

        // 鉴权
        checkPermissions(authority.getResourceExpression(), holdingPermits, resourceType);
    }

    private void checkPermissions(String neededPermit, Set<String> holdingPermits, ResourceType resourceType) {
        if (holdingPermits == null ||
                holdingPermits.isEmpty()) {
            log.warn("拒绝访问资源: " + neededPermit);
            throwAutorizationException(resourceType);
        }
        if (!holdingPermits.contains("*") &&
                !holdingPermits.contains(neededPermit)) {
            log.warn("拒绝访问资源: " + neededPermit);
            throwAutorizationException(resourceType);
        }

    }

    private void throwAutorizationException(ResourceType resourceType) {
        if (ResourceType.MENU.equals(resourceType)) {
            throw new MyRedirectAuthorizationException("没有权限");
        } else {
            throw new MyAuthorizationException("没有权限");
        }
    }

    private String[] parsePermissions(String[] requiresPermissions, String[] parameterNames, Object[] args) {
        final String[] newRequiresPermissions = new String[requiresPermissions.length];
        for (int i = 0; i < requiresPermissions.length; i++) {
            String requiresPermission = requiresPermissions[i];
            newRequiresPermissions[i] = parsePermission(requiresPermission, parameterNames, args);
        }
        return newRequiresPermissions;
    }

    /**
     * 转化权限表达式，遵循url格式
     * <p>
     * 原理：获取注解的权限表达式和注解所在方法的参数和请求值，转化成可带有数据id的url
     * <p>
     * 支持功能力度，如菜单、按钮，权限表达式为无参数url，如：/user/add；
     * 支持数据力度，权限表达式为带参数url，
     * 参数格式分为：
     * 1.基本类型、String，如：
     * /rule/insert?ruleId 转化为 /rule/insert?0001, ruleId为注解所在方法的参数，0001为对应参数请求值
     * 2.对象类型，如：
     * /column/insertColumn?column.tableId 转化为 /column/insertColumn?0001, column为对象，tableId是对象的属性
     *
     * @param requiresPermission 需要访问的权限
     * @param parameterNames     方法参数名称列表
     * @param args               方法参数值（只允许一个，可以唯一确定资源的值）
     * @return
     */
    private String parsePermission(String requiresPermission, String[] parameterNames, Object[] args) {
        if (requiresPermission.contains("?")) {     // 含有参数，表示数据级的控制
            List<String> list = Splitter.on("?").splitToList(requiresPermission);
            final String params = list.get(1);
            final List<String> paramList = Splitter.on("&").splitToList(params);
            List<String> valueList = new ArrayList<>(paramList.size());
            for (String param : paramList) {
                if (param.contains(".")) {  // 参数为对象类型
                    log.debug("解析对象类型参数：" + param);
                    final List<String> valueExpList = Splitter.on(".").splitToList(param);
                    final String objName = valueExpList.get(0);
                    final String fieldName = valueExpList.get(1);
                    Object o = getRequestValue(parameterNames, args, objName);
                    final String valueStr;
                    try {
                        valueStr = ReflectUtil.getFieldStringValue(o, fieldName);
                    } catch (ReflectException e) {
                        throw new IllegalStateException("获取请求值[" + param + "]失败");
                    }
                    valueList.add(valueStr);
                } else {
                    final String requestValue = getRequestValue(parameterNames, args, param).toString();
                    valueList.add(requestValue);
                }
            }
            String valueExp = Joiner.on("&").join(valueList);
            requiresPermission = Joiner.on("?").join(list.get(0), valueExp);
        }
        return requiresPermission;
    }

    private Authority parse2Permission(String requiresPermission, String[] parameterNames, Object[] args) {
        final Authority authority = new Authority();
        if (requiresPermission.contains("?")) {     // 含有参数，表示数据级的控制
            List<String> list = Splitter.on("?").splitToList(requiresPermission);
            final String param = list.get(1);
            String value = null;
            if (param.contains(".")) {  // 参数为对象类型
                log.debug("解析对象类型参数：" + param);
                final List<String> valueExpList = Splitter.on(".").splitToList(param);
                final String objName = valueExpList.get(0);
                final String fieldName = valueExpList.get(1);
                Object o = getRequestValue(parameterNames, args, objName);
                try {
                    value = ReflectUtil.getFieldStringValue(o, fieldName);
                } catch (ReflectException e) {
                    throw new IllegalStateException("获取请求值[" + param + "]失败");
                }
            } else {
                value = getRequestValue(parameterNames, args, param).toString();
            }

            authority.setResourceId(value);
            authority.setResourceExpression(list.get(0));
        } else {
            authority.setResourceExpression(requiresPermission);
        }
        return authority;
    }

    private <T> T getRequestValue(String[] parameterNames, Object[] args, String paramName) {
        final int index = ArrayUtils.indexOf(parameterNames, paramName);
        if (index != -1) {
            return (T) args[index];
        }
        throw new IllegalArgumentException("参数[" + paramName + "]找不到");
    }

}
