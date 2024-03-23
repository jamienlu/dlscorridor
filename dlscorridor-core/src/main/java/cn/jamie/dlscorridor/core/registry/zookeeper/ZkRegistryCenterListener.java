package cn.jamie.dlscorridor.core.registry.zookeeper;

import cn.jamie.dlscorridor.core.meta.InstanceMeta;
import cn.jamie.dlscorridor.core.meta.ServiceMeta;
import cn.jamie.dlscorridor.core.registry.RegistryCenterListener;
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
    private final Map<String,ServiceMeta> registryServiceMetas = new HashMap<>();
    private final Map<String, List<InstanceMeta>> serverInstanceMetas = new HashMap<>();

    @Override
    public void onRegistry(ServiceMeta serviceMeta) {
        String serverPath = serviceMeta.toPath();
        log.info("watch zkClient registry serviceMeta:" + serverPath);
        registryServiceMetas.putIfAbsent(serverPath, serviceMeta);
    }

    @Override
    public void onUnRegistry(ServiceMeta serviceMeta) {
        String serverPath = serviceMeta.toPath();
        log.info("watch zkClient unregistry serviceMeta:" + serverPath);
        registryServiceMetas.remove(serverPath);
    }

    @Override
    public void onSubscribe(ServiceMeta serviceMeta, List<InstanceMeta> instanceMetas) {
        String serverPath = serviceMeta.toPath();
        log.info("watch zkClient subscribe serviceMeta:" + serverPath);
        serverInstanceMetas.put(serverPath, instanceMetas);
    }


    @Override
    public void onUnSubscribe(ServiceMeta serviceMeta) {
        String serverPath = serviceMeta.toPath();
        log.info("watch zkClient unsubscribe serviceMeta:" + serverPath);
        serverInstanceMetas.remove(serverPath);
    }

    /**
     * 可以从自己监听的服务获取实例数据
     *
     * @param serviceMeta 服务元数据
     * @return List<InstanceMeta>
     */
    public List<InstanceMeta> fetchInstanceMetas(ServiceMeta serviceMeta) {
        return serverInstanceMetas.get(serviceMeta.toPath());
    }

    public List<ServiceMeta> fetchAllServicetas() {
        return registryServiceMetas.values().stream().toList();
    }
}
