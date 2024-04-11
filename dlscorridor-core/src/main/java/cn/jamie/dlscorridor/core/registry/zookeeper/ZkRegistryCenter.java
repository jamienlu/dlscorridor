package cn.jamie.dlscorridor.core.registry.zookeeper;

import cn.jamie.dlscorridor.core.constant.MetaConstant;
import cn.jamie.dlscorridor.core.exception.RpcException;
import cn.jamie.dlscorridor.core.meta.InstanceMeta;
import cn.jamie.dlscorridor.core.meta.ServiceMeta;
import cn.jamie.dlscorridor.core.registry.RegistryCenter;
import cn.jamie.dlscorridor.core.registry.RegistryCenterListener;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 *
 * @author jamieLu
 * @create 2024-03-17
 */
@Slf4j
public class ZkRegistryCenter implements RegistryCenter {
    private CuratorFramework client;
    private final ZkEnvData zkEnvData;
    private final ZkRegistryEvent registryEvent;
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

    public ZkRegistryCenter(ZkEnvData zkEnvData) {
        this.zkEnvData = zkEnvData;
        registryEvent = new ZkRegistryEvent();
        // 初始化默认创建一个监听器用来传递注册和订阅的数据
        listeners.add(new ZkRegistryCenterListener(registryEvent));
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
        registryEvent.cleanUp();
        log.info("zk registryCenter stopted");
    }

    /**
     * zk 注册节点路径树
     * service:
     * app
     *  namspace
     *    env
     *      group
     *        name
     * instance:
     *            ip:port
     * @param service 服务
     * @param instance 实例
     */
    @Override
    public void register(ServiceMeta service, InstanceMeta instance) {
        String appNode = "/" + service.getApp();
        try {
            // 创建服务的持久化节点
            if (client.checkExists().forPath(appNode) == null) {
                client.create().withMode(CreateMode.PERSISTENT).forPath(appNode,"appNode".getBytes());
            }
            String namspaceNode = appNode + "/" + service.getNamespace();
            if (client.checkExists().forPath(namspaceNode) == null) {
                client.create().withMode(CreateMode.PERSISTENT).forPath(namspaceNode,"namspaceNode".getBytes());
            }
            String envNode = namspaceNode + "/" + service.getEnv();
            if (client.checkExists().forPath(envNode) == null) {
                client.create().withMode(CreateMode.PERSISTENT).forPath(envNode,"envNode".getBytes());
            }
            String groupNode = envNode + "/" + service.getGroup();
            if (client.checkExists().forPath(groupNode) == null) {
                client.create().withMode(CreateMode.PERSISTENT).forPath(groupNode,"groupNode".getBytes());
            }
            String serviceNode = groupNode + "/" + service.getName();
            if (client.checkExists().forPath(serviceNode) == null) {
                client.create().withMode(CreateMode.PERSISTENT).forPath(serviceNode, instance.toMetas());
            }
            // 根据服务信息修改实例信息
            instance.addMeta(MetaConstant.VERSION,service.getVersion());
            String instancePath = serviceNode + "/" + instance.toPath();
            client.create().withMode(CreateMode.EPHEMERAL).forPath(instancePath, instance.toMetas());
            log.info("create zk registry instance path:" + instancePath);
        } catch (Exception e) {
            throw new RpcException(e.getCause(),e.getMessage());
        } finally {
            listenerEvent(event -> event.onRegistry(service));

        }
    }

    @Override
    public void unregister(ServiceMeta service, InstanceMeta instance) {
        String appNode = "/" + service.getApp();
        try {
            if (client.checkExists().forPath(appNode) == null) {
                return;
            }
            String namspaceNode = appNode + "/" + service.getNamespace();
            if (client.checkExists().forPath(namspaceNode) == null) {
                return;
            }
            String envNode = namspaceNode + "/" + service.getEnv();
            if (client.checkExists().forPath(envNode) == null) {
                return;
            }
            String groupNode = envNode + "/" + service.getGroup();
            if (client.checkExists().forPath(groupNode) == null) {
                return;
            }
            String serviceNode = groupNode + "/" + service.getName();
            if (client.checkExists().forPath(serviceNode) == null) {
                return;
            }
            String versionNode = serviceNode + "/" + service.getVersion();
            if (client.checkExists().forPath(serviceNode) == null) {
                return;
            }
            // 删除实例的临时节点
            String instancePath = versionNode + "/" + instance.toPath();
            log.info("remove zk registry instance path:" + instancePath);
            client.delete().quietly().forPath(instancePath);
        } catch (Exception e) {
            throw new RpcException(e.getCause(),e.getMessage());
        } finally {
            listenerEvent(event -> event.onUnRegistry(service));
        }
    }


    private List<InstanceMeta> fectchZkInstanceMetas(ServiceMeta service) {
        List<InstanceMeta> result = new ArrayList<>();
        String serverNode = "/" + service.getApp() + "/" + service.getNamespace() + "/" + service.getEnv()
            + "/" + service.getGroup() + "/" + service.getName();
        try {
            if (client.checkExists().forPath(serverNode) == null) {
                return result;
            }
            result = client.getChildren().forPath(serverNode).stream().map(path -> {
                InstanceMeta instanceMeta = InstanceMeta.pathToInstance(path);
                String nodePath = serverNode + "/" + path;
                byte[] bytes;
                try {
                    bytes = client.getData().forPath(nodePath);
                } catch (Exception e) {
                    log.error("get zk node data error", e);
                    throw new RpcException(e.getCause(), e.getMessage());
                }
                Map<String,Object> params = JSON.parseObject(new String(bytes));
                params.forEach((k,v) -> instanceMeta.getParameters().put(k,v == null ? null : v.toString()));
                return instanceMeta;
            }).collect(Collectors.toList());
            log.debug("fetch zk serverNode path:" + serverNode + "##size:" + result.size());
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            throw new RpcException(e.getCause(),e.getMessage());
        }
        if (null != service.getVersion() && !service.getVersion().isEmpty()) {
            result = result.stream().filter(x -> {
                String curVersion = x.getParameters().get(MetaConstant.VERSION);
                return VersionUtil.compareVersion(curVersion, service.getVersion()) >= 0;
            }).collect(Collectors.toList());
        }
        log.debug("real zk serverNode path:" + serverNode + "##size:" + result.size());
        return result;
    }
    @Override
    public List<InstanceMeta> fectchAll(ServiceMeta service) {
        return registryEvent.searchInstanceMetas(service);
    }

    @Override
    public void subscribe(ServiceMeta service) {
        log.debug("prepare subscribe stub:" + JSON.toJSONString(service));
        // 初始拉一次订阅数据数据
        String serverNode = "/" + service.getApp() + "/" + service.getNamespace() + "/" + service.getEnv()
                + "/" + service.getGroup() + "/" + service.getName();
        // 订阅zk节点
        CuratorCache cache = CuratorCache.build(client, serverNode);
        CuratorCacheListener cacheListener = CuratorCacheListener.builder()
                .forAll((type,oldNode,newNode) -> {
                    if (oldNode != null) {
                        log.info("change node old node:" + oldNode.getPath());
                    }
                    if (newNode != null) {
                        log.info("change node new node:" + newNode.getPath());
                    }
                    // 订阅后需要把数据更新传递给监听器处理
                    List<InstanceMeta> instanceMetas = fectchZkInstanceMetas(service);
                    listenerEvent(event -> event.onSubscribe(service, instanceMetas));
                })
                .build();
        cache.listenable().addListener(cacheListener);
        cache.start();
        // 初次加载数据先从zk查一次以便fectchAll查询
        registryEvent.saveServiceInstanceMetas(service, fectchZkInstanceMetas(service));
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
