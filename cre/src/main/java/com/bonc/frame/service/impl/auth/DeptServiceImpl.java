package com.bonc.frame.service.impl.auth;

import com.bonc.frame.dao.DaoHelper;
import com.bonc.frame.entity.auth.Channel;
import com.bonc.frame.entity.auth.DepartmentVo;
import com.bonc.frame.entity.auth.Dept;
import com.bonc.frame.entity.commonresource.ModelGroupChannelVo;
import com.bonc.frame.service.auth.DeptPathService;
import com.bonc.frame.service.auth.DeptService;
import com.bonc.frame.util.IdUtil;
import com.bonc.frame.util.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service("deptService")
public class DeptServiceImpl implements DeptService {
    @Autowired
    private DaoHelper daoHelper;
    @Autowired
    private DeptPathService deptPathService;

    private final String _DEPT_PREFIX = "com.bonc.frame.mapper.auth.DeptMapper.";
    private final String _MIDDLE_TABLE_PREFIX = "com.bonc.frame.mapper.auth.MiddleTableMapper.";
    private final String _DEPTPATH_PREFIX = "com.bonc.frame.mapper.auth.DeptPathMapper.";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult save(Dept dept, String loginUserId) {
        if (dept == null) {
            return ResponseResult.createFailInfo("请求参数不能为null");
        }
        final Dept oldDept = findByName(dept.getDeptName());
        if (oldDept != null) {
            return ResponseResult.createFailInfo("组织机构已存在");
        }
        final String deptId = IdUtil.createId();
        dept.setDeptId(deptId);
        dept.setUserNum(0);
        dept.setCreateDate(new Date());
        dept.setCreatePerson(loginUserId);
        daoHelper.insert(_DEPT_PREFIX + "insert", dept);
        //插入dept_path表
        deptPathService.save(deptId, dept.getParentId());
        return ResponseResult.createSuccessInfo();
    }

    @Override
    public Map<String, Object> list(String deptName, String start, String size) {
        Map<String, Object> param = new HashMap<>();
        param.put("deptName", deptName);
        Map<String, Object> result = daoHelper.queryForPageList(_DEPT_PREFIX + "listByDeptName", param, start, size);
        return result;
    }

    @Override
    public List<Object> nameList() {
        List<Object> objects = daoHelper.queryForList(_DEPT_PREFIX + "nameList");
        return objects;
    }

    @Override
    public List<DepartmentVo> deptTree() {
        List<Dept> list = daoHelper.queryForList(_DEPT_PREFIX + "list");
        List<DepartmentVo> voList = new ArrayList<>();
        for(Dept d:list){
            DepartmentVo dt = new DepartmentVo(d.getDeptId(),d.getParentId(),d.getDeptName());
            voList.add(dt);
        }
        return DepartmentVo.departmentTreeInfo(voList);
    }

    @Override
    public Map<String, Object> deptChannel(String channelName,String deptId,  String start, String size) {
        Map<String, String> map = new HashMap<>();
        map.put("channelName", channelName);
        map.put("deptId", deptId);
        Map<String, Object> result= daoHelper.queryForPageList(_MIDDLE_TABLE_PREFIX + "deptChannel", map);
        return result;
    }

    /**
     * 校验权限后获取当前用户的所属机构id
     * 如果该渠道是总行大数据渠道，则返回null
     * @param userId 登录用户id
     * @return 用户所属机构id
     */
    @Override
    public String getChannelIdByUserId(String userId) {
        ModelGroupChannelVo vo = (ModelGroupChannelVo) daoHelper.queryOne(_DEPT_PREFIX + "getChannelIdByUserId", userId);
        if (vo.getChannelName().contains("大数据")) {
            if (vo.getDeptName().contains("总行")) {
                return null;
            }
        }
        return vo.getChannelId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult delete(String deptId) {
        List<Channel> list = daoHelper.queryForList(_DEPT_PREFIX + "selectChannelByDeptId", deptId);
            if (list.size()>0){
                   return ResponseResult.createFailInfo("当前机构下存在渠道 无法删除");
            }
             if (deptId == null) {
            return ResponseResult.createFailInfo("请求参数不能为null");
            }
            daoHelper.delete(_DEPT_PREFIX + "deleteByPrimaryKey", deptId);
            deptPathService.delete(deptId);

            return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult update(Dept dept, String loginUserId) {
        if (dept == null) {
            return ResponseResult.createFailInfo("请求参数不能为null");
        }
        if (!isExists(dept)) {
            return ResponseResult.createFailInfo("组织机构不存在， dept: " + dept.getDeptId());
        }
        dept.setUpdateDate(new Date());
        dept.setUpdatePerson(loginUserId);
        String deptId = dept.getDeptId();
        Dept old = selectByPrimaryKey(deptId);
        daoHelper.update(_DEPT_PREFIX + "updateByPrimaryKeySelective", dept);
        if (!(old.getParentId() == null && dept.getParentId() == null)) {
            deptPathService.delete(deptId);
            deptPathService.save(deptId, dept.getParentId());
        }
        return ResponseResult.createSuccessInfo();
    }

    public boolean isExists(Dept dept) {
        final Dept resultDept = selectByPrimaryKey(dept.getDeptId());
        return resultDept == null ? false : true;
    }



    public Dept selectByPrimaryKey(String deptId) {
        return (Dept) daoHelper.queryOne(_DEPT_PREFIX + "selectByPrimaryKey", deptId);
    }

    public Dept findByName(String deptName) {
        return (Dept) daoHelper.queryOne(_DEPT_PREFIX + "findByName", deptName);
    }
}
