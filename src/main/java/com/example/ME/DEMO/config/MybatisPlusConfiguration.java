/*
 * @Author: konakona konakona@crazyphper.com
 * @Date: 2022-05-11 11:32:26
 * @LastEditors: konakona konakona@crazyphper.com
 * @LastEditTime: 2022-05-12 16:36:25
 * @Description: 
 * 
 * Copyright (c) 2022 by konakona konakona@crazyphper.com, All Rights Reserved. 
 */
package com.example.ME.DEMO.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisPlusConfiguration {
    /**
     * 乐观锁插件配置
     * 
     * @return MybatisPlusInterceptor
     */
    @Bean
    public MybatisPlusInterceptor innerInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 添加乐观锁
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        // 添加Sql的分页拦截器
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return interceptor;
    }
}