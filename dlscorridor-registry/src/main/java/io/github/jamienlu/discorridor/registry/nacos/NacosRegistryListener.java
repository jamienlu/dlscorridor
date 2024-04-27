package io.github.jamienlu.discorridor.registry.nacos;

import io.github.jamienlu.discorridor.common.meta.InstanceMeta;
import io.github.jamienlu.discorridor.common.meta.ServiceMeta;
import io.github.jamienlu.discorridor.registry.api.RegistryCenterListener;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author jamieLu
 * @create 2024-04-06
 */
@Slf4j
public class NacosRegistryListener implements RegistryCenterListener {
    private final NacosRegistryEvent registryEvent;

    public NacosRegistryListener(NacosRegistryEvent registryEvent) {
        this.registryEvent = registryEvent;
    }

    @Override
    public void onRegistry(ServiceMeta serviceMeta) {
        registryEvent.saveServiceMeta(serviceMeta);
    }

    @Override
    public void onUnRegistry(ServiceMeta serviceMeta) {
        registryEvent.removeServiceMeta(serviceMeta);
    }

    @Override
    public void onSubscribe(ServiceMeta serviceMeta, List<InstanceMeta> instanceMetas) {
        registryEvent.saveServiceInstanceMetas(serviceMeta, instanceMetas);
    }


    @Override
    public void onUnSubscribe(ServiceMeta serviceMeta) {
        registryEvent.removeServiceInstanceMetas(serviceMeta);
    }
}
