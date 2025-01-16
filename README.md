项目介绍 discorridor - 大灵书回廊  rpc框架实现

>git地址   [https://github.com/JamieAsura/dlscorridor](https://github.com/JamieAsura/dlscorridor)
## 项目模块说明

1. discorridor-common 元数据和公共方法和工具类等

2. discorridor-core 生产者注册消费者反射调用 包含过滤器负载均衡流控灰度等实现

3. discorridor-spring-boot server spring boot启动

4. discorridor-spring-boot-autoconfigure spring bean自动装配各模块的初始化

5. discorridor-transform 网络传输模块 http和netty实现可在装配时选择

6. discorridor-registry 注册中心 生产者实现

7. discorridor-serialization 序列化模块  类传输序列化方式 protobuf fastjson2

8. discorridor-demo-api rpc 服务接口示例

9. discorridor-demo-provider rpc服务提供者使用示例 

10. discorridor-demo-consumer rpc服务消费者使用示例 

11. discorridor-demo-mix rpc服务提供和消费者混合示例 


## discorridor-core相关功能说明

```plain
dlscorridor-core/src/main/java/cn/jamie/dlscorridor/core/annotation
  服务提供者和消费者注解和服务代理接口注解
dlscorridor-core/src/main/java/cn/jamie/dlscorridor/core/api
  RPC调用上下文
dlscorridor-core/src/main/java/cn/jamie/dlscorridor/core/cluster
  RPC集群相关 路由和负载均衡
dlscorridor-core/src/main/java/cn/jamie/dlscorridor/core/consumer
  RPC消费者相关 服务订阅和服务代理调用
dlscorridor-core/src/main/java/cn/jamie/dlscorridor/core/filter
  过滤器相关 - 限流token 和 请求缓存cache
dlscorridor-core/src/main/java/cn/jamie/dlscorridor/core/meta
  服务元数据相关  注册和订阅的服务描述
dlscorridor-core/src/main/java/cn/jamie/dlscorridor/core/provider
  RPC提供者相关  服务反射调用和RPC接口
dlscorridor-core/src/main/java/cn/jamie/dlscorridor/core/util
```


## rpc运行视图
![image](https://github.com/user-attachments/assets/86e3bdf4-f8b6-4aa1-8e9a-a8a8c99ff8d2)

## 使用示例

```plain
<dependency>
  <groupId>io.github.jamienlu</groupId>
  <artifactId>dlscorridor</artifactId>
   <version>2.0.0</version>
</dependency>


spring application.yml文件配置


服务提供者=>


discorridor:
  app: discorridor-provider
  name: provider-v1
  version: 1.0.0
  meta:
    env: dev
    namespace: public
    group: DEFAULT_GROUP
    dc: cd
    unit: A001
    tc: 50
    serialization: fastjson2
    gray: false
  netty:
    enable: true
    port: 8888
    read-idle-time: 0
    write-idle-time: 0
    close-idle-time: 10000
  http:
    alive-time: 60000
    max-idle-cons: 12
    read-out-time: 1000
    write-out-time: 1000
    con-out-time: 5000
  registry:
#    meta:
#      type: zk
#      namespace: public
#      url: 192.168.0.100:2181
#      over-time: 1000
#      retry-count: 3
    meta:
      type: nacos
      namespace: public
      url: 192.168.0.100:8848
      over-time: 1000
      retry-count: 3
      username: nacos
      password: nacos
  provider:
    enable: true


app:
  id: discorridor
apollo:
  cacheDir: /temp/conf/data                   #配置本地配置缓存目录
  cluster: default                           #指定使用哪个集群的配置
  meta: http://192.168.0.100:8080          #DEV环境配置中心地址
  autoUpdateInjectedSpringProperties: true   #是否开启 Spring 参数自动更新
  bootstrap:
    enabled: true                            #是否开启 Apollo
    namespaces: provider.yml                     #设置 Namespace
    eagerLoad:
      enabled: false                         #将 Apollo 加载提到初始化日志系统之前


logging:
  level:
    root: info
    cn.jamie: debug


 ——————————————————————————————————————————————————————————————————————————————————————————————————————
代码示例 对需要提供RPC的实现类标记注解@JMProvider需要RPC的接口标记@RpcServic


@JMProvider
public class OrderServiceImpl implements @RpcService OrderService, Serializable {
}




服务消费者 =>
server:
  port: 8801


discorridor:
  app: discorridor-customer
  name: customer-v1
  version: 1.0.0
  meta:
    env: dev
    namespace: public
    group: DEFAULT_GROUP
    dc: cd
    unit: A001
    tc: 50
    serialization: fastjson2
    gray: false
  netty:
    enable: true
    port: 8888
    read-idle-time: 0
    write-idle-time: 0
    close-idle-time: 10000
  http:
    alive-time: 60000
    max-idle-cons: 12
    read-out-time: 1000
    write-out-time: 1000
    con-out-time: 5000
#  registry:
#    meta:
#      type: zk
#      namespace: public
#      url: 192.168.0.100:2181
#      over-time: 1000
#      retry-count: 3
  registry:
    meta:
      type: nacos
      namespace: public
      url: 192.168.0.100:8848
      over-time: 1000
      retry-count: 3
  consumer:
    enable: true
    filters:
      - type: token
        token-size: 1000
        token-seconds: 100


      - type: context


      - type: cache
        cache-size: 100
        cache-seconds: 60
    gray:
      enable: true
      ratio: 50
    transform: netty


app:
  id: discorridor
apollo:
  cacheDir: /temp/conf/data                   #配置本地配置缓存目录
  cluster: default                           #指定使用哪个集群的配置
  meta: http://192.168.0.100:8080          #DEV环境配置中心地址
  autoUpdateInjectedSpringProperties: true   #是否开启 Spring 参数自动更新
  bootstrap:
    enabled: true                            #是否开启 Apollo
    namespaces: application,customer.yml                     #设置 Namespace
    eagerLoad:
      enabled: false                         #将 Apollo 加载提到初始化日志系统之前


logging:
  level:
    root: info
    cn.jamie: debug




 ——————————————————————————————————————————————————————————————————————————————————————————————————————
代码示例 对需要RPC的的接口标记@JMConsumer 
service = discorridor.env.app
version = discorridor.env.version 版本向上兼容可以订阅大于等于需要的版本


@JMConsumer(service = "discorridor-provider", version = "2.0.0")
OrderService orderService;
```


## 版本计划

v1 - simple

v2 - 模块化

v3 - 注册中心和配置中心自实现模块和调用




