package com.bonc.frame.service.variable;

import com.bonc.frame.entity.commonresource.VariableGroup;
import com.bonc.frame.entity.commonresource.VariableGroupExt;
import com.bonc.frame.entity.variable.Variable;
import com.bonc.frame.entity.variable.VariableTreeNode;
import com.bonc.frame.entity.variableentity.VariableEntity;
import com.bonc.frame.entity.variableentityrelation.VariableEntityRelation;
import com.bonc.frame.util.ResponseResult;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface VariableService {

    boolean isUsed(String variableId);

    ResponseResult checkUsed(String variableId);

    Map<String, Object> selectVariables(Variable variable, String start, String size);

    List<Variable> selectVariableByVariableCodeList(List<String> variableCodeList, String folderId, List<String> type);

    Variable selectVariableByVariableCode(String variableId, String variableCode, String variableAlias, String entityId, String isPlulic, String folderId, List<String> type);

    Map<String, Object> selectVariablesByFolderId(Variable variable, String start, String size);

    Map<String, Object> selectFlatVariablesByFolderId(Variable variable, String start, String size);

    Variable selectVariableByKey(String variableId);

    List<Variable> selectVariableByVariableIdList(List<String> variableIdList);

    Map<String, Variable> selectVariableByKeyBatch(List<String> variableId);

    /**
     * 给参数重新修改参数Id持久化参数
     *
     * @param variable
     * @return
     */
    ResponseResult insertVariable(Variable variable);

    /**
     * 持久化参数
     */
    void instreVariableDataPersistence(Variable variable);

    /**
     * 持久化参数, 如果是实体类型 , 添加实体-属性的关系
     */
    void instreVariableTreeNodeDataPersistence(VariableTreeNode variable);

    ResponseResult updateVariable(Variable variable);

    ResponseResult deleteVariable(Variable variable);

    ResponseResult isDeleteEntity(Variable variable);

    List<Map<String, Object>> getVariableType();

    Map<String, Object> selectVariableEntityRelation(String entityId, String start, String size);

    ResponseResult insertVariableRelation(Variable variable, VariableEntity variableEntity, VariableEntityRelation variableEntityRelation);

    ResponseResult updateVariableRelation(Variable variable, VariableEntity variableEntity, VariableEntityRelation variableEntityRelation);

    ResponseResult deleteVariableRelation(String variableId, String entityId);

    /**
     * 获取公共变量树
     *
     * @return
     */
    Map<String, Object> getPubVariableTree();

    Map<String, Object> getPubVariableAndKpiTree();

    /**
     * 获取变量树
     *
     * @param folderId
     * @return
     */
    public Map<String, Object> getVariableTree(String folderId);

    public Map<String, Object> getVariableTree_new(String folderId);

    /**
     * 获取变量种类
     */
    public List<Map<String, Object>> getVariableKind();

    /**
     * 获取文件夹下所有的实体类型
     *
     * @return
     */
    public List<Map<String, Object>> getEntityType(String folderId);

    /**
     * 根据规则Id获取变量列表
     *
     * @param ruleId
     * @param folderId
     * @return
     */
    public List<Map<String, Object>> getVariableByRuleId(String ruleId, String folderId);

    /**
     * 获取规则包中所有基本类型的变量
     *
     * @param folderId
     * @return
     */
    public List<Map<String, Object>> getBaseVariable(String folderId);

    /**
     * 判断参数集合中是否包含私有变量
     *
     * @param variableIdList
     * @param folderId
     * @return 有私有变量返回 {@code true}
     */
    boolean checkExitsPrivateVariable(Collection<String> variableIdList, final String folderId);

    List<String> getNestedVariableIds(String entityId);

    List<String> getNestedVariableIdsByEntityIds(List<String> entityIds);

    // ------------------------------ 公共参数 ------------------------------

    Map<String, Object> pubSelectVariables(String variableAlias, String variableGroupName,
                                           String variableTypeId, String kindId,
                                           String startDate, String endDate,
                                           String start, String size);

    Map<String, Object> pubSelectFlatVariables(String variableAlias, String variableGroupName,
                                               String variableTypeId, String kindId,
                                               String startDate, String endDate,
                                               String start, String size);

    Map<String, Object> pagedPubVariableResources(String variableAlias, String variableGroupName,
                                                  String variableTypeId,
                                                  String startDate, String endDate,
                                                  String start, String size);

    List<VariableGroupExt> pubSelectFlatVariables(String variableAlias,
                                                  String variableGroupName,
                                                  String variableTypeId,
                                                  String startDate, String endDate);

    List<Map<String, Object>> getVariableMapByRuleId(String ruleName, String ruleId, String isPublic, String kindId);

    /**
     * 获取模型引用的所有非实体公共参数 包括接口、指标,引用的参数Map<code:别名>
     *
     * @return Map<code:别名>
     */
    Map<String, Object> getVariableCodeAndNameMap(String ruleName, String ruleId);

    List<Map<String, Object>> getVariableMapByRuleId(String ruleId, String kindId);

    List<Map<String, Object>> getOutVariableMapByRuleId(String ruleId);

    List<Map<String, Object>> getPubVariableMapWithoutKpiByRuleId(String ruleId);

    List<Map<String, Object>> getVariableMapWithoutKpiByRuleId(String ruleId, String kindId);

    ResponseResult pubVariablesByRule(String ruleId);

    ResponseResult pubVariablesByFolder(String folderId);

    ResponseResult pubInsert(Variable variable, String userId);

    ResponseResult pubInsertVariableRelation(Variable variable, VariableEntity variableEntity,
                                             VariableEntityRelation variableEntityRelation,
                                             String userId);

    // 将私有变量变为公有变量
    ResponseResult upgrade(Variable variable, String variableGroupId, String currentUser);
    
    ResponseResult batchUpgrade(Variable variable, String variableGroupId, String currentUser);

    ResponseResult pubInsertVariableGroup(VariableGroup variableGroup, String userId);

    void insertVariableGroupDataPersistence(VariableGroup variableGroup);

    ResponseResult pubUpdateVariableGroup(VariableGroup variableGroup, String userId);

    ResponseResult pubDeleteVariableGroup(String variableGroupId);

    ResponseResult pubVariableGroups(String variableGroupName);

    VariableGroup getVariableGroupById(String variableGroupId, String variableGroupName);

    Map<String, Object> pubVariableGroupsPaged(String variableGroupName, String start, String length);

    ResponseResult pubSelectVariablesByGroupId(String variableGroupId);

    // ------------------------ VariableTreeNode ------------------------------------------
    List<VariableTreeNode> getApiReturnVariableTree(String apiId, String apiValueType);

    /**
     * 查询参数,以及参数的下层所有参数
     * VariableTreeNode.variableNestedIdList中存放改参数的子节点的参数Id
     */
    List<VariableTreeNode> selectVariableNodeWithNestedByVariableProperty(String variableId,
                                                                          String entityId,
                                                                          String variableAlias,
                                                                          String variableCode,
                                                                          String typeId,
                                                                          String isPublic,
                                                                          String folderId,
                                                                          String variableGroupId);

    /**
     * 返回 id:VariableTreeNode
     * VariableTreeNode中的variableNestedIdList中存放改参数的子节点的参数Id
     *
     * @param variableTreeNode 通过该参数,获取这个参数的下层所有参数;
     * @return
     */
    public Map<String, VariableTreeNode> selectVariableTreeNodeWithNested(VariableTreeNode variableTreeNode);

    List<VariableTreeNode> selectVariableTreeNodeWithNestedByVariableIdListy(List<String> variableIdList);

    /**
     * 查询参数,不包括下层的参数
     */
    public List<VariableTreeNode> selectVariableNodeByVariableProperty(String variableId,
                                                                       String entityId,
                                                                       String variableAlias,
                                                                       String variableCode,
                                                                       String isPublic,
                                                                       String folderId,
                                                                       String variableGroupId);

//    List<Variable> getVariableByApiId(String apiId);
}
