package cn.jamie.dlscorridor.core.registry.zookeeper;

import cn.jamie.dlscorridor.core.meta.InstanceMeta;
import cn.jamie.dlscorridor.core.meta.ServiceMeta;
import cn.jamie.dlscorridor.core.registry.RegistryCenter;
import cn.jamie.dlscorridor.core.registry.RegistryCenterListener;

import java.util.List;

/**
 * @author jamieLu
 * @create 2024-03-23
 */
public class ZkRegistryCenterAdapter implements RegistryCenter {
    private final ZkRegistryCenter registryCenter;

    public ZkRegistryCenterAdapter(RegistryCenter registryCenter) {
        assert registryCenter instanceof ZkRegistryCenter;
        this.registryCenter = (ZkRegistryCenter) registryCenter;
    }

    @Override
    public void start() {
        registryCenter.start();
    }

    @Override
    public void stop() {
        registryCenter.stop();
    }

    @Override
    public void register(ServiceMeta service, InstanceMeta instance) {
        registryCenter.register(service, instance);
    }

    @Override
    public void unregister(ServiceMeta service, InstanceMeta instance) {
        registryCenter.unregister(service, instance);
    }

    @Override
    public List<InstanceMeta> fectchAll(ServiceMeta service) {
        return registryCenter.fectchAll(service);
    }

    @Override
    public void subscribe(ServiceMeta service) {
        registryCenter.subscribe(service);
    }

    @Override
    public void unsubscribe(ServiceMeta service) {
        registryCenter.unsubscribe(service);
    }

    public void addListener(RegistryCenterListener listener) {
        registryCenter.addListener(listener);
    }
}
