package com.bonc.frame.entity.commonresource;

import java.util.List;

public class ModelChanIdDto {
    private List<String> idList;
    private String modelGroupId;

    public String getModelGroupId() {
        return modelGroupId;
    }

    public void setModelGroupId(String modelGroupId) {
        this.modelGroupId = modelGroupId;
    }

    public ModelChanIdDto(List<String> idList, String modelGroupId) {
        this.idList = idList;
        this.modelGroupId = modelGroupId;
    }

    public ModelChanIdDto() {
    }

    public ModelChanIdDto(List<String>  idList) {
        this.idList = idList;
    }

    @Override
    public String toString() {
        return "ModelChanIdDto{" +
                "idList=" + idList +
                ", modelGroupId='" + modelGroupId + '\'' +
                '}';
    }

    public List<String>  getIdList() {
        return idList;
    }

    public void setIdList(List<String>  idList) {
        this.idList = idList;
    }
}
