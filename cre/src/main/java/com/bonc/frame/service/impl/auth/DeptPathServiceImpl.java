package com.bonc.frame.service.impl.auth;

import com.bonc.frame.dao.DaoHelper;
import com.bonc.frame.entity.auth.DeptPath;
import com.bonc.frame.service.auth.DeptPathService;
import com.bonc.frame.util.IdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("deptPathService")
public class DeptPathServiceImpl implements DeptPathService {
    @Autowired
    private DaoHelper daoHelper;

    private final String _DEPTPATH_PREFIX = "com.bonc.frame.mapper.auth.DeptPathMapper.";

    @Override
    @Transactional
    public void save(String childId, String parentId) {
        final String deptPathId = IdUtil.createId();
        DeptPath deptPath = new DeptPath();
        deptPath.setChildId(childId);
        deptPath.setParentId(parentId);
        daoHelper.insert(_DEPTPATH_PREFIX + "insertNew", deptPath);
    }

    @Override
    @Transactional
    public void delete(String deptId) {
        daoHelper.delete(_DEPTPATH_PREFIX + "deleteByDeptId", deptId);
    }

    private boolean hasParent(String deptId) {
        if (parentIds(deptId).size() > 0) return true;
        return false;
    }

    private List<DeptPath> parentIds(String deptId) {
        List<DeptPath> deptPaths = daoHelper.queryForList(_DEPTPATH_PREFIX + "selectByChildId", deptId);
        return deptPaths;
    }
}
