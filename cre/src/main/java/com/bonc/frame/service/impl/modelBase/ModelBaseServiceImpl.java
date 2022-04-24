package com.bonc.frame.service.impl.modelBase;

import com.bonc.frame.dao.DaoHelper;
import com.bonc.frame.entity.auth.Channel;
import com.bonc.frame.entity.auth.Dept;
import com.bonc.frame.entity.commonresource.*;
import com.bonc.frame.entity.rule.RuleDetail;
import com.bonc.frame.service.auth.DeptService;
import com.bonc.frame.service.auth.RoleService;
import com.bonc.frame.service.modelBase.ModelBaseService;
import com.bonc.frame.util.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * 模型库
 *
 * @author yedunyao
 * @date 2019/9/2 15:50
 */
@Service
public class ModelBaseServiceImpl implements ModelBaseService {

    private static final String _MODEL_GROUP_MAPPER = "com.bonc.frame.mapper.resource.ModelGroupMapper.";
    private static final String _DEPT_PREFIX = "com.bonc.frame.mapper.auth.DeptMapper.";

    @Autowired
    private DaoHelper daoHelper;

    @Autowired
    private DeptService deptService;

    @Autowired
    private RoleService roleService;

    // ------------------------ 模型组管理 ------------------------

    // 获取参数组列表
    @Override
    @Transactional
    public ResponseResult getModelGroups(String modelGroupName,String loginUserId) {
        // 验证是否全权
        boolean b = roleService.checkAuthorityIsAll(loginUserId);
        String channelId;
        if (b) {
            // 全权
            channelId = null;
        } else {
            // 非全权只能看自己渠道关联的产品列表
            channelId = deptService.getChannelIdByUserId(loginUserId);
        }
        deptService.getChannelIdByUserId(loginUserId);
        Map<String, Object> param = new HashMap<>();
        param.put("modelGroupName", modelGroupName);
        param.put("channelId", channelId);
        final List<ModelGroup> list = daoHelper.queryForList(_MODEL_GROUP_MAPPER +
                "getByGroupName", param);
        List<ModelGroupVo> voList = new ArrayList<>();
        for (ModelGroup mg : list) {
            ModelGroupVo vo = ModelGroupVo.getVo(mg);
            List<Channel> nameList = daoHelper.queryForList(_MODEL_GROUP_MAPPER + "getChannelName", mg.getModelGroupId());
            vo.setChannelList(nameList);
            voList.add(vo);
        }
        return ResponseResult.createSuccessInfo("success", voList);
    }

    @Override
    @Transactional
    public ModelGroup getModelGroupByModelId(String modelGroupId) {
        Map<String, Object> param = new HashMap<>();
        param.put("modelGroupId", modelGroupId);
        return (ModelGroup) daoHelper.queryOne(_MODEL_GROUP_MAPPER +
                "getByGroupName", param);
    }

    @Override
    @Transactional
    public ModelGroup getModelGroupByModelName(String modelGroupName) {
        Map<String, Object> param = new HashMap<>();
        param.put("modelGroupName", modelGroupName);
        return (ModelGroup) daoHelper.queryOne(_MODEL_GROUP_MAPPER +
                "getModelGroupByGroupName", param);
    }

    @Override
    @Transactional
    public Map<String, Object> getModelGroupsPaged(String loginUserId,
                                                   String modelGroupName, String channelId,
                                                   String start, String length,
                                                   String startDate, String endDate) {
        // 验证默认分组-其他的存在，如果没有就插入一条，
        ModelGroup ch = ModelGroup.getDefaultGroup();
        List<ModelGroup> ls = daoHelper.queryForList(_MODEL_GROUP_MAPPER + "checkDefaultGroup", ch);
        if (ls.size() == 0) {
            daoHelper.insert(_MODEL_GROUP_MAPPER + "insertSelective", ch);
        }
// 验证是否全权
        boolean b = roleService.checkAuthorityIsAll(loginUserId);
        String chanId;
        if (b) {
            // 全权
            chanId = null;
        } else {
            // 非全权只能看自己渠道关联的产品列表
            chanId = deptService.getChannelIdByUserId(loginUserId);
        }
        Map<String, Object> param = new HashMap<>();
        param.put("modelGroupName", modelGroupName);
        param.put("startDate", startDate);
        param.put("endDate", endDate);
        if (channelId == null || channelId.equals("")) {
            // 接口未传选择的渠道id-根据权限来展示
            param.put("channelId", chanId);
        } else {
            // 接口传了选择的渠道，则展示本渠道下的产品
            param.put("channelId", channelId);
        }
        final Map<String, Object> map = daoHelper.queryForPageList(_MODEL_GROUP_MAPPER +
                "getByGroupName", param, start, length);
        List<ModelGroup> data = (List<ModelGroup>) map.get("data");
        List<Map> dataMap = new ArrayList<>();
        for (ModelGroup datum : data) {
            String mgId = datum.getModelGroupId();
            List<Channel> nameList = daoHelper.queryForList(_MODEL_GROUP_MAPPER + "getChannelName", mgId);
            Map mgMap = new HashMap();
            try {
                mgMap = MapBeanUtil.convertBean2Map(datum);
            } catch (IntrospectionException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            if (nameList.size() > 0) {
                StringBuilder names = new StringBuilder();
                for (int i = 0; i < nameList.size(); i++) {
                    names.append(nameList.get(i).getChannelName());
                    if (i<nameList.size()-1){
                        names.append(", ");
                    }
                }
                mgMap.put("modelGroupChannel", names.toString());
            } else {
                mgMap.put("modelGroupChannel", "");
            }
            dataMap.add(mgMap);
        }

        map.remove("data");
        map.put("data", dataMap);
        return map;
    }

    private boolean checkGroupNameIsExist(String modelGroupName, @Nullable String modelGroupId) {
        Map<String, String> param = new HashMap<>();
        param.put("modelGroupName", modelGroupName);
        param.put("modelGroupId", modelGroupId);
        int obj = (int) daoHelper.queryOne(_MODEL_GROUP_MAPPER + "countByName", param);
        if (obj > 0) {
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public ResponseResult createModelGroup(ModelGroupDto mg, String userId) {
        if (mg == null) {
            return ResponseResult.createFailInfo("请求参数[modelGroup]为空");
        }

        if (checkGroupNameIsExist(mg.getModelGroupName(), null)) {
            return ResponseResult.createFailInfo("产品名称已存在");
        }
        ModelGroup modelGroup = ModelGroupDto.getMg(mg);
        modelGroup.setModelGroupId(IdUtil.createId());
        modelGroup.setCreateDate(new Date());
        modelGroup.setCreatePerson(userId);
        insertModelGroupDataPersistence(modelGroup);
//        daoHelper.insert(_MODEL_GROUP_MAPPER + "insertSelective", modelGroup);
        // 建立产品与渠道的关联
//        if (mg.getChannelList().size() > 0) {
//            this.groupAddChannel(modelGroup.getModelGroupId(), mg.getChannelList());
//        }
        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertModelGroupDataPersistence(ModelGroup modelGroup) {
        if (modelGroup != null) {
            String currentUser = ControllerUtil.getCurrentUser();
            modelGroup.setCreateDate(new Date());
            modelGroup.setCreatePerson(currentUser);
            daoHelper.insert(_MODEL_GROUP_MAPPER + "insertSelective", modelGroup);
        }
    }

    @Override
    @Transactional
    public ResponseResult updateModelGroup(ModelGroupDto dto, String userId) {
        if (dto == null) {
            return ResponseResult.createFailInfo("请求参数[modelGroup]不能为空");
        }

        if (checkGroupNameIsExist(dto.getModelGroupName(), dto.getModelGroupId())) {
            return ResponseResult.createFailInfo("产品名称已存在");
        }
        ModelGroup modelGroup = ModelGroupDto.getMg(dto);
        modelGroup.setUpdateDate(new Date());
        modelGroup.setUpdatePerson(userId);
        daoHelper.update(_MODEL_GROUP_MAPPER + "updateByPrimaryKeySelective", modelGroup);
        // 更新关联渠道数据
        this.groupAddChannel(modelGroup.getModelGroupId(), dto.getChannelList());
        return ResponseResult.createSuccessInfo();
    }

    // 当前模型组是否在使用中
    @Override
    @Transactional
    public boolean isGroupUsed(String modelGroupId) {
        int obj = (int) daoHelper.queryOne(_MODEL_GROUP_MAPPER +
                "countGroupUsed", modelGroupId);
        if (obj > 0) {
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public ResponseResult deleteModelGroup(String modelGroupId) {
        if (StringUtils.isBlank(modelGroupId)) {
            return ResponseResult.createFailInfo("请求参数[modelGroupId]不能为空");
        }

        if (isGroupUsed(modelGroupId)) {
            return ResponseResult.createFailInfo("当前模型组正在使用中，不能删除");
        }
        // 将被删除的产品所关联的模型的对应产品换成默认产品：其他
        daoHelper.update(_MODEL_GROUP_MAPPER + "deleteByUpdate", modelGroupId);
        daoHelper.delete(_MODEL_GROUP_MAPPER + "deleteByPrimaryKey", modelGroupId);
        return ResponseResult.createSuccessInfo();
    }

    /**
     * 产品设置调用渠道
     * @param modelGroupId  产品的id
     * @param dto  渠道的id的集合
     * @return 操作结果
     */
    @Override
    public ResponseResult groupAddChannel(String modelGroupId, ModelChanIdDto dto) {
        // 先清除原来此产品关联的，再保存新的
        daoHelper.delete(_MODEL_GROUP_MAPPER + "connectDelete", modelGroupId);
        for (String chanId : dto.getIdList()) {
            String id = IdUtil.createId();
            ModelGroupChannel mo = new ModelGroupChannel(id, chanId, modelGroupId);
            daoHelper.insert(_MODEL_GROUP_MAPPER + "groupConnectChannel", mo);
        }
        return ResponseResult.createSuccessInfo("success");
    }
    /**
     * 根据id获取产品信息
     * @param modelGroupId 产品id
     * @return 产品信息
     */
    @Override
    public ModelGroup getGroupInfoById(String modelGroupId) {
        ModelGroup mg = (ModelGroup) daoHelper.queryOne(_MODEL_GROUP_MAPPER + "getGroupInfoById", modelGroupId);
        return mg;
    }

    /**
     * 根据模型头id获取版本号及对应id
     * @param modelId 模型id
     * @return id及版本号
     */
    @Override
    public List<ModelVersion> modelGetVersion(String modelId) {
        HashMap<String,String> result = (HashMap<String, String>) daoHelper.queryOne(_MODEL_GROUP_MAPPER + "getModelInfoById", modelId);
        List<ModelVersion> list = daoHelper.queryForList(_MODEL_GROUP_MAPPER + "modelGetVersion", result);
        return list;
    }

    /**
     * 根据模型版本id获取关联的规则集
     * @param modelId 模型id
     * @return 规则集数据
     */
    @Override
    public List<RuleSetForModel> modelVersionWithRuleSet(String modelId) {
        List<RuleSetForModel> list = daoHelper.queryForList(_MODEL_GROUP_MAPPER + "modelGetRuleSet", modelId);
        return list;
    }

    /**
     * 获取渠道数据用于产品设置调用渠道
     * @param loginUserId 用户id，区分权限
     * @return 渠道信息数据
     */
    @Override
    public List<ModelGroupChannelVo> channelList(String loginUserId, String modelGroupId) {
        List<ModelGroupChannelVo> voList = new ArrayList<>();
        // 校验权限-获取全权数据或者本渠道数据
        if (loginUserId != null) {
            // 全权
            List<Dept> list = daoHelper.queryForList(_DEPT_PREFIX + "list");
            for(Dept d:list){
                List<ModelGroupChannelVo> chList = getChannelInfoByDept(d.getDeptId(), modelGroupId);
                for (ModelGroupChannelVo vo : chList) {
                    voList.add(vo);
                }
            }
        } else {
            // 本机构下的渠道数据
            return voList;
        }
        return voList;
    }

    /**
     * 根据id获取产品信息
     * @param modelGroupId 产品id
     * @return 产品信息
     */
    @Override
    public ModelGroup getGroupInfoById(String modelGroupId) {
        ModelGroup mg = (ModelGroup) daoHelper.queryOne(_MODEL_GROUP_MAPPER + "getGroupInfoById", modelGroupId);
        return mg;
    }

    /**
     * 根据模型头id获取版本号及对应id
     * @param modelId 模型id
     * @return id及版本号
     */
    @Override
    public List<ModelVersion> modelGetVersion(String modelId) {
        RuleDetail ruleDetail = (RuleDetail) daoHelper.queryOne(_MODEL_GROUP_MAPPER + "getModelInfoById", modelId);
        HashMap<String, String> map = new HashMap<>();
        map.put("ruleName", ruleDetail.getRuleName());
        map.put("ruleStatus", ruleDetail.getRuleStatus());
        List<ModelVersion> list = daoHelper.queryForList(_MODEL_GROUP_MAPPER + "modelGetVersion", map);
        return list;
    }

    /**
     * 根据模型版本id获取关联的规则集
     * @param modelId 模型id
     * @return 规则集数据
     */
    @Override
    public List<RuleSetForModel> modelVersionWithRuleSet(String modelId) {
        List<RuleSetForModel> list = daoHelper.queryForList(_MODEL_GROUP_MAPPER + "modelGetRuleSet", modelId);
        return list;
    }

    /**
     * 根据机构信息获取渠道数据，用于关联产品
     * @param deptId
     * @param modelGroupId
     * @return
     */
    private List<ModelGroupChannelVo> getChannelInfoByDept(String deptId, String modelGroupId) {
        // 获取该机构下的渠道相关信息
        List<ModelGroupChannelVo> chList = daoHelper.queryForList(_DEPT_PREFIX + "getChannelByDept", deptId);
        // 校验该渠道有无被关联，给数据加上标记
        String isSelected = "0";
        if (!CollectionUtil.isEmpty(chList)) {
            for (ModelGroupChannelVo vo : chList) {
                // 传了产品id就要判断某个渠道和产品是否已有关联
                Map<String,String> map = new HashMap<>();
                map.put("modelGroupId", modelGroupId);
                map.put("channelId", vo.getChannelId());
                List<ModelGroupChannel> conList = daoHelper.queryForList(_MODEL_GROUP_MAPPER + "isConnected", map);
                if (conList.size() > 0) {
                    isSelected = "1";
                }
                vo.setIsConnected(isSelected);
            }
        }
        return chList;
    }

}
