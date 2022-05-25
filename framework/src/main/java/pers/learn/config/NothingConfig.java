/*
 * @Author: konakona konakona@crazyphper.com
 * @Date: 2022-05-16 17:14:33
 * @LastEditors: konakona konakona@crazyphper.com
 * @LastEditTime: 2022-05-16 21:22:42
 * 
 * Copyright (c) 2022 by konakona konakona@crazyphper.com, All Rights Reserved. 
 */
package pers.learn.framework.config;

import pers.learn.annotation.ConditionOnClass;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NothingConfig {
    @Bean
    // @ConditionOnClass({ "org.springframework.data.redis.core.RedisTemplate", "com.alibaba.fastjson.JSON" })
    @ConditionOnClass("org.springframework.data.redis.core.RedisTemplate")
    public String x() {
        return "存在RedisTemplate，可以继续";
    }

    @ConditionalOnProperty(name = "define_p", havingValue = "yes")
    public String xx() {
        return "存在define_p配置，值正确";
    }
}
