package cn.jamie.dlscorridor.core.provider;

import cn.jamie.dlscorridor.core.annotation.JMProvider;
import cn.jamie.dlscorridor.core.annotation.RpcService;
import cn.jamie.dlscorridor.core.api.RegistryCenter;
import cn.jamie.dlscorridor.core.meta.ProviderMeta;
import cn.jamie.dlscorridor.core.util.RpcMethodUtil;
import cn.jamie.dlscorridor.core.util.RpcReflectUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import lombok.Data;
import lombok.SneakyThrows;
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
public class ProviderBootstrap implements ApplicationContextAware {
    ApplicationContext applicationContext;
    private Map<String,Map<String, ProviderMeta>> skeltonMap = new HashMap<>();
    @Value("${server.port}")
    private String port;
    private String instance;
    private RegistryCenter registryCenter;

    @PostConstruct
    public void initProviders() {
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


    }
    @SneakyThrows
    public void startRegistryCenter() {
        String ip = InetAddress.getLocalHost().getHostAddress();
        instance = ip + "_" + port;
        registryCenter = applicationContext.getBean(RegistryCenter.class);
        registryCenter.start();
        skeltonMap.keySet().forEach(serverName -> registryCenter.register(serverName,instance));
    }
    @PreDestroy
    public void destroyRegistryCenter() {
        skeltonMap.keySet().forEach(serverName -> registryCenter.unregister(serverName,instance));
        registryCenter.stop();
    }
}
