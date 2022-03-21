package com.bonc.frame.module.db.operator.hbase;

/**
 * @author yedunyao
 * @date 2019/7/9 16:38
 */
public class HbaseConfig {

    private String dbId;

    private String host;

    private String port;

    private String namespace;

    private Integer poolSize = 1;

    public String getDbId() {
        return dbId;
    }

    public void setDbId(String dbId) {
        this.dbId = dbId;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public Integer getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(Integer poolSize) {
        if (poolSize >= 1) {
            this.poolSize = poolSize;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String dbId;

        private String host;

        private String port;

        private String namespace;

        private Integer poolSize = 1;

        public Builder dbId(String dbId) {
            this.dbId = dbId;
            return this;
        }

        public Builder host(String host) {
            this.host = host;
            return this;
        }

        public Builder port(String port) {
            this.port = port;
            return this;
        }

        public Builder namespace(String namespace) {
            this.namespace = namespace;
            return this;
        }

        public Builder poolSize(int poolSize) {
            if (poolSize >= 1) {
                this.poolSize = poolSize;
            }
            return this;
        }

        private void construct(HbaseConfig hbaseHelper) {
            hbaseHelper.setHost(host);
            hbaseHelper.setPort(port);
            hbaseHelper.setDbId(dbId);
            hbaseHelper.setNamespace(namespace);
            hbaseHelper.setPoolSize(poolSize);
        }

        public HbaseConfig build() {
            HbaseConfig hbaseHelper = new HbaseConfig();
            construct(hbaseHelper);
            return hbaseHelper;
        }


    }

}
