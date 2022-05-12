package com.example.ME.DEMO.compontent;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

/**
 * 全局元数据处理器
 */
@Component
public class MyMetaHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        boolean hasSetter = metaObject.hasSetter("createTime");
        if (hasSetter) {
            System.out.println("添加插入时间");
            // 填充属性名，而非字段名
            setFieldValByName("createTime", LocalDateTime.now(), metaObject);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        Object updateTimeVal = getFieldValByName("updateTime", metaObject);
        boolean hasSetter = metaObject.hasSetter("updateTime");
        if (hasSetter && updateTimeVal == null) {
            System.out.println("添加更新时间");
            // 填充属性名，而非字段名
            setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
        }
    }

}
