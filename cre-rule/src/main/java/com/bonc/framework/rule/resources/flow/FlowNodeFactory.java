package com.bonc.framework.rule.resources.flow;

import com.bonc.framework.rule.resources.flow.annotation.FlowNodeAnno;
import com.bonc.framework.rule.resources.flow.annotation.LoadFlowNodeClasses;
import com.bonc.framework.util.FrameLogUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 根据类型实例化FlowNode节点，当添加结点类型时，只需在static中put即可。
 *
 * @author qxl
 * @version 1.0.0
 * @date 2016年11月14日 下午6:05:12
 */
public class FlowNodeFactory {
    public static final String conditionKey = "condition";

    /**
     * 线
     */
    public static final String TYPE_PATH = "path";
    /**
     * 开始
     */
    public static final String TYPE_START = "start";
    /**
     * 结束
     */
    public static final String TYPE_END = "end";
    /**
     * 分支
     */
    public static final String TYPE_FORK = "fork";


    public static final String TYPE_ROOTELE_TAG = "rootElement";
    public static final String TYPE_VAR_TAG = "property";
    public static final String TYPE_PROCESS_TAG = "process";
    public static final String TYPE_SEQUENCEFLOW_TAG = "sequenceFlow";

    /**
     * 决策树中生成的drl规则的groupid前缀
     */
    public static final String ruleFlowGroupHeader = "group_";
    /**
     * 决策树中生成的drl规则的id前缀
     */
    public static final String ruleFlowRuleHeader = "rule_";

    private static Map<String, String> flowMap = new HashMap<String, String>();

    private static Map<String, String> flowTagMap = new HashMap<String, String>();

    private static Map<String, String> flowExecuteClassMap = new HashMap<String, String>();

    private static String[] scanPackage = {"com.bonc.framework.rule.resources.flow.basicflow"};

    static {
        // 扫描指定包下包含FlowNodeAnno的类，并根据注解缓存各类型节点的类名、标签、执行器信息
        LoadFlowNodeClasses loadFlowNodeClasses = new LoadFlowNodeClasses(scanPackage, FlowNodeAnno.class);
        Set<Class<?>> classSet = null;
        try {
            classSet = loadFlowNodeClasses.getClassSet();
            for (Class<?> clazz : classSet) {
//				System.out.println(clazz.getName());
                FlowNodeAnno flowNode = (FlowNodeAnno) clazz.getAnnotation(FlowNodeAnno.class);
                if (flowNode != null) {
                    String className = clazz.getName();//获取类名
                    String type = flowNode.type();//获取FlowNode类型
                    String tagName = flowNode.tagName();//获取对应标签名
                    String executeClass = flowNode.executeClass();//获取对应执行器类名
                    flowMap.put(type, className);
                    if (tagName != null && !tagName.isEmpty()) {
                        flowTagMap.put(type, tagName);
                    }
                    if (executeClass != null && !executeClass.isEmpty()) {
                        flowExecuteClassMap.put(type, executeClass);
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            FrameLogUtil.error(FlowNodeFactory.class, e.getMessage());
        } catch (IOException e) {
            FrameLogUtil.error(FlowNodeFactory.class, e.getMessage());
        }

        flowTagMap.put(TYPE_ROOTELE_TAG, "definitions");
        flowTagMap.put(TYPE_VAR_TAG, "property");
        flowTagMap.put(TYPE_PROCESS_TAG, "process");
        flowTagMap.put(TYPE_SEQUENCEFLOW_TAG, "sequenceFlow");
    }

    /**
     * 根据节点类型，实例化FlowNode对象
     *
     * @param type
     * @return
     * @throws Exception
     */
    public static FlowNode getFlowNode(String type) throws Exception {
        if (!flowMap.containsKey(type)) {
            throw new Exception("The type of [" + type + "] is illegal.");
        }
        String classStr = flowMap.get(type);
        FlowNode flowNode = (FlowNode) Class.forName(classStr).newInstance();
        return flowNode;
    }

    /**
     * 根据节点类型，获取对应标签名
     *
     * @param type
     * @return
     * @throws Exception
     */
    public static String getFlowTag(String type) throws Exception {
        if (!flowTagMap.containsKey(type)) {
            throw new Exception("The type of [" + type + "] is illegal.");
        }
        return flowTagMap.get(type);
    }

    /**
     * 根据节点类型，获取对应执行器类名
     *
     * @param type
     * @return
     * @throws Exception
     */
    public static String getExecuteClass(String type) throws Exception {
        if (!flowExecuteClassMap.containsKey(type)) {
            throw new Exception("The type of [" + type + "] is illegal.");
        }
        return flowExecuteClassMap.get(type);
    }

    /**
     * 根据节点类型，获取对应执行器Map
     *
     * @param type
     * @return
     * @throws Exception
     */
    public static Map<String, String> getExecuteClassMap() {
        return flowExecuteClassMap;
    }
}

