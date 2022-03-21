package com.bonc.frame.entity.syslog;

import java.util.Date;
import java.util.Map;

/**
 * 这个类用来日志记录,包括,用户,ip,请求的类名,方法名,传入的参数,返回值,异常等
 *
 * @Author: wangzhengbao
 * @DATE: 2019/12/2 10:35
 */
public class SysLogDetails extends SysLog {
    private String clazzName; // 类名
    private String methodName; // 方法名
    private Map<String, Object> parameters; // 传入参数
    private Object returnValue; // 返回值
    private Exception exception; // 异常
    private String comments; // 注释

    public static BuildSysLogDetails buildSysLogDetails() {
        return new BuildSysLogDetails();
    }

    @Override
    public String toString() {
        return "SysLogDetails{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", clazzName='" + clazzName + '\'' +
                ", methodName='" + methodName + '\'' +
                ", parameters=" + parameters +
                ", returnValue=" + returnValue +
                ", comments=" + comments +
                ", exception='" + exception + '\'' +
                ", logId='" + logId + '\'' +
                ", ip='" + ip + '\'' +
                ", logType='" + logType + '\'' +
                ", operateType='" + operateType + '\'' +
                ", operateTime=" + operateTime +
                ", operateInfo='" + operateInfo + '\'' +
                ", operateResult='" + operateResult + '\'' +
                '}';
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public Object getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(Object returnValue) {
        this.returnValue = returnValue;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public String getClazzName() {
        return clazzName;
    }

    public void setClazzName(String clazzName) {
        this.clazzName = clazzName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }


    public static class BuildSysLogDetails {
        private SysLogDetails sysLogDetails;

        BuildSysLogDetails() {
            sysLogDetails = new SysLogDetails();
        }

        public SysLogDetails build() {
            return sysLogDetails;
        }

        public BuildSysLogDetails setLogId(String logId) {
            sysLogDetails.setLogId(logId);
            return this;
        }

        public BuildSysLogDetails setIp(String ip) {
            sysLogDetails.setIp(ip);
            return this;
        }

        public BuildSysLogDetails setLogType(String logType) {
            sysLogDetails.setLogType(logType);
            return this;
        }

        public BuildSysLogDetails setOperateType(String operateType) {
            sysLogDetails.setOperateType(operateType);
            return this;
        }

        public BuildSysLogDetails setOperateTime(Date operateTime) {
            sysLogDetails.setOperateTime(operateTime);
            return this;
        }

        public BuildSysLogDetails setOperateInfo(String operateInfo) {
            sysLogDetails.setOperateInfo(operateInfo);
            return this;
        }

        public BuildSysLogDetails setOperateResult(String operateResult) {
            sysLogDetails.setOperateResult(operateResult);
            return this;
        }

        public BuildSysLogDetails setUserId(String userId) {
            sysLogDetails.setUserId(userId);
            return this;
        }

        public BuildSysLogDetails setUserName(String userName) {
            sysLogDetails.setUserName(userName);
            return this;
        }

        public BuildSysLogDetails setClazzName(String clazzName) {
            sysLogDetails.setClazzName(clazzName);
            return this;
        }

        public BuildSysLogDetails setMethodName(String methodName) {
            sysLogDetails.setMethodName(methodName);
            return this;
        }

        public BuildSysLogDetails setParameters(Map<String, Object> parameters) {
            sysLogDetails.setParameters(parameters);
            return this;
        }

        public BuildSysLogDetails setReturnValue(String returnValue) {
            sysLogDetails.setReturnValue(returnValue);
            return this;
        }

        public BuildSysLogDetails setException(Exception exception) {
            sysLogDetails.setException(exception);
            return this;
        }

        public BuildSysLogDetails setComments(String comments) {
            sysLogDetails.setComments(comments);
            return this;
        }
    }
}
