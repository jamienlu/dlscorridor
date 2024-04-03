package cn.jamie.dlscorridor.core.registry.zookeeper;

import cn.jamie.dlscorridor.core.exception.RpcException;
import cn.jamie.dlscorridor.core.meta.InstanceMeta;
import cn.jamie.dlscorridor.core.meta.ServiceMeta;
import cn.jamie.dlscorridor.core.registry.RegistryCenter;
import cn.jamie.dlscorridor.core.registry.RegistryCenterListener;
import cn.jamie.dlscorridor.core.registry.RegistryStorage;
import cn.jamie.dlscorridor.core.util.VersionUtil;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.zookeeper.CreateMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author jamieLu
 * @create 2024-03-17
 */
@Slf4j
public class ZkRegistryCenter implements RegistryCenter {
    private CuratorFramework client;
    private final ZkEnvData zkEnvData;
    private final RegistryStorage registryStorage;
    private final List<RegistryCenterListener> listeners = new ArrayList<>();
    private final Map<String, CuratorCache> serverCuratorCaches = new HashMap<>();
    public void addListener(RegistryCenterListener listener) {
        listeners.add(listener);
    }
    private void listenerEvent(Consumer<RegistryCenterListener> consumer) {
        if (CollectionUtils.isNotEmpty(listeners)) {
            for (RegistryCenterListener listener : listeners) {
                consumer.accept(listener);
            }
        }
    }

    public ZkRegistryCenter(ZkEnvData zkEnvData,RegistryStorage registryStorage) {
        this.zkEnvData = zkEnvData;
        this.registryStorage = registryStorage;
        // 初始化默认创建一个监听器用来传递注册和订阅的数据
        listeners.add(new ZkRegistryCenterListener(registryStorage));
    }

    @Override
    public void start() {
        log.info("prepare start zk registryCenter");
        // 初始化建立连接
        client = CuratorFrameworkUtil.buildCuratorFramework(zkEnvData);
        client.start();
        log.info("zk registryCenter started");
    }

    @Override
    public void stop() {
        log.info("prepare destroy zk registryCenter");
        // 关闭连接
        client.close();
        // 清理注册和订阅内容(反注册和反订阅已清理)
        registryStorage.cleanUp();
        log.info("zk registryCenter stopted");
    }

    @Override
    public void register(ServiceMeta service, InstanceMeta instance) {
        String serverPath = "/" + service.toPath();
        try {
            // 创建服务的持久化节点
            if(client.checkExists().forPath(serverPath) == null) {
                client.create().withMode(CreateMode.PERSISTENT).forPath(serverPath,"service".getBytes());
                log.info("create zk registry server path:" + serverPath);
            }
            String versionPath = serverPath + "/" + service.getVersion();
            if(client.checkExists().forPath(versionPath) == null) {
                client.create().withMode(CreateMode.PERSISTENT).forPath(versionPath,"version".getBytes());
                log.info("create zk registry server path:" + versionPath);
            }
            // 创建实例的临时节点
            String instancePath = versionPath + "/" + instance.toPath();
            client.create().withMode(CreateMode.EPHEMERAL).forPath(instancePath,"provider".getBytes());
            log.info("create zk registry instance path:" + instancePath);
        } catch (Exception e) {
            throw new RpcException(e.getCause(),e.getMessage());
        } finally {
            listenerEvent(event -> event.onRegistry(service));

        }
    }

    @Override
    public void unregister(ServiceMeta service, InstanceMeta instance) {
        String serverPath = "/" + service.toPath();
        try {
            // 删除服务的持久化节点
            if(client.checkExists().forPath(serverPath) == null) {
                return;
            }
            String versionPath = serverPath + "/" + service.getVersion();
            if(client.checkExists().forPath(versionPath) == null) {
                return;
            }
            // 删除实例的临时节点
            String instancePath = versionPath + "/" + instance.toPath();
            log.info("remove zk registry instance path:" + instancePath);
            client.delete().quietly().forPath(instancePath);
        } catch (Exception e) {
            throw new RpcException(e.getCause(),e.getMessage());
        } finally {
            listenerEvent(event -> event.onUnRegistry(service));
        }
    }


    private Map<String,List<InstanceMeta>> fectchZkInstanceMetas(ServiceMeta service) {
        Map<String,List<InstanceMeta>> result = new HashMap<>();
        String serverPath = "/" + service.toPath();
        try {
            List<String> versions = client.getChildren().forPath(serverPath).stream().toList();
            for (String version : versions) {
                // 你需要的版本应该<=服务列表里的版本
                if (VersionUtil.compareVersion(service.getVersion(), version) <= 0) {
                    List<InstanceMeta> nodes = client.getChildren().forPath(serverPath + "/" + version).stream()
                        .map(InstanceMeta::pathToInstance).collect(Collectors.toList());
                    log.debug("real fetch path:" + service.toPath() + "##version:" + version + "##size:" + nodes.size());
                    result.put(version, nodes);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            throw new RpcException(e.getCause(),e.getMessage());
        }
        return result;
    }
    @Override
    public List<InstanceMeta> fectchAll(ServiceMeta service) {
        return registryStorage.searchInstanceMetas(service);
    }

    @Override
    public void subscribe(ServiceMeta service) {
        log.debug("prepare subscribe stub:" + JSON.toJSONString(service));
        // 初始拉一次订阅数据数据
        listenerEvent(event -> event.onSubscribe(service, fectchZkInstanceMetas(service)));

        String serverPath = "/" + service.toPath();
        // 订阅zk节点
        CuratorCache cache = CuratorCache.build(client, serverPath);
        CuratorCacheListener cacheListener = CuratorCacheListener.builder()
                .forAll((type,oldNode,newNode) -> {
                    if (oldNode != null) {
                        log.info("change node old node:" + oldNode.getPath());
                    }
                    if (newNode != null) {
                        log.info("change node new node:" + newNode.getPath());
                    }
                    // 订阅后需要把数据更新传递给监听器处理
                    Map<String,List<InstanceMeta>> instanceMetas = fectchZkInstanceMetas(service);
                    listenerEvent(event -> event.onSubscribe(service, instanceMetas));
                } )
                .build();
        cache.listenable().addListener(cacheListener);
        cache.start();
        // 去订阅需要关闭订阅流
        serverCuratorCaches.put(service.toPath(), cache);
    }

    @Override
    public void unsubscribe(ServiceMeta service) {
        log.debug("prepare unsubscribe stub:" + JSON.toJSONString(service));
        try {
            serverCuratorCaches.get(service.toPath()).close();
            serverCuratorCaches.remove(service.toPath());
        } finally {
            // 监听器去取消订阅
            listenerEvent(event -> event.onUnSubscribe(service));
        }
    }


}
