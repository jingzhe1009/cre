package com.bonc.frame.entity.commonresource;


/**
 * 产品-渠道，绑定时回显数据实体类
 */
public class ModelGroupChannelVo {
    // 渠道id-用于关联
    private String channelId;
    // 渠道名称
    private String channelName;
    // 渠道编码
    private String channelCode;
    // 机构名称
    private String deptName;
    // 机构编码
    private String deptCode;
    // 是否绑定-0代表未关联，1代表已关联
    private String isConnected;

    public ModelGroupChannelVo() {
    }

    public ModelGroupChannelVo(String channelId, String channelName, String channelCode, String deptName, String deptCode, String isConnected) {
        this.channelId = channelId;
        this.channelName = channelName;
        this.channelCode = channelCode;
        this.deptName = deptName;
        this.deptCode = deptCode;
        this.isConnected = isConnected;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public String getIsConnected() {
        return isConnected;
    }

    public void setIsConnected(String isConnected) {
        this.isConnected = isConnected;
    }
}
