package com.bonc.frame.entity.rulefolder;

import java.util.Date;
import java.util.Objects;

public class RuleFolder {
    private String folderId;

    private String folderPid;

    private String folderName;

    private String folderType;

    private String isDel;

    private String createPerson;

    private Date createDate;

    private String updatePerson;

    private Date updateDate;

    private String folderDesc;

    /**
     * 是否为公共；0，私有；1，公共
     */
    private String isPublic;

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public String getFolderPid() {
        return folderPid;
    }

    public void setFolderPid(String folderPid) {
        this.folderPid = folderPid;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderType() {
        return folderType;
    }

    public void setFolderType(String folderType) {
        this.folderType = folderType;
    }

    public String getIsDel() {
        return isDel;
    }

    public void setIsDel(String isDel) {
        this.isDel = isDel;
    }

    public String getCreatePerson() {
        return createPerson;
    }

    public void setCreatePerson(String createPerson) {
        this.createPerson = createPerson;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getUpdatePerson() {
        return updatePerson;
    }

    public void setUpdatePerson(String updatePerson) {
        this.updatePerson = updatePerson;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getFolderDesc() {
        return folderDesc;
    }

    public void setFolderDesc(String folderDesc) {
        this.folderDesc = folderDesc;
    }

    public String getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(String isPublic) {
        this.isPublic = isPublic;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RuleFolder{");
        sb.append("folderId='").append(folderId).append('\'');
        sb.append(", folderPid='").append(folderPid).append('\'');
        sb.append(", folderName='").append(folderName).append('\'');
        sb.append(", folderType='").append(folderType).append('\'');
        sb.append(", isDel='").append(isDel).append('\'');
        sb.append(", createPerson='").append(createPerson).append('\'');
        sb.append(", createDate=").append(createDate);
        sb.append(", updatePerson='").append(updatePerson).append('\'');
        sb.append(", updateDate=").append(updateDate);
        sb.append(", folderDesc='").append(folderDesc).append('\'');
        sb.append(", isPublic='").append(isPublic).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RuleFolder folder = (RuleFolder) o;
        return Objects.equals(folderName, folder.folderName);
    }
}