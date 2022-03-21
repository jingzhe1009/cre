package com.bonc.frame.entity.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ApiConf {
    private String apiId;

    private String folderId;

    private String apiType;

    private String apiName;

    private String apiDesc;

    private String isLog;

    private String createPersion;

    private Date createDate;

    private String updatePersion;

    private Date updateDate;

    private String apiContent;

    /**
     * 是否为公共资源，非1：私有；1：公共
     * 如果是公共资源，{@code folderId}，
     * 否则，{@code folderId}必不为空
     */
    private String isPublic;

    // 所属公共参数组
    private String apiGroupId;

    public String getApiId() {
        return apiId;
    }

    public void setApiId(String apiId) {
        this.apiId = apiId;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public String getApiType() {
        return apiType;
    }

    public void setApiType(String apiType) {
        this.apiType = apiType;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getApiDesc() {
        return apiDesc;
    }

    public void setApiDesc(String apiDesc) {
        this.apiDesc = apiDesc;
    }

    public String getIsLog() {
        return isLog;
    }

    public void setIsLog(String isLog) {
        this.isLog = isLog;
    }

    public String getCreatePersion() {
        return createPersion;
    }

    public void setCreatePersion(String createPersion) {
        this.createPersion = createPersion;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getUpdatePersion() {
        return updatePersion;
    }

    public void setUpdatePersion(String updatePersion) {
        this.updatePersion = updatePersion;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getApiContent() {
        return apiContent;
    }

    public void setApiContent(String apiContent) {
        this.apiContent = apiContent;
    }

    public String getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(String isPublic) {
        this.isPublic = isPublic;
    }

    public String getApiGroupId() {
        return apiGroupId;
    }

    public void setApiGroupId(String apiGroupId) {
        this.apiGroupId = apiGroupId;
    }

    public String paseApiUrl() {
        if (StringUtils.isBlank(apiContent)) {
            return null;
        }
        String url = null;
        try {

            JSONObject apiContentJSON = JSONObject.parseObject(apiContent);
            if (apiContentJSON != null && !apiContentJSON.isEmpty()) {
                url = apiContentJSON.getString("url");
            }
        } catch (Exception ignored) {
        }
        return url;
    }

    public String paseApiUrlWithReturnAndParam() {
        if (StringUtils.isBlank(apiContent)) {
            return null;
        }
        StringBuilder result = null;
        String url = null;
        String returnValue = null;
        Set<Object> params = null;
        try {

            JSONObject apiContentJSON = JSONObject.parseObject(apiContent);
            if (apiContentJSON != null && !apiContentJSON.isEmpty()) {
                url = apiContentJSON.getString("url");
                returnValue = apiContentJSON.getString("returnValue");
                JSONArray paramJsonArray = apiContentJSON.getJSONArray("param");
                params = new HashSet<>(paramJsonArray);

                result = new StringBuilder(url + returnValue);
                for (Object param : params) {
                    if (param != null) {
                        result.append(param.toString());
                    }
                }
            }
        } catch (Exception ignored) {
        }
        if (result == null) {
            return null;
        }
        return result.toString();
    }

    @Override
    public String toString() {
        return "ApiConf{" +
                "apiId='" + apiId + '\'' +
                ", folderId='" + folderId + '\'' +
                ", apiType='" + apiType + '\'' +
                ", apiName='" + apiName + '\'' +
                ", apiDesc='" + apiDesc + '\'' +
                ", isLog='" + isLog + '\'' +
                ", createPersion='" + createPersion + '\'' +
                ", createDate=" + createDate +
                ", updatePersion='" + updatePersion + '\'' +
                ", updateDate=" + updateDate +
                ", apiContent='" + apiContent + '\'' +
                ", isPublic='" + isPublic + '\'' +
                ", apiGroupId='" + apiGroupId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiConf otherApiConf = (ApiConf) o;

        String apiUrl = paseApiUrlWithReturnAndParam();
        String otherApiUrl = otherApiConf.paseApiUrlWithReturnAndParam();
        boolean apiContentEquals = false;
        if (!StringUtils.isBlank(apiUrl) && !StringUtils.isBlank(otherApiUrl)) {
            apiContentEquals = apiUrl.equals(otherApiUrl);
        } else if (StringUtils.isBlank(apiUrl) && StringUtils.isBlank(otherApiUrl)) {
            apiContentEquals = true;
        }
        return Objects.equals(apiType, otherApiConf.apiType) && apiContentEquals;
    }
}