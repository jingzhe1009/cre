package com.bonc.frame.service.api;

import com.bonc.frame.entity.api.ApiConf;
import com.bonc.frame.entity.api.ApiConfGroup;
import com.bonc.frame.entity.commonresource.ApiGroup;
import com.bonc.frame.util.ResponseResult;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author qxl
 * @version 1.0
 * @date 2018年3月20日 下午4:26:07
 */
public interface ApiService {

    /**
     * 获取所有的api接口类型
     * 返回格式：
     * [
     * {key:'http',text: 'http',selected:true},
     * {key:'redis',text: 'redis'},
     * {key:'soap',text: 'soap'}
     * ]
     *
     * @return
     */
    List<Map<String, Object>> getAllApiType();

    ResponseResult getVariablesByApiId(String apiId);

    List<ApiConf> selectByPrimaryKey(String apiId);

    ApiConf selectApiByApiId(String apiId);

    /**
     * 通过下面的属性获取api
     * ip为 apiContent 里面的Ip 通过ip模糊匹配
     *
     * @param apiId
     * @param apiName
     * @param url     ip为 apiContent 里面的Ip 通过ip模糊匹配
     * @return
     */
    List<ApiConf> selectApiByProperty(String apiId, String apiName, String url, String isPublic, String folderId);

    List<ApiConf> selectApiByApiIdList(List<String> apiIdList);

    /**
     * 保存新建的接口
     *
     * @param apiConf
     * @return
     */
    ResponseResult insertApi(ApiConf apiConf, String currentUser);

    void insertApiDataPersistence(ApiConf apiConf);

    /**
     * 修改接口
     *
     * @param apiConf
     * @return
     */
    ResponseResult updateApi(ApiConf apiConf);

    /**
     * 获取所有接口列表
     *
     * @param folerId
     * @param start
     * @param size
     * @return
     */
    Map<String, Object> getApiList(String apiName, String folderId, String start, String size);


    List<ApiConf> selecApiPropertiesByRuleId(String apiId);

    /**
     * 删除接口
     *
     * @param apiId
     * @return
     */
    ResponseResult deleteApi(String apiId, String folderId);

    /**
     * 根据文件夹ID获取所以的API
     *
     * @param folderId
     * @return
     */
    List<ApiConf> getAllApiByFolderId(String folderId);

    boolean isUsed(String apiId);

    ResponseResult checkUsed(String apiId);

    boolean checkExitsPrivateApi(Collection<String> apiIdList, final String folderId);

    List<ApiConf> pubGetAllApiList();

    /**
     * 分页获取Api
     */
    Map<String, Object> pubGetApiList(String apiName, String apiGroupName,
                                      String startDate, String endDate,
                                      String start, String size);

    /**
     * 获取所有的Api
     */
    List<ApiConfGroup> pubGetApiListAll(String apiName, String apiGroupName,
                                        String startDate, String endDate);

    Map<String, Object> pagedPubApiResource(String apiName, String apiGroupName,
                                            String startDate, String endDate,
                                            String start, String size);

    ResponseResult pubInsert(ApiConf apiConf, String userId);

    ResponseResult pubUpdateApi(ApiConf apiConf, String userId);

    ResponseResult pubDeleteApi(String apiId);

    ResponseResult upgrade(String apiId, String apiGroupId, String currentUser);

    ResponseResult pubInsertApiGroup(ApiGroup apiGroup, String userId);

    void insertApiGroupDataPersistence(ApiGroup apiGroup);

    ResponseResult pubUpdateApiGroup(ApiGroup apiGroup, String userId);

    ResponseResult pubDeleteApiGroup(String apiGroupId);

    ResponseResult pubApiGroups(String apiGroupName);

    /**
     * 通过 id 或 name 完全匹配
     *
     * @param apiGroupId
     * @param apiGroupName
     * @return
     */
    List<ApiGroup> getApiGroup(String apiGroupId, String apiGroupName);

    Map<String, Object> pubApiGroupsPaged(String apiGroupName, String start, String length);

    ResponseResult pubSelectApisByGroupId(String apiGroupId);

}
