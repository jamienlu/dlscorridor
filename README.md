# discorridor

## 项目模块说明

1. discorridor-core RPC框架核心源码
2. discorridor-spring-boot 框架spring boot包整合一键启动
3. discorridor-spring-boot-autoconfigure 框架启动需要使用的bean自动装配 <- core
4. discorridor-demo-api rpc 接口示例
5. discorridor-demo-provider rpc服务提供者使用示例 <- autoconfigure 
6. discorridor-demo-consumer rpc服务消费者使用示例 <- autoconfigure 
7. discorridor-demo-mix rpc服务提供和消费者混合示例 <- autoconfigure 
## discorridor-core相关功能说明

```plain
dlscorridor-core/src/main/java/cn/jamie/dlscorridor/core/annotation
  服务提供者和消费者注解
dlscorridor-core/src/main/java/cn/jamie/dlscorridor/core/api
  RPC调用参数
dlscorridor-core/src/main/java/cn/jamie/dlscorridor/core/cluster
  RPC集群相关 路由和负载均衡
dlscorridor-core/src/main/java/cn/jamie/dlscorridor/core/consumer
  RPC消费者相关 服务订阅和服务代理调用
dlscorridor-core/src/main/java/cn/jamie/dlscorridor/core/exception
  自定义RPC异常
dlscorridor-core/src/main/java/cn/jamie/dlscorridor/core/filter
  过滤器相关 - 限流token 和 请求缓存cache
dlscorridor-core/src/main/java/cn/jamie/dlscorridor/core/meta
  服务元数据相关  注册和订阅的服务描述
dlscorridor-core/src/main/java/cn/jamie/dlscorridor/core/provider
  RPC提供者相关  服务反射调用和RPC接口
dlscorridor-core/src/main/java/cn/jamie/dlscorridor/core/registry
  注册中心 (zookeeper) 服务提供者注册反注册  消费者订阅反订阅
dlscorridor-core/src/main/java/cn/jamie/dlscorridor/core/serialization
  序列化手段 (未实现 强制使用fastjson2)
dlscorridor-core/src/main/java/cn/jamie/dlscorridor/core/transform
  RPC传输（http）
dlscorridor-core/src/main/java/cn/jamie/dlscorridor/core/util
```

## discorridor-core运行视图


![RPC流程图](https://github.com/JamieAsura/dlscorridor/assets/37697410/3863e1b1-50fd-46a9-b09c-863c87a0661a)


## 使用示例

## 
```plain
<dependency>
    <groupId>cn.jamie</groupId>
    <artifactId>dlscorridor-core</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>

<dependency>
    <groupId>cn.jamie</groupId>
    <artifactId>discorridor-spring-boot-autoconfigure</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### 服务提供者：

```plain
spring yml文件配置

discorridor:
  env:
    app: discorridor-provider
    namespace: public
    env: dev
    name: provider-v1
    version: 1.0.0
  registry:
    env:
      namespace: public
      url: 192.168.0.100:2181
      over-time: 1000
      retry-count: 3
  provider:
    enable: true
    gray: false
    
 ——————————————————————————————————————————————————————————————————————————————————————————————————————
代码示例 对需要提供RPC的实现类标记注解@JMProvider需要RPC的接口标记@RpcServic

@JMProvider
public class OrderServiceImpl implements @RpcService OrderService, Serializable {
}
```

### 服务消费者：

```plain
spring yml文件配置

discorridor:
  env:
    app: discorridor-customer
    namespace: public
    env: dev
    name: customer-v1
    version: 1.0.0
  registry:
    env:
      namespace: public
      url: 192.168.0.100:2181
      over-time: 1000
      retry-count: 3
  consumer:
    enable: true
    filters:
      - type: token
        token-size: 1000
        token-seconds: 100
      - type: cache
        cache-size: 100
        cache-seconds: 60
    gray:
      enable: true
      ratio: 50
    
 ——————————————————————————————————————————————————————————————————————————————————————————————————————
代码示例 对需要RPC的的接口标记@JMConsumer 
service = discorridor.env.app
version = discorridor.env.version 版本向上兼容可以订阅大于等于需要的版本

@JMConsumer(service = "discorridor-provider", version = "1.0.1")
OrderService orderService;
```

## 版本计划

v1 - fix

v2 -注册中心模块独立 - 实现增加nacos

v3 -RPC传输模块独立  - netty实现

v4 -序列化模块独立 - 实现增加 jdk + protostuff
