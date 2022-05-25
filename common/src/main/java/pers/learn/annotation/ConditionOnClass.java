/*
 * @Author: konakona konakona@crazyphper.com
 * @Date: 2022-05-16 17:33:32
 * @LastEditors: konakona konakona@crazyphper.com
 * @LastEditTime: 2022-05-16 17:39:05
 * @Description: ClassCondition的注解实现
 * 
 * Copyright (c) 2022 by konakona konakona@crazyphper.com, All Rights Reserved. 
 */
package pers.learn.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import pers.learn.condition.ClassCondition;

import java.lang.annotation.*;
import org.springframework.context.annotation.Conditional;

@Conditional(ClassCondition.class)  //使用自定义的Condition
@Target({ ElementType.TYPE, ElementType.METHOD }) // 代表注解可以被加到方法和类型上
@Retention(RetentionPolicy.RUNTIME) // 代表注解生效的时机是在Runtime上
@Documented // 生成javadoc文档
public @interface ConditionOnClass {
    String[] value();
}
