package com.bonc.frame.engine.builder;

import com.bonc.frame.dao.DaoHelper;
import com.bonc.frame.engine.cache.DefaultBuildCache;
import com.bonc.framework.rule.cache.ICache;
import org.apache.commons.lang3.StringUtils;

/**
 * @author qxl
 * @version 1.0
 * @date 2018年4月2日 上午10:45:46
 */
public abstract class AbstractEngineBuilder implements IEngineBuilder {

    //是否已经builder
    protected ICache<String, String> isBuildCache = new DefaultBuildCache<String, String>();

    private DaoHelper daoHelper;

    public DaoHelper getDaoHelper() {
        return daoHelper;
    }

    public void setDaoHelper(DaoHelper daoHelper) {
        this.daoHelper = daoHelper;
    }

    @Override
    public boolean isBuild(String folderId, String ruleId) {
        if ("true".equals(isBuildCache.get(folderId))) {
            return true;
        }
        return false;
    }

    @Override
    public void clean(String folderId, String ruleId) {
        if (isBuildCache.containsKey(folderId)) {
            isBuildCache.remove(folderId);
        }
    }

    @Override
    public void builder(String folderId, String ruleId) throws Exception {
        this.builder(folderId, ruleId, false);
    }

    @Override
    public void builder(String folderId, String ruleId, boolean isTest) throws Exception {
        if (StringUtils.isBlank(ruleId)) {
            throw new IllegalArgumentException("参数[ruleId]不能为空");
        }
        if (StringUtils.isBlank(folderId)) {
            throw new IllegalArgumentException("参数[folderId]不能为空");
        }
        if (isBuild(folderId, ruleId)) {
            //判断是否已经build
            return;
        }
        //开始build
        build(folderId, ruleId, isTest);
        //build结束
        buildOver(folderId, ruleId);
    }

    abstract void build(String folderId, String ruleId, boolean isTest) throws Exception;

    @Override
    public void buildOver(String folderId, String ruleId) {
        isBuildCache.put(folderId, "true");

    }

    public ICache<String, String> getIsBuildCache() {
        return isBuildCache;
    }

    public void setIsBuildCache(ICache<String, String> isBuildCache) {
        this.isBuildCache = isBuildCache;
    }

}
