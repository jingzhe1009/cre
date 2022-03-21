package com.bonc.frame.module.service;

import com.bonc.frame.module.aBTest.metric.CpuMetric;
import com.bonc.frame.security.zk.CREInterProcessMutex;
import com.bonc.frame.util.BeanUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.nodes.GroupMember;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yedunyao
 * @since 2020/9/15 16:45
 */
public class ServiceManager {

    public static final String MEMBERSHIP_PATH = "/cre/service";
    public static final String WORKER_SUFFIX = "-worker";

    private Logger logger = LogManager.getLogger(CREInterProcessMutex.class);
    private CuratorFramework client;
    private String membershipPath;
    private String serviceId;
    private GroupMember groupMember;

    public ServiceManager(CuratorFramework client, String membershipPath, String serviceId, CpuMetric metric) throws Exception {
//        serviceId = SystemTool.getCurrentProcessHost();
        this.client = client;
        this.membershipPath = membershipPath;
        this.serviceId = serviceId;
        byte[] data = BeanUtils.bean2Byte(metric);
        this.groupMember = new GroupMember(client, membershipPath, serviceId, data);
    }

    // 注册服务
    public void register() {
        groupMember.start();

    }

    public Map<String, CpuMetric> getCurrentMembers() {
        Map<String, byte[]> currentMembers = groupMember.getCurrentMembers();
        HashMap<String, CpuMetric> result = new HashMap<>(currentMembers.size());
        for (Map.Entry<String, byte[]> entry : currentMembers.entrySet()) {
            String key = entry.getKey();
            if (serviceId.equals(key)) {
                continue;
            }
            if (!key.endsWith(WORKER_SUFFIX)) {
                continue;
            }
            byte[] value = entry.getValue();
            CpuMetric cpuMetric = null;
            try {
                cpuMetric = (CpuMetric) BeanUtils.byte2Obj(value);
            } catch (Exception e) {
                logger.warn(e);
                cpuMetric = CpuMetric.OPTIMISTIC_METRIC;
            }
            result.put(key, cpuMetric);
        }
        return result;
    }

    public void set(CpuMetric cpuMetric) throws Exception {
        byte[] bytes = BeanUtils.bean2Byte(cpuMetric);
        groupMember.setThisData(bytes);
    }

    public String getServiceId() {
        return serviceId;
    }

    public void close() {
        groupMember.close();
    }

}
