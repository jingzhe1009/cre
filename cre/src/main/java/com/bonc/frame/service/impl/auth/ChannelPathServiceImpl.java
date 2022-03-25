package com.bonc.frame.service.impl.auth;

import com.bonc.frame.dao.DaoHelper;
import com.bonc.frame.entity.auth.ChannelPath;
import com.bonc.frame.service.auth.ChannelPathService;
import com.bonc.frame.util.IdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service("ChannelPathService")
public class ChannelPathServiceImpl implements ChannelPathService {

    @Autowired
    private DaoHelper daoHelper;

    private final String _CHANNELPATH_PREFIX = "com.bonc.frame.mapper.auth.ChannelPathMapper.";

    @Override
    @Transactional
    public void save(String childId, String parentId) {
        final String channelPathId = IdUtil.createId();
        ChannelPath channelPath = new ChannelPath();
        channelPath.setChildId(childId);
        channelPath.setParentId(parentId);
        daoHelper.insert(_CHANNELPATH_PREFIX + "insertNew", channelPath);
    }

    @Override
    @Transactional
    public void delete(String channelId) {
        daoHelper.delete(_CHANNELPATH_PREFIX + "deleteByChannelId", channelId);
    }

    private boolean hasParent(String channelId) {
        if (parentIds(channelId).size() > 0) return true;
        return false;
    }

    private List<ChannelPath> parentIds(String channelId) {
        List<ChannelPath> channelPaths = daoHelper.queryForList(_CHANNELPATH_PREFIX + "selectByChildId", channelId);
        return channelPaths;
    }
}
