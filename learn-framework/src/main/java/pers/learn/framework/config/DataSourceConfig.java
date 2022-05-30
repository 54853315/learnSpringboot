/*
 * @Author: konakona konakona@crazyphper.com
 * @Date: 2022-05-06 12:23:14
 * @LastEditors: konakona konakona@crazyphper.com
 * @LastEditTime: 2022-05-25 15:34:20
 * @Description: 
 * 
 * Copyright (c) 2022 by konakona konakona@crazyphper.com, All Rights Reserved. 
 */
package pers.learn.framework.config;

import javax.sql.DataSource;

import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

@Configuration
// 表示通过aop框架暴露该代理对象,AopContext能够访问
// @EnableAspectJAutoProxy(exposeProxy = true)
// 配置使用这个数据库的mapper所在包
@MapperScan(basePackages = "pers.learn.**.mapper")
// 将pers.learn.**.mapper包链接的数据库都指向spring.datasource.db1的配置
public class DataSourceConfig {
    @Bean(name = "db1DataSource")
    @ConfigurationProperties(prefix = "spring.datasource.db1")
    public DataSource baseDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean("db1SqlSessionFactory")
    public SqlSessionFactory db1SqlSessionFactory(DataSource dataSource) throws Exception {
        /**
         * 使用 mybatis plus 配置
         */
        MybatisSqlSessionFactoryBean b1 = new MybatisSqlSessionFactoryBean();
        // System.out.println("dataSourceLyz" + dataSource.toString());
        b1.setDataSource(dataSource);
        b1.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mappers/*.xml"));
        return b1.getObject();
    }
}
