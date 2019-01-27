# 微服务开发 demo

> 使用前需修改`application.yml`配置文件中服务名`spring.application.name`（非常重要）。

> 按需修改数据源地址及服务端口号。

## 开发环境

> 根据实际需要增删

- jdk 1.8
- mysql 5.6
- spring-boot 2.1.2.RELEASE
- mybatis 1.3.2
- mybatis-plus 3.0.6 （mybatis增强工具，无侵入，可做通用mapper）
- swagger 2.6.1
- ...

PS：详细引用见pom依赖配置及备注说明。

## 项目结构

> 按需更改。

```
auto    -- 自动代码生成默认文件夹
│
│
eureka-client-2  -- 项目开发目录
│
├─base 继承根类 
├─config 配置类 
├─enums 枚举类
├─hystrix 熔断处理类
├─listener 消息监听（消费者）
├─provider 消息发送（生产者）
├─remote 远程调用接口
├─runner 初始化runner
├─utils 工具类
├─web 前后台交互模块
│  ├─controller 前端控制器
│  ├─mapper 数据表映射
│  ├─model 实体类
│  ├─service 服务（DAO层）
│  └─vo 查询类
│ 
├─EurekaClient2Application 项目启动类
│  
├──resources 
│  ├─lib 第三方jar依赖
│  ├─mapper 扩展mapper
│  ├─sql 项目涉及表结构及初始化数据
│  ├─static 静态资源 
│  ├─application.yml 配置文件 （用于添加个体项目配置信息）
│  └─bootstrap.yml 配置文件（加载云端（git）统一配置、注册中心配置、Stream配置） 

```

## 配置说明

> 配置文件相关项已有注释说明。

### 统一配置：Spring Cloud Config

浏览器访问<http://192.168.20.134:9999/application-dev.yml>可查看统一配置信息。

### 注册中心：Eureka Server

浏览器访问<http://192.168.20.134:8761/>可查看微服务注册信息。

### 熔断监控：Hystrix Dashboard

- 熔断监控

浏览器访问<http://[server-ip]:[port]/hystrix>，进入 Hystrix Dashboard 。
输入`http://[server-ip]:[port]/actuator/hystrix.stream`,点击`Monitor Stream`可查看自身微服务熔断情况。

- spring boot 服务监控：actuator

浏览器访问<http://[server-ip]:[port]/actuator/health>，可查看服务状态。

### 分布式追踪（Sleuth with Zipkin via HTTP）

浏览器访问<http://192.168.20.134:9411/zipkin/>可查看分布式链路监控信息

## demo 示例

### 微服务间消息传递

- feign 远程调用 -- [开源地址(包含使用说明)](https://github.com/OpenFeign/feign)
    1. 添加服务映射接口，参考`Client1Remote`及其注释。
    1. 使用形式同普通service，例`TestController`中调用方式。

- 消息队列 -- 默认模式（为方便测试，生产者、消费者写在相同微服务中） 
    1. 默认配置见`bootstrap.yml`中`stram`相关配置说明。
    1. 添加生产者,参考`IMessageProvider`接口及其实现。
    1. 添加消费者，参考`IMessageListener`。
    
- 消息队列 -- 自定义模式

### 服务降级处理（熔断/回调）

- feign 熔断/回调 参考上面远程调用即可

- hystrix 熔断/回调（目前测试出是针对接口调用发生熔断）
    1. 需熔断接口上添加`@HystrixCommand`注解
    2. 回调方法为注解中`fallbackMethod`属性值
    3. 示例见`TestController`中`getObjectStr`接口

### 分布式事务

> 尽量在设计上避免使用，如需要使用方式见申越提供文档说明。

### redis 使用

> 根据需求配置使用，配置文件参考`application.yml`。

- 使用模板
    
    参考`EurekaClient2ApplicationTests`中相关测试方法`testRedis()`、`testRedisObj()`。
    
- 自动根据方法生成缓存

### mybatis-plus 使用

- 代码自动生成（mapper、controller、entity、xml等）
    1. 修改测试文件夹中`CodeGenerator`相关配置，执行主函数
    2. 将要使用的文件复制到指定目录（PS：在不修改给出的默认配置时，xml必须放在`../resources/mapper`目录下）

- 分页示例
    1. 在xml中添加sql语句，参考`DictConstantExdMapper.xml`
    2. 在mapper中添加接口，参考`DictConstantMapper`
    3. 在service中封装mapper，参考`DictConstantService`接口及其实现（PS：建议这么使用，其实直接调用mapper也可以分页）
    4. 调用示例参考`DictConstantController`。
    
- CRUD 
    1. 普通增删改直接调用mybatis-plus相关api即可完成单表操作
    2. 查询示例参考`DictConstantService.selectDictConstant()`
    
- api

    建议直接使用service api，不要使用 mapper api，但可在service中封装mapper api。[官网API](https://mp.baomidou.com/guide/crud-interface.html)
    
## 数据库

> 脚本中预留表及数据仅做测试使用，按需删除。

## 目前架构图

![目前架构图](http://assets.processon.com/chart_image/5c49b9c0e4b0641c83e8d829.png)

## 资料

- [Spring Cloud 中文网](https://springcloud.cc/)

- [Spring Cloud 官网](https://spring.io/projects/spring-cloud)

