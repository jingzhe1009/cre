package com.bonc.frame.entity.commonresource;

/**
 * @author HeYaning
 * @date 2022-3-28 16:30
 * 产品与渠道关联的中间表
 */
public class ModelGroupChannel {
    private String id;
    // 渠道id
    private String channelId;
    // 产品id
    private String modelGroupId;

    public ModelGroupChannel() {
    }

    public ModelGroupChannel(String id, String channelId, String modelGroupId) {
        this.id = id;
        this.channelId = channelId;
        this.modelGroupId = modelGroupId;
    }

    @Override
    public String toString() {
        return "ModelGroupChannel{" +
                "id='" + id + '\'' +
                ", channelId='" + channelId + '\'' +
                ", modelGroupId='" + modelGroupId + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getModelGroupId() {
        return modelGroupId;
    }

    public void setModelGroupId(String modelGroupId) {
        this.modelGroupId = modelGroupId;
    }
}
