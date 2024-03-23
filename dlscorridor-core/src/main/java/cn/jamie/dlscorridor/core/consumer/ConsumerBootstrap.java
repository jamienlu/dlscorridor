package cn.jamie.dlscorridor.core.consumer;

import cn.jamie.dlscorridor.core.annotation.JMConsumer;
import cn.jamie.dlscorridor.core.api.LoadBalancer;
import cn.jamie.dlscorridor.core.meta.InstanceMeta;
import cn.jamie.dlscorridor.core.meta.ServiceMeta;
import cn.jamie.dlscorridor.core.registry.RegistryCenter;
import cn.jamie.dlscorridor.core.api.Router;
import cn.jamie.dlscorridor.core.api.RpcContext;
import cn.jamie.dlscorridor.core.registry.zookeeper.ZkRegistryCenterAdapter;
import cn.jamie.dlscorridor.core.registry.zookeeper.ZkRegistryCenterListener;
import cn.jamie.dlscorridor.core.util.RpcReflectUtil;
import com.alibaba.fastjson2.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消费者获取服务代理
 */
@Data
@Slf4j
public class ConsumerBootstrap implements ApplicationContextAware, EnvironmentAware {
    ApplicationContext applicationContext;
    Environment environment;
    private Map<String,Object> stub = new HashMap<>();
    private ZkRegistryCenterAdapter zkRegistryCenterAdapter;
    private ZkRegistryCenterListener zkRegistryCenterListener;
    private ServiceMeta serviceMeta;
    /**
     * 解析消费者中需要服务提供者提供注入的服务实例
     */
    public void loadConsumerProxy() {
        Router router = applicationContext.getBean(Router.class);
        LoadBalancer loadBalancer = applicationContext.getBean(LoadBalancer.class);
        RpcContext rpcContext = RpcContext.builder().router(router).loadBalancer(loadBalancer).build();
        RegistryCenter registryCenter = applicationContext.getBean(RegistryCenter.class);
        serviceMeta = applicationContext.getBean(ServiceMeta.class);
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        // 识别注册中心类型 这里确定是zk
        zkRegistryCenterListener = applicationContext.getBean(ZkRegistryCenterListener.class);
        zkRegistryCenterAdapter = new ZkRegistryCenterAdapter(registryCenter);
        zkRegistryCenterAdapter.addListener(zkRegistryCenterListener);
        // 扫描服务提供者的类 跳过其他框架的bean
        Arrays.stream(beanNames).filter(x -> !x.startsWith("java.") && !x.startsWith("org.springframework"))
            .forEach( beanName ->  {
                Object bean = applicationContext.getBean(beanName);
                List<Field> fields = RpcReflectUtil.findAnnotationFields(bean.getClass(),JMConsumer.class);
                fields.forEach(f -> {
                    Class<?> service = f.getType();
                    Object consumer = stub.computeIfAbsent(service.getCanonicalName(),key -> createConsumerFromRegistry(service, rpcContext));
                    f.setAccessible(true);
                    try {
                        f.set(bean, consumer);
                    } catch (IllegalAccessException e) {
                        log.error(e.getMessage(),e);
                    }
                });
            });

    }
    private Object createConsumerFromRegistry(Class<?> service, RpcContext rpcContext) {
        // 存储的instance是ip_port形式
        ServiceMeta target = new ServiceMeta();
        BeanUtils.copyProperties(serviceMeta, target);
        target.setName(service.getName());
        zkRegistryCenterAdapter.subscribe(target);
        List<InstanceMeta> instanceMetas = zkRegistryCenterListener.fetchInstanceMetas(target);
        log.info("current service instance" + JSON.toJSONString(instanceMetas));
        return createConsumer(service, rpcContext, instanceMetas);
    }
    private Object createConsumer(Class<?> service, RpcContext rpcContext, List<InstanceMeta> instanceMetas) {
        return Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service},new JMInvocationHandler(service,rpcContext,instanceMetas));
    }
}
