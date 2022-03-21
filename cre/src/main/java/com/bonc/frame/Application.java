package com.bonc.frame;

import com.bonc.frame.config.Config;
import com.bonc.frame.module.kafka.v0821.RuleTaskConsumer;
import com.bonc.frame.module.log.deleteLog.DeleteLogTask;
import com.bonc.frame.module.task.offlineTask.ScanNeedStopOfflineTask;
import com.bonc.frame.module.task.offlineTask.ScanOfflineTask;
import com.bonc.frame.util.IdUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.concurrent.TimeUnit;

/**
 * @author 作者：limf
 * @version 版本： 1.0
 * 说明：Hello world!
 * 注解@MapperScan("com.bonc.frame.mapper")作用：添加动态扫描
 * @date 创建时间：2018年1月29日 下午3:25:44
 */
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@EnableScheduling
// Spring Boot核心注解，用于开启自动配置
@SpringBootApplication
@EnableTransactionManagement
//@EnableRetry 注解，启用重试功能
public class Application {

    public static void main(String[] args) {
        System.out.println("应用启动时间：" + IdUtil.CURRENT_TIME_MILLIS);
        SpringApplication.run(Application.class, args);

        Log log = LogFactory.getLog(Application.class);

        log.error("error 级别日志开启");
        log.warn("warn 级别日志开启");
        log.info("info 级别日志开启");
        log.debug("debug 级别日志开启");
        log.trace("trace 级别日志开启");

        if (Config.OFFLINE_TASK_ENABLE || Config.RULE_TASK_CONSUMER_ENABLE) {
            try {
                log.info(String.format("将在%d秒后，启动离线任务处理器",
                        Config.OFFLINE_TASK_SCAN_START_DELAY));
                TimeUnit.SECONDS.sleep(Config.OFFLINE_TASK_SCAN_START_DELAY);
            } catch (InterruptedException e) {
                log.error(e);
                System.exit(-1);
            }
        }

        if (Config.OFFLINE_TASK_ENABLE) {
            log.info("开启定时任务扫描器");
            final Thread scanOfflineTask = new Thread(new ScanOfflineTask(), "ScanOfflineTaskThread");
            scanOfflineTask.setDaemon(true);
            scanOfflineTask.start();

            final Thread scanOfflineTaskStop = new Thread(new ScanNeedStopOfflineTask(), "ScanOfflineTaskStopThread");
            scanOfflineTaskStop.setDaemon(true);
            scanOfflineTaskStop.start();
        }

        if (Config.RULE_TASK_CONSUMER_ENABLE) {
            log.info("开启规则任务处理器");
            final Thread ruleTaskConsumer = new Thread(new RuleTaskConsumer(), "RuleTaskConsumerThread");
            ruleTaskConsumer.setDaemon(true);
            ruleTaskConsumer.start();
        }
        if (Config.CRE_TASK_DELETELOG_DATABASE_ENABLE) {
            log.info("开启定时删除数据库日志");
            new DeleteLogTask().startDeleteLog();
        }
        if (Config.CRE_TASK_DELETE_RULETASKLOG_DATABASE_ENABLE) {
            log.info("开启定时删除数据库日志");
            new DeleteLogTask().startDeleteRuleTaskLog();
        }

    }
}
