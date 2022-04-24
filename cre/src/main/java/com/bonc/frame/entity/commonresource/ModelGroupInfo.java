package com.bonc.frame.entity.commonresource;

import com.bonc.frame.entity.auth.Channel;
import com.bonc.frame.entity.rule.RuleDetail;
import com.bonc.frame.entity.rule.RuleDetailHeader;

import java.util.List;

/**
 * 用于通过产品查看模型的页面数据的加载
 */
public class ModelGroupInfo{
    // 渠道相关数据
    private List<Channel> channelList;
    // 产品下模型数据
    private List<ModelRuleDetail> modelList;
    // 产品id
    private String modelGroupId;

    public ModelGroupInfo() {
    }

    public ModelGroupInfo(List<Channel> channelList, List<ModelRuleDetail> modelList, String modelGroupId) {
        this.channelList = channelList;
        this.modelList = modelList;
        this.modelGroupId = modelGroupId;
    }

    public ModelGroupInfo(String modelGroupName, String modelGroupCode, String modelGroupDesc, List<Channel> channelList, List<ModelRuleDetail> modelList, String modelGroupId) {
        this.channelList = channelList;
        this.modelList = modelList;
        this.modelGroupId = modelGroupId;
    }

    public String getModelGroupId() {
        return modelGroupId;
    }

    public void setModelGroupId(String modelGroupId) {
        this.modelGroupId = modelGroupId;
    }

    public List<Channel> getChannelList() {
        return channelList;
    }

    public void setChannelList(List<Channel> channelList) {
        this.channelList = channelList;
    }

    public List<ModelRuleDetail> getModelList() {
        return modelList;
    }

    public void setModelList(List<ModelRuleDetail> modelList) {
        this.modelList = modelList;
    }

    public void getModelGroupInfo(ModelGroupInfo info, ModelGroup mg) {
        info.setModelGroupId(mg.getModelGroupId());
    }
}
