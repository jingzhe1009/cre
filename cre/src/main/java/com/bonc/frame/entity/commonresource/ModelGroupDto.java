package com.bonc.frame.entity.commonresource;

/**
 * 新增模型传的实体类
 */
public class ModelGroupDto extends ModelGroup {
    private ModelChanIdDto channelList;

    public ModelGroupDto(String modelGroupName, String modelGroupCode, String modelGroupDesc, ModelChanIdDto channelList) {
        super(modelGroupName, modelGroupCode, modelGroupDesc);
        this.channelList = channelList;
    }

    public ModelGroupDto() {
    }

    public static ModelGroup getMg(ModelGroupDto dto) {
        ModelGroup mg = new ModelGroup();
        if (dto.getModelGroupId() != null && !dto.getModelGroupId().isEmpty()) {
            mg.setModelGroupId(dto.getModelGroupId());
        }
        if (dto.getModelGroupName() != null &&!dto.getModelGroupName().isEmpty() ) {
            mg.setModelGroupName(dto.getModelGroupName());
        }
        if (dto.getModelGroupCode() != null && !dto.getModelGroupCode().isEmpty()) {
            mg.setModelGroupCode(dto.getModelGroupCode());
        }
        if (dto.getModelGroupDesc() != null && !dto.getModelGroupDesc().isEmpty()) {
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

    public ModelChanIdDto getChannelList() {
        return channelList;
    }

    public void setChannelList(ModelChanIdDto channelList) {
        this.channelList = channelList;
    }

    public ModelGroupDto(ModelChanIdDto channelList) {
        this.channelList = channelList;
    }
}
