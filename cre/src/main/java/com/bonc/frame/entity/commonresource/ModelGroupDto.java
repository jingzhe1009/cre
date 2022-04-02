package com.bonc.frame.entity.commonresource;

import java.util.List;

/**
 * 新增模型传的实体类
 */
public class ModelGroupDto extends ModelGroup {
    private List<String> channelList;

    public ModelGroupDto(String modelGroupName, String modelGroupCode, String modelGroupDesc, List<String> channelList) {
        super(modelGroupName, modelGroupCode, modelGroupDesc);
        this.channelList = channelList;
    }

    public ModelGroupDto() {
    }

    public static ModelGroup getMg(ModelGroupDto dto) {
        ModelGroup mg = new ModelGroup();
        if (!dto.getModelGroupId().isEmpty() && dto.getModelGroupId() != null) {
            mg.setModelGroupId(dto.getModelGroupId());
        }
        if (!dto.getModelGroupName().isEmpty() && dto.getModelGroupName() != null) {
            mg.setModelGroupName(dto.getModelGroupName());
        }
        if (!dto.getModelGroupCode().isEmpty() && dto.getModelGroupCode() != null) {
            mg.setModelGroupCode(dto.getModelGroupCode());
        }
        if (!dto.getModelGroupDesc().isEmpty() && dto.getModelGroupDesc() != null) {
            mg.setModelGroupDesc(dto.getModelGroupDesc());
        }
        if (dto.getCreatePerson() != null) {
            mg.setCreatePerson(dto.getCreatePerson());
        }
        if (dto.getCreateDate()!=null) {
            mg.setCreateDate(dto.getCreateDate());
        }
        if (dto.getUpdatePerson() != null) {
            mg.setUpdatePerson(dto.getUpdatePerson());
        }
        if (dto.getUpdateDate()!=null) {
            mg.setUpdateDate(dto.getUpdateDate());
        }
        return mg;
    }

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
