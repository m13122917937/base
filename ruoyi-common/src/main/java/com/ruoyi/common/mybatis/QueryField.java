package com.ruoyi.common.mybatis;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 声明查询字段的列名、操作符及空字符串策略；未标注的业务字段默认使用等值查询。 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface QueryField {
    /** 返回该字段使用的操作符，默认等值查询。 */
    DynamicCondition.Operator operator() default DynamicCondition.Operator.EQ;

    /** 返回服务端固定列名，空值表示使用属性名；支持表别名，驼峰会转为下划线。 */
    String field() default "";

    /** 返回是否查询空字符串或纯空白字符串；null 始终忽略。 */
    boolean queryEmpty() default false;

    /** 返回是否完全忽略该字段；忽略字段不会读取值或生成 SQL。 */
    boolean ignore() default false;
}
