package com.bonc.frame.service.impl.auth;

import com.bonc.frame.dao.DaoHelper;
import com.bonc.frame.entity.auth.*;
import com.bonc.frame.entity.commonresource.ModelGroupChannelVo;
import com.bonc.frame.service.auth.ChannelPathService;
import com.bonc.frame.service.auth.ChannelService;
import com.bonc.frame.service.auth.DeptService;
import com.bonc.frame.service.auth.RoleService;
import com.bonc.frame.util.CollectionUtil;
import com.bonc.frame.util.ControllerUtil;
import com.bonc.frame.util.IdUtil;
import com.bonc.frame.util.ResponseResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service("channelService")
public class ChannelServiceImpl implements ChannelService {

    @Autowired
    private DaoHelper daoHelper;

    @Autowired
    private RoleService roleService;

    @Autowired
    private DeptService deptService;

    @Autowired
    private ChannelPathService channelPathService;
    private final String _DEPT_PREFIX = "com.bonc.frame.mapper.auth.DeptMapper.";
    private final String _CHANNELPATH_PREFIX = "com.bonc.frame.mapper.auth.ChannelPathMapper.";
    private final String _CHANNEL_PREFIX="com.bonc.frame.mapper.auth.ChannelMapper.";

    //保存渠道信息
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult save(Channel channel, String loginUserId) {
        //非空判断
        if (channel ==null){
             ResponseResult.createFailInfo("请求参数不能为null");
        }
        final Channel oldChannel=findByName(channel.getChannelName());
        if (oldChannel !=null){
            return ResponseResult.createFailInfo("渠道已存在");
        }
        //新增渠道信息，入库保存
        final String channelId = IdUtil.createId();
        channel.setChannelId(channelId);
        //channel.setParentId();
        channel.setUserNum(0);
        channel.setCreateDate(new Date());
        channel.setCreatePerson(loginUserId);
        daoHelper.insert(_CHANNEL_PREFIX+"insert",channel);
        // 如果存在父子关系则保存
//        if (!channel.getParentId().isEmpty()) {
//            channelPathService.save(channelId,channel.getParentId());
//        }
        return ResponseResult.createSuccessInfo();
    }

    private Channel findByName(String channelName) {
        return (Channel) daoHelper.queryOne(_CHANNEL_PREFIX+"findByName",channelName);
    }
    //新建渠道名单
    @Override
    public Map<String, Object>  list(HttpServletRequest request, String channelName, String start, String size) {
        String loginUserId = ControllerUtil.getLoginUserId(request);
        boolean b = roleService.checkAuthorityIsAll(loginUserId);
        Map<Object, Object> param = new HashMap<>();
        param.put("channelName",channelName);
        if (!b) {
            // 不是全权
            String channelId = deptService.getChannelIdByUserId(loginUserId);
            param.put("channelId", channelId);
        }
        //根据渠道名称查找相关信息
        Map<String, Object> result=daoHelper.queryForPageList("com.bonc.frame.mapper.auth.ChannelMapper.listByChannelName",param,start,size);
        return result;
    }
    //删除渠道
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult delete(String channelId) {
        List<UserChannel> list=daoHelper.queryForList(_CHANNEL_PREFIX+"selectUserByChannelId",channelId);
        if (list.size()>0){
            return ResponseResult.createFailInfo("渠道下存在用户，无法删除");
        }
        if (channelId == null){
            return ResponseResult.createFailInfo("未检测到渠道数据");
        }
        daoHelper.delete(_CHANNEL_PREFIX+"deleteByPrimaryKey",channelId);
        channelPathService.delete(channelId);
        return ResponseResult.createSuccessInfo();
    }
    //更新渠道
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult update(Channel channel, String loginUserId) {
        //非空判断
        if (channel == null){
            return ResponseResult.createFailInfo("请求参数不能为null");
        }
        if (!isExists(channel)){
            return ResponseResult.createFailInfo("该渠道不存在，channel:"+channel.getChannelId());
        }
        //新增渠道
        channel.setUpdateDate(new Date());
        channel.setUpdatePerson(loginUserId);
        String channelId=channel.getChannelId();
        //更新渠道Id
        Channel old = selectByPrimaryKey(channelId);
        daoHelper.update(_CHANNEL_PREFIX+"updateByPrimaryKeySelective",channel);
        //判断原来渠道的上级渠道ID和新增渠道的上级渠道ID非空
        if (!(old.getParentId() == null && channel.getParentId() == null)) {
            channelPathService.delete(channelId);
            channelPathService.save(channelId, channel.getParentId());
        }
        return ResponseResult.createSuccessInfo();
    }

    public boolean isExists(Channel channel){
       final Channel resultChannel=selectByPrimaryKey(channel.getChannelId());
       return resultChannel == null ? false :true;
    }

    private Channel selectByPrimaryKey(String channelId) {
        return (Channel) daoHelper.queryOne(_CHANNEL_PREFIX + "selectByPrimaryKey",channelId);
    }

    //渠道名称表
    @Override
    public List<Object> nameList() {
        List<Object> objects=daoHelper.queryForList(_CHANNEL_PREFIX+"nameList");
        return objects;
    }
    
    @Override
    public List<Map<String,Object>> getChannelListByGroupId(String groupId) {
        return daoHelper.queryForList(_CHANNEL_PREFIX+"selectChannelList");
    }

    //渠道树
    @Override
    public List<PlaceVo> channelTree() {
        List<Channel> list = daoHelper.queryForList(_CHANNEL_PREFIX + "list");
        List<PlaceVo> voList = new ArrayList<>();
        for(Channel d:list){
            PlaceVo ch = new PlaceVo(d.getChannelId(),d.getParentId(),d.getChannelName());
            voList.add(ch);
        }
        return PlaceVo.PlaceTreeInfo(voList);
    }

    /**
     * 查看渠道下用户列表
     * @param channelId 渠道id
     * @return 用户信息
     */
    @Override
    public List<Object> userListByChannel(String channelId) {
        List<Object> objects = daoHelper.queryForList(_CHANNEL_PREFIX + "userListByChannel", channelId);
        return objects;
    }

    /**
     * 获取机构-渠道树
     * @param  loginUserId-当前登录用户-校验权限用
     * @return 树形数据
     */
    @Override
    public List<DeptChannelTree> channelTreeWithDept(String loginUserId) {
        // 校验权限-获取全权数据或者本渠道数据

        List<DeptChannelTree> deptTree = new ArrayList<>();
        // 本账号的机构-渠道数据

        // 全权数据
        List<Dept> list = daoHelper.queryForList(_DEPT_PREFIX + "list");
        for(Dept d:list){
            DeptChannelTree dt = new DeptChannelTree(d.getDeptId(),d.getParentId(),d.getDeptName(),"0");
            deptTree.add(dt);
            // 每个机构-查询下属渠道数据
            List<Channel> chList = daoHelper.queryForList(_DEPT_PREFIX+"getChannelByDept",d.getDeptId());
            if (!CollectionUtil.isEmpty(chList)) {
                for (Channel channel : chList) {
                    // 如果渠道没有父数据,父id存机构id
                    DeptChannelTree ch;
                    if (Objects.equals(channel.getParentId(), null) || Objects.equals(channel.getParentId(), "")) {
                        ch = new DeptChannelTree(channel.getChannelId(), channel.getDeptId(), channel.getChannelName(),"1");
                    } else {
                        // 有父数据的，parentId存父id
                        ch = new DeptChannelTree(channel.getChannelId(),channel.getParentId(),channel.getChannelName(),"1");
                    }
                    deptTree.add(ch);
                }
            }
        }
        // 转换树形数据
        return DeptChannelTree.listToTree(deptTree);
    }

    /**
     * 展示渠道数据-拼接机构名版
     * @param loginUserId 用户id，用于判定权限
     * @return 集合
     */
    @Override
    public List<ChannelDto> channelNameList(String loginUserId) {
        // 权限相关-非全权只能看自己的渠道
        String deptId = null;
        boolean b = roleService.checkAuthorityIsAll(loginUserId);
        List<ChannelDto> list;
        if (b) {
            // 全权-展示所有渠道数据
             list= daoHelper.queryForList(_DEPT_PREFIX + "channelListWithDept", deptId);
        } else {
            // 只展示自己的渠道数据
            ModelGroupChannelVo vo = (ModelGroupChannelVo) daoHelper.queryOne(_DEPT_PREFIX + "getChannelIdByUserId", loginUserId);
            // 总行大数据平台人员也是全权的
            if (vo.getChannelName().contains("大数据")) {
                if (vo.getDeptName().contains("总行")) {
                    return daoHelper.queryForList(_DEPT_PREFIX + "channelListWithDept", deptId);
                }
            }
            list = new ArrayList<>();
            list.add(new ChannelDto(vo.getChannelId(), vo.getChannelName(), vo.getChannelCode(),vo.getDeptName()));
        }
        return list;
    }
}
