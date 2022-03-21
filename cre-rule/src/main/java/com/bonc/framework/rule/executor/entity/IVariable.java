package com.bonc.framework.rule.executor.entity;

/**
 * 参数抽象类 包括参数和指标（特殊参数）
 *
 * @author yedunyao
 * @date 2019/10/29 17:24
 * @see VariableExt
 * @see com.bonc.framework.rule.executor.entity.kpi.KpiDefinition
 */
public interface IVariable {

    String VARTYPE_STRING = "1";
    String VARTYPE_INT = "2";
    String VARTYPE_OBJ = "3";
    String VARTYPE_FLOAT = "4";
    String VARTYPE_ARRAY = "5";

    /**
     * 唯一标识
     */
    String getId();

    /**
     * 编码
     */
    String getCode();

    /**
     * 名称
     */
    String getName();

    /**
     * 获取数据类型id
     *
     * @return
     */
    String getType();

    /**
     * 获取默认值
     */
    String getDefaultValue();

    /**
     * 获取在模型中使用的参数编码
     * <p>
     * 当模型使用参数{@link VariableExt}时，
     * 可能会使用参数编码{@link IVariable#getCode()}或嵌套类型参数的编码别名{@link IVariable#getVarCode()}；
     * 当模型使用参数{@link com.bonc.framework.rule.executor.entity.kpi.KpiDefinition}时，
     * 使用指标编码{@link IVariable#getCode()}
     *
     * @return
     */
    String getVarCode();

}
