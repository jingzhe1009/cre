package com.bonc.frame.entity.auth.resource;

/**
 * @author yedunyao
 * @date 2019/8/16 10:38
 */
public class DataSourceResource extends DataResource implements Cloneable {

    private String dbId;//数据库链接标识

    private String dbAlias;//数据库链接别名，满足唯一性约束

    private String dbIp;//数据库链接IP

    private Integer dbPort;//数据库链接端口

    private String dbType;//数据库链接类型

    private String dbTypeName;//数据库链接类型名

    private String dbServiceName;//数据库服务名

    private String dbUsername;//数据库用户名

    public String getDbId() {
        return dbId;
    }

    public void setDbId(String dbId) {
        this.dbId = dbId;
        super.resourceId = dbId;
    }

    public String getDbAlias() {
        return dbAlias;
    }

    public void setDbAlias(String dbAlias) {
        this.dbAlias = dbAlias;
    }

    public String getDbIp() {
        return dbIp;
    }

    public void setDbIp(String dbIp) {
        this.dbIp = dbIp;
    }

    public Integer getDbPort() {
        return dbPort;
    }

    public void setDbPort(Integer dbPort) {
        this.dbPort = dbPort;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public String getDbTypeName() {
        return dbTypeName;
    }

    public void setDbTypeName(String dbTypeName) {
        this.dbTypeName = dbTypeName;
    }

    public String getDbServiceName() {
        return dbServiceName;
    }

    public void setDbServiceName(String dbServiceName) {
        this.dbServiceName = dbServiceName;
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public void setDbUsername(String dbUsername) {
        this.dbUsername = dbUsername;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DataSourceResource{");
        sb.append("dbId='").append(dbId).append('\'');
        sb.append(", dbAlias='").append(dbAlias).append('\'');
        sb.append(", dbIp='").append(dbIp).append('\'');
        sb.append(", dbPort=").append(dbPort);
        sb.append(", dbType='").append(dbType).append('\'');
        sb.append(", dbTypeName='").append(dbTypeName).append('\'');
        sb.append(", dbServiceName='").append(dbServiceName).append('\'');
        sb.append(", dbUsername='").append(dbUsername).append('\'');
        sb.append(", resourceId='").append(resourceId).append('\'');
        sb.append(", resources=").append(resources);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
