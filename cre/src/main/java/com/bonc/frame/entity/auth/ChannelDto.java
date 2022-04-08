package com.bonc.frame.entity.auth;

public class ChannelDto {
    //渠道编码
    private String channelId;
    //渠道名称
    private String channelName;
    private String channelCode;

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    // 机构名-用于拼接
    private String deptName;

    public ChannelDto() {
    }

    public ChannelDto(String channelId, String channelName, String deptName,String channelCode) {
        this.channelId = channelId;
        this.channelName = channelName;
        this.deptName = deptName;
        this.channelCode = channelCode;
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

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }
}
