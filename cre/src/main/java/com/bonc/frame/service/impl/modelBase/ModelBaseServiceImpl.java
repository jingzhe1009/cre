package com.bonc.frame.service.impl.modelBase;

import com.bonc.frame.dao.DaoHelper;
import com.bonc.frame.entity.auth.Channel;
import com.bonc.frame.entity.auth.Dept;
import com.bonc.frame.entity.auth.DeptChannelTree;
import com.bonc.frame.entity.commonresource.ModelGroup;
import com.bonc.frame.entity.commonresource.ModelGroupChannel;
import com.bonc.frame.entity.commonresource.ModelGroupDto;
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

    // ------------------------ 模型组管理 ------------------------

    // 获取参数组列表
    @Override
    @Transactional
    public ResponseResult getModelGroups(String modelGroupName) {
        Map<String, Object> param = new HashMap<>();
        param.put("modelGroupName", modelGroupName);
        final List<Object> list = daoHelper.queryForList(_MODEL_GROUP_MAPPER +
                "getByGroupName", param);
        return ResponseResult.createSuccessInfo("success", list);
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
    public Map<String, Object> getModelGroupsPaged(String modelGroupName, String start, String length) {
        // 验证默认分组-其他的存在，如果没有就插入一条，
        ModelGroup ch = ModelGroup.getDefaultGroup();
        List<ModelGroup> ls = daoHelper.queryForList(_MODEL_GROUP_MAPPER + "checkDefaultGroup", ch);
        if (ls.size() == 0) {
            daoHelper.insert(_MODEL_GROUP_MAPPER + "insertSelective", ch);
        }
        Map<String, Object> param = new HashMap<>();
        param.put("modelGroupName", modelGroupName);
        final Map<String, Object> map = daoHelper.queryForPageList(_MODEL_GROUP_MAPPER +
                "getByGroupName", param, start, length);
        List<ModelGroup> data = (List<ModelGroup>) map.get("data");
        List<Map> dataMap = new ArrayList<>();
        for (ModelGroup datum : data) {
            String mgId = datum.getModelGroupId();
            List<String> nameList = daoHelper.queryForList(_MODEL_GROUP_MAPPER + "getChannelName", mgId);
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
                mgMap.put("modelGroupChannel", nameList.toString());
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
        ModelGroup modelGroup = new ModelGroup(mg.getModelGroupName(),mg.getModelGroupCode(),mg.getModelGroupDesc());
        modelGroup.setModelGroupId(IdUtil.createId());
        modelGroup.setCreateDate(new Date());
        modelGroup.setCreatePerson(userId);
        insertModelGroupDataPersistence(modelGroup);
//        daoHelper.insert(_MODEL_GROUP_MAPPER + "insertSelective", modelGroup);
        // 建立产品与渠道的关联
        this.groupAddChannel(modelGroup.getModelGroupId(), mg.getChannelList());
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
    public ResponseResult updateModelGroup(ModelGroup modelGroup, String userId) {
        if (modelGroup == null) {
            return ResponseResult.createFailInfo("请求参数[modelGroup]不能为空");
        }

        if (checkGroupNameIsExist(modelGroup.getModelGroupName(), modelGroup.getModelGroupId())) {
            return ResponseResult.createFailInfo("模型组名称已存在");
        }

        modelGroup.setUpdateDate(new Date());
        modelGroup.setUpdatePerson(userId);
        daoHelper.update(_MODEL_GROUP_MAPPER + "updateByPrimaryKeySelective", modelGroup);
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
     * @param channelIds  渠道的id的集合
     * @return 操作结果
     */
    @Override
    public ResponseResult groupAddChannel(String modelGroupId, List<String> channelIds) {
        // 先清除原来此产品关联的，再保存新的
        daoHelper.delete(_MODEL_GROUP_MAPPER + "connectDelete", modelGroupId);
        for (String channelId : channelIds) {
            String id = IdUtil.createId();
            ModelGroupChannel mo = new ModelGroupChannel(id, channelId, modelGroupId);
            daoHelper.insert(_MODEL_GROUP_MAPPER + "groupConnectChannel", mo);
        }
        return ResponseResult.createSuccessInfo();
    }

    /**
     * 获取渠道树
     * @param loginUserId 用户id，区分权限
     * @return 渠道树
     */
    @Override
    public List<DeptChannelTree> channelTree(String loginUserId,String modelGroupId) {
        // 校验权限-获取全权数据或者本渠道数据

        List<DeptChannelTree> deptTree = new ArrayList<>();
        // 本账号的机构-渠道数据

        // 全权数据
        List<Dept> list = daoHelper.queryForList(_DEPT_PREFIX + "list");
        //
        String isSelected = "0";
        for(Dept d:list){
            DeptChannelTree dt = new DeptChannelTree(d.getDeptId(),d.getParentId(),d.getDeptName(),"0");
            deptTree.add(dt);
            // 每个机构-查询下属渠道数据
            List<Channel> chList = daoHelper.queryForList(_DEPT_PREFIX+"getChannelByDept",d.getDeptId());
            if (!CollectionUtil.isEmpty(chList)) {
                for (Channel channel : chList) {
                    // 传了产品id就要判断某个渠道和产品是否已有关联
                    if (modelGroupId != null && !modelGroupId.equals("")) {
                        Map<String,String> map = new HashMap<>();
                        map.put("modelGroupId", modelGroupId);
                        map.put("channelId", channel.getChannelId());
                        List<ModelGroupChannel> conList = daoHelper.queryForList(_MODEL_GROUP_MAPPER + "isConnected", map);
                        if (conList.size() > 0) {
                            isSelected = "1";
                        }
                    }
                    // 如果渠道没有父数据,父id存机构id
                    DeptChannelTree ch;
                    if (Objects.equals(channel.getParentId(), null) || Objects.equals(channel.getParentId(), "")) {
                        ch = new DeptChannelTree(channel.getChannelId(), channel.getDeptId(), channel.getChannelName(),"1",isSelected);
                    } else {
                        // 有父数据的，parentId存父id
                        ch = new DeptChannelTree(channel.getChannelId(),channel.getParentId(),channel.getChannelName(),"1",isSelected);
                    }
                    deptTree.add(ch);
                }
            }
        }
        // 转换树形数据
        return DeptChannelTree.listToTree(deptTree);
    }

}
