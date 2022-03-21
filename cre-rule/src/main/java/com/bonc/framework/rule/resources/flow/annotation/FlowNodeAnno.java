package com.bonc.framework.rule.resources.flow.annotation;

import java.lang.annotation.*;

/**
 * FlowNode的注解，当扩展标签时，在类的前面加上此注解即可
 * type为必填项，与前端扩展的节点的类型相对应。
 * tagName为选填，若想生成的bpmn中有此标签，则在此变量上指定标签名。
 * executeClass为选填，若此节点对应有执行器实例，将执行器包名.类名写于此。
 *
 * @author qxl
 * @version 1.0.0
 * @date 2016年11月23日 下午4:59:53
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FlowNodeAnno {
    /**
     * 对应前端形状的类型
     *
     * @return
     */
    String type();

    /**
     * 对应bpmn中标签的名字
     *
     * @return
     */
    String tagName() default "";

    /**
     * 对应节点的执行器
     *
     * @return
     */
    String executeClass() default "";
}
