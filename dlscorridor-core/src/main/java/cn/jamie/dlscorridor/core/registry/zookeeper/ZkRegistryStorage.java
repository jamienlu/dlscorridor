package cn.jamie.dlscorridor.core.registry.zookeeper;

import cn.jamie.dlscorridor.core.meta.InstanceMeta;
import cn.jamie.dlscorridor.core.meta.ServiceMeta;
import cn.jamie.dlscorridor.core.registry.RegistryStorage;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jamieLu
 * @create 2024-03-29
 */
@Slf4j
public class ZkRegistryStorage implements RegistryStorage {
    private final Map<String,ServiceMeta> registryServiceMetas = new HashMap<>();
    private final Map<String, List<InstanceMeta>> serverInstanceMetas = new HashMap<>();

    @Override
    public void saveServiceMeta(ServiceMeta service) {
        String serverPath = service.toPath();
        registryServiceMetas.putIfAbsent(serverPath, service);
        log.info("save success serviceMeta path:" + serverPath);
    }

    @Override
    public void removeServiceMeta(String serverPath) {
        registryServiceMetas.remove(serverPath);
        log.info("remove success serviceMeta path:" + serverPath);
    }

    @Override
    public void saveServiceInstanceMetas(ServiceMeta service, List<InstanceMeta> instanceMetas) {
        String serverPath = service.toPath();
        serverInstanceMetas.put(serverPath, instanceMetas);
        log.info("save success serviceMeta instanceMetas path and size:" + serverPath + "##" + instanceMetas.size());
    }

    @Override
    public void removeServiceInstanceMetas(String serverPath) {
        serverInstanceMetas.remove(serverPath);
        log.info("remove success serviceMeta instanceMetas path" + serverPath);
    }

    @Override
    public List<InstanceMeta> searchInstanceMetas(String serverPath) {
        return serverInstanceMetas.get(serverPath);
    }

    @Override
    public List<ServiceMeta> findAllServiceMetas() {
        return registryServiceMetas.values().stream().toList();
    }

    @Override
    public void cleanUp() {
        registryServiceMetas.clear();
        serverInstanceMetas.clear();
        log.info("zkregistry storage clean up success!");
    }
}
