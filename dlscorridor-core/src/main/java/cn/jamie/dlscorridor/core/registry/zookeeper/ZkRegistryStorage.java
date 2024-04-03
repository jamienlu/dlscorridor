package cn.jamie.dlscorridor.core.registry.zookeeper;

import cn.jamie.dlscorridor.core.meta.InstanceMeta;
import cn.jamie.dlscorridor.core.meta.ServiceMeta;
import cn.jamie.dlscorridor.core.registry.RegistryStorage;
import cn.jamie.dlscorridor.core.util.VersionUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 目前设计是没有一个服务或消费实例就会持有一个各自的storage
 *
 * @author jamieLu
 * @create 2024-03-29
 */
@Slf4j
public class ZkRegistryStorage implements RegistryStorage {
    // 服务元数据 key是zk持久化节点路径
    private final Map<String,ServiceMeta> registryServiceMetas = new HashMap<>();
    // 订阅服务存在多个版本 保存服务路径到版本的集合
    private final Map<String, List<String>> serverVersions = new HashMap<>();
    // 实例元数据 key是zk实例节点路径+版本
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
    public void saveServiceInstanceMetas(ServiceMeta service, Map<String, List<InstanceMeta>> instanceMetas) {
        String serverPath = service.toPath();
        // 每个服务订阅者存的版本服务实例有序  这样可以在获取的时候拿到小于等于他最接近的实例
        serverVersions.put(serverPath, instanceMetas.keySet().stream().sorted(VersionUtil::compareVersion).collect(Collectors.toList()));
        instanceMetas.forEach((key,value) -> serverInstanceMetas.putIfAbsent(serverPath + "/" + key, instanceMetas.get(key)));
        log.info("save success serviceMeta instanceMetas path and size:" + serverPath + "##" + instanceMetas.size());
    }

    @Override
    public void removeServiceInstanceMetas(String serverPath) {
        // 带版本号包含不带版本号
        serverInstanceMetas.entrySet().removeIf(node -> node.getKey().contains(serverPath));
        log.info("remove success serviceMeta instanceMetas path" + serverPath);
    }

    @Override
    public List<InstanceMeta> searchInstanceMetas(ServiceMeta service) {
        List<String> versions = serverVersions.get(service.toPath());
        log.debug("searchInstanceMetas versions:" + versions);
        if (versions == null || versions.isEmpty()) {
            return new ArrayList<>();
        }
        int index = 0;
        for (String version : versions) {
            if (VersionUtil.compareVersion(service.getVersion(),version) >= 0) {
                break;
            }
            index++;
        }
        if (index == 0 && VersionUtil.compareVersion(service.getVersion(),versions.get(index)) > 0) {
            log.error("no existed equal or over need version");
            return new ArrayList<>();
        }
        String key =  service.toPath() + "/" + versions.get(index);
        return serverInstanceMetas.get(key);
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
