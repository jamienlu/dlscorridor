package io.github.jamienlu.discorridor.core.consumer;

import io.github.jamienlu.discorridor.core.annotation.JMConsumer;
import io.github.jamienlu.discorridor.core.filter.FilterChain;
import io.github.jamienlu.discorridor.common.meta.InstanceMeta;
import io.github.jamienlu.discorridor.common.meta.ServiceMeta;
import io.github.jamienlu.discorridor.registry.api.RegistryCenter;
import io.github.jamienlu.discorridor.core.api.RpcContext;
import io.github.jamienlu.discorridor.common.util.ReflectUtil;
import com.alibaba.fastjson2.JSON;
import jakarta.annotation.PreDestroy;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 消费者获取服务代理
 */
@Data
@Slf4j
public class ConsumerBootstrap implements ApplicationContextAware {
    ApplicationContext applicationContext;
    private ServiceMeta serviceMeta;
    private RegistryCenter registryCenter;
    private FilterChain filterChain;

    private Map<String,Object> stub = new HashMap<>();
    private Set<ServiceMeta> services = new HashSet<>();
    /**
     * 解析消费者中需要服务提供者提供注入的服务实例
     */
    public void initStubs() {
        // 识别注册中心类型 这里确定是zk
        registryCenter = applicationContext.getBean(RegistryCenter.class);
        serviceMeta = applicationContext.getBean(ServiceMeta.class);
        // rpc配置参数
        RpcContext rpcContext =  applicationContext.getBean(RpcContext.class);
        // 扫描类中使用服务提供者的属性
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        // 扫描服务提供者的类 跳过其他框架的bean
        Arrays.stream(beanNames).filter(x -> !x.startsWith("java.") && !x.startsWith("org.springframework"))
            .forEach(beanName -> {
                Object bean = applicationContext.getBean(beanName);
                List<Field> fields = ReflectUtil.findAnnotationFields(bean.getClass(),JMConsumer.class);
                fields.forEach(field -> {
                    Class<?> service = field.getType();
                    Object consumer = stub.computeIfAbsent(service.getCanonicalName(),
                        key -> createConsumerFromRegistry(service, buildStubServiceMeta(field.getAnnotation(JMConsumer.class), service.getCanonicalName()), rpcContext));
                    field.setAccessible(true);
                    try {
                        field.set(bean, consumer);
                    } catch (IllegalAccessException e) {
                        log.error(e.getMessage(),e);
                    }
                });
            });
    }

    /**
     * 构建消费者使用的服务提供者信息
     *
     * @param jmConsumer 消费者注解
     * @param serviceName 消费服务的接口名
     * @return ServiceMeta
     */
    private ServiceMeta buildStubServiceMeta(JMConsumer jmConsumer, String serviceName) {
        ServiceMeta target = new ServiceMeta();
        BeanUtils.copyProperties(serviceMeta, target);
        target.setApp(jmConsumer.service());
        target.setGroup(jmConsumer.group());
        target.setName(serviceName);
        target.setVersion(jmConsumer.version());
        services.add(target);
        return target;
    }
    private Object createConsumerFromRegistry(Class<?> service, ServiceMeta serviceMeta, RpcContext rpcContext) {
        // 创建服务的动态代理需要订阅服务用于远程调用时路由
        registryCenter.subscribe(serviceMeta);
        return createConsumer(service, rpcContext, registryCenter.fectchAll(serviceMeta));
    }
    private Object createConsumer(Class<?> service, RpcContext rpcContext, List<InstanceMeta> instanceMetas) {
        return Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service},new CustomerProxy(service,rpcContext,instanceMetas));
    }

    /**
     * 反订阅
     */
    @PreDestroy
    public void destroy() {
        if (log.isDebugEnabled()) {
            log.debug("prepare destroy instances" + JSON.toJSONString(registryCenter.fectchAll(serviceMeta)));
        }
        services.forEach(serviceMeta -> {
            registryCenter.unsubscribe(serviceMeta);
        });
        if (log.isDebugEnabled()) {
            log.debug("after destroy instances" + JSON.toJSONString(registryCenter.fectchAll(serviceMeta)));
        }
    }
}
