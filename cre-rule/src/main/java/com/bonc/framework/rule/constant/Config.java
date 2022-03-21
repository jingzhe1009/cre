package com.bonc.framework.rule.constant;


import com.bonc.framework.rule.util.PathUtil;
import com.google.common.base.Splitter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Config {

    private static Log log = LogFactory.getLog(Config.class);

    public static String CRE_LOG_RULE_NONPERSIST;
    public static List<String> CRE_LOG_RULE_NONPERSIST_LIST;

    static {
        Properties properties = new Properties();

        final String configPath = PathUtil.getConfigPath2("config") + File.separator + "application.properties";
        log.info("加载服务配置文件的目录：" + configPath);

        try (InputStreamReader input = new InputStreamReader(
                new FileInputStream(configPath), "UTF-8")
        ) {
            properties.load(input);

            CRE_LOG_RULE_NONPERSIST = properties.getProperty("cre.log.rule.nonPersist", "");
            CRE_LOG_RULE_NONPERSIST_LIST = new ArrayList<>(5);
            List<String> list = Splitter.on(",").splitToList(CRE_LOG_RULE_NONPERSIST);
            CRE_LOG_RULE_NONPERSIST_LIST.addAll(list);

        } catch (Exception e) {
            log.error(e);
            System.exit(0);
        }
    }

    private static Properties loadProperties(String configPath) {
        Properties properties = new Properties();
        try (InputStreamReader input = new InputStreamReader(
                new FileInputStream(configPath), "UTF-8")
        ) {
            properties.load(input);
        } catch (Exception e) {
            log.error(e);
            System.exit(0);
        }
        return properties;
    }
}
