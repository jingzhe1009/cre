package com.bonc.frame.entity.syslog;

import java.util.Date;
import java.util.Map;

/**
 * @author yedunyao
 * @date 2019/5/7 15:34
 */
public class SysLog {

    protected String logId;

    protected String userId;

    protected String userName;

    protected String ip;

    protected String logType;

    protected String operateType;

    protected Date operateTime;

    protected String operateInfo;

    protected String operateResult;

    public static BuildSysLog buildSysLog() {
        return new BuildSysLog();
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public String getOperateInfo() {
        return operateInfo;
    }

    public void setOperateInfo(String operateInfo) {
        this.operateInfo = operateInfo;
    }

    public String getOperateResult() {
        return operateResult;
    }

    public void setOperateResult(String operateResult) {
        this.operateResult = operateResult;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "SysLog{" +
                "logId='" + logId + '\'' +
                ", ip='" + ip + '\'' +
                ", logType='" + logType + '\'' +
                ", operateType='" + operateType + '\'' +
                ", operateTime=" + operateTime +
                ", operateInfo='" + operateInfo + '\'' +
                ", operateResult='" + operateResult + '\'' +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }

    public static class BuildSysLog {
        private SysLog sysLog;

        public BuildSysLog() {
            sysLog = new SysLog();
        }

        public SysLog build() {
            return sysLog;
        }

        public BuildSysLog setLogId(String logId) {
            sysLog.setLogId(logId);
            return this;
        }

        public BuildSysLog setIp(String ip) {
            sysLog.setIp(ip);
            return this;
        }

        public BuildSysLog setLogType(String logType) {
            sysLog.setLogType(logType);
            return this;
        }

        public BuildSysLog setOperateType(String operateType) {
            sysLog.setOperateType(operateType);
            return this;
        }

        public BuildSysLog setOperateTime(Date operateTime) {
            sysLog.setOperateTime(operateTime);
            return this;
        }

        public BuildSysLog setOperateInfo(String operateInfo) {
            sysLog.setOperateInfo(operateInfo);
            return this;
        }

        public BuildSysLog setOperateResult(String operateResult) {
            sysLog.setOperateResult(operateResult);
            return this;
        }

        public BuildSysLog setUserId(String userId) {
            sysLog.setUserId(userId);
            return this;
        }

        public BuildSysLog setUserName(String userName) {
            sysLog.setUserName(userName);
            return this;
        }
    }

}
