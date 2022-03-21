package com.bonc.frame.config;


import com.bonc.frame.util.PathUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class Config {

    private static Log log = LogFactory.getLog(Config.class);

    // 端口号
    public static int SERVER_PORT;

    // 数据源
    public static int DATASOURCE_CACHE_MAX_SIZE;
    public static int DATASOURCE_TABLE_CREATE_CACHE_MAX_SIZE;
    // 定时删除日志任务
    public static boolean CRE_TASK_DELETELOG_DATABASE_ENABLE;
    public static String CRE_TASK_DELETELOG_DATABASE_CRON;
    public static int CRE_TASK_DELETELOG_DATABASE_DAY;
    // 定时删除离线任务的日志
    public static boolean CRE_TASK_DELETE_RULETASKLOG_DATABASE_ENABLE;
    public static String CRE_TASK_DELETE_RULETASKLOG_DATABASE_CRON;
    public static int CRE_TASK_DELETE_RULETASKLOG_DATABASE_DAY;

    // 离线任务
    public static int SERVER_NUM;
    public static int SERVER_INDEX;

    public static String TASK_TOPIC;

    public static boolean OFFLINE_TASK_ENABLE;
    public static int OFFLINE_TASK_SCAN_START_DELAY;
    public static int OFFLINE_TASK_SCAN_INTERVAL;
    public static int OFFLINE_TASK_SCAN_STOP_INTERVAL;
    public static int OFFLINE_TASK_SCHEDULE_POOL_SIZE;

    public static long RULE_TASK_SCAN_MAX_SIZE;
    public static long RULE_TASK_SCAN_PAGE_SIZE;
    public static long RULE_TASK_SCAN_SUCCESS_INTERVAL;
    public static long RULE_TASK_SCAN_FAIL_INTERVAL;

    public static Properties PRODUCER_PROPERTIES;

    // 规则任务
    public static boolean RULE_TASK_CONSUMER_ENABLE;
    public static Properties CONSUMER_PROPERTIES;
    public static int RULE_TASK_CONSUMER_POOL_SIZE;


    // 日志渠道号
    public static String LOG_RULE_TEST_CONSUMERID;
    public static String LOG_RULE_TRAIL_CONSUMERID;
    public static String LOG_RULE_TASK_CONSUMERID;

    public static String CRE_AUTH_SUBJECT_TTL;

    public static String SESSION_STORE_TYPE;

    public static int CRE_TOKEN_TTL;
    public static int CRE_TOKEN_CACHE_MAXSIZE;

    public static String ZOO_KEEPER_CONNECT;
    public static int ZOO_CURATOR_DEFAULT_SESSION_TIMEOUT;
    public static int ZOO_CURATOR_DEFAULT_CONNECTION_TIMEOUT;
    public static boolean LOCK_DISTUPTED_ENABLE;

    public static String CRE_EXPORT_FILEPATH;
    // 引用校验
    public static boolean CRE_REFERENCE_CHECK_ENABLE;

    public static String ABTEST_ZOO_KEEPER_CONNECT;
    public static int ABTEST_ZOO_CURATOR_DEFAULT_SESSION_TIMEOUT;
    public static int ABTEST_ZOO_CURATOR_DEFAULT_CONNECTION_TIMEOUT;
    public static Properties AB_TEST_PRODUCER_PROPERTIES;
    public static Properties AB_TEST_CONSUMER_PROPERTIES;

    public static String CRE_ABTEST_MANAGER_ABTESTMETRIC_TOPIC;
    public static int CRE_ABTEST_MANAGER_ABTESTMETRIC_UPDATE_INTERVAL;

    static {
        Properties properties = new Properties();

        final String configPath = PathUtil.getConfigPath("config") + File.separator + "application.properties";
        // File file = ResourceUtils.getFile("application.properties");
        //   final String configPath =  "classpath:application.properties";
        log.info("加载服务配置文件的目录：" + configPath);

        //  ClassPathResource resource = new ClassPathResource("application.properties");
        try (InputStreamReader input = new InputStreamReader(
                new FileInputStream(configPath), "UTF-8")
        ) {
            properties.load(input);
            SERVER_PORT = Integer.parseInt(properties.getProperty("server.port"));

            DATASOURCE_CACHE_MAX_SIZE = Integer.parseInt(properties.getProperty("cre.datasource.cache.max.size", "20"));
            DATASOURCE_TABLE_CREATE_CACHE_MAX_SIZE = Integer.parseInt(properties.getProperty("cre.datasource.table.create.cache.max.size", "256"));

            // 定时删除数据库中的日志
            CRE_TASK_DELETELOG_DATABASE_ENABLE = Boolean.parseBoolean(properties.getProperty("cre.task.deleteLog.database.enable", ConstantFinal.DIS_ENABLE));
            CRE_TASK_DELETELOG_DATABASE_CRON = properties.getProperty("cre.task.deleteLog.database.cron", ConstantFinal.CRE_TASK_DELETELOG_DATABASE_CRON_DEFAULT);
            CRE_TASK_DELETELOG_DATABASE_DAY = Integer.parseInt(properties.getProperty("cre.task.deleteLog.database.day", ConstantFinal.CRE_TASK_DELETELOG_DATABASE_DAY_DEFAULT));
            // 定时删除离线任务的日志
            CRE_TASK_DELETE_RULETASKLOG_DATABASE_ENABLE = Boolean.parseBoolean(properties.getProperty("cre.task.deleteRuleTaskLog.database.enable", ConstantFinal.DIS_ENABLE));
            CRE_TASK_DELETE_RULETASKLOG_DATABASE_CRON = properties.getProperty("cre.task.deleteRuleTaskLog.database.cron", ConstantFinal.CRE_TASK_DELETELOG_DATABASE_CRON_DEFAULT);
            CRE_TASK_DELETE_RULETASKLOG_DATABASE_DAY = Integer.parseInt(properties.getProperty("cre.task.deleteRuleTaskLog.database.day", ConstantFinal.CRE_TASK_DELETERYLETASKLOG_DATABASE_DAY_DEFAULT));


            SERVER_NUM = Integer.parseInt(properties.getProperty("cre.server.num", "1"));
            SERVER_INDEX = Integer.parseInt(properties.getProperty("cre.server.index", "0"));

            TASK_TOPIC = properties.getProperty("cre.task.topic", "cre_task");

            OFFLINE_TASK_ENABLE = Boolean.parseBoolean(properties.getProperty("cre.task.offlineTask.enable"));
            OFFLINE_TASK_SCAN_START_DELAY = Integer.parseInt(properties.getProperty("cre.task.offlineTask.scan.start.delay"));
            OFFLINE_TASK_SCAN_INTERVAL = Integer.parseInt(properties.getProperty("cre.task.offlineTask.scan.interval"));
            OFFLINE_TASK_SCAN_STOP_INTERVAL = Integer.parseInt(properties.getProperty("cre.task.offlineTask.scan.stop.interval"));
            OFFLINE_TASK_SCHEDULE_POOL_SIZE = Integer.parseInt(properties.getProperty("cre.task.offlineTask.schedule.pool.size"));

            if (OFFLINE_TASK_ENABLE) {
                final String producerConfigPath = PathUtil.getConfigPath("config") + File.separator + "producer.properties";
                log.info("加载生产者配置文件的目录：" + producerConfigPath);
                PRODUCER_PROPERTIES = loadProperties(producerConfigPath);

            }


            RULE_TASK_SCAN_MAX_SIZE = Long.parseLong(properties.getProperty("cre.task.ruleTask.scan.maxSize", String.valueOf(Integer.MAX_VALUE)));
            RULE_TASK_SCAN_PAGE_SIZE = Long.parseLong(properties.getProperty("cre.task.ruleTask.scan.page.size", "1000"));
            RULE_TASK_SCAN_SUCCESS_INTERVAL = Long.parseLong(properties.getProperty("cre.task.ruleTask.scan.success.interval", "30"));
            RULE_TASK_SCAN_FAIL_INTERVAL = Long.parseLong(properties.getProperty("cre.task.ruleTask.scan.fail.interval", "60"));

            RULE_TASK_CONSUMER_ENABLE = Boolean.parseBoolean(properties.getProperty("cre.task.ruleTask.consumer.enable"));
            if (RULE_TASK_CONSUMER_ENABLE) {
                final String consumerConfigPath = PathUtil.getConfigPath("config") + File.separator + "consumer.properties";
                log.info("加载消费者配置文件的目录：" + consumerConfigPath);
                CONSUMER_PROPERTIES = loadProperties(consumerConfigPath);
            }
            RULE_TASK_CONSUMER_POOL_SIZE = Integer.parseInt(properties.getProperty("cre.task.ruleTask.consumer.pool.size", "10"));

            // 日志渠道号
            LOG_RULE_TEST_CONSUMERID = properties.getProperty("cre.log.rule.test.consumerId", "cre_test");
            LOG_RULE_TRAIL_CONSUMERID = properties.getProperty("cre.log.rule.trail.consumerId", "cre_trail");
            LOG_RULE_TASK_CONSUMERID = properties.getProperty("cre.log.rule.task.consumerId", "cre_task");

            CRE_AUTH_SUBJECT_TTL = properties.getProperty("cre.auth.subject.ttl", "3");

            SESSION_STORE_TYPE = properties.getProperty("spring.session.store-type", "none");

            CRE_TOKEN_TTL = Integer.parseInt(properties.getProperty("cre.token.ttl", "1440"));
            CRE_TOKEN_CACHE_MAXSIZE = Integer.parseInt(properties.getProperty("cre.token.cache.maxSize", "100"));

            ZOO_KEEPER_CONNECT = properties.getProperty("zookeeper.connect", "172.16.26.71:2181");
            LOCK_DISTUPTED_ENABLE = Boolean.parseBoolean(properties.getProperty("cre.lock.disrupted.enable", "false"));
            ZOO_CURATOR_DEFAULT_SESSION_TIMEOUT = Integer.parseInt(properties.getProperty("zookeeper.curator.default.session.timeout", "60000"));
            ZOO_CURATOR_DEFAULT_CONNECTION_TIMEOUT = Integer.parseInt(properties.getProperty("zookeeper.curator.default.connection.timeout", "15000"));

            CRE_EXPORT_FILEPATH = properties.getProperty("cre.export.filePath", "classpath:/static/cre/export/v2.0/");
            CRE_REFERENCE_CHECK_ENABLE = Boolean.parseBoolean(properties.getProperty("cre.reference.check.enable", "false"));

            ABTEST_ZOO_KEEPER_CONNECT = properties.getProperty("cre.aBTest.zookeeper.connect", "172.16.26.71:2181");
            ABTEST_ZOO_CURATOR_DEFAULT_SESSION_TIMEOUT = Integer.parseInt(properties.getProperty("cre.aBTest.zookeeper.curator.default.session.timeout", "60000"));
            ABTEST_ZOO_CURATOR_DEFAULT_CONNECTION_TIMEOUT = Integer.parseInt(properties.getProperty("cre.aBTest.zookeeper.curator.default.connection.timeout", "15000"));

            //
            CRE_ABTEST_MANAGER_ABTESTMETRIC_TOPIC = properties.getProperty("cre.aBTest.manager.abTestMetric.topic", "cre-abTestMetric");
            CRE_ABTEST_MANAGER_ABTESTMETRIC_UPDATE_INTERVAL = Integer.parseInt(properties.getProperty("cre.aBTest.manager.abTestMetric.update.interval", "30"));

            final String abTestProducerConfigPath = PathUtil.getConfigPath("config") + File.separator + "abTest-producer.properties";
            log.info("加载A/B测试生产者配置文件的目录：" + abTestProducerConfigPath);
            AB_TEST_PRODUCER_PROPERTIES = loadProperties(abTestProducerConfigPath);
            final String abTestConsumerConfigPath = PathUtil.getConfigPath("config") + File.separator + "abTest-consumer.properties";
            log.info("加载A/B测试消费者配置文件的目录：" + abTestConsumerConfigPath);
            AB_TEST_CONSUMER_PROPERTIES = loadProperties(abTestConsumerConfigPath);


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
