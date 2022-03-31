package com.bonc.frame.entity.commonresource;

import java.util.List;

/**
 * 新增模型传的实体类
 */
public class ModelGroupDto extends ModelGroup {
    private List<String> channelList;

    public List<String> getChannelList() {
        return channelList;
    }

    public void setChannelList(List<String> channelList) {
        this.channelList = channelList;
    }

    public ModelGroupDto(List<String> channelList) {
        this.channelList = channelList;
    }
}
