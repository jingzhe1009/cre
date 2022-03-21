package com.bonc.frame.engine.builder;

import com.bonc.frame.util.CollectionUtil;
import com.bonc.frame.util.MapBeanUtil;
import com.bonc.framework.entity.EntityEngine;
import com.bonc.framework.entity.EntityEngineFactory;
import com.bonc.framework.entity.builder.entity.EntityRelation;
import com.bonc.framework.entity.builder.entity.Variable;
import com.bonc.framework.rule.exception.LoadContextException;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 实体引擎构造器
 *
 * @author qxl
 * @version 1.0
 * @date 2018年4月2日 上午10:44:48
 */
public class EntityEngineBuilder extends AbstractEngineBuilder {
    Log log = LogFactory.getLog(getClass());

    private static final String _mybitsId = "com.bonc.frame.engine.mapper.EntityEngineMapper.";

    private static final String _RULE_MAPPER = "com.bonc.frame.engine.mapper.RuleEngineMapper.";

    @Override
    public void build(String folderId, String ruleId, boolean isTest) throws Exception {
        try {
            EntityEngine entityEngine = EntityEngineFactory.getInstance().createEntityEngine(folderId);//创建实体引擎
            // 获取场景下对象类型变量
            List<Map<String, String>> list1 = this.getDaoHelper().queryForList(_mybitsId +
                    "selectEntityProperties", folderId);
            // 实体关系列表
            List<Map<String, String>> list2 = this.getDaoHelper().queryForList(_mybitsId +
                    "selectEntityRel", folderId);

            List<Variable> variableList = MapBeanUtil.convertMap2List(Variable.class, list1);
//		System.out.println(variableList);
            List<EntityRelation> entityRelList = MapBeanUtil.convertMap2List(EntityRelation.class, list2);
//		System.out.println(entityRelList);


            // 找到场景引用的所有（包括私有、公有）接口所引用的公共实体
            List<String> pubEntityIds = this.getDaoHelper().queryForList(_RULE_MAPPER +
                    "getPubEntityVarInApiByFolderId", folderId);

            if (!CollectionUtil.isEmpty(pubEntityIds)) {
                Set<Variable> variableSet = new HashSet<>();
                Set<EntityRelation> entityRelationSet = new HashSet<>();
                for (String entityId : pubEntityIds) {
                    // 获取实体类型变量及其子变量
                    List<Map<String, String>> pubFlatEntity = this.getDaoHelper().queryForList(_mybitsId +
                            "selectPubFlatEntity", entityId);
                    List<Variable> variableList2 = MapBeanUtil.convertMap2List(Variable.class, pubFlatEntity);
                    variableSet.addAll(variableList2);

                    // 获取实体变量及其子实体变量之间的关系
                    List<Map<String, String>> pubEntityRel = this.getDaoHelper().queryForList(_mybitsId +
                            "selectEntityRelByEntityId", entityId);
                    List<EntityRelation> entityRelList2 = MapBeanUtil.convertMap2List(EntityRelation.class, pubEntityRel);
                    entityRelationSet.addAll(entityRelList2);
                }

                variableList.addAll(variableSet);
                entityRelList.addAll(entityRelationSet);
            }
            if (entityEngine == null) {
                throw new Error("实体引擎为空");
            }
            entityEngine.buildAllEntity(variableList, entityRelList);
            EntityEngineFactory.getInstance().putEntityEngine(folderId, entityEngine);
        } catch (Exception e) {
            log.error("加载模型引用的实体类型参数失败。" + e.getMessage(), e);
            throw new LoadContextException("加载模型引用的实体类型参数失败,失败原因:[" + e.getMessage() + "]", e);
        }
    }

    @Override
    public void clean(String folderId, String ruleId) {
        super.clean(folderId, ruleId);
        EntityEngineFactory.getInstance().removeEntityEngine(folderId);
    }


}
