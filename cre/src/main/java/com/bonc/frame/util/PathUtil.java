package com.bonc.frame.util;

import com.bonc.framework.rule.RuleEngineFacade;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author yedunyao
 * @date 2018/11/2 10:56
 */
public enum PathUtil {
    ;

    private static ClassLoader getCurrentClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    public static URI getClassLoaderRelativePathURI(String path) throws URISyntaxException {
        ClassLoader contextClassLoader = getCurrentClassLoader();
        return contextClassLoader.getResource(path).toURI();
    }

    public static String getClassLoaderRelativePathString(String path) {
        ClassLoader contextClassLoader = getCurrentClassLoader();
        return contextClassLoader.getResource(path).getPath();
    }


    public static Path getClassLoaderRelativePath(String path) throws URISyntaxException {
        return Paths.get(getClassLoaderRelativePathURI(path));
    }

    public static String getClassLoaderPath(String path) {
        return RuleEngineFacade.class.getClassLoader().getResource(path).getPath();
    }

    public static String getJVMPath() {
        return System.getProperty("user.dir");
    }


    /**
     * @param outer config dir out of jar
     * @return
     */
    public static String getConfigPath(String outer) {
        if (outer == null) {
            throw new IllegalArgumentException();
        } else {
            // 获取jar包外config目录
            File file = new File(getJVMPath() + File.separator + outer);
            String configPath = file.getAbsolutePath();
            if (!file.exists()) {
                return PathUtil.class.getClassLoader().getResource("").getPath();
            }
            return configPath;
        }
    }


}
