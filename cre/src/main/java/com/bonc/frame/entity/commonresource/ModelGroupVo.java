package com.bonc.frame.entity.commonresource;

import com.bonc.frame.entity.auth.Channel;

import java.util.List;
import java.util.Objects;

/**
 * 产品信息带渠道数据，可用于回显等展示
 */
public class ModelGroupVo extends ModelGroup{
    // 渠道的id和name
    private List<Channel> channelList;

    public ModelGroupVo() {
    }

    public ModelGroupVo(String modelGroupName, String modelGroupCode, String modelGroupDesc) {
        super(modelGroupName, modelGroupCode, modelGroupDesc);
    }

    public static ModelGroupVo getVo(ModelGroup mg) {
        ModelGroupVo vo = new ModelGroupVo();
        if (mg.getModelGroupId() != null && !Objects.equals(mg.getModelGroupId(), "")) {
            vo.setModelGroupId(mg.getModelGroupId());
        }
        if (mg.getModelGroupName() != null && !Objects.equals(mg.getModelGroupName(), "")) {
            vo.setModelGroupName(mg.getModelGroupName());
        }
        if (mg.getModelGroupCode() != null && !Objects.equals(mg.getModelGroupCode(), "")) {
            vo.setModelGroupCode(mg.getModelGroupCode());
        }
        if (mg.getModelGroupDesc() != null && !Objects.equals(mg.getModelGroupDesc(), "")) {
            vo.setModelGroupDesc(mg.getModelGroupDesc());
        }
        if (mg.getCreateDate() != null ) {
            vo.setCreateDate(mg.getCreateDate());
        }
        if (mg.getCreatePerson() != null && !Objects.equals(mg.getCreatePerson(), "")) {
            vo.setCreatePerson(mg.getCreatePerson());
        }
        if (mg.getUpdateDate() != null) {
            vo.setUpdateDate(mg.getUpdateDate());
        }
        if (mg.getUpdatePerson() != null && !Objects.equals(mg.getUpdatePerson(), "")) {
            vo.setUpdatePerson(mg.getUpdatePerson());
        }
        return vo;
    }

    public List<Channel> getChannelList() {
        return channelList;
    }

    public void setChannelList(List<Channel> channelList) {
        this.channelList = channelList;
    }
}
