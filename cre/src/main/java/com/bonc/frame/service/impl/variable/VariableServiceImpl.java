package com.bonc.frame.service.impl.variable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bonc.frame.config.Config;
import com.bonc.frame.dao.DaoHelper;
import com.bonc.frame.entity.api.ApiConf;
import com.bonc.frame.entity.commonresource.ApiGroup;
import com.bonc.frame.entity.commonresource.RuleSet;
import com.bonc.frame.entity.commonresource.RuleSetGroup;
import com.bonc.frame.entity.commonresource.RuleSetHeader;
import com.bonc.frame.entity.commonresource.VariableGroup;
import com.bonc.frame.entity.commonresource.VariableGroupExt;
import com.bonc.frame.entity.rule.RuleDetail;
import com.bonc.frame.entity.rule.RuleDetailWithBLOBs;
import com.bonc.frame.entity.rulefolder.RuleFolder;
import com.bonc.frame.entity.variable.Variable;
import com.bonc.frame.entity.variable.VariableExt;
import com.bonc.frame.entity.variable.VariableTreeNode;
import com.bonc.frame.entity.variableentity.VariableEntity;
import com.bonc.frame.entity.variableentityrelation.VariableEntityRelation;
import com.bonc.frame.module.lock.distributed.curator.CuratorMutexLock;
import com.bonc.frame.module.lock.distributed.curator.LockDataType;
import com.bonc.frame.security.ResourceType;
import com.bonc.frame.service.api.ApiService;
import com.bonc.frame.service.auth.AuthorityService;
import com.bonc.frame.service.rule.RuleDetailService;
import com.bonc.frame.service.ruleSetBase.RuleSetBaseService;
import com.bonc.frame.service.syslog.SysLogService;
import com.bonc.frame.service.variable.VariableService;
import com.bonc.frame.service.variableentity.VariableEntityService;
import com.bonc.frame.service.variableentityrelation.VariableEntityRelationService;
import com.bonc.frame.util.*;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import javax.annotation.Resource;

import java.util.*;

@Service("variableService")
public class VariableServiceImpl implements VariableService {
    Log log = LogFactory.getLog(VariableServiceImpl.class);

    private final String _MYBITSID_PREFIX = "com.bonc.frame.dao.variable.VariableMapper.";
    private final String _MYBITSID_KIND_PREFIX = "com.bonc.frame.mapper.oracle.variable.VariableKindMapper.";

    /**
     * ???????????????
     */
    private final String _MYBITSID_VG_PREFIX = "com.bonc.frame.mapper.resource.VariableGroupMapper.";

    @Autowired
    private DaoHelper daoHelper;
    @Autowired
    private VariableEntityService variableEntityService;
    @Autowired
    private VariableEntityRelationService variableEntityRelationService;
    @Autowired
    private RuleDetailService ruleDetailService;
    @Autowired
    private AuthorityService authorityService;
    @Autowired
    private RuleSetBaseService ruleSetService;
    @Autowired
    private ApiService apiService;

    @Autowired
    private SysLogService sysLogService;

    // ?????????????????????
    private int countUsedByVariableId(String variableId) {
        final int count = (int) daoHelper.queryOne(_MYBITSID_PREFIX + "countUsedByVariableId", variableId);
        return count;
    }

    private int countEntityUsedByVariableId(String entityId) {
        final int count = (int) daoHelper.queryOne(_MYBITSID_PREFIX + "countEntityUsedByVariableId", entityId);
        return count;
    }

    public boolean isUsed(Variable variable) {
        int count = 0;
        if (variable.getTypeId().equals(ConstantUtil.VARIABLE_ENTITY_TYPE)) {
            count = countEntityUsedByVariableId(variable.getEntityId());
        } else {
            count = countUsedByVariableId(variable.getVariableId());
        }

        if (count > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isUsed(String variableId) {
        if (!Config.CRE_REFERENCE_CHECK_ENABLE) {
            return false;
        }
        final Variable variable = selectVariableByKey(variableId);
        if (isUsed(variable)) {
            //return true;
        }
        return false;
    }

    @Override
    public ResponseResult checkUsed(String variableId) {
    	
    		
		final Variable variable = selectVariableByKey(variableId);
    	//????????????????????????
        List<RuleDetailWithBLOBs> ruleList = ruleDetailService.getRuleDetailWhthBOLOBListByFolderId(variable.getVariableGroupId());
        String model = "";
        String rule ="";
        if (ruleList != null) {
            for (RuleDetailWithBLOBs ruleDetailWithBLOBs : ruleList) {
            	//model += ruleDetailWithBLOBs.getModuleName()+",";
            	String targetString = ruleDetailWithBLOBs.getRuleContent();
            	JSONObject jsonObject = JSONObject.parseObject(targetString);
            	JSONObject states = jsonObject.getJSONObject("states");
        		for (Map.Entry entry : states.entrySet()) {
        			JSONObject rect = (JSONObject) entry.getValue();
        			if(!rect.getString("type").equals("task")) {
        				continue;
        			}
        			JSONObject props = rect.getJSONObject("props");
        			String text = props.getJSONObject("text").getString("value");
        			JSONObject action = props.getJSONObject("action");
        			JSONArray array = action.getJSONArray("value");
        			if(array==null)
        				continue;
        			for(int i=0;i<array.size();i++) {
        				JSONObject value = (JSONObject) array.get(i);
        				String jsonStr = JSONObject.toJSONString(value);
        				String actRuleName = value.getString("actRuleName")+"("+ruleDetailWithBLOBs.getModuleName()+")";
        				if(jsonStr.indexOf(variableId)!=-1) {
        					rule += actRuleName+"\n,";
        				}
        			}
        		}
            }
        }
        List<RuleDetailWithBLOBs> ruleList2 = ruleDetailService.getRuleDetailWhthBOLOBListByFolderId("0000000000000000000000000000001");
        if (ruleList2 != null) {
            for (RuleDetailWithBLOBs ruleDetailWithBLOBs : ruleList2) {
            	//model += ruleDetailWithBLOBs.getModuleName()+",";
            	String targetString = ruleDetailWithBLOBs.getRuleContent();
            	JSONObject jsonObject = JSONObject.parseObject(targetString);
            	JSONObject states = jsonObject.getJSONObject("states");
        		for (Map.Entry entry : states.entrySet()) {
        			JSONObject rect = (JSONObject) entry.getValue();
        			if(!rect.getString("type").equals("task")) {
        				continue;
        			}
        			JSONObject props = rect.getJSONObject("props");
        			String text = props.getJSONObject("text").getString("value");
        			JSONObject action = props.getJSONObject("action");
        			JSONArray array = action.getJSONArray("value");
        			if(array==null)
        				continue;
        			for(int i=0;i<array.size();i++) {
        				JSONObject value = (JSONObject) array.get(i);
        				String jsonStr = JSONObject.toJSONString(value);
        				String actRuleName = value.getString("actRuleName")+"("+ruleDetailWithBLOBs.getModuleName()+")";
        				if(jsonStr.indexOf(variableId)!=-1) {
        					rule += actRuleName+"\n,";
        				}
        			}
        		}
            }
        }
        if(!"".equals(rule)) {
        	rule = rule.substring(0,rule.length()-1);
        	return ResponseResult.createFailInfo("?????????????????????,???????????????:"+rule);
        }
    		
        
        return ResponseResult.createSuccessInfo();
    }

    /**
     * ?????????????????????????????????
     *
     * @param variableId    ?????????????????????null
     * @param variableAlias
     * @param variableCode
     * @param folderId      ?????????????????????????????????null
     * @return
     */
    private boolean isAliasOrCodeDuplicate(@Nullable String variableId,
                                           String variableAlias,
                                           String variableCode,
                                           @Nullable String folderId) {
        if (!Config.CRE_REFERENCE_CHECK_ENABLE) {
            return false;
        }
        Map<String, String> param = new HashMap<>();
        param.put("variableId", variableId);
        param.put("variableAlias", variableAlias);
        param.put("variableCode", variableCode);
        param.put("folderId", folderId);

        int count = (int) daoHelper.queryOne(_MYBITSID_PREFIX + "countAliasOrCode", param);
        if (count > 0) {
            return true;
        }
        return false;
    }

    private boolean isAliasOrCodeDuplicate(Variable variable) {
        return isAliasOrCodeDuplicate(variable.getVariableId(),
                variable.getVariableAlias(),
                variable.getVariableCode(),
                variable.getFolderId());
    }

    @Override
    public Map<String, Object> selectVariables(Variable variable, String start, String size) {
        Map<String, Object> result = this.daoHelper.queryForPageList(_MYBITSID_PREFIX + "selectByFolder",
                variable, start, size);
        return result;
    }

    @Override
    public Map<String, Object> selectVariablesByFolderId(Variable variable, String start, String size) {
        Map<String, Object> result = this.daoHelper.queryForPageList(_MYBITSID_PREFIX +
                "selectVariablesByFolderId", variable, start, size);
        return result;
    }

    @Override
    public List<Variable> selectVariableByVariableCodeList(List<String> variableCodeList, String folderId, List<String> type) {

        if (variableCodeList == null || variableCodeList.isEmpty()) {
            return Collections.emptyList();
        }
        Map<String, Object> param = new HashMap<>();
        param.put("variableCodeList", variableCodeList);
        param.put("folderId", folderId);
        param.put("type", type);
        return daoHelper.queryForList(_MYBITSID_PREFIX +
                "selectVariableByCodeList", param);
    }

    @Override
    public Variable selectVariableByVariableCode(String variableId, String variableCode, String variableAlias, String entityId, String isPlulic, String folderId, List<String> type) {
        if (StringUtils.isBlank(variableId) && StringUtils.isBlank(variableCode) && StringUtils.isBlank(variableAlias) &&
                StringUtils.isBlank(entityId) && StringUtils.isBlank(folderId) && CollectionUtil.isEmpty(type)) { // ???????????????null??????,??????null
            return null;
        }
        Map<String, Object> param = new HashMap<>();
        param.put("variableId", variableId);
        param.put("variableCode", variableCode);
        param.put("variableAlias", variableAlias);//VARIABLE_ALIAS
        param.put("entityId", entityId);//ENTITY_ID
        param.put("isPlulic", isPlulic);
        param.put("folderId", folderId);
        param.put("type", type);
        return (Variable) daoHelper.queryOne(_MYBITSID_PREFIX +
                "selectVariableByCodeList", param);
    }

    @Override
    public Map<String, Object> selectFlatVariablesByFolderId(Variable variable, String start, String size) {
        Map<String, Object> result = this.daoHelper.queryForPageList(_MYBITSID_PREFIX +
                "selectFlatVariablesByFolderId", variable, start, size);
        return result;
    }

    @Override
    public Variable selectVariableByKey(String variableId) {
        Variable variable = (Variable) this.daoHelper.queryOne(_MYBITSID_PREFIX + "selectByPrimaryKey",
                variableId);
        return variable;
    }

    @Override
    public List<Variable> selectVariableByVariableIdList(List<String> variableIdList) {
        if (variableIdList == null || variableIdList.isEmpty()) {
            return Collections.emptyList();
        }
        Map<String, Object> param = new HashMap<>();
        param.put("variableIdList", variableIdList);
        return daoHelper.queryForList(_MYBITSID_PREFIX +
                "selectVariableByPrimaryKeyBatch", param);
    }

    public Map<String, Variable> selectVariableByKeyBatch(List<String> variableIdList) {
        if (CollectionUtil.isEmpty(variableIdList)) {
            return new HashMap<>();
        }

        Map<String, Object> param = new HashMap<>();
        param.put("variableIdList", variableIdList);
        List<Variable> variableList = daoHelper.queryForList(_MYBITSID_PREFIX +
                "selectVariableByPrimaryKeyBatch", param);

        if (CollectionUtil.isEmpty(variableList)) {
            return new HashMap<>();
        }
        Map<String, Variable> results = new HashMap<>(variableList.size());
        for (Variable variable : variableList) {
            if (variable != null) {
                results.put(variable.getVariableId(), variable);
            }
        }
        return results;
    }

    @Override
    @CuratorMutexLock(value = {"variableAlias", "variableCode"}, lockAlias = {"name", "code"}, lockDataType = LockDataType.VARIABLE)
    public ResponseResult insertVariable(Variable variable) {
        // ??????????????????????????????????????????
        ResponseResult result = new ResponseResult();
        if (checkNameOrCodeExistInFolder(variable, result)) {
            return result;
        }

        if (variable.getTypeId().equals(ConstantUtil.VARIABLE_ENTITY_TYPE)) {
            String entityId = IdUtil.createId();
            variable.setEntityId(entityId);
//            VariableEntity variableEntity = new VariableEntity();
//            variableEntity.setCreateDate(variable.getCreateDate());
//            variableEntity.setCreatePerson(variable.getCreatePerson());
//            variableEntity.setEntityId(entityId);
//            this.variableEntityService.insertVariableEntity(variableEntity);
        }

        final String id = IdUtil.createId();
        variable.setVariableId(id);
        variable.setVariableAlias(variable.getVariableAlias().trim());
        variable.setVariableCode(variable.getVariableCode().trim());
//        this.daoHelper.insert(_MYBITSID_PREFIX + "insertSelective", variable);
//
//        sysLogService.logOperate(ConstantUtil.OPERATE_CREATE_PARAM,
//                JSON.toJSONString(variable));
        instreVariableDataPersistence(variable);

        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void instreVariableDataPersistence(Variable variable) {
        if (variable != null) {
            String currentUser = ControllerUtil.getCurrentUser();
            variable.setCreateDate(new Date());
            variable.setCreatePerson(currentUser);
            if (variable.getTypeId().equals(ConstantUtil.VARIABLE_ENTITY_TYPE)) {
                String entityId = variable.getEntityId();
                VariableEntity variableEntity = new VariableEntity();
                variableEntity.setCreateDate(variable.getCreateDate());
                variableEntity.setCreatePerson(variable.getCreatePerson());
                variableEntity.setEntityId(entityId);
                this.variableEntityService.insertVariableEntity(variableEntity);
            }
            this.daoHelper.insert(_MYBITSID_PREFIX + "insertSelective", variable);
            sysLogService.logOperate(ConstantUtil.OPERATE_CREATE_PARAM,
                    JSON.toJSONString(variable));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void instreVariableTreeNodeDataPersistence(VariableTreeNode variable) {
        if (variable != null) {
            String currentUser = ControllerUtil.getCurrentUser();
            variable.setCreateDate(new Date());
            variable.setCreatePerson(currentUser);
            String parentEntityId = variable.getParentEntityId();
            if (variable.getTypeId().equals(ConstantUtil.VARIABLE_ENTITY_TYPE)) {
                String entityId = variable.getEntityId();
                VariableEntity variableEntity = new VariableEntity();
                variableEntity.setCreateDate(variable.getCreateDate());
                variableEntity.setCreatePerson(variable.getCreatePerson());
                variableEntity.setEntityId(entityId);
                variableEntity.setEntityPid(parentEntityId);
                this.variableEntityService.insertVariableEntity(variableEntity);
            }
            this.daoHelper.insert(_MYBITSID_PREFIX + "insertSelective", variable);
            if(StringUtils.isNotBlank(parentEntityId)){
                VariableEntityRelation variableEntityRelation = new VariableEntityRelation();
                variableEntityRelation.setEntityId(parentEntityId);
                variableEntityRelation.setVariableId(variable.getVariableId());
                variableEntityRelation.setEntityVariableAlias(variable.getVariableCode());
                this.variableEntityRelationService.insertVariableEntityRelation(variableEntityRelation);
            }
//            Set<String> variableNestedIdList = variable.getVariableNestedIdList();
//            if (!CollectionUtil.isEmpty(variableNestedIdList)) {
//                for (String variableNestedId : variableNestedIdList) {
//                    VariableEntityRelation variableEntityRelation = new VariableEntityRelation();
//                    variableEntityRelation.setEntityId(variable.getEntityId());
//                    variableEntityRelation.setVariableId(variableNestedId);
//                    variableEntityRelation.setEntityVariableAlias(variable.getAliasCode());
//                    this.variableEntityRelationService.insertVariableEntityRelation(variableEntityRelation);
//                }
//            }
            sysLogService.logOperate(ConstantUtil.OPERATE_CREATE_PARAM,
                    JSON.toJSONString(variable));
        }
    }

    public boolean needLock(Variable variable) {
        Variable oldVariable = this.selectVariableByKey(variable.getVariableId());
        if (variable.getVariableCode().equals(oldVariable.getVariableCode()) && variable.getVariableAlias().equals(oldVariable.getVariableAlias())) {
            return false;
        }
        return true;
    }

    @Override
    @CuratorMutexLock(value = {"variableAlias", "variableCode"}, lockAlias = {"name", "code"}, lockDataType = LockDataType.VARIABLE,
            addLockCondition = "#{#myService.needLock(#variable)}")
    public ResponseResult updateVariable(Variable variable) {
        ResponseResult result = new ResponseResult();
        result = ruleDetailService.checkCanModify(variable.getVariableId(), variable.getFolderId());
        if (result.getStatus() == ResponseResult.ERROR_STAUS) {
            return result;
        }
        if (checkNameOrCodeExist(variable, result)) {
            return result;
        }

        if (variable.getTypeId().equals(ConstantUtil.VARIABLE_ENTITY_TYPE) && StringUtils.isBlank(variable.getEntityId())) {
            String entityId = IdUtil.createId();
            variable.setEntityId(entityId);
            VariableEntity variableEntity = new VariableEntity();
            variableEntity.setCreateDate(variable.getCreateDate());
            variableEntity.setCreatePerson(variable.getCreatePerson());
            variableEntity.setEntityId(entityId);
            this.variableEntityService.insertVariableEntity(variableEntity);
        }
        variable.setVariableAlias(variable.getVariableAlias().trim());
        variable.setVariableCode(variable.getVariableCode().trim());
        daoHelper.update(_MYBITSID_PREFIX + "updateByPrimaryKeySelective", variable);

        // FIXME: ???????????????????????????
        if (variable.getTypeId().equals(ConstantUtil.VARIABLE_ENTITY_TYPE)) {
            daoHelper.update(_MYBITSID_PREFIX + "updateNestedVariablesKind", variable);
        }

        sysLogService.logOperate(ConstantUtil.OPERATE_UPDATE_PARAM,
                JSON.toJSONString(variable));

        return ResponseResult.createSuccessInfo();
    }

    @Override
    public List<Map<String, Object>> getVariableType() {
        List<Map<String, Object>> list = this.daoHelper.queryForList(_MYBITSID_PREFIX + "selectVariableType", null);
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult deleteVariable(Variable variable) {
        if (isUsed(variable.getVariableId())) {
            return ResponseResult.createFailInfo("???????????????????????????????????????");
        }

        ResponseResult result = ruleDetailService.checkCanModify(variable.getVariableId(), variable.getFolderId());
        if (result.getStatus() == ResponseResult.ERROR_STAUS) {
            return result;
        }

        // ????????????????????????????????????
        authorityService.deleteByResourceId(variable.getVariableId(), ResourceType.DATA_PUB_VARIABLE.getType());

        VariableEntityRelation variableEntityRelation = new VariableEntityRelation();
        variableEntityRelation.setEntityId(variable.getEntityId());     // ????????????????????????????????????null
        variableEntityRelation.setVariableId(variable.getVariableId());
        daoHelper.delete(_MYBITSID_PREFIX + "deleteByPrimaryKey", variable);
        this.variableEntityRelationService.deleteVariableEntityRelation(variableEntityRelation);
        this.variableEntityService.deleteVariableEntity(variable.getEntityId());

        sysLogService.logOperate(ConstantUtil.OPERATE_DELETE_PARAM,
                JSON.toJSONString(variable));

        return ResponseResult.createSuccessInfo();
    }

    @Override
    public Map<String, Object> selectVariableEntityRelation(String entityId, String start, String size) {
        Map<String, String> param = new HashMap<>();
        param.put("entityId", entityId);
        final Map<String, Object> map = this.daoHelper.queryForPageList(_MYBITSID_PREFIX +
                "selectVariableEntityRelation", param, start, size);
        return map;
    }

    @Override
    @CuratorMutexLock(value = {"variableAlias", "variableCode"}, lockAlias = {"name", "code"},
            lockDataType = LockDataType.VARIABLE)
    public ResponseResult insertVariableRelation(Variable variable, VariableEntity variableEntity, VariableEntityRelation variableEntityRelation) {
        if (isAliasOrCodeDuplicate(variable)) {
            return ResponseResult.createFailInfo("?????????????????????????????????");
        }

        String variableId = IdUtil.createId();
        variable.setVariableId(variableId);
        variable.setVariableAlias(variable.getVariableAlias().trim());
        variable.setVariableCode(variable.getVariableCode().trim());
        if (ConstantUtil.VARIABLE_ENTITY_TYPE.equals(variable.getTypeId())) {
            String entityId = IdUtil.createId();
            variable.setEntityId(entityId);
            variableEntityRelation.setEntityId(entityId);
            variableEntity.setCreateDate(variable.getCreateDate());
            variableEntity.setCreatePerson(variable.getCreatePerson());
            variableEntity.setEntityId(entityId);
            this.variableEntityService.insertVariableEntity(variableEntity);
        }
        variableEntityRelation.setEntityId(variableEntity.getEntityPid());
        variableEntityRelation.setVariableId(variableId);
        variableEntityRelation.setEntityVariableAlias(variableEntityRelation.getEntityVariableAlias().trim());
        this.daoHelper.insert(_MYBITSID_PREFIX + "insertSelective", variable);
        this.variableEntityRelationService.insertVariableEntityRelation(variableEntityRelation);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult updateVariableRelation(Variable variable, VariableEntity variableEntity, VariableEntityRelation variableEntityRelation) {
        if (isAliasOrCodeDuplicate(variable)) {
            return ResponseResult.createFailInfo("?????????????????????????????????");
        }

        //????????????????????????????????????,??????????????????????????????????????????????????????????????????????????????
        if (variable.getTypeId().equals(ConstantUtil.VARIABLE_ENTITY_TYPE) && StringUtils.isBlank(variable.getEntityId())) {
            String entityId = IdUtil.createId();
            variable.setEntityId(entityId);
            variableEntity.setCreateDate(variable.getCreateDate());
            variableEntity.setCreatePerson(variable.getCreatePerson());
            variableEntity.setEntityId(entityId);
            this.variableEntityService.insertVariableEntity(variableEntity);
        }
        variableEntityRelation.setEntityId(variableEntity.getEntityPid());
        variable.setVariableAlias(variable.getVariableAlias().trim());
        variable.setVariableCode(variable.getVariableCode().trim());
        this.daoHelper.update(_MYBITSID_PREFIX + "updateByPrimaryKeySelective", variable);
        this.variableEntityRelationService.updateVariableEntityRelation(variableEntityRelation);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult deleteVariableRelation(String variableId, String entityId) {

        if (isUsed(variableId)) {
            return ResponseResult.createFailInfo("???????????????????????????????????????");
        }

        Variable variable = new Variable();
        variable.setVariableId(variableId);
        variable.setEntityId(entityId);
        daoHelper.delete(_MYBITSID_PREFIX + "deleteByPrimaryKey", variable);
        VariableEntityRelation variableEntityRelation = new VariableEntityRelation();
        variableEntityRelation.setEntityId(entityId);
        variableEntityRelation.setVariableId(variableId);
        this.variableEntityRelationService.deleteVariableEntityRelation(variableEntityRelation);
        this.variableEntityService.deleteVariableEntity(entityId);
        return ResponseResult.createSuccessInfo();
    }

    // ?????????????????????
    @Override
    public Map<String, Object> getPubVariableTree() {
        // ??????????????????
        final Map<String, Object> pubVariableTree = buildPubVariableTree();
        List<Map<String, Object>> outVariableTree = new ArrayList<Map<String, Object>>();

        outVariableTree.add(pubVariableTree);
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("outVariableTree", outVariableTree);
        return result;
    }

    // ????????????????????????????????????
    @Override
    public Map<String, Object> getPubVariableAndKpiTree() {
        // ??????????????????
        final Map<String, Object> pubVariableTree = buildPubVariableTree();
        List<Map<String, Object>> variableAndKpiTree = new ArrayList<Map<String, Object>>();

        // ????????????
        Map<String, Object> kpiMap = new HashMap(3);
        kpiMap.put("id", ConstantUtil.KPI);
        kpiMap.put("title", "????????????");
        kpiMap.put("name", "????????????");

        variableAndKpiTree.add(pubVariableTree);
        variableAndKpiTree.add(kpiMap);
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("variableAndKpiTree", variableAndKpiTree);
        return result;
    }


    //???????????????
    @Override
    public Map<String, Object> getVariableTree(String folderId) {
        List<Map<String, Object>> inVariableTree = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> outVariableTree = new ArrayList<Map<String, Object>>();

        List<Map<String, Object>> inVariableList = buildVariableTree(folderId, ConstantUtil.VARIABLE_KIND_INPUT);
        if (inVariableList != null && !inVariableList.isEmpty()) {
            Map<String, Object> inVariableMap = new HashMap<String, Object>();
            inVariableMap.put("id", ConstantUtil.VARIABLE_KIND_INPUT);
            inVariableMap.put("title", "????????????");
            inVariableMap.put("name", "????????????");
            inVariableMap.put("children", inVariableList);
            inVariableTree.add(inVariableMap);
        }

        List<Map<String, Object>> tempVariableList = buildVariableTree(folderId, ConstantUtil.VARIABLE_KIND_TEMP);
        if (tempVariableList != null && !tempVariableList.isEmpty()) {
            Map<String, Object> tempVariableMap = new HashMap<String, Object>();
            tempVariableMap.put("id", ConstantUtil.VARIABLE_KIND_TEMP);
            tempVariableMap.put("title", "????????????");
            tempVariableMap.put("name", "????????????");
            tempVariableMap.put("children", tempVariableList);
            outVariableTree.add(tempVariableMap);
        }

        List<Map<String, Object>> sysConstVariableList = buildVariableTree(folderId, ConstantUtil.VARIABLE_KIND_SYS_CONSTANT);

        if (sysConstVariableList != null && !sysConstVariableList.isEmpty()) {
            Map<String, Object> sysConstVariableMap = new HashMap<String, Object>();
            sysConstVariableMap.put("id", ConstantUtil.VARIABLE_KIND_SYS_CONSTANT);
            sysConstVariableMap.put("title", "????????????");
            sysConstVariableMap.put("name", "????????????");
            sysConstVariableMap.put("children", sysConstVariableList);
            inVariableTree.add(sysConstVariableMap);
        }

        List<Map<String, Object>> outVariableList = buildVariableTree(folderId, ConstantUtil.VARIABLE_KIND_OUTPUT);
        if (outVariableList != null && !outVariableList.isEmpty()) {
            Map<String, Object> outVariableMap = new HashMap<String, Object>();
            outVariableMap.put("id", ConstantUtil.VARIABLE_KIND_OUTPUT);
            outVariableMap.put("title", "????????????");
            outVariableMap.put("name", "????????????");
            outVariableMap.put("children", outVariableList);
            outVariableTree.add(outVariableMap);
        }


        // ??????????????????
        final Map<String, Object> pubVariableTree = buildPubVariableTree();
        outVariableTree.add(pubVariableTree);

        Map<String, Object> result = new HashMap<String, Object>();
        inVariableTree.addAll(outVariableTree);

        // ????????????
        Map<String, Object> kpiMap = new HashMap(3);
        kpiMap.put("id", ConstantUtil.KPI);
        kpiMap.put("title", "????????????");
        kpiMap.put("name", "????????????");
        inVariableTree.add(kpiMap);

        result.put("inVariableTree", inVariableTree);
        result.put("outVariableTree", outVariableTree);
        return result;
    }

    //???????????????
    @Override
    public Map<String, Object> getVariableTree_new(String folderId) {
        List<Map<String, Object>> inVariableTree = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> outVariableTree = new ArrayList<Map<String, Object>>();

//        List<Map<String, Object>> inVariableList = buildVariableTree(folderId, ConstantUtil.VARIABLE_KIND_INPUT);
//        if (inVariableList != null && !inVariableList.isEmpty()) {
        Map<String, Object> inVariableMap = new HashMap<String, Object>();
        inVariableMap.put("id", ConstantUtil.VARIABLE_KIND_INPUT);
        inVariableMap.put("title", "????????????");
        inVariableMap.put("name", "????????????");
//            inVariableMap.add("children", inVariableList);
        inVariableTree.add(inVariableMap);
//        }

//        List<Map<String, Object>> tempVariableList = buildVariableTree(folderId, ConstantUtil.VARIABLE_KIND_TEMP);
//        if (tempVariableList != null && !tempVariableList.isEmpty()) {
        Map<String, Object> tempVariableMap = new HashMap<String, Object>();
        tempVariableMap.put("id", ConstantUtil.VARIABLE_KIND_TEMP);
        tempVariableMap.put("title", "????????????");
        tempVariableMap.put("name", "????????????");
//            tempVariableMap.add("children", tempVariableList);
        outVariableTree.add(tempVariableMap);
//        }

//        List<Map<String, Object>> sysConstVariableList = buildVariableTree(folderId, ConstantUtil.VARIABLE_KIND_SYS_CONSTANT);
//        if (sysConstVariableList != null && !sysConstVariableList.isEmpty()) {
        Map<String, Object> sysConstVariableMap = new HashMap<String, Object>();
        sysConstVariableMap.put("id", ConstantUtil.VARIABLE_KIND_SYS_CONSTANT);
        sysConstVariableMap.put("title", "????????????");
        sysConstVariableMap.put("name", "????????????");
//            sysConstVariableMap.add("children", sysConstVariableList);
        inVariableTree.add(sysConstVariableMap);
//        }

//        List<Map<String, Object>> outVariableList = buildVariableTree(folderId, ConstantUtil.VARIABLE_KIND_OUTPUT);
//        if (outVariableList != null && !outVariableList.isEmpty()) {
        Map<String, Object> outVariableMap = new HashMap<String, Object>();
        outVariableMap.put("id", ConstantUtil.VARIABLE_KIND_OUTPUT);
        outVariableMap.put("title", "????????????");
        outVariableMap.put("name", "????????????");
//            outVariableMap.add("children", outVariableList);
        outVariableTree.add(outVariableMap);
//        }


//        // ??????????????????
//        final Map<String, Object> pubVariableTree = buildPubVariableTree();
//        outVariableTree.add(pubVariableTree);

        Map<String, Object> result = new HashMap<String, Object>();
        inVariableTree.addAll(outVariableTree);

        // ????????????
        Map<String, Object> kpiMap = new HashMap(3);
        kpiMap.put("id", ConstantUtil.KPI);
        kpiMap.put("title", "????????????");
        kpiMap.put("name", "????????????");
        inVariableTree.add(kpiMap);

        result.put("inVariableTree", inVariableTree);
        result.put("outVariableTree", outVariableTree);
        return result;
    }

    // ????????????id??????????????????????????????
    private List<Map<String, Object>> buildVariableTree(String folderId, String kindId) {
        //????????????
        Map<String, Object> param = new HashMap<String, Object>();
        List<String> kindIds = new ArrayList<String>();
        kindIds.add(kindId);
        param.put("folderId", folderId);
        param.put("kindIds", kindIds);

        List<Variable> variableList = daoHelper.queryForList(_MYBITSID_PREFIX +
                "selectByFolderId", param);
        List<Map<String, String>> entityList = daoHelper.queryForList(_MYBITSID_PREFIX +
                "selectEntityByFolderId", param);
        List<Map<String, String>> entityRefList = daoHelper.queryForList(_MYBITSID_PREFIX +
                "selectEntityRefByFolderId", param);
        return buildVariableTree(variableList, entityList, entityRefList);
    }

    // ?????????????????????
    private Map<String, Object> buildPubVariableTree() {
        Map<String, Object> pubVariableMap = new HashMap<>();
        pubVariableMap.put("id", ConstantUtil.VARIABLE_KIND_PUB);
        pubVariableMap.put("title", "????????????");
        pubVariableMap.put("name", "????????????");

        // ??????????????????????????????
        final List<Map<String, Object>> pubVariableTreeWihtoutVariableGroupId = buildPubVariableTreeWihtoutVariableGroupId();

        final List<VariableGroup> variableGroupList = daoHelper.queryForList(_MYBITSID_VG_PREFIX + "listAll");
        List<Map<String, Object>> childern = new LinkedList<>();

        // FIXME: ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        if (variableGroupList != null && !variableGroupList.isEmpty()) {
            for (VariableGroup variableGroup : variableGroupList) {
                final String variableGroupId = variableGroup.getVariableGroupId();
                final String variableGroupName = variableGroup.getVariableGroupName();
                List<Map<String, Object>> groupVariableList = buildVariableTreeByVariableGroupId(variableGroupId);
                if (groupVariableList != null && !groupVariableList.isEmpty()) {
                    Map<String, Object> groupVariableMap = new HashMap<>();
                    groupVariableMap.put("id", variableGroupId);
                    groupVariableMap.put("title", variableGroupName);
                    groupVariableMap.put("name", variableGroupName);
                    groupVariableMap.put("children", groupVariableList);
                    childern.add(groupVariableMap);
                }
            }
        }

        childern.addAll(pubVariableTreeWihtoutVariableGroupId);
        pubVariableMap.put("children", childern);
        return pubVariableMap;
    }


    //??????apiId?????? ?????????????????????,?????????????????????,VariableTreeNode??????
    public List<VariableTreeNode> getApiReturnVariableTree(String apiId, String apiValueType) {
        //selectResultVariableByApiId
        Map<String, Object> selectValueParms = new HashMap<>();
        selectValueParms.put("apiId", apiId);
        selectValueParms.put("apiVariableType", apiValueType);
//        selectValueParms.add("kindId", kindId);
        // ????????????????????????
        List<VariableTreeNode> variableList = daoHelper.queryForList(_MYBITSID_PREFIX + "selectResultVariableByApiId", selectValueParms);

        List<VariableTreeNode> results = new ArrayList<>();

        for (VariableTreeNode variable : variableList) {
            if (ConstantUtil.VARIABLE_ENTITY_TYPE.equals(variable.getTypeId())) {
                String variableId = variable.getVariableId();
                String entityId = variable.getEntityId();
                if (StringUtils.isBlank(variableId) || StringUtils.isBlank(entityId)) {

                    continue;
                }
                Map<String, Object> selectNestparms = new HashMap<>();
                selectNestparms.put("variableId", variableId);
                selectNestparms.put("entityId", entityId);
//                selectNestparms.add("kindId", kindId);

                final List<VariableTreeNode> nestedvariableList = daoHelper.queryForList(_MYBITSID_PREFIX + "selectNestedVariable", selectNestparms);

                VariableTreeNode variableTree = buildVariableTree(nestedvariableList, variable);
                results.add(variableTree);
            } else {
                results.add(variable);
            }
        }

        return results;
    }

    /**
     * ????????????,?????????????????????????????????
     * VariableTreeNode.variableNestedIdList???????????????????????????????????????Id
     */
    public List<VariableTreeNode> selectVariableNodeWithNestedByVariableProperty(String variableId,
                                                                                 String entityId,
                                                                                 String variableAlias,
                                                                                 String variableCode,
                                                                                 String typeId,
                                                                                 String isPublic,
                                                                                 String folderId,
                                                                                 String variableGroupId) {
        Map<String, Object> selectVariableparms = new HashMap<>();
        selectVariableparms.put("variableId", variableId);
        selectVariableparms.put("entityId", entityId);
        selectVariableparms.put("variableAlias", variableAlias);
        selectVariableparms.put("variableCode", variableCode);
        selectVariableparms.put("typeId", typeId);
        selectVariableparms.put("isPublic", isPublic);
        selectVariableparms.put("folderId", folderId);
        selectVariableparms.put("variableGroupId", variableGroupId);
        List<VariableTreeNode> variableTreeNodeList =
                daoHelper.queryForList(_MYBITSID_PREFIX + "selectVariableTreeNodeByVariableProperty", selectVariableparms);
        if (CollectionUtil.isEmpty(variableTreeNodeList)) {
            return Collections.emptyList();
        }
        Map<String, VariableTreeNode> result = new HashMap<>();
        for (VariableTreeNode variableTreeNode : variableTreeNodeList) {
            if (variableTreeNode != null) {
                result.putAll(selectVariableTreeNodeWithNested(variableTreeNode));
            }
        }
        return new ArrayList<>(result.values());
    }

    /**
     * ??????????????????,?????????????????????????????????
     * VariableTreeNode.variableNestedIdList???????????????????????????????????????Id
     */
    @Override
    public List<VariableTreeNode> selectVariableTreeNodeWithNestedByVariableIdListy(List<String> variableIdList) {
        if (CollectionUtil.isEmpty(variableIdList)) {
            return Lists.emptyList();
        }
        Map<String, Object> selectVariableparms = new HashMap<>();
        selectVariableparms.put("variableIdList", variableIdList);
        List<VariableTreeNode> variableTreeNodeList = daoHelper.queryForList(_MYBITSID_PREFIX + "selectVariableTreeNodeByVariableProperty", selectVariableparms);
        if (CollectionUtil.isEmpty(variableTreeNodeList)) {
            return Collections.emptyList();
        }
        Map<String, VariableTreeNode> result = new HashMap<>();
        for (VariableTreeNode variableTreeNode : variableTreeNodeList) {
            if (variableTreeNode != null) {
                result.putAll(selectVariableTreeNodeWithNested(variableTreeNode));
            }
        }
        return new ArrayList<>(result.values());
    }

    public Map<String, VariableTreeNode> selectVariableTreeNodeWithNested(VariableTreeNode variableTreeNode) {
        Map<String, VariableTreeNode> result = new HashMap<>();
        if (variableTreeNode != null) {
            if (StringUtils.isNotBlank(variableTreeNode.getTypeId()) && ConstantUtil.VARIABLE_ENTITY_TYPE.equals(variableTreeNode.getTypeId())) {
                Map<String, Object> selectNestparms = new HashMap<>();
                selectNestparms.put("variableId", variableTreeNode.getVariableId());
                selectNestparms.put("entityId", variableTreeNode.getEntityId());

                List<VariableTreeNode> nestedvariableList = daoHelper.queryForList(_MYBITSID_PREFIX + "selectDirectNestedVariableWithOwn", selectNestparms);
                if (!CollectionUtil.isEmpty(nestedvariableList)) {
                    for (VariableTreeNode nestVariable : nestedvariableList) {
                        if (nestVariable == null) {
                            continue;
                        }
                        if (variableTreeNode.getVariableId().equals(nestVariable.getVariableId())) {
                            continue;
                        }
                        variableTreeNode.addVariableNestedIdList(nestVariable.getVariableId());
                        if (StringUtils.isNotBlank(nestVariable.getTypeId()) && ConstantUtil.VARIABLE_ENTITY_TYPE.equals(nestVariable.getTypeId())) {
                            Map<String, VariableTreeNode> variableTreeNodeMap1 = selectVariableTreeNodeWithNested(nestVariable);
                            if (variableTreeNodeMap1 != null && !variableTreeNodeMap1.isEmpty()) {
                                result.putAll(variableTreeNodeMap1);
                            }
                        }
                        result.put(nestVariable.getVariableId(), nestVariable);
                    }
                }
            }
            result.put(variableTreeNode.getVariableId(), variableTreeNode);
        }
        return result;
    }

    /**
     * ????????????,?????????????????????????????????
     * VariableTreeNode.variableNestedIdList???????????????????????????????????????Id
     */
    public List<VariableTreeNode> selectVariableNodeByVariableProperty(String variableId,
                                                                       String entityId,
                                                                       String variableAlias,
                                                                       String variableCode,
                                                                       String isPublic,
                                                                       String folderId,
                                                                       String variableGroupId) {
        Map<String, Object> selectVariableparms = new HashMap<>();
        selectVariableparms.put("variableId", variableId);
        selectVariableparms.put("entityId", entityId);
        selectVariableparms.put("folderId", folderId);
        selectVariableparms.put("variableAlias", variableAlias);
        selectVariableparms.put("variableCode", variableCode);
        selectVariableparms.put("isPublic", isPublic);
        selectVariableparms.put("variableGroupId", variableGroupId);
        return daoHelper.queryForList(_MYBITSID_PREFIX + "selectVariableTreeNodeByVariableProperty", selectVariableparms);
    }


    public VariableTreeNode buildVariableTree(VariableTreeNode variable) {
        if (ConstantUtil.VARIABLE_ENTITY_TYPE.equals(variable.getTypeId())) {
            String variableId = variable.getVariableId();
            String entityId = variable.getEntityId();
            Map<String, Object> selectNestparms = new HashMap<>();
            selectNestparms.put("variableId", variableId);
            selectNestparms.put("entityId", entityId);

            final List<VariableTreeNode> nestedvariableList = daoHelper.queryForList(_MYBITSID_PREFIX + "selectNestedVariable", selectNestparms);

            VariableTreeNode variableTree = buildVariableTree(nestedvariableList, variable);
            return variableTree;
        } else {
            return variable;
        }
    }


    private VariableTreeNode buildVariableTree(List<VariableTreeNode> nestedvariableList, VariableTreeNode firstVariableNode) {
        if (nestedvariableList == null || nestedvariableList.isEmpty() || firstVariableNode == null) {
            //FIXME: log.err ?????? ??????
            return null;
        }
        String firstNodeEntityId = firstVariableNode.getEntityId();
        if (StringUtils.isBlank(firstNodeEntityId)) {
            //FIXME: log.err ?????????
            return null;
        }
        Map<String, VariableTreeNode> entityMap = new HashMap<>();
        entityMap.put(firstNodeEntityId, firstVariableNode);
        //?????????????????????????????? ?????????????????????
        for (VariableTreeNode variableNode : nestedvariableList) {
            if (ConstantUtil.VARIABLE_ENTITY_TYPE.equals(variableNode.getTypeId())
                    && !StringUtils.isBlank(variableNode.getEntityId())) {
                entityMap.put(variableNode.getEntityId(), variableNode);
            }
        }
        //??????,????????????????????????????????????
        for (VariableTreeNode variableNode : nestedvariableList) {
            String parentEntityId = variableNode.getParentEntityId();
            if (!StringUtils.isBlank(parentEntityId)) {
                VariableTreeNode parentVariableNode = entityMap.get(parentEntityId);
                if (parentVariableNode != null) {
                    parentVariableNode.addVariableNestedList(variableNode);
                    parentVariableNode.addVariableNestedIdList(variableNode.getVariableId());
                }
            }
        }

        return entityMap.get(firstNodeEntityId);

    }

    // ????????????????????????????????????????????????
    private List<Map<String, Object>> buildPubVariableTreeWihtoutVariableGroupId() {
        List<Variable> variableList = daoHelper.queryForList(_MYBITSID_PREFIX +
                "selectPubByVariableGroupIdIsNull");
        List<Map<String, String>> entityList = daoHelper.queryForList(_MYBITSID_PREFIX +
                "selectPubEntityByVariableGroupIdIsNull2");
        List<Map<String, String>> entityRefList = daoHelper.queryForList(_MYBITSID_PREFIX +
                "selectPubEntityRefByVariableGroupIdIsNull");
        return buildVariableTree(variableList, entityList, entityRefList);
    }

    // ????????????????????????????????????????????????
    private List<Map<String, Object>> buildVariableTreeByVariableGroupId(String variableGroupId) {
        List<Variable> variableList = daoHelper.queryForList(_MYBITSID_PREFIX +
                "pubSelectVariablesByGroupId", variableGroupId);
        List<Map<String, String>> entityList = daoHelper.queryForList(_MYBITSID_PREFIX +
                "selectEntityByVariableGroupId2", variableGroupId);
        List<Map<String, String>> entityRefList = daoHelper.queryForList(_MYBITSID_PREFIX +
                "selectEntityRefByVariableGroupId", variableGroupId);
        return buildVariableTree(variableList, entityList, entityRefList);
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> buildVariableTree(List<Variable> variableList,
                                                        List<Map<String, String>> entityList,
                                                        List<Map<String, String>> entityRefList) {

        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        //????????????????????????????????????
        Map<String, Map<String, Object>> entityMap = new HashMap<String, Map<String, Object>>();
        for (Variable variable : variableList) {
            String entityId = variable.getEntityId();
            if (entityId != null && !entityId.isEmpty()) {    // ?????????????????????
                Map<String, Object> entityObj = new HashMap<String, Object>();
                entityObj.put("id", variable.getVariableId());
                entityObj.put("title", variable.getVariableAlias());
                entityObj.put("name", variable.getVariableAlias());
                List<Map<String, Object>> children = new ArrayList<Map<String, Object>>();
                entityObj.put("children", children);
                entityMap.put(entityId, entityObj);
            }
        }
        //???????????????????????????Map,key-??????ID???value-??????ID
        Map<String, String> vidEidMap = new HashMap<String, String>();
        for (Map<String, String> tempMap : entityRefList) {
            String entityId = tempMap.get("entityId");
            String variableId = tempMap.get("variableId");
            vidEidMap.put(variableId, entityId);
        }
        //????????????????????????
        for (Variable variable : variableList) {
            String entityId = variable.getEntityId();
            if (entityId != null && !entityId.isEmpty()) {
                continue;
            }
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", variable.getVariableId());
            map.put("title", variable.getVariableAlias());
            map.put("name", variable.getVariableAlias());
            map.put("typeId", variable.getTypeId());
            map.put("variableCode", variable.getVariableCode());
            entityId = vidEidMap.get(variable.getVariableId());
            if (entityId == null || entityId.isEmpty()) {
                result.add(map);
                continue;
            }
            Map<String, Object> entity = entityMap.get(entityId);
            if (entity == null) {
                continue;
            }
            List<Map<String, Object>> children = (List<Map<String, Object>>) entity.get("children");
            children.add(map);
        }

        boolean flag = true;
        Set<String> pidSet = new HashSet<String>();
        while (flag) {
            pidSet.clear();
            for (Map<String, String> tempMap : entityList) {
                String entityPid = tempMap.get("entityPid");
                if (entityPid != null && !entityPid.isEmpty()) {
                    pidSet.add(entityPid);
                }
            }
            for (int i = entityList.size() - 1; i >= 0; i--) {
                Map<String, String> tempMap = entityList.get(i);
                String entityId = tempMap.get("entityId");
                String entityPid = tempMap.get("entityPid");
                if (pidSet.contains(entityId)) {
                    continue;
                }
                Map<String, Object> childEntity = entityMap.get(entityId);
                Map<String, Object> parentEntity = entityMap.get(entityPid);
                if (entityPid == null) {
                    result.add(childEntity);
                } else if (parentEntity != null) {
                    List<Map<String, Object>> children = (List<Map<String, Object>>) parentEntity.get("children");
                    if (children != null) {
                        children.add(childEntity);
                    }
                }
                entityList.remove(i);
            }
            if (entityList.size() == 0) {
                flag = false;
            }
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getVariableKind() {
        return this.daoHelper.queryForList(_MYBITSID_KIND_PREFIX + "selectVariableKind", null);
    }

    @Override
    public List<Map<String, Object>> getEntityType(String folderId) {
        List<Map<String, Object>> list = this.daoHelper.queryForList(_MYBITSID_PREFIX + "selectEntityType", folderId);
        return list;
    }

    @Override
    @Transactional
    public ResponseResult isDeleteEntity(Variable variable) {
        String variableId = variable.getVariableId();
        List<RuleDetailWithBLOBs> list = this.ruleDetailService.getRuleBlobList(variable.getFolderId());
        if (list == null) {
            return ResponseResult.createSuccessInfo();
        }
        for (RuleDetailWithBLOBs rule : list) {
            String ruleContent = rule.getRuleContent();
            if (ruleContent.contains(variableId)) {
                return ResponseResult.createFailInfo("", "{ruleId:" + rule.getRuleId() + "}");
            }
        }

        return null;
    }

    /**
     * ???????????????????????????????????????????????????
     *
     * @return
     */
    private boolean checkAliasOrCodeIsExist(Variable variable, ResponseResult result) {
        if (!Config.CRE_REFERENCE_CHECK_ENABLE) {
            return false;
        }
        List<Variable> list = this.daoHelper.queryForList(_MYBITSID_PREFIX +
                "selectByFolder", variable);
        for (Variable v : list) {
            if (v.getVariableId().equals(variable.getVariableId())) {
                continue;
            }
            String variableCode = v.getVariableCode();
            String variableAlias = v.getVariableAlias();
            if (variable.getVariableAlias().equals(variableAlias)) {
                result.setStatus(ResponseResult.ERROR_STAUS);
                result.setMsg("variableAlias");
                return true;
            }
            if (variable.getVariableCode().equals(variableCode)) {
                result.setStatus(ResponseResult.ERROR_STAUS);
                result.setMsg("variableCode");
                return true;
            }
        }
        return false;
    }

    /**
     * ???????????????????????????????????????????????????
     *
     * @return
     */
    private boolean checkEntityAliasIsExist(Variable variable, String entityVariableAlias, String entityId, ResponseResult result) {
        if (!Config.CRE_REFERENCE_CHECK_ENABLE) {
            return false;
        }
        List<VariableExt> list = this.daoHelper.queryForList(_MYBITSID_PREFIX + "selectVariableEntityRelation", entityId);
        for (VariableExt v : list) {
            if (v.getVariableId().equals(variable.getVariableId())) {
                continue;
            }
            String variableCode = v.getVariableCode();
            String variableAlias = v.getVariableAlias();
            String codeAlias = v.getEntityVariableAlias();
            if (variable.getVariableAlias().equals(variableAlias)) {
                result.setStatus(ResponseResult.ERROR_STAUS);
                result.setMsg("??????????????????");
                return true;
            }
            if (variable.getVariableCode().equals(variableCode)) {
                result.setStatus(ResponseResult.ERROR_STAUS);
                result.setMsg("????????????");
                return true;
            }
            if (entityVariableAlias.equals(codeAlias)) {
                result.setStatus(ResponseResult.ERROR_STAUS);
                result.setMsg("??????????????????");
                return true;
            }
        }
        return false;
    }

    private boolean checkNameOrCodeExist(Variable variable, ResponseResult result) {
        Preconditions.checkArgument(variable != null);
        Preconditions.checkArgument(result != null);

        String isPublic = variable.getIsPublic();
        String variableGroupId = variable.getVariableGroupId();
        if ("1".equals(isPublic) || StringUtils.isNotBlank(variableGroupId)) {
            return checkNameOrCodeExistInPublic(variable, result);
        }
        return checkNameOrCodeExistInFolder(variable, result);
    }

    private boolean checkNameOrCodeExistInFolder(Variable variable, ResponseResult result) {
        if (!Config.CRE_REFERENCE_CHECK_ENABLE) {
            return false;
        }
        Preconditions.checkArgument(variable != null);
        Preconditions.checkArgument(result != null);

        String variableId = variable.getVariableId();
        String folderId = variable.getFolderId();

        if (checkNameInFolderExist(variable.getVariableAlias(),
                variableId, folderId)) {
            result.setStatus(ResponseResult.ERROR_STAUS);
            result.setMsg("??????????????????????????????????????????????????????????????????????????????????????????");
            return true;
        }

        if (checkCodeInFolderExist(variable.getVariableCode(),
                variableId, folderId)) {
            result.setStatus(ResponseResult.ERROR_STAUS);
            result.setMsg("??????????????????????????????????????????????????????????????????????????????????????????");
            return true;
        }
        return false;
    }

    private boolean checkNameOrCodeExistInPublic(Variable variable, ResponseResult result) {
        if (!Config.CRE_REFERENCE_CHECK_ENABLE) {
            return false;
        }
        Preconditions.checkArgument(variable != null);
        Preconditions.checkArgument(result != null);

        String variableId = variable.getVariableId();

        if (checkNameInPublicExist(variable.getVariableAlias(),
                variableId)) {
            result.setStatus(ResponseResult.ERROR_STAUS);
            result.setMsg("???????????????????????????????????????????????????????????????");
            return true;
        }

        if (checkCodeInPublicExist(variable.getVariableCode(),
                variableId)) {
            result.setStatus(ResponseResult.ERROR_STAUS);
            result.setMsg("???????????????????????????????????????????????????????????????");
            return true;
        }
        return false;
    }

    /**
     * ???????????????????????????????????????
     * 1.????????????????????????
     * 2.???????????????????????????
     * 3.?????????????????????
     *
     * @param variableAlias
     * @param variableId
     * @param folderId
     * @return
     */
    private boolean checkNameInFolderExist(String variableAlias,
                                           @Nullable String variableId,
                                           String folderId) {
        if (!Config.CRE_REFERENCE_CHECK_ENABLE) {
            return false;
        }
        Preconditions.checkArgument(StringUtils.isNotBlank(variableAlias));
        Preconditions.checkArgument(StringUtils.isNotBlank(folderId));
        Map<String, Object> param = new HashMap<>(3);
        param.put("variableAlias", variableAlias);
        param.put("variableId", variableId);
        param.put("folderId", folderId);
        int count = (int) daoHelper.queryOne(_MYBITSID_PREFIX + "countNameInFolder", param);
        if (count > 0) {
            return true;
        }
        return false;
    }

    /**
     * ???????????????????????????????????????
     * 1.????????????????????????
     * 2.???????????????????????????
     * 3.?????????????????????
     *
     * @param variableCode
     * @param variableId
     * @param folderId
     * @return
     */
    private boolean checkCodeInFolderExist(String variableCode,
                                           @Nullable String variableId,
                                           String folderId) {
        if (!Config.CRE_REFERENCE_CHECK_ENABLE) {
            return false;
        }
        Preconditions.checkArgument(StringUtils.isNotBlank(variableCode));
        Preconditions.checkArgument(StringUtils.isNotBlank(folderId));
        Map<String, Object> param = new HashMap<>(3);
        param.put("variableCode", variableCode);
        param.put("variableId", variableId);
        param.put("folderId", folderId);
        int count = (int) daoHelper.queryOne(_MYBITSID_PREFIX + "countCodeInFolder", param);
        if (count > 0) {
            return true;
        }
        return false;
    }

    /**
     * ????????????????????????????????????
     * 1.???????????????????????????
     * 2.?????????????????????????????????
     * 3.?????????????????????
     *
     * @param variableAlias
     * @param variableId
     * @return
     */
    private boolean checkNameInPublicExist(String variableAlias,
                                           @Nullable String variableId) {
        Preconditions.checkArgument(StringUtils.isNotBlank(variableAlias));
        Map<String, Object> param = new HashMap<>(2);
        param.put("variableAlias", variableAlias);
        param.put("variableId", variableId);
        int count = (int) daoHelper.queryOne(_MYBITSID_PREFIX + "countNameInPublic", param);
        if (count > 0) {
            return true;
        }
        return false;
    }

    /**
     * ????????????????????????????????????
     * 1.???????????????????????????
     * 2.?????????????????????????????????
     * 3.?????????????????????
     *
     * @param variableCode
     * @param variableId
     * @return
     */
    private boolean checkCodeInPublicExist(String variableCode,
                                           @Nullable String variableId) {
        Preconditions.checkArgument(StringUtils.isNotBlank(variableCode));
        Map<String, Object> param = new HashMap<>(2);
        param.put("variableCode", variableCode);
        param.put("variableId", variableId);
        int count = (int) daoHelper.queryOne(_MYBITSID_PREFIX + "countCodeInPublic", param);
        if (count > 0) {
            return true;
        }
        return false;
    }

    //????????????Id??????????????????
    @Override
    public List<Map<String, Object>> getVariableByRuleId(String ruleId, String folderId) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("ruleId", ruleId);
        param.put("folderId", folderId);
        param.put("kindId", "K1");
        List<Map<String, Object>> result = daoHelper.queryForList(_MYBITSID_PREFIX +
                "getVariableByRuleId", param);
        return result;
    }

    @Override
    public List<Map<String, Object>> getBaseVariable(String folderId) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("folderId", folderId);
        List<Map<String, Object>> result = daoHelper.queryForList(_MYBITSID_PREFIX + "getBaseVariable", param);
        return result;
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @param variableIdList
     * @param folderId
     * @return ????????????????????? {@code true}
     */
    @Override
    public boolean checkExitsPrivateVariable(Collection<String> variableIdList, final String folderId) {
        if (CollectionUtil.isEmpty(variableIdList)) {
            return false;
        }

        Map<String, Object> params = new HashMap<>();
        params.put("folderId", folderId);
        params.put("variableIdList", variableIdList);
        int count = (int) daoHelper.queryOne(_MYBITSID_PREFIX + "checkExitsPrivateVariable", params);
        if (count > 0) {
            return true;
        }
        return false;
    }

    @Override
    public List<String> getNestedVariableIds(String entityId) {
        List<String> nestedVariableIds = daoHelper.queryForList(_MYBITSID_PREFIX +
                "getNestedVariableIds", entityId);
        return nestedVariableIds;
    }

    @Override
    public List<String> getNestedVariableIdsByEntityIds(List<String> entityIds) {
        List<String> nestedVariableIds = daoHelper.queryForList(_MYBITSID_PREFIX +
                "getNestedVariableIdsByEntityIds", entityIds);
        return nestedVariableIds;
    }

    // ------------------------------ ???????????? ------------------------------

    @Override
    public Map<String, Object> pubSelectVariables(String variableAlias, String variableGroupName,
                                                  String variableTypeId, String kindId,
                                                  String startDate, String endDate,
                                                  String start, String size) {
        Map<String, Object> param = new HashMap<>();
        param.put("variableAlias", variableAlias);
        param.put("variableGroupName", variableGroupName);
        param.put("typeId", variableTypeId);
        param.put("kindId", kindId);
        param.put("startDate", startDate);
        param.put("endDate", endDate);
        final Map<String, Object> result = daoHelper.queryForPageList(_MYBITSID_PREFIX +
                "pubSelectVariables", param, start, size);
        return result;
    }

    /**
     * ????????????????????????????????????????????????????????????????????????
     *
     * @param variableAlias
     * @param variableGroupName
     * @param variableTypeId
     * @param startDate
     * @param endDate
     * @param start
     * @param size
     * @return
     */
    @Override
    public Map<String, Object> pubSelectFlatVariables(String variableAlias, String variableGroupName,
                                                      String variableTypeId, String kindId,
                                                      String startDate, String endDate,
                                                      String start, String size) {
        Map<String, Object> param = new HashMap<>();
        param.put("variableAlias", variableAlias);
        param.put("variableGroupName", variableGroupName);
        param.put("typeId", variableTypeId);
        param.put("kindId", kindId);
        param.put("startDate", startDate);
        param.put("endDate", endDate);
        final Map<String, Object> result = daoHelper.queryForPageList(_MYBITSID_PREFIX +
                "pubSelectFlatVariables", param, start, size);
        return result;
    }

    /**
     * ????????????????????????????????????????????????????????????????????????
     *
     * @param variableAlias
     * @param variableGroupName
     * @param variableTypeId
     * @param startDate
     * @param endDate
     * @return
     */
    @Override
    public List<VariableGroupExt> pubSelectFlatVariables(String variableAlias,
                                                         String variableGroupName,
                                                         String variableTypeId,
                                                         String startDate, String endDate) {
        Map<String, Object> param = new HashMap<>();
        param.put("variableAlias", variableAlias);
        param.put("variableGroupName", variableGroupName);
        param.put("typeId", variableTypeId);
        param.put("startDate", startDate);
        param.put("endDate", endDate);
        return daoHelper.queryForList(_MYBITSID_PREFIX +
                "pubSelectFlatVariables", param);
    }

    // ??????????????????
    @Override
    public Map<String, Object> pagedPubVariableResources(String variableAlias, String variableGroupName,
                                                         String variableTypeId,
                                                         String startDate, String endDate,
                                                         String start, String size) {
        Map<String, Object> param = new HashMap<>();
        param.put("variableAlias", variableAlias);
        param.put("variableGroupName", variableGroupName);
        param.put("typeId", variableTypeId);
        param.put("startDate", startDate);
        param.put("endDate", endDate);
        final Map<String, Object> result = daoHelper.queryForPageList(_MYBITSID_PREFIX +
                "pagedPubVariableResources", param, start, size);
        return result;
    }

    /**
     * ???????????????????????????????????????????????? ?????????????????????,????????????????????????
     *
     * @param ruleId
     * @return
     */
    @Override
    public List<Map<String, Object>> getVariableMapByRuleId(String ruleName, String ruleId, String isPublic, String kindId) {
        Map<String, Object> params = new HashMap<>();
        params.put("ruleName", ruleName);
        params.put("ruleId", ruleId);
        params.put("isPublic", isPublic);
        params.put("kindId", kindId);
        List<Map<String, Object>> variableMap = daoHelper.queryForList(_MYBITSID_PREFIX +
                "getVariableMapByRuleId", params);
        return variableMap;
    }

    /**
     * ???????????????????????????????????????????????? ?????????????????????,???????????????Map<code:??????>
     *
     * @return Map<code:??????>
     */
    @Override
    public Map<String, Object> getVariableCodeAndNameMap(String ruleName, String ruleId) {
        List<Map<String, Object>> variableMapByRuleId = getVariableMapByRuleId(ruleName, ruleId, null, null);
        if (variableMapByRuleId == null) {
            return new HashMap<>(1);
        }
        Map<String, Object> variableCodeDescMap = new HashMap<>(variableMapByRuleId.size());
        for (Map<String, Object> variableMap : variableMapByRuleId) {
            if (variableMap != null) {
                String variableCode = (String) variableMap.get("variableCode");
                String variableAlias = (String) variableMap.get("variableAlias");
                if (!StringUtils.isBlank(variableCode)) {
                    if (StringUtils.isBlank(variableAlias)) {
                        variableAlias = variableCode;
                    }
                    variableCodeDescMap.put(variableCode, variableAlias);
                }
            }
        }
        return variableCodeDescMap;
    }

    /**
     * ???????????????????????????????????? ????????????????????????????????????
     *
     * @param ruleId
     * @param kindId K1 ????????????   K2	????????????     K3	????????????     K4	????????????
     * @return
     */
    @Override
    public List<Map<String, Object>> getVariableMapByRuleId(String ruleId, String kindId) {
        return getVariableMapByRuleId(null, ruleId, null, kindId);
    }

    /**
     * ????????????????????????????????????????????????
     *
     * @param ruleId
     * @return
     */
    @Override
    public List<Map<String, Object>> getOutVariableMapByRuleId(String ruleId) {
        List<Map<String, Object>> variableMap = daoHelper.queryForList(_MYBITSID_PREFIX +
                "getOutVariableMapByRuleId", ruleId);
        return variableMap;
    }

    /**
     * ??????????????????
     * ???????????????????????????????????????????????? ???????????????????????????
     *
     * @param ruleId
     * @return
     */
    @Override
    public List<Map<String, Object>> getPubVariableMapWithoutKpiByRuleId(String ruleId) {
        List<Map<String, Object>> variableMap = daoHelper.queryForList(_MYBITSID_PREFIX +
                "getPubVariableMapWithoutKpiByRuleId", ruleId);
        return variableMap;
    }

    /**
     * ??????????????????
     * ?????????????????????????????????????????? ???????????????????????????
     *
     * @param ruleId
     * @return
     */
    @Override
    public List<Map<String, Object>> getVariableMapWithoutKpiByRuleId(String ruleId, String kindId) {
        Map<String, Object> parms = new HashMap<>();
        parms.put("ruleId", ruleId);
        parms.put("kindId", kindId);
        List<Map<String, Object>> variableMap = daoHelper.queryForList(_MYBITSID_PREFIX +
                "getVariableMapWithoutKpiByRuleId", parms);
        return variableMap;
    }

    @Override
    public ResponseResult pubVariablesByRule(String ruleId) {
        List<VariableExt> variableExts = daoHelper.queryForList(_MYBITSID_PREFIX +
                "getPubVariableByRuleId", ruleId);
        return ResponseResult.createSuccessInfo("", variableExts);
    }


    @Override
    public ResponseResult pubVariablesByFolder(String folderId) {
        List<VariableExt> variableExts = daoHelper.queryForList(_MYBITSID_PREFIX +
                "getPubVariableByFolderId", folderId);
        return ResponseResult.createSuccessInfo("", variableExts);
    }

    @Override
    @Transactional
    @CuratorMutexLock(value = {"variableAlias", "variableCode"}, lockAlias = {"name", "code"},
            lockDataType = LockDataType.VARIABLE)
    public ResponseResult pubInsert(Variable variable, String userId) {

        ResponseResult result = new ResponseResult();
        if (checkNameOrCodeExistInPublic(variable, result)) {
            return result;
        }

        // ????????????????????????????????????????????????????????????????????????
        if (ConstantUtil.VARIABLE_ENTITY_TYPE.equals(variable.getTypeId())) {
            String entityId = IdUtil.createId();
            variable.setEntityId(entityId);    // variable ?????????????????????id
            VariableEntity variableEntity = new VariableEntity();
            variableEntity.setCreateDate(variable.getCreateDate());
            variableEntity.setCreatePerson(variable.getCreatePerson());
            variableEntity.setEntityId(entityId);
            this.variableEntityService.insertVariableEntity(variableEntity);
        }
        final String id = IdUtil.createId();
        variable.setVariableId(id);
        variable.setVariableAlias(variable.getVariableAlias().trim());
        variable.setVariableCode(variable.getVariableCode().trim());
        variable.setCreateDate(new Date());
        variable.setCreatePerson(userId);
        variable.setIsPublic(ConstantUtil.PUBLIC);
        this.daoHelper.insert(_MYBITSID_PREFIX + "pubInsert", variable);

        // ?????????????????????????????????????????????
        authorityService.autoGrantAuthToCurrentUser(id, ResourceType.DATA_PUB_VARIABLE);

        sysLogService.logOperate(ConstantUtil.OPERATE_CREATE_PARAM,
                JSON.toJSONString(variable));

        return ResponseResult.createSuccessInfo();
    }

    @Override
    @CuratorMutexLock(value = {"variableAlias", "variableCode"}, lockAlias = {"name", "code"},
            lockDataType = LockDataType.VARIABLE)
    public ResponseResult pubInsertVariableRelation(Variable variable, VariableEntity variableEntity,
                                                    VariableEntityRelation variableEntityRelation,
                                                    String userId) {
        ResponseResult result = new ResponseResult();
        if (checkNameOrCodeExistInPublic(variable, result)) {
            return result;
        }

        String variableId = IdUtil.createId();
        variable.setVariableId(variableId);
        variable.setVariableAlias(variable.getVariableAlias().trim());
        variable.setVariableCode(variable.getVariableCode().trim());
        variable.setCreateDate(new Date());
        variable.setCreatePerson(userId);
        variable.setIsPublic(ConstantUtil.PUBLIC);
        if (ConstantUtil.VARIABLE_ENTITY_TYPE.equals(variable.getTypeId())) { // ????????????????????????
            String entityId = IdUtil.createId();
            variable.setEntityId(entityId);
            variableEntity.setCreateDate(variable.getCreateDate());
            variableEntity.setCreatePerson(variable.getCreatePerson());
            variableEntity.setEntityId(entityId);
            this.variableEntityService.insertVariableEntity(variableEntity);
        }
        variableEntityRelation.setEntityId(variableEntity.getEntityPid());
        variableEntityRelation.setVariableId(variableId);
        this.daoHelper.insert(_MYBITSID_PREFIX + "pubInsert", variable);
        variableEntityRelation.setEntityVariableAlias(variableEntityRelation.getEntityVariableAlias().trim());
        this.variableEntityRelationService.insertVariableEntityRelation(variableEntityRelation);
        return ResponseResult.createSuccessInfo();
    }


    // ?????????????????????????????????
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult upgrade(Variable variable, String variableGroupId, String currentUser) {
        // FIXME?????????????????????????????????????????????????????????????????????????????????????????????????????????????????? ??????????????????
        if (StringUtils.isBlank(variable.getTypeId())) {
            return ResponseResult.createFailInfo("????????????[typeId]????????????");
        }
        if (variable.getTypeId().equals(ConstantUtil.VARIABLE_ENTITY_TYPE)) {
            if (StringUtils.isBlank(variable.getEntityId())) {
                return ResponseResult.createFailInfo("?????????????????????????????????[entityId]????????????");
            }
        }
        variable.setUpdatePerson(currentUser);
        variable.setUpdateDate(new Date());
        variable.setVariableGroupId(variableGroupId);
        daoHelper.update(_MYBITSID_PREFIX + "upgrade", variable);
        return ResponseResult.createSuccessInfo();
    }
    
    @Override
    public ResponseResult batchUpgrade(Variable variable, String variableGroupId, String currentUser) {
    	
    	String groupName = variable.getVariableRemarks();
    	variableGroupId = variable.getFolderId();
    	VariableGroup variableGroup = new VariableGroup();
    	variableGroup.setVariableGroupId(variableGroupId);
    	
    	//??????????????????????????????????????????
    	if(groupName!=null&&!"".equals(groupName)) {
    		variableGroup.setVariableGroupName(groupName);
    	}else {
    		Map<String, String> param = new HashMap<>(1);
            param.put("folderId", variableGroupId);
        	List<RuleFolder> list = daoHelper.queryForList("com.bonc.frame.dao.rulefolder.RuleFolderMapper.selectByPrimaryKey",param);
        	for(RuleFolder ruleFolder :list) {
        		variableGroup.setVariableGroupName(ruleFolder.getFolderName());
        	}
    	}
    	
    	
    	//???????????????,???????????????
    	int count = (int) daoHelper.queryOne(_MYBITSID_VG_PREFIX +
                "countGroupById", variableGroup.getVariableGroupId());
    	if(count==0) {
    		insertVariableGroupDataPersistence(variableGroup);
    	}
        
    	//??????
        /*Variable v = new Variable();
        v.setFolderId(variableGroupId);
    	v.setUpdatePerson(currentUser);
        v.setUpdateDate(new Date());
        v.setVariableGroupId(variableGroupId);
        daoHelper.update(_MYBITSID_PREFIX + "batchUpgrade", v);*/
    	
        //?????????????????????????????????
        List<String> kindIds = new ArrayList<String>();
        kindIds.add("K1");
        kindIds.add("K2");
        kindIds.add("K3");
        kindIds.add("K4");
        Map<String, Object> p = new HashMap<String, Object>();
        p.put("folderId", variableGroupId);
        p.put("kindIds", kindIds);
        List<Variable> variableList = daoHelper.queryForList(_MYBITSID_PREFIX +"selectByFolderId", p);
        
        //????????????????????????????????????(?????????????????????,???????????????????????????????????????)
        for(Variable v:variableList) {
        	if("1".equals(v.getIsPublic()))
        		continue;
        	v.setFolderId(null);
        	v.setUpdatePerson(currentUser);
            v.setUpdateDate(new Date());
            v.setVariableGroupId(variableGroupId);
            v.setIsPublic("1");
            daoHelper.update(_MYBITSID_PREFIX + "upgrade", v);
        }
        //??????
        batchApiUpgrade(variableGroupId,currentUser,groupName);
        //??????
        publish(variableGroupId,currentUser,groupName);
        
        return ResponseResult.createSuccessInfo();
    }
    
    private void batchApiUpgrade(String apiGroupId,String currentUser,String groupName) {
    	
    	//???????????????
    	ApiGroup apiGroup = new ApiGroup();
    	apiGroup.setApiGroupId(apiGroupId);
    	apiGroup.setCreateDate(new Date());
    	apiGroup.setCreatePerson(currentUser);
        //??????????????????????????????????????????
    	if(groupName!=null&&!"".equals(groupName)) {
    		apiGroup.setApiGroupName(groupName);
    	}else {
    		Map<String, String> map = new HashMap<>(1);
        	map.put("folderId", apiGroupId);
        	List<RuleFolder> folderList = daoHelper.queryForList("com.bonc.frame.dao.rulefolder.RuleFolderMapper.selectByPrimaryKey",map);
        	for(RuleFolder ruleFolder :folderList) {
        		apiGroup.setApiGroupName(ruleFolder.getFolderName());
        	}
    	}
    	//??????????????????????????????
    	List<ApiConf> apiConfList = daoHelper.queryForList("com.bonc.frame.dao.api.ApiMapper." + "pubSelectApisByGroupId",apiGroupId);
		if (apiConfList.size()>0) {
			System.out.println("?????????????????????,??????????????????");
		    return;
		}else { 
			daoHelper.insert("com.bonc.frame.mapper.resource.ApiGroupMapper.insertSelective",apiGroup); 
		}
    	
    	Map<String, Object> param = new HashMap<>();
        param.put("isPublic", "0");
        param.put("folderId", apiGroupId);
        List<ApiConf> list = daoHelper.queryForList("com.bonc.frame.dao.api.ApiMapper." +"selectApiByProperty", param);
        for(ApiConf data:list) {
        	String apiId = data.getApiId();
        	apiService.upgrade(apiId, apiGroupId, currentUser);
        }
    }
    
    private void publish(String folderId,String currentUser,String groupName) {
    	//int t1=0;
    	//int t2=0;
    	//int count =0;
    	//??????????????????
    	RuleSetGroup ruleSetGroup = new RuleSetGroup();
        ruleSetGroup.setRuleSetGroupId(folderId);
        ruleSetGroup.setCreateDate(new Date());
        ruleSetGroup.setCreatePerson(currentUser);
        //?????????????????????????????????????????????
        if(groupName!=null&&!"".equals(groupName)) {
        	ruleSetGroup.setRuleSetGroupName(groupName);
    	}else {
    		Map<String, String> param = new HashMap<>(1);
            param.put("folderId", folderId);
        	List<RuleFolder> folderList = daoHelper.queryForList("com.bonc.frame.dao.rulefolder.RuleFolderMapper.selectByPrimaryKey",param);
        	for(RuleFolder ruleFolder :folderList) {
        		ruleSetGroup.setRuleSetGroupName(ruleFolder.getFolderName());
        	}
    	}
    	//?????????????????????????????????
        int obj = (int) daoHelper.queryOne("com.bonc.frame.mapper.resource.RuleSetGroupMapper." +"countGroupById", ruleSetGroup.getRuleSetGroupId());
		if (obj > 0) {
		    System.out.println("????????????????????????,??????????????????");
		    return;
		}else { 
			daoHelper.insert("com.bonc.frame.mapper.resource.RuleSetGroupMapper.insertSelective",ruleSetGroup); 
		}
		 
        //??????ruledetail???,???????????????????????????????????????
		//List<RuleDetail> list = ruleDetailService.getRuleHeaderListByFolderId(folderId);
		List<RuleDetailWithBLOBs> list = ruleDetailService.getRuleDetailWhthBOLOBListByFolderId(folderId);
    	for(RuleDetailWithBLOBs ruleDetail:list) {
    		//System.out.println("t1:"+t1++);
    		String ruleId = ruleDetail.getRuleId();
    		RuleDetailWithBLOBs rule = ruleDetailService.getRule(ruleId);
			/* System.out.println("oldRuleJson:"+rule.getRuleContent()); */
    		//???rule_detail??????json???????????????????????????json??????
    		JSONObject json =JSONObject.parseObject(rule.getRuleContent());
    		JSONObject states = json.getJSONObject("states");
    		for (Map.Entry entry : states.entrySet()) {
    			//System.out.println("t2="+t2++);
    			JSONObject rect = (JSONObject) entry.getValue();
    			if(!rect.getString("type").equals("task")) {
    				continue;
    			}
    			JSONObject props = rect.getJSONObject("props");
    			String text = props.getJSONObject("text").getString("value");
    			
    			text += "("+ruleSetGroup.getRuleSetGroupName()+"-"+rule.getModuleName()+")";
    			JSONObject action = props.getJSONObject("action");
    			JSONArray array = action.getJSONArray("value");
    			JSONArray result = new JSONArray();
    			if(array==null||"".equals(array)) {
    				System.out.println("????????????????????????????????????");
    				System.out.println("text="+text);
    				System.out.println("ruleId="+ruleId);
    				continue;
    				
    			}
    			for(int i=0;i<array.size();i++) {
    				JSONObject value = (JSONObject) array.get(i);
    				String actRuleName = value.getString("actRuleName");
    				String uid = value.getString("uid");
    				//actRuleName += "("+uid+")";
					/*
					 * if(i==0) { text += "("+folderId+","+rule.getModuleName()+","+uid+")"; }
					 */
    				String isEndAction = value.getString("isEndAction");
    				String isEndFlow = value.getString("isEndFlow");
    				JSONArray LHSTxt = value.getJSONArray("LHSTxt");
    				JSONArray RHSTxt = value.getJSONArray("RHSTxt");
    				String LHS = value.getJSONObject("LHS").getString("union");
    				String LHSTxtContent = "";
    				for(int x=0;x<LHSTxt.size();x++) {
    					if(x==0) {
    						LHSTxtContent = (String) LHSTxt.get(x);
    					}else {
    						if("or".equals(LHS)) {
    							LHSTxtContent = LHSTxtContent + " ??? " + (String) LHSTxt.get(x);
        					}else {
        						LHSTxtContent = LHSTxtContent + " ??? " + (String) LHSTxt.get(x);
        					}
    					}
    				}
    				String RHSTxtContent = "";
    				for(int x=0;x<RHSTxt.size();x++) {
    					if(x==0) {
    						RHSTxtContent = (String) RHSTxt.get(x);
    					}else {
    						RHSTxtContent = RHSTxtContent + "," + (String) RHSTxt.get(x);
    					}
    				}
    				
    				JSONArray content = new JSONArray();
    				content.add("000"+(i+1));
    				content.add(actRuleName);
    				content.add(isEndAction);
    				content.add(isEndFlow);
    				content.add(LHSTxtContent);
    				content.add(RHSTxt.get(0).toString());
    				content.add(value);
    				
    				result.add(content);
    				
    				
					
    			}
    			
    			RuleSetHeader ruleSetHeader = new RuleSetHeader();
	    		ruleSetHeader.setRuleSetName(text);//????????????
	    		ruleSetHeader.setRuleSetGroupId(folderId);//????????????
    			
    			RuleSet RuleSet = new RuleSet();
	    		RuleSet.setRuleSetContent(result.toString());//????????????
	    		RuleSet.setEnable("1");
	    		RuleSet.setRuleSetName(text);//?????????
	    		RuleSet.setRuleSetGroupId(ruleDetail.getRuleName());
	    		//System.out.println("text="+text+",ruleId:"+ruleId);
	    		if (ruleSetService.checkNameIsExist(ruleSetHeader.getRuleSetName(), ruleSetHeader.getRuleSetHeaderId())) 
	    			continue;
	    		ruleSetService.publish(ruleSetHeader, RuleSet, ruleId, folderId, currentUser);
    			
    			/*
				 * System.out.println("ruleId:"+ruleId);
				 * System.out.println("folderId:"+folderId);
				 * System.out.println("currentUser:"+currentUser);
				 * System.out.println("groupId:"+ruleDetail.getRuleName());
				 * System.out.println("ruleSetName:"+text); System.out.println("text="+text);
				 * System.out.println("json:"+result.toString());
				 */
				
				
	    		
	    		
            }
    		
    	}
    }

    private boolean checkVariableGroupNameIsExist(String variableGroupName, @Nullable String variableGroupId) {
        Map<String, String> param = new HashMap<>();
        param.put("variableGroupName", variableGroupName);
        param.put("variableGroupId", variableGroupId);
        int obj = (int) daoHelper.queryOne(_MYBITSID_VG_PREFIX + "countByName", param);
        if (obj > 0) {
            return true;
        }
        return false;
    }

    // ???????????????
    @Override
    @Transactional
    public ResponseResult pubInsertVariableGroup(VariableGroup variableGroup, String userId) {
        if (variableGroup == null) {
            return ResponseResult.createFailInfo("????????????[variableGroup]??????");
        }

        if (checkVariableGroupNameIsExist(variableGroup.getVariableGroupName(), null)) {
            return ResponseResult.createFailInfo("????????????????????????");
        }

        variableGroup.setVariableGroupId(IdUtil.createId());
        variableGroup.setCreateDate(new Date());
        variableGroup.setCreatePerson(userId);
//        daoHelper.insert(_MYBITSID_VG_PREFIX + "insertSelective", variableGroup);
        insertVariableGroupDataPersistence(variableGroup);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertVariableGroupDataPersistence(VariableGroup variableGroup) {
        if (variableGroup != null) {
            String currentUser = ControllerUtil.getCurrentUser();
            variableGroup.setCreateDate(new Date());
            variableGroup.setCreatePerson(currentUser);
            daoHelper.insert(_MYBITSID_VG_PREFIX + "insertSelective", variableGroup);
        }
    }

    @Override
    @Transactional
    public ResponseResult pubUpdateVariableGroup(VariableGroup variableGroup, String userId) {
        if (variableGroup == null) {
            return ResponseResult.createFailInfo("????????????[variableGroup]??????");
        }

        if (checkVariableGroupNameIsExist(variableGroup.getVariableGroupName(), variableGroup.getVariableGroupId())) {
            return ResponseResult.createFailInfo("????????????????????????");
        }

        variableGroup.setUpdateDate(new Date());
        variableGroup.setUpdatePerson(userId);

        daoHelper.update(_MYBITSID_VG_PREFIX + "updateByPrimaryKeySelective", variableGroup);
        return ResponseResult.createSuccessInfo();
    }

    private int countVariablesById(String variableGroupId) {
        if (variableGroupId == null || "".equals(variableGroupId.trim())) {
            throw new IllegalArgumentException("????????????[variableGroupId]??????");
        }
        int count = (int) daoHelper.queryOne(_MYBITSID_VG_PREFIX +
                "countVariablesById", variableGroupId);
        return count;
    }

    private boolean pubGroupCanDeleted(String variableGroupId) {
        if (countVariablesById(variableGroupId) > 0) {
            return false;
        }
        return true;
    }

    @Override
    @Transactional
    public ResponseResult pubDeleteVariableGroup(String variableGroupId) {
        if (variableGroupId == null || "".equals(variableGroupId.trim())) {
            return ResponseResult.createFailInfo("????????????[variableGroupId]??????");
        }
        if (!pubGroupCanDeleted(variableGroupId)) {
            return ResponseResult.createFailInfo("???????????????????????????????????????");
        }

        // ?????????????????????????????????????????????????????????????????????????????????id?????????null
        daoHelper.delete(_MYBITSID_VG_PREFIX + "deleteByPrimaryKey", variableGroupId);
        return ResponseResult.createSuccessInfo();
    }

    // ?????????????????????
    @Override
    public ResponseResult pubVariableGroups(String variableGroupName) {
        final List<Object> list = daoHelper.queryForList(_MYBITSID_VG_PREFIX + "select", variableGroupName);
        return ResponseResult.createSuccessInfo("success", list);
    }

    @Override
    public VariableGroup getVariableGroupById(String variableGroupId, String variableGroupName) {
        if (StringUtils.isBlank(variableGroupId) && StringUtils.isBlank(variableGroupName)) {
            return null;
        }
        Map<String, Object> param = new HashMap<>();
        param.put("variableGroupId", variableGroupId);
        param.put("variableGroupName", variableGroupName);
        return (VariableGroup) daoHelper.queryOne(_MYBITSID_VG_PREFIX + "getVariableGroupById", param);
    }

    @Override
    public Map<String, Object> pubVariableGroupsPaged(String variableGroupName, String start, String length) {
        final Map<String, Object> map = daoHelper.queryForPageList(_MYBITSID_VG_PREFIX + "select", variableGroupName, start, length);
        return map;
    }

    // ?????????????????????id???????????????
    @Override
    public ResponseResult pubSelectVariablesByGroupId(String variableGroupId) {
        List<Variable> variableList = daoHelper.queryForList(_MYBITSID_PREFIX + "pubSelectVariablesByGroupId", variableGroupId);
        return ResponseResult.createSuccessInfo("success", variableList);
    }


}
