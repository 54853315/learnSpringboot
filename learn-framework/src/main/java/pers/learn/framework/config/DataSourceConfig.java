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

import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import pers.learn.common.compontent.MybatisPlusMetaHandler;

import javax.sql.DataSource;

@Slf4j
@Configuration
// 表示通过aop框架暴露该代理对象,AopContext能够访问
//@EnableAspectJAutoProxy(exposeProxy = true)
// 配置使用这个数据库的mapper所在包
//@MapperScan(basePackages = "pers.learn.**.mapper", sqlSessionFactoryRef = "db1SqlSessionFactory")
// 将pers.learn.**.mapper包链接的数据库都指向spring.datasource.db1的配置
public class DataSourceConfig {
    @Bean(name = "db1DataSource")
    @Primary    // 代表这是主数据源，当有多个数据源的时候必须有一个Primary
    @ConfigurationProperties(prefix = "spring.datasource.db1")
    public DataSource baseDataSource() {
        // 创建数据源
        log.info("创建主数据源");
        HikariDataSource dataSource = DataSourceBuilder.create().type(HikariDataSource.class).build();
        dataSource.setPoolName("hikariPool");
        return dataSource;
    }

    /**
     * 使用 mybatis plus 配置
     */
    @Primary
    @Bean("db1SqlSessionFactory")
//    @ConditionalOnMissingBean
    public SqlSessionFactory db1SqlSessionFactory(@Qualifier("db1DataSource") DataSource dataSource) throws Exception {
        log.info("注册主数据源的Mybatis plus MybatisSqlSessionFactoryBean");
        MybatisSqlSessionFactoryBean b1 = new MybatisSqlSessionFactoryBean();
        // 注入数据源
        b1.setDataSource(dataSource);
        Resource[] resources = new PathMatchingResourcePatternResolver().getResources("classpath*:mappers/**/*.xml");
//        System.out.println("resources:" + Arrays.toString(resources));
        b1.setMapperLocations(resources);

        //获取mybatis-plus全局配置
        GlobalConfig globalConfig = GlobalConfigUtils.defaults();

        // mybatis-plus全局配置设置元数据对象处理器
        globalConfig.setMetaObjectHandler(new MybatisPlusMetaHandler());

        // mybatisSqlSessionFactoryBean关联设置全局配置
        b1.setGlobalConfig(globalConfig);
        return b1.getObject();
    }

}
