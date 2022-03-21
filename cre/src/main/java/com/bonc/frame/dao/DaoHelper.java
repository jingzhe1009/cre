package com.bonc.frame.dao;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库查询DAO，封装了基本的查询方法，包括增删改查分页查等。
 * 使用此类时，使用@Resource注解注入。
 *
 * @author qxl
 * @version 1.0.0
 * @date 2017年1月10日 上午10:23:07
 */

@Component
public class DaoHelper {

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    /**
     * 根据条件删除记录
     *
     * @param _mybitsId
     * @param obj
     * @return
     */
    public <T> int delete(String _mybitsId, T obj) {
        return sqlSessionTemplate.delete(_mybitsId, obj);
    }

    public <T> int delete(String _mybitsId) {
        return sqlSessionTemplate.delete(_mybitsId);
    }

    /**
     * 将指定的对象插入到数据库
     *
     * @param _mybitsId
     * @param obj
     * @return
     */
    public <T> int insert(String _mybitsId, T obj) {
        return sqlSessionTemplate.insert(_mybitsId, obj);
    }

    public <T> int insert(String _mybitsId) {
        return sqlSessionTemplate.insert(_mybitsId);
    }

    /**
     * 批量将指定的对象插入到数据库
     *
     * @param _mybitsId
     * @param obj
     * @return
     */
    public <T> int insertBatch(String _mybitsId, List<T> obj) {
        return sqlSessionTemplate.insert(_mybitsId, obj);
    }

    /**
     * 更新
     *
     * @param _mybitsId
     * @param obj
     * @return
     */
    public <T> int update(String _mybitsId, T obj) {
        return sqlSessionTemplate.update(_mybitsId, obj);
    }

    /**
     * 无条件查询结果集，返回List<T>
     *
     * @param _mybitsId
     * @return
     */
    public <T> List<T> queryForList(String _mybitsId) {
        return sqlSessionTemplate.selectList(_mybitsId);
    }

    /**
     * 查询结果集，查询参数为Map，返回List<T>
     *
     * @param _mybitsId
     * @param _params
     * @return
     */
    public <T> List<T> queryForList(String _mybitsId, Map<String, Object> _params) {
        return sqlSessionTemplate.selectList(_mybitsId, _params);
    }

    /**
     * 查询结果集，查询参数为Object，返回List<T>
     *
     * @param _mybitsId
     * @param _params
     * @return
     */
    public <T> List<T> queryForList(String _mybitsId, Object _params) {
        return sqlSessionTemplate.selectList(_mybitsId, _params);
    }

    /**
     * 分页查询 , 如果传入的start,和size 都为空, 则不分页
     *
     * @param _mybitsId
     * @param _params   查询参数
     * @param start     从第几条数据开始查
     * @param size      每页显示的条数
     * @return Map<String ,   Object>--total：总条数，data：查询结果
     */
    public <T> Map<String, Object> queryForPageList(String _mybitsId, Object _params, String start, String size) {
        List<T> result;
        long total = 0;

        if (StringUtils.isBlank(start) && StringUtils.isBlank(size)) {
            result = sqlSessionTemplate.selectList(_mybitsId, _params);
            total = result.size();
        } else {
            int pageSize = Integer.parseInt((size == null || "0".equals(size)) ? "5" : size);
            int currentPage = Integer.parseInt((start == null) ? "0" : start) / pageSize + 1;
            PageHelper.startPage(currentPage, pageSize);
            List<T> selectResult = sqlSessionTemplate.selectList(_mybitsId, _params);
            PageInfo<T> page = new PageInfo<T>(selectResult);
            total = page.getTotal();
            result = page.getList();
            PageHelper.clearPage();
        }
        Map<String, Object> resultMap = new HashMap<String, Object>();

        resultMap.put("recordsTotal", total);
        resultMap.put("recordsFiltered", total);
        resultMap.put("total", total);
        resultMap.put("data", result);
        return resultMap;
    }

    /**
     * 分页查询--默认从第一页开始查，每页5条
     *
     * @param _mybitsId
     * @param _params   查询参数
     */
    public <T> Map<String, Object> queryForPageList(String _mybitsId, Object _params) {
        return queryForPageList(_mybitsId, _params, null, null);
    }


    /**
     * 查询一条记录
     *
     * @param _mybitsId
     * @param object
     * @return
     */
    public Object queryOne(String _mybitsId, Object object) {
        Object one = sqlSessionTemplate.selectOne(_mybitsId, object);
        return one;
    }


    /**
     * 获取sqlsessiontemplate
     */
    public SqlSessionTemplate getSqlSessionTemplate() {
        return this.sqlSessionTemplate;
    }
}
