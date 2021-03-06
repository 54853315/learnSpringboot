package pers.learn.common.compontent;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

/**
 * 全局元数据处理器
 */
@Component
public class MybatisPlusMetaHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        System.out.println("添加插入时间");
        // 填充属性名，而非字段名
        setFieldValByName("createTime", LocalDateTime.now(), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        System.out.println("添加更新时间");
        // 填充属性名，而非字段名
        setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
    }

}
