package com.bonc.frame.security.zk;

import com.bonc.frame.config.Config;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.CuratorConnectionLossException;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.ArrayList;
import java.util.List;

public class CREInterProcessMutex {
    private Log log = LogFactory.getLog(CREInterProcessMutex.class);
    private List<InterProcessMutex> interProcessMutexes;
    private CuratorFramework client;
    private String clientName;

    private static boolean isNeedLock = Config.LOCK_DISTUPTED_ENABLE;

    public CREInterProcessMutex(Object lockDataType, Object lockPro) throws Exception {
        if (isNeedLock) {
            client = CuratorFrameworkFactory.newClient(Config.ZOO_KEEPER_CONNECT,
                    Config.ZOO_CURATOR_DEFAULT_SESSION_TIMEOUT,Config.ZOO_CURATOR_DEFAULT_CONNECTION_TIMEOUT,
                    new ExponentialBackoffRetry(1000, 3));
            try {
                client.start();
            } catch (Exception e) {
               throw e;
            }
            this.clientName = lockDataType.toString();
            interProcessMutexes = new ArrayList<>();
            final String[] lockProperty = (String[]) lockPro;
            for (String pro : lockProperty) {
                interProcessMutexes.add(new InterProcessMutex(client, "/cre/" + lockDataType + "/" + pro));
            }
        }
    }

    public void getLock() throws Exception {
        if (isNeedLock) {
            for (InterProcessMutex interProcessMutex : interProcessMutexes) {
                interProcessMutex.acquire();
            }
            log.info(clientName + " get the lock");
        }
    }

    public void releaseLock() throws Exception {
        if (isNeedLock) {
            log.info(clientName + " releasing the lock");
            for (InterProcessMutex interProcessMutex : interProcessMutexes) {
                interProcessMutex.release();
            }
        }
    }

    public void stop() {
        if (isNeedLock) {
            client.close();
        }
    }
}
