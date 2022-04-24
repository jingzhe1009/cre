package com.bonc.frame.service.impl.ruleSet;

import com.bonc.frame.config.Config;
import com.bonc.frame.dao.DaoHelper;
import com.bonc.frame.entity.auth.Authority;
import com.bonc.frame.entity.commonresource.*;
import com.bonc.frame.module.lock.distributed.curator.CuratorMutexLock;
import com.bonc.frame.module.lock.distributed.curator.LockDataType;
import com.bonc.frame.module.log.aop.operatorLog.OperatorLogDataPersistence;
import com.bonc.frame.security.ResourceType;
import com.bonc.frame.service.auth.AuthorityService;
import com.bonc.frame.service.impl.rule.RuleDetailServiceImpl;
import com.bonc.frame.service.reference.RuleSetReferenceService;
import com.bonc.frame.service.ruleSetBase.RuleSetBaseService;
import com.bonc.frame.service.variable.VariableService;
import com.bonc.frame.util.CollectionUtil;
import com.bonc.frame.util.ConstantUtil;
import com.bonc.frame.util.IdUtil;
import com.bonc.frame.util.ResponseResult;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.*;

/**
 * 规则库管理
 *
 * @author yedunyao
 * @date 2019/9/2 15:50
 */
@Service
public class RuleSetBaseServiceImpl implements RuleSetBaseService {

    private static final String _RULE_SET_GROUP_MAPPER = "com.bonc.frame.dao.resource.RuleSetGroupMapper.";

    private static final String _RULE_SET_MAPPER = "com.bonc.frame.dao.resource.RuleSetMapper.";

    private static final String _RULE_DETAIL = "com.bonc.frame.mapper.oracle.rule.RuleDetailMapper.";

    /**
     * 模型-规则库规则集引用中间表
     */
    private static final String _RULE_SET_RULE_PREFIX = "com.bonc.frame.mapper.commonresource.RuleSetRuleMapper.";

    @Autowired
    private DaoHelper daoHelper;

    @Autowired
    private VariableService variableService;

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private RuleSetReferenceService ruleSetReferenceService;

    // ------------------------ 规则集头信息管理 ------------------------

    @Override
    public boolean checkNameIsExist(String ruleSetName, @Nullable String ruleSetHeaderId) {
        if (!Config.CRE_REFERENCE_CHECK_ENABLE) {
            return false;
        }
        Map<String, String> param = new HashMap<>(2);
        param.put("ruleSetName", ruleSetName);
        param.put("ruleSetHeaderId", ruleSetHeaderId);
        int obj = (int) daoHelper.queryOne(_RULE_SET_MAPPER + "countByName", param);
        if (obj > 0) {
            return true;
        }
        return false;
    }

    @Override
    public ResponseResult getHeaderList(@Nullable String ruleSetName,
                                        @Nullable String ruleSetGroupName,
                                        @Nullable String startDate,
                                        @Nullable String endDate) {
        Map<String, String> param = new HashMap<>(4);
        param.put("ruleSetName", ruleSetName);
        param.put("ruleSetGroupName", ruleSetGroupName);
        param.put("startDate", startDate);
        param.put("endDate", endDate);
        List<RuleSetHeaderGroupExt> ruleSetHeaderGroupExts = daoHelper.queryForList(_RULE_SET_MAPPER +
                "getHeaderList", param);
        return ResponseResult.createSuccessInfo("", ruleSetHeaderGroupExts);
    }

    @Override
    public Map<String, Object> getHeaderList(@Nullable String ruleSetName,
                                             @Nullable String ruleSetGroupName,
                                             @Nullable String startDate,
                                             @Nullable String endDate,
                                             String start, String length) {
        Map<String, String> param = new HashMap<>(4);
        param.put("ruleSetName", ruleSetName);
        param.put("ruleSetGroupName", ruleSetGroupName);
        param.put("startDate", startDate);
        param.put("endDate", endDate);
        return daoHelper.queryForPageList(_RULE_SET_MAPPER +
                "getHeaderList", param, start, length);
    }

    @Override
    public Map<String, Object> getHeaderListResource(@Nullable String ruleSetName,
                                                     @Nullable String ruleSetGroupName,
                                                     @Nullable String startDate,
                                                     @Nullable String endDate,
                                                     String start, String size) {
        Map<String, String> param = new HashMap<>(4);
        param.put("ruleSetName", ruleSetName);
        param.put("ruleSetGroupName", ruleSetGroupName);
        param.put("startDate", startDate);
        param.put("endDate", endDate);
        return daoHelper.queryForPageList(_RULE_SET_MAPPER +
                "getHeaderListResource", param, start, size);
    }

    @Override
    public Map<String, Object> getRuleSetGroupHeaderListResource(@Nullable String ruleSetGroupId,
                                                     @Nullable String ruleSetGroupName,
                                                     @Nullable String startDate,
                                                     @Nullable String endDate,
                                                     String start, String size) {
        Map<String, String> param = new HashMap<>(4);
        param.put("ruleSetGroupId", ruleSetGroupId);
        param.put("ruleSetGroupName", ruleSetGroupName);
        param.put("startDate", startDate);
        param.put("endDate", endDate);
        Map<String, Object> map = daoHelper.queryForPageList(_RULE_SET_GROUP_MAPPER +
                "getRuleSetGroupHeaderListResource", param, start, size);
        return map;
    }


    @Override
    @CuratorMutexLock(value = {"ruleSetName"}, lockDataType = LockDataType.RULE_SET)
    @OperatorLogDataPersistence(operatorType = ConstantUtil.OPERATE_CREATE_RULE_SET_HRADER, logType = "1")
    public ResponseResult createRuleSetHeader(RuleSetHeader ruleSetHeader, String userId) {
        if (ruleSetHeader == null) {
            return ResponseResult.createFailInfo("请求参数[ruleSetHeader]不能为空");
        }

        if (checkNameIsExist(ruleSetHeader.getRuleSetName(), ruleSetHeader.getRuleSetHeaderId())) {
            return ResponseResult.createFailInfo("规则集名称已存在");
        }

        String uuId = IdUtil.createId();
        ruleSetHeader.setRuleSetHeaderId(uuId);
        ruleSetHeader.setCreateDate(new Date());
        ruleSetHeader.setCreatePerson(userId);
        daoHelper.insert(_RULE_SET_MAPPER + "insertRuleSetHeader", ruleSetHeader);


        // 自动授权：插入用户对数据的权限
        /*final Authority authority = new Authority();
        authority.setResourceId(uuId);
        authority.setResourceExpression("*");
        authorityService.grantToUser(authority, ResourceType.DATA_PUB_RULE_SET.getType(), userId);*/
        authorityService.autoGrantAuthToCurrentUser(uuId, ResourceType.DATA_PUB_RULE_SET);

        return ResponseResult.createSuccessInfo("", uuId);
    }

    public boolean needLock(RuleSetHeader ruleSetHeader) {
        RuleSetHeader oldRuleSet = (RuleSetHeader) daoHelper.queryOne(_RULE_SET_MAPPER +
                "getRuleSetHeaderByPrimaryKey", ruleSetHeader.getRuleSetHeaderId());
        return !oldRuleSet.getRuleSetName().equals(ruleSetHeader.getRuleSetName());
    }
//    public boolean needLock(RuleSet ruleSet){
//        RuleSetHeader oldRuleSet =   (RuleSetHeader) daoHelper.queryOne(_RULE_SET_MAPPER +
//                "getRuleSetByPrimaryKey", ruleSet.getRuleSetId());
//        return !oldRuleSet.getRuleSetName().equals(ruleSet.getRuleSetName());
//    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CuratorMutexLock(value = {"ruleSetName"}, lockDataType = LockDataType.RULE_SET)
    @OperatorLogDataPersistence(operatorType = ConstantUtil.OPERATE_UPDATE_RULE_SET_HRADER, logType = "1")
    public ResponseResult updateRuleSetHeader(RuleSetHeader ruleSetHeader, String userId) {
        if (ruleSetHeader == null) {
            return ResponseResult.createFailInfo("请求参数[ruleSetHeader]不能为空");
        }

        if (checkNameIsExist(ruleSetHeader.getRuleSetName(), ruleSetHeader.getRuleSetHeaderId())) {
            return ResponseResult.createFailInfo("规则集名称已存在");
        }

        ruleSetHeader.setUpdateDate(new Date());
        ruleSetHeader.setUpdatePerson(userId);
        daoHelper.update(_RULE_SET_MAPPER + "updateRuleSetHeader", ruleSetHeader);
        return ResponseResult.createSuccessInfo();
    }

    /**
     * 查看规则集中是否存启用状态下的版本
     * 如果存在，则不能删除规则集头信息，不能修改规则集头信息中的名称
     */
    @Override
    public boolean checkAnyVersionIsEnable(String ruleSetHeaderId) {
        int obj = (int) daoHelper.queryOne(_RULE_SET_MAPPER +
                "countEnableVersions", ruleSetHeaderId);
        if (obj > 0) {
            return true;
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperatorLogDataPersistence(operatorType = ConstantUtil.OPERATE_DELETE_RULE_SET_HRADERT, logType = "1")
    public ResponseResult deleteRuleSetHeader(String ruleSetHeaderId) {
        if (StringUtils.isBlank(ruleSetHeaderId)) {
            return ResponseResult.createFailInfo("请求参数[ruleSetHeaderId]不能为空");
        }

        if (checkAnyVersionIsEnable(ruleSetHeaderId)) {
            return ResponseResult.createFailInfo("存在启用中的版本，不能删除");
        }

        // 级联删除: 删除其下的所有版本
        deleteRuleSetsByHeaderId(ruleSetHeaderId);

        // 删除规则集头
        daoHelper.delete(_RULE_SET_MAPPER + "deleteRuleSetHeaderByHeaderId", ruleSetHeaderId);

        // 级联删除: 删除权限表中的权限
        authorityService.deleteByResourceId(ruleSetHeaderId, ResourceType.DATA_PUB_RULE_SET.getType());
        return ResponseResult.createSuccessInfo();
    }


    // ------------------------ 规则集版本管理 ------------------------

    @Override
    public boolean checkVersionIsExist(String version, String ruleSetHeaderId,
                                       @Nullable String ruleSetId) {
        if (!Config.CRE_REFERENCE_CHECK_ENABLE) {
            return false;
        }
        Map<String, String> param = new HashMap<>(3);
        param.put("version", version);
        param.put("ruleSetHeaderId", ruleSetHeaderId);
        param.put("ruleSetId", ruleSetId);
        int obj = (int) daoHelper.queryOne(_RULE_SET_MAPPER +
                "countByVersionAndHeaderId", param);
        if (obj > 0) {
            return true;
        }
        return false;
    }

    @Override
    public RuleSetReferenceExt getRuleSetByRuleSetId(String ruleSetId) {
        // 获取引用的指标
        List<Map<String, Object>> kpiInfo = ruleSetReferenceService.selectKpiInfoByRuleSetId(ruleSetId);
        RuleSet ruleSet = (RuleSet) daoHelper.queryOne(_RULE_SET_MAPPER + "getRuleSetByRuleSetId", ruleSetId);
        if (ruleSet == null) {
            throw new RuntimeException("规则集[" + ruleSetId + "]不存在");
        }
        RuleSetReferenceExt ruleSetReferenceExt = new RuleSetReferenceExt();
        BeanUtils.copyProperties(ruleSet, ruleSetReferenceExt);
        ruleSetReferenceExt.setKpiInfo(kpiInfo);
        return ruleSetReferenceExt;
    }

    @Override
    public ResponseResult getRuleSetVersionList(String ruleSetHeaderId,
                                                @Nullable String enable,
                                                @Nullable String startDate,
                                                @Nullable String endDate) {
        Map<String, String> param = new HashMap<>(4);
        param.put("ruleSetHeaderId", ruleSetHeaderId);
        param.put("enable", enable);
        param.put("startDate", startDate);
        param.put("endDate", endDate);
        List<RuleSet> ruleSets = daoHelper.queryForList(_RULE_SET_MAPPER +
                "getRuleSetVersionList", param);
        return ResponseResult.createSuccessInfo("", ruleSets);
    }

    @Override
    public Map<String, Object> getRuleSetVersionList(String ruleSetHeaderId,
                                                     @Nullable String enable,
                                                     @Nullable String startDate,
                                                     @Nullable String endDate,
                                                     String start, String length) {
        Map<String, String> param = new HashMap<>(4);
        param.put("ruleSetHeaderId", ruleSetHeaderId);
        param.put("enable", enable);
        param.put("startDate", startDate);
        param.put("endDate", endDate);
        return daoHelper.queryForPageList(_RULE_SET_MAPPER +
                "getRuleSetVersionList", param, start, length);
    }

    @Override
    @CuratorMutexLock(value = {"ruleSetHeaderId"}, lockDataType = LockDataType.RULE_SET)
    @OperatorLogDataPersistence(operatorType = ConstantUtil.OPERATE_CREATE_RULE_SET_VERSION, logType = "1")
    public ResponseResult createRuleSet(RuleSet ruleSet, String userId) {
        if (ruleSet == null) {
            return ResponseResult.createFailInfo("请求参数[ruleSet]为空");
        }

        if (checkVersionIsExist(ruleSet.getVersion(), ruleSet.getRuleSetHeaderId(), null)) {
            return ResponseResult.createFailInfo("创建失败，版本号已存在");
        }

        String ruleSetId = IdUtil.createId();
        ruleSet.setRuleSetId(ruleSetId);
        ruleSet.setCreateDate(new Date());
        ruleSet.setCreatePerson(userId);
        ruleSet.setVersion(generateVersion(ruleSet.getRuleSetHeaderId()));

        daoHelper.insert(_RULE_SET_MAPPER + "insertRuleSet", ruleSet);

        String data = ruleSet.getRuleSetContent();
        // 建立规则集-公共参数引用关系
        insertReference(data, ruleSetId);
        return ResponseResult.createSuccessInfo("", ruleSetId);
    }

    private void insertReference(String data, String ruleSetId) {
        // 建立规则集-公共参数、指标引用关系
        if (StringUtils.isNotEmpty(data)) {
            final Set<String> varIdsFromModle = RuleDetailServiceImpl.getVarIdsFromModle(data);
            final Set<String> kpiIdsFromModle = RuleDetailServiceImpl.getKpiIdsFromModle(data);
            if (!varIdsFromModle.isEmpty()) {
                final List<String> newVarIds = ImmutableList.copyOf(varIdsFromModle);
                // 插入规则集-参数引用关系
                ruleSetReferenceService.insertVariableRuleSetBatch(ruleSetId, newVarIds);
            }
            if (!kpiIdsFromModle.isEmpty()) {
                final List<String> newKpiIds = ImmutableList.copyOf(kpiIdsFromModle);
                // 插入规则集-指标引用关系
                ruleSetReferenceService.insertKpiRuleSetBatch(ruleSetId, newKpiIds);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperatorLogDataPersistence(operatorType = ConstantUtil.OPERATE_UPDATE_RULE_SET_VERSION, logType = "1")
    public ResponseResult updateRuleSet(RuleSet ruleSet, String oldRuleContent, String userId) {
        if (ruleSet == null) {
            return ResponseResult.createFailInfo("请求参数[ruleSet]不能为空");
        }

        String ruleSetId = ruleSet.getRuleSetId();
        if (checkRuleSetEnable(ruleSetId)) {
            return ResponseResult.createFailInfo("当前版本的规则集正在启用中，不能修改");
        }

        ruleSet.setUpdateDate(new Date());
        ruleSet.setUpdatePerson(userId);
        daoHelper.update(_RULE_SET_MAPPER + "updateRuleSet", ruleSet);

        // 更新参数引用
        final Set<String> newVarIdsFromRuleSet = RuleDetailServiceImpl.getVarIdsFromModle(ruleSet.getRuleSetContent());
        final Set<String> oldVarIdsFromRuleSet = RuleDetailServiceImpl.getVarIdsFromModle(oldRuleContent);
        final List<String> needInsertVarIds = CollectionUtil.diffCollection(newVarIdsFromRuleSet, oldVarIdsFromRuleSet);
        ruleSetReferenceService.insertVariableRuleSetBatch(ruleSetId, needInsertVarIds);
        if (oldVarIdsFromRuleSet != null && !oldVarIdsFromRuleSet.isEmpty()) {
            List<String> needDeleteVarIds = ImmutableList.copyOf(oldVarIdsFromRuleSet);
            ruleSetReferenceService.deleteVariableRuleSetBatch(ruleSetId, needDeleteVarIds);
        }

        // 更新指标引用
        final Set<String> newKpiIdsFromRuleSet = RuleDetailServiceImpl.getKpiIdsFromModle(ruleSet.getRuleSetContent());
        final Set<String> oldKpiIdsFromRuleSet = RuleDetailServiceImpl.getKpiIdsFromModle(oldRuleContent);
        final List<String> needInsertKpiIds = CollectionUtil.diffCollection(newKpiIdsFromRuleSet, oldKpiIdsFromRuleSet);
        ruleSetReferenceService.insertKpiRuleSetBatch(ruleSetId, needInsertKpiIds);
        if (oldKpiIdsFromRuleSet != null && !oldKpiIdsFromRuleSet.isEmpty()) {
            List<String> needDeleteKpiIds = ImmutableList.copyOf(oldKpiIdsFromRuleSet);
            ruleSetReferenceService.deleteBatchByRuleSetIdsKpiIds(ruleSetId, needDeleteKpiIds);
        }

        return ResponseResult.createSuccessInfo();
    }

    // 检查当前版本的规则集是否为启用状态
    @Override
    public boolean checkRuleSetEnable(String ruleSetId) {
        int obj = (int) daoHelper.queryOne(_RULE_SET_MAPPER +
                "checkRuleSetEnable", ruleSetId);
        if (obj > 0) {
            return true;
        }
        return false;
    }

    // 检查当前版本的规则集是否被引用
    @Override
    public boolean checkRuleSetUsed(String ruleSetId) {
        if (!Config.CRE_REFERENCE_CHECK_ENABLE) {
            return false;
        }
        final int count = (int) daoHelper.queryOne(_RULE_SET_RULE_PREFIX + "countByRuleSetId", ruleSetId);
        if (count > 0) {
            return true;
        }
        return false;
    }

    // 启用/停用
    @Override
    @Transactional
    @OperatorLogDataPersistence(operatorType = ConstantUtil.OPERATE_ENABLE_RULE_SET_VERSION, logType = "1")
    public ResponseResult enable(String ruleSetId, String userId) {
        RuleSet ruleSet = new RuleSet();
        ruleSet.setRuleSetId(ruleSetId);
        ruleSet.setEnable(RuleSet.ENABLE);
        ruleSet.setUpdateDate(new Date());
        ruleSet.setUpdatePerson(userId);
        daoHelper.update(_RULE_SET_MAPPER + "updateRuleSet", ruleSet);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional
    @OperatorLogDataPersistence(operatorType = ConstantUtil.OPERATE_DISABLE_RULE_SET_VERSION, logType = "1")
    public ResponseResult disable(String ruleSetId, String userId) {
        if (checkRuleSetUsed(ruleSetId)) {
            //return ResponseResult.createFailInfo("当前版本的规则集正在被使用中，不能停用");
        }

        RuleSet ruleSet = new RuleSet();
        ruleSet.setRuleSetId(ruleSetId);
        ruleSet.setEnable(RuleSet.DISABLE);
        ruleSet.setUpdateDate(new Date());
        ruleSet.setUpdatePerson(userId);
        daoHelper.update(_RULE_SET_MAPPER + "updateRuleSet", ruleSet);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperatorLogDataPersistence(operatorType = ConstantUtil.OPERATE_DELETE_RULE_SET_VERSION, logType = "1")
    public ResponseResult deleteRuleSet(String ruleSetId) {
        if (StringUtils.isBlank(ruleSetId)) {
            return ResponseResult.createFailInfo("请求参数[ruleSetId]不能为空");
        }

        if (checkRuleSetEnable(ruleSetId)) {
            return ResponseResult.createFailInfo("当前版本的规则集正在启用中，不能删除");
        }

        if (checkRuleSetUsed(ruleSetId)) {
            return ResponseResult.createFailInfo("当前版本的规则集正在被使用，不能删除");
        }

        // 级联删除：参数引用
        ruleSetReferenceService.deleteVariableRuleSet(ruleSetId);
//        deleteVariablesByRuleSetId(ruleSetId);

        daoHelper.delete(_RULE_SET_MAPPER + "deleteRuleSet", ruleSetId);
        return ResponseResult.createSuccessInfo();
    }

    /**
     * 删除指定规则集下的所有版本
     * 没有对各个版本进行启用状态的检查，在调用前需要先进行检查
     * 存在一个启用中的版本则拒绝删除
     *
     * @param ruleSetHeaderId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
//    @OperatorLogDataPersistence(operatorType = ConstantUtil.OPERATE_DELETE_RULE_SET_HRADERT, logType = "1")
    public ResponseResult deleteRuleSetsByHeaderId(String ruleSetHeaderId) {
        if (StringUtils.isBlank(ruleSetHeaderId)) {
            return ResponseResult.createFailInfo("请求参数[ruleSetHeaderId]不能为空");
        }

        // 级联删除：参数引用
        ruleSetReferenceService.deleteVariablesByRuleSetHeaderId(ruleSetHeaderId);

        daoHelper.delete(_RULE_SET_MAPPER + "deleteRuleSetsByHeaderId", ruleSetHeaderId);
        return ResponseResult.createSuccessInfo();
    }

    private boolean checkExitsPrivateParam(final String ruleSetContent, final String folderId) {
        Set<String> varIdsFromRuleSet = RuleDetailServiceImpl.getVarIdsFromModle(ruleSetContent);
        return variableService.checkExitsPrivateVariable(varIdsFromRuleSet, folderId);
    }

    /**
     * 发布模型中的规则集到规则库中
     * <p>
     * 发布可以选择在规则库中新建规则集或已存在的规则集，
     * 并新建版本，最终保存模型对发布后的规则集的引用。
     * <p>
     * 如果{@link RuleSetHeader#}为空则新建规则集
     *
     * @param ruleSetHeader
     * @param ruleSet
     * @param ruleId
     * @param userId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperatorLogDataPersistence(operatorType = ConstantUtil.OPERATE_PUBLISH_RULE_SET_VERSION, logType = "1")
    public ResponseResult publish(RuleSetHeader ruleSetHeader,
                                  RuleSet ruleSet, String ruleId,
                                  final String folderId, String userId) {

        final String ruleSetContent = ruleSet.getRuleSetContent();

        if (StringUtils.isBlank(ruleSetContent)) {
            throw new IllegalArgumentException("参数[ruleSetContent]不能为空");
        }

        // 检查引用的变量是否存在私有的
        if (checkExitsPrivateParam(ruleSetContent, folderId)) {
            throw new RuntimeException("发布失败，规则集中存在对私有参数的引用");
        }

        String ruleSetHeaderId = ruleSetHeader.getRuleSetHeaderId();
        if (StringUtils.isBlank(ruleSetHeaderId)) {
            // 新建规则集
            String ruleSetName = ruleSetHeader.getRuleSetName();
            String ruleSetGroupId = ruleSetHeader.getRuleSetGroupId();
            if (StringUtils.isBlank(ruleSetName)) {
                throw new IllegalArgumentException("参数[ruleSetName]不能为空");
            }
            if (StringUtils.isBlank(ruleSetGroupId)) {
                throw new IllegalArgumentException("参数[ruleSetGroupId]不能为空");
            }
            ResponseResult createHeaderResponse = createRuleSetHeader(ruleSetHeader, userId);
            if (ResponseResult.SUCCESS_STAUS != createHeaderResponse.getStatus()) {
                throw new RuntimeException("创建规则集失败。" + createHeaderResponse.getMsg());
            }
            ruleSetHeaderId = (String) createHeaderResponse.getData();
        }

        // 新建版本
        ruleSet.setRuleSetHeaderId(ruleSetHeaderId);
        ruleSet.setEnable(RuleSet.ENABLE);  // 因为模型需要引用，所以默认为启用状态
        ruleSet.setVersion(generateVersion(ruleSet.getRuleSetHeaderId()));
        ResponseResult createRuleSetResponse = createRuleSet(ruleSet, userId);
        if (ResponseResult.SUCCESS_STAUS != createRuleSetResponse.getStatus()) {
            throw new RuntimeException("创建规则集新版本失败。" + createRuleSetResponse.getMsg());
        }
        String ruleSetId = (String) createRuleSetResponse.getData();

        // 插入模型-规则集引用关系
        final RuleSetRule ruleSetRule = new RuleSetRule();
        ruleSetRule.setRuleId(ruleId);
        ruleSetRule.setRuleSetId(ruleSetId);
        daoHelper.insert(_RULE_SET_RULE_PREFIX + "insertSelective", ruleSetRule);

        return ResponseResult.createSuccessInfo("", ruleSetId);
    }

    // ------------------------ 规则集组管理 ------------------------

    // 获取参数组列表
    @Override
    public ResponseResult getRuleSetGroups(String ruleSetGroupName) {
        final List<Object> list = daoHelper.queryForList(_RULE_SET_GROUP_MAPPER +
                "getByGroupName", ruleSetGroupName);
        return ResponseResult.createSuccessInfo("success", list);
    }

    @Override
    public List<String> getKpiByRuleSetId(String ruleSetId) {
        final List<String> list = daoHelper.queryForList(_RULE_SET_GROUP_MAPPER +
                "getKpiByRuleSetId", ruleSetId);
        return list;
    }

    @Override
    public List<String> getModelByRuleSetId(String ruleSetId){
         final List<String> list = daoHelper.queryForList(_RULE_DETAIL +
                "getModelByRuleSetId",  ruleSetId);
        return list;
    }

    @Override
    public Map<String, Object> getRuleSetGroupsPaged(String ruleSetGroupName, String start, String length) {
        final Map<String, Object> map = daoHelper.queryForPageList(_RULE_SET_GROUP_MAPPER +
                "getByGroupName", ruleSetGroupName, start, length);
        return map;
    }

    @Override
    public boolean checkGroupNameIsExist(String ruleSetGroupName, @Nullable String ruleSetGroupId) {
        Map<String, String> param = new HashMap<>(2);
        param.put("ruleSetGroupName", ruleSetGroupName);
        param.put("ruleSetGroupId", ruleSetGroupId);
        int obj = (int) daoHelper.queryOne(_RULE_SET_GROUP_MAPPER + "countByName", param);
        if (obj > 0) {
            return true;
        }
        return false;
    }

    private String generateVersion(String ruleSetHeaderId) {
        String version = (String) daoHelper.queryOne(_RULE_SET_MAPPER + "getMaxVersion", ruleSetHeaderId);
        if (null != version) {
            BigDecimal big100 = BigDecimal.valueOf(100f);
            return String.valueOf(BigDecimal.valueOf(Double.parseDouble(version)).multiply(big100).add(BigDecimal.valueOf(1f)).divide(big100));
        } else {
            return "1.0";
        }
    }

    @Override
    @Transactional
    public ResponseResult createRuleSetGroup(RuleSetGroup ruleSetGroup, String userId) {
        if (ruleSetGroup == null) {
            return ResponseResult.createFailInfo("请求参数[ruleSetGroup]为空");
        }

        if (checkGroupNameIsExist(ruleSetGroup.getRuleSetGroupName(), null)) {
            return ResponseResult.createFailInfo("规则集组名称已存在");
        }

        ruleSetGroup.setRuleSetGroupId(IdUtil.createId());
        ruleSetGroup.setCreateDate(new Date());
        ruleSetGroup.setCreatePerson(userId);
        daoHelper.insert(_RULE_SET_GROUP_MAPPER + "insertSelective", ruleSetGroup);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional
    public ResponseResult updateRuleSetGroup(RuleSetGroup ruleSetGroup, String userId) {
        if (ruleSetGroup == null) {
            return ResponseResult.createFailInfo("请求参数[ruleSetGroup]不能为空");
        }

        if (checkGroupNameIsExist(ruleSetGroup.getRuleSetGroupName(), ruleSetGroup.getRuleSetGroupId())) {
            return ResponseResult.createFailInfo("规则集组名称已存在");
        }

        ruleSetGroup.setUpdateDate(new Date());
        ruleSetGroup.setUpdatePerson(userId);
        daoHelper.update(_RULE_SET_GROUP_MAPPER + "updateByPrimaryKeySelective", ruleSetGroup);
        return ResponseResult.createSuccessInfo();
    }

    // 当前规则集组是否在使用中
    @Override
    public boolean isGroupUsed(String ruleSetGroupId) {
        int obj = (int) daoHelper.queryOne(_RULE_SET_GROUP_MAPPER +
                "countGroupUsed", ruleSetGroupId);
        if (obj > 0) {
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public ResponseResult deleteRuleSetGroup(String ruleSetGroupId) {
        if (StringUtils.isBlank(ruleSetGroupId)) {
            return ResponseResult.createFailInfo("请求参数[ruleSetGroupId]不能为空");
        }

        if (isGroupUsed(ruleSetGroupId)) {
            return ResponseResult.createFailInfo("当前规则集组正在使用中，不能删除");
        }

        daoHelper.delete(_RULE_SET_GROUP_MAPPER + "deleteByPrimaryKey", ruleSetGroupId);
        return ResponseResult.createSuccessInfo();
    }

    /**
     * 根據ruleSetHeader获取版本信息
     * @param ruleSetHeaderId id
     * @return 键值对信息的list
     */
    @Override
    public List<Map<String, String>> getRuleSetIdByHeader(String ruleSetHeaderId) {
        return daoHelper.queryForList(_RULE_SET_MAPPER + "getRuleSetIdByHeader", ruleSetHeaderId);
    }

}
