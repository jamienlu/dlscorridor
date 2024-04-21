package io.github.jamienlu.discorridor.core.provider;

import io.github.jamienlu.discorridor.core.annotation.JMProvider;
import io.github.jamienlu.discorridor.core.annotation.RpcService;
import io.github.jamienlu.discorridor.common.meta.InstanceMeta;
import io.github.jamienlu.discorridor.common.meta.ServiceMeta;
import io.github.jamienlu.discorridor.core.registry.RegistryCenter;
import io.github.jamienlu.discorridor.core.meta.ProviderMeta;
import io.github.jamienlu.discorridor.core.util.RpcMethodUtil;
import io.github.jamienlu.discorridor.common.util.ReflectUtil;
import io.github.jamienlu.discorridor.common.util.ScanPackageUtil;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
    private ProviderStorage providerStorage;

    public ProviderBootstrap(ProviderStorage providerStorage) {
        this.providerStorage = providerStorage;
    }

    public void init() {
        log.info("spring init ProviderBootstrap");
        ServiceMeta serviceMeta = applicationContext.getBean(ServiceMeta.class);
        providerStorage = applicationContext.getBean(ProviderStorage.class);
        // 加载
        loadProvider(serviceMeta);
        // 获取注册数据
        List<ServiceMeta> serviceMetas = providerStorage.findSkeltonRegs();
        InstanceMeta instanceMeta = applicationContext.getBean(InstanceMeta.class);
        // 注册中心注册
        RegistryCenter registryCenter = applicationContext.getBean(RegistryCenter.class);
        serviceMetas.forEach(meta -> registryCenter.register(meta,instanceMeta));
        log.info("spring load ProviderBootstrap end");
    }

    private void loadProvider(ServiceMeta serviceMeta) {
        List<ServiceMeta> skeltonRegs = new ArrayList<>();
        Map<String, Map<String, ProviderMeta>> skeltonInvokers = new HashMap<>();
        List<Class<?>> stubImpls = ScanPackageUtil.scanClass("", JMProvider.class);
        stubImpls.forEach(stubImpl ->{
            List<Class<?>> skeltonInterfaces = ReflectUtil.findAnnotationInterfaces(stubImpl, RpcService.class);
            skeltonInterfaces.forEach(skeltonInterface -> {
                ServiceMeta skltonMeta = ServiceMeta.builder().env(serviceMeta.getEnv()).namespace(serviceMeta.getNamespace()).app(serviceMeta.getApp())
                        .name(skeltonInterface.getCanonicalName()).group(serviceMeta.getGroup()).version(serviceMeta.getVersion()).parameters(serviceMeta.getParameters()).build();
                skeltonRegs.add(skltonMeta);
                skeltonInvokers.putIfAbsent(skeltonInterface.getCanonicalName(), new HashMap<>());
                Arrays.stream(stubImpl.getDeclaredMethods())
                        // 过滤一些不提供调用的方法
                        .filter(method -> !RpcMethodUtil.notPermissionMethod(method.getName()))
                        .forEach(method -> {
                            String methodSign = ReflectUtil.analysisMethodSign(method);
                            Object stubInstance;
                            try {
                                stubInstance = stubImpl.getConstructor().newInstance();
                                // 保存服务方法
                                skeltonInvokers.get(skeltonInterface.getCanonicalName()).put(methodSign, ProviderMeta.builder().methodSign(methodSign).method(method).serviceImpl(stubInstance).build());
                            } catch (InstantiationException | InvocationTargetException | IllegalAccessException |
                                     NoSuchMethodException e) {
                                log.error("stub not create instance export outside", e);
                            }
                      });
            });
        });
        // 避免重复加载
        providerStorage.cleanUp();
        providerStorage.storage(skeltonRegs, skeltonInvokers);
    }
    public void destroy() {
        List<ServiceMeta> serviceMetas = providerStorage.findSkeltonRegs();
        InstanceMeta instanceMeta = applicationContext.getBean(InstanceMeta.class);
        RegistryCenter registryCenter = applicationContext.getBean(RegistryCenter.class);
        serviceMetas.forEach(meta -> registryCenter.unregister(meta,instanceMeta));
        providerStorage.cleanUp();
    }
}
