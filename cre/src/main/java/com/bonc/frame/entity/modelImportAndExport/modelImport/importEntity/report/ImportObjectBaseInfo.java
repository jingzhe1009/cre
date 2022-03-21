package com.bonc.frame.entity.modelImportAndExport.modelImport.importEntity.report;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/4/5 22:22
 */
public class ImportObjectBaseInfo {
    private String type;
    private String id;
    private String name;
    private String code;
    private String isPublic;

    public ImportObjectBaseInfo() {
    }

    public ImportObjectBaseInfo(String type, String id, String name, String code, String isPublic) {
        this.type = type;
        this.id = id;
        this.name = name;
        this.code = code;
        this.isPublic = isPublic;
    }

    public String getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(String isPublic) {
        this.isPublic = isPublic;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
