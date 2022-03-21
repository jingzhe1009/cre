package com.bonc.frame.util.os;

import com.bonc.frame.config.Config;
import com.bonc.frame.util.SpringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.env.Environment;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.util.UUID;

/**
 * @Description 获取系统、进程相关信息
 * @Author yedunyao
 * @date 2018/7/4 19:45
 */
public class SystemTool {

    private static Logger logger = LogManager.getLogger(SystemTool.class);

    private SystemTool() {
        throw new AssertionError("It does not support a instance.");
    }

    public static void main(String[] args) throws Exception {
        System.out.println(getCurrentProcessName());
        System.out.println(getCurrentProcessPid());
        System.out.println(getLocalHostLANAddress().getHostAddress());
        System.out.println(getLocalHostLANAddress().getHostName());
    }

    /**
     * 获取当前运行的jvm名称，
     * 不同jvm返回的不同
     * 一般为 pid@hotname
     */
    public static String getCurrentProcessName() {
        return ManagementFactory.getRuntimeMXBean().getName();
    }

    /**
     * 获取当前jvm pid
     */
    public static String getCurrentProcessPid() {
        // get pid
        return getCurrentProcessName().split("@")[0];
    }

    /**
     * 获取当前进程的ip:port
     */
    public static String getCurrentProcessHost() {
        int port = Config.SERVER_PORT;
        String ip = "";

        try {
            ip = getLocalHostLANAddress().getHostAddress();
        } catch (Exception e) {
            logger.error("获取本机ip失败", e);
            ip = UUID.randomUUID().toString();
        }

        try {
            Environment env = (Environment) SpringUtils.getBean(Environment.class);

            if (env != null) {
                port = Integer.parseInt(env.getProperty("server.port"));
                logger.debug("当前应用端口号: [{}]", Config.SERVER_PORT);
            }
        } catch (Exception e) {
            logger.error(e);
            port = Config.SERVER_PORT;
        }
        return ip + ":" + port;
    }

    /**
     * 获取本机的主机名、ip等信息
     *
     * @return InetAddress
     * @throws Exception
     */
    public static InetAddress getLocalHostLANAddress() throws Exception {
        InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
        return jdkSuppliedAddress;

    }


}
