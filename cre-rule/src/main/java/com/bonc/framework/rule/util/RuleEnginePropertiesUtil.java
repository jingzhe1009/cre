package com.bonc.framework.rule.util;

import com.bonc.framework.rule.RuleEngineFacade;
import com.bonc.framework.rule.constant.RuleEngineConstant;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author qxl
 * @version 1.0
 * @date 2018年3月2日 下午5:05:50
 */
public class RuleEnginePropertiesUtil {
    private static Log log = LogFactory.getLog(RuleEnginePropertiesUtil.class);

    private static Properties prop;

    static {
        init();
    }

    private static void init() {
        final String configFile = "config" + File.separator + RuleEngineConstant.PROPERTIESPATH;
        final String path = PathUtil.getConfigPath(configFile);

        InputStream in = null;
        try {
            if (path == null) {
                in = RuleEngineFacade.class.getClassLoader().getResourceAsStream(RuleEngineConstant.PROPERTIESPATH);
            } else {
                log.info("Rule engine config file: " + path);
                in = new FileInputStream(path);
            }
            prop = new Properties();
            prop.load(in);
        } catch (Exception e) {
            log.error(e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.warn(e);
                }
            }
        }
    }

    /**
     * 获取配置文件中的属性值
     *
     * @param key
     * @return
     */
    public static String getProperty(String key, String defaultVal) {
        if (prop == null) {
            init();
        }
        return prop.getProperty(key, defaultVal);
    }

    public static String getProperty(String key) {
        return getProperty(key, "");
    }

    public static int getInt(String key, String defaultVal) {
        return Integer.valueOf(getProperty(key, defaultVal));
    }

    public static int getInt(String key) {
        return getInt(key, "0");
    }

    public static double getDouble(String key, String defaultVal) {
        return Double.valueOf(getProperty(key, defaultVal));
    }

    public static double getDouble(String key) {
        return getDouble(key, "0");
    }
}
