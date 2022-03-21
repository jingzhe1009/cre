package com.bonc.frame.entity.auth.resource;

/**
 * @author yedunyao
 * @since 2020/3/23 18:13
 */
public class RuleFolderResource extends DataResource implements Cloneable {

    private String folderId;

    private String folderName;

    private String folderDesc;

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
        super.resourceId = folderId;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderDesc() {
        return folderDesc;
    }

    public void setFolderDesc(String folderDesc) {
        this.folderDesc = folderDesc;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RuleFolderResource{");
        sb.append("folderId='").append(folderId).append('\'');
        sb.append(", folderName='").append(folderName).append('\'');
        sb.append(", folderDesc='").append(folderDesc).append('\'');
        sb.append(", resourceId='").append(resourceId).append('\'');
        sb.append(", resources=").append(resources);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
