package com.bonc.frame.entity.commonresource;

import com.bonc.frame.entity.auth.Channel;
import com.bonc.frame.entity.rule.RuleDetail;
import com.bonc.frame.entity.rule.RuleDetailHeader;

import java.util.List;

/**
 * 用于通过产品查看模型的页面数据的加载
 */
public class ModelGroupInfo extends ModelGroup {
    // 渠道相关数据
    private List<Channel> channelList;
    // 产品下模型数据
    private List<RuleDetailHeader> modelList;

    public ModelGroupInfo() {
    }

    public List<Channel> getChannelList() {
        return channelList;
    }

    public void setChannelList(List<Channel> channelList) {
        this.channelList = channelList;
    }

    public List<RuleDetailHeader> getModelList() {
        return modelList;
    }

    public void setModelList(List<RuleDetailHeader> modelList) {
        this.modelList = modelList;
    }

    public void getModelGroupInfo(ModelGroupInfo info, ModelGroup mg) {
        info.setModelGroupId(mg.getModelGroupId());
        if (!mg.getModelGroupName().isEmpty() && mg.getModelGroupName() != null) {
            info.setModelGroupName(mg.getModelGroupName());
        }
        if (!mg.getModelGroupCode().isEmpty() && mg.getModelGroupCode() != null) {
            info.setModelGroupCode(mg.getModelGroupCode());
        }
        if (!mg.getModelGroupDesc().isEmpty() && mg.getModelGroupDesc() != null) {
            info.setModelGroupDesc(mg.getModelGroupDesc());
        }
        if (mg.getCreateDate() != null) {
            info.setCreateDate(mg.getCreateDate());
        }
        if (!mg.getCreatePerson().isEmpty() && mg.getCreatePerson() != null) {
            info.setCreatePerson(mg.getCreatePerson());
        }
        if (mg.getUpdateDate() != null) {
            info.setUpdateDate(mg.getUpdateDate());
        }
        if (!mg.getUpdatePerson().isEmpty() && mg.getUpdatePerson() != null) {
            info.setUpdatePerson(mg.getUpdatePerson());
        }
    }
}
