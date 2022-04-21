package com.bonc.frame.service.impl.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bonc.frame.config.Config;
import com.bonc.frame.dao.DaoHelper;
import com.bonc.frame.entity.api.ApiConf;
import com.bonc.frame.entity.api.ApiConfGroup;
import com.bonc.frame.entity.commonresource.ApiGroup;
import com.bonc.frame.entity.commonresource.VariableGroupExt;
import com.bonc.frame.entity.variable.reference.VariableApi;
import com.bonc.frame.module.lock.distributed.curator.CuratorMutexLock;
import com.bonc.frame.module.lock.distributed.curator.LockDataType;
import com.bonc.frame.security.ResourceType;
import com.bonc.frame.service.api.ApiService;
import com.bonc.frame.service.auth.AuthorityService;
import com.bonc.frame.service.rule.RuleDetailService;
import com.bonc.frame.service.syslog.SysLogService;
import com.bonc.frame.util.*;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import java.util.*;

/**
 * @author qxl
 * @version 1.0
 * @date 2018年3月20日 下午4:26:47
 */
@Service("apiService")
public class ApiServiceImpl implements ApiService {

    @Autowired
    private DaoHelper daoHelper;

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private SysLogService sysLogService;

    private final String _MYBITSID_PREFIX = "com.bonc.frame.dao.api.ApiMapper.";
    private final String _MYBITSID_AG_PREFIX = "com.bonc.frame.mapper.resource.ApiGroupMapper.";

    /**
     * 接口-变量中间表
     */
    private final String _VARIABLE_API_PREFIX = "com.bonc.frame.mapper.variable.VariableApiMapper.";

    /**
     * 模型-接口中间表
     */
    private final String _API_RULE_PREFIX = "com.bonc.frame.mapper.api.ApiRuleMapper.";

    @Autowired
    private RuleDetailService ruleDetailService;

    //获取所有的api接口类型
    @Override
    public List<Map<String, Object>> getAllApiType() {
        List<Map<String, Object>> list = daoHelper.queryForList(_MYBITSID_PREFIX + "selectApiTypes");
        if (list != null && list.size() > 0) {
            list.get(0).put("selected", true);
        }
        return list;
    }

    @Override
    public ResponseResult getVariablesByApiId(String apiId) {
        List<VariableGroupExt> list = daoHelper.queryForList(_MYBITSID_PREFIX +
                "getVariablesByApiId", apiId);
        return ResponseResult.createSuccessInfo("success", list);
    }

    @Override
    public List<ApiConf> selectByPrimaryKey(String apiId) {
        Map<String, Object> param = new HashMap<>();
        param.put("apiId", apiId);
        List<ApiConf> list = daoHelper.queryForList(_MYBITSID_PREFIX +
                "selectByPrimaryKey", param);
        return list;
    }

    public ApiConf selectApiByApiId(String apiId) {
        Map<String, Object> param = new HashMap<>();
        param.put("apiId", apiId);
        ApiConf apiConf = (ApiConf) daoHelper.queryOne(_MYBITSID_PREFIX +
                "selectByPrimaryKey", param);
        return apiConf;
    }

    public List<ApiConf> selectApiByProperty(String apiId, String apiName, String url, String isPublic, String folderId) {
        if (StringUtils.isBlank(apiId) && StringUtils.isBlank(apiName) && StringUtils.isBlank(url)) {
            return Collections.emptyList();
        }
        Map<String, Object> param = new HashMap<>();
        param.put("apiId", apiId);
        param.put("apiName", apiName);
        param.put("url", url);
        param.put("isPublic", isPublic);
        param.put("folderId", folderId);
        return daoHelper.queryForList(_MYBITSID_PREFIX +
                "selectApiByProperty", param);
    }

    public List<ApiConf> selectApiByApiIdList(List<String> apiIdList) {
        if (apiIdList == null || apiIdList.isEmpty()) {
            return Collections.emptyList();
        }
        Map<String, Object> param = new HashMap<>();
        param.put("apiIdList", apiIdList);
        return daoHelper.queryForList(_MYBITSID_PREFIX +
                "selectApiByApiIdList", param);
    }

    //保存新建的接口
    @Override
    @CuratorMutexLock(value = {"apiName"}, lockDataType = LockDataType.API)
    public ResponseResult insertApi(ApiConf apiConf, String currentUser) {
        if (checkApiNameIsExist(apiConf.getApiName(), apiConf.getFolderId(), apiConf.getApiId())) {
            return ResponseResult.createFailInfo("API接口名重复.");
        }

        final String id = IdUtil.createId();
        apiConf.setApiId(id);

//        // 插入接口-参数引用关系
//        insertNewVariables(apiConf);

//        daoHelper.insert(_MYBITSID_PREFIX + "insertSelective", apiConf);
//
//        // 自动授权：插入用户对数据的权限
//       /* final Authority authority = new Authority();
//        authority.setResourceId(id);
//        authority.setResourceExpression("*");
//        authorityService.grantToUser(authority, ResourceType.DATA_PUB_API.getType(), currentUser);*/
//        authorityService.autoGrantAuthToCurrentUser(id, ResourceType.DATA_PUB_API);
//
//        sysLogService.logOperate(ConstantUtil.OPERATE_CREATE_API,
//                JSON.toJSONString(apiConf));
        insertApiDataPersistence(apiConf);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertApiDataPersistence(ApiConf apiConf) {
        if (apiConf != null) {
            String apiId = apiConf.getApiId();
            String currentUser = ControllerUtil.getCurrentUser();
            apiConf.setCreateDate(new Date());
            apiConf.setCreatePersion(currentUser);
            // 插入接口-参数引用关系
            insertNewVariables(apiConf);

            daoHelper.insert(_MYBITSID_PREFIX + "insertSelective", apiConf);

            // 自动授权：插入用户对数据的权限
            authorityService.autoGrantAuthToCurrentUser(apiId, ResourceType.DATA_PUB_API);

            sysLogService.logOperate(ConstantUtil.OPERATE_CREATE_API,
                    JSON.toJSONString(apiConf));
        }
    }

    public boolean needLock(ApiConf apiConf) {
        List<ApiConf> oldVariable = this.selectByPrimaryKey(apiConf.getApiId());
        return !oldVariable.get(0).getApiName().equals(apiConf.getApiName());
    }

    //修改接口
    @Override
    @CuratorMutexLock(value = {"apiName"}, addLockCondition = "#{#myService.needLock(#apiConf)}",
            lockDataType = LockDataType.API)
    public ResponseResult updateApi(ApiConf apiConf) {
        // 是否被模型引用
        /*if (isUsed(apiConf.getApiId())) {
            return ResponseResult.createFailInfo("接口正在使用中，不能修改！");
        }*/

        ResponseResult result = ruleDetailService.checkCanModify(apiConf.getApiId(), apiConf.getFolderId());
        if (result.getStatus() == ResponseResult.ERROR_STAUS) {
            return result;
        }
        if (checkApiNameIsExist(apiConf.getApiName(), apiConf.getFolderId(), apiConf.getApiId())) {
            return ResponseResult.createFailInfo("API接口名重复.");
        }

        // 更新接口-参数引用关系
        updateApiVariables(apiConf);

        daoHelper.update(_MYBITSID_PREFIX + "updateByPrimaryKeySelective", apiConf);

        sysLogService.logOperate(ConstantUtil.OPERATE_UPDATE_API,
                JSON.toJSONString(apiConf));
        return ResponseResult.createSuccessInfo();
    }

    //获取所有接口列表
    @Override
    public Map<String, Object> getApiList(String apiName, String folderId, String start, String size) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("apiName", "%" + apiName + "%");
        param.put("folderId", folderId);
        Map<String, Object> result = daoHelper.queryForPageList(_MYBITSID_PREFIX + "getApiList", param, start, size);
        return result;
    }

    @Override
    public List<ApiConf> selecApiPropertiesByRuleId(String ruleId) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("ruleId", ruleId);
        List<ApiConf> result = daoHelper.queryForList(_MYBITSID_PREFIX + "selecApiPropertiesByRuleId", param);
        return result;
    }

    //删除接口
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult deleteApi(String apiId, String folderId) {
        // 是否被模型引用
        if (isUsed(apiId)) {
            return ResponseResult.createFailInfo("接口正在使用中，不能删除！");
        }

        ResponseResult result = ruleDetailService.checkCanModify(apiId, folderId);
        if (result.getStatus() == ResponseResult.ERROR_STAUS) {
            return result;
        }

        // 级联删除：参数引用
        deleteVaraiblesByApiId(apiId);

        // 级联删除：数据对应的权限
        authorityService.deleteByResourceId(apiId, ResourceType.DATA_PUB_API.getType());

        daoHelper.delete(_MYBITSID_PREFIX + "deleteByPrimaryKey", apiId);

        final JSONObject info = new JSONObject();
        info.put("apiId", apiId);
        info.put("folderId", folderId);
        sysLogService.logOperate(ConstantUtil.OPERATE_DELETE_API,
                info.toJSONString());

        return ResponseResult.createSuccessInfo();
    }

    /**
     * 检查同一个文件夹中的api接口名是否重复
     *
     * @param apiName
     * @param folderId
     * @param apiId
     * @return
     */
    private boolean checkApiNameIsExist(String apiName, String folderId, String apiId) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("apiName", apiName);
        param.put("folderId", folderId);
        param.put("apiId", apiId);
        Object o = daoHelper.queryOne(_MYBITSID_PREFIX + "getApiListByName", param);
        Integer obj = (Integer) o;
        if (obj != null && obj > 0) {
            return true;
        }
        return false;
    }

    //根据文件夹ID获取所以的API
    @Override
    public List<ApiConf> getAllApiByFolderId(String folderId) {
        List<ApiConf> list = daoHelper.queryForList(_MYBITSID_PREFIX + "getAllApiByFolderId", folderId);
        return list;
    }

    // 查看接口是否被引用
    @Override
    public boolean isUsed(String apiId) {
        if (!Config.CRE_REFERENCE_CHECK_ENABLE) {
            return false;
        }
        final int count = (int) daoHelper.queryOne(_MYBITSID_PREFIX + "countApiUsedByApiId", apiId);
        if (count > 0) {
            return true;
        }
        return false;
    }

    @Override
    public ResponseResult checkUsed(String apiId) {
        if (isUsed(apiId)) {
            return ResponseResult.createFailInfo("接口正在使用中");
        }
        return ResponseResult.createSuccessInfo();
    }

    /**
     * 判断参数集合中是否包含私有变量
     *
     * @param apiIdList
     * @param folderId
     * @return 有私有变量返回 {@code true}
     */
    @Override
    public boolean checkExitsPrivateApi(Collection<String> apiIdList, final String folderId) {
        if (CollectionUtil.isEmpty(apiIdList)) {
            return false;
        }

        Map<String, Object> params = new HashMap<>();
        params.put("folderId", folderId);
        params.put("apiIdList", apiIdList);
        int count = (int) daoHelper.queryOne(_MYBITSID_PREFIX + "checkExitsPrivateApi", params);
        if (count > 0) {
            return true;
        }
        return false;
    }

    // ------------------------------ 公共接口 ------------------------------

    // 同一组下名称不能相同
    private boolean checkApiNameByGroupIsExist(String apiName, String apiId, String apiGroupId) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("apiName", apiName);
        param.put("apiId", apiId);
        param.put("apiGroupId", apiGroupId);
        Integer obj = (Integer) daoHelper.queryOne(_MYBITSID_PREFIX + "getApiListByName", param);
        if (obj != null && obj > 0) {
            return true;
        }
        return false;
    }

    @Override
    public List<ApiConf> pubGetAllApiList() {
        final List<ApiConf> pubApiList = daoHelper.queryForList(_MYBITSID_PREFIX + "pubGetAllApiList");
        return pubApiList;
    }

    @Override
    public Map<String, Object> pubGetApiList(String apiName, String apiGroupName,
                                             String startDate, String endDate,
                                             String start, String size) {
        Map<String, Object> param = new HashMap<>();
        param.put("apiName", apiName);
        param.put("apiGroupName", apiGroupName);
        param.put("startDate", startDate);
        param.put("endDate", endDate);
        final Map<String, Object> result = daoHelper.queryForPageList(_MYBITSID_PREFIX + "pubGetApiList",
                param, start, size);
        return result;
    }

    public List<ApiConfGroup> pubGetApiListAll(String apiName, String apiGroupName,
                                               String startDate, String endDate) {
        Map<String, Object> param = new HashMap<>();
        param.put("apiName", apiName);
        param.put("apiGroupName", apiGroupName);
        param.put("startDate", startDate);
        param.put("endDate", endDate);
        List<ApiConfGroup> result = daoHelper.queryForList(_MYBITSID_PREFIX + "pubGetApiList",
                param);
        return result;
    }

    @Override
    public Map<String, Object> pagedPubApiResource(String apiName, String apiGroupName,
                                                   String startDate, String endDate,
                                                   String start, String size) {
        Map<String, Object> param = new HashMap<>();
        param.put("apiName", apiName);
        param.put("apiGroupName", apiGroupName);
        param.put("startDate", startDate);
        param.put("endDate", endDate);
        final Map<String, Object> result = daoHelper.queryForPageList(_MYBITSID_PREFIX +
                "pagedPubApiResource", param, start, size);
        return result;
    }

    @Override
    public Map<String, Object> pagedPubApiGroupResource(String apiGroupId, String apiGroupName,
                                                   String startDate, String endDate,
                                                   String start, String size) {
        Map<String, Object> param = new HashMap<>();
        param.put("apiGroupId", apiGroupId);
        param.put("apiGroupName", apiGroupName);
        param.put("startDate", startDate);
        param.put("endDate", endDate);
        final Map<String, Object> result = daoHelper.queryForPageList(_MYBITSID_AG_PREFIX +
                "pagedPubApiGroupResource", param, start, size);
        return result;
    }

    // 插入接口-参数引用关系
    private void insertNewVariables(ApiConf apiConf) {
        final String apiContent = apiConf.getApiContent();
        if (StringUtils.isNotEmpty(apiContent)) {
            final JSONObject jsonObject = JSON.parseObject(apiContent);

            // 插入接口返回值-参数引用
            final String newReturnParamId = jsonObject.getString("newReturnParamId");
            if (StringUtils.isNotEmpty(newReturnParamId)) {
                insertReturnVariable(apiConf.getApiId(), newReturnParamId);
            }

            // 插入接口请求参数-参数引用
            final JSONArray newParamIds = jsonObject.getJSONArray("newParamId");
            insertApiVarible(newParamIds, apiConf.getApiId());
        }
    }

    // 更新接口-参数引用关系
    private void updateApiVariables(ApiConf apiConf) {
        final String apiContent = apiConf.getApiContent();
        if (StringUtils.isNotEmpty(apiContent)) {
            final JSONObject jsonObject = JSON.parseObject(apiContent);
            // 返回值
            final String newReturnParamId = jsonObject.getString("newReturnParamId");
            final String oldReturnParamId = jsonObject.getString("oldReturnParamId");
            if (!StringUtils.isBlank(newReturnParamId) || !StringUtils.isBlank(oldReturnParamId)) {
                if (StringUtils.isBlank(newReturnParamId)) {
                    if (!StringUtils.isBlank((oldReturnParamId))) {
                        // 删除
                        deleteVariable(apiConf.getApiId(), oldReturnParamId);
                    }
                } else {
                    if (StringUtils.isBlank(oldReturnParamId)) {
                        // 新增
                        insertReturnVariable(apiConf.getApiId(), newReturnParamId);
                    } else {
                        if (!newReturnParamId.equals(oldReturnParamId)) {
                            // 修改
                            updateVariable(apiConf.getApiId(), newReturnParamId, oldReturnParamId);
                        }
                    }
                }
            }

            // 请求参数列表
            final JSONArray newParamIds = jsonObject.getJSONArray("newParamId");
            final JSONArray oldParamIds = jsonObject.getJSONArray("oldParamId");

            if (newParamIds == null && oldParamIds == null) {
                return;
            } else if (newParamIds == null) {
                // 删除
                deleteBatchByVariableIdApiId(apiConf.getApiId(), oldParamIds);
            } else if (oldParamIds == null) {
                // 新增
                List<VariableApi> needInsertParams = new LinkedList<>();
                for (int i = 0; i < newParamIds.size(); i++) {
                    final String param = (String) newParamIds.get(i);
                    final VariableApi variableApi = new VariableApi();
                    variableApi.setApiId(apiConf.getApiId());
                    variableApi.setVariableId(param);
                    variableApi.setApiVariableType(VariableApi.REQUEST_VARIABLE_CODE);
                    needInsertParams.add(variableApi);
                }
                insertRequestVariableBatch(needInsertParams);
            } else {
                List<VariableApi> needInsertParams = new LinkedList<>();
                for (int i = 0; i < newParamIds.size(); i++) {
                    final String param = (String) newParamIds.get(i);
                    if (oldParamIds.contains(param)) {
                        oldParamIds.remove(param);
                    } else {
                        // 新增
                        final VariableApi variableApi = new VariableApi();
                        variableApi.setApiId(apiConf.getApiId());
                        variableApi.setVariableId(param);
                        variableApi.setApiVariableType(VariableApi.REQUEST_VARIABLE_CODE);
                        needInsertParams.add(variableApi);
                    }
                }
                // 删除
                deleteBatchByVariableIdApiId(apiConf.getApiId(), oldParamIds);
                insertRequestVariableBatch(needInsertParams);
            }
        }
    }

    // 新建接口
    @Override
    @CuratorMutexLock(value = {"apiName"}, lockDataType = LockDataType.API)
    public ResponseResult pubInsert(ApiConf apiConf, String userId) {
        if (apiConf == null) {
            return ResponseResult.createFailInfo("请求参数[apiConf]为空");
        }

        if (checkApiNameByGroupIsExist(apiConf.getApiName(), apiConf.getApiId(), apiConf.getApiGroupId())) {
            return ResponseResult.createFailInfo("API接口名重复.");
        }

        apiConf.setApiId(IdUtil.createId());
        apiConf.setCreateDate(new Date());
        apiConf.setCreatePersion(userId);
        apiConf.setIsPublic(ConstantUtil.PUBLIC);

        // 插入接口-参数引用关系
        insertNewVariables(apiConf);

        daoHelper.insert(_MYBITSID_PREFIX + "pubInsertSelective", apiConf);


        sysLogService.logOperate(ConstantUtil.OPERATE_CREATE_API,
                JSON.toJSONString(apiConf));

        return ResponseResult.createSuccessInfo();
    }


    //修改接口
    @Override
    @CuratorMutexLock(value = {"apiName"}, addLockCondition = "#{#myService.needLock(#apiConf)}",
            lockDataType = LockDataType.API)
    public ResponseResult pubUpdateApi(ApiConf apiConf, String userId) {
        if (apiConf == null) {
            return ResponseResult.createFailInfo("请求参数[apiConf]为空");
        }

        // 是否被模型引用
        /*if (isUsed(apiConf.getApiId())) {
            return ResponseResult.createFailInfo("接口正在使用中，不能修改！");
        }*/

        if (checkApiNameByGroupIsExist(apiConf.getApiName(), apiConf.getApiId(), apiConf.getApiGroupId())) {
            return ResponseResult.createFailInfo("API接口名重复.");
        }

        // 更新接口-参数引用关系
        updateApiVariables(apiConf);

        apiConf.setUpdateDate(new Date());
        apiConf.setUpdatePersion(userId);

        daoHelper.update(_MYBITSID_PREFIX + "updateByPrimaryKeySelective", apiConf);

        sysLogService.logOperate(ConstantUtil.OPERATE_UPDATE_API,
                JSON.toJSONString(apiConf));
        return ResponseResult.createSuccessInfo();
    }

    // 删除接口
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult pubDeleteApi(String apiId) {
        if (apiId == null || "".equals(apiId.trim())) {
            return ResponseResult.createFailInfo("请求参数[apiId]为空");
        }

        if (isUsed(apiId)) {
            return ResponseResult.createFailInfo("接口正在使用中，不能删除");
        }

        // 注意：级联删除参数引用
        deleteVaraiblesByApiId(apiId);
//        daoHelper.delete(_VARIABLE_API_PREFIX + "deleteVaraiblesByApiId", apiId);
        daoHelper.delete(_MYBITSID_PREFIX + "deleteByPrimaryKey", apiId);

        final JSONObject info = new JSONObject();
        info.put("apiId", apiId);
        sysLogService.logOperate(ConstantUtil.OPERATE_DELETE_API,
                info.toJSONString());

        return ResponseResult.createSuccessInfo();
    }

    private int countPrivateVariables(String apiId) {
        int countPrivateVariables = (int) daoHelper.queryOne(_MYBITSID_PREFIX +
                "countPrivateVariables", apiId);
        return countPrivateVariables;
    }

    private boolean hasPrivateVariables(String apiId) {
        if (countPrivateVariables(apiId) > 0) {
            return true;
        }
        return false;
    }

    /**
     * 私有接口变为公有接口
     * <p>
     * 当接口引用的变量中存在私有变量则拒绝升级
     *
     * @param apiId
     * @param apiGroupId
     * @param currentUser
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult upgrade(String apiId, String apiGroupId, String currentUser) {
        // 当接口引用的变量中存在私有变量则拒绝升级
        if (hasPrivateVariables(apiId)) {
            //return ResponseResult.createFailInfo("当前接口存在对私有参数的引用，不能变为公有接口");
        }
        Map<String, Object> param = new HashMap<>(4);
        param.put("apiId", apiId);
        param.put("apiGroupId", apiGroupId);
        param.put("updatePersion", currentUser);
        param.put("updateDate", new Date());

        daoHelper.update(_MYBITSID_PREFIX + "upgrade", param);
        return ResponseResult.createSuccessInfo();
    }

    private boolean checkApiGroupNameIsExist(String apiGroupName, @Nullable String apiGroupId) {
        Map<String, String> param = new HashMap<>();
        param.put("apiGroupName", apiGroupName);
        param.put("apiGroupId", apiGroupId);
        int obj = (int) daoHelper.queryOne(_MYBITSID_AG_PREFIX +
                "countByName", param);
        if (obj > 0) {
            return true;
        }
        return false;
    }

    // 新建接口组
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult pubInsertApiGroup(ApiGroup apiGroup, String userId) {
        if (apiGroup == null) {
            return ResponseResult.createFailInfo("请求参数[apiGroup]为空");
        }

        if (checkApiGroupNameIsExist(apiGroup.getApiGroupName(), null)) {
            return ResponseResult.createFailInfo("接口组名称已存在");
        }

        apiGroup.setApiGroupId(IdUtil.createId());
        apiGroup.setCreateDate(new Date());
        apiGroup.setCreatePerson(userId);
//        daoHelper.insert(_MYBITSID_AG_PREFIX + "insertSelective", apiGroup);
        insertApiGroupDataPersistence(apiGroup);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertApiGroupDataPersistence(ApiGroup apiGroup) {
        if (apiGroup != null) {
            String currentUser = ControllerUtil.getCurrentUser();
            apiGroup.setCreateDate(new Date());
            apiGroup.setCreatePerson(currentUser);
            daoHelper.insert(_MYBITSID_AG_PREFIX + "insertSelective", apiGroup);
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult pubUpdateApiGroup(ApiGroup apiGroup, String userId) {
        if (apiGroup == null) {
            return ResponseResult.createFailInfo("请求参数[apiGroup]为空");
        }

        if (checkApiGroupNameIsExist(apiGroup.getApiGroupName(), apiGroup.getApiGroupId())) {
            return ResponseResult.createFailInfo("接口类型名称已存在");
        }

        apiGroup.setUpdateDate(new Date());
        apiGroup.setUpdatePerson(userId);

        daoHelper.update(_MYBITSID_AG_PREFIX + "updateByPrimaryKeySelective", apiGroup);
        return ResponseResult.createSuccessInfo();
    }

    private int countApisById(String apiGroupId) {
        if (apiGroupId == null || "".equals(apiGroupId.trim())) {
            throw new IllegalArgumentException("请求参数[apiGroupId]为空");
        }
        int count = (int) daoHelper.queryOne(_MYBITSID_AG_PREFIX +
                "countApisById", apiGroupId);
        return count;
    }

    private boolean pubGroupCanDeleted(String apiGroupId) {
        if (countApisById(apiGroupId) > 0) {
            return false;
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult pubDeleteApiGroup(String apiGroupId) {
        if (apiGroupId == null || "".equals(apiGroupId.trim())) {
            return ResponseResult.createFailInfo("请求参数[apiGroupId]为空");
        }
        if (!pubGroupCanDeleted(apiGroupId)) {
            return ResponseResult.createFailInfo("接口组正在使用中，不能删除");
        }

        // 注意：使用表中外键的约束条件，接口组被删除时，接口的组id将变为null
        daoHelper.delete(_MYBITSID_AG_PREFIX + "deleteByPrimaryKey", apiGroupId);
        return ResponseResult.createSuccessInfo();
    }

    // 获取接口组列表
    @Override
    public ResponseResult pubApiGroups(String apiGroupName) {
        final List<ApiGroup> list = daoHelper.queryForList(_MYBITSID_AG_PREFIX + "select", apiGroupName);
        return ResponseResult.createSuccessInfo("success", list);
    }

    @Override
    public List<ApiGroup> getApiGroup(String apiGroupId, String apiGroupName) {
        Map<String, Object> param = new HashMap<>();
        param.put("apiGroupId", apiGroupId);
        param.put("apiGroupName", apiGroupName);
        return daoHelper.queryForList(_MYBITSID_AG_PREFIX + "selectByApiGroupProperty", param);
    }

    @Override
    public Map<String, Object> pubApiGroupsPaged(String apiGroupName, String start, String length) {
        Map<String, Object> map = daoHelper.queryForPageList(_MYBITSID_AG_PREFIX + "select",
                apiGroupName, start, length);
        return map;
    }

    // 获取指定参数组id的所有变量
    @Override
    public ResponseResult pubSelectApisByGroupId(String apiGroupId) {
        List<ApiConf> apiConfList = daoHelper.queryForList(_MYBITSID_PREFIX + "pubSelectApisByGroupId",
                apiGroupId);
        return ResponseResult.createSuccessInfo("success", apiConfList);
    }

    // ------------------------------ 引用参数 ------------------------------

    // 接口添加参数
    private ResponseResult insertVariable(VariableApi variableApi) {
        daoHelper.insert(_VARIABLE_API_PREFIX + "insertSelective", variableApi);
        return ResponseResult.createSuccessInfo();
    }

    private ResponseResult insertReturnVariable(String apiId, String variableId) {
        final VariableApi variableApi = new VariableApi();
        variableApi.setApiId(apiId);
        variableApi.setVariableId(variableId);
        variableApi.setApiVariableType(VariableApi.RETURN_VARIABLE_CODE);
        insertVariable(variableApi);
        return ResponseResult.createSuccessInfo();
    }

    private void insertRequestVariableBatch(List<VariableApi> variableApiList) {
        if (variableApiList != null && !variableApiList.isEmpty()) {
            daoHelper.insertBatch(_VARIABLE_API_PREFIX + "insertBatch", variableApiList);
        }
    }

    private void deleteBatchByVariableIdApiId(String apiId, JSONArray oldParamIds) {
        List<VariableApi> needDeleteParams = new LinkedList<>();
        for (int i = 0; i < oldParamIds.size(); i++) {
            final String param = (String) oldParamIds.get(i);
            final VariableApi variableApi = new VariableApi();
            variableApi.setApiId(apiId);
            variableApi.setVariableId(param);
            needDeleteParams.add(variableApi);
        }
        deleteBatchByVariableIdApiId(needDeleteParams);
    }

    private void deleteBatchByVariableIdApiId(List<VariableApi> variableApiList) {
        if (variableApiList != null && !variableApiList.isEmpty()) {
            daoHelper.delete(_VARIABLE_API_PREFIX + "deleteBatchByVariableIdApiId", variableApiList);
        }
    }

    private void insertApiVarible(JSONArray newParamIds, String apiId) {
        if (newParamIds != null && !newParamIds.isEmpty()) {
            // 插入接口请求参数-参数引用
            List<VariableApi> needInsertParams = new LinkedList<>();
            for (int i = 0; i < newParamIds.size(); i++) {
                final String param = (String) newParamIds.get(i);
                final VariableApi variableApi = new VariableApi();
                variableApi.setApiId(apiId);
                variableApi.setVariableId(param);
                variableApi.setApiVariableType(VariableApi.REQUEST_VARIABLE_CODE);
                needInsertParams.add(variableApi);
            }
            insertRequestVariableBatch(needInsertParams);
        }
    }

    // 接口更新参数
    private ResponseResult updateVariable(String apiId, String variableId, String oldVariableId) {
        Map<String, Object> param = ImmutableMap.<String, Object>of("apiId", apiId,
                "variableId", variableId,
                "oldVariableId", oldVariableId);
        daoHelper.update(_VARIABLE_API_PREFIX + "updateVariableId", param);
        return ResponseResult.createSuccessInfo();
    }

    // 接口删除参数
    private ResponseResult deleteVariable(String apiId, String variableId) {
        Map<String, Object> param = ImmutableMap.<String, Object>of("apiId", apiId,
                "variableId", variableId);
        daoHelper.delete(_VARIABLE_API_PREFIX + "deleteByVariableIdApiId", param);
        return ResponseResult.createSuccessInfo();
    }

    // 根据接口id删除参数
    private ResponseResult deleteVaraiblesByApiId(String apiId) {
        daoHelper.delete(_VARIABLE_API_PREFIX + "deleteByApiId", apiId);
        return ResponseResult.createSuccessInfo();
    }
}
