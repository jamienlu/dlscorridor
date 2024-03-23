package cn.jamie.dlscorridor.core.provider;

import cn.jamie.dlscorridor.core.annotation.JMProvider;
import cn.jamie.dlscorridor.core.annotation.RpcService;
import cn.jamie.dlscorridor.core.meta.InstanceMeta;
import cn.jamie.dlscorridor.core.meta.ServiceMeta;
import cn.jamie.dlscorridor.core.registry.RegistryCenter;
import cn.jamie.dlscorridor.core.meta.ProviderMeta;
import cn.jamie.dlscorridor.core.registry.zookeeper.ZkRegistryCenterAdapter;
import cn.jamie.dlscorridor.core.registry.zookeeper.ZkRegistryCenterListener;
import cn.jamie.dlscorridor.core.util.RpcMethodUtil;
import cn.jamie.dlscorridor.core.util.RpcReflectUtil;
import com.alibaba.fastjson2.JSON;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
/**
 * 服务提供者封装类接口和其实现类对象
 *
 * @author jamieLu
 * @create 2024-03-12
 */
@Data
@Slf4j
public class ProviderBootstrap implements ApplicationContextAware {
    ApplicationContext applicationContext;
    private Map<String,Map<String, ProviderMeta>> skeltonMap = new HashMap<>();
    @Value("${server.port}")
    private Integer port;
    private ServiceMeta serviceMeta;
    private InstanceMeta instanceMeta;
    private ZkRegistryCenterAdapter zkRegistryCenterAdapter;
    private ZkRegistryCenterListener zkRegistryCenterListener;

    @SneakyThrows
    @PostConstruct
    public void initProviders() {
        serviceMeta = applicationContext.getBean(ServiceMeta.class);
        // 查找服务提供类
        Map<String,Object> providerBeanMap = applicationContext.getBeansWithAnnotation(JMProvider.class);
        // 注入映射的接口和服务提供方法
        providerBeanMap.values()
            // 获取提供服务的接口
            .forEach(providerBean -> RpcReflectUtil.findAnnotationInterfaces(providerBean.getClass(), RpcService.class)
            // 映射实现类和服务方法
            .forEach(intefaceClass -> {
                skeltonMap.putIfAbsent(intefaceClass.getCanonicalName(), new HashMap<>());
                Map<String, ProviderMeta> skeltonBeanMap = skeltonMap.get(intefaceClass.getCanonicalName());
            Arrays.stream(providerBean.getClass().getDeclaredMethods())
                    .filter(method -> !RpcMethodUtil.notPermissionMethod(method.getName()))
                    .forEach(method -> {
                        String methodSign = RpcReflectUtil.analysisMethodSign(method);
                        skeltonBeanMap.put(methodSign, ProviderMeta.builder().methodSign(methodSign).method(method).serviceImpl(providerBean).build());
                    });
            }));
        String ip = InetAddress.getLocalHost().getHostAddress();
        instanceMeta = InstanceMeta.builder().host(ip).port(port).build();
        RegistryCenter registryCenter = applicationContext.getBean(RegistryCenter.class);
        zkRegistryCenterListener = applicationContext.getBean(ZkRegistryCenterListener.class);
        zkRegistryCenterAdapter = new ZkRegistryCenterAdapter(registryCenter);
        zkRegistryCenterAdapter.addListener(zkRegistryCenterListener);
    }
    @SneakyThrows
    public void registryRegistryCenter() {
        skeltonMap.keySet().forEach(serverName -> {
            ServiceMeta target = new ServiceMeta();
            BeanUtils.copyProperties(serviceMeta, target);
            target.setName(serverName);
            zkRegistryCenterAdapter.register(target, instanceMeta);
        });
        log.info("all registry service:" + JSON.toJSONString(zkRegistryCenterListener.fetchAllServicetas()));
    }
    @PreDestroy
    public void destroyRegistryCenter() {
        skeltonMap.keySet().forEach(serverName -> {
            ServiceMeta target = new ServiceMeta();
            BeanUtils.copyProperties(serviceMeta, target);
            target.setName(serverName);
            zkRegistryCenterAdapter.unregister(target,instanceMeta);
        });
        zkRegistryCenterAdapter.stop();
        log.info("all registry service:" + JSON.toJSONString(zkRegistryCenterListener.fetchAllServicetas()));
    }
}
