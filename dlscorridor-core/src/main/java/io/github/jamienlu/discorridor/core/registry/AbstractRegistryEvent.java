package io.github.jamienlu.discorridor.core.registry;

import io.github.jamienlu.discorridor.core.constant.MetaConstant;
import io.github.jamienlu.discorridor.core.meta.InstanceMeta;
import io.github.jamienlu.discorridor.core.meta.ServiceMeta;
import io.github.jamienlu.discorridor.core.util.VersionUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author jamieLu
 * @create 2024-04-06
 */
@Slf4j
public abstract class AbstractRegistryEvent implements RegistryEvent {
    // 服务元数据 group + "-" + serviceName -> ServiceMeta
    private final Map<String, ServiceMeta> serviceMetas = new ConcurrentHashMap<>();
    // 实例元数据 group + "-" + serviceName -> List<InstanceMeta>
    private final Map<String, List<InstanceMeta>> serverInstanceMetas = new ConcurrentHashMap<>();
    // 实例带版本元数据 group + "-" + serviceName -> version -> List<InstanceMeta>
    private final Map<String, Map<String,List<InstanceMeta>>> serverVersionInstanceMetas = new ConcurrentHashMap<>();
    // 服务版本集 group + "-" + serviceName -> [version]
    private final Map<String, List<String>> instancesVersions = new ConcurrentHashMap<>();


    @Override
    public void saveServiceMeta(ServiceMeta service) {
        String key = RegistryUtil.serviceMetaKey(service);
        serviceMetas.putIfAbsent(key, service);
        log.info("save success service key:" + key);
    }

    @Override
    public void removeServiceMeta(ServiceMeta service) {
        String key = RegistryUtil.serviceMetaKey(service);
        serviceMetas.remove(key);
        log.info("remove success service key:" + key);
    }

    @Override
    public void saveServiceInstanceMetas(ServiceMeta service, List<InstanceMeta> instanceMetas) {
        String key = RegistryUtil.serviceMetaKey(service);
        // 订阅和节点变化都是全量数据 先清除
        if (instancesVersions.containsKey(key)) {
            instancesVersions.clear();
        }
        if (serverInstanceMetas.containsKey(key)) {
            serverInstanceMetas.clear();
        }
        if (serverVersionInstanceMetas.containsKey(key)) {
            serverVersionInstanceMetas.clear();
        }
        // 存版本 升序便于查找大于等于需要的版本数据
        List<String> versions = instanceMetas.stream()
                .map(x -> x.getParameters().getOrDefault(MetaConstant.VERSION,MetaConstant.VERSION_DEFAULT))
                .sorted(VersionUtil::compareVersion).distinct().toList();
        instancesVersions.putIfAbsent(key, new ArrayList<>());
        instancesVersions.get(key).addAll(versions);
        // 存实例
        serverInstanceMetas.putIfAbsent(key, new ArrayList<>());
        serverInstanceMetas.get(key).addAll(instanceMetas);
        // 存版本实例
        serverVersionInstanceMetas.putIfAbsent(key, new HashMap<>());
        Map<String,List<InstanceMeta>> versionInstanceMetas = instanceMetas.stream().collect(Collectors.groupingBy(ins -> ins.getParameters().getOrDefault(MetaConstant.VERSION, MetaConstant.VERSION_DEFAULT)));
        versionInstanceMetas.forEach((version, list) -> {
            serverVersionInstanceMetas.get(key).putIfAbsent(version, new ArrayList<>());
            serverVersionInstanceMetas.get(key).get(version).addAll(list);
        });
        log.info("save success serviceMeta key:" + key + " instanceMetas##size:" + instanceMetas.size());
    }

    @Override
    public void removeServiceInstanceMetas(ServiceMeta service) {
        String key = RegistryUtil.serviceMetaKey(service);
        instancesVersions.remove(key);
        serverInstanceMetas.remove(key);
        serverVersionInstanceMetas.remove(key);
        log.info("remove success serviceMeta instanceMetas key" + key);
    }

    @Override
    public List<InstanceMeta> searchInstanceMetas(ServiceMeta service) {
        String key = RegistryUtil.serviceMetaKey(service);
        // 未指定需要的版本
        if (service.getVersion().equals(MetaConstant.VERSION_DEFAULT)) {
            log.debug("search no version instanceMetas");
            return serverInstanceMetas.get(key);
        } else {
            List<String> versions = instancesVersions.get(key);
            if (versions == null || versions.isEmpty()) {
                log.error("not found registry instanceMetas");
                return serverInstanceMetas.get(key);
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
            String needVersion = versions.get(index);
            log.debug("search instanceMetas need version:" + needVersion);
            return serverVersionInstanceMetas.getOrDefault(key, new HashMap<>()).get(needVersion);
        }
    }

    @Override
    public List<ServiceMeta> findAllServiceMetas() {
        return serviceMetas.values().stream().toList();
    }

    @Override
    public void cleanUp() {
        // 消费者下线数据清空
        serviceMetas.clear();
        serverInstanceMetas.clear();
        instancesVersions.clear();
        serverVersionInstanceMetas.clear();
        log.info("registry clean up success!");
    }
}
