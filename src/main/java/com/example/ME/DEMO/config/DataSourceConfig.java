package com.example.ME.DEMO.config;

import javax.sql.DataSource;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
// 配置使用这个数据库的mapper所在包
@MapperScan(basePackages = "com.example.ME.DEMO.mapper")
// 将com.example.ME.DEMO.message.mapper包链接的数据库都指向spring.datasource.db1的配置
public class DataSourceConfig {
    @Bean(name ="db1DataSource")
    @ConfigurationProperties(prefix = "spring.datasource.db1")
    public DataSource baseDataSource() {
        return DataSourceBuilder.create().build();
    }
}
