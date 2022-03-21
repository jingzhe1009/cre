package com.bonc.framework.rule.resources.flow.basicflow;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bonc.framework.rule.RuleEngineFactory;
import com.bonc.framework.rule.executor.entity.IVariable;
import com.bonc.framework.rule.executor.entity.VariableExt;
import com.bonc.framework.rule.executor.entity.kpi.KpiDefinition;
import com.bonc.framework.rule.resources.flow.FlowNode;
import com.bonc.framework.rule.resources.flow.FlowNodeStateEnum;
import com.bonc.framework.rule.resources.flow.SerializeForm;
import com.bonc.framework.util.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author qxl
 * @version 1.0.0
 * @date 2016年11月14日 上午9:56:35
 */
public abstract class AbstractFlowNode implements FlowNode, SerializeForm,
        Serializable {
    private static final long serialVersionUID = -7547289093683163227L;

    protected String nodeId;
    protected String ruleId;
    protected String folderId;
    protected String nodeType;
    protected String nodeName;

    protected FlowNodeStateEnum nodeState;
//    protected transient ThreadLocal<FlowNodeStateEnum> nodeState;

    protected int maxNodeDeep;//节点最大深度

    protected int inDegree;    // 节点入度
//    protected transient ThreadLocal<Integer> inDegree;    // 节点入度

    public AbstractFlowNode() {
//        this.nodeState = new ThreadLocal<>();
//        this.inDegree = new ThreadLocal<>();
    }

    //位置信息
    private String x;
    private String y;
    private String width;
    private String height;

    protected List<FlowNode> childFlowNodes;
    protected List<FlowNode> parentFlowNodes;

    //遍历FlowNode时需要的属性
    protected FlowNode currectNode;
    protected FlowNode parentNode;

    private Map<String, KpiDefinition> needKpis;
    private Map<String, VariableExt> needVariables;

    public FlowNode getCurrectNode() {
        return currectNode;
    }

    public void setCurrectNode(FlowNode currectNode) {
        this.currectNode = currectNode;
    }

    public FlowNode getParentNode() {
        return parentNode;
    }

    public void setParentNode(FlowNode parentNode) {
        this.parentNode = parentNode;
    }

    @Override
    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public FlowNodeStateEnum getNodeState() {
        return nodeState;
    }

    public void setNodeState(FlowNodeStateEnum nodeState) {
        this.nodeState = nodeState;
    }

    public int getMaxNodeDeep() {
        return maxNodeDeep;
    }

    public void setMaxNodeDeep(int maxNodeDeep) {
        this.maxNodeDeep = maxNodeDeep;
    }


    @Override
    public boolean isSlowNode() {
        return false;
    }

    public int inDegree() {
        return inDegree;
    }

    public void setInDegree(int inDegree) {
        this.inDegree = inDegree;
    }

//    public void removeThisThreadLocal() {
//        if (this.inDegree != null) {
//            this.inDegree.remove();
//        }
//        if (this.nodeState != null) {
//            this.nodeState.remove();
//        }
//    }
//
//    public void removeAllThreadLocal() {
//        removeThisThreadLocal();
//        if (childFlowNodes != null && !childFlowNodes.isEmpty()) {
//            for (FlowNode flowNode : childFlowNodes) {
//                flowNode.removeAllThreadLocal();
//            }
//        }
//    }


    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    @Override
    public List<FlowNode> getChildFlowNodes() {
        return this.childFlowNodes;
    }

    @Override
    public void setChildFlowNodes(List<FlowNode> nodes) {
        this.childFlowNodes = nodes;
    }

    @Override
    public void setParentFlowNodes(List<FlowNode> nodes) {
        this.parentFlowNodes = nodes;
    }

    @Override
    public List<FlowNode> getParentFlowNodes() {
        return this.parentFlowNodes;
    }

    public Map<String, KpiDefinition> getNeedKpis() {
        return needKpis;
    }

    public void setNeedKpis(Map<String, KpiDefinition> needKpis) {
        this.needKpis = needKpis;
    }

    public void putNeedKpi(String kpiId, KpiDefinition kpiDefinition) {
        if (needKpis == null) {
            needKpis = new HashMap<>();
        }
        needKpis.put(kpiId, kpiDefinition);
    }

    public Map<String, VariableExt> getNeedVariables() {
        return needVariables;
    }

    public void setNeedVariables(Map<String, VariableExt> needVariables) {
        this.needVariables = needVariables;
    }

    public void putNeedVariable(String variableId, VariableExt variableExt) {
        if (needVariables == null) {
            needVariables = new HashMap<>();
        }
        needVariables.put(variableId, variableExt);
    }

    @Override
    public String parseToJson() {
        return null;
    }

    @Override
    public void serialize(Map<String, String> data) {
        return;
    }

    @Override
    public String toString() {
        return "AbstractFlowNode{" +
                "nodeId='" + nodeId + '\'' +
                ", nodeType='" + nodeType + '\'' +
                ", nodeName='" + nodeName + '\'' +
                ", nodeState=" + nodeState +
                ", maxNodeDeep=" + maxNodeDeep +
                ", inDegree=" + inDegree +
                '}';
    }

    @Override
    public Element parseToXml(Element ele, Map<String, String> data) {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof AbstractFlowNode) {
            AbstractFlowNode abstObj = (AbstractFlowNode) obj;
            if (abstObj.getNodeId().equals(this.getNodeId())) {
                return true;
            }
        }
        return false;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    // ---------------------- 处理规则表达式 ----------------------

    protected IVariable getIVariable(String iVariableId, boolean isKpi) {
        IVariable iVariable;
        if (!isKpi) {
            iVariable = getVariableExt(iVariableId);
            if (iVariable == null) {
                throw new IllegalStateException("变量不存在，value: " + iVariableId);
            }
        } else {
            iVariable = getKpiDefinition(iVariableId);
            if (iVariable == null) {
                throw new IllegalStateException("指标不存在，value: " + iVariableId);
            }
            putNeedKpi(iVariableId, (KpiDefinition) iVariable);
        }
        return iVariable;
    }

    protected VariableExt getVariableExt(String variableId) {
        //从缓存中获取文件夹下面所有的规则变量
        List<VariableExt> variableExts = RuleEngineFactory.getRuleEngine().getConContext().queryVariables(this.getFolderId());
        for (VariableExt variableExt : variableExts) {
            if (variableId.equals(variableExt.getVariableId())) {
                return variableExt;
            }
        }
        return null;
    }

    protected KpiDefinition getKpiDefinition(String kpiId) {
        //从缓存中获取文件夹下面所有的规则变量
        List<KpiDefinition> kpiDefinitions = RuleEngineFactory.getRuleEngine().getConContext()
                .queryKpiDefinitions(this.getRuleId());
        for (KpiDefinition kpiDefinition : kpiDefinitions) {
            if (kpiId.equals(kpiDefinition.getKpiId())) {
                return kpiDefinition;
            }
        }
        return null;
    }

    protected VariableExt getVariableExtByName(String variableRemark) {
        //从缓存中获取文件夹下面所有的规则变量
        List<VariableExt> variableExts = RuleEngineFactory.getRuleEngine().getConContext().queryVariables(this.getFolderId());
        for (VariableExt variableExt : variableExts) {
            if (variableRemark.equals(variableExt.getVariableAlias())) {
                return variableExt;
            }
        }
        return null;
    }

    protected KpiDefinition getKpiByName(String kpiName) {
        //从缓存中获取文件夹下面所有的规则变量
        List<KpiDefinition> kpiDefinitions = RuleEngineFactory.getRuleEngine().getConContext().queryKpiDefinitions(this.getRuleId());
        for (KpiDefinition kpiDefinition : kpiDefinitions) {
            if (kpiName.equals(kpiDefinition.getKpiName())) {
                return kpiDefinition;
            }
        }
        return null;
    }


    // ---------------------- 处理规则表达式 ----------------------

    protected String parseExpression(String cdt) {
        Map<String, Object> cdtDetailMap = null;
        try {
            // 单个规则条件
            cdtDetailMap = JsonUtils.stringToCollect(cdt);
        } catch (Exception e) {
//            throw new IllegalStateException("解析模型失败，规则条件格式错误。规则条件[" + cdt + "]");
        }
        if (cdtDetailMap != null && !cdtDetailMap.isEmpty()) {
            IVariable iVariable = getIVariableInCdtDetailMap(cdtDetailMap);
            String varCode = iVariable.getVarCode();
            String opt = (String) cdtDetailMap.get("opt");
            String value = getCompareRightExpression(cdtDetailMap, iVariable);
            cdt = varCode + opt + value;
        }
        return cdt;
    }

    /**
     * 获取表达式比较符左边的参数（参数或指标）
     *
     * @param cdtDetailMap
     * @return
     */
    protected IVariable getIVariableInCdtDetailMap(Map<String, Object> cdtDetailMap) {
        IVariable iVariable;
        if (cdtDetailMap.containsKey("varId")) {
            iVariable = getVariableInCdtDetailMap(cdtDetailMap);
        } else if (cdtDetailMap.containsKey("kpiId")) {
            iVariable = getKpiInCdtDetailMap(cdtDetailMap);
        } else {
            throw new RuntimeException("解析条件表达式失败，表达式中没有引用参数或指标");
        }
        return iVariable;
    }

    protected VariableExt getVariableInCdtDetailMap(Map<String, Object> cdtDetailMap) {
        String varId = (String) cdtDetailMap.get("varId");
        if (varId != null) {
            varId = varId.replace("[", "").replace("]", "");
        }
        VariableExt variableExt = getVariableExt(varId);
        if (variableExt == null) {
            throw new RuntimeException("解析条件表达式失败,无法找到参数[" + varId + "]");
        }
        putNeedVariable(varId, variableExt);
        return variableExt;
    }

    protected KpiDefinition getKpiInCdtDetailMap(Map<String, Object> cdtDetailMap) {
        String kpiId = (String) cdtDetailMap.get("kpiId");
        if (kpiId != null) {
            kpiId = kpiId.replace("[", "").replace("]", "");
        }
        KpiDefinition kpiDefinition = getKpiDefinition(kpiId);
        if (kpiDefinition == null) {
            throw new RuntimeException("解析条件表达式失败,无法找到指标[" + kpiId + "]");
        }
        putNeedKpi(kpiId, kpiDefinition);
        return kpiDefinition;
    }

    /**
     * 获取规则条件表达式比较符右边的表达式
     */
    protected String getCompareRightExpression(Map<String, Object> cdtDetailMap, IVariable leftIVariable) {
        String valueType = (String) cdtDetailMap.get("valueType");
        String value = (String) cdtDetailMap.get("value");
        switch (valueType) {
            case "t_variable":
                value = processVariable(cdtDetailMap, value);
                break;
            case "t_expRule":
                value = processExpression(cdtDetailMap, value);
                break;
            case "t_value":
                value = processValue(leftIVariable, value);
                break;
            default:
                throw new IllegalArgumentException("解析条件表达式失败，条件中存在不支持的值类型");
        }
        return value;
    }

    protected String processValue(IVariable variable, String value) {
        if (IVariable.VARTYPE_STRING.equals(variable.getType())) {
            value = "\"" + value + "\"";
        }
        return value;
    }

    protected String processVariable(Map<String, Object> cdtDetailMap, String value) {
        boolean isKpi = false;
        if (cdtDetailMap.containsKey("isKpi")) {
            isKpi = Boolean.valueOf(cdtDetailMap.get("isKpi").toString());
        }

        value = value.replace("[", "").replace("]", "");
        IVariable iVariable = getIVariable(value, isKpi);
        return iVariable.getVarCode();
    }

    protected String processExpression(Map<String, Object> cdtDetailMap, String value) {
        // 表达式
        JSONObject cdtDetailObject = (JSONObject) cdtDetailMap;
        JSONArray references = new JSONArray();
        if (cdtDetailObject.containsKey("references")) {
            references = cdtDetailObject.getJSONArray("references");
        }

        int variableNameLen = 0;

        int referenceLen = references.size();
        // 表达式中的指标名称
//        Map<String, String> variableNameMap = new LinkedHashMap<>();
        List<VaribleMapping> varibleMappings = new ArrayList<>(referenceLen);
        Pattern pattern = Pattern.compile("\\[(.*?)\\]");
        Matcher m = pattern.matcher(value);
        while (m.find()) {
            String replaceStr = m.group(0);
            String replace = m.group(1);
            variableNameLen++;
//            variableNameMap.put(replace, replaceStr);
            varibleMappings.add(new VaribleMapping(replace, replaceStr));
        }

//        Set<Map.Entry<String, String>> variableNameEntries = variableNameMap.entrySet();

        if (variableNameLen == 0) {
            return value;
        }

        //  兼容以前版本，当引用列表为空时，所有的参数默认为非指标参数
        if (referenceLen == 0) {
            // 按之前的处理
            for (VaribleMapping variableNameEntry : varibleMappings) {
                String variableName = variableNameEntry.variableName;
                String variableNamePlaceHolder = variableNameEntry.variableExpress;

                VariableExt variableExtValue = getVariableExtByName(variableName);
                if (variableExtValue == null) {
                    throw new RuntimeException("解析条件表达式失败，无法找到参数[" + variableName + "]");
                }
                String varCodeValue = variableExtValue.getVariableCode();
                String varAliasCodeValue = variableExtValue.getEntityVariableAlias();

                if (StringUtils.isBlank(varAliasCodeValue)) {
                    value = value.replace(variableNamePlaceHolder, varCodeValue);
                } else {
                    value = value.replace(variableNamePlaceHolder, varAliasCodeValue);
                }
            }
            return value;
        }

        if (referenceLen != variableNameLen) {
            throw new RuntimeException("解析条件表达式失败，条件表达式引用参数列表长度不一致");
        } else {
            int i = 0;
            for (VaribleMapping variableNameEntry : varibleMappings) {
                String variableName = variableNameEntry.variableName;
                String variableNamePlaceHolder = variableNameEntry.variableExpress;

                JSONObject jsonObject = references.getJSONObject(i);
//                String kpiId = jsonObject.getString("kpiId").replaceAll("\\[|\\]", "");
                String kpiId = getIVariableIdFromReference(jsonObject, "kpiId");
                if (kpiId != null) {
                    KpiDefinition kpiDefinition = getKpiDefinition(kpiId);
                    if (variableName.equals(kpiDefinition.getKpiName())) {
                        value = value.replace(variableNamePlaceHolder, kpiDefinition.getVarCode());
                    } else {
                        throw new RuntimeException("解析条件表达式失败，条件表达式引用参数列表指标名称不一致");
                    }
                    putNeedKpi(kpiId, kpiDefinition);
                } else {
//                    String varId = jsonObject.getString("varId").replaceAll("\\[|\\]", "");
                    String varId = getIVariableIdFromReference(jsonObject, "varId");
                    if (varId == null) {
                        throw new RuntimeException("解析条件表达式失败，条件表达式引用参数列表不合法");
                    }
                    VariableExt variableExt = getVariableExt(varId);
                    if (variableName.equals(variableExt.getVariableAlias())) {
                        value = value.replace(variableNamePlaceHolder, variableExt.getVarCode());
                    } else {
                        throw new RuntimeException("解析条件表达式失败，条件表达式引用参数列表参数名称不一致");
                    }
                }
                i++;
            }
        }
        return value;
    }

    private String getIVariableIdFromReference(JSONObject referenceObject, String id) {
        String idPlaceHolder = referenceObject.getString(id);
        if (StringUtils.isBlank(idPlaceHolder)) {
            return null;
        }
        return idPlaceHolder.replaceAll("\\[|\\]", "");
    }

    private void writeObject(java.io.ObjectOutputStream s)
            throws java.io.IOException {
        //执行 JVM 默认的序列化操作
        s.defaultWriteObject();
    }

    private void readObject(java.io.ObjectInputStream s)
            throws java.io.IOException, ClassNotFoundException {

        s.defaultReadObject();
//        this.nodeState = new ThreadLocal<>();
//        this.inDegree = new ThreadLocal<>();
    }

    class VaribleMapping {
        String variableName;
        String variableExpress;

        public VaribleMapping(String variableName, String variableExpress) {
            this.variableName = variableName;
            this.variableExpress = variableExpress;
        }
    }
}
