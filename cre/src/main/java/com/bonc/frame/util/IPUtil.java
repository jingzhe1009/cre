package com.bonc.frame.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * @author yedunyao
 * @date 2019/5/7 17:12
 */
public class IPUtil {

    private static final Log LOG = LogFactory.getLog(IPUtil.class);

    /**
     * 获取客户端真实IP地址（注意：如果通过Squid反向代理软件，需更改squid.conf配置文件forwarded_for项默认是为on，
     * 否则获取到的IP为unknown）
     * @param request
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        if(null != ip && ip.indexOf(',') != -1){
            //如果通过了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串 IP 值
            //取X-Forwarded-For中第一个非unknown的有效IP字符串
            //如：X-Forwarded-For：192.168.1.110, 192.168.1.120, 192.168.1.130, 192.168.1.100
            //用户真实IP为： 192.168.1.110
            //注意:当访问地址为 localhost 时 地址格式为 0:0:0:0:0:0:1
            if (LOG.isDebugEnabled()) {
                LOG.debug("ip=" + ip);
            }

            String[] ips = ip.split(",");
            for (int i = 0; i < ips.length; i++) {
                if(null != ips[i] && !"unknown".equalsIgnoreCase(ips[i])){
                    ip = ips[i];
                    break;
                }
            }
            if("0:0:0:0:0:0:1".equals(ip)){
                LOG.warn("由于客户端访问地址使用 localhost，获取客户端真实IP地址错误，请使用IP方式访问");
            }
        }

        if("unknown".equalsIgnoreCase(ip)){
            LOG.warn("由于客户端通过Squid反向代理软件访问，获取客户端真实IP地址错误，请更改squid.conf配置文件forwarded_for项默认是为on解决");
        }
        return ip;
    }

}
