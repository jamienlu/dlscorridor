package cn.jamie.dlscorridor.core.registry;

import cn.jamie.dlscorridor.core.api.RegistryCenter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.List;

/**
 * @author jamieLu
 * @create 2024-03-17
 */
@Slf4j
public class ZkRegistryCenter implements RegistryCenter {
    private CuratorFramework client;
    @Override
    public void start() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,3);
        client = CuratorFrameworkFactory.builder()
                .connectString("192.168.0.101:2181")
                .namespace("discorridor")
                .retryPolicy(retryPolicy)
                .build();
        client.start();
        log.info("ZkRegistryCenter started");
    }

    @Override
    public void stop() {
        client.close();
        log.info("ZkRegistryCenter stopped");
    }

    @Override
    public void register(String service, String instance) {
        String serverPath = "/" + service;
        try {
            // 创建服务的持久化节点
            if(client.checkExists().forPath(serverPath) == null) {
                client.create().withMode(CreateMode.PERSISTENT).forPath(serverPath,"service".getBytes());
                log.info("create zk registry server path:" + serverPath);
            }
            // 创建实例的临时节点
            String instancePath = serverPath + "/" + instance;
            client.create().withMode(CreateMode.EPHEMERAL).forPath(instancePath,"provider".getBytes());
            log.info("create zk registry instance path:" + instancePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void unregister(String service, String instance) {
        String serverPath = "/" + service;
        try {
            // 删除服务的持久化节点
            if(client.checkExists().forPath(serverPath) == null) {
                return;
            }
            // 删除实例的临时节点
            String instancePath = serverPath + "/" + instance;
            log.info("remove zk registry instance path:" + instancePath);
            client.delete().quietly().forPath(instancePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> fectchAll(String service) {
        String serverPath = "/" + service;
        List<String> nodes = null;
        try {
            nodes = client.getChildren().forPath(serverPath);
            log.info("find zk nodes:" + nodes);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            throw new RuntimeException(e);
        }
        return nodes;
    }

    @SneakyThrows
    @Override
    public void subscribe(String service, RegistryCenterListener listener) {
        final CuratorCache cache = CuratorCache.build(client, "/"+ service);
        CuratorCacheListener cacheListener = CuratorCacheListener.builder()
                .forAll((type,oldNode,newNode) -> {
                    List<String> currentNodes = fectchAll(service);
                    if (oldNode != null) {
                        log.info("change node old node:" + oldNode.getPath());
                    }
                    if (newNode != null) {
                        log.info("change node new node:" + newNode.getPath());
                    }
                    listener.fire(RegistryCenterEvent.builder().nodes(currentNodes).build());
                } )
                .build();
        cache.listenable().addListener(cacheListener);
        cache.start();

    }
}
