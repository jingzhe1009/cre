package com.bonc.frame.applicationrunner;

import com.bonc.frame.security.aop.PermissibleData;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

/**
 * 扫描包寻找所有的<link @PermissibleData> 注解的方法， 优化反射性能
 *
 * @author yedunyao
 * @since 2020/7/8 10:11
 */
@Component
@Order(1)
public class PermissibleDataLoader implements CommandLineRunner {

    private static final String RESOURCE_PATTERN = "/**/*.class";

    private static final String[] PACKAGES = {"com.bonc.frame.dao"};

    private static Log log = LogFactory.getLog(StaticDataLoader.class);

    @Autowired
    private PermissibleDataCache permissibleDataCache;

    @Override
    public void run(String... args) throws Exception {
        // 加载静态数据
        log.info("加载安全拦截的dao方法");
        scanPackage();
    }

    public void scanPackage() throws Exception {
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

        for (String pkg : PACKAGES) {
            String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                    ClassUtils.convertClassNameToResourcePath(pkg) + RESOURCE_PATTERN;
            Resource[] resources = resourcePatternResolver.getResources(pattern);
            MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
            for (Resource resource : resources) {
                loadClassMethod(readerFactory, resource);
            }
            permissibleDataCache.toImmutable();
        }
    }

    /**
     * 加载资源，判断里面的方法
     *
     * @param metadataReaderFactory spring中用来读取resource为class的工具
     * @param resource              这里的资源就是一个Class
     * @throws IOException
     */
    private void loadClassMethod(MetadataReaderFactory metadataReaderFactory, Resource resource) throws IOException {
        try {
            if (resource.isReadable()) {
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                if (metadataReader != null) {
                    String className = metadataReader.getClassMetadata().getClassName();
                    try {
                        tryCacheMethod(className);
                    } catch (ClassNotFoundException e) {
                        log.error("检查" + className + "是否含有需要信息失败", e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("判断类中的方法实现需要检测xxx失败", e);
        }
    }

    /**
     * 把action下面的所有method遍历一次，标记他们是否需要进行xxx验证 如果需要，放入cache中
     *
     * @param fullClassName
     */
    private void tryCacheMethod(String fullClassName) throws ClassNotFoundException {
        Class<?> clz = Class.forName(fullClassName);
        Method[] methods = clz.getDeclaredMethods();
        Set<Method> set = new HashSet<>();
        for (Method method : methods) {
            if (method.getModifiers() != (Modifier.PUBLIC | Modifier.ABSTRACT)) {
                continue;
            }
            Annotation annotation = method.getAnnotation(PermissibleData.class);
            if (annotation != null) {
                set.add(method);
            }
        }
        if (!set.isEmpty()) {
            permissibleDataCache.put(fullClassName, ImmutableSet.copyOf(set));
        }
    }

}
