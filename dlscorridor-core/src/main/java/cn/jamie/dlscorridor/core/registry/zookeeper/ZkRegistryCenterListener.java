package cn.jamie.dlscorridor.core.registry.zookeeper;

import cn.jamie.dlscorridor.core.meta.InstanceMeta;
import cn.jamie.dlscorridor.core.meta.ServiceMeta;
import cn.jamie.dlscorridor.core.registry.RegistryCenterListener;
import cn.jamie.dlscorridor.core.registry.RegistryStorage;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 注册中心监听器 监听注册中心事件
 *
 * 每个消费者注册一个监听器  对他归属的服务和实例信息进行操作
 *
 * @author jamieLu
 * @create 2024-03-21
 */
@Slf4j
public class ZkRegistryCenterListener implements RegistryCenterListener {
    private final RegistryStorage registryStorage;

    public ZkRegistryCenterListener(RegistryStorage registryStorage) {
        this.registryStorage = registryStorage;
    }

    @Override
    public void onRegistry(ServiceMeta serviceMeta) {
        String serverPath = serviceMeta.toPath();
        log.info("watch zkClient registry serviceMeta:" + serverPath);
        registryStorage.saveServiceMeta(serviceMeta);
    }

    @Override
    public void onUnRegistry(ServiceMeta serviceMeta) {
        String serverPath = serviceMeta.toPath();
        log.info("watch zkClient unregistry serviceMeta:" + serverPath);
        registryStorage.removeServiceMeta(serverPath);
    }

    @Override
    public void onSubscribe(ServiceMeta serviceMeta, List<InstanceMeta> instanceMetas) {
        log.info("watch zkClient subscribe serviceMeta path:" + serviceMeta.toPath());
        registryStorage.saveServiceInstanceMetas(serviceMeta, instanceMetas);
    }


    @Override
    public void onUnSubscribe(ServiceMeta serviceMeta) {
        String serverPath = serviceMeta.toPath();
        log.info("watch zkClient unsubscribe serviceMeta:" + serverPath);
        registryStorage.removeServiceMeta(serverPath);
    }
}
