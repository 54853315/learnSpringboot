/*
 * @Author: konakona konakona@crazyphper.com
 * @Date: 2022-05-16 17:16:13
 * @LastEditors: konakona konakona@crazyphper.com
 * @LastEditTime: 2022-05-25 17:00:23
 * @Description: 对应字节码是否存在判断类
 * 
 * Copyright (c) 2022 by konakona konakona@crazyphper.com, All Rights Reserved. 
 */
package pers.learn.common.condition;

import java.util.Map;

import pers.learn.common.annotation.ConditionOnClass;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class ClassCondition implements Condition {

    @Override
    /**
     * @param ConditionContext      context 上下文对象，用户获取环境，IOC容器，classLoader对象
     * @param AnnotatedTypeMetadata metadata 注解元对象。可以用于获取注解定义的属性值
     * @return boolean
     */
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Map<String, Object> map = metadata.getAnnotationAttributes(ConditionOnClass.class.getName());
        System.out.println("map" + map);
        String[] values = (String[]) map.get("value");
        boolean flag = true;
        try {
            for (String calssName : values) {
                Class.forName(calssName);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            flag = false;
        }
        return flag;
    }

}
