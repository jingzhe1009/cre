package com.bonc.frame.module.db.dbcp;

/**
 * @author qxl
 * @version 1.0
 * @date 2017年7月19日 上午9:20:03
 */
public class DbPoolFactory {

    private static volatile IDbPool dbPool;
    private static volatile HbaseDbPool hbaseDbPool;

    public static IDbPool getDbPool() {
        if (dbPool == null) {
            synchronized (DbPoolFactory.class) {
                if (dbPool == null) {
                    dbPool = new DefaultDbPool();
                }
            }
        }
        return dbPool;
    }

    public static HbaseDbPool getHbaseDbPool() {
        if (hbaseDbPool == null) {
            synchronized (DbPoolFactory.class) {
                if (hbaseDbPool == null) {
                    hbaseDbPool = new HbaseDbPool();
                }
            }
        }
        return hbaseDbPool;
    }


}
