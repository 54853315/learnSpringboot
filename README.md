# 快速上手

## 技术选型

1. 系统环境

- JAVA EE 11
- Apache Maven3
- Mysql 5.7
- Redis

2. 主框架

- Spring Boot Framework 2.6.7
- Apache Shiro 1.9
- JWT 3.19

3. 持久层

- Apache Mybatis 2.2.2
- Mybatis Plus 3.5.1
- Hibernate
- RedisTemplate

4. 日志层
- Log4j2


# 功能代码

> 演示各种特性和包的使用，集成了Mysql/Redis/Cache/Lombok/Hibernate/MybatisPlus/Log4j2/Shiro/Jwt组件与服务。

> 可结合我的Notion学习笔记一起理解这套程序的运行技巧（非必需）。

- [x] [多端用户授权登录、权限判断](/learn-admin/src/main/java/pers/learn/web/controller/AuthController.java)
    - 使用Shiro+JWT实现oauth的加密注册，授权登录和角色权限验证
    - 主功能是access token进行身份认证，ShiroConfig.java禁用了session；若需要了解Shiro Session存放到Db中实现session持久化的做法见这份[Config](/learn-framework/src/main/java/pers/learn/framework/config/ShiroConfiguration__session-version.java)
- [x] [文章管理](/learn-admin/src/main/java/pers/learn/web/controller/ArticleController.java)
    - 基于springboot+mybatis plus（CURD+乐观锁+全局元数据处理器+分页service） + hibernate（用于自动构建article数据表）实现
- [x] [留言管理](/learn-admin/src/main/java/pers/learn/web/controller/MessageController.java)
    - 基于springboot+mybatis实现
- [x] [多数据源](/learn-framework/src/main/java/pers/learn/framework/config/DataSourceConfig.java)

关于项目目录说明：
```
.
├── learn-admin      web程序，里面有控制器和VO
├── learn-common     通用设置、通用常量、通用方法、自定义注解、基操都在这里
├── learn-framework  框架设定(Shiro设定、MP设定、SpringBoot设定、异常捕获设定)
├── learn-system     业务核心

```

其他说明：

- application.properties 有mysql和postgresql 驱动切换的代码，简化了日志输出内容
- application-dev.properties和application-prod.properties 有不同环境下要启用的配置

## 打包发布

```bash

# 生产环境运行
java -jar learning-java-spring-boot-0.0.1-SNAPSHOT.jar -Dspring.profiles.active=prod

```